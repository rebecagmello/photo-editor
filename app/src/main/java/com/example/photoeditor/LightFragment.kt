package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.addCallback
import androidx.core.graphics.createBitmap
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.photoeditor.databinding.FragmentLightBinding


class LightFragment : Fragment(), OnSeekBarChangeListener {

    private val mainViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentLightBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLightBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.image.observe(viewLifecycleOwner) { bitmap ->
            binding.lightImageView.setImageBitmap(bitmap)
        }

        binding.seekBar.setOnSeekBarChangeListener(this)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val originalBitmap = mainViewModel.image.value ?: return@addCallback

            val resultBitmap = applyBrightnessFilter(originalBitmap, binding.seekBar.progress)
            mainViewModel.changeImage(resultBitmap)

            findNavController().navigateUp()
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        val originalBitmap = mainViewModel.image.value ?: return true
                        val result = applyBrightnessFilter(originalBitmap, binding.seekBar.progress)
                        mainViewModel.changeImage(result)
                        findNavController().navigateUp()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val brightness = progress
        binding.textView.text = "Brightness: $brightness"


        val matrix = ColorMatrix().apply {
            set(
                floatArrayOf(
                    1f, 0f, 0f, 0f, brightness.toFloat(),
                    0f, 1f, 0f, 0f, brightness.toFloat(),
                    0f, 0f, 1f, 0f, brightness.toFloat(),
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }


        val filter = ColorMatrixColorFilter(matrix)
        binding.lightImageView.colorFilter = filter

    }


    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // do nothing
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // do nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applyBrightnessFilter(src: Bitmap, brightness: Int): Bitmap {
        val mutableSrc = src.copy(Bitmap.Config.ARGB_8888, true)
        val bmp = createBitmap(mutableSrc.width, mutableSrc.height)
        val canvas = Canvas(bmp)
        val paint = Paint()

        val matrix = ColorMatrix().apply {
            set(
                floatArrayOf(
                    1f, 0f, 0f, 0f, brightness.toFloat(),
                    0f, 1f, 0f, 0f, brightness.toFloat(),
                    0f, 0f, 1f, 0f, brightness.toFloat(),
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(mutableSrc, 0f, 0f, paint)

        return bmp
    }

}
