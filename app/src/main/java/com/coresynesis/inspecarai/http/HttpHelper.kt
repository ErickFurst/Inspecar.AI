package com.coresynesis.inspecarai.http


import okhttp3.OkHttpClient
import okhttp3.Request


class HttpHelper {
    fun getPredicaoVal() {
        val URL =
            "http://127.0.0.1:5000/csycardamage/https://www.nerdwallet.com/assets/blog/wp-content/uploads/2015/12/exterior-car-damage-384x233.jpg"

        val client = OkHttpClient()
        val request = Request.Builder().url(URL).get().build()
        val response = client.newCall(request).execute()

        val responseBody = response.body
        if (responseBody != null) {
            var json = responseBody.string()
            println(json)
        }
    }

}