package br.com.bmsrangel.dev.todolist.app.modules.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.fragments.WeatherFragment
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.AuthViewModel
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.SuccessAuthState
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.UnauthenticatedAuthState
import br.com.bmsrangel.dev.todolist.app.modules.auth.LoginActivity
import br.com.bmsrangel.dev.todolist.app.modules.profile.ProfileFragment
import com.google.android.gms.ads.MobileAds
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav : BottomNavigationView

    private val authViewModel: AuthViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        loadApp()
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                loadApp()
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        authViewModel.getUserFromLocalStorage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        askNotificationPermission()

        MobileAds.initialize(this)
    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    private fun loadApp() {
        authViewModel.getUser().observe(this) {authState ->
            when(authState) {
                is UnauthenticatedAuthState -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                is SuccessAuthState -> {
                    val weatherFragment = WeatherFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.weatherFragmentContainer, weatherFragment).commit()
                    loadFragment(TasksFragment())
                    val toolbarTitleRef = findViewById<MaterialToolbar>(R.id.mainToolbarTitle)
                    bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                    bottomNav.setOnItemSelectedListener {
                        when (it.itemId) {
                            R.id.navTasks -> {
                                toolbarTitleRef.title = getString(R.string.homeAppBarTitle)
                                loadFragment(TasksFragment())
                                true
                            }
                            R.id.navProfile -> {
                                toolbarTitleRef.title = getString(R.string.profileAppBarTitle)
                                loadFragment(ProfileFragment())
                                true
                            }
                            else -> false
                        }
                    }
                }
            }
        }
    }
}