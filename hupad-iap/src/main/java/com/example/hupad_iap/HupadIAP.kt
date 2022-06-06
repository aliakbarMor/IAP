package com.example.hupad_iap
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class HupadIAP {
    companion object {
        private const val TAG = "HUPAD_IAP"

        fun iapOnClick(activity: AppCompatActivity, productId: String) {
            Log.d(TAG, "iapOnClick: testttttttttttttt")

            val resultLauncher =
                activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    Log.d(TAG, "resultCode: ${result.resultCode}")
                    if (result.resultCode == 521378) {
                        val data: Intent? = result.data
                        Log.d(TAG, data?.getStringExtra("appName").toString())
                        Log.d(TAG, data?.getStringExtra("packageName").toString())
                        Log.d(TAG, data?.getStringExtra("productId").toString())
                    }
                }
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
            resultLauncher.launch(intent);
        }
    }


}