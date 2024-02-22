package com.useb.wasm_ocr_sample_android_kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.useb.wasm_ocr_sample_android_kotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val btnOnClickListener = OnClickListener {
            val secondIntent = Intent(applicationContext, WebViewActivity::class.java)
            if (sendDataToWebview(secondIntent, it.tag.toString(), binding!!.btnEncryptMode.isChecked)) {
                startActivity(secondIntent)
            }
        }
        binding!!.btnIdcard.setOnClickListener(btnOnClickListener)
        binding!!.btnAlien.setOnClickListener(btnOnClickListener)
        binding!!.btnCredit.setOnClickListener(btnOnClickListener)
        binding!!.btnPassport.setOnClickListener(btnOnClickListener)
        binding!!.btnIdcardSsa.setOnClickListener(btnOnClickListener)
        binding!!.btnAlienSsa.setOnClickListener(btnOnClickListener)
        binding!!.btnPassportSsa.setOnClickListener(btnOnClickListener)

    }

    private fun sendDataToWebview(secondIntent: Intent, scanType: String, useEncryptMode: Boolean): Boolean {
        if (!isValid(scanType)) return false

        secondIntent.putExtra("scanType", scanType)
        secondIntent.putExtra("useEncryptMode", if (useEncryptMode) "true" else "false")
        return true
    }

    private fun isValid(scanType: String) : Boolean {
        val types = ArrayList(
            listOf("idcard", "passport", "alien", "credit", "idcard-ssa", "passport-ssa", "alien-ssa")
        )

        return types.contains(scanType)
    }
}