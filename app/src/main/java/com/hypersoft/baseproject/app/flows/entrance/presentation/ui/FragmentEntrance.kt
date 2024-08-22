package com.hypersoft.baseproject.app.flows.entrance.presentation.ui

import androidx.fragment.app.viewModels
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.app.flows.entrance.presentation.viewModel.ViewModelEntrance
import com.hypersoft.baseproject.databinding.FragmentEntranceBinding
import com.hypersoft.baseproject.utilities.base.fragment.BaseFragment
import com.hypersoft.baseproject.utilities.extensions.navigateTo

class FragmentEntrance : BaseFragment<FragmentEntranceBinding>(FragmentEntranceBinding::inflate) {

    private val viewModel by viewModels<ViewModelEntrance>()

    override fun onViewCreated() {
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        //binding.lavAnimationEntrance.resumeAnimation()
    }

    override fun onPause() {
        super.onPause()
        //binding.lavAnimationEntrance.pauseAnimation()
    }


    private fun initObserver() {
        viewModel.navigateLiveData.observe(viewLifecycleOwner) {
            navigateScreen()
        }
    }

    private fun navigateScreen() {
        navigateTo(R.id.fragmentEntrance, R.id.action_fragmentEntrance_to_fragmentLanguage)
    }
}