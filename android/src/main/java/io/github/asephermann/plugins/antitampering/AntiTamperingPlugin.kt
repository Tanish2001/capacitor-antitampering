package io.github.asephermann.plugins.antitampering

import android.app.Activity
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

    @PluginMethod
    fun verify(call: PluginCall) {

        checkAndStopExecution(activity)

        var status: String
        var assetsCount: Int
        var messages: String

        val ret = JSObject()

        val executor = Executors.newSingleThreadExecutor()

        executor.submit {
            try {
                assetsCount = check(activity, activity.assets)
                status = "CHECKED"
                messages = check(activity, activity.packageName)
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

    private fun checkAndStopExecution(mActivity: Activity) {
        try {
            check(mActivity, mActivity.assets)
            check(mActivity, mActivity.packageName)
        } catch (e: Exception) {
            mActivity.runOnUiThread {
                e.printStackTrace()
                throw TamperingException("Anti-Tampering check failed")
            }
        }
    }
}