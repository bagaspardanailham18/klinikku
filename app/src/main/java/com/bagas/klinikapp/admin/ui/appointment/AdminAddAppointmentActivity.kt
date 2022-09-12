package com.bagas.klinikapp.admin.ui.appointment

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bagas.klinikapp.R
import com.bagas.klinikapp.admin.AdminMainActivity
import com.bagas.klinikapp.auth.LoginActivity
import com.bagas.klinikapp.databinding.ActivityAdminAddAppointmentBinding
import com.bagas.klinikapp.model.PersonalUser
import com.bagas.klinikapp.model.UserAppointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdminAddAppointmentActivity : AppCompatActivity() {

    private lateinit var userRef: DatabaseReference
    private lateinit var appointmentRef: DatabaseReference

    private lateinit var binding:ActivityAdminAddAppointmentBinding
    private lateinit var auth: FirebaseAuth

    private var noAntrian: Int = 0

    companion object {
        const val STRING_LENGTH = 10;
        const val ALPHANUMERIC_REGEX = "[a-zA-Z0-9]+";
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Auth
        auth = FirebaseAuth.getInstance()

        userRef = FirebaseDatabase.getInstance().getReference("PersonalUsers")
        appointmentRef = FirebaseDatabase.getInstance().getReference("UserAppointment")

        val gender = resources.getStringArray(R.array.gender)
        val arrayGenderAdapter = ArrayAdapter(this, R.layout.dropdown_gender_item, gender)
        binding.edtRegisterGender.setAdapter(arrayGenderAdapter)

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

        // Validate Form when Clicked
        binding.btnAddAppointment.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val gender = binding.edtRegisterGender.text.toString()
            val age = binding.edtAge.text.toString()
            val address = binding.edtAddress.text.toString()
            val email = binding.edtEmail.text.toString().trim()
            val phone = binding.edtPhone.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            val date = binding.edtDate.text.toString()
            val poli = binding.edtPoli.text.toString().trim()
            val time = binding.edtTime.text.toString().trim()
            val description = binding.edtDescription.text.toString().trim()

            if (name.isEmpty() || name == "") {
                binding.layoutEdtName.error = "Nama harus diisi!!"
                binding.layoutEdtName.requestFocus()
                return@setOnClickListener

            } else if (gender.isEmpty()) {
                binding.edtLayoutGender.error = "Jenis kelamin harus diisi"
                binding.edtLayoutGender.requestFocus()
                return@setOnClickListener

            } else if (address.isEmpty() || address == "") {
                binding.layoutEdtAddress.error = "Alamat harus diisi!!"
                binding.layoutEdtAddress.requestFocus()
                return@setOnClickListener

            } else if (age.isEmpty() || age == "") {
                binding.layoutEdtAge.error = "Umur harus diisi!!"
                binding.layoutEdtAge.requestFocus()
                return@setOnClickListener

            } else if (email.isEmpty() || name == "") {
                binding.layoutEdtEmail.error = "Email harus diisi!!"
                binding.layoutEdtEmail.requestFocus()
                return@setOnClickListener

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.layoutEdtEmail.error = "Email tidak valid"
                binding.layoutEdtEmail.requestFocus()
                return@setOnClickListener

            } else if (phone.isEmpty() || phone == "") {
                binding.layoutEdtPhone.error = "No telepon harus diisi!!"
                binding.layoutEdtPhone.requestFocus()
                return@setOnClickListener

            } else if (password.isEmpty() || password == "") {
                binding.layoutEdtPassword.error = "Password harus diisi!!"
                binding.layoutEdtPassword.requestFocus()
                return@setOnClickListener

            } else if (poli.isEmpty() || poli == "") {
                binding.layoutEdtPoli.error = "Poli harus diisi!!"
                binding.layoutEdtPoli.requestFocus()
                return@setOnClickListener

            } else if (time.isEmpty() || time == "") {
                binding.layoutEdtTime.error = "Password harus diisi!!"
                binding.layoutEdtTime.requestFocus()
                return@setOnClickListener

            } else if (description.isEmpty() || description == "") {
                binding.layoutEdtDescription.error = "Keluhan harus diisi!!"
                binding.layoutEdtDescription.requestFocus()
                return@setOnClickListener

            } else {
//                val uid = auth.uid ?: ""
//                val length = 10
//                val randomUid = getRandomString(length)
//
//                println(randomUid)


                registerUser(name, gender, age, address, email, phone, password, date, poli, time, description)

            }
        }

    }


    fun getRandomString(length: Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    private fun registerUser(name: String, gender: String, age: String, address: String, email: String, phone: String, password: String, date: String, poli: String, time: String, description: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    Toast.makeText(this, "Pendaftaran akun berhasil!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnSuccessListener(this) {
                val uid = auth.currentUser?.uid
                saveUserToFirebaseDatabase(uid, name, gender, age, address, email, phone, "")
                addAppointment(uid, name, email, date, poli, time, description)
            }
    }

    private fun saveUserToFirebaseDatabase(uid: String?, name: String, gender: String, age: String, address: String, email: String, phone: String, username: String) {
        val usertype = "Personal"

        val user = PersonalUser(uid, username, name, email, phone, age, gender, address, usertype, "", "", "")
        uid?.let {
            userRef.child(it).setValue(user)
                .addOnCompleteListener {
                    Log.d("RegisterActivity", "Finally we saved the user to firebase database")
                }
        }
    }

    private fun addAppointment(uid: String?, name: String, email: String, date: String, poli: String, time: String, description: String) {
        uid?.let { setNoAntrian(it, name, email, date, poli, time, description) }
    }

    private fun setNoAntrian(uid: String?, name: String, email: String, date: String, poli: String, time: String, description: String) {
        appointmentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (time == "Pagi (06.00–08.00)") {
                    if (!snapshot.exists()) {
                        noAntrian += 1
                        Log.d("antrian", "No Antrian : $noAntrian")
                        uid?.let { makeAppointment(it, name, email, date, poli, time, description, noAntrian) }
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
                            noAntrian += maxNoAntrian.toInt() + 1
                        }
//                        val jumlah = jumlahPagi + 1


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

    private fun makeAppointment(uid: String?, name: String, email: String, date: String, poli: String, time: String, description: String, noAntrian: Int) {
        val appointment = UserAppointment(uid, name, email, date, poli, time, description, noAntrian)
        uid?.let {
            appointmentRef.child(it).setValue(appointment)
                .addOnCompleteListener {
                    Toast.makeText(this, "Janji Temu dibuat", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminMainActivity::class.java))
                    finish()
                }
        }
    }
}