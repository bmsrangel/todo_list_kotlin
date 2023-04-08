package br.com.bmsrangel.dev.todolist.app.modules.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.components.CustomButtonFragment
import br.com.bmsrangel.dev.todolist.app.modules.auth.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val PERMISSION_REQUEST_CAMERA = 0
    private val PERMISSON_REQUEST_READ_EXTERNAL_STORAGE = 1

    private var image: Bitmap? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var activity: Activity
    private lateinit var view: View

    companion object {
        private const val REQUEST_IMAGE_GALLERY = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity = requireActivity()
        view = inflater.inflate(R.layout.fragment_profile, container, false)
        val googleSignOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, googleSignOptions)

        val cameraButtonRef = view.findViewById<FloatingActionButton>(R.id.fabCamera)
        val galleryButtonRef = view.findViewById<FloatingActionButton>(R.id.fabGallery)

        val updateProfileButtonRef = CustomButtonFragment()
        updateProfileButtonRef.buttonText = getString(R.string.profileUpdateButtonText)
        updateProfileButtonRef.onClick = {saveProfile()}
        childFragmentManager.beginTransaction().replace(R.id.updateProfileBtnFragment, updateProfileButtonRef).commit()

        val logoutButtonRef = CustomButtonFragment()
        logoutButtonRef.buttonText = getString(R.string.profileLogoutButtonText)
        logoutButtonRef.onClick = {
            firebaseAuth.signOut()
            googleSignInClient.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        childFragmentManager.beginTransaction().replace(R.id.logoutBtnFragment, logoutButtonRef).commit()

        val emailEditTxtRef = view.findViewById<EditText>(R.id.editTextProfileEmailAddress)
        val firstNameEditTextRef = view.findViewById<EditText>(R.id.editTextFirstName)
        val lastNameEditTextRef = view.findViewById<EditText>(R.id.editTextLastName)

        firebaseAuth = FirebaseAuth.getInstance()


        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val displayName = currentUser.displayName
            val email = currentUser.email
            var photoUrl = currentUser.photoUrl

            val splittedName = displayName?.split(" ")
            // TODO: Verify if the display name contains in fact 2 names; change to handle the case where there's just first name
            val firstName = splittedName?.first()
            val lastName = splittedName?.last()

            emailEditTxtRef.setText(email)
            firstNameEditTextRef.setText(firstName)
            lastNameEditTextRef.setText(lastName)
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
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSON_REQUEST_READ_EXTERNAL_STORAGE)
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
                Toast.makeText(activity, "Permissão de câmera concedida", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Permissão de câmera negada", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == PERMISSON_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                println("Galeria")
                Toast.makeText(activity, "Permissão de galeria concedida", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Permissão de galeria negada", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if(result.resultCode == AppCompatActivity.RESULT_OK) {
            val capturedImage = result.data?.extras?.get("data") as Bitmap
            this.image = capturedImage
            val profileImageRef = view.findViewById<ImageView>(R.id.profileImage)
            profileImageRef.setImageBitmap(capturedImage)

        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImage: Uri? = result.data?.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, selectedImage)
            this.image = imageBitmap
            val profileImageRef = view.findViewById<ImageView>(R.id.profileImage)
            profileImageRef.setImageBitmap(imageBitmap)
        }
    }

    private fun saveProfile() {}

    private fun saveLocalFile() {}
}