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
import com.lubosoft.smsforwarder.adapters.PhoneNumberListAdapter
import com.lubosoft.smsforwarder.data.room.PhoneNumberData
import com.lubosoft.smsforwarder.databinding.PhoneNumberListBinding
import com.lubosoft.smsforwarder.utilities.InjectorUtils
import com.lubosoft.smsforwarder.viewmodels.PhoneNumberViewModel

open class PhoneNumberListFragment : Fragment(), PhoneNumberListAdapter.PhoneNumberViewHolder.OnDeleteButtonClickListener {

    protected lateinit var binding: PhoneNumberListBinding

    private lateinit var viewModel: PhoneNumberViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.phone_number_list, container, false)
        return binding.root
    }

    override fun onDeleteClick(item: PhoneNumberData) {
        val adapter = binding.phoneNumberRecyclerView.adapter as PhoneNumberListAdapter
        val itemPosition = adapter.indexOfItem(item)
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle(getString(R.string.delete_from_list))
            setMessage(getString(if (binding.isBlackList) R.string.delete_blacklist_number_confirmation_message else R.string.delete_phone_number_confirmation_message))
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
        val factory = InjectorUtils.providePhoneNumberViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, factory).get(PhoneNumberViewModel::class.java)
        val adapter = PhoneNumberListAdapter(this, binding.isBlackList)
        binding.phoneNumberRecyclerView.apply {
            this.adapter = adapter
            setHasFixedSize(true)
        }
        subscribeUi(adapter)
        setRecylerViewItemTouchListener()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_phone_number_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_data -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.apply {
                    setTitle(getString(if (binding.isBlackList) R.string.clear_blacklist_numbers_title else R.string.clear_target_phone_numbers_title))
                    setMessage(getString(if (binding.isBlackList) R.string.clear_blacklist_numbers_confirmation else R.string.clear_target_phone_numbers_confirmation))
                    setPositiveButton(R.string.clear_button) { _, _ ->
                        viewModel.deleteAll(binding.isBlackList)
                        val adapter = binding.phoneNumberRecyclerView.adapter
                        adapter?.notifyItemRangeRemoved(0, adapter.itemCount)
                    }
                    setNegativeButton(R.string.cancel_button) { _, _ -> }
                }
                builder.create().show()
                true
            }
            else ->  {
                // Workaround as sometimes blacklist number list back to blacklist number
                if (binding.isBlackList) {
                    findNavController().navigate(R.id.target_phone_number_list_fragment)
                    true
                } else super.onOptionsItemSelected(item)
            }
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
                val adapter = binding.phoneNumberRecyclerView.adapter as PhoneNumberListAdapter
                val item = adapter.getItemAtPosition(viewHolder.adapterPosition)
                onDeleteClick(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.phoneNumberRecyclerView)
    }

    private fun subscribeUi(adapter: PhoneNumberListAdapter) {
        viewModel.getAllPhoneNumbers(binding.isBlackList).observe(viewLifecycleOwner, Observer { phoneNumbers ->
            if (phoneNumbers != null) {
                adapter.setPhoneNumbers(phoneNumbers)
            }
        })
    }
}
