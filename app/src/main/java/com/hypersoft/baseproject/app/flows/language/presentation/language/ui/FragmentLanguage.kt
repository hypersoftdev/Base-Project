package com.hypersoft.baseproject.app.flows.language.presentation.language.ui

import androidx.fragment.app.viewModels
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.app.flows.language.domain.usecases.UseCaseLanguage
import com.hypersoft.baseproject.app.flows.language.presentation.language.viewmodels.ViewModelLanguage
import com.hypersoft.baseproject.app.flows.language.presentation.language.viewmodels.ViewModelLanguageFactory
import com.hypersoft.baseproject.databinding.FragmentLanguageBinding
import com.hypersoft.baseproject.app.flows.language.data.repository.RepositoryLanguage
import com.hypersoft.baseproject.app.flows.language.presentation.language.adapter.AdapterLanguage
import com.hypersoft.baseproject.utilities.base.BaseFragment
import com.hypersoft.baseproject.utilities.extensions.navigateTo

class FragmentLanguage : BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {

    private val adapter by lazy { AdapterLanguage(itemClick) }

    // MVVM
    private val repositoryLanguage = RepositoryLanguage()
    private val useCaseLanguage = UseCaseLanguage(repositoryLanguage)
    private val viewModel by viewModels<ViewModelLanguage> { ViewModelLanguageFactory(useCaseLanguage) }

    override fun onViewCreated() {
        initRecyclerView()
        initObservers()
        fetchData()

        binding.mbContinueLanguage.setOnClickListener { applyLanguage() }
    }

    private fun initRecyclerView() {
        binding.rvListLanguage.adapter = adapter
    }

    private fun initObservers() {
        viewModel.languageLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.appliedLiveData.observe(viewLifecycleOwner) {
            navigateTo(R.id.fragmentLanguage, R.id.action_fragmentLanguage_to_fragmentDashboard)
        }
    }

    private fun fetchData() {
        val appliedLanguageCode = globalContext.resources.configuration.locales.get(0).language
        viewModel.fetchLanguages(appliedLanguageCode)
    }

    private fun applyLanguage() {
        viewModel.applyLanguage()
    }

    private val itemClick: (selectedCode: String) -> Unit = {
        viewModel.updateLanguage(selectedCode = it)
    }
}