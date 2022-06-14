package com.example.iap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hupad_iap.HupadIAP
import com.example.iap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var iap: HupadIAP = HupadIAP(this, { productId: String, error: String ->
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }) { productId: String, trackingCode: String? ->
        Toast.makeText(this, "Payment was successful \ntxid: $trackingCode", Toast.LENGTH_LONG)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.purchase.setOnClickListener { view ->
            iap.iapOnClick("f70a7225-e413-4138-9431-104958489623")
        }

    }
}