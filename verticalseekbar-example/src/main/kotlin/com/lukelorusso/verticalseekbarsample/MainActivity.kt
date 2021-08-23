package com.lukelorusso.verticalseekbarsample

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lukelorusso.verticalseekbarsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val thumb = TextView(this).apply {
            text = "0"
            layoutParams = ViewGroup.LayoutParams(
                300,
                80
            )
        }
        binding.mainVerticalSeekBar.apply {
            progress = 75

            setOnProgressChangeListener { progressValue ->
                Log.d("VerticalSeekBar", "PROGRESS CHANGED at value: $progressValue")
                binding.mainProgressValue.text = progressValue.toString()
                thumb.text = progressValue.toString()
            }

            setOnPressListener { progressValue ->
                Log.d("VerticalSeekBar", "PRESSED at value: $progressValue")
            }

            setOnReleaseListener { progressValue ->
                Log.d("VerticalSeekBar", "RELEASED at value: $progressValue")
            }

        }

        binding.mainProgressValue.text = binding.mainVerticalSeekBar.progress.toString()

        binding.mainVerticalSeekBar.thumbCustomView = thumb
    }

}
