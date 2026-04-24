package com.prime.frequently.ui

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.prime.frequently.R
import com.prime.frequently.databinding.FragmentHistoryBinding
import com.prime.frequently.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryFragment : Fragment() {

    private var _b: FragmentHistoryBinding? = null
    private val b get() = _b!!

    private val vm: HistoryViewModel by viewModels()
    private val adapter = HistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentHistoryBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupBarChart()
        setupListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.sessions.collect { sessions ->
                        adapter.submitList(sessions)
                    }
                }
                launch {
                    vm.stats.collect { stats ->
                        b.tvTotalSessions.text = stats.totalSessions.toString()
                        b.tvTotalMinutes.text = stats.totalMinutes.toString()
                        b.tvStreakDays.text = stats.streakDays.toString()
                        updateBarChart(stats.weekCounts)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        b.sessionList.layoutManager = LinearLayoutManager(requireContext())
        b.sessionList.adapter = adapter

        val swipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                val session = adapter.currentList[vh.adapterPosition]
                vm.delete(session.id)
            }

            override fun onChildDraw(
                c: Canvas, rv: RecyclerView, vh: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isActive: Boolean
            ) {
                val itemView = vh.itemView
                val dangerColor = ContextCompat.getColor(requireContext(), R.color.danger)
                c.save()
                c.clipRect(itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                c.drawColor(dangerColor)
                c.restore()
                super.onChildDraw(c, rv, vh, dX, dY, actionState, isActive)
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(b.sessionList)
    }

    private fun setupBarChart() {
        val chart = b.barChart
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        chart.description.isEnabled = false
        chart.setMaxVisibleValueCount(7)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        chart.setBackgroundColor(Color.TRANSPARENT)
        chart.legend.isEnabled = false

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.ink_mute)
        xAxis.textSize = 10f

        chart.axisLeft.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.setTouchEnabled(false)
    }

    private fun updateBarChart(weekCounts: List<Int>) {
        val dayFmt = SimpleDateFormat("EEE", Locale.getDefault())
        val cal = Calendar.getInstance()
        val labels = (6 downTo 0).map { daysAgo ->
            cal.timeInMillis = System.currentTimeMillis() - daysAgo * 86_400_000L
            dayFmt.format(cal.time)
        }

        val entries = weekCounts.mapIndexed { i, count -> BarEntry(i.toFloat(), count.toFloat()) }
        val violetColor = ContextCompat.getColor(requireContext(), R.color.violet)
        val dataSet = BarDataSet(entries, "").apply {
            color = violetColor
            setDrawValues(false)
        }

        b.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        b.barChart.data = BarData(dataSet).apply { barWidth = 0.6f }
        b.barChart.invalidate()
    }

    private fun setupListeners() {
        b.tvClearAll.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear all sessions?")
                .setMessage("This cannot be undone.")
                .setPositiveButton("Clear") { _, _ -> vm.deleteAll() }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
