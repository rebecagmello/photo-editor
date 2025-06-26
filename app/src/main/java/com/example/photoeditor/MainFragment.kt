package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.photoeditor.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.P)
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {

                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                viewModel.changeImage(bitmap)

            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("crop_result") { _, bundle ->
            val croppedImage = bundle.getParcelable<Bitmap>("bitmap", Bitmap::class.java)
            croppedImage?.let {
                viewModel.changeImage(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.image.observe(viewLifecycleOwner) { bitmap ->
            binding.imageView.setImageBitmap(bitmap)
        }

        binding.buttonLoad.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        fun isImageSelected(): Boolean {
            return viewModel.image.value != null
        }

        fun showSelectImageMessage() {
            AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle("Atenção")
                .setMessage("Selecione uma imagem")
                .setPositiveButton("OK", null)
                .create()
                .show()
        }

        binding.buttonCrop.setOnClickListener {
            if (isImageSelected()) {
                findNavController().navigate(R.id.action_mainFragment_to_cropFragment)

            } else {
                showSelectImageMessage()
            }
        }

        binding.buttonLight.setOnClickListener {
            if (isImageSelected()) {
                findNavController().navigate(R.id.action_mainFragment_to_lightFragment)
            } else {
                showSelectImageMessage()
            }
        }

        val navController = findNavController()
        val buttonMap = mapOf(
            binding.buttonColor to "COLOR",
            binding.buttonFilters to "FILTERS"
        )

        for ((button, name) in buttonMap) {
            button.setOnClickListener {
                if (isImageSelected()) {
                    val bundle = Bundle().apply {
                        putString("feature", name)
                    }
                    navController.navigate(R.id.featureFragment, bundle)
                } else {
                    showSelectImageMessage()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}