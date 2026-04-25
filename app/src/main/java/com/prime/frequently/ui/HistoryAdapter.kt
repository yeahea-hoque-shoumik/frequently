package com.prime.frequently.ui

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prime.frequently.R
import com.prime.frequently.data.SessionRecord
import com.prime.frequently.data.WaveCategory
import com.prime.frequently.databinding.ItemSessionBinding
import com.prime.frequently.utils.FrequencyUtils
import com.prime.frequently.utils.TimeUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<SessionRecord, HistoryAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<SessionRecord>() {
            override fun areItemsTheSame(a: SessionRecord, b: SessionRecord) = a.id == b.id
            override fun areContentsTheSame(a: SessionRecord, b: SessionRecord) = a == b
        }
        private val DATE_FMT = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    }

    class VH(val b: ItemSessionBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = getItem(position)
        val b = holder.b
        val ctx = holder.itemView.context

        val category = FrequencyUtils.beatHzToCategory(s.beatHz)
        val colorRes = bandHiColor(category)
        val circle = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(ContextCompat.getColor(ctx, colorRes))
        }
        b.orbDot.background = circle

        b.tvPresetName.text = s.presetName.ifEmpty { "Custom" }
        b.tvStatusBadge.visibility = if (s.completed) View.GONE else View.VISIBLE
        b.tvWhen.text = DATE_FMT.format(Date(s.startTime))
        b.tvBand.text = category.name.lowercase().replaceFirstChar { it.uppercase() }
        b.tvDuration.text = TimeUtils.secondsToMmSs(s.actualDurationSecs)
    }

    private fun bandHiColor(category: WaveCategory) = when (category) {
        WaveCategory.DELTA    -> R.color.band_delta_hi
        WaveCategory.THETA    -> R.color.band_theta_hi
        WaveCategory.ALPHA    -> R.color.band_alpha_hi
        WaveCategory.BETA     -> R.color.band_beta_hi
        WaveCategory.GAMMA    -> R.color.band_gamma_hi
        WaveCategory.SPIRITUAL -> R.color.violet
        WaveCategory.JOURNEY   -> R.color.violet_2
    }
}
