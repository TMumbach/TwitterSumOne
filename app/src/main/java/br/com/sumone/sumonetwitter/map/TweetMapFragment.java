package br.com.sumone.sumonetwitter.map;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sumone.sumonetwitter.R;
import br.com.sumone.sumonetwitter.profile.ProfileActivity;
import br.com.sumone.sumonetwitter.utils.TwitterController;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * Created by Tiago on 07/08/2015.
 */
public class TweetMapFragment extends Fragment implements GoogleMap.OnMarkerClickListener{

    private SupportMapFragment fragment;
    private GoogleMap map;

    private Map<Integer, Long> markers;

    public static TweetMapFragment newInstance() {
        return new TweetMapFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tweet_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        markers = new HashMap<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (map == null) {
            map = fragment.getMap();
            map.setOnMarkerClickListener(this);
        }

        new LoadTweetMap().execute();
    }

    class LoadTweetMap extends AsyncTask<String, String, List<Status>> {

        @Override
        protected List<twitter4j.Status> doInBackground(String... strings) {
            try {
                return TwitterController.getInstance().getTwitter().getHomeTimeline();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            createMarkers(statuses);
        }
    }

    private void createMarkers(List<twitter4j.Status> statuses) {
        for (Status status : statuses) {
            if(status.getPlace() != null) {
                String place = status.getPlace().getFullName() + " - " + status.getPlace().getCountry();
                LatLng latLng = getLocationFromAddress(getActivity(), place);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).snippet("@" + status.getUser().getScreenName()).title(status.getUser().getName());
                loadImage(markerOptions, status.getUser().getBiggerProfileImageURL(), status.getUser().getId());
            }
        }
    }

    private void loadImage(MarkerOptions markerOptions, String url, final Long userId) {
        PicassoMarker marker = new PicassoMarker(markerOptions);
        marker.setOnImageLoaded(new PicassoMarker.OnImageLoaded() {
            @Override
            public void imageLoaded(MarkerOptions markerOptions) {
                addMarker(markerOptions, userId);
            }
        });
        Picasso.with(getActivity()).load(url).into(marker);
    }

    private void addMarker(MarkerOptions marker, Long userId) {
        markers.put(marker.getSnippet().hashCode(), userId);
        map.addMarker(marker);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Long userId = markers.get(marker.getSnippet().hashCode());
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.ARG_USER_ID, userId);
        startActivity(intent);
        return false;
    }

    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }
}
