package com.coresynesis.inspecarai.http

import com.coresynesis.inspecarai.models.PredicaoModel
//import com.coresynesis.inspecarai.models.TesteModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface Methods  {

    @Multipart
    @POST("ivi/")
    fun uploadImage(
        @Part part: MultipartBody.Part,
        @Part("image") requestBody: RequestBody?
    ): Call<PredicaoModel>

}



