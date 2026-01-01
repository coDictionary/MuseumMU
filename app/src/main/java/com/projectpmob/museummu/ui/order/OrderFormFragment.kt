package com.projectpmob.museummu.ui.order

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.projectpmob.museummu.R
import com.projectpmob.museummu.SessionManager
import com.projectpmob.museummu.databinding.FragmentOrderFormBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class OrderFormFragment : Fragment(R.layout.fragment_order_form) {

    private var _binding: FragmentOrderFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    // Variabel Penentu Mode
    private var isGroupOrder = false
    private var currentCaptcha = ""
    private var selectedDateString = "" // YYYY-MM-DD

    companion object {
        const val ARG_IS_GROUP = "is_group"

        // Fungsi ini dipanggil oleh OrderPagerAdapter
        fun newInstance(isGroup: Boolean): OrderFormFragment {
            val fragment = OrderFormFragment()
            val args = Bundle()
            args.putBoolean(ARG_IS_GROUP, isGroup)
            fragment.arguments = args
            return fragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrderFormBinding.bind(view)
        sessionManager = SessionManager(requireContext())

        // 1. Ambil Argumen dari Adapter (Individu/Grup?)
        isGroupOrder = arguments?.getBoolean(ARG_IS_GROUP) ?: false

        // 2. Atur Tampilan berdasarkan Mode
        setupUIMode()

        // 3. Setup Fitur Lainnya
        generateCaptcha()
        setupDatePicker()
        setupListeners()
        setupObservers()
        autoFillUserData()
    }

    private fun setupUIMode() {
        if (isGroupOrder) {
            // MODE GRUP: Tampilkan Input Jumlah Orang
            binding.layoutGroupInput.visibility = View.VISIBLE
            binding.etTotalPerson.setText("10") // Default saran
            binding.btnSubmitOrder.text = "Pesan Tiket Rombongan"
        } else {
            // MODE INDIVIDU: Sembunyikan Input, Set nilai 1
            binding.layoutGroupInput.visibility = View.GONE
            binding.etTotalPerson.setText("1")
            binding.btnSubmitOrder.text = "Pesan Tiket Individu"
        }
    }

    private fun autoFillUserData() {
        binding.etName.setText(sessionManager.prefs.getString("full_name", ""))
        binding.etEmail.setText(sessionManager.prefs.getString("email", ""))
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            // Hanya bisa pilih HARI INI ke depan
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Kunjungan")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

            datePicker.show(parentFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->
                // Validasi Sabtu & Minggu
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = selection
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    Toast.makeText(context, "Museum tutup hari Sabtu & Minggu", Toast.LENGTH_LONG).show()
                    binding.etDate.setText("")
                    selectedDateString = ""
                } else {
                    val formatDisplay = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                    binding.etDate.setText(formatDisplay.format(calendar.time))

                    val formatApi = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    selectedDateString = formatApi.format(calendar.time)

                    // Cek Sesi setelah tanggal dipilih
                    checkAvailability()
                }
            }
        }
    }

    private fun checkAvailability() {
        if (selectedDateString.isNotEmpty()) {
            val personCount = binding.etTotalPerson.text.toString().toIntOrNull() ?: 1
            viewModel.checkSessionAvailability(selectedDateString, personCount)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListeners() {
        binding.btnRefreshCaptcha.setOnClickListener { generateCaptcha() }

        // Jika mode GRUP dan jumlah orang diubah, cek ulang ketersediaan sesi
        if (isGroupOrder) {
            binding.etTotalPerson.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { checkAvailability() }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        binding.btnSubmitOrder.setOnClickListener {
            val totalPerson = binding.etTotalPerson.text.toString().toIntOrNull() ?: 1
            val type = if (isGroupOrder) "group" else "individual"

            // 1. Ambil UID asli dari Firebase Authentication
            val currentUser = FirebaseAuth.getInstance().currentUser
            val currentUid = currentUser?.uid

            // Cek safety, kalau user null (sesi habis), jangan boleh pesan
            if (currentUid == null) {
                Toast.makeText(context, "Sesi login habis, silakan login ulang", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Validasi Khusus Grup
            if (isGroupOrder && (totalPerson < 1 || totalPerson > 60)) {
                binding.tilTotalPerson.error = "Jumlah wajib 1 - 60 orang"
                return@setOnClickListener
            } else {
                binding.tilTotalPerson.error = null
            }

            viewModel.submitTicket(
                userId = currentUid,
                type = type,
                totalPerson = totalPerson,
                sessionFullString = binding.actvSession.text.toString(),
                name = binding.etName.text.toString().trim(),
                email = binding.etEmail.text.toString().trim(),
                phone = binding.etPhone.text.toString().trim(),
                origin = binding.etOrigin.text.toString().trim(),
                captchaInput = binding.etCaptchaInput.text.toString(),
                captchaReal = currentCaptcha
            )
        }
    }

    private fun setupObservers() {
        viewModel.availableSessions.observe(viewLifecycleOwner) { sessions ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sessions)
            binding.actvSession.setAdapter(adapter)
            binding.actvSession.text = null // Reset pilihan
            binding.tilSession.hint = if (sessions.isEmpty()) "Sesi Penuh/Libur" else "Pilih Sesi"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.btnSubmitOrder.isEnabled = !isLoading
        }

        viewModel.orderResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Pemesanan Berhasil!", Toast.LENGTH_SHORT).show()

                // --- KODE PERBAIKAN MULAI ---

                // Kita buat opsi navigasi:
                // "PopUpTo orderFragment inclusive = true" artinya: Hapus halaman Order dari memori saat pindah
                val navOptions = androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.orderFragment, true)
                    .setLaunchSingleTop(true)
                    .build()

                findNavController().navigate(R.id.historyFragment, null, navOptions)

            }.onFailure {
                Toast.makeText(context, "Gagal: ${it.message}", Toast.LENGTH_LONG).show()
                generateCaptcha() // Reset captcha agar tidak spam
            }
        }
    }

    private fun generateCaptcha() {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        currentCaptcha = (1..5).map { chars.random() }.joinToString("")
        binding.tvCaptchaCode.text = currentCaptcha
        binding.etCaptchaInput.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}