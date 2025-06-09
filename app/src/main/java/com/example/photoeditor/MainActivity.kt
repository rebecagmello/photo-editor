package com.example.photoeditor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var imageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                binding.imageView.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLoad.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        val buttonMap = mapOf(
            binding.buttonCrop to "CROP",
            binding.buttonLight to "LIGHT",
            binding.buttonColor to "COLOR",
            binding.buttonFilters to "FILTERS"
        )

        for ((button, name) in buttonMap) {
            button.setOnClickListener {
                val intent = Intent(this, FeatureActivity::class.java)
                intent.putExtra("feature_name", name)
                startActivity(intent)
            }
        }
    }
}