package com.wtsystems.hallothere.api;

import com.wtsystems.hallothere.Model.NotificationData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationService {


    @Headers({
            "Authorization:key=AAAALWEHifA:APA91bGlQWrkmh42lEDljsBIc8hh-E3sF7isHqM6txHWGPnmnSxlbOgDYL4h7_cx6OcYHMi-59slwpiSyjO26UMwLxsZiWHI7HE8OBFFw3zpXgLhjkPN2Je8Gt1mbCY8EJ59mDT1-2be",
            "Content-Type:application/json"
    })
    @POST("send")
    Call<NotificationData> saveNotification(@Body NotificationData notificationData);

}
