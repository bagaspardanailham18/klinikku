package com.bagas.klinikapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bagas.klinikapp.R
import com.bagas.klinikapp.databinding.ActivityRegisterBinding
import com.bagas.klinikapp.model.PersonalUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Auth
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("PersonalUsers")

        val gender = resources.getStringArray(R.array.gender)
        val arrayGenderAdapter = ArrayAdapter(this, R.layout.dropdown_gender_item, gender)
        binding.edtRegisterGender.setAdapter(arrayGenderAdapter)

        // Validate Form when Clicked
        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val gender = binding.edtRegisterGender.text.toString()
            val age = binding.edtAge.text.toString()
            val address = binding.edtAddress.text.toString()
            val email = binding.edtEmail.text.toString().trim()
            val phone = binding.edtPhone.text.toString().trim()
            val username = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

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
                binding.layoutEdtPhone.error = "No telepon harusu diisi!!"
                binding.layoutEdtPhone.requestFocus()
                return@setOnClickListener

            } else if (username.isEmpty() || username == "") {
                binding.layoutEdtUsername.error = "Username harus diisi!!"
                binding.layoutEdtUsername.requestFocus()
                return@setOnClickListener

            } else if (password.isEmpty() || password == "") {
                binding.layoutEdtPassword.error = "Password harus diisi!!"
                binding.layoutEdtPassword.requestFocus()
                return@setOnClickListener

            } else {
                registerUser(name, gender, age, address, email, phone, username, password)
            }
        }
    }

    private fun registerUser(name: String, gender: String, age: String, address: String, email: String, phone: String, username: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    Toast.makeText(this, "Pendaftaran akun berhasil!", Toast.LENGTH_SHORT).show()
                    saveUserToFirebaseDatabase(name, gender, age, address, email, phone, username)
                } else {
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToFirebaseDatabase(name: String, gender: String, age: String, address: String, email: String, phone: String, username: String) {
        val uid = auth.uid ?: ""
        val usertype = "Personal"

        val user = PersonalUser(uid, username, name, email, phone, age, gender, address, usertype, "", "", "")
        database.child(uid).setValue(user)
            .addOnCompleteListener {
                Log.d("RegisterActivity", "Finally we saved the user to firebase database")
            }
    }
}