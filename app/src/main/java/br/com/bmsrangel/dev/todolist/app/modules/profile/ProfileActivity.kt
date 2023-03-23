package br.com.bmsrangel.dev.todolist.app.modules.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.modules.auth.LoginActivity
import br.com.bmsrangel.dev.todolist.app.modules.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST_CAMERA = 0
    private val PERMISSON_REQUEST_READ_EXTERNAL_STORAGE = 1

    var _image: Bitmap? = null

    private lateinit var firebaseAuth: FirebaseAuth

    companion object {
        private const val REQUEST_IMAGE_GALLERY = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.hide()

        val homeButtonRef = findViewById<ImageView>(R.id.btnHome)
        val cameraButtonRef = findViewById<FloatingActionButton>(R.id.fabCamera)
        val galleryButtonRef = findViewById<FloatingActionButton>(R.id.fabGallery)
        val updateButtonRef = findViewById<Button>(R.id.btnUpdateProfile)
        val logoutButtonRef = findViewById<Button>(R.id.btnProfileLogout)

        val emailEditTxtRef = findViewById<EditText>(R.id.editTextProfileEmailAddress)
        val firstNameEditTextRef = findViewById<EditText>(R.id.editTextFirstName)
        val lastNameEditTextRef = findViewById<EditText>(R.id.editTextLastName)

        firebaseAuth = FirebaseAuth.getInstance()

        val googleSignOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignOptions)

        var currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            var displayName = currentUser.displayName
            var email = currentUser.email
            var photoUrl = currentUser.photoUrl

            val splittedName = displayName?.split(" ")
            // TODO: Verify if the display name contains in fact 2 names; change to handle the case where there's just first name
            val firstName = splittedName?.first()
            val lastName = splittedName?.last()

            emailEditTxtRef.setText(email)
            firstNameEditTextRef.setText(firstName)
            lastNameEditTextRef.setText(lastName)
        }

        homeButtonRef.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSON_REQUEST_READ_EXTERNAL_STORAGE)
        }

        updateButtonRef.setOnClickListener {
            saveProfile()
        }

        logoutButtonRef.setOnClickListener {
            firebaseAuth.signOut()
            googleSignInClient.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
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
                Toast.makeText(this, "Permissão de câmera concedida", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == PERMISSON_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                println("Galeria")
                Toast.makeText(this, "Permissão de galeria concedida", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissão de galeria negada", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if(result.resultCode == RESULT_OK) {
            val capturedImage = result.data?.extras?.get("data") as Bitmap
            this._image = capturedImage
            val profileImageRef = findViewById<ImageView>(R.id.profileImage)
            profileImageRef.setImageBitmap(capturedImage)

        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri? = result.data?.data;
            var imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
            this._image = imageBitmap
            val profileImageRef = findViewById<ImageView>(R.id.profileImage)
            profileImageRef.setImageBitmap(imageBitmap)
        }
    }

    private fun saveProfile() {}

    private fun saveLocalFile() {}
}