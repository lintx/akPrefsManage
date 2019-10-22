package org.lintx.akprefsmanage.utils

import android.app.Activity
import android.content.Context
import com.onedrive.sdk.authentication.MSAAuthenticator
import com.onedrive.sdk.concurrency.ICallback
import com.onedrive.sdk.core.DefaultClientConfig
import com.onedrive.sdk.core.ClientException
import com.onedrive.sdk.core.OneDriveErrorCodes
import com.onedrive.sdk.extensions.IItemRequestBuilder
import com.onedrive.sdk.extensions.IOneDriveClient
import com.onedrive.sdk.extensions.Item
import com.onedrive.sdk.extensions.OneDriveClient
import org.lintx.akprefsmanage.model.Prefs
import java.io.FileInputStream
import java.io.InputStream
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread


class OneDrive private constructor(){
    companion object {
        private var instance: OneDrive? = null
            get() {
                if (field == null) {
                    field = OneDrive()
                }
                return field
            }
        fun get(): OneDrive{
            return instance!!
        }
    }

    private val mClient = AtomicReference<IOneDriveClient>()
    private val msaAuthentication = MSAAuthenticators()
    private val oneDriveConfig = DefaultClientConfig.createWithAuthenticator(msaAuthentication)

    private val fileName = "tokens.data"

    fun createClient(activity: Activity,callback: ICallback<IOneDriveClient>){
        val cb = object : ICallback<IOneDriveClient>{
            override fun success(result: IOneDriveClient?) {
                mClient.set(result)
                callback.success(result)
            }

            override fun failure(ex: ClientException?) {
                callback.failure(ex)
            }
        }
        OneDriveClient.Builder()
            .fromConfig(oneDriveConfig)
            .loginAndBuildClient(activity, cb)
    }

    fun getTokens(callback:ICallback<Item>){
        val client = mClient.get()
        if (client==null){
            callback.failure(ClientException("no file",
                Throwable(),
                OneDriveErrorCodes.NotAllowed
            ))
            return
        }
        thread {
            getRequestBuilder(client).buildRequest().get(callback)
        }
    }

    fun downloadTokens(callback: ICallback<InputStream>){
        val client = mClient.get() ?: return

        thread {
            getRequestBuilder(client).content.buildRequest().get(callback)
        }
    }

    fun updateTokens(context: Context,callback:ICallback<Item>){
        val client = mClient.get()
        if (client==null){
            callback.failure(ClientException("",
                Throwable(),
                OneDriveErrorCodes.NotAllowed
            ))
            return
        }
        val file = Prefs.get().getTokensFile(context)
        val fis = FileInputStream(file)
        thread {
            getRequestBuilder(client).content.buildRequest().put(fis.readBytes(),callback)
        }
    }

    private fun getRequestBuilder(client: IOneDriveClient): IItemRequestBuilder {
        return client.drive.getSpecial("approot").getItemWithPath(fileName)
    }
}

class MSAAuthenticators : MSAAuthenticator() {
    override fun getScopes(): Array<String> {
//        return arrayOf("onedrive.appfolder","onedrive.readwrite","offline_access")
        return arrayOf("onedrive.readwrite","offline_access")
    }

    override fun getClientId(): String {
        return "7cea49c4-57bd-49ca-a178-f06e219f09dc"
    }
}
