package com.example.android_colorpickerpointer_example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.android_colorpickerpointer_example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.colorPickerPointer.run {
            viewMargin = 10f * resources.displayMetrics.density
            colorChipRadius = 30f * resources.displayMetrics.density
            colorChipY = 52.5f * resources.displayMetrics.density
        }

        binding.button.setOnClickListener {
            binding.colorPickerPointer.let { pointer: ColorPickerPointer ->
                if (pointer.isVisible) {
                    pointer.hide()
                    binding.button.text = "Show"
                } else {
                    pointer.show { color: Int -> }
                    binding.button.text = "Hide"
                }
            }
        }
    }
}
