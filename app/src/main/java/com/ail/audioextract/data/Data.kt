package com.ail.rocksvideoediting.data

import com.ail.audioextract.data.ITEMS

data class HomeItem(var title:String, var homeEventItem: ITEMS, var imageDrawable:Int, var backgroundGradientDrawable:Int)
data class AudioModel(
        var aPath: String,
        var aName: String,
        var aAlbum: String,
        var aArtist: String)
