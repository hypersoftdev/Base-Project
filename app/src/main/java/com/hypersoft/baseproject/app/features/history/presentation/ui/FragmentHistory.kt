package com.hypersoft.baseproject.app.features.history.presentation.ui

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hypersoft.baseproject.utilities.base.fragment.BaseFragment
import com.hypersoft.baseproject.databinding.FragmentHistoryBinding
import com.hypersoft.baseproject.app.features.history.data.repository.RepositoryHistory
import com.hypersoft.baseproject.app.features.history.domain.usecases.UseCaseHistory
import com.hypersoft.baseproject.app.features.history.presentation.adapter.AdapterHistory
import com.hypersoft.baseproject.app.features.history.presentation.viewmodels.ViewModelHistory
import com.hypersoft.baseproject.app.features.history.presentation.viewmodels.ViewModelHistoryFactory

class FragmentHistory : BaseFragment<FragmentHistoryBinding>(FragmentHistoryBinding::inflate) {

    private val adapter by lazy { AdapterHistory() }

    // MVVM
    private val repositoryHistory = RepositoryHistory()
    private val useCaseHistory = UseCaseHistory(repositoryHistory)
    private val viewModel by viewModels<ViewModelHistory> { ViewModelHistoryFactory(useCaseHistory) }

    override fun onViewCreated() {
        initObservers()
    }

    private fun initObservers() {
        binding.rvListHistory.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        binding.rvListHistory.adapter = adapter
        viewModel.historyLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}