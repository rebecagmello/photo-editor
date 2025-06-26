package com.example.photoeditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.photoeditor.databinding.FragmentLightBinding

class LightFragment : Fragment(), OnSeekBarChangeListener {

    private val mainViewModel: LightViewModel by viewModels()
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
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        binding.textView.text = "Brightness: $progress"
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
}
