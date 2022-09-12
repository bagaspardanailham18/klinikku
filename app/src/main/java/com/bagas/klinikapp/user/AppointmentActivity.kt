package com.bagas.klinikapp.user

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bagas.klinikapp.R
import com.bagas.klinikapp.databinding.ActivityAppointmentBinding
import com.bagas.klinikapp.model.UserAppointment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AppointmentActivity : AppCompatActivity() {

    @SuppressLint("NewApi", "WeekBasedYear")
    private var formatDate = SimpleDateFormat("dd-MMM-YYYY", Locale.US)

    private lateinit var binding: ActivityAppointmentBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var databaseUser: DatabaseReference

    private var noAntrian: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.edtDate.setOnClickListener { setDatePicker() }

//        binding.edtTime.setOnKeyListener(null)

        val policlinic = resources.getStringArray(R.array.poli)
        val arrayPoliAdapter = ArrayAdapter(this, R.layout.dropdown_gender_item, policlinic)
        binding.edtPoli.setAdapter(arrayPoliAdapter)


        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datetime = current.format(formatter)
        val waktu = resources.getStringArray(R.array.time)
        val arrayTimeAdapter = ArrayAdapter(this, R.layout.dropdown_gender_item, waktu)
        binding.edtTime.setAdapter(arrayTimeAdapter)

        binding.edtDate.setText(datetime)

        auth = Firebase.auth
//        val currentUser = auth.currentUser
//        val userId = currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference("UserAppointment")


        Log.d("user", "User ID : ${auth.uid}")

        binding.btnMakeAppointment.setOnClickListener {

            val uid = auth.currentUser?.uid.toString().trim()
            val date = binding.edtDate.text.toString()
            val poli = binding.edtPoli.text.toString().trim()
            val time = binding.edtTime.text.toString().trim()
            val description = binding.edtDescription.text.toString().trim()

            if (date.isEmpty()) {
                binding.layoutEdtDate.error = "Tanggal harus diisi!"
                binding.layoutEdtDate.requestFocus()
                return@setOnClickListener
            } else if (poli.isEmpty() || poli == "") {
                binding.layoutEdtPoli.error = "Poli harus diisi!"
                binding.layoutEdtPoli.requestFocus()
                return@setOnClickListener
            } else if (time.isEmpty() || time == "") {
                binding.layoutEdtTime.error = "Waktu harus diisi!"
                binding.layoutEdtTime.requestFocus()
                return@setOnClickListener
            } else if (description.isEmpty() || description == "") {
                binding.layoutEdtDescription.error = "Keluhan harus diisi!"
                binding.layoutEdtDescription.requestFocus()
            } else {
                databaseUser = FirebaseDatabase.getInstance().getReference("PersonalUsers")
                databaseUser.child(auth.uid!!).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = snapshot.child("name").value.toString()
                        val email = snapshot.child("email").value.toString()
                        Log.d("Data", "Name : $name, Email : $email")
//                        makeAppointment(uid, name, email, date, poli, doctor, time, description)
                        setNoAntrian(uid, name, email, date, poli, time, description)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }

    private fun setNoAntrian(uid:String, name: String, email: String, date: String, poli: String, time: String, description: String) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (time == "Pagi (06.00–08.00)") {
                    if (!snapshot.exists()) {
                        noAntrian += 1
                        Log.d("antrian", "No Antrian : $noAntrian")
                        makeAppointment(uid, name, email, date, poli, time, description, noAntrian)
                    } else {
                        var jumlahPagi = 0
                        val arrNoAntrian = mutableListOf<Long>()

                        for (n in snapshot.children) {
                            if (n.child("time").value == "Pagi (06.00–08.00)") {
                                arrNoAntrian.add(n.child("noantrian").value as Long)
                            } else {
                                continue
                            }
                        }

                        if (arrNoAntrian.size == 0) {
                            noAntrian = 1
                        } else {
                            var maxNoAntrian = arrNoAntrian[0]

                            for (no in arrNoAntrian) {
                                if (maxNoAntrian < no) {
                                    maxNoAntrian = no
                                }
                            }

//                        val jumlah = jumlahPagi + 1
                            noAntrian += maxNoAntrian.toInt() + 1
                        }

//                        Log.d("antrian", "Max Value : $maxNoAntrian")
//                        Log.d("antrian", "Antrian : $noAntrian")
                        makeAppointment(uid, name, email, date, poli, time, description, noAntrian)
                    }
                } else {
                    if (!snapshot.exists()) {
                        noAntrian += 1
                        makeAppointment(uid, name, email, date, poli, time, description, noAntrian)
                    } else {
                        val arrNoAntrian = mutableListOf<Long>()
                        for (n in snapshot.children) {
                            if (n.child("time").equals("Sore (16.00–18.00)")) {
                                arrNoAntrian.add(n.child("noantrian").value as Long)
                            } else {
                                continue
                            }
                        }

                        if (arrNoAntrian.size == 0) {
                            noAntrian = 1
                        } else {
                            var maxNoAntrian = arrNoAntrian[0]

                            for (no in arrNoAntrian) {
                                if (maxNoAntrian < no) {
                                    maxNoAntrian = no
                                }
                            }
                            noAntrian += maxNoAntrian.toInt() + 1
                        }

                        makeAppointment(uid, name, email, date, poli, time, description, noAntrian)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun makeAppointment(uid: String, name: String, email: String, date: String, poli: String, time: String, description: String, noAntrian: Int) {
        val appointment = UserAppointment(uid, name, email, date, poli, time, description, noAntrian)
        database.child(uid).setValue(appointment)
            .addOnCompleteListener {
                Toast.makeText(this, "Appointment dibuat", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
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
            binding.edtDate.setText(date)
        }, getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

//    @SuppressLint("SetTextI18n")
//    private fun setTimePicker() {
//        val isSystem24Hour = is24HourFormat(this)
//        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
//
//        val timePicker = MaterialTimePicker.Builder()
//            .setTimeFormat(clockFormat)
//            .setHour(12)
//            .setTitleText("Set Time")
//            .build()
//
//        timePicker.show(supportFragmentManager, "TAG")
//
//        timePicker.addOnPositiveButtonClickListener {
//            val h = timePicker.hour.toString()
//            val min = timePicker.minute.toString()
//            binding.edtTime.setText("$h:$min")
//        }
//
//    }
}