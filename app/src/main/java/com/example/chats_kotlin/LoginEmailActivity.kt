package com.example.chats_kotlin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chats_kotlin.databinding.ActivityLoginEmailBinding

class LoginEmailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Guardamos el padding original definido en XML
        val mainLayout = binding.main
        val originalPadding = intArrayOf(
            mainLayout.paddingLeft,
            mainLayout.paddingTop,
            mainLayout.paddingRight,
            mainLayout.paddingBottom
        )

        // Ajustamos solo los insets del sistema sumándolos al padding original
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                originalPadding[0] + systemBars.left,
                originalPadding[1] + systemBars.top,
                originalPadding[2] + systemBars.right,
                originalPadding[3] + systemBars.bottom
            )
            insets
        }

        // Click en "Registrarme"
        binding.tvRegistrarme.setOnClickListener {
            startActivity(Intent(this, RegistroEmailActivity::class.java))
        }
    }
}
