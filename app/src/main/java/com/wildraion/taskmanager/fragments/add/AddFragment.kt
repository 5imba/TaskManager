package com.wildraion.taskmanager.fragments.add

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.wildraion.taskmanager.R
import com.wildraion.taskmanager.databinding.FragmentAddBinding
import com.wildraion.taskmanager.model.Task
import com.wildraion.taskmanager.util.Utils
import com.wildraion.taskmanager.viewmodel.TaskViewModel
import kotlinx.coroutines.*
import java.util.*


class AddFragment : Fragment() {

    private lateinit var mUserViewModel: TaskViewModel
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private var colorTag: Int = 0

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        mUserViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        binding.addSaveBtn.setOnClickListener {
            insertDataToDatabase()
        }

        colorTag = binding.addColortagIv.imageTintList?.defaultColor!!.toInt()

        // Color tag popup menu
        binding.addColortagIv.setOnClickListener{

            val popupView = layoutInflater.inflate(R.layout.colortag_menu, null)
            val popup = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            popup.isOutsideTouchable = true
            popup.isFocusable = true
            popup.elevation = 10.0f

            val iv1 = popup.contentView.findViewById<ImageView>(R.id.color_tag_1)
            iv1.setOnClickListener{ setColorTag(iv1, popup) }

            val iv2 = popup.contentView.findViewById<ImageView>(R.id.color_tag_2)
            iv2.setOnClickListener{ setColorTag(iv2, popup) }

            val iv3 = popup.contentView.findViewById<ImageView>(R.id.color_tag_3)
            iv3.setOnClickListener{ setColorTag(iv3, popup) }

            val iv4 = popup.contentView.findViewById<ImageView>(R.id.color_tag_4)
            iv4.setOnClickListener{ setColorTag(iv4, popup) }

            val iv5 = popup.contentView.findViewById<ImageView>(R.id.color_tag_5)
            iv5.setOnClickListener{ setColorTag(iv5, popup) }

            val iv6 = popup.contentView.findViewById<ImageView>(R.id.color_tag_6)
            iv6.setOnClickListener{ setColorTag(iv6, popup) }

            val iv7 = popup.contentView.findViewById<ImageView>(R.id.color_tag_7)
            iv7.setOnClickListener{ setColorTag(iv7, popup) }

            val iv8 = popup.contentView.findViewById<ImageView>(R.id.color_tag_8)
            iv8.setOnClickListener{ setColorTag(iv8, popup) }

            popup.showAsDropDown(binding.addColortagIv)
        }

        // Date & Time pickers
        binding.addDeadlineEt.setOnFocusChangeListener { view, hasFocus ->
            hideSoftKeyboard(view)
            binding.addPickersLayout.visibility = if(hasFocus) View.VISIBLE else View.GONE
            setDeadlineText()
        }

        binding.addDatePicker.setOnDateChangedListener { _, _, _, _ ->
            setDeadlineText()
        }
        binding.addTimePicker.setOnTimeChangedListener { _, _, _ ->
            setDeadlineText()
        }

        binding.addTagsEt.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                Toast.makeText(requireContext(), R.string.tags_separator, Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    private fun hideSoftKeyboard(view: View){
        val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("SetTextI18n")
    private fun setDeadlineText(){

        val date = Utils.datePatternToPretty("${binding.addDatePicker.dayOfMonth}-" +
                "${binding.addDatePicker.month + 1}-" +
                "${binding.addDatePicker.year}")
        val time = Utils.timePeriodConverter(binding.addTimePicker.hour,
                binding.addTimePicker.minute,
                binding.addTimePicker.is24HourView)

        binding.addDeadlineEt.setText("$date, $time")
    }

    private fun setColorTag(iv: ImageView, popup: PopupWindow) {
        colorTag = iv.imageTintList?.defaultColor!!.toInt()
        binding.addColortagIv.setColorFilter(colorTag)
        popup.dismiss()
    }

    private fun insertDataToDatabase() {
        val year = binding.addDatePicker.year
        val month = binding.addDatePicker.month
        val day = binding.addDatePicker.dayOfMonth
        val hour = binding.addTimePicker.hour
        val minute = binding.addTimePicker.minute
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute, 0)

        val title = binding.addTitleEt.text.toString()
        val tags = binding.addTagsEt.text.toString()
        val date = "$day-$month-$year"
        val time = "$hour:$minute:${binding.addTimePicker.is24HourView}"

        if(Utils.inputCheck(title, binding.addDeadlineEt.text.toString())){
            // Create and add Task object
            val task = Task(0, date, time, title, colorTag, tags, false)
            GlobalScope.launch { addTask(task, calendar) }
            // Popup
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
            // Navigate back
            findNavController().popBackStack()
        }else{
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun addTask(task: Task, date: Calendar) =
        withContext(Dispatchers.Default) {
            val id = mUserViewModel.addTask(task).toInt()

            Log.w("Notification sys", "Added task id: $id")

            com.wildraion.taskmanager.notification.Notification(
                id,
                "Task deadline expires",
                task.title,
                date.timeInMillis)
                .scheduleNotification(requireContext())
            null
        }
}