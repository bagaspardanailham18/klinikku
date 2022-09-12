package com.bagas.klinikapp.admin.ui.appointment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bagas.klinikapp.databinding.ItemRowAppointmentBinding
import com.bagas.klinikapp.model.UserAppointment

class ListAppointmentAdapter(val listAppointment: List<UserAppointment>): RecyclerView.Adapter<ListAppointmentAdapter.ListAppointmentVH>() {

    private lateinit var onItemClickCallback: ListAppointmentAdapter.OnItemClickCallback

    fun setOnItemClicked(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListAppointmentAdapter.ListAppointmentVH {
        val binding = ItemRowAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListAppointmentVH(binding)
    }

    override fun onBindViewHolder(holder: ListAppointmentAdapter.ListAppointmentVH, position: Int) {
        val item = listAppointment[position]

        holder.apply {
            tvname.text = item.name.toString().trim()
            tvNoAntrian.text = item.noantrian.toString().trim()
            tvTime.text = item.time.toString().trim()
            tvpoli.text = item.poli.toString().trim()
            tvdescription.text = item.description.toString().trim()
        }

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listAppointment[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = listAppointment.size

    inner class ListAppointmentVH(binding: ItemRowAppointmentBinding): RecyclerView.ViewHolder(binding.root) {
        val tvname = binding.tvPasienName
        val tvNoAntrian = binding.tvNoAntrian
        val tvTime = binding.tvTime
        val tvpoli = binding.tvPoliAppointment
        val tvdescription = binding.tvDescriptionAppointment
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserAppointment)
    }
}