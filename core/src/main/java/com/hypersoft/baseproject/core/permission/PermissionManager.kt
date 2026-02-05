package com.hypersoft.baseproject.core.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hypersoft.baseproject.core.constants.Constants.TAG
import com.hypersoft.baseproject.core.extensions.launchWhenStarted
import com.hypersoft.baseproject.core.permission.enums.MediaPermission
import com.hypersoft.baseproject.core.permission.model.PermissionRequest
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

    private val prefs: SharedPreferences? by lazy { fragment.context?.getSharedPreferences("permission_prefs", Context.MODE_PRIVATE) }

    private fun getRequestCount(type: MediaPermission): Int = prefs?.getInt("request_count_${type.name}", 0) ?: 0
    private fun incrementRequestCount(type: MediaPermission) = prefs?.edit { putInt("request_count_${type.name}", getRequestCount(type) + 1) }
    private fun markAsked(type: MediaPermission) = prefs?.edit { putBoolean("asked_${type.name}", true) }

    private var currentRequest: PermissionRequest? = null

    /**
     * REGISTERED EARLY → SAFE → no more IllegalStateException
     */
    private val permissionLauncher = fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
        handlePermissionResult()
    }

    private val settingsLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        handleSettingsResult()
    }

    /**
     * Request permission with callback. Callback is only invoked after launcher responses.
     * Callback is guaranteed to be invoked even if exceptions occur.
     */
    fun requestPermission(type: MediaPermission, callback: (PermissionResult) -> Unit) {
        try {
            if (!isFragmentValid()) {
                safeCallback(callback, PermissionResult.Denied)
                return
            }

            fragment.launchWhenStarted {
                try {
                    val perms = permissionStringsFor(type)

                    // Check if already granted (full or limited)
                    val permissionStatus = checkPermissionStatus(type, perms)
                    when (permissionStatus) {
                        PermissionResult.GrantedFull -> {
                            safeCallback(callback, PermissionResult.GrantedFull)
                            return@launchWhenStarted
                        }

                        PermissionResult.GrantedLimited -> {
                            safeCallback(callback, PermissionResult.GrantedLimited)
                            return@launchWhenStarted
                        }

                        else -> { /* Continue to request flow */
                        }
                    }

                    val requestCount = getRequestCount(type)
                    val showRationale = try {
                        perms.any {
                            isFragmentValid() && ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), it)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error checking rationale", e)
                        false
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
                                    safeCallback(callback, PermissionResult.Denied)
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
                                    safeCallback(callback, PermissionResult.Denied)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in requestPermission", e)
                    safeCallback(callback, PermissionResult.Denied)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting requestPermission", e)
            safeCallback(callback, PermissionResult.Denied)
        }
    }

    fun requestFullPermission(type: MediaPermission, callback: (PermissionResult) -> Unit) {
        try {
            if (!isFragmentValid()) {
                safeCallback(callback, PermissionResult.Denied)
                return
            }

            fragment.launchWhenStarted {
                try {
                    val perms = permissionStringsFor(type)

                    // Check if already granted (full or limited)
                    val permissionStatus = checkPermissionStatus(type, perms)
                    when (permissionStatus) {
                        PermissionResult.GrantedFull -> {
                            safeCallback(callback, PermissionResult.GrantedFull)
                            return@launchWhenStarted
                        }

                        else -> { /* Continue to request flow */
                        }
                    }

                    val requestCount = getRequestCount(type)
                    val showRationale = try {
                        perms.any {
                            isFragmentValid() && ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), it)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error checking rationale", e)
                        false
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
                                    safeCallback(callback, PermissionResult.Denied)
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
                                    safeCallback(callback, PermissionResult.Denied)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in requestPermission", e)
                    safeCallback(callback, PermissionResult.Denied)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting requestPermission", e)
            safeCallback(callback, PermissionResult.Denied)
        }
    }

    /**
     * Suspend version of requestPermission for use in coroutines.
     */
    suspend fun requestPermissionSuspend(type: MediaPermission): PermissionResult {
        return try {
            suspendCancellableCoroutine { continuation ->
                requestPermission(type) { result ->
                    try {
                        continuation.resume(result)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error resuming continuation", e)
                        // Continuation might be cancelled, try to resume anyway
                        try {
                            continuation.resume(PermissionResult.Denied)
                        } catch (e2: Exception) {
                            Log.e(TAG, "Error resuming continuation after exception", e2)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in requestPermissionSuspend", e)
            PermissionResult.Denied
        }
    }

    /**
     *  Check if already granted (full or limited)
     */
    fun checkPermissionGranted(mediaPermission: MediaPermission, callback: (PermissionResult) -> Unit) {
        val perms = permissionStringsFor(mediaPermission)
        val result = checkPermissionStatus(mediaPermission, perms)
        callback.invoke(result)
    }

    /**
     * Check if permission is granted.
     */
    fun isPermissionGranted(type: MediaPermission): Boolean {
        return try {
            val ctx = getContextSafely() ?: return false
            when (type) {
                MediaPermission.IMAGES_VIDEOS -> {
                    val hasFullImages = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                    val hasFullVideos = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                    hasFullImages && hasFullVideos
                }

                MediaPermission.AUDIOS -> {
                    // Audio doesn't have limited permission on Android 14+
                    true
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "PermissionManager: Error checking limited permission", ex)
            false
        }
    }

    /**
     * Check if limited permission is granted (Android 14+ only).
     * Use this to show a banner/snackBar to user to grant full permission.
     * Returns true if user has limited access (READ_MEDIA_VISUAL_USER_SELECTED)
     * but not full access (both READ_MEDIA_IMAGES and READ_MEDIA_VIDEO).
     */
    fun isLimitedPermissionGranted(type: MediaPermission): Boolean {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return false

            val ctx = getContextSafely() ?: return false
            when (type) {
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
        } catch (ex: Exception) {
            Log.e(TAG, "PermissionManager: Error checking limited permission", ex)
            false
        }
    }

    /**
     * Navigate to settings screen where user can grant full permission.
     * Call this when user clicks "Grant" on the snackBar/banner.
     * Callback will be invoked when user returns from settings with the permission status.
     */
    fun openSettingsForPermission(type: MediaPermission, callback: (PermissionResult) -> Unit) {
        try {
            if (!isFragmentValid()) {
                safeCallback(callback, PermissionResult.Denied)
                return
            }

            val ctx = getContextSafely()
            if (ctx == null) {
                safeCallback(callback, PermissionResult.Denied)
                return
            }

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", ctx.packageName, null)
            }
            currentRequest = PermissionRequest(type, callback)
            settingsLauncher.launch(intent)
        } catch (ex: Exception) {
            Log.e(TAG, "PermissionManager: Error opening settings", ex)
            safeCallback(callback, PermissionResult.Denied)
        }
    }

    // permission result handlers
    private fun askSystem(permissions: Array<String>, type: MediaPermission, callback: (PermissionResult) -> Unit) {
        try {
            if (!isFragmentValid()) {
                safeCallback(callback, PermissionResult.Denied)
                return
            }

            currentRequest = PermissionRequest(type, callback)
            permissionLauncher.launch(permissions)
        } catch (ex: Exception) {
            Log.e(TAG, "PermissionManager: Error asking system for permission", ex)
            currentRequest = null
            safeCallback(callback, PermissionResult.Denied)
        }
    }

    private fun handlePermissionResult() {
        val request = currentRequest ?: return
        currentRequest = null // Clear request to prevent memory leak

        if (!isFragmentValid()) {
            safeCallback(request.callback, PermissionResult.Denied)
            return
        }

        val ctx = getContextSafely()
        if (ctx == null) {
            safeCallback(request.callback, PermissionResult.Denied)
            return
        }

        val perms = permissionStringsFor(request.type)

        val isLimited = isLimitedPermissionGranted(request.type)

        if (isLimited) {
            safeCallback(request.callback, PermissionResult.GrantedLimited)
            return
        }

        // Check if the user denied everything
        val fullyGranted = perms.all { perm ->
            ContextCompat.checkSelfPermission(ctx, perm) == PackageManager.PERMISSION_GRANTED
        }

        when (fullyGranted) {
            true -> safeCallback(request.callback, PermissionResult.GrantedFull)
            false -> safeCallback(request.callback, PermissionResult.Denied)
        }
    }

    private fun handleSettingsResult() {
        val request = currentRequest ?: return
        currentRequest = null // Clear request to prevent memory leak

        try {
            if (!isFragmentValid()) {
                safeCallback(request.callback, PermissionResult.Denied)
                return
            }

            val ctx = getContextSafely()
            if (ctx == null) {
                safeCallback(request.callback, PermissionResult.Denied)
                return
            }

            val perms = permissionStringsFor(request.type)

            // Check permission status after returning from settings
            val permissionStatus = checkPermissionStatus(request.type, perms)
            safeCallback(request.callback, permissionStatus)
        } catch (ex: Exception) {
            Log.e(TAG, "PermissionManager: Error handling settings result", ex)
            safeCallback(request.callback, PermissionResult.Denied)
        }
    }

    private fun checkPermissionStatus(type: MediaPermission, perms: Array<String>): PermissionResult {
        return try {
            val ctx = getContextSafely() ?: return PermissionResult.Denied

            // Check if all requested permissions are granted
            val allGranted = perms.all {
                try {
                    ContextCompat.checkSelfPermission(ctx, it) == PackageManager.PERMISSION_GRANTED
                } catch (ex: Exception) {
                    Log.e(TAG, "PermissionManager: Error checking permission status: $it", ex)
                    false
                }
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
                try {
                    val hasLimited = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED
                    if (hasLimited) {
                        return PermissionResult.GrantedLimited
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error checking limited permission", e)
                }
            }

            PermissionResult.Denied
        } catch (e: Exception) {
            Log.e(TAG, "Error in checkPermissionStatus", e)
            PermissionResult.Denied
        }
    }

    private fun showRationaleDialog(type: MediaPermission, onResult: (Boolean) -> Unit) {
        if (!isFragmentValid()) {
            onResult(false)
            return
        }

        val ctx = getContextSafely()
        if (ctx == null) {
            onResult(false)
            return
        }

        val msg = when (type) {
            MediaPermission.IMAGES_VIDEOS -> "We need access to your photos and videos to display them in your gallery. This allows you to view, organize, and manage all your media files in one place."
            MediaPermission.AUDIOS -> "We need access to your audio files to play and manage your music collection. This allows you to browse and enjoy all your audio files."
        }

        val dialog = MaterialAlertDialogBuilder(ctx)
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
            .create()

        dialog.setOnShowListener {
            dialog.findViewById<android.widget.TextView>(android.R.id.message)?.setTextColor(android.graphics.Color.WHITE)
            dialog.findViewById<android.widget.TextView>(androidx.appcompat.R.id.alertTitle)?.setTextColor(android.graphics.Color.WHITE)
        }
        dialog.show()
    }

    private fun showSettingsDialog(onResult: (Boolean) -> Unit) {
        if (!isFragmentValid()) {
            onResult(false)
            return
        }

        val ctx = getContextSafely()
        if (ctx == null) {
            onResult(false)
            return
        }

        val dialog = MaterialAlertDialogBuilder(ctx)
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
            .create()

        dialog.setOnShowListener {
            dialog.findViewById<android.widget.TextView>(android.R.id.message)?.setTextColor(android.graphics.Color.WHITE)
            dialog.findViewById<android.widget.TextView>(androidx.appcompat.R.id.alertTitle)?.setTextColor(android.graphics.Color.WHITE)
        }
        dialog.show()
    }

    private fun openSettings() {
        if (!isFragmentValid()) {
            return
        }

        val ctx = getContextSafely() ?: return

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", ctx.packageName, null)
        }
        settingsLauncher.launch(intent)
    }

    // permissions logic
    /**
     * Returns permission strings based on type and Android version.
     * - IMAGES_VIDEOS: Requests both READ_MEDIA_IMAGES and READ_MEDIA_VIDEO together (Android 13+)
     * - AUDIOS: Requests READ_MEDIA_AUDIO (Android 13+) or READ_EXTERNAL_STORAGE (older)
     */
    private fun permissionStringsFor(type: MediaPermission): Array<String> {
        return try {
            when (type) {
                MediaPermission.IMAGES_VIDEOS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
                } else {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                MediaPermission.AUDIOS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
                } else {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "PermissionManager: Error getting permission strings", ex)
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    // endregion

    // region: safety helpers
    /**
     * Safely checks if fragment is valid and attached.
     */
    private fun isFragmentValid(): Boolean {
        return try {
            fragment.isAdded && fragment.context != null && !fragment.isDetached && !fragment.isRemoving
        } catch (e: Exception) {
            Log.e(TAG, "Error checking fragment validity", e)
            false
        }
    }

    /**
     * Safely gets context without throwing exceptions.
     */
    private fun getContextSafely(): Context? {
        return try {
            if (isFragmentValid()) {
                fragment.context
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting context", e)
            null
        }
    }

    /**
     * Safely invokes callback, catching any exceptions.
     */
    private fun safeCallback(callback: (PermissionResult) -> Unit, result: PermissionResult) {
        try {
            callback(result)
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking callback", e)
            // Callback failed, but we tried - user won't be stuck
        }
    }
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
- Use isLimitedPermissionGranted() to show banner/snackBar for Android 14+ users with limited access.
- Use openSettingsForPermission() when user clicks "Grant" on the banner to navigate to settings.
- IMAGES_VIDEOS requests both READ_MEDIA_IMAGES and READ_MEDIA_VIDEO together on Android 13+.
- AUDIOS requests READ_MEDIA_AUDIO separately on Android 13+.
*/