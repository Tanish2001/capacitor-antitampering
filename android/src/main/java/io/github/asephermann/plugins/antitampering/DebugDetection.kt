package io.github.asephermann.plugins.antitampering

import android.app.Activity
import android.app.AlertDialog
import android.os.Debug
import java.lang.reflect.Field


internal object DebugDetection {
    @Throws(Exception::class)
    fun check(
        activity: Activity,
        packageName: String,
        throwExceptionEnabled: Boolean = false
    ): String {

        var msg = ""
        try {
            if (hasDebuggerAttached()) {
                msg = "Debugger attached"
            } else if (getDebugField(packageName)) {
                msg = "App running in Debug mode"
            }

            val alertDialog: AlertDialog = AlertDialog.Builder(activity).create()
            alertDialog.setTitle("Debug Detection")
            alertDialog.setMessage(msg)
            alertDialog.setButton(
                AlertDialog.BUTTON_POSITIVE, "OK"
            ) { dialog, _ ->
                dialog.dismiss()
                if (msg != "") activity.finish()
                if (throwExceptionEnabled) throw Exception(msg)
            }

            if (msg != "") alertDialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return msg
    }

    @Throws(
        ClassNotFoundException::class,
        NoSuchFieldException::class,
        IllegalAccessException::class
    )
    private fun getDebugField(packageName: String): Boolean {
        val buildConfigClass = Class.forName("$packageName.BuildConfig")
        val debugField: Field = buildConfigClass.getField("DEBUG")
        return debugField.getBoolean(null)
    }

    private fun hasDebuggerAttached(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }
}