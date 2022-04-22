package com.wildraion.taskmanager.fragments.list

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.transition.TransitionManager
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.wildraion.taskmanager.R
import com.wildraion.taskmanager.data.UserData
import com.wildraion.taskmanager.databinding.FragmentListBinding
import com.wildraion.taskmanager.fragments.taskspage.TasksPageAdapter
import com.wildraion.taskmanager.viewmodel.TaskViewModel

class ListFragment : Fragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private lateinit var mTaskViewModel: TaskViewModel
    private val adapter = ListAdapter()

    private lateinit var binding: FragmentListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)

        val recyclerView = binding.listRecycler
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        val adapter = TasksPageAdapter(this)
        val viewPager = binding.listViewpager
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabSelector(position)
            }
        })

        binding.listUnfulfilledLabel.setOnClickListener { viewPager.currentItem = 0 }
        binding.listTaskdoneLabel.setOnClickListener { viewPager.currentItem = 1 }

        binding.listAddTaskBtn.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        setTab(UserData.mainTabIndex)
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun tabSelector(tabIndex: Int) {
        if(tabIndex != UserData.mainTabIndex){
            setTab(tabIndex)
        }
    }

    private fun setTab(tabIndex: Int) {

        val tabSelector = binding.listTabSelector
        val layoutParams = tabSelector.layoutParams as LinearLayout.LayoutParams
        TransitionManager.beginDelayedTransition(binding.listTabSelectorContainer)

        val taskDoneAnim: ObjectAnimator
        val unfulfilledAnim: ObjectAnimator
        when(tabIndex) {
            0 -> {
                taskDoneAnim = ObjectAnimator.ofInt(binding.listTaskdoneLabel,
                    "textAppearance", R.style.TabHighlighted, R.style.TabHidden)
                unfulfilledAnim = ObjectAnimator.ofInt(binding.listUnfulfilledLabel,
                    "textAppearance", R.style.TabHidden, R.style.TabHighlighted)
            }

            1 -> {
                taskDoneAnim = ObjectAnimator.ofInt(binding.listTaskdoneLabel,
                    "textAppearance", R.style.TabHidden, R.style.TabHighlighted)
                unfulfilledAnim = ObjectAnimator.ofInt(binding.listUnfulfilledLabel,
                    "textAppearance", R.style.TabHighlighted, R.style.TabHidden)
            }

            else -> {
                taskDoneAnim = ObjectAnimator.ofInt(binding.listTaskdoneLabel,
                    "textAppearance", R.style.TabHighlighted, R.style.TabHidden)
                unfulfilledAnim = ObjectAnimator.ofInt(binding.listUnfulfilledLabel,
                    "textAppearance", R.style.TabHidden, R.style.TabHighlighted)
            }
        }

        taskDoneAnim.setEvaluator(ArgbEvaluator())
        unfulfilledAnim.setEvaluator(ArgbEvaluator())
        taskDoneAnim.start()
        unfulfilledAnim.start()

        when(tabIndex) {
            0 -> layoutParams.weight = 0.0f
            1 -> layoutParams.weight = 1.0f
        }
        tabSelector.layoutParams = layoutParams
        UserData.mainTabIndex = tabIndex
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val search = menu.findItem(R.id.search_menu)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        searchView?.setOnCloseListener(this)
        searchView?.setOnSearchClickListener{
            binding.listSearchContainer.visibility = View.VISIBLE
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchData(query)
        }
        return true
    }

    override fun onClose(): Boolean {
        binding.listSearchContainer.visibility = View.GONE
        return false
    }

    private fun searchData(query: String) {
        val searchQuery = "%$query%"

        mTaskViewModel.searchData(searchQuery).observe(viewLifecycleOwner) { tasks ->
            adapter.setData(tasks)
        }
    }

}