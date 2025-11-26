package com.hypersoft.baseproject.presentation.history.ui

import androidx.core.view.isVisible
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.presentation.databinding.FragmentHistoryBinding
import com.hypersoft.baseproject.presentation.history.adapter.HistoryAdapter
import com.hypersoft.baseproject.presentation.history.effect.HistoryEffect
import com.hypersoft.baseproject.presentation.history.state.HistoryState
import com.hypersoft.baseproject.presentation.history.viewModel.HistoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : BaseFragment<FragmentHistoryBinding>(FragmentHistoryBinding::inflate) {

    private val viewModel: HistoryViewModel by viewModel()
    private val adapter by lazy { HistoryAdapter() }

    override fun onViewCreated() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rcvListHistory.adapter = adapter
    }

    override fun initObservers() {
        observeState()
        observeEffect()
    }

    private fun observeState() {
        collectWhenStarted(viewModel.state) { state ->
            renderState(state)
        }
    }

    private fun observeEffect() {
        collectWhenStarted(viewModel.effect) { effect ->
            handleEffect(effect)
        }
    }

    private fun renderState(state: HistoryState) {
        binding.cpiLoadingHistory.isVisible = state.isLoading

        adapter.submitList(state.histories)
    }

    private fun handleEffect(effect: HistoryEffect) {
        when (effect) {
            is HistoryEffect.ShowError -> context?.showToast(effect.message)
        }
    }
}