package com.lukelorusso.verticalseekbarsample

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainVerticalSeekBar.progress = 75
        mainVerticalSeekBar.setOnProgressChangeListener { progressValue ->
            mainProgressValue.text = progressValue.toString()
        }
        mainProgressValue.text = mainVerticalSeekBar.progress.toString()
        Handler().postDelayed({
            mainVerticalSeekBar.clickToSetProgress = true
            Toast.makeText(this, "Now you can click on drawable to set progress!", Toast.LENGTH_LONG).show()
        }, 5000)
    }

}
