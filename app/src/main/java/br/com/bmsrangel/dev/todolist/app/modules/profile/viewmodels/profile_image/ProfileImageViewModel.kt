package br.com.bmsrangel.dev.todolist.app.modules.profile.viewmodels.profile_image

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.bmsrangel.dev.todolist.app.core.repositories.storage.StorageRepository
import br.com.bmsrangel.dev.todolist.app.modules.profile.viewmodels.profile_image.states.ErrorProfileImageState
import br.com.bmsrangel.dev.todolist.app.modules.profile.viewmodels.profile_image.states.LoadingProfileImageState
import br.com.bmsrangel.dev.todolist.app.modules.profile.viewmodels.profile_image.states.ProfileImageState
import br.com.bmsrangel.dev.todolist.app.modules.profile.viewmodels.profile_image.states.SuccessProfileImageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileImageViewModel @Inject constructor(private val storageRepository: StorageRepository): ViewModel() {
    private var profileImageLiveData = MutableLiveData<ProfileImageState>()

    fun getImage(): LiveData<ProfileImageState> = profileImageLiveData

    fun uploadImage(userId: String, profileImage: Bitmap) {
        profileImageLiveData.value = LoadingProfileImageState()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                storageRepository.saveImageToStorage(userId, profileImage)
            }
            result.fold({
                profileImageLiveData.value = SuccessProfileImageState(it)
            }, {
                profileImageLiveData.value = ErrorProfileImageState(it.message)
            })
        }
    }


}