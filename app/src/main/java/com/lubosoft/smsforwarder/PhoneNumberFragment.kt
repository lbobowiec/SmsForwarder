package com.lubosoft.smsforwarder

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.lubosoft.smsforwarder.contacts.ContactsResolver
import com.lubosoft.smsforwarder.data.room.PhoneNumberData
import com.lubosoft.smsforwarder.databinding.PhoneNumberBinding
import com.lubosoft.smsforwarder.extensions.longToast
import com.lubosoft.smsforwarder.permission.CheckPermissionResult
import com.lubosoft.smsforwarder.permission.PermissionHandler
import com.lubosoft.smsforwarder.sms.SmsHelper
import com.lubosoft.smsforwarder.utilities.InjectorUtils
import com.lubosoft.smsforwarder.utilities.KeyboardUtils
import com.lubosoft.smsforwarder.viewmodels.PhoneNumberViewModel

open class PhoneNumberFragment : Fragment() {

    companion object {
        private const val TAG = "PhoneNumberFragment"
        const val PHONE_NUMBER_ID = "phone_number_id"
        const val REQUEST_CONTACT = 201
        const val PERMISSION_REQUEST_READ_CONTACTS = 303
    }

    protected lateinit var binding: PhoneNumberBinding
    private lateinit var viewModel: PhoneNumberViewModel
    private var contactName: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.phone_number, container, false)
        binding.pickContact.isEnabled = hasPermissionToPickContact()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = InjectorUtils.providePhoneNumberViewModelFactory(requireActivity())
        viewModel = ViewModelProviders.of(this, factory).get(PhoneNumberViewModel::class.java)
        val phoneNumberId: Long = arguments?.get(PHONE_NUMBER_ID) as Long
        if (phoneNumberId == -1L) {
            initSaveButton(null)
        } else {
            viewModel.getPhoneNumber(phoneNumberId).observe(this, Observer<PhoneNumberData> {
                contactName = it.contactName
                initPhoneEditText(it)
                initSaveButton(it)
            })
        }
        initPickContactButton()
        initCancelButton()
        checkPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_CONTACT && resultCode == RESULT_OK) {
            intent?.data?.let { contactUri ->
                val phoneNumberData = ContactsResolver(requireContext().contentResolver).retrieveContact(contactUri)
                binding.editPhoneNumber.setText(phoneNumberData.phoneNumber)
                contactName = phoneNumberData.contactName
            }
        }
    }

    private fun initPhoneEditText(item: PhoneNumberData) {
        binding.editPhoneNumber.apply {
            setText(item.phoneNumber)
            setSelection(item.phoneNumber.length)
            requestFocus()
        }
    }

    private fun initSaveButton(item: PhoneNumberData?) {
        binding.apply {
            saveNewPhoneNumberButton.setOnClickListener {
                val input = editPhoneNumber.text.trim()
                if (TextUtils.isEmpty(input)) {
                    editPhoneNumber.error = getString(R.string.empty_phone_number_error_message)
                    editPhoneNumber.requestFocus()
                } else {
                    val smsHelper = SmsHelper()
                    if (!smsHelper.isValidPhoneNumber(input.toString())) {
                        editPhoneNumber.error = getString(R.string.invalid_phone_number)
                        editPhoneNumber.requestFocus()
                        return@setOnClickListener
                    }

                    val formattedPhoneNumber = smsHelper.formatPhoneNumber(input.toString())
                    editPhoneNumber.setText(formattedPhoneNumber)
                    if (item != null) {
                        item.phoneNumber = formattedPhoneNumber
                        item.contactName = contactName
                        viewModel.update(item)
                    } else {
                        viewModel.insert(
                            PhoneNumberData(
                                phoneNumber = formattedPhoneNumber,
                                contactName = contactName,
                                isBlackList = isBlackList
                            )
                        )
                    }
                    backToPhoneNumberListFragment()
                }
            }
        }
    }

    private fun initCancelButton() {
        binding.cancelNewPhoneNumberButton.setOnClickListener {
            backToPhoneNumberListFragment()
        }
    }

    private fun initPickContactButton() {
        binding.pickContact.apply {
            setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(intent, REQUEST_CONTACT)
            }
        }
    }

    protected open fun backToPhoneNumberListFragment() {
        KeyboardUtils.hideKeyboardFrom(requireContext(), binding.editPhoneNumber)
    }

    private fun hasPermissionToPickContact() = !PermissionHandler.shouldAskPermission(requireContext(), Manifest.permission.READ_CONTACTS)

    private fun checkPermissions() {
        PermissionHandler.checkPermission(this, Manifest.permission.READ_CONTACTS) {
            when (it) {
                CheckPermissionResult.PermissionGranted -> {
                    // FIXME
                    // context?.toast(getString(R.string.read_contacts_permission_granted))
                }
                CheckPermissionResult.PermissionDisabled -> {
                    context?.longToast(getString(R.string.read_contacts_permission_disabled))
                }
                CheckPermissionResult.PermissionAsk -> {
                    PermissionHandler.requestPermissions(
                        this@PhoneNumberFragment,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        PERMISSION_REQUEST_READ_CONTACTS
                    )
                }
                CheckPermissionResult.PermissionPreviouslyDenied -> {
                    PermissionHandler.showAlertInFragment(
                        this@PhoneNumberFragment,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        PERMISSION_REQUEST_READ_CONTACTS,
                        getString(R.string.permission_required_dialog_title),
                        getString(R.string.contact_access_required)
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_CONTACTS -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission to read Contacts has been denied by user")
                    binding.pickContact.isEnabled = false
                } else {
                    Log.d(TAG, "Permission to read Contacts has been granted by user")
                    binding.pickContact.isEnabled = true
                }
            }
        }
    }

}
