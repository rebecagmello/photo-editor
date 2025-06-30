package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.example.photoeditor.databinding.FragmentColorBinding
import kotlin.getValue
import androidx.core.graphics.createBitmap
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController

class ColorFragment : Fragment(){
    private val colorViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentColorBinding? = null
    private val binding get() = _binding!!
    private fun setupSeekBars() {

        binding.seekContrastBar.max = 200   
        binding.seekContrastBar.progress = 100

        binding.seekSaturationBar.max = 200
        binding.seekSaturationBar.progress = 100


        val seekListener = object : OnSeekBarChangeListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                applyAdjustments()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }

        binding.seekContrastBar.setOnSeekBarChangeListener(seekListener)
        binding.seekSaturationBar.setOnSeekBarChangeListener(seekListener)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun applyAdjustments() {
        val original = colorViewModel.image.value ?: return

        val contrastInput = binding.seekContrastBar.progress
        val contrastFactor = (contrastInput + 100) / 100f

        val saturationFactor = binding.seekSaturationBar.progress / 100f

        binding.contTextView.text = "Contraste: $contrastInput"
        binding.satTextView.text = "Saturação: ${"%.2f".format(saturationFactor)}"

        val adjustedBitmap = createAdjustedBitmap(original, contrastFactor, saturationFactor)

        binding.colorImageView.setImageBitmap(adjustedBitmap)
        colorViewModel.changeImage(adjustedBitmap)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAdjustedBitmap(
        original: Bitmap,
        contrast: Float,
        saturation: Float
    ): Bitmap {

        val safeBitmap = if (original.config == Bitmap.Config.HARDWARE || !original.isMutable) {
            original.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            original
        }

        val contrastMatrix = ColorMatrix().apply {
            set(floatArrayOf(
                contrast, 0f, 0f, 0f, 0f,
                0f, contrast, 0f, 0f, 0f,
                0f, 0f, contrast, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
        }

        val saturationMatrix = ColorMatrix().apply {
            setSaturation(saturation)
        }

        contrastMatrix.postConcat(saturationMatrix)

        val resultBitmap = createBitmap(safeBitmap.width, safeBitmap.height)

        val canvas = Canvas(resultBitmap)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(contrastMatrix)
        }

        canvas.drawBitmap(safeBitmap, 0f, 0f, paint)
        return resultBitmap
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentColorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorViewModel.image.observe(viewLifecycleOwner) { bitmap ->
            binding.colorImageView.setImageBitmap(bitmap)
        }
        setupSeekBars()

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        val originalBitmap = colorViewModel.image.value ?: return true
                        val contrast = binding.seekContrastBar.progress.toFloat()
                        val saturation = binding.seekSaturationBar.progress.toFloat()
                        val result = createAdjustedBitmap(originalBitmap, contrast = contrast , saturation = saturation)
                        colorViewModel.changeImage(result)
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}











