package com.prime.frequently.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prime.frequently.data.SessionIntent
import com.prime.frequently.databinding.BottomSheetSessionIntentBinding
import com.prime.frequently.databinding.ItemIntentCardBinding
import com.prime.frequently.viewmodel.HomeViewModel

class SessionIntentBottomSheet : BottomSheetDialogFragment() {

    private val vm: HomeViewModel by activityViewModels()
    private var _b: BottomSheetSessionIntentBinding? = null
    private val b get() = _b!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = BottomSheetSessionIntentBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.rvIntents.layoutManager = LinearLayoutManager(requireContext())
        b.rvIntents.adapter = IntentAdapter(SessionIntent.entries.toList()) { intent ->
            vm.applyIntent(intent)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }

    // ── Inner adapter ──────────────────────────────────────────────────────────

    private inner class IntentAdapter(
        private val intents: List<SessionIntent>,
        private val onClick: (SessionIntent) -> Unit
    ) : RecyclerView.Adapter<IntentAdapter.VH>() {

        inner class VH(val binding: ItemIntentCardBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val b = ItemIntentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return VH(b)
        }

        override fun getItemCount() = intents.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val intent = intents[position]
            holder.binding.tvEmoji.text = intent.emoji
            holder.binding.tvIntentLabel.text = intent.label
            holder.binding.tvIntentDesc.text = intent.description
            holder.itemView.setOnClickListener { onClick(intent) }
        }
    }
}
