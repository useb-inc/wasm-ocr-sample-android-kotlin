package com.useb.wasm_ocr_sample_android_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.useb.wasm_ocr_sample_android_kotlin.databinding.ActivityWebViewBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

class WebViewActivity : AppCompatActivity() {
    private var binding: ActivityWebViewBinding? = null
    private var webview: WebView? = null
    private val OCR_LICENSE_KEY =
        "FPkTBLFIa/Tn/mCZ5WKPlcuDxyb2bJVPSURXacnhj2d82wm39/tFIjCPpMsiXoPxGbN6G6l5gSLMBfwB6nwgIJZFWX0WlS1Jl49321wADP7yEhxE="


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val url = "https://ocr.useb.co.kr/ocr.html"
        // 바인딩 설정
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.btnClose.setOnClickListener {
            finish()
        }
        // 웹뷰 설정
        webview = binding!!.webview
        webview!!.settings.javaScriptEnabled = true
        webview!!.webViewClient = WebViewClient()
        webview!!.webChromeClient = WebChromeClient()
        webview!!.addJavascriptInterface(this, "usebwasmocr")
        webview!!.settings.setAppCacheEnabled(false)
        webview!!.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        // 사용자 데이터 인코딩
        val encodedUserInfo: String = encodeJson()

        // POST
        postUserInfo(url, encodedUserInfo)

    }


    // WebView 액티비티에서 뒤로가기 버튼 막기
    override fun onBackPressed() {
        //super.onBackPressed();
    }

    private fun postUserInfo(url: String, encodedUserInfo: String) {
        val handler = Handler()
        handler.post(Runnable { // 카메라 권한 요청
            cameraAuthRequest()
            webview!!.loadUrl(url)
            webview!!.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    webview!!.loadUrl("javascript:usebwasmocrreceive('$encodedUserInfo')")
                }
            }
        })
    }


    private fun encodeJson(): String {
        val data: String = encodeURIComponent(getData().toString())
        return Base64.encodeToString(data.toByteArray(), 0)
    }

    @Throws(JSONException::class)
    private fun getData(): JSONObject {
        val scanType = intent.getStringExtra("scanType")
        return dataToJson(scanType)
    }

    @Throws(JSONException::class)
    private fun dataToJson(ocrType: String?): JSONObject {
        val settings = JSONObject()
        settings.put("licenseKey", this.OCR_LICENSE_KEY)
        settings.put("useEncryptMode", intent.getStringExtra("useEncryptMode"))
        val jsonObject = JSONObject()
        jsonObject.put("ocrType", ocrType)
        jsonObject.put("settings", settings)
        return jsonObject
    }

    private fun encodeURIComponent(encoded: String): String {
        return URLEncoder.encode(encoded, "UTF-8").replace("\\+".toRegex(), "%20")
            .replace("%21".toRegex(), "!").replace("%27".toRegex(), "'")
            .replace("%28".toRegex(), "(").replace("%29".toRegex(), ")")
            .replace("%7E".toRegex(), "~")

    }

    @JavascriptInterface
    @Throws(JSONException::class)
    fun receive(data: String?) {
        try {
            val intent = Intent(this@WebViewActivity, ReportActivity::class.java)
            val decodedData = decodedReceiveData(data)
            val jsonData = JSONObject(decodedData)
            val reviewResult = JSONObject(jsonData.getString("review_result"))
            var ocrType = reviewResult.getString("ocr_type")
            ocrType = if (ocrType == "idcard") {
                "주민증록증/운전면허증"
            } else if (ocrType == "passport") {
                "국내/해외여권"
            } else if (ocrType == "alien") {
                "외국인등록증"
            } else if (ocrType == "credit") {
                "신용카드"
            } else {
                "INVALID_TYPE"
            }
            val result = jsonData.getString("result")
            if (result == "success") {
                if (reviewResult.has("ocr_origin_image")) {
                    var b64 = reviewResult.getString("ocr_origin_image")
                    if (b64 != "null") {
                        if (b64.startsWith("data:image/")) {
                            b64 = b64.substring(b64.indexOf(",") + 1)
                            val byteArray = getByteArrayFromBase64String(b64)
                            intent.putExtra("originalImage", byteArray)
                        } else {
                            intent.putExtra("originalImageEncrypted", "Encrypted")
                        }
                    }
                }
                if (reviewResult.has("ocr_masking_image")) {
                    var b64 = reviewResult.getString("ocr_masking_image")
                    if (b64 != "null") {
                        if (b64.startsWith("data:image/")) {
                            b64 = b64.substring(b64.indexOf(",") + 1)
                            val byteArray = getByteArrayFromBase64String(b64)
                            intent.putExtra("maskedImage", byteArray)
                        } else {
                            intent.putExtra("maskedImageEncrypted", "Encrypted")
                        }
                    }
                }
                val modifiedJsonData = ModifyReviewResult(jsonData)
                intent.putExtra("status", "OCR이 완료되었습니다.")
                intent.putExtra("result", "- 인증 결과 : 성공\n- OCR 종류 : $ocrType")
                intent.putExtra("detail", modifiedJsonData.toString(4))
            } else if (result == "failed") {
                intent.putExtra("status", "OCR이 실패되었습니다.")
                intent.putExtra("result", "- 인증 결과 : 실패\n- OCR 종류 : $ocrType")
                intent.putExtra("detail", jsonData.toString(4))
            }
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            Log.e("EXCEPTION!!!!!!!", e.message!!)
            e.printStackTrace()
        }
    }

    private fun getByteArrayFromBase64String(str: String): ByteArray {
        val stream = ByteArrayOutputStream()
        val bitmap = getBitmapFromBase64String(str)
        val scale = (1024 / 2 / bitmap.width.toFloat())
        val image_w = (bitmap.width * scale).toInt()
        val image_h = (bitmap.height * scale).toInt()
        val resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true)
        resize.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun getBitmapFromBase64String(str: String): Bitmap {
        val decodedString = Base64.decode(str.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    @Throws(JSONException::class)
    private fun ModifyReviewResult(JsonObject: JSONObject): JSONObject {
        val reviewResult = JsonObject.getString("review_result")
        val reviewResultJsonObject = JSONObject(reviewResult)
        var originalImage = reviewResultJsonObject.getString("ocr_origin_image")
        var maskingImage = reviewResultJsonObject.getString("ocr_masking_image")
        var faceImage = reviewResultJsonObject.getString("ocr_face_image")
        if (originalImage !== "null") {
            originalImage = originalImage.substring(0, 20) + "...생략(omit)..."
            reviewResultJsonObject.put("ocr_origin_image", originalImage)
        }
        if (maskingImage !== "null") {
            maskingImage = maskingImage.substring(0, 20) + "...생략(omit)..."
            reviewResultJsonObject.put("ocr_masking_image", maskingImage)
        }
        if (faceImage !== "null") {
            faceImage = faceImage.substring(0, 20) + "...생략(omit)..."
            reviewResultJsonObject.put("ocr_face_image", faceImage)
        }
        JsonObject.put("review_result", reviewResultJsonObject)
        return JsonObject
    }

    fun decodedReceiveData(data: String?): String? {
        val decoded = String(Base64.decode(data, 0))
        return decodeURIComponent(decoded)
    }

    private fun decodeURIComponent(decoded: String): String? {
        var decodedURI: String? = null
        try {
            decodedURI = URLDecoder.decode(decoded, "UTF-8").replace("%20".toRegex(), "\\+")
                .replace("!".toRegex(), "\\%21").replace("'".toRegex(), "\\%27")
                .replace("\\(".toRegex(), "\\%28").replace("\\)".toRegex(), "\\%29")
                .replace("~".toRegex(), "\\%7E")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return decodedURI
    }


    private fun cameraAuthRequest() {
        webview = binding!!.webview
        val ws = webview!!.settings
        ws.mediaPlaybackRequiresUserGesture = false
        webview!!.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {

                //API레벨이 21이상인 경우
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val requestedResources = request.resources
                    for (r in requestedResources) {
                        if (r == PermissionRequest.RESOURCE_VIDEO_CAPTURE) {
                            request.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                            break
                        }
                    }
                }
            }
        }
        val cameraPermissionCheck =
            ContextCompat.checkSelfPermission(this@WebViewActivity, Manifest.permission.CAMERA)
        if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
            ActivityCompat.requestPermissions(
                this@WebViewActivity,
                arrayOf(Manifest.permission.CAMERA),
                1000
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@WebViewActivity,
                    "카메라/갤러리 접근 권한이 없습니다. 권한 허용 후 이용해주세요. no access permission for camera and gallery.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

}