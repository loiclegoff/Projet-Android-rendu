package com.tbuonomo.jawgmapsample;

/**
 * Created by leo on 26/10/17.
 */

import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.tbuonomo.jawgmapsample.data.model.IPMAnswersResponse;
import com.tbuonomo.jawgmapsample.data.model.Picture;
import com.tbuonomo.jawgmapsample.data.remote.ApiUtils;
import com.tbuonomo.jawgmapsample.data.remote.IPMService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MapFragment extends Fragment {

    private MapView mapView;
    private String token = "89f0a3beaf5a79a826155f9c21fe64fd5407c5a7600c2de30bc4f1f681f73646";
    private Double lat = 45.784263; //69.0;
    private Double lon = 4.86975; //51.0;
    private Double dist = 100.0;
    private IPMService mIPMService;
    private List<Picture> Pictures;

    private static final String TAG = "MapFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.map_layout, container, false);

        Mapbox.getInstance(this.getContext(), BuildConfig.MAP_BOX_TOKEN);
        mapView = rootView.findViewById(R.id.mapView);
        mapView.setStyleUrl("https://tile.jawg.io/jawg-streets.json?access-token=" + BuildConfig.JAWG_API_KEY);
        mapView.onCreate(savedInstanceState);

        mIPMService = ApiUtils.getIPMService();

        sendPost(token,lat,lon,dist);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
            for (final Picture picture : Pictures) {
                Log.d(TAG, "response: "+picture.getTitle()+" -- lat:"+ picture.getLat()+ ",lon: " +picture.getLon());
                mapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(picture.getLat(),picture.getLon()))
                    .title(picture.getTitle())
                    .snippet("Author")
                );
            }
            }
        });

        return rootView;
    }

    @Override public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void sendPost(String token, Double lat, Double lon, Double dist) {
        mIPMService.getAnswers(token,lat,lon,dist).enqueue(new Callback<IPMAnswersResponse>() {

            @Override
            public void onResponse(Call<IPMAnswersResponse> call, Response<IPMAnswersResponse> response) {
                if(response.isSuccessful()) {
                    showResponse(response.body());
                    Log.i(TAG, "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<IPMAnswersResponse> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API.");
            }
        });
    }

    public void showResponse(IPMAnswersResponse response) {
        Log.d(TAG, "response: "+ response);

        Pictures = response.getPictures();

        for (Picture picture : Pictures) {
            Log.d(TAG, "response: "+ picture);
        }
    }
}
