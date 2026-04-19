package com.prime.frequently.ui

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prime.frequently.R
import com.prime.frequently.data.WaveCategory
import com.prime.frequently.data.WavePreset
import com.prime.frequently.databinding.ItemPresetCardBinding

class WavePresetAdapter(
    private val onClick: (WavePreset) -> Unit
) : ListAdapter<WavePreset, WavePresetAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemPresetCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPresetCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val preset = getItem(position)
        holder.binding.root.setOnClickListener { onClick(preset) }
        val ctx = holder.itemView.context
        val b = holder.binding

        val (hiColor, loColor, accentColor) = bandColors(preset.category)
        val hi = ContextCompat.getColor(ctx, hiColor)
        val lo = ContextCompat.getColor(ctx, loColor)
        val accent = ContextCompat.getColor(ctx, accentColor)

        val gd = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(hi, lo))
        gd.cornerRadius = ctx.resources.getDimension(R.dimen.radius_card_sm)
        b.thumbnail.background = gd

        b.tvBandHz.text = "${preset.category.name} · ${"%.1f".format(preset.beatHz)} Hz"
        b.tvBandHz.setTextColor(accent)
        b.tvName.text = preset.name
        b.tvDuration.text = "${preset.recommendedDurationMin} min"
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<WavePreset>() {
            override fun areItemsTheSame(a: WavePreset, b: WavePreset) = a.id == b.id
            override fun areContentsTheSame(a: WavePreset, b: WavePreset) = a == b
        }

        // Returns (hiColorRes, loColorRes, accentColorRes)
        fun bandColors(category: WaveCategory): Triple<Int, Int, Int> = when (category) {
            WaveCategory.DELTA    -> Triple(R.color.band_delta_hi, R.color.band_delta_lo, R.color.violet)
            WaveCategory.THETA    -> Triple(R.color.band_theta_hi, R.color.band_theta_lo, R.color.violet_2)
            WaveCategory.ALPHA    -> Triple(R.color.band_alpha_hi, R.color.band_alpha_lo, R.color.cyan_accent)
            WaveCategory.BETA     -> Triple(R.color.band_beta_hi,  R.color.band_beta_lo,  R.color.cyan_accent)
            WaveCategory.GAMMA    -> Triple(R.color.band_gamma_hi, R.color.band_gamma_lo, R.color.amber_accent)
            WaveCategory.SPIRITUAL -> Triple(R.color.band_theta_hi, R.color.band_theta_lo, R.color.magenta_accent)
        }
    }
}
