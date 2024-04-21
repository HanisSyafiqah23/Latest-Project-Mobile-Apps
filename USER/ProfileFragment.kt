package com.example.waveoffoodorder.Fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.waveoffoodorder.LoginActivity
import com.example.waveoffoodorder.Model.UserModel
import com.example.waveoffoodorder.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setUserData()

        binding.apply {
            name.isEnabled = false
            email.isEnabled = false
            address.isEnabled = false
            phone.isEnabled = false

            editButton.setOnClickListener {
                toggleEditMode()
            }

            saveInfoButton.setOnClickListener {
                val name = name.text.toString()
                val email = email.text.toString()
                val address = address.text.toString()
                val phone = phone.text.toString()
                updateUserData(name, email, address, phone)
            }

            logoutButton.setOnClickListener {
                logout()
            }

            userManualButton.setOnClickListener {
                showUserManualDialog()
            }
        }

        return binding.root
    }

    private fun toggleEditMode() {
        binding.apply {
            name.isEnabled = !name.isEnabled
            email.isEnabled = !email.isEnabled
            address.isEnabled = !address.isEnabled
            phone.isEnabled = !phone.isEnabled
        }
    }

    private fun updateUserData(name: String, email: String, address: String, phone: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userReference = database.getReference("user").child(userId)

            val userData = hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone
            )
            userReference.setValue(userData).addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Profile Update successfully 😊",
                    Toast.LENGTH_SHORT
                ).show()
            }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Profile Update Failed 😒", Toast.LENGTH_SHORT)
                        .show()
                }

        }

    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null && view != null) { // Check if view is available
            val userReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(UserModel::class.java)
                        if (userProfile != null) {
                            binding.name.setText(userProfile.name)
                            binding.address.setText(userProfile.address)
                            binding.email.setText(userProfile.email)
                            binding.phone.setText(userProfile.phone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled event
                }
            })
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun openUserManual() {
        val userManualUrl = "Your user manual text goes here."
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(userManualUrl))
        startActivity(intent)
    }

    private fun showUserManualDialog() {
        val userManualText1 = "1. Register your account."
        val userManualText2 = "2. Login your account."
        val userManualText3 = "3. Choose and Add to cart you food."
        val userManualText4 = "4. Press the proceed button."
        val userManualText5 = "5. Check out your payment."
        val userManualText6 = "6. Wait for your order."
        val userManualMessage = "$userManualText1\n\n$userManualText2\n\n$userManualText3\n\n$userManualText4\n\n$userManualText5\n\n$userManualText6"

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("User Manual")
            .setMessage(userManualMessage)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}