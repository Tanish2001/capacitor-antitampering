package io.github.asephermann.plugins.antitampering

import android.os.Debug
import java.lang.reflect.Field


internal object DebugDetection {
    @Throws(Exception::class)
    fun check(packageName: String) {
        if (hasDebuggerAttached()) {
            throw Exception("Debugger attached")
        } else if (getDebugField(packageName)) {
            throw Exception("App running in Debug mode")
        }
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