package com.droidteahouse.cooperhewitt.vo

import com.google.gson.annotations.SerializedName

data class ImagesItem(
    //@todo maybe need deserializer
    @SerializedName("n")
    val n: N

)