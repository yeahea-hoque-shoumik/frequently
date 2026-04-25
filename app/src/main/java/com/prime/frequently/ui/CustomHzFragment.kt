package com.prime.frequently.ui

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.prime.frequently.R
import com.prime.frequently.constants.AppConstants
import com.prime.frequently.data.WaveCategory
import com.prime.frequently.databinding.FragmentCustomHzBinding
import com.prime.frequently.utils.FrequencyUtils
import com.prime.frequently.viewmodel.HomeViewModel

class CustomHzFragment : Fragment() {

    private var _b: FragmentCustomHzBinding? = null
    private val b get() = _b!!

    private val homeVm: HomeViewModel by activityViewModels()

    // Buffer holds digits the user is typing for carrier Hz
    private val inputBuffer = StringBuilder("200")

    // Current beat Hz derived from slider progress
    private var currentBeatHz: Double = 7.8

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentCustomHzBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.btnBack.setOnClickListener { findNavController().navigateUp() }

        setupKeypad()
        setupBeatSlider()
        setupQuickRecall()
        updateCarrierDisplay()
        updateBeatDisplay()
        updatePreviewCard()

        // Apply gradient to carrier display after layout pass
        b.tvCarrierDisplay.doOnLayout { applyGradient() }

        b.btnPlay.setOnClickListener {
            val carrier = inputBuffer.toString().toDoubleOrNull()
                ?.coerceIn(AppConstants.CARRIER_HZ_MIN, AppConstants.CARRIER_HZ_MAX)
                ?: AppConstants.CARRIER_HZ_MIN
            homeVm.setCarrierHz(carrier)
            homeVm.setBeatHz(currentBeatHz)
            homeVm.play()
            findNavController().navigate(R.id.action_customHz_to_player)
        }
    }

    // ── Keypad ────────────────────────────────────────────────────────────────

    private fun setupKeypad() {
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("⌫", "0", "C")
        )
        val gap = resources.getDimensionPixelSize(R.dimen.keypad_gap)
        val btnHeight = resources.getDimensionPixelSize(R.dimen.keypad_btn_height)

        rows.forEach { keys ->
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = gap }
            }

            keys.forEach { key ->
                val btn = TextView(requireContext()).apply {
                    text = key
                    textSize = 22f
                    gravity = Gravity.CENTER
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.ink))
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_card_sm)
                    layoutParams = LinearLayout.LayoutParams(0, btnHeight, 1f).also {
                        it.marginEnd = gap
                    }
                    setOnClickListener { onKeyTap(key) }
                }
                row.addView(btn)
            }

            b.keypadGrid.addView(row)
        }
    }

    private fun onKeyTap(key: String) {
        when (key) {
            "⌫" -> if (inputBuffer.isNotEmpty()) inputBuffer.deleteCharAt(inputBuffer.lastIndex)
            "C"  -> { inputBuffer.clear(); inputBuffer.append("1") }
            "."  -> {} // carrier Hz is always integer, ignore decimal
            else -> {
                // Prevent leading zeros and cap at 3 digits
                if (inputBuffer.toString() == "0") inputBuffer.clear()
                if (inputBuffer.length < 3) inputBuffer.append(key)
                else {
                    // Replace all — start fresh with this digit
                    inputBuffer.clear()
                    inputBuffer.append(key)
                }
            }
        }

        val hz = inputBuffer.toString().toDoubleOrNull() ?: AppConstants.CARRIER_HZ_MIN
        val clamped = hz.coerceIn(AppConstants.CARRIER_HZ_MIN, AppConstants.CARRIER_HZ_MAX)

        updateCarrierDisplay()
        applyGradient()
        updatePreviewCard()

        // Only push to ViewModel when value is valid
        if (hz == clamped && inputBuffer.isNotEmpty()) {
            homeVm.setCarrierHz(clamped)
        }
    }

    // ── Beat slider ───────────────────────────────────────────────────────────

    private fun setupBeatSlider() {
        // progress 0-99 maps to 0.5-50 Hz in 0.5 steps
        b.sliderBeat.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                currentBeatHz = 0.5 + progress * 0.5
                homeVm.setBeatHz(currentBeatHz)
                updateBeatDisplay()
                updatePreviewCard()
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })
        // Sync slider to current ViewModel beat Hz
        val initProgress = ((homeVm.beatHz.value - 0.5) / 0.5).toInt().coerceIn(0, 99)
        b.sliderBeat.progress = initProgress
        currentBeatHz = homeVm.beatHz.value
    }

    // ── Quick recall chips ────────────────────────────────────────────────────

    private data class Recall(val label: String, val carrierHz: Double?, val beatHz: Double?)

    private fun setupQuickRecall() {
        val recalls = listOf(
            Recall("Schumann · 7.83 Hz", null, 7.83),
            Recall("OM · 136 Hz", 136.0, null),
            Recall("Solfeggio · 417 Hz", 417.0, null),
            Recall("Delta · 2 Hz", null, 2.0)
        )
        val gap = resources.getDimensionPixelSize(R.dimen.spacing_sm)

        recalls.forEach { recall ->
            val chip = TextView(requireContext()).apply {
                text = recall.label
                textSize = 12f
                gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(requireContext(), R.color.ink_dim))
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chip)
                val hPad = resources.getDimensionPixelSize(R.dimen.spacing_md)
                val vPad = resources.getDimensionPixelSize(R.dimen.spacing_sm)
                setPadding(hPad, vPad, hPad, vPad)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.marginEnd = gap }
                setOnClickListener { applyRecall(recall) }
            }
            b.chipGroupRecall.addView(chip)
        }
    }

    private fun applyRecall(recall: Recall) {
        recall.carrierHz?.let { hz ->
            val clamped = hz.coerceIn(AppConstants.CARRIER_HZ_MIN, AppConstants.CARRIER_HZ_MAX)
            inputBuffer.clear()
            inputBuffer.append(clamped.toInt().toString())
            homeVm.setCarrierHz(clamped)
            updateCarrierDisplay()
            applyGradient()
        }
        recall.beatHz?.let { hz ->
            currentBeatHz = hz.coerceIn(0.5, 50.0)
            homeVm.setBeatHz(currentBeatHz)
            val progress = ((currentBeatHz - 0.5) / 0.5).toInt().coerceIn(0, 99)
            b.sliderBeat.progress = progress
            updateBeatDisplay()
        }
        updatePreviewCard()
    }

    // ── Display helpers ───────────────────────────────────────────────────────

    private fun updateCarrierDisplay() {
        val text = if (inputBuffer.isEmpty()) "0" else inputBuffer.toString()
        b.tvCarrierDisplay.text = text
    }

    private fun updateBeatDisplay() {
        b.tvBeatDisplay.text = "%.1f".format(currentBeatHz)
    }

    private fun updatePreviewCard() {
        val carrier = inputBuffer.toString().toDoubleOrNull()
            ?.coerceIn(AppConstants.CARRIER_HZ_MIN, AppConstants.CARRIER_HZ_MAX)
            ?: AppConstants.CARRIER_HZ_MIN
        val left = FrequencyUtils.leftHz(carrier, currentBeatHz)
        val right = FrequencyUtils.rightHz(carrier, currentBeatHz)
        val category = FrequencyUtils.beatHzToCategory(currentBeatHz)

        b.tvLeftHz.text = "%.1f".format(left)
        b.tvBeatPreview.text = "%.1f".format(currentBeatHz)
        b.tvRightHz.text = "%.1f".format(right)
        b.tvBandPreview.text = bandLabel(category)
    }

    private fun applyGradient() {
        val tv = b.tvCarrierDisplay
        if (tv.width == 0) return
        val gradient = LinearGradient(
            0f, 0f, tv.width.toFloat(), 0f,
            intArrayOf("#A594FF".toColorInt(), "#5EF0E3".toColorInt()),
            null,
            Shader.TileMode.CLAMP
        )
        tv.paint.shader = gradient
        tv.invalidate()
    }

    private fun bandLabel(category: WaveCategory): String = when (category) {
        WaveCategory.DELTA    -> "Delta  ·  0.5 – 4 Hz  ·  Sleep"
        WaveCategory.THETA    -> "Theta  ·  4 – 8 Hz  ·  Meditation"
        WaveCategory.ALPHA    -> "Alpha  ·  8 – 13 Hz  ·  Calm focus"
        WaveCategory.BETA     -> "Beta  ·  13 – 30 Hz  ·  Active focus"
        WaveCategory.GAMMA    -> "Gamma  ·  30+ Hz  ·  Peak cognition"
        WaveCategory.SPIRITUAL -> "Spiritual"
        WaveCategory.JOURNEY   -> "Journey  ·  multi-band  ·  Progressive"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
