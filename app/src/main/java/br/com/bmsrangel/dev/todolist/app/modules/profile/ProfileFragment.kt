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
import androidx.fragment.app.Fragment
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
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.components.CustomButtonFragment
import br.com.bmsrangel.dev.todolist.app.modules.auth.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val PERMISSION_REQUEST_CAMERA = 0
    private val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1

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
            saveProfile(nameEditTextRef.text.toString())
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



        firebaseAuth = FirebaseAuth.getInstance()


        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val displayName = currentUser.displayName
            val email = currentUser.email
            val photoUrl = currentUser.photoUrl

            emailEditTxtRef.setText(email)
            nameEditTextRef.setText(displayName)
            val profileImageRef = view.findViewById<ImageView>(R.id.profileImage)
            if (photoUrl != null) {
              Thread {
                  val file = saveLocalFile(photoUrl.toString())
                  activity.runOnUiThread {
                      val bitmap = BitmapFactory.decodeFile(file.path)
                      profileImageRef.setImageBitmap(bitmap)
                  }
              }.start()
            } else {
                val personDrawable = ContextCompat.getDrawable(activity, R.drawable.baseline_person_24)!!.mutate()
                personDrawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(activity, R.color.infnet_blue), PorterDuff.Mode.SRC_IN)
                profileImageRef.setImageDrawable(personDrawable)
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

    private fun saveProfile(name: String) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val uid = firebaseAuth.currentUser!!.uid

        val imageRef = storageRef.child("profile_images/$uid")
        val byteArrayOutputStream = ByteArrayOutputStream()
        this.image?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(activity, "Falha ao salvar imagem", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {uri ->
                val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(Uri.parse(uri.toString())).build()
                firebaseAuth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(activity, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "Falha ao atualizar o perfil", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }


    }

    private fun saveLocalFile(imageUrl: String): File {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val input = connection.inputStream
        val dir = File(activity.getExternalFilesDir(null), "images")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "profile.jpg")
        val output = FileOutputStream(file)
        val buffer = ByteArray(1024 )
        var read: Int
        while(input.read(buffer).also { read = it } != -1) {
            output.write(buffer, 0, read)
        }
        output.flush()
        output.close()
        input.close()

        return file
    }
}