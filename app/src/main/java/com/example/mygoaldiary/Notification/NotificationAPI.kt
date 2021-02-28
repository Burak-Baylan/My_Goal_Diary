package com.example.mygoaldiary.Notification

import com.example.mygoaldiary.Notification.Constants.Companion.CONTENT_TYPE
import com.example.mygoaldiary.Notification.Constants.Companion.SERVER_KEY
import com.squareup.okhttp.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification : PushNotificationData
    ) : Response<ResponseBody>

}