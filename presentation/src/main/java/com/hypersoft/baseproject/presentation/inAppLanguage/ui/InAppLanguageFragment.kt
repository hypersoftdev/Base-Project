package com.hypersoft.baseproject.presentation.inAppLanguage.ui

import androidx.core.view.isVisible
import com.hypersoft.baseproject.core.extensions.collectWhenCreated
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.base.fragment.BaseFragment
import com.hypersoft.baseproject.presentation.databinding.FragmentInAppLanguageBinding
import com.hypersoft.baseproject.presentation.inAppLanguage.adapter.InAppLanguageAdapter
import com.hypersoft.baseproject.presentation.inAppLanguage.effect.InAppLanguageEffect
import com.hypersoft.baseproject.presentation.inAppLanguage.intent.InAppLanguageIntent
import com.hypersoft.baseproject.presentation.inAppLanguage.state.InAppLanguageState
import com.hypersoft.baseproject.presentation.inAppLanguage.viewModel.InAppLanguageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class InAppLanguageFragment : BaseFragment<FragmentInAppLanguageBinding>(FragmentInAppLanguageBinding::inflate) {

    private val adapter by lazy { InAppLanguageAdapter() }
    private val viewModel: InAppLanguageViewModel by viewModel()

    override fun onViewCreated() {
        initRecyclerView()

        binding.mtbToolbarInAppLanguage.setNavigationOnClickListener { viewModel.handleIntent(InAppLanguageIntent.NavigateBack) }
        binding.mbContinueInAppLanguage.setOnClickListener { viewModel.handleIntent(InAppLanguageIntent.ApplyLanguage) }
    }

    private fun initRecyclerView() {
        binding.rcvListInAppLanguage.adapter = adapter
    }

    override fun initObservers() {
        collectWhenStarted(viewModel.state) { renderState(it) }
        collectWhenCreated(viewModel.effect) { handleEffect(it) }
    }

    private fun renderState(state: InAppLanguageState) {
        binding.cpiLoadingInAppLanguage.isVisible = state.isLoading
        adapter.submitList(state.languages)
    }

    private fun handleEffect(effect: InAppLanguageEffect) {
        when (effect) {
            is InAppLanguageEffect.NavigateBack -> popFrom(R.id.inAppLanguageFragment)
            is InAppLanguageEffect.ShowError -> context?.showToast(effect.message)
        }
    }
}