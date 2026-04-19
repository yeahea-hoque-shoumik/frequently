package com.prime.frequently.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.prime.frequently.R
import com.prime.frequently.data.WaveCategory
import com.prime.frequently.databinding.FragmentLibraryBinding
import com.prime.frequently.viewmodel.HomeViewModel
import com.prime.frequently.viewmodel.PresetsViewModel
import kotlinx.coroutines.launch

class LibraryFragment : Fragment() {

    private var _b: FragmentLibraryBinding? = null
    private val b get() = _b!!

    private val vm: PresetsViewModel by viewModels()
    private val homeVm: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentLibraryBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = WavePresetAdapter { preset ->
            homeVm.applyPreset(preset)
            findNavController().navigate(R.id.action_library_to_player)
        }

        b.presetList.layoutManager = GridLayoutManager(requireContext(), 2)
        b.presetList.adapter = adapter

        // Chip click listeners
        val chipMap = mapOf(
            b.chipAll to null,
            b.chipDelta to WaveCategory.DELTA,
            b.chipTheta to WaveCategory.THETA,
            b.chipAlpha to WaveCategory.ALPHA,
            b.chipBeta to WaveCategory.BETA,
            b.chipGamma to WaveCategory.GAMMA,
            b.chipSpiritual to WaveCategory.SPIRITUAL
        )
        chipMap.forEach { (chip, category) ->
            chip.setOnClickListener { vm.selectCategory(category) }
        }

        // Search watcher
        b.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                vm.setSearchQuery(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        b.btnCustomHz.setOnClickListener {
            findNavController().navigate(R.id.action_library_to_customHz)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.presets.collect { presets ->
                        adapter.submitList(presets)
                    }
                }
                launch {
                    vm.selectedCategory.collect { selected ->
                        updateChipStyles(chipMap, selected)
                    }
                }
            }
        }
    }

    private fun updateChipStyles(
        chipMap: Map<TextView, WaveCategory?>,
        selected: WaveCategory?
    ) {
        chipMap.forEach { (chip, category) ->
            val isActive = category == selected
            chip.background = ContextCompat.getDrawable(
                requireContext(),
                if (isActive) R.drawable.bg_chip_active else R.drawable.bg_chip
            )
            chip.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isActive) R.color.ink else R.color.ink_dim
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
