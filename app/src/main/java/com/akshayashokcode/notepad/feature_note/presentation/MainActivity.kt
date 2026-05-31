package com.akshayashokcode.notepad.feature_note.presentation

import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.akshayashokcode.notepad.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.akshayashokcode.notepad.feature_note.presentation.add_edit_note.components.AppKeyboardFocusManager
import com.akshayashokcode.notepad.feature_note.presentation.archive.ArchiveScreen
import com.akshayashokcode.notepad.feature_note.presentation.notes.NotesScreen
import com.akshayashokcode.notepad.feature_note.presentation.util.Screen
import com.akshayashokcode.notepad.ui.theme.NotePadTheme
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var updateFlowResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val TAG = "MainActivity"

    override fun onStart() {
        super.onStart()
        checkUpdate()
    }

    override fun onResume() {
        super.onResume()
        if (::appUpdateManager.isInitialized) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                } else {
                    Log.d(TAG, "State of update: ${appUpdateInfo.installStatus()}")
                }
            }
        }
    }

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedText = if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else null

        updateFlowResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            Log.d(TAG, "Update result: ${result.resultCode}")
            if (result.resultCode != RESULT_OK) Log.e(TAG, "app download failed")
        }
        setContent {
            AppKeyboardFocusManager()
            NotePadTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.ime)
                ) {
                    val navController = rememberNavController()
                    LaunchedEffect(Unit) {
                        sharedText?.let { text ->
                            navController.navigate(
                                Screen.AddEditNoteScreen.route +
                                        "?noteColor=-1&sharedContent=${Uri.encode(text)}"
                            )
                        }
                    }
                    NavHost(
                        navController = navController,
                        startDestination = Screen.NotesScreen.route
                    ) {
                        composable(route = Screen.NotesScreen.route) {
                            NotesScreen(navController = navController)
                        }
                        composable(
                            route = Screen.AddEditNoteScreen.route +
                                    "?noteId={noteId}&noteColor={noteColor}&sharedContent={sharedContent}",
                            arguments = listOf(
                                navArgument("noteId") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                },
                                navArgument("noteColor") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                },
                                navArgument("sharedContent") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                    nullable = true
                                }
                            )
                        ) {
                            val color = it.arguments?.getInt("noteColor") ?: -1
                            AddEditNoteScreen(navController = navController, noteColor = color)
                        }
                        composable(route = Screen.ArchiveScreen.route) {
                            ArchiveScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }

    private fun checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateFlowResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            } else {
                Log.d(TAG, "Update not available: ${appUpdateInfo.updateAvailability()}")
            }
        }
        appUpdateManager.registerListener(listener)
    }

    private val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) popupSnackbarForCompleteUpdate()
        Log.d(TAG, "State of update: ${state.installStatus()}")
    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            show()
        }
    }

    override fun onStop() {
        if (::appUpdateManager.isInitialized) appUpdateManager.unregisterListener(listener)
        super.onStop()
    }
}
