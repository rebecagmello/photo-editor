package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.photoeditor.databinding.FragmentFilterBinding
import androidx.core.graphics.createBitmap

class FilterFragment : Fragment() {
    private val filterViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    private var currentBitmap: Bitmap? = null
    private var originalBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterViewModel.image.observe(viewLifecycleOwner) { bitmap ->
            currentBitmap = bitmap
            originalBitmap = bitmap
            binding.imageView.setImageBitmap(bitmap)
        }

        binding.buttonBw.setOnClickListener {
            currentBitmap?.let {
                val bwBitmap = applyBlackAndWhiteFilter(originalBitmap ?: it)
                binding.imageView.setImageBitmap(bwBitmap)
                currentBitmap = bwBitmap
            }
        }

        binding.buttonAutumn.setOnClickListener {
            currentBitmap?.let {
                val autumnBitmap = applyAutumnFilter(originalBitmap ?: it)
                binding.imageView.setImageBitmap(autumnBitmap)
                currentBitmap = autumnBitmap
            }
        }

        binding.buttonInverted.setOnClickListener {
            currentBitmap?.let {
                val invertedBitmap = applyInvertedFilter(originalBitmap ?: it)
                binding.imageView.setImageBitmap(invertedBitmap)
                currentBitmap = invertedBitmap
            }
        }

        binding.buttonSave.setOnClickListener {
            currentBitmap?.let { bitmap ->
                filterViewModel.changeImage(bitmap)
            }
        }
    }

    fun applyBlackAndWhiteFilter(src: Bitmap): Bitmap {
        val mutableSrc = src.copy(Bitmap.Config.ARGB_8888, true)
        val bmp = createBitmap(mutableSrc.width, mutableSrc.height)
        val canvas = Canvas(bmp)
        val paint = Paint()

        val matrix = ColorMatrix().apply {
            setSaturation(0f)
        }

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(mutableSrc, 0f, 0f, paint)
        return bmp
    }

    fun applyAutumnFilter(src: Bitmap): Bitmap {
        val mutableSrc = src.copy(Bitmap.Config.ARGB_8888, true)
        val bmp = createBitmap(mutableSrc.width, mutableSrc.height)
        val canvas = Canvas(bmp)
        val paint = Paint()

        val matrix = ColorMatrix().apply {
            set(floatArrayOf(
                1f, 0f, 0f, 0f, 50f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
        }

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(mutableSrc, 0f, 0f, paint)
        return bmp
    }

    fun applyInvertedFilter(src: Bitmap): Bitmap {
        val mutableSrc = src.copy(Bitmap.Config.ARGB_8888, true)
        val bmp = createBitmap(mutableSrc.width, mutableSrc.height)
        val canvas = Canvas(bmp)
        val paint = Paint()

        val matrix = ColorMatrix().apply {
            set(floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
            ))
        }

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(mutableSrc, 0f, 0f, paint)
        return bmp
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
