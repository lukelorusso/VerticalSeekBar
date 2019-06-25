package com.lukelorusso.verticalseekbarsample

import android.os.Bundle
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
    }

}
