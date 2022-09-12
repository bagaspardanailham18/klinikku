package com.bagas.klinikapp.admin.ui.appointment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagas.klinikapp.databinding.FragmentAppointmentBinding
import com.bagas.klinikapp.model.UserAppointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var appointmentList: MutableList<UserAppointment>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(AppointmentViewModel::class.java)

        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        ref = FirebaseDatabase.getInstance().getReference("UserAppointment")
        appointmentList = mutableListOf()
        val adapter = ListAppointmentAdapter(appointmentList)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    appointmentList.clear()
                    shownotfound(false)

                    for (a in snapshot.children) {
                        val data = a.getValue(UserAppointment::class.java)
                        if (data != null) {
                            appointmentList.add(data)
                        }
                    }

                    binding.rvAppointment.layoutManager = LinearLayoutManager(requireActivity())
                    binding.rvAppointment.adapter = adapter
                } else {
                    shownotfound(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        binding.rvAppointment.setHasFixedSize(true)

        adapter.setOnItemClicked(object : ListAppointmentAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserAppointment) {
                val toAppointmentDetail = AppointmentFragmentDirections.actionNavAppointmentToAppointmentDetailActivity()
                toAppointmentDetail.uid = data.uid.toString()
                toAppointmentDetail.name = data.name.toString()
                toAppointmentDetail.email = data.email.toString()
                toAppointmentDetail.poli = data.poli.toString()
                toAppointmentDetail.date = data.date.toString()
                toAppointmentDetail.time = data.time.toString()
                toAppointmentDetail.description = data.description.toString()
                toAppointmentDetail.noantrian = data.noantrian!!
                view.findNavController().navigate(toAppointmentDetail)
            }

        })

        binding.fabAddAppointment.setOnClickListener {
            val toAddAppointment = AppointmentFragmentDirections.actionNavAppointmentToAdminAddAppointmentActivity()
            view.findNavController().navigate(toAddAppointment)
        }
    }

    private fun shownotfound(show: Boolean) {
        if (show) {
            binding.rvAppointment.visibility = View.GONE
            binding.emptyMessage.visibility = View.VISIBLE
        } else {
            binding.rvAppointment.visibility = View.VISIBLE
            binding.emptyMessage.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}