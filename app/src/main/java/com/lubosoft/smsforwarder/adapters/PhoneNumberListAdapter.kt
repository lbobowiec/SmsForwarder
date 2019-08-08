package com.lubosoft.smsforwarder.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.lubosoft.smsforwarder.BlackListPhoneNumberListFragmentDirections
import com.lubosoft.smsforwarder.R
import com.lubosoft.smsforwarder.TargetPhoneNumberListFragmentDirections
import com.lubosoft.smsforwarder.data.room.PhoneNumberData
import com.lubosoft.smsforwarder.databinding.ListItemPhoneNumberBinding
import com.lubosoft.smsforwarder.utilities.CharacterUtils

class PhoneNumberListAdapter(private val onDeleteListener: PhoneNumberViewHolder.OnDeleteButtonClickListener,
                             val isBlackList: Boolean = false): RecyclerView.Adapter<com.lubosoft.smsforwarder.adapters.PhoneNumberListAdapter.PhoneNumberViewHolder>() {

    private lateinit var binding: ListItemPhoneNumberBinding

    private var phoneNumbers = emptyList<PhoneNumberData>() // cached copy of phone numbers

    private val generator = ColorGenerator.MATERIAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneNumberViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_phone_number, parent, false)
        return PhoneNumberViewHolder(binding)
    }

    private fun createOnClickListener(phoneNumberDataId: Long): View.OnClickListener {
        return View.OnClickListener {
            val directions = if (isBlackList) BlackListPhoneNumberListFragmentDirections.navigateToBlacklistPhoneNumber(phoneNumberDataId)
                else TargetPhoneNumberListFragmentDirections.navigateToTargetPhoneNumber(phoneNumberDataId)
            it.findNavController().navigate(directions)
        }
    }

    override fun getItemCount() = phoneNumbers.size

    override fun onBindViewHolder(holder: PhoneNumberViewHolder, position: Int) {
        val phoneNumberData = phoneNumbers[position]
        val letterOrDigit = CharacterUtils.firstNonBlankLetter(phoneNumberData)
        val textDrawable = TextDrawable.builder().buildRound(letterOrDigit, generator.getColor(phoneNumberData.phoneNumber))
        holder.apply {
            bind(createOnClickListener(phoneNumberData.id), phoneNumberData, textDrawable, onDeleteListener)
            itemView.tag = phoneNumberData
        }
    }

    fun getItemAtPosition(position: Int) = phoneNumbers[position]

    fun indexOfItem(element: PhoneNumberData) = phoneNumbers.indexOf(element)

    internal fun setPhoneNumbers(phoneNumbers: List<PhoneNumberData>) {
        this.phoneNumbers = phoneNumbers
        notifyDataSetChanged()
    }

    class PhoneNumberViewHolder(private val binding: ListItemPhoneNumberBinding): RecyclerView.ViewHolder(binding.root) {

        interface OnDeleteButtonClickListener {
            fun onDeleteClick(item: PhoneNumberData)
        }

        fun bind(listener: View.OnClickListener, item: PhoneNumberData, image: Drawable, deleteListener: OnDeleteButtonClickListener) {
            binding.apply {
                clickListener = listener
                phoneNumberData = item
                contactImage.setImageDrawable(image)
                onDeleteListener = deleteListener
                executePendingBindings()
            }
        }

    }

}

