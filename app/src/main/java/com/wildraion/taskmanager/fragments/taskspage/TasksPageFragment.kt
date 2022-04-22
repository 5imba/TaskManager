package com.wildraion.taskmanager.fragments.taskspage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Observer
import com.wildraion.taskmanager.databinding.FragmentTasksPageBinding
import com.wildraion.taskmanager.fragments.list.ListAdapter
import com.wildraion.taskmanager.viewmodel.TaskViewModel

const val ARG_OBJECT = "tagList"

class TasksPageFragment : Fragment() {

    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var binding: FragmentTasksPageBinding
    private val adapter = ListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTasksPageBinding.inflate(layoutInflater)

        val recyclerView = binding.taskPageRecycler
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {

            mTaskViewModel.readAllData.observe(viewLifecycleOwner, Observer { task->
                adapter.setData(task.filter { query ->
                    query.isDone == (getInt(ARG_OBJECT) == 1)
                })
            })

        }
    }
}