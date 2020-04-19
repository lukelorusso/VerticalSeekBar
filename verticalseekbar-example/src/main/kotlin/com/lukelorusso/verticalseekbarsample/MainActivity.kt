package com.lukelorusso.verticalseekbarsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainVerticalSeekBar.apply {
            progress = 75

            setOnProgressChangeListener { progressValue ->
                Log.d("VerticalSeekBar", "PROGRESS CHANGED at value: $progressValue")
                mainProgressValue.text = progressValue.toString()
            }

            setOnPressListener { progressValue ->
                Log.d("VerticalSeekBar", "PRESSED at value: $progressValue")
            }

            setOnReleaseListener { progressValue ->
                Log.d("VerticalSeekBar", "RELEASED at value: $progressValue")
            }
        }

        mainProgressValue.text = mainVerticalSeekBar.progress.toString()
    }

}
