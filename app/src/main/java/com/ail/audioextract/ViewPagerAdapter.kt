package com.ail.audioextract

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ail.audioextract.views.fragments.SavedAudioListFragments
import com.ail.audioextract.views.fragments.SavedEditedVideosFragment


class ViewPagerAdapter(context: Context, fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val tabTitles = arrayOf(context.getString(R.string.label_video_to_audio), context.getString(R.string.label_video_cutter), context.getString(R.string.label_audio_cutter), context.getString(R.string.label_audio_merger))

    private val fragmentsArray = arrayOf(SavedAudioListFragments(),
            SavedEditedVideosFragment.newInstance(SAVED_EDITED_MEDIA),
            SavedAudioListFragments(),
            SavedAudioListFragments())

    override fun getItem(position: Int): Fragment = fragmentsArray[position]

    override fun getCount(): Int = fragmentsArray.size

    override fun getPageTitle(position: Int): CharSequence? = tabTitles[position]
}