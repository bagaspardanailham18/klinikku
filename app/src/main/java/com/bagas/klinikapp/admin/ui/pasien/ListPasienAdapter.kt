package com.bagas.klinikapp.admin.ui.pasien

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bagas.klinikapp.databinding.ItemRowPasienBinding
import com.bagas.klinikapp.model.PersonalUser

class ListPasienAdapter(val listPasien: List<PersonalUser>): RecyclerView.Adapter<ListPasienAdapter.ListPasienVH>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClicked(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListPasienAdapter.ListPasienVH {
        val binding = ItemRowPasienBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListPasienVH(binding)
    }

    override fun onBindViewHolder(holder: ListPasienAdapter.ListPasienVH, position: Int) {
        val item = listPasien[position]

        holder.tvName.text = item.name
        holder.tvEmail.text = item.email
        holder.tvPhone.text = item.phonenumber

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listPasien[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = listPasien.size

    inner class ListPasienVH(binding: ItemRowPasienBinding): RecyclerView.ViewHolder(binding.root) {
        val tvName = binding.tvPasienName
        val tvEmail = binding.tvPasienEmail
        val tvPhone = binding.tvPasienPhone
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: PersonalUser)
    }
}