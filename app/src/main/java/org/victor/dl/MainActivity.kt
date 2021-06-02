package org.victor.dl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity(),View.OnClickListener {

     var mBtnUpgradeNow: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()
    }

    fun initialize () {
        mBtnUpgradeNow = findViewById(R.id.mBtnUpgradeNow)
        mBtnUpgradeNow?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mBtnUpgradeNow -> {
                var updateDialog = AppUpdateDialog(this)
                updateDialog.mLatestVersionData = LatestVersionData()
                updateDialog.show()
            }
        }
    }
}