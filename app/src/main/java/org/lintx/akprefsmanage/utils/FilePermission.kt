package org.lintx.akprefsmanage.utils

import java.io.DataOutputStream

class FilePermission(private val file:String) {
    fun setRead():Boolean{
        return changeFilePermission("+r")
    }

    fun setReadWrite():Boolean{
        return changeFilePermission("+rw")
    }

    fun cancel():Boolean{
        return changeFilePermission("-rw")
    }

    private fun changeFilePermission(permission:String):Boolean{
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            val cmd = "chmod o$permission $file"
            process = Runtime.getRuntime().exec("su") //切换到root帐号
            os = DataOutputStream(process!!.outputStream)
            os.writeBytes(cmd + "\n")
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor()
        } catch (e: Exception) {
            return false
        } finally {
            try {
                os?.close()
                process!!.destroy()
            } catch (e: Exception) {
            }

        }
        return true
    }
}