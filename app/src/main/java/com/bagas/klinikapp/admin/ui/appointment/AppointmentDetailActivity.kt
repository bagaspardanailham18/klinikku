package com.bagas.klinikapp.admin.ui.appointment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavArgs
import androidx.navigation.navArgs
import com.bagas.klinikapp.R
import com.bagas.klinikapp.databinding.ActivityAppointmentDetailBinding
import com.google.firebase.database.*

class AppointmentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentDetailBinding

    private val args: AppointmentDetailActivityArgs by navArgs<AppointmentDetailActivityArgs>()

    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ref = FirebaseDatabase.getInstance().getReference("PersonalUsers")

        binding.tvPasienName.text = args.name
        binding.tvPasienEmail.text = args.email

        binding.tvNoAntrian.text = args.noantrian.toString()
        binding.tvPoli.text = args.poli
        binding.tvDate.text = args.date
        binding.tvTime.text = args.time
        binding.tvDescription.text = args.description

        ref.child(args.uid).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.tvAge.setText(getString(R.string.tv_age, snapshot.child("age").value.toString()))
                    binding.tvGender.text = snapshot.child("gender").value.toString()
                    binding.tvPasienPhone.text = snapshot.child("phonenumber").value.toString()
                    binding.tvAddress.text = snapshot.child("address").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}