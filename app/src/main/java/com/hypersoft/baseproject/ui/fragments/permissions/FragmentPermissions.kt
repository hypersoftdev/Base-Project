package com.hypersoft.baseproject.ui.fragments.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.commons.listeners.RapidSafeListener.setOnRapidClickSafeListener
import com.hypersoft.baseproject.databinding.FragmentPermissionsBinding
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentPermissions : BaseFragment<FragmentPermissionsBinding>(R.layout.fragment_permissions) {

    private val REQUIRED_PERMISSIONS =
        mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        }.toTypedArray()

    private var settingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result?.let {
            if (it.resultCode == Activity.RESULT_OK){
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var isGranted = true
        REQUIRED_PERMISSIONS.forEach {
            if (permissions[it] != true) {
                isGranted = false
                return@forEach
            }
        }
        if (isGranted){
            diComponent.sharedPreferenceUtils.isFirstTimeAskingPermission = true
            onPermissionGranted()
        }else{
            onPermissionNotGranted()
        }
    }

    override fun onViewCreatedOneTime() {
        if (isPermissionGranted()){
            performOperations()
        }else{
            binding.tvMessage.text =getString(R.string.no_permission_is_granted)
        }

        binding.btnPermission.setOnRapidClickSafeListener{
            if (isPermissionGranted()){
                performOperations()
            }else{
                askForPermission()
            }
        }

    }

    override fun onViewCreatedEverytime() {}

    private fun performOperations(){
        binding.tvMessage.text =getString(R.string.all_permission_granted)
        showToast(getString(R.string.all_permission_granted))
    }

    private fun isPermissionGranted(): Boolean {
        var isGranted = true
        REQUIRED_PERMISSIONS.forEach {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isGranted = false
                return@forEach
            }

        }
        return isGranted
    }

    private fun askForPermission() {
        var isRationale = false
        REQUIRED_PERMISSIONS.forEach {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),it)) {
                isRationale = true
                return@forEach
            }
        }

        if (isRationale){
            diComponent.sharedPreferenceUtils.isFirstTimeAskingPermission = false
            showPermissionDialog()
        }else{
            if (diComponent.sharedPreferenceUtils.isFirstTimeAskingPermission){
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
            }else{
                showSettingDialog()
            }
        }
    }

    private fun onPermissionGranted() {
        binding.tvMessage.text =getString(R.string.all_permission_granted)
        showToast(getString(R.string.all_permission_granted))
    }

    private fun onPermissionNotGranted() {
        binding.tvMessage.text =getString(R.string.please_grant_permission)
        showToast(getString(R.string.please_grant_permission))
    }


    private fun showPermissionDialog() {
        val builder = context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getResString(R.string.permission_required))
                .setMessage(getResString(R.string.please_grant_permission))
                .setCancelable(false)
                .setPositiveButton(getResString(R.string.enable)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
                }
                .setNegativeButton(getResString(R.string.cancel)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
        }

        if (!(context as Activity).isFinishing)
            builder?.show()
    }

    private fun showSettingDialog() {
        val builder = context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getResString(R.string.permission_required))
                .setMessage(getResString(R.string.allow_permission_settings))
                .setCancelable(false)
                .setPositiveButton(getResString(R.string.setting)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    openSettingPage()
                }
                .setNegativeButton(getResString(R.string.cancel)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
        }

        if (!(context as Activity).isFinishing)
            builder?.show()
    }

    private fun openSettingPage() {
        context?.let {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", it.packageName, null)
            intent.data = uri
            settingLauncher.launch(intent)
        }
    }
}