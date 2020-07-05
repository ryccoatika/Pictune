package com.ryccoatika.pictune

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ryccoatika.pictune.db.room.AppDatabase
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(home_toolbar)


        val navController = findNavController(R.id.home_fragment_container)
        home_bottom_nav.setupWithNavController(navController)

        home_bottom_nav.setOnNavigationItemReselectedListener {}
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.destroyDB()
    }
}
