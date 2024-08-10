package com.magnifyingglass.magnifiercamera.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.common.util.concurrent.ListenableFuture
import com.magnifyingglass.magnifiercamera.R
import com.magnifyingglass.magnifiercamera.base.BaseActivity
import com.magnifyingglass.magnifiercamera.databinding.ActivityMainBinding
import com.magnifyingglass.magnifiercamera.dialog.LightDialog
import com.magnifyingglass.magnifiercamera.util.DataManager
import com.magnifyingglass.magnifiercamera.util.ToastUtils
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToonFilter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var gpuImage: GPUImage
    override fun initView() {
        gpuImage = GPUImage(this)
        gpuImage.setGLSurfaceView(binding.glSurfaceView)
        outputDirectory = getOutputDirectory()
        if (hasCameraPermission()) {
            startCamera()
        } else {
            requestCameraPermission()
        }
        imageProcessor = GPUImageFilter()
    }

    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private var lvjing: Int = 0

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private fun takePhoto() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            savePhoto()
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_EXTERNAL_STORAGE_REQUEST
                )
            } else {
                savePhoto()
            }
        }
    }

    private fun savePhoto() {
        if (lvjing > 0) {
            val photoFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "Camera/${
                    SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis())
                }.jpg"
            )
            // Saving a Bitmap to a File
            try {
                val outputStream = FileOutputStream(photoFile)
                bitmap = gpuImage.bitmapWithFilterApplied
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                ToastUtils.showShort(this@MainActivity,getString(R.string.success_photo))
                // Notification Album
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(photoFile.absolutePath),
                    arrayOf("image/jpeg"),
                    null
                )
            } catch (e: IOException) {
                ToastUtils.showShort(this@MainActivity,getString(R.string.fail_photo))
            }
        } else {
            if(!isSuo){
                // Create a timestamped file to save the photo
                val photoFile = File(
                    outputDirectory,
                    SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis()) + ".jpg"
                )

                // Set Save Options
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                // Take photos
                imageCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            ToastUtils.showShort(this@MainActivity,getString(R.string.fail_photo))
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = output.savedUri ?: photoFile.toUri()
                            ToastUtils.showShort(this@MainActivity,getString(R.string.success_photo))
                            // Notification Album
                            MediaScannerConnection.scanFile(
                                this@MainActivity,
                                arrayOf(photoFile.absolutePath),
                                arrayOf("image/jpeg"),
                                null
                            )
                        }
                    }
                )
            }else{
                if(bitmap_dong!=null){
                    val photoFile = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                        "Camera/${
                            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                                .format(System.currentTimeMillis())
                        }.jpg"
                    )
                    // Saving a Bitmap to a File
                    try {
                        val outputStream = FileOutputStream(photoFile)
                        bitmap_dong?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.close()
                        ToastUtils.showShort(this@MainActivity,getString(R.string.success_photo))
                        // Notification Album
                        MediaScannerConnection.scanFile(
                            this,
                            arrayOf(photoFile.absolutePath),
                            arrayOf("image/jpeg"),
                            null
                        )
                    } catch (e: IOException) {
                        ToastUtils.showShort(this@MainActivity,getString(R.string.fail_photo))
                    }
                }else{
                    ToastUtils.showShort(this@MainActivity,getString(R.string.fail_photo))
                }
            }
        }
    }

    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private fun hasCameraPermission(): Boolean {
        val permission = Manifest.permission.CAMERA
        val granted = PackageManager.PERMISSION_GRANTED
        return ContextCompat.checkSelfPermission(this, permission) == granted
    }

    private fun requestCameraPermission() {
        val permission = Manifest.permission.CAMERA
        ActivityCompat.requestPermissions(this, arrayOf(permission), CAMERA_PERMISSION_REQUEST_CODE)
    }

    private var imageProcessor: GPUImageFilter? = null
    private fun applyFilter(filter: GPUImageFilter?) {
        imageProcessor = filter
        gpuImage.setFilter(imageProcessor)
        gpuImage.requestRender()
    }

    private val WRITE_EXTERNAL_STORAGE_REQUEST = 123

    // Processing permission request results
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The user granted camera permission
                    startCamera()
                } else {
                    // The user denied the camera permission. You can explain to the user why this permission is needed.
                    // You can also disable related features or provide alternatives
                    Toast.makeText(this, getString(R.string.permission_text), Toast.LENGTH_SHORT)
                        .show()
                }
            }
            WRITE_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User granted permission
                    savePhoto()
                } else {
                    // The user denied the permission request, you can handle it here
                    ToastUtils.showShort(this@MainActivity,getString(R.string.please))
                }
                return
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider.unbindAll()
    }

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraSelector: CameraSelector
    private lateinit var camera: Camera
    private fun startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        // Initialize CameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Start camera preview
            bindCameraUseCases(cameraProvider, cameraSelector)
        }, ContextCompat.getMainExecutor(this))

    }

    var bitmap: Bitmap? = null
    private fun bindCameraUseCases(
        cameraProvider: ProcessCameraProvider,
        cameraSelector: CameraSelector
    ) {
        cameraProvider.unbindAll()
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

        // Setting up an image analysis use case
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageCapture = ImageCapture.Builder()
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            if (lvjing > 0) {
                binding.previewView.post {
                    gpuImage.setFilter(imageProcessor)
                    gpuImage.setImage(binding.previewView.bitmap)
                    gpuImage.requestRender()
                }
            }
            imageProxy.image?.close()
            imageProxy.close()
        }
        camera = cameraProvider.bindToLifecycle(
            this,
            cameraSelector,
            preview,
            imageAnalysis,
            imageCapture
        )
    }

    private var isSuo = false
    private var isSdt = false
    private var bitmap_dong:Bitmap?=null

    override fun initListener() {
        binding.progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Calculate the scaling factor
                camera.cameraControl.setLinearZoom(progress / 100.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        binding.setting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        binding.light.setOnClickListener {
            binding.light.visibility = View.GONE
            binding.setting.visibility = View.GONE
            binding.lvjing.visibility = View.GONE
            binding.pai.visibility = View.GONE
            binding.suo.visibility = View.GONE
            binding.progress.visibility = View.GONE
            binding.imageView.visibility = View.GONE
            binding.sdt.visibility = View.GONE
            val dialog = LightDialog(this, DataManager.light, object : LightDialog.OnLightCallback {
                override fun close() {
                    binding.light.visibility = View.VISIBLE
                    binding.setting.visibility = View.VISIBLE
                    binding.lvjing.visibility = View.VISIBLE
                    binding.pai.visibility = View.VISIBLE
                    binding.suo.visibility = View.VISIBLE
                    binding.progress.visibility = View.VISIBLE
                    binding.imageView.visibility = View.VISIBLE
                    binding.sdt.visibility = View.VISIBLE
                }

                override fun onProgressNumber(progress: Float) {
                    setExposureCompensation(progress.toInt())
                    DataManager.light = progress.toInt()
                }
            })
            dialog.show()
        }
        binding.pai.setOnClickListener {
            takePhoto()
        }
        binding.lvjing.setOnClickListener {
            binding.lvjing.visibility = View.GONE
            binding.pai.visibility = View.GONE
            binding.suo.visibility = View.GONE
            binding.setting.visibility = View.GONE
            binding.layoutLvjing.visibility = View.VISIBLE
            binding.light.visibility = View.GONE
            binding.sdt.visibility = View.GONE
        }
        binding.cha.setOnClickListener {
            binding.lvjing.visibility = View.VISIBLE
            binding.pai.visibility = View.VISIBLE
            binding.suo.visibility = View.VISIBLE
            binding.setting.visibility = View.VISIBLE
            binding.layoutLvjing.visibility = View.GONE
            binding.light.visibility = View.VISIBLE
            binding.sdt.visibility = View.VISIBLE
        }
        binding.suo.setOnClickListener {
            if (isSuo) {
                binding.suo.setImageResource(R.mipmap.aasuo)
                binding.light.visibility = View.VISIBLE
                binding.setting.visibility = View.VISIBLE
                binding.lvjing.visibility = View.VISIBLE
                binding.progress.visibility = View.VISIBLE
                binding.imageView.visibility = View.VISIBLE
                binding.sdt.visibility = View.VISIBLE
                binding.dong.visibility = View.GONE
                isSuo = false
                startCamera()
            } else {
                binding.suo.setImageResource(R.mipmap.aasuoliang)
                binding.light.visibility = View.GONE
                binding.setting.visibility = View.GONE
                binding.lvjing.visibility = View.GONE
                binding.progress.visibility = View.GONE
                binding.imageView.visibility = View.GONE
                binding.sdt.visibility = View.GONE
                isSuo = true
                bitmap_dong = binding.previewView.bitmap
                binding.dong.visibility = View.VISIBLE
                binding.dong.setImageBitmap(bitmap_dong)
                cameraProvider.unbindAll()
            }
        }
        binding.sdt.setOnClickListener {
            if (isSdt) {
                binding.sdt.setImageResource(R.mipmap.aasdt)
                isSdt = false
                camera.cameraControl.enableTorch(false)
            } else {
                binding.sdt.setImageResource(R.mipmap.aasdtw)
                isSdt = true
                camera.cameraControl.enableTorch(true)
            }
        }
        binding.lvjing1.setOnClickListener {
            binding.glSurfaceView.visibility = View.GONE
            lvjing = 0
            applyFilter(GPUImageFilter())
        }
        binding.lvjing2.setOnClickListener {
            binding.glSurfaceView.visibility = View.VISIBLE
            lvjing = 1
            applyFilter(GPUImageGrayscaleFilter())
        }
        binding.lvjing3.setOnClickListener {
            binding.glSurfaceView.visibility = View.VISIBLE
            lvjing = 2
            applyFilter(GPUImageSketchFilter())
        }
        binding.lvjing4.setOnClickListener {
            binding.glSurfaceView.visibility = View.VISIBLE
            lvjing = 3
            applyFilter(GPUImageToonFilter())
        }
    }

    private fun setExposureCompensation(value: Int) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraControl = camera.cameraControl

            cameraControl.setExposureCompensationIndex(value)
        }, ContextCompat.getMainExecutor(this))
    }

    override fun initData() {

    }

}