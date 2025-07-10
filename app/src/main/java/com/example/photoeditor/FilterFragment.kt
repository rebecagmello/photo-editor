package com.example.photoeditor

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.photoeditor.databinding.FragmentFilterBinding

class FilterFragment : Fragment() {
    private val filterViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    private var currentBitmap: Bitmap? = null
    private var originalBitmap: Bitmap? = null

    companion object {
        init {
            System.loadLibrary("photoeditor")
        }
    }
    external fun applyNegative(bitmap: Bitmap)
    external fun applySepia(bitmap: Bitmap)
    external fun applyGrayscale(bitmap: Bitmap)

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
            originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)?.let { bwBitmap ->
                applyGrayscale(bwBitmap)
                binding.imageView.setImageBitmap(bwBitmap)
                currentBitmap = bwBitmap
            }
        }

        binding.buttonSepia.setOnClickListener {
            originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)?.let { sepiaBitmap ->
                applySepia(sepiaBitmap)
                binding.imageView.setImageBitmap(sepiaBitmap)
                currentBitmap = sepiaBitmap
            }
        }

        binding.buttonInverted.setOnClickListener {
            originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)?.let { invBitmap ->
                applyNegative(invBitmap)
                binding.imageView.setImageBitmap(invBitmap)
                currentBitmap = invBitmap
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

            filterViewModel.changeImage(currentBitmap!!)
            findNavController().navigateUp()
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        filterViewModel.changeImage(currentBitmap!!)
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
