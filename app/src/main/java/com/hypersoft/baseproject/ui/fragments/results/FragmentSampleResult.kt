package com.hypersoft.baseproject.ui.fragments.results

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentSampleResultBinding
import com.hypersoft.baseproject.ui.activities.SampleResult
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentSampleResult : BaseFragment<FragmentSampleResultBinding>(R.layout.fragment_sample_result) {

    override fun onViewCreatedOneTime() {
        binding.mbCounterSampleResult.setOnClickListener { askForCounter() }
    }

    override fun onViewCreatedEverytime() {}

    private fun askForCounter() {
        val intent = Intent(requireActivity(), SampleResult::class.java)
        requestActivityResult.launch(intent)
    }
    /* ----------- Apis ----------- */

    private val requestActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val result = it.data
            binding.mtvResultSampleResult.text = result?.getStringExtra("text").toString()
        }
    }
}