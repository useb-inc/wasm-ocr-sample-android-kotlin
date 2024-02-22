package com.useb.wasm_ocr_sample_android_kotlin

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import org.json.JSONException
import org.json.JSONObject

class ReportActivity : AppCompatActivity() {
    private var result: String? = ""
    private var detail: String? = ""
    private var status: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        val retryButton = findViewById<AppCompatButton>(R.id.btn_retry)
        retryButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        status = intent.getStringExtra("status")
        result = intent.getStringExtra("result")
        detail = intent.getStringExtra("detail")

        setResult()
    }

    private fun setResult() {
        try {
            val jsonObject = JSONObject(detail)
            val reviewResult = JSONObject(jsonObject.getString("review_result"))
            val statusTv = findViewById<TextView>(R.id.status)
            statusTv.text = status
            val resultTv = findViewById<TextView>(R.id.result)
            resultTv.text = result
            if (intent.hasExtra("originalImage")) {
                val byteArray = intent.getByteArrayExtra("originalImage")
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                val tv = findViewById<TextView>(R.id.textOriginalImage)
                if (reviewResult.getString("ocr_type") == "credit") {
                    tv.text = "- 신용카드 원본 사진"
                } else {
                    tv.text = "- 신분증 원본 사진"
                }
                tv.visibility = View.VISIBLE
                val iv = findViewById<ImageView>(R.id.originalImageView)
                iv.visibility = View.VISIBLE
                iv.setImageBitmap(bitmap)
            } else if (intent.hasExtra("originalImageEncrypted")) {
                val encryptedImage = intent.getStringExtra("originalImageEncrypted")
                val tv = findViewById<TextView>(R.id.textOriginalImage)
                if (reviewResult.getString("ocr_type") == "credit") {
                    tv.text = "- 신용카드 원본 사진"
                } else {
                    tv.text = "- 신분증 원본 사진"
                }
                tv.visibility = View.VISIBLE
                val tv2 = findViewById<TextView>(R.id.textEncryptedOriginal)
                tv2.text = encryptedImage
                tv2.visibility = View.VISIBLE
            }
            if (intent.hasExtra("maskedImage")) {
                val byteArray = intent.getByteArrayExtra("maskedImage")
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                val tv = findViewById<TextView>(R.id.textMaskImage)
                tv.visibility = View.VISIBLE
                if (reviewResult.getString("ocr_type") == "credit") {
                    tv.text = "- 신용카드 마스킹 사진"
                } else {
                    tv.text = "- 신분증 마스킹 사진"
                }
                val iv = findViewById<ImageView>(R.id.maskedImageView)
                iv.visibility = View.VISIBLE
                iv.setImageBitmap(bitmap)
            } else if (intent.hasExtra("maskedImageEncrypted")) {
                val encryptedImage = intent.getStringExtra("maskedImageEncrypted")
                val tv = findViewById<TextView>(R.id.textMaskImage)
                if (reviewResult.getString("ocr_type") == "credit") {
                    tv.text = "- 신용카드 원본 사진"
                } else {
                    tv.text = "- 신분증 원본 사진"
                }
                tv.visibility = View.VISIBLE
                val tv2 = findViewById<TextView>(R.id.textEncryptedMaskImage)
                tv2.text = encryptedImage
                tv2.visibility = View.VISIBLE
            }
            val detailTv = findViewById<TextView>(R.id.detail)
            detailTv.text = detail
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}