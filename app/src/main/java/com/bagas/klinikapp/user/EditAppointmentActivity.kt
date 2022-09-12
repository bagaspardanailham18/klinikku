package com.bagas.klinikapp.user

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bagas.klinikapp.R
import com.bagas.klinikapp.databinding.ActivityEditAppointmentBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentActivity : AppCompatActivity() {

    @SuppressLint("NewApi", "WeekBasedYear")
    private var formatDate = SimpleDateFormat("dd-MMM-YYYY", Locale.US)

    private lateinit var binding: ActivityEditAppointmentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val policlinic = resources.getStringArray(R.array.poli)
        val arrayPoliAdapter = ArrayAdapter(this, R.layout.dropdown_gender_item, policlinic)
        binding.edtPoli.setAdapter(arrayPoliAdapter)

        auth = Firebase.auth
        ref = FirebaseDatabase.getInstance().getReference("UserAppointment")

        ref.child(auth.uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.edtPoli.setText(snapshot.child("poli").value.toString())
                    binding.edtTime.setText(snapshot.child("time").value.toString())
                    binding.edtDescription.setText(snapshot.child("description").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.btnEditAppointment.setOnClickListener { performEdit() }
    }

    @SuppressLint("NewApi")
    private fun setDatePicker() {
        val getDate = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val selectDate = Calendar.getInstance()
            selectDate.set(Calendar.YEAR, year)
            selectDate.set(Calendar.MONTH, month)
            selectDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val date = formatDate.format(selectDate.time)
//            binding.edtDate.setText(date)
        }, getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setTimePicker() {
        val isSystem24Hour = DateFormat.is24HourFormat(this)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setTitleText("Set Time")
            .build()

        timePicker.show(supportFragmentManager, "TAG")

        timePicker.addOnPositiveButtonClickListener {
            val h = timePicker.hour.toString()
            val min = timePicker.minute.toString()
            binding.edtTime.setText("$h:$min")
        }

    }

    private fun performEdit() {
        val uid = auth.currentUser?.uid
        val poli = binding.edtPoli.text.toString().trim()
        val time = binding.edtTime.text.toString().trim()
        val description = binding.edtDescription.text.toString().trim()

        uid?.let {
            ref.child(it).child("poli").setValue(poli)
            ref.child(it).child("time").setValue(time)
            ref.child(it).child("description").setValue(description)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil diubah!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
        }
    }
}