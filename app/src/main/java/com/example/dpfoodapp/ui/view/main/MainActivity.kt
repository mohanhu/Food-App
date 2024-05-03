package com.example.dpfoodapp.ui.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dpfoodapp.R
import com.example.dpfoodapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navcontroller = Navigation.findNavController(findViewById(R.id.frameLayout1))
        binding.bottomNavigationView.setupWithNavController(navcontroller)
        setupActionBarWithNavController(navcontroller)
        supportActionBar?.hide()
    }
}



