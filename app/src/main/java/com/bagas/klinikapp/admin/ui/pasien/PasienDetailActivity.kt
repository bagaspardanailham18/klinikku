package com.bagas.klinikapp.admin.ui.pasien

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.navArgs
import com.bagas.klinikapp.R
import com.bagas.klinikapp.databinding.ActivityPasienDetailBinding

class PasienDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasienDetailBinding

    private val args: PasienDetailActivityArgs by navArgs<PasienDetailActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasienDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvPasienName.text = args.name
        binding.tvPasienEmail.text = args.email
        binding.tvAge.setText(getString(R.string.tv_age, args.age))
        binding.tvGender.text = args.gender
        binding.tvPasienPhone.text = args.phone
        binding.tvAddress.text = args.address
        binding.tvBlood.text = args.bloodtype
        binding.tvObat.text = args.obat
        binding.tvRiwayat.text = args.riwayat

        binding.btnUpdatePasien.setOnClickListener {
            val intent = Intent(this, AdminUpdatePasienActivity::class.java).apply {
//                putExtra(AdminUpdatePasienActivity.EXTRA_NAME, args.name)
//                putExtra(AdminUpdatePasienActivity.EXTRA_EMAIL, args.email)
//                putExtra(AdminUpdatePasienActivity.EXTRA_AGE, args.age)
//                putExtra(AdminUpdatePasienActivity.EXTRA_PHONE, args.phone)
//                putExtra(AdminUpdatePasienActivity.EXTRA_ADDRESS, args.address)
                putExtra(AdminUpdatePasienActivity.EXTRA_UID, args.uid)
            }
            startActivity(intent)
        }

        binding.btnCall.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + args.phone)
            startActivity(dialIntent)
        }
    }
}