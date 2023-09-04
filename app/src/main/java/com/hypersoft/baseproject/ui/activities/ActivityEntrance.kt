package com.hypersoft.baseproject.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.ActivityEntranceBinding
import com.hypersoft.baseproject.ui.activities.base.BaseActivity

class ActivityEntrance : BaseActivity<ActivityEntranceBinding>(R.layout.activity_entrance) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkCaseType()
    }

    private fun checkCaseType() {
        navigateScreen()
    }

    fun nextActivity(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateScreen() {
        val navController = (supportFragmentManager.findFragmentById(binding.fcvContainerEntrance.id) as NavHostFragment).navController
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.nav_graph_entrance)
        if (diComponent.sharedPreferenceUtils.showFirstScreen) {
            navGraph.setStartDestination(R.id.fragmentEntranceStart)
        }else{
            navGraph.setStartDestination(R.id.fragmentEntrance)
        }
        navController.graph = navGraph
    }

    override fun onRecreate() {}
}