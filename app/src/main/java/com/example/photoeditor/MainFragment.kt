package com.example.photoeditor

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.photoeditor.databinding.FragmentMainBinding
import java.io.File
import androidx.core.view.MenuProvider


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()

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

        setFragmentResultListener("light_result") { _, bundle ->
            val lightedImage = bundle.getParcelable<Bitmap>("bitmap", Bitmap::class.java)
            lightedImage?.let {
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
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
                menu.findItem(R.id.action_save)?.let {
                    setMenuItemColor(it, Color.BLUE)
                }
                menu.findItem(R.id.action_share)?.let {
                    setMenuItemColor(it, Color.BLUE)
                }
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_save -> {
                        saveImageToGallery(viewModel.image.value)
                        true
                    }
                    R.id.action_share -> {
                        shareImage(viewModel.image.value)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

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

        binding.buttonColor.setOnClickListener {
            if (isImageSelected()) {
                findNavController().navigate(R.id.action_mainFragment_to_colorFragment)
            } else {
                showSelectImageMessage()
            }
        }

        binding.buttonFilters.setOnClickListener {
            if (isImageSelected()) {
                findNavController().navigate(R.id.action_mainFragment_to_filterFragment)
            } else {
                showSelectImageMessage()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun shareImage(bitmap: Bitmap) {
        val file = File(requireContext().cacheDir, "shared_image.jpg")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Image"))
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val context = requireContext() // pega uma vez só

        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val contentResolver = context.contentResolver

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                val saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                if (!saved) {
                    Toast.makeText(context, "Erro ao salvar imagem", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, values, null, null)

            Toast.makeText(context, "Imagem salva com sucesso", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Falha ao salvar imagem", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMenuItemColor(menuItem: MenuItem?, color: Int) {
        menuItem?.let {
            val title = it.title.toString()
            val coloredTitle = SpannableString(title).apply {
                setSpan(ForegroundColorSpan(color), 0, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            it.title = coloredTitle
        }
    }
}