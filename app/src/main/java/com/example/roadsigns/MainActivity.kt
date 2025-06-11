package com.example.roadsigns

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Size
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.common.FileUtil
import androidx.camera.core.YuvToRgbConverter

class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var resultText: TextView
    private lateinit var interpreter: Interpreter
    private lateinit var yuvToRgb: YuvToRgbConverter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        previewView = findViewById(R.id.preview_view)
        resultText = findViewById(R.id.result_text)
        yuvToRgb = YuvToRgbConverter(this)

        val model = FileUtil.loadMappedFile(this, "model.tflite")
        interpreter = Interpreter(model)

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val analysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(416, 416))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(this), FrameAnalyzer())
                }
            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, selector, preview, analysis)
        }, ContextCompat.getMainExecutor(this))
    }

    inner class FrameAnalyzer : ImageAnalysis.Analyzer {
        private val output = Array(1) { FloatArray(1) }

        override fun analyze(image: ImageProxy) {
            val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
            yuvToRgb.yuvToRgb(image.image!!, bitmap)
            val tensorImage = TensorImage.fromBitmap(bitmap)
            interpreter.run(tensorImage.buffer, output)
            val percent = output[0][0] * 100
            runOnUiThread {
                resultText.text = String.format("Confidence: %.2f%%", percent)
            }
            image.close()
        }
    }
}
