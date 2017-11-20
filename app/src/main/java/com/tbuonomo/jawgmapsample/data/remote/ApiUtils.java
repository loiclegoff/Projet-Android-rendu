package com.tbuonomo.jawgmapsample.data.remote;

/**
 * Created by llgle on 10/11/2017.
 */

public class ApiUtils {

    public static final String BASE_URL = "http://www.poketrav.me/";

    public static IPMService getIPMService() {
        return RetrofitClient.getClient(BASE_URL).create(IPMService.class);
    }
}