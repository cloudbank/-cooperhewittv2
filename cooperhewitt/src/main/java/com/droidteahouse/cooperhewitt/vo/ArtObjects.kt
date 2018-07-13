package com.droidteahouse.cooperhewitt.vo

import com.google.gson.annotations.SerializedName

data class ArtObjects(@SerializedName("per_page")
                      val perPage: Int = 0,
                      @SerializedName("total")
                      val total: Int = 0,
                      @SerializedName("stat")
                      val stat: String = "",
                      @SerializedName("pages")
                      val pages: Int = 0,
                      @SerializedName("objects")
                      var objects: List<ArtObject>?,
                      @SerializedName("page")
                      val page: Int = 0)