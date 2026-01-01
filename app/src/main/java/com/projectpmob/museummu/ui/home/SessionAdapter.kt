package com.projectpmob.museummu.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projectpmob.museummu.data.model.Session
import com.projectpmob.museummu.databinding.ItemSessionStatusBinding

// Sesuaikan import

class SessionAdapter : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    private val sessions = ArrayList<Session>()

    fun setSessions(data: List<Session>) {
        sessions.clear()
        sessions.addAll(data)
        notifyDataSetChanged()
    }

    class SessionViewHolder(private val binding: ItemSessionStatusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(session: Session) {
            binding.tvSessionName.text = "Sesi ${session.id}"

            // Memecah String jam "08:00 - 10:00" menjadi dua bagian
            val times = session.timeRange.split("-")
            if (times.size > 1) {
                binding.tvTime.text = times[0].trim() // "08:00"
                binding.tvTimeEnd.text = "- ${times[1].trim()}" // "- 10:00"
            } else {
                binding.tvTime.text = session.timeRange
                binding.tvTimeEnd.text = ""
            }

            // Update Progress Bar
            binding.pbCapacity.max = session.maxCapacity
            binding.pbCapacity.progress = session.currentBooked
            binding.tvCapacityCount.text = "${session.currentBooked} / ${session.maxCapacity}"

            // Logic Warna Indikator
            val context = binding.root.context
            val remaining = session.getRemaining()

            // Mengubah warna Badge bulat kecil di pojok kanan atas
            if (remaining <= 0) {
                binding.cvStatusBadge.setCardBackgroundColor(Color.RED)
                // Opsional: Ubah tint progress bar jadi merah
                binding.pbCapacity.progressTintList =
                    android.content.res.ColorStateList.valueOf(Color.RED)
            } else if (remaining < 10) {
                binding.cvStatusBadge.setCardBackgroundColor(Color.parseColor("#FFA000")) // Orange
                binding.pbCapacity.progressTintList =
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#FFA000"))
            } else {
                binding.cvStatusBadge.setCardBackgroundColor(Color.parseColor("#006935")) // Hijau
                binding.pbCapacity.progressTintList =
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#006935"))
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(sessions[position])
    }

    override fun getItemCount(): Int = sessions.size
}