package org.victor.dl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import java.io.File

class MainActivity : AppCompatActivity(),View.OnClickListener {
    val TAG = "MainActivity"
     var mBtnUpgradeNow: Button? = null
     var mBtnDelete: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()
    }

    fun initialize () {
        mBtnUpgradeNow = findViewById(R.id.mBtnUpgradeNow)
        mBtnDelete = findViewById(R.id.mBtnDelete)

        mBtnUpgradeNow?.setOnClickListener(this)
        mBtnDelete?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mBtnUpgradeNow -> {
                var updateDialog = AppUpdateDialog(this)
                updateDialog.lifecycleScope = GlobalScope
                updateDialog.mLatestVersionData = LatestVersionData()
                updateDialog.show()
            }
            R.id.mBtnDelete -> {
                deleteDir(File(filesDir.path))
            }
        }
    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        //判定空
        return dir?.delete() ?: false
    }
}