package br.com.bmsrangel.dev.todolist.app.modules.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.modules.profile.ProfileFragment
//import com.google.android.gms.ads.MobileAds
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

//        MobileAds.initialize(this)

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
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
}