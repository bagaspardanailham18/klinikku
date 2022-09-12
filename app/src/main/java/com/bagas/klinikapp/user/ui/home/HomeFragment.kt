package com.bagas.klinikapp.user.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bagas.klinikapp.R
import com.bagas.klinikapp.databinding.FragmentHomeBinding
import com.bagas.klinikapp.user.AppointmentActivity
import com.bagas.klinikapp.user.HistoryAppointmentActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ref = FirebaseDatabase.getInstance().getReference("UserAppointment")
        auth = Firebase.auth

        binding.menuCreate.setOnClickListener(this)
        binding.menuCheck.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.menu_create -> {
                ref.child(auth.uid!!).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Snackbar.make(requireView(), "Anda harus menunggu proses appointment saat ini selesai terlebih dahulu!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                            return
                        } else {
                            val toAppointment = Intent(requireActivity(), AppointmentActivity::class.java)
                            startActivity(toAppointment)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
            R.id.menu_check -> {
                val toHistory = Intent(requireActivity(), HistoryAppointmentActivity::class.java)
                startActivity(toHistory)
            }
        }
    }
}