import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Butuh dependency fragment-ktx
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.projectpmob.museummu.R
import com.projectpmob.museummu.databinding.FragmentEditProfileBinding
import com.projectpmob.museummu.ui.profile.ResultState

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi ViewModel
    private val viewModel: EditProfileViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = auth.currentUser?.uid

        if (uid != null) {
            // 1. Panggil data saat fragment dibuat
            viewModel.loadUser(uid)

            // 2. Setup Tombol Simpan
            binding.btnSave.setOnClickListener {
                val name = binding.etName.text.toString().trim()
                val phone = binding.etPhone.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val username = binding.etUserName.text.toString().trim()
                val passHash = binding.etPass.text.toString().trim()


                viewModel.updateUser(uid, username,name, email, phone , passHash)

                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
            }
        } else {
            Toast.makeText(context, "Sesi habis, silakan login ulang", Toast.LENGTH_SHORT).show()
        }

        observeData()
    }

    private fun observeData() {
        // Observer untuk Load Data User
        viewModel.userState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showLoading(false)
                    val user = result.data
                    binding.etUserName.setText(user.username)
                    binding.etName.setText(user.fullName)
                    binding.etEmail.setText(user.email)
                    binding.etPhone.setText(user.phone)
                    binding.etPass.setText(user.passwordHash)
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observer untuk Proses Update
        viewModel.updateState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showLoading(false)
                    Toast.makeText(context, "Profil Berhasil Diupdate!", Toast.LENGTH_LONG).show()
                    findNavController().navigateUp() // Kembali ke halaman sebelumnya
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSave.isEnabled = !isLoading
        binding.btnSave.text = if (isLoading) "Menyimpan..." else "Simpan Perubahan"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}