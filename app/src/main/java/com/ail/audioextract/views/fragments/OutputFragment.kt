package com.ail.audioextract.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ail.audioextract.R
import com.ail.audioextract.ViewPagerAdapter
import com.ail.audioextract.views.activity.HomeAppActivity
import kotlinx.android.synthetic.main.fragment_output.*


class OutputFragment : Fragment(R.layout.fragment_output) {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var fragmentManager=(activity as HomeAppActivity).supportFragmentManager
        var viewPagerAdapter =  ViewPagerAdapter(requireContext(),fragmentManager);
        viewPager.adapter = viewPagerAdapter;
        tab_layout.setupWithViewPager(viewPager);

    }
}