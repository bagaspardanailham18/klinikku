package com.bagas.klinikapp.admin.ui.pasien

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bagas.klinikapp.databinding.FragmentPasienBinding
import com.bagas.klinikapp.model.PersonalUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.*

class PasienFragment : Fragment() {

    private var _binding: FragmentPasienBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

//    private lateinit var adapter: ListPasienAdapter
    private lateinit var pasienList: MutableList<PersonalUser>
    private lateinit var listPasien: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(PasienViewModel::class.java)

        _binding = FragmentPasienBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        ref = FirebaseDatabase.getInstance().getReference("PersonalUsers")
        pasienList = mutableListOf()
        listPasien = RecyclerView(requireActivity())
        var adapter = ListPasienAdapter(pasienList)

        binding.edtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query?.isNotEmpty()!! || query != "" || query != null) {
                    binding.edtSearch.clearFocus()
                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                pasienList.clear()
                                for (n in snapshot.children) {
                                    val data = n.getValue(PersonalUser::class.java)
                                    if (data?.name?.lowercase(Locale.getDefault()).equals("${query?.trim()
                                            ?.lowercase(Locale.getDefault())}") ) {
                                        pasienList.add(data!!)
                                    }
                                }
                                listPasien.adapter = adapter
                                adapter = ListPasienAdapter(pasienList)
                                binding.rvPasien.layoutManager = LinearLayoutManager(requireActivity())
                                binding.rvPasien.adapter = adapter
                                binding.rvPasien.setHasFixedSize(true)
                                Log.d("list", pasienList.size.toString())
                                adapter.setOnItemClicked(object : ListPasienAdapter.OnItemClickCallback {
                                    override fun onItemClicked(data: PersonalUser) {
                                        val toPasienDetailActivity = PasienFragmentDirections.actionNavPasienToPasienDetailActivity()
                                        toPasienDetailActivity.uid = data.uid.toString().trim()
                                        toPasienDetailActivity.email = data.email.toString().trim()
                                        toPasienDetailActivity.name = data.name.toString().trim()
                                        toPasienDetailActivity.age = data.age
                                        toPasienDetailActivity.gender = data.gender
                                        toPasienDetailActivity.phone = data.phonenumber
                                        toPasienDetailActivity.address = data.address
                                        toPasienDetailActivity.bloodtype = data.bloodtype
                                        toPasienDetailActivity.obat = data.obat
                                        toPasienDetailActivity.riwayat = data.riwayatPenyakit
                                        view.findNavController().navigate(toPasienDetailActivity)
                                    }

                                })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query?.isNotEmpty()!! || query != "" || query != null) {
                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                pasienList.clear()
                                for (n in snapshot.children) {
                                    val data = n.getValue(PersonalUser::class.java)
                                    if (data?.name?.lowercase(Locale.getDefault()).equals("${query?.trim()
                                            ?.lowercase(Locale.getDefault())}") ) {
                                        pasienList.add(data!!)
                                    }
                                }
                                adapter = ListPasienAdapter(pasienList)
                                listPasien.adapter = adapter
                                binding.rvPasien.layoutManager = LinearLayoutManager(requireActivity())
                                binding.rvPasien.adapter = adapter
                                binding.rvPasien.setHasFixedSize(true)
                                Log.d("list", pasienList.size.toString())
                                adapter.setOnItemClicked(object : ListPasienAdapter.OnItemClickCallback {
                                    override fun onItemClicked(data: PersonalUser) {
                                        val toPasienDetailActivity = PasienFragmentDirections.actionNavPasienToPasienDetailActivity()
                                        toPasienDetailActivity.uid = data.uid.toString().trim()
                                        toPasienDetailActivity.email = data.email.toString().trim()
                                        toPasienDetailActivity.name = data.name.toString().trim()
                                        toPasienDetailActivity.age = data.age
                                        toPasienDetailActivity.gender = data.gender
                                        toPasienDetailActivity.phone = data.phonenumber
                                        toPasienDetailActivity.address = data.address
                                        toPasienDetailActivity.bloodtype = data.bloodtype
                                        toPasienDetailActivity.obat = data.obat
                                        toPasienDetailActivity.riwayat = data.riwayatPenyakit
                                        view.findNavController().navigate(toPasienDetailActivity)
                                    }
                                })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }

                return true
            }

        })

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    pasienList.clear()
                    shownotfound(false)

                    for (p in snapshot.children) {
                        if (p.key == auth.currentUser?.uid.toString()) {
                            continue
                        }

                        val data = p.getValue(PersonalUser::class.java)
//                        Log.d("Pasien", data.toString())
                        if (data != null) {
                            pasienList.add(data)
                        }
                    }

                    Log.d("Pasien", "Data: ${pasienList}")

                    binding.rvPasien.adapter = adapter
                    binding.rvPasien.layoutManager = LinearLayoutManager(requireActivity())
                    binding.rvPasien.setHasFixedSize(true)
                } else {
                    shownotfound(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        adapter.setOnItemClicked(object : ListPasienAdapter.OnItemClickCallback {
            override fun onItemClicked(data: PersonalUser) {
                val toPasienDetailActivity = PasienFragmentDirections.actionNavPasienToPasienDetailActivity()
                toPasienDetailActivity.uid = data.uid.toString().trim()
                toPasienDetailActivity.email = data.email.toString().trim()
                toPasienDetailActivity.name = data.name.toString().trim()
                toPasienDetailActivity.age = data.age
                toPasienDetailActivity.gender = data.gender
                toPasienDetailActivity.phone = data.phonenumber
                toPasienDetailActivity.address = data.address
                toPasienDetailActivity.bloodtype = data.bloodtype
                toPasienDetailActivity.obat = data.obat
                toPasienDetailActivity.riwayat = data.riwayatPenyakit
                view.findNavController().navigate(toPasienDetailActivity)
            }

        })
    }

    private fun shownotfound(show: Boolean) {
        if (show) {
            binding.rvPasien.visibility = View.GONE
            binding.emptyMessage.visibility = View.VISIBLE
        } else {
            binding.rvPasien.visibility = View.VISIBLE
            binding.emptyMessage.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}