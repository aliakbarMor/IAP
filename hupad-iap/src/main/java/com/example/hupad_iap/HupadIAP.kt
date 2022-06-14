package com.example.hupad_iap

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class HupadIAP(
    private val activity: AppCompatActivity,
    private val onError: (productId: String, error: String) -> Unit,
    private val onSuccess: (productId: String, trackingCode: String) -> Unit
) {
    private val resultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val trackingCode = data?.getStringExtra("trackingCode")
                if (trackingCode != null) {

                    OkHttpClient().newCall(
                        Request.Builder()
                            .url("https://master.hupadstore.ir/api/Wallet/CheckPurchase")
                            .method(
                                "POST", RequestBody.create(
                                    MediaType.parse("application/json"),
                                    "{\"txid\":\"$trackingCode\"}"
                                )
                            )
                            .addHeader("Content-Type", "application/json")
                            .build()
                    ).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            onError.invoke(
                                data.getStringExtra("productId") ?: "",
                                e.toString()
                            )
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val jsonData = response.body()!!.string()
                            val jObject = JSONObject(jsonData)
                            val status = jObject.getInt("status")
                            if (status == 1)
                                onSuccess.invoke(
                                    data.getStringExtra("productId")!!,
                                    jObject.getString("entity")
                                )
                            else onError.invoke(
                                data.getStringExtra("productId") ?: "",
                                JSONObject(
                                    data.getStringExtra("error") ?: ""
                                ).getString(data.getStringExtra("message") ?: "") ?: ""
                            )
                        }
                    })
                } else {
                    onError.invoke(
                        data?.getStringExtra("productId") ?: "",
                        "field"
                    )
                }
            }
        }


    fun iapOnClick(productId: String) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName(
                "com.example.hupad_market",
                "com.example.hupad_market.IAPActivity"
            )
            putExtra(
                "appName",
                activity.applicationInfo.loadLabel(activity.packageManager).toString()
            )
            putExtra("packageName", activity.packageName)
            putExtra("productId", productId)
        }

        try {
            resultLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                activity,
                "Hupad store is not installed on this device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}