package com.example.photoeditor

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.photoeditor.databinding.FragmentMainBinding
import androidx.fragment.app.activityViewModels

class MainFragment : Fragment() {

    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!

    //class that allows access to the user gallery

    private val sharedViewModel : SharedViewModel by activityViewModels()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> //here whit getContent the user permits access to his gallery by selecting a image
            uri?.let {
                sharedViewModel.imageUri = it
                binding.imageView.setImageURI(it)
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container : ViewGroup?, savedInstanceState: Bundle?) : View {
        _binding= FragmentMainBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.imageUri?.let {
            binding.imageView.setImageURI(it)
        }

        binding.buttonLoad.setOnClickListener {
            pickImageLauncher.launch("image/*") //here i am telling what my load is responsible for
        }

        val navController = findNavController()

        val buttonMap = mapOf(
            binding.buttonCrop to "CROP",
            binding.buttonLight to "LIGHT",
            binding.buttonColor to "COLOR",
            binding.buttonFilters to "FILTERS"
        )

        for ((button, name) in buttonMap) { // for each one of my buttons the feature activity displays an different action
            button.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("feature", name)
                }
                navController.navigate(R.id.featureFragment,bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}