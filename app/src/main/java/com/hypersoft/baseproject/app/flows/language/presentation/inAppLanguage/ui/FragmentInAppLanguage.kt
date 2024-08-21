package com.hypersoft.baseproject.app.flows.language.presentation.inAppLanguage.ui

import androidx.fragment.app.viewModels
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.app.flows.language.data.repository.RepositoryLanguage
import com.hypersoft.baseproject.app.flows.language.domain.usecases.UseCaseInAppLanguage
import com.hypersoft.baseproject.app.flows.language.presentation.inAppLanguage.adapter.AdapterInAppLanguage
import com.hypersoft.baseproject.app.flows.language.presentation.inAppLanguage.viewmodels.ViewModelInAppLanguage
import com.hypersoft.baseproject.app.flows.language.presentation.inAppLanguage.viewmodels.ViewModelInAppLanguageFactory
import com.hypersoft.baseproject.databinding.FragmentInAppLanguageBinding
import com.hypersoft.baseproject.utilities.base.BaseFragment
import com.hypersoft.baseproject.utilities.extensions.popFrom
import com.hypersoft.baseproject.utilities.extensions.showToast

class FragmentInAppLanguage : BaseFragment<FragmentInAppLanguageBinding>(FragmentInAppLanguageBinding::inflate) {

    private val adapter by lazy { AdapterInAppLanguage(itemClick) }

    // MVVM
    private val repositoryLanguage = RepositoryLanguage()
    private val useCaseInAppLanguage = UseCaseInAppLanguage(repositoryLanguage)
    private val viewModel by viewModels<ViewModelInAppLanguage> { ViewModelInAppLanguageFactory(useCaseInAppLanguage) }

    override fun onViewCreated() {
        initObservers()

        binding.mtbToolbarInAppLanguage.setNavigationOnClickListener { popFrom(R.id.fragmentInAppLanguage) }
        binding.mbContinueInAppLanguage.setOnClickListener { applyLanguage() }
    }

    private fun initObservers() {
        binding.rvListInAppLanguage.adapter = adapter
        viewModel.languageLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.appliedLiveData.observe(viewLifecycleOwner) { isApplied ->
            when (isApplied) {
                true -> popFrom(R.id.fragmentInAppLanguage)
                false -> context.showToast(R.string.select_distinct_language)
            }
        }
    }

    private fun applyLanguage() {
        viewModel.applyLanguage()
    }

    private val itemClick: (selectedCode: String) -> Unit = {
        viewModel.updateLanguage(selectedCode = it)
    }

}