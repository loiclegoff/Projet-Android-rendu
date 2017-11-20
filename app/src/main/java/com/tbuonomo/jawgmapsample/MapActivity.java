package com.tbuonomo.jawgmapsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
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
import retrofit2.Response;
import retrofit2.Callback;

public class MapActivity extends AppCompatActivity {

  private MapView mapView;
  private String token = "89f0a3beaf5a79a826155f9c21fe64fd5407c5a7600c2de30bc4f1f681f73646";
  private Double lat = 45.784263; //69.0;
  private Double lon = 4.86975; //51.0;
  private Double dist = 100.0;
  private IPMService mIPMService;
  private List<Picture> Pictures;

  private static final String TAG = "MyActivity";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Mapbox.getInstance(this, BuildConfig.MAP_BOX_TOKEN);
    setContentView(R.layout.map_layout);
    mapView = findViewById(R.id.mapView);
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

   /* mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(MapboxMap mapboxMap) {
        mapboxMap.addMarker(new MarkerViewOptions()
                .position(new LatLng(45.784263,4.86975))
                .title("Title")
                .snippet("Author")
        );
        // Customize map with markers, polylines, etc.

      }
    });*/
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

  @Override protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
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

          //List<Picture> Pictures = response.body().getPictures();




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

//    List<Picture> Pictures = response.getPictures();
    Pictures = response.getPictures();


    for (Picture picture : Pictures) {
      Log.d(TAG, "response: "+ picture);
    }
  }
}
