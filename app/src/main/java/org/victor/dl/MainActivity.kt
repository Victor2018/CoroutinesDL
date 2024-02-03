package org.victor.dl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import org.victor.dl.library.CoroutinesDL.download
import org.victor.dl.library.util.clear
import org.victor.dl.library.util.shadow
import org.victor.dl.library.util.tmp
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
                deleteDir()
            }
        }
    }

    fun deleteDir() {
        val data = LatestVersionData()
        val downloadTask = GlobalScope.download(data.appDownloadUrl ?: "",filesDir.path)
        downloadTask.remove()
    }
}