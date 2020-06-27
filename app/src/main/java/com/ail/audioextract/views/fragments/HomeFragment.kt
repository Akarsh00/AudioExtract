package com.ail.audioextract.views.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.ail.audioextract.HomeItemRecyclerViewAdapter
import com.ail.audioextract.R
import com.ail.audioextract.data.ITEMS
import com.ail.rocksvideoediting.data.HomeItem
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home), HomeItemRecyclerViewAdapter.Interaction {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycler_view.layoutManager = GridLayoutManager(context, 2)
        var listOfItems = mutableListOf<HomeItem>()
        listOfItems.add(HomeItem(getString(R.string.home_item_video_to_audio), ITEMS.VIDEO_TO_AUDIO, R.drawable.video_cutter, R.drawable.home_item_gradient_4))
        listOfItems.add(HomeItem("Video Cutter", ITEMS.VIDEO_TRIM, R.drawable.video_cutter, R.drawable.home_item_gradient_4))
        listOfItems.add(HomeItem(getString(R.string.home_item_audio_cutter), ITEMS.AUDIO_CUTTER, R.drawable.audio_cutter, R.drawable.home_item_gradient_2))
        listOfItems.add(HomeItem(getString(R.string.home_item_audio_merger), ITEMS.AUDIO_MERGER, R.drawable.audio_cutter, R.drawable.home_item_gradient_2))
        listOfItems.add(HomeItem(getString(R.string.home_item_output_folder), ITEMS.OUTPUT_FOLDER, R.drawable.ic_audio_merger, R.drawable.home_item_gradient_1))
        listOfItems.add(HomeItem(getString(R.string.home_item_gifts), ITEMS.GIFTS, R.drawable.ic_audio_merger, R.drawable.home_item_gradient_1))
        recycler_view.adapter = HomeItemRecyclerViewAdapter(this).also { it.submitList(listOfItems) }
    }

    override fun onItemSelected(position: Int, item: HomeItem) {
        if (item.homeEventItem == ITEMS.VIDEO_TO_AUDIO) {
            val action =
                    HomeFragmentDirections.actionHomeFragmentToAllVideosFragment(
                            eventType = item.homeEventItem.events)
            Navigation.findNavController(requireView()).navigate(action)
        } else if (item.homeEventItem == ITEMS.VIDEO_TRIM) {
            val action =
                    HomeFragmentDirections.actionHomeFragmentToAllVideosFragment(
                            eventType = item.homeEventItem.events)
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

}