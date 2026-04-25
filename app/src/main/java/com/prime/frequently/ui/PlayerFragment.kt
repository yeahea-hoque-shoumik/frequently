package com.prime.frequently.ui

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.prime.frequently.R
import com.prime.frequently.constants.AppConstants
import com.prime.frequently.databinding.FragmentPlayerBinding
import com.prime.frequently.utils.TimeUtils
import com.prime.frequently.viewmodel.HomeViewModel
import com.prime.frequently.viewmodel.SessionEvent
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {

    private val vm: HomeViewModel by activityViewModels()
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

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        b.btnBack.setOnClickListener { findNavController().navigateUp() }

        b.btnMore.setOnClickListener {
            SessionIntentBottomSheet().show(parentFragmentManager, "intent")
        }

        b.btnPlayPause.setOnClickListener {
            // Warn on play start if headphones are not connected (and pref is enabled).
            if (!vm.isPlaying.value) {
                val warnEnabled = prefs.getBoolean(AppConstants.PREF_HEADPHONE_WARNING, true)
                if (warnEnabled && !areHeadphonesConnected()) {
                    Snackbar.make(b.root, R.string.snackbar_no_headphones, Snackbar.LENGTH_LONG).show()
                }
            }
            vm.togglePlayPause()
        }

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
                        // Keep screen on while playing if pref is enabled.
                        val keepOn = prefs.getBoolean(AppConstants.PREF_KEEP_SCREEN_ON, true)
                        if (playing && keepOn) {
                            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        } else {
                            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        }
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
                        b.tvDuration.text = if (secs > 0) "-${TimeUtils.secondsToMmSs(secs)}" else "∞"
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

    override fun onStop() {
        super.onStop()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }

    private fun areHeadphonesConnected(): Boolean {
        val am = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any {
            it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
            it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
        }
    }
}
