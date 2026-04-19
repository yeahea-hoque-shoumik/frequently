package com.prime.frequently.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.prime.frequently.R
import com.prime.frequently.constants.AppConstants
import com.prime.frequently.databinding.FragmentPlayerBinding
import com.prime.frequently.utils.TimeUtils
import com.prime.frequently.viewmodel.HomeViewModel
import com.prime.frequently.viewmodel.SessionEvent
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {

    private val vm: HomeViewModel by viewModels()
    private var _b: FragmentPlayerBinding? = null
    private val b get() = _b!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentPlayerBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val carrierRange = (AppConstants.CARRIER_HZ_MAX - AppConstants.CARRIER_HZ_MIN).toInt()
        b.sliderCarrier.max = carrierRange

        b.btnPlayPause.setOnClickListener { vm.togglePlayPause() }

        b.sliderCarrier.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) vm.setCarrierHz(AppConstants.CARRIER_HZ_MIN + progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })

        b.sliderVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) vm.setVolume(progress / 100f)
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.isPlaying.collect { playing ->
                        b.btnPlayPause.setImageResource(
                            if (playing) R.drawable.ic_pause else R.drawable.ic_play
                        )
                    }
                }
                launch {
                    vm.carrierHz.collect { hz ->
                        b.tvCarrierHz.text = "%.0f Hz".format(hz)
                        if (!b.sliderCarrier.isPressed) {
                            b.sliderCarrier.progress =
                                (hz - AppConstants.CARRIER_HZ_MIN).toInt().coerceIn(0, carrierRange)
                        }
                    }
                }
                launch {
                    vm.beatHz.collect { hz ->
                        b.tvBeatHz.text = "%.1f".format(hz)
                    }
                }
                launch {
                    vm.volume.collect { vol ->
                        b.tvVolumePct.text = "${(vol * 100).toInt()}%"
                        if (!b.sliderVolume.isPressed) {
                            b.sliderVolume.progress = (vol * 100).toInt()
                        }
                    }
                }
                launch {
                    vm.elapsedSeconds.collect { secs ->
                        b.tvElapsed.text = TimeUtils.secondsToMmSs(secs)
                        val total = vm.durationSeconds.value
                        if (total > 0) {
                            b.seekbarProgress.progress = ((secs * 100) / total).coerceIn(0, 100)
                        }
                    }
                }
                launch {
                    vm.remainingSeconds.collect { secs ->
                        b.tvDuration.text = if (secs > 0) "-${TimeUtils.secondsToMmSs(secs)}" else "\u221e"
                    }
                }
                launch {
                    vm.events.collect { event ->
                        when (event) {
                            is SessionEvent.TimerCompleted ->
                                Toast.makeText(requireContext(), "Session complete!", Toast.LENGTH_SHORT).show()
                            is SessionEvent.Stopped -> {}
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
