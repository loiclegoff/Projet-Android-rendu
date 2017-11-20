package com.tbuonomo.jawgmapsample.data.remote;

/**
 * Created by llgle on 10/11/2017.
 */

import com.tbuonomo.jawgmapsample.data.model.IPMAnswersResponse;
import com.tbuonomo.jawgmapsample.data.model.IPMLoginResponse;

import java.util.List;
import retrofit2.Call;
// import retrofit2.http.GET;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IPMService {

    @POST("/ipicmap.php?a=get_pictures")
    @FormUrlEncoded
    Call<IPMAnswersResponse> getAnswers(@Field("token") String token, @Field("lat") Double lat,  @Field("lon") Double lon, @Field("dist") Double dist);
    //Call<IPMAnswersResponse> getPictureByCoordinates(@Field("token") String token, @Field("lat") Double lat,  @Field("lon") Double lon, @Field("dist") Double dist);

   /* @POST("/ipicmap.php")
    @FormUrlEncoded
    Call<IPMLoginResponse> getToken(@Field("username") String username, @Field("pwd") String pwd);*/
/*    @GET("/answers?order=desc&sort=activity&site=stackoverflow")
    Call<IPMAnswersResponse> getAnswers(@Query("tagged") String tags);*/
}