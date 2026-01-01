package com.projectpmob.museummu.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.projectpmob.museummu.R
import com.projectpmob.museummu.databinding.FragmentDetailHistoryBinding

class DetailHistoryFragment : Fragment(R.layout.fragment_detail_history) {

    private val args: DetailHistoryFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDetailHistoryBinding.bind(view)

        // Ambil data tiket dari Arguments
        val ticket = args.ticketData

        // Set ke tampilan
        binding.tvDetailId.text = "${ticket.ticketId}"
        binding.tvDetailName.text = "${ticket.name}"
        binding.tvDetailSession.text = "${ticket.sessionLabel}"
        binding.tvDetailDate.text = "${ticket.visitDate}"
        binding.tvDetailPerson.text = "Jumlah: ${ticket.totalPerson} Orang"
        binding.tvDetailPrice.text = "Total Bayar: Rp ${ticket.totalPrice}"
        binding.tvDetailStatus.text = "Status: ${ticket.status}"
        binding.tvDetailEmail.text = ticket.email
        binding.tvDetailPhone.text = ticket.phone
        binding.tvDetailOrigin.text = ticket.originCity
        // Lanjutkan untuk email, hp, asal, timestamp, dll
    }
}