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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.photoeditor.databinding.FragmentMainBinding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.P)
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> //here whit getContent the user permits access to his gallery by selecting a image
            uri?.let {
                sharedViewModel.imageUri = it
                val source = ImageDecoder.createSource( requireContext().contentResolver, uri )
                val bitmap = ImageDecoder.decodeBitmap(source)
                mainViewModel.changeImage(bitmap)

            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("crop_result"){_,bundle ->
            val croppedImage = bundle.getParcelable<Bitmap>("bitmap", Bitmap::class.java)
            croppedImage?.let{
                mainViewModel.changeImage(it)
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


        mainViewModel.image.observe(viewLifecycleOwner) { bitmap ->
            binding.imageView.setImageBitmap(bitmap)
        }


        binding.buttonLoad.setOnClickListener {
            pickImageLauncher.launch("image/*") //here i am telling what my load is responsible for
        }

        binding.buttonCrop.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_cropFragment)
        }

        val navController = findNavController()

        val buttonMap = mapOf(
            binding.buttonLight to "LIGHT",
            binding.buttonColor to "COLOR",
            binding.buttonFilters to "FILTERS"
        )

        for ((button, name) in buttonMap) { // for each one of my buttons the feature activity displays an different action
            button.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("feature", name)
                }
                navController.navigate(R.id.featureFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}