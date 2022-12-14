package com.example.wildrunning.activity

import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wildrunning.R
import com.example.wildrunning.activity.LoginActivity.Companion.userEmail
import com.example.wildrunning.activity.MainActivity.Companion.lastImage
import com.example.wildrunning.activity.MainActivity.Companion.photoNumber
import com.example.wildrunning.constants.Constants.CAMERA_PERMISSIONS
import com.example.wildrunning.constants.Constants.CAMERA_REQUEST_CODE
import com.example.wildrunning.constants.Constants.RATIO_16_9
import com.example.wildrunning.constants.Constants.RATIO_4_3
import com.example.wildrunning.databinding.ActivityCameraBinding
import com.example.wildrunning.utils.StorageClient
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraActivity : AppCompatActivity() {

    lateinit var binding: ActivityCameraBinding

    private var preview: Preview? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var runDate: String
    private lateinit var runStartTime: String
    private var fileName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        runDate = bundle?.getString("runDate").toString()
        runStartTime = bundle?.getString("runStartTime").toString()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) startCamera() else
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSIONS, CAMERA_REQUEST_CODE)
    }

    private fun bindCamera() {
        val metrics = DisplayMetrics().also {
            binding.viewFinder.display.getRealMetrics(it)
        }
        val screenAspectRatio = createAspectRatio(metrics.widthPixels, metrics.heightPixels)
        val rotation = binding.viewFinder.display.rotation

        val cameraProvider =
            cameraProvider ?: throw IllegalStateException(getString(R.string.failed_init_cam))

        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        cameraProvider.unbindAll()

        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (e: Exception) {
            Log.e(this.javaClass.name, CameraActivity::bindCamera.name, e)
        }
    }

    private fun startCamera() {
        val cameraProviderFinally = ProcessCameraProvider.getInstance(this)
        cameraProviderFinally.addListener(
            {
                cameraProvider = cameraProviderFinally.get()
                lensFacing = when {
                    hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                    hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                    else -> throw IllegalStateException(getString(R.string.no_cam_advice))
                }

                manageSwitchButton()

                bindCamera()

            },
            ContextCompat.getMainExecutor(this),
        )
    }

    private fun manageSwitchButton() {
        try {
            binding.cameraSwitchButton.isEnabled = hasBackCamera() && hasFrontCamera()
        } catch (e: CameraInfoUnavailableException) {
            binding.cameraSwitchButton.isEnabled = false
        }
    }

    private fun hasFrontCamera() =
        cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false

    private fun hasBackCamera() =
        cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false

    private fun createAspectRatio(widthPixels: Int, heightPixels: Int): Int {
        val previewRatio =
            max(widthPixels, heightPixels).toDouble() / min(widthPixels, heightPixels)
        return if (abs(previewRatio - RATIO_4_3) >= abs(previewRatio - RATIO_16_9)) {
            AspectRatio.RATIO_4_3
        } else AspectRatio.RATIO_16_9
    }

    private fun allPermissionsGranted() = CAMERA_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "wildRunning").apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (allPermissionsGranted()) startCamera()
            else {
                Toast.makeText(this, getString(R.string.cam_perm_req), Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    fun onCameraCaptureButtonClicked(view: View) {
        fileName = (getString(R.string.app_name) + userEmail + runDate)
            .replace("/", "")
            .replace(":", "")

        val photoFile = File(outputDirectory, "$fileName.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val saveUri = Uri.fromFile(photoFile)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setGalleryThumbnail(saveUri)
                    }

                    val mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(saveUri.toFile().extension)
                    MediaScannerConnection.scanFile(
                        baseContext,
                        arrayOf(saveUri.toFile().absolutePath),
                        arrayOf(mimeType),
                        null
                    )

                    Snackbar.make(
                        binding.clMain,
                        getString(R.string.image_saved_ok),
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction("OK") {
                            binding.clMain.setBackgroundColor(Color.CYAN)
                        }.show()

                    uploadFile(photoFile)

                }

                override fun onError(exception: ImageCaptureException) {
                    Snackbar.make(
                        binding.clMain,
                        getString(R.string.image_save_error),
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction("OK") {
                            binding.clMain.setBackgroundColor(Color.CYAN)
                        }.show()
                }

            }
        )
    }

    private fun onUploadedFileCallback(dirName: String, file: File) {
        lastImage = "images/${userEmail}/$dirName/$fileName"
        photoNumber++

        val mFile = File(file.absolutePath)
        mFile.delete()

        Snackbar.make(
            binding.clMain,
            getString(R.string.image_uploaded_ok),
            Snackbar.LENGTH_SHORT
        )
            .setAction("OK") {
                binding.clMain.setBackgroundColor(Color.CYAN)
            }.show()
    }

    private fun failureCallback(e: Exception) {
        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
    }

    private fun uploadFile(file: File) = StorageClient.uploadFile(
        runDate,
        runStartTime,
        file,
        ::onUploadedFileCallback,
        ::failureCallback
    )

    private fun setGalleryThumbnail(saveUri: Uri) {
        val thumbnail = binding.photoViewButton
        thumbnail.post {
            Glide.with(thumbnail)
                .load(saveUri)
                .apply(RequestOptions.circleCropTransform())
                .into(thumbnail)
        }
    }

    fun onCameraSwitchButtonClicked(view: View) {
        lensFacing =
            if (CameraSelector.LENS_FACING_FRONT == lensFacing) CameraSelector.LENS_FACING_BACK
            else CameraSelector.LENS_FACING_FRONT
        bindCamera()


    }
}