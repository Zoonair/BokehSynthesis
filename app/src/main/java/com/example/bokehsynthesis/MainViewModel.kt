package com.example.bokehsynthesis

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val cameraManager = application.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private val _availableSensors = MutableStateFlow<List<CameraSensor>>(emptyList())
    val availableSensors: StateFlow<List<CameraSensor>> = _availableSensors

    fun fetchAllCameraSensors() {
        val sensorList = mutableListOf<CameraSensor>()

        try {
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)

                val facingInt = characteristics.get(CameraCharacteristics.LENS_FACING)
                val facingString = when (facingInt) {
                    CameraCharacteristics.LENS_FACING_BACK -> "Back Camera"
                    CameraCharacteristics.LENS_FACING_FRONT -> "Front Camera"
                    CameraCharacteristics.LENS_FACING_EXTERNAL -> "External Camera"
                    else -> "Unknown"
                }

                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                val minimumFocus = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)

                sensorList.add(
                    CameraSensor(
                        id = cameraId,
                        facing = facingString,
                        isFlashSupported = hasFlash,
                        minimumFocusDistance = minimumFocus
                    )
                )
            }
            _availableSensors.value = sensorList

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var cameraDevice: CameraDevice? = null

    @SuppressLint("MissingPermission")
    fun initCamera() {
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return

            val stateCallback = object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                }
                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                    cameraDevice = null
                }
                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                    cameraDevice = null
                }
            }

            cameraManager.openCamera(cameraId, stateCallback, Handler(Looper.getMainLooper()))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}