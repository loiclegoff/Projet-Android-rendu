package com.tbuonomo.jawgmapsample;

/**
 * Created by leo on 26/10/17.
 */

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.home, container, false);

        //Getting components
        final TextView title = rootView.findViewById(R.id.title);

        //Loading fonts
        Typeface cabinSketch = Typeface.createFromAsset(getActivity().getAssets(), "CabinSketch.ttf");

        //Setting fonts
        title.setTypeface(cabinSketch);

        return rootView;
    }

    @Override public void onStart() {
        super.onStart();
    }

    @Override public void onResume() {
        super.onResume();
    }

    @Override public void onPause() {
        super.onPause();
    }

    @Override public void onStop() {
        super.onStop();
    }

    @Override public void onLowMemory() {
        super.onLowMemory();
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
