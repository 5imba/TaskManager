package com.wildraion.taskmanager.fragments.select

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.wildraion.taskmanager.R
import com.wildraion.taskmanager.databinding.FragmentSelectBinding
import com.wildraion.taskmanager.model.Task
import com.wildraion.taskmanager.notification.Notification
import com.wildraion.taskmanager.util.Utils
import com.wildraion.taskmanager.viewmodel.TaskViewModel

class SelectFragment : Fragment() {

    private val args by navArgs<SelectFragmentArgs>()

    private lateinit var mTaskViewModel: TaskViewModel
    private var _binding: FragmentSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentSelectBinding.inflate(inflater, container, false)
        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        binding.selectTitleTv.text = args.currentTask.title
        binding.selectColortagIv.setColorFilter(args.currentTask.colorTag)
        binding.selectTagsTv.text = args.currentTask.tags
        binding.selectDateEt.text = Utils.datePatternToPretty(args.currentTask.date)
        val time = args.currentTask.time.split(':')
        binding.selectTimeEt.text =
                Utils.timePeriodConverter(Integer.parseInt(time[0]), Integer.parseInt(time[1]), time[2].toBoolean())

        if(args.currentTask.isDone){
            setDoneBtn()
        }
        else{
            binding.selectSetDoneBtn.setOnClickListener{
                val notification = Notification(
                    args.currentTask.id,
                    "Task deadline expires",
                    args.currentTask.title,
                    0)
                notification.removeNotification(requireContext())
                setIsDone()
            }
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setIsDone() {
        val updatedTask =  Task(args.currentTask.id, args.currentTask.date,
                args.currentTask.time, args.currentTask.title, args.currentTask.colorTag, args.currentTask.tags, true)

        mTaskViewModel.updateTask(updatedTask)
        Toast.makeText(context, "Task is Done, congrats!", Toast.LENGTH_LONG).show()
        setDoneBtn()
    }

    private fun setDoneBtn(){
        val doneBtn = binding.selectSetDoneBtn
        doneBtn.isClickable = false
        doneBtn.setBackgroundColor(args.currentTask.colorTag)
        doneBtn.setText(R.string.task_done)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.update_menu){
            val action = SelectFragmentDirections.actionSelectFragmentToUpdateFragment(args.currentTask)
            view?.findNavController()?.navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }
}