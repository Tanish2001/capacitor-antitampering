package io.github.asephermann.plugins.antitampering

import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import io.github.asephermann.plugins.antitampering.AssetsIntegrity.check
import io.github.asephermann.plugins.antitampering.DebugDetection.check
import java.util.concurrent.Executors


@CapacitorPlugin(name = "AntiTampering")
class AntiTamperingPlugin : Plugin() {
    init {
        checkAndStopExecution()
    }

    private fun checkAndStopExecution() {
        try {
            AssetsIntegrity.check(activity.assets)
            DebugDetection.check(activity.packageName)
        } catch (e: Exception) {
            activity.runOnUiThread {
                e.printStackTrace()
                throw TamperingException("Anti-Tampering check failed")
            }
        }
    }

    @PluginMethod
    fun verify(call: PluginCall) {
        var status: String
        var assetsCount: Int
        var messages: String

        val ret = JSObject()

        val executor = Executors.newSingleThreadExecutor()

        executor.submit {
            try {
                check(activity.packageName)
                assetsCount = check(activity.assets)
                status = "OK"
                messages = "OK"
            } catch (e: Exception) {
                assetsCount = 0
                status = "ERROR"
                messages = e.toString()
            }

            ret.put("status", status)
            ret.put("messages", messages)
            ret.put("assetsCount", assetsCount)
            call.resolve(ret)
        }
    }
}