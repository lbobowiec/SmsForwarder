package com.lubosoft.smsforwarder

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.lubosoft.smsforwarder.adapters.TimeOffRuleListAdapter
import com.lubosoft.smsforwarder.data.room.TimeOffRule
import com.lubosoft.smsforwarder.databinding.FragmentTimeoffRuleListBinding
import com.lubosoft.smsforwarder.utilities.InjectorUtils
import com.lubosoft.smsforwarder.viewmodels.TimeOffRuleViewModel

class TimeOffRuleListFragment : Fragment(), TimeOffRuleListAdapter.TimeOffRuleViewHolder.OnDeleteButtonClickListener {

    private lateinit var binding: FragmentTimeoffRuleListBinding

    private lateinit var viewModel: TimeOffRuleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeoff_rule_list, container, false)
        return binding.root
    }

    override fun onDeleteClick(item: TimeOffRule) {
        val adapter = binding.timeoffRuleRecyclerView.adapter as TimeOffRuleListAdapter
        val itemPosition = adapter.indexOfItem(item)
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle(getString(R.string.delete_from_list)) // TODO ??
            setMessage(getString(R.string.delete_timeoff_rule_confirmation_message))
            setPositiveButton(R.string.delete_button) { _, _ ->
                viewModel.delete(item)
                adapter.notifyItemRemoved(itemPosition)
            }
            setNegativeButton(R.string.cancel_button) { _, _ ->
                adapter.notifyItemChanged(itemPosition)
            }
            setOnCancelListener {
                adapter.notifyItemChanged(itemPosition)
            }
        }
        builder.create().show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = InjectorUtils.provideTimeOffRuleViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, factory).get(TimeOffRuleViewModel::class.java)
        val adapter = TimeOffRuleListAdapter(this)
        binding.timeoffRuleRecyclerView.apply {
            this.adapter = adapter
            setHasFixedSize(true)
        }
        subscribeUi(adapter)
        setRecylerViewItemTouchListener()
        initFloatingActionButton()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_timeoff_rule_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_timeoff_rules -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.apply {
                    setTitle(getString(R.string.clear_timeoff_rules_title))
                    setMessage(getString(R.string.clear_timeoff_rules_confirmation))
                    setPositiveButton(R.string.clear_button) { _, _ ->
                        viewModel.deleteAll()
                        val adapter = binding.timeoffRuleRecyclerView.adapter
                        adapter?.notifyItemRangeRemoved(0, adapter.itemCount)
                    }
                    setNegativeButton(R.string.cancel_button) { _, _ -> }
                }
                builder.create().show()
                true
            }
            else ->  {
                // FIXME Workaround as sometimes time-off rules list backs to time-off rule
                findNavController().navigate(R.id.target_phone_number_list_fragment)
                true
            }
        }
    }

    private fun initFloatingActionButton() {
        binding.fab.setOnClickListener {
            val directions = TimeOffRuleListFragmentDirections.navigateToTimeoffRule(-1L)
            findNavController().navigate(directions)
        }
    }

    private fun setRecylerViewItemTouchListener() {
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.timeoffRuleRecyclerView.adapter as TimeOffRuleListAdapter
                val item = adapter.getItemAtPosition(viewHolder.adapterPosition)
                onDeleteClick(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.timeoffRuleRecyclerView)
    }

    private fun subscribeUi(adapter: TimeOffRuleListAdapter) {
        viewModel.getAll().observe(viewLifecycleOwner, Observer { timeOffRules ->
            if (timeOffRules != null) {
                adapter.setTimeOffRules(timeOffRules)
            }
        })
    }

}