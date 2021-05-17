package com.lukelorusso.verticalseekbarsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lukelorusso.verticalseekbarsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mainVerticalSeekBar.apply {
            progress = 75

            setOnProgressChangeListener { progressValue ->
                Log.d("VerticalSeekBar", "PROGRESS CHANGED at value: $progressValue")
                binding.mainProgressValue.text = progressValue.toString()
            }

            setOnPressListener { progressValue ->
                Log.d("VerticalSeekBar", "PRESSED at value: $progressValue")
            }

            setOnReleaseListener { progressValue ->
                Log.d("VerticalSeekBar", "RELEASED at value: $progressValue")
            }
        }

        binding.mainProgressValue.text = binding.mainVerticalSeekBar.progress.toString()
    }

}
