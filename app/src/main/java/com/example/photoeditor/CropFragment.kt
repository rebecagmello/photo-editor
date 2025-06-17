package com.example.photoeditor

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.example.photoeditor.databinding.FragmentCropBinding

class CropFragment : Fragment() {

    private val mainViewModel : MainViewModel by activityViewModels()
    private var _binding: FragmentCropBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCropBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.image.observe(viewLifecycleOwner){ bitmap->
            binding.cropImageView.setImageBitmap(bitmap) }


        binding.buttonRotate.setOnClickListener {
            binding.cropImageView.rotateImage(90)
        }

        binding.buttonSave.setOnClickListener {
            val cropped = binding.cropImageView.getCroppedImage()
            cropped?.let{
                mainViewModel.changeImage(it)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}