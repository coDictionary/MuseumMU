import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.projectpmob.museummu.data.model.User
import com.projectpmob.museummu.data.repository.UserRepository
import com.projectpmob.museummu.ui.profile.ResultState

class EditProfileViewModel : ViewModel() {

    private val repository = UserRepository() // Injeksi Repository

    // LiveData untuk Load Data Profil
    private val _userState = MutableLiveData<ResultState<User>>()
    val userState: LiveData<ResultState<User>> = _userState

    // LiveData untuk status Update
    private val _updateState = MutableLiveData<ResultState<Boolean>>()
    val updateState: LiveData<ResultState<Boolean>> = _updateState

    fun loadUser(uid: String) {
        repository.getUserProfile(uid) { result ->
            _userState.value = result
        }
    }

    fun updateUser(uid: String, username: String, fullname: String, email: String, phone: String, passwordHash:String) {
        // Validasi sederhana bisa dilakukan di ViewModel
        if (uid.isEmpty()) {
            _updateState.value = ResultState.Error("Nama tidak boleh kosong")
            return
        }

        val updates = mapOf<String, Any>(
            "username" to username,
            "fullName" to fullname,
            "email" to email,
            "phone" to phone,
            "passwordHash" to passwordHash
        )

        repository.updateUserProfile(uid, updates) { result ->
            _updateState.value = result
        }
    }
}