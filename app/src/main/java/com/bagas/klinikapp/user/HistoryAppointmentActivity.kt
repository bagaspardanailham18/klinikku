package com.bagas.klinikapp.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bagas.klinikapp.R
import com.bagas.klinikapp.databinding.ActivityHistoryAppointmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class HistoryAppointmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryAppointmentBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ref = FirebaseDatabase.getInstance().getReference("UserAppointment")
        auth = Firebase.auth

        ref.child(auth.uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.emptyMessage.visibility = View.GONE
                    binding.appointmentData.visibility = View.VISIBLE

                    binding.tvPoli.setText(getString(R.string.tv_poli, snapshot.child("poli").value.toString()))
                    binding.tvNoAntrian.text = snapshot.child("noantrian").value.toString()
                    binding.tvDate.text = snapshot.child("date").value.toString()
                    binding.tvTime.text = snapshot.child("time").value.toString()
                } else {
                    binding.emptyMessage.visibility = View.VISIBLE
                    binding.appointmentData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.btnEditAppointment.setOnClickListener {
            startActivity(Intent(this, EditAppointmentActivity::class.java))
        }

        binding.btnFinishAppointment.setOnClickListener { performFinishAppointment() }
    }

    private fun performFinishAppointment() {
        auth.currentUser?.uid?.let {
            ref.child(it).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Appointment anda selesai!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
        }
    }
}