package com.bagas.klinikapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.bagas.klinikapp.admin.AdminMainActivity
import com.bagas.klinikapp.databinding.ActivityLoginBinding
import com.bagas.klinikapp.user.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnLogin.setOnClickListener { formValidation() }
        binding.toRegister.setOnClickListener {
            val toRegister = Intent(this, RegisterActivity::class.java)
            startActivity(toRegister)
        }
    }

    private fun formValidation() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (email.isEmpty() || email == "") {
            binding.layoutEdtEmail.error = "Email harus diisi!"
            binding.layoutEdtEmail.requestFocus()
            return

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEdtEmail.error = "Email tidak valid"
            binding.layoutEdtEmail.requestFocus()
            return

        } else if (password.isEmpty() || password == "") {
            binding.layoutEdtPassword.error = "Password harus diisi!"
            binding.layoutEdtPassword.requestFocus()
            return

        } else {
            performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(this, "User belum terdaftar atau password salah.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            firebaseDatabase = FirebaseDatabase.getInstance()
            databaseReference = firebaseDatabase.getReference("PersonalUsers").child(auth.uid!!)
            databaseReference.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("usertype").value == "Personal") {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                    if (snapshot.child("usertype").value == "Admin") {
                        startActivity(Intent(this@LoginActivity, AdminMainActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        } else {
            return
        }
    }
}