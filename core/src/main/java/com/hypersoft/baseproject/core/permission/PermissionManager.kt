package com.hypersoft.baseproject.core.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hypersoft.baseproject.core.extensions.launchWhenStarted
import com.hypersoft.baseproject.core.permission.enums.MediaPermission
import com.hypersoft.baseproject.core.permission.result.PermissionResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * One-line API for Fragments to request media permissions in a MVI-friendly way.
 *
 * Usage (one-liner):
 * permissionManager.requestPermission(MediaPermission.IMAGES_VIDEOS) { result ->
 *     when(result) {
 *         PermissionResult.GrantedFull -> { /* handle full access */ }
 *         PermissionResult.GrantedLimited -> { /* handle limited access (Android 14+) */ }
 *         PermissionResult.Denied -> { /* show toast */ }
 *     }
 * }
 *
 * Or from a coroutine (suspend):
 * val result = permissionManager.requestPermissionSuspend(MediaPermission.IMAGES_VIDEOS)
 */
class PermissionManager(private val fragment: Fragment) {

    // region: prefs
    private val prefs: SharedPreferences by lazy {
        fragment.requireContext().getSharedPreferences("perm_prefs", Context.MODE_PRIVATE)
    }

    private fun getRequestCount(type: MediaPermission): Int =
        prefs.getInt("request_count_${type.name}", 0)

    private fun incrementRequestCount(type: MediaPermission) {
        prefs.edit { putInt("request_count_${type.name}", getRequestCount(type) + 1) }
    }

    private fun markAsked(type: MediaPermission) {
        prefs.edit { putBoolean("asked_${type.name}", true) }
    }
    // endregion

    // region: launchers
    private data class PermissionRequest(
        val type: MediaPermission,
        val callback: (PermissionResult) -> Unit
    )

    private var currentRequest: PermissionRequest? = null

    /**
     * REGISTERED EARLY → SAFE → no more IllegalStateException
     */
    private val permissionLauncher = fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
        handlePermissionResult(resultMap)
    }

    private val settingsLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        handleSettingsResult()
    }
    // endregion

    // region: public API
    /**
     * Request permission with callback. Callback is only invoked after launcher responses.
     */
    fun requestPermission(type: MediaPermission, callback: (PermissionResult) -> Unit) {
        fragment.launchWhenStarted {
            val perms = permissionStringsFor(type)

            // Check if already granted (full or limited)
            val permissionStatus = checkPermissionStatus(type, perms)
            when (permissionStatus) {
                PermissionResult.GrantedFull -> {
                    callback(PermissionResult.GrantedFull)
                    return@launchWhenStarted
                }

                PermissionResult.GrantedLimited -> {
                    callback(PermissionResult.GrantedLimited)
                    return@launchWhenStarted
                }

                else -> { /* Continue to request flow */
                }
            }

            val requestCount = getRequestCount(type)
            val showRationale = perms.any {
                ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), it)
            }

            when {
                // First time → ask system directly (Android built-in dialog)
                requestCount == 0 && !showRationale -> {
                    incrementRequestCount(type)
                    markAsked(type)
                    askSystem(perms, type, callback)
                }

                // Second time → show rationale dialog (Material dialog explaining why)
                requestCount == 1 || showRationale -> {
                    incrementRequestCount(type)
                    showRationaleDialog(type) { ok ->
                        if (ok) {
                            askSystem(perms, type, callback)
                        } else {
                            // User cancelled rationale dialog → denied
                            callback(PermissionResult.Denied)
                        }
                    }
                }

                // Third time → no rationale, no built-in popup → show settings dialog
                else -> {
                    incrementRequestCount(type)
                    showSettingsDialog { open ->
                        if (open) {
                            currentRequest = PermissionRequest(type, callback)
                            openSettings()
                        } else {
                            // User cancelled settings dialog → denied
                            callback(PermissionResult.Denied)
                        }
                    }
                }
            }
        }
    }

    /**
     * Suspend version of requestPermission for use in coroutines.
     */
    suspend fun requestPermissionSuspend(type: MediaPermission): PermissionResult {
        return suspendCancellableCoroutine { continuation ->
            requestPermission(type) { result ->
                continuation.resume(result)
            }
        }
    }

    /**
     * Check if limited permission is granted (Android 14+ only).
     * Use this to show a banner/snackbar to user to grant full permission.
     * Returns true if user has limited access (READ_MEDIA_VISUAL_USER_SELECTED)
     * but not full access (both READ_MEDIA_IMAGES and READ_MEDIA_VIDEO).
     */
    fun isLimitedPermissionGranted(type: MediaPermission): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return false

        val ctx = fragment.requireContext()
        return when (type) {
            MediaPermission.IMAGES_VIDEOS -> {
                val hasLimited = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED
                val hasFullImages = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                val hasFullVideos = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                // Limited if has limited permission but not both full permissions
                hasLimited && (!hasFullImages || !hasFullVideos)
            }

            MediaPermission.AUDIOS -> {
                // Audio doesn't have limited permission on Android 14+
                false
            }
        }
    }

    /**
     * Navigate to settings screen where user can grant full permission.
     * Call this when user clicks "Grant" on the snackbar/banner.
     */
    fun openSettingsForPermission() {
        val ctx = fragment.requireContext()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", ctx.packageName, null)
        }
        fragment.startActivity(intent)
    }
    // endregion

    // region: permission result handlers
    private fun askSystem(permissions: Array<String>, type: MediaPermission, callback: (PermissionResult) -> Unit) {
        currentRequest = PermissionRequest(type, callback)
        permissionLauncher.launch(permissions)
    }

    private fun handlePermissionResult(result: Map<String, Boolean>) {
        val request = currentRequest ?: return
        currentRequest = null // Clear request to prevent memory leak

        val ctx = fragment.requireContext()
        val perms = permissionStringsFor(request.type)

        // Check if all permissions granted
        val allGranted = perms.all { perm ->
            result[perm] == true && ContextCompat.checkSelfPermission(ctx, perm) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            // Check for limited permission on Android 14+
            val isLimited = isLimitedPermissionGranted(request.type)
            request.callback(
                if (isLimited) PermissionResult.GrantedLimited
                else PermissionResult.GrantedFull
            )
        } else {
            // Permission denied → callback for toast
            request.callback(PermissionResult.Denied)
        }
    }

    private fun handleSettingsResult() {
        val request = currentRequest ?: return
        currentRequest = null // Clear request to prevent memory leak

        val ctx = fragment.requireContext()
        val perms = permissionStringsFor(request.type)

        // Check permission status after returning from settings
        val permissionStatus = checkPermissionStatus(request.type, perms)
        request.callback(permissionStatus)
    }

    private fun checkPermissionStatus(type: MediaPermission, perms: Array<String>): PermissionResult {
        val ctx = fragment.requireContext()

        // Check if all requested permissions are granted
        val allGranted = perms.all {
            ContextCompat.checkSelfPermission(ctx, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            // On Android 14+, check if it's limited permission
            // Limited means user has READ_MEDIA_VISUAL_USER_SELECTED but not full permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && type == MediaPermission.IMAGES_VIDEOS) {
                val isLimited = isLimitedPermissionGranted(type)
                return if (isLimited) PermissionResult.GrantedLimited
                else PermissionResult.GrantedFull
            }
            return PermissionResult.GrantedFull
        }

        // On Android 14+, check if user has limited permission even if full permissions not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && type == MediaPermission.IMAGES_VIDEOS) {
            val hasLimited = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED
            if (hasLimited) {
                return PermissionResult.GrantedLimited
            }
        }

        return PermissionResult.Denied
    }
    // endregion

    // region: dialogs
    private fun showRationaleDialog(type: MediaPermission, onResult: (Boolean) -> Unit) {
        val msg = when (type) {
            MediaPermission.IMAGES_VIDEOS -> "We need access to your photos and videos to display them in your gallery. This allows you to view, organize, and manage all your media files in one place."
            MediaPermission.AUDIOS -> "We need access to your audio files to play and manage your music collection. This allows you to browse and enjoy all your audio files."
        }

        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle("Permission Required")
            .setMessage(msg)
            .setPositiveButton("Allow") { d, _ ->
                d.dismiss()
                onResult(true)
            }
            .setNegativeButton("Deny") { d, _ ->
                d.dismiss()
                onResult(false)
            }
            .setCancelable(false)
            .show()
    }

    private fun showSettingsDialog(onResult: (Boolean) -> Unit) {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle("Permission Needed")
            .setMessage("You've denied permission multiple times. Please grant permission from App Settings to continue using this feature.")
            .setPositiveButton("Open Settings") { d, _ ->
                d.dismiss()
                onResult(true)
            }
            .setNegativeButton("Cancel") { d, _ ->
                d.dismiss()
                onResult(false)
            }
            .setCancelable(false)
            .show()
    }
    // endregion

    // region: settings intent
    private fun openSettings() {
        val ctx = fragment.requireContext()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", ctx.packageName, null)
        }
        settingsLauncher.launch(intent)
    }
    // endregion

    // region: permissions logic
    /**
     * Returns permission strings based on type and Android version.
     * - IMAGES_VIDEOS: Requests both READ_MEDIA_IMAGES and READ_MEDIA_VIDEO together (Android 13+)
     * - AUDIOS: Requests READ_MEDIA_AUDIO (Android 13+) or READ_EXTERNAL_STORAGE (older)
     */
    private fun permissionStringsFor(type: MediaPermission): Array<String> {
        return when (type) {
            MediaPermission.IMAGES_VIDEOS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13+: Request both images and videos together
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
                } else {
                    // Older versions: Use READ_EXTERNAL_STORAGE
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

            MediaPermission.AUDIOS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
                } else {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }
    // endregion
}

// ------------------------------------------------------------------
// Notes & Tips (in the code file)
// ------------------------------------------------------------------

/*
- This helper keeps permission UI out of your ViewModel and Fragment core logic; it provides a small, reusable API.
- We persist request count in SharedPreferences so the flow remains consistent across process death and multiple sessions.
- For Android 14+ partial/selected-photos behavior: We detect limited access using READ_MEDIA_VISUAL_USER_SELECTED.
- Callbacks are only invoked after launcher responses (permission or settings), not during dialog interactions.
- Flow: First time → Android dialog → callback. Second time → Rationale dialog → Android dialog → callback. 
  Third time → Settings dialog → Settings intent → callback.
- Use isLimitedPermissionGranted() to show banner/snackbar for Android 14+ users with limited access.
- Use openSettingsForPermission() when user clicks "Grant" on the banner to navigate to settings.
- IMAGES_VIDEOS requests both READ_MEDIA_IMAGES and READ_MEDIA_VIDEO together on Android 13+.
- AUDIOS requests READ_MEDIA_AUDIO separately on Android 13+.
*/
