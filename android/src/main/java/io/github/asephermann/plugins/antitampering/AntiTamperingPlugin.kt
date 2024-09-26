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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

@CapacitorPlugin(name = "AntiTampering")
class AntiTamperingPlugin : Plugin() {

    @PluginMethod
    fun verify(call: PluginCall) {
        checkAndStopExecution(activity)
        var status: String
        var assetsCount: Int
        var messages: String
        var finalVerdict:Boolean
        var tamperFileNames:String
        val ret = JSObject()

        val executor = Executors.newSingleThreadExecutor()
        val check: HashMap<String, String?> = check(activity, activity.assets)
        executor.submit {
            try {
                assetsCount = check.get("assetsHashesSize")?.toInt()!!
                status = "CHECKED"
                messages = check(activity, activity.packageName)
                finalVerdict = check.get("isTampered").toBoolean()
                tamperFileNames = check.get("tamperFileNames")!!
            } catch (e: Exception) {
                assetsCount = 0
                status = "ERROR"
                messages = e.toString()
                finalVerdict = check.get("isTampered").toBoolean()
                tamperFileNames = check.get("tamperFileNames")!!
            }

            ret.put("status", status)
            ret.put("messages", messages)
            ret.put("assetsCount", assetsCount)
            ret.put("finalVerdict",finalVerdict)
            ret.put("tamperFileNames",tamperFileNames)
            call.resolve(ret)
        }
    }

    private fun checkAndStopExecution(mActivity: Activity) {
        val backgroundScope = CoroutineScope(IO);
        try {
            backgroundScope.launch {
                check(mActivity, mActivity.assets)
            }
//            mActivity.runOnUiThread {
//                val runnable = object : Runnable {
//                    override fun run() {
//                        // Do something
//                        check(mActivity, mActivity.assets)
//                    }
//                }
//            }
//            check(mActivity, mActivity.packageName)
        } catch (e: Exception) {
            mActivity.runOnUiThread {
                e.printStackTrace()
                throw TamperingException("Anti-Tampering check failed")
            }
        }
    }
}