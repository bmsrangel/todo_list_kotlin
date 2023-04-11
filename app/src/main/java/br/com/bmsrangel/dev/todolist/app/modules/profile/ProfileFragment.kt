package br.com.bmsrangel.dev.todolist.app.modules.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.dtos.ProfileDTO
import br.com.bmsrangel.dev.todolist.app.core.fragments.CustomButtonFragment
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.AuthViewModel
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.SuccessAuthState
import br.com.bmsrangel.dev.todolist.app.modules.auth.LoginActivity
import br.com.bmsrangel.dev.todolist.app.modules.profile.viewmodels.profile_image.ProfileImageViewModel
import br.com.bmsrangel.dev.todolist.app.modules.profile.viewmodels.profile_image.states.ErrorProfileImageState
import br.com.bmsrangel.dev.todolist.app.modules.profile.viewmodels.profile_image.states.SuccessProfileImageState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModels()
    private val profileImageViewModel: ProfileImageViewModel by viewModels()

    private val PERMISSION_REQUEST_CAMERA = 0
    private val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1

//    private var image: Bitmap? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var activity: Activity
    private lateinit var view: View

    companion object {
        private const val REQUEST_IMAGE_GALLERY = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }

    override fun onStart() {
        super.onStart()
        authViewModel.getUserFromLocalStorage()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity = requireActivity()
        view = inflater.inflate(R.layout.fragment_profile, container, false)

        val emailEditTxtRef = view.findViewById<EditText>(R.id.editTextProfileEmailAddress)
        val nameEditTextRef = view.findViewById<EditText>(R.id.editTextProfileName)

        val googleSignOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, googleSignOptions)

        val cameraButtonRef = view.findViewById<FloatingActionButton>(R.id.fabCamera)
        val galleryButtonRef = view.findViewById<FloatingActionButton>(R.id.fabGallery)

        val updateProfileButtonRef = CustomButtonFragment()
        updateProfileButtonRef.buttonText = getString(R.string.profileUpdateButtonText)
        updateProfileButtonRef.onClick = {
            val profileDTO = ProfileDTO(nameEditTextRef.text.toString(), null)
            authViewModel.updateProfile(profileDTO)
            nameEditTextRef.clearFocus()
        }
        childFragmentManager.beginTransaction().replace(R.id.updateProfileBtnFragment, updateProfileButtonRef).commit()

        val updatePasswordButtonRef = CustomButtonFragment()
        updatePasswordButtonRef.buttonText = getString(R.string.changePasswordButtonText)
        updatePasswordButtonRef.onClick = {
            val intent = Intent(activity, UpdatePasswordActivity::class.java)
            startActivity(intent)
        }
        childFragmentManager.beginTransaction().replace(R.id.updatePasswordBtnFragment, updatePasswordButtonRef).commit()

        val logoutButtonRef = CustomButtonFragment()
        logoutButtonRef.buttonText = getString(R.string.profileLogoutButtonText)
        logoutButtonRef.onClick = {
            firebaseAuth.signOut()
            googleSignInClient.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        childFragmentManager.beginTransaction().replace(R.id.logoutBtnFragment, logoutButtonRef).commit()

        authViewModel.getUser().observe(requireActivity()) { it ->
            val user = (it as SuccessAuthState).user
            val displayName = user.name
            val email = user.email
            val photoUrl = user.photoUrl

            emailEditTxtRef.setText(email)
            nameEditTextRef.setText(displayName)
            val profileImageRef = view.findViewById<ImageView>(R.id.profileImage)
            if (photoUrl != null) {
                Thread {
                    val directory = File(activity.getExternalFilesDir(null), "images")
                    val directoryPath = directory.path
                    profileImageViewModel.downloadAndSaveLocalImage(photoUrl, directoryPath)
                    activity.runOnUiThread {
                        val bitmap =
                            BitmapFactory.decodeFile(profileImageViewModel.imageFile!!.path)
                        profileImageRef.setImageBitmap(bitmap)
                    }

                }.start()
            } else {
                val personDrawable =
                    ContextCompat.getDrawable(activity, R.drawable.baseline_person_24)!!
                        .mutate()
                personDrawable.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(activity, R.color.infnet_blue),
                    PorterDuff.Mode.SRC_IN
                )
                profileImageRef.setImageDrawable(personDrawable)
            }
        }

        profileImageViewModel.getImage().observe(viewLifecycleOwner) {
            if (it is SuccessProfileImageState) {
                val profileDTO = ProfileDTO(null, it.profileImage)
                authViewModel.updateProfile(profileDTO)
            } else if (it is ErrorProfileImageState) {
                Toast.makeText(activity, getString(R.string.imageLoadErrorText), Toast.LENGTH_SHORT).show()
            }
        }

        cameraButtonRef.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra("request_type", REQUEST_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }

        galleryButtonRef.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra("request_type", REQUEST_IMAGE_GALLERY)
            galleryLauncher.launch(intent)
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
        }

        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                println("Camera")
                Toast.makeText(activity, getString(R.string.cameraPermissionGrantedText), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, getString(R.string.cameraPermissionDeniedText), Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                println("Galeria")
                Toast.makeText(activity, getString(R.string.galleryPermissionGrantedText), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, getString(R.string.galleryPermissionDeniedText), Toast.LENGTH_SHORT).show()
            }

        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if(result.resultCode == AppCompatActivity.RESULT_OK) {
            val capturedImage = result.data?.extras?.get("data") as Bitmap
            val user = authViewModel.userModel!!
            profileImageViewModel.uploadImage(user.uid, capturedImage)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImage: Uri? = result.data?.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, selectedImage)
            profileImageViewModel.imageBitmap = imageBitmap
            val user = (authViewModel.getUser().value as SuccessAuthState).user
            profileImageViewModel.uploadImage(user.uid, imageBitmap)
        }
    }
}