package com.droidteahouse.cooperhewitt.vo

import com.google.gson.annotations.SerializedName

data class Sq(@SerializedName("is_primary")
              val isPrimary: String = "",
              @SerializedName("width")
              val width: Int = 0,
              @SerializedName("image_id")
              val imageId: String = "",
              @SerializedName("url")
              val url: String = "",
              @SerializedName("height")
              val height: Int = 0)