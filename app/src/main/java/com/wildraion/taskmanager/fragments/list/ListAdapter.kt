package com.wildraion.taskmanager.fragments.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wildraion.taskmanager.R
import com.wildraion.taskmanager.model.Task
import com.wildraion.taskmanager.notification.Notification
import com.wildraion.taskmanager.util.Utils
import com.wildraion.taskmanager.viewmodel.TaskViewModel
import java.util.*


class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>(){

    private lateinit var mTaskViewModel: TaskViewModel
    private var taskList = emptyList<Task>()
    private var context: Context? = null

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        mTaskViewModel = ViewModelProvider((context as FragmentActivity?)!!)[TaskViewModel::class.java]
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = taskList[position]

        // Set task color
        holder.itemView.findViewById<ConstraintLayout>(R.id.task_container).backgroundTintList =
                ColorStateList.valueOf(currentItem.colorTag)
        // Set task title
        holder.itemView.findViewById<TextView>(R.id.task_title_txt).text = currentItem.title
        // Set date adn time
        holder.itemView.findViewById<TextView>(R.id.date_text).text = Utils.datePatternToPretty(currentItem.date)
        val time = currentItem.time.split(':')
        holder.itemView.findViewById<TextView>(R.id.time_text).text =
                Utils.timePeriodConverter(Integer.parseInt(time[0]), Integer.parseInt(time[1]), time[2].toBoolean())
        // Set task state
        holder.itemView.findViewById<ImageView>(R.id.task_state).setImageResource(
                if(currentItem.isDone) R.drawable.ic_tick
                else R.drawable.ic_circle_empty)

        // Bind select action
        holder.itemView.findViewById<ConstraintLayout>(R.id.task_item).setOnClickListener{
            val action = ListFragmentDirections.actionListFragmentToSelectFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }

        holder.itemView.findViewById<ImageView>(R.id.task_state).setOnClickListener{
            changeIsDone(currentItem)
        }

        // Bind edit button
        holder.itemView.findViewById<Button>(R.id.task_edit_btn).setOnClickListener{
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }

        val tags = currentItem.tags.split(',')

        val adapter = TagsAdapter()
        val recyclerView = holder.itemView.findViewById<RecyclerView>(R.id.task_tags_recycler)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        adapter.setData(tags.filter { str -> str.trim() != "" })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(taskList: List<Task>){
        this.taskList = taskList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeIsDone(currentItem: Task){
        val updatedTask = Task(currentItem.id, currentItem.date, currentItem.time,
                currentItem.title, currentItem.colorTag, currentItem.tags, !currentItem.isDone)
        mTaskViewModel.updateTask(updatedTask)

        val date = updatedTask.date.split('-')
        val time = updatedTask.time.split(':')
        val calendar = Calendar.getInstance()
        calendar.set(Integer.parseInt(date[2]),
            Integer.parseInt(date[1]),
            Integer.parseInt(date[0]),
            Integer.parseInt(time[0]),
            Integer.parseInt(time[1]), 0)

        val notification = Notification(
            currentItem.id,
            "Task deadline expires",
            currentItem.title,
            calendar.timeInMillis)

        if(updatedTask.isDone) notification.removeNotification(context!!)
        else notification.scheduleNotification(context!!)


        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

}