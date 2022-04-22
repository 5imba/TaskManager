package com.wildraion.taskmanager.fragments.update

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.wildraion.taskmanager.R
import com.wildraion.taskmanager.databinding.FragmentUpdateBinding
import com.wildraion.taskmanager.model.Task
import com.wildraion.taskmanager.notification.Notification
import com.wildraion.taskmanager.util.Utils
import com.wildraion.taskmanager.viewmodel.TaskViewModel
import java.util.*

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var mTaskViewModel: TaskViewModel
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    private var colorTag: Int = 0

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)

        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        colorTag = args.currentTask.colorTag

        binding.updateTitleEt.setText(args.currentTask.title)
        binding.updateTagsEt.setText(args.currentTask.tags)
        binding.updateColortagIv.setColorFilter(args.currentTask.colorTag)

        val time = args.currentTask.time.split(':')
        binding.updateDeadlineEd.setText("${Utils.datePatternToPretty(args.currentTask.date)}, " +
                Utils.timePeriodConverter(Integer.parseInt(time[0]), Integer.parseInt(time[1]), time[2].toBoolean()))

        // Is done switch value
        binding.updateSetUndoneSwitch.isChecked = args.currentTask.isDone
        setSwitchColorStates(args.currentTask.colorTag)

        // Set DatePicker value
        val date = args.currentTask.date.split('-')
        binding.updateDatePicker.updateDate(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]))

        // Set TimePicker value
        binding.updateTimePicker.hour = Integer.parseInt(time[0])
        binding.updateTimePicker.minute = Integer.parseInt(time[1])

        binding.updateSaveBtn.setOnClickListener{
            updateItem()
        }

        // Color tag popup menu
        binding.updateColortagIv.setOnClickListener{

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

            popup.showAsDropDown(binding.updateColortagIv)
        }

        // Date & Time pickers
        binding.updateDeadlineEd.setOnFocusChangeListener { view, hasFocus ->
            hideSoftKeyboard(view)
            binding.updatePickersLayout.visibility = if(hasFocus) View.VISIBLE else View.GONE
            setDeadlineText()
        }

        binding.updateDatePicker.setOnDateChangedListener { _, _, _, _ ->
            setDeadlineText()
        }

        binding.updateTimePicker.setOnTimeChangedListener { _, _, _ ->
            setDeadlineText()
        }

        // Add option remove button
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun hideSoftKeyboard(view: View){
        val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setColorTag(iv: ImageView, popup: PopupWindow) {
        colorTag = iv.imageTintList?.defaultColor!!.toInt()
        binding.updateColortagIv.setColorFilter(colorTag)
        setSwitchColorStates(colorTag)
        popup.dismiss()
    }

    private fun setSwitchColorStates(checkedColor: Int){
        val states  = arrayOf(intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked))

        val colors = intArrayOf(checkedColor,
                ContextCompat.getColor(requireContext(), R.color.gray))

        binding.updateSetUndoneSwitch.thumbTintList = ColorStateList(states, colors)
        binding.updateSetUndoneSwitch.trackTintList = ColorStateList(states, colors)
    }

    @SuppressLint("SetTextI18n")
    private fun setDeadlineText(){

        val date = Utils.datePatternToPretty("${binding.updateDatePicker.dayOfMonth}-" +
                "${binding.updateDatePicker.month + 1}-" +
                "${binding.updateDatePicker.year}")
        val time = Utils.timePeriodConverter(binding.updateTimePicker.hour,
                binding.updateTimePicker.minute,
                binding.updateTimePicker.is24HourView)

        binding.updateDeadlineEd.setText("$date, $time")
    }

    private fun updateItem() {
        val year = binding.updateDatePicker.year
        val month = binding.updateDatePicker.month
        val day = binding.updateDatePicker.dayOfMonth
        val hour = binding.updateTimePicker.hour
        val minute = binding.updateTimePicker.minute
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute, 0)

        val title = binding.updateTitleEt.text.toString()
        val tags = binding.updateTagsEt.text.toString()
        val date = "$day-$month-$year"
        val time = "$hour:$minute:${binding.updateTimePicker.is24HourView}"
        val isDone = binding.updateSetUndoneSwitch.isChecked

        if(Utils.inputCheck(title, binding.updateDeadlineEd.text.toString())){
            val updatedTask =  Task(args.currentTask.id, date, time, title, colorTag, tags, isDone)

            mTaskViewModel.updateTask(updatedTask)

            val notification = Notification(
                args.currentTask.id,
                "Task deadline expires",
                title,
                calendar.timeInMillis)

            if(!isDone){
                notification.scheduleNotification(requireContext())
            }
            else{
                notification.removeNotification(requireContext())
            }

            Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteTask() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mTaskViewModel.deleteTask(args.currentTask)
            Toast.makeText(
                    requireContext(),
                    "Task successfully removed",
                    Toast.LENGTH_LONG).show()

            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") { _, _ ->

        }
        builder.setTitle("Delete \"${args.currentTask.title}\"?")
        builder.setMessage("Are you sure you want to delete \"${args.currentTask.title}\"?")
        builder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.delete_menu){
            deleteTask()
        }
        return super.onOptionsItemSelected(item)
    }
}