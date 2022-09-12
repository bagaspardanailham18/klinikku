package com.bagas.klinikapp.admin.ui.pasien

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bagas.klinikapp.R
import com.bagas.klinikapp.admin.AdminMainActivity
import com.bagas.klinikapp.databinding.ActivityAdminUpdatePasienBinding
import com.bagas.klinikapp.user.MainActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminUpdatePasienActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminUpdatePasienBinding

    private lateinit var ref: DatabaseReference

    companion object {
        const val EXTRA_UID = "extra_uid"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUpdatePasienBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ref = FirebaseDatabase.getInstance().getReference("PersonalUsers")

        val uid = intent.getStringExtra(EXTRA_UID).toString().trim()

        val bloodType = resources.getStringArray(R.array.blood_type)
        val arrayBloodAdapter = ArrayAdapter(this, R.layout.dropdown_gender_item, bloodType)
        binding.edtBlood.setAdapter(arrayBloodAdapter)

        binding.btnEditPasien.setOnClickListener {
            Log.d("data", binding.edtRiwayat.text.toString())
            Log.d("data", binding.edtObat.text.toString())
            Log.d("data", binding.edtBlood.text.toString())
            uid.let {
                ref.child(it).child("riwayatPenyakit").setValue(binding.edtRiwayat.text.toString())
                ref.child(it).child("obat").setValue(binding.edtObat.text.toString())
                ref.child(it).child("bloodtype").setValue(binding.edtBlood.text.toString())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data berhasil diubah!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, AdminMainActivity::class.java))
                        finish()
                    }
            }
        }

    }
}