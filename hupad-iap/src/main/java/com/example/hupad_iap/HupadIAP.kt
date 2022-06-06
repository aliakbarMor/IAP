package com.example.hupad_iap

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class HupadIAP(
    private val activity: AppCompatActivity,
    private val onCLick: (s: String, b: Boolean) -> Unit
) {
    private val resultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 521378) {
                val data: Intent? = result.data
                onCLick.invoke(
                    data?.getStringExtra("productId") ?: "",
                    data?.getBooleanExtra("result", false)!!
                )
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