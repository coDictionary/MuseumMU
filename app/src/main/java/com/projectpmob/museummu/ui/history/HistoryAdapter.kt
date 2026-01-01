package com.projectpmob.museummu.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projectpmob.museummu.data.model.Ticket
import com.projectpmob.museummu.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val onClick: (Ticket) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val list = mutableListOf<Ticket>()

    fun submitList(newList: List<Ticket>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ticket: Ticket) {
            binding.tvType.text = ticket.type.uppercase()
            binding.tvTicketId.text = "ID: ${ticket.ticketId}"
            binding.tvVisitDate.text = ticket.visitDate
            binding.tvSession.text = "Sesi ${ticket.session} (${ticket.sessionLabel})"

            binding.root.setOnClickListener {
                onClick(ticket)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}