package apps.codette.geobuy;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import apps.codette.forms.Banner;
import apps.codette.forms.Category;
import apps.codette.forms.CategoryMaster;
import apps.codette.forms.Organization;
import apps.codette.geobuy.Constants.GeobuyConstants;
import apps.codette.geobuy.adapters.BannerAdapter;
import apps.codette.geobuy.adapters.BannerPagerAdapter;
import apps.codette.geobuy.adapters.CategoryAdapter;
import apps.codette.geobuy.adapters.CategoryMasterAdapter;
import apps.codette.geobuy.adapters.MyCustomPagerAdapter;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;
import me.relex.circleindicator.CircleIndicator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressDialog pd;
    private OnFragmentInteractionListener mListener;
    LinearLayout home_ll;
    List<Banner> banners;

    Switch home_view_switch;
    TextView distance_text;
    private int distance =5;
    DiscreteSeekBar filter_by_distance;
    LinearLayout distance_filter_view;

    ImageView location_settings;
    SessionManager sessionManager;
    TextView location_text;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 1000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 4000;

    MainActivity mainActivity;
    View rootView;
    Location location;
    LocationManager locationManager;
    Map<String,?> details;
    Dialog settingsDialog;
    String lat, lon;

    private LinearLayout no_offers;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sessionManager = new SessionManager(this.getContext());
        mainActivity = (MainActivity) this.getActivity();
        mainActivity.setModule("HOMEFRAGMENT");
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeAllViews();
        getHomeBanners();
        return rootView;
    }

    private void initializeAllViews() {
        details = sessionManager.getUserDetails();
        lat = details.get("lat") != null ? details.get("lat").toString() : null;
        lon = details.get("lon") != null ? details.get("lon").toString() : null;

        pd = new ProgressDialog(this.getContext());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        location_text = rootView.findViewById(R.id.location_text);
        home_view_switch = rootView.findViewById(R.id.home_view_switch);
        distance_filter_view = rootView.findViewById(R.id.distance_filter_view);
        location_settings = rootView.findViewById(R.id.location_settings);
        location_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.show();
            }
        });
        no_offers = rootView.findViewById(R.id.no_offers);
        hideView(distance_filter_view);

        home_view_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    showView(location_settings);
                    showView(distance_filter_view);
                    lat = details.get("lat") != null ? details.get("lat").toString() : null;
                    lon = details.get("lon") != null ? details.get("lon").toString() : null;
                    String place = details.get("place") != null ? details.get("place").toString() : null;

                    if(lat !=null && lon != null)
                        getHomeBanners();
                    else
                        openPlacePicker();
                    if(place != null)
                        location_text.setText(place);
                }
                else{
                    hideView(distance_filter_view);
                    hideView(location_settings);
                    getHomeBanners();
                }
            }
        });
        settingsDialog = new Dialog(this.getContext());
        settingsDialog.setContentView(R.layout.banner_location_settings);
        distance_text = settingsDialog.findViewById(R.id.distance_text);
        filter_by_distance = settingsDialog.findViewById(R.id.filter_by_distance);
        TextView txtclose = (TextView) settingsDialog.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog.dismiss();
            }
        });
        Button select_location = settingsDialog.findViewById(R.id.select_location);
        select_location.setOnClickListener(locationSelectListener());
        filter_by_distance.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                distance = value;
                distance_text.setText("Km(" + distance + ")");
                getHomeBanners();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    private boolean getHomeViewSwitchStatus() {
        location = getLastBestLocation();
        if(location != null) {
            return true;
        }
        return false;
    }

    private void getHomeBanners() {

        StringBuffer queryBuffer = new StringBuffer("banners");
        RequestParams requestParams = new RequestParams();
        pd.show();
        if(home_view_switch.isChecked()) {

            queryBuffer.append("?maxlattitude="+(Double.valueOf(lat) + (distance * 0.0043352)));
            queryBuffer.append("&maxlongitude="+(Double.valueOf(lon) + (distance * 0.0043352)));
            queryBuffer.append("&minlattitude="+(Double.valueOf(lat) - (distance * 0.0043352)));
            queryBuffer.append("&minlongitude="+(Double.valueOf(lon) - (distance * 0.0043352)));
        }
        RestCall.get(queryBuffer.toString(), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(pd != null)
                    pd.dismiss();
                Gson gson = new Gson();
                Type type = new TypeToken<List<Banner>>() {}.getType();
                banners = gson.fromJson(new String(responseBody), type);
                List<Banner> topBanners = new ArrayList<>();
                List<Banner> offerBanners = new ArrayList<>();
                for(Banner banner : banners){
                    if(!banner.isBanner())
                        topBanners.add(banner);
                    else
                        offerBanners.add(banner);
                }
                updateHomeBanners(topBanners, rootView);
                updateOfferBanners(offerBanners, rootView);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(pd != null)
                    pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    private void updateOfferBanners(List<Banner> offerBanners, View rootView) {
        BannerAdapter categoryAdapter = null;
        RecyclerView recyclerView = rootView.findViewById(R.id.banner_view);
        if(offerBanners != null && !offerBanners.isEmpty()) {
            showView(recyclerView);
            hideView(no_offers);
            if(lat != null && lon != null)
                categoryAdapter = new BannerAdapter(this.getContext(), offerBanners, home_view_switch.isChecked(), new LatLng(Double.valueOf(lat),(Double.valueOf(lon) )));
            else
                categoryAdapter = new BannerAdapter(this.getContext(), offerBanners, home_view_switch.isChecked(), null);
            recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
            recyclerView.setAdapter(categoryAdapter);
        } else {
            showView(no_offers);
            hideView(recyclerView);
        }
    }


    private void updateHomeBanners(final List<Banner> banners,  View rootView) {

        final ViewPager viewPager = rootView.findViewById(R.id.home_banner);
        CircleIndicator indicator = (CircleIndicator) rootView.findViewById(R.id.indicator);
        BannerPagerAdapter myCustomPagerAdapter = new BannerPagerAdapter(this.getActivity(), banners);
        viewPager.setAdapter(myCustomPagerAdapter);
        indicator.setViewPager(viewPager);
        if(!mainActivity.isBannerRunning()) {
            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == banners.size()) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    mainActivity.setRunning(true);
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        //toast("onLocationChanged");
        nativeupdatePosition(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //toast("onStatusChanged");
        nativeupdatePosition(null);
    }

    @Override
    public void onProviderEnabled(String s) {
        //toast("onProviderEnabled");
        //goToCurrentLocationOffers();
    }

    @Override
    public void onProviderDisabled(String s) {
        //toast("onProviderDisabled");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    private void requestToTurnOnGPS() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addApi(LocationServices.API)
                //.addConnectionCallbacks(this)
                //.addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setInterval(5 * 1000);
        //locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
//                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        goToCurrentLocationOffers();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            getS(status);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GeobuyConstants.PLACE_PICKER_REQUEST :
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        Place place = PlacePicker.getPlace(data, this.mainActivity);
                        LatLng latLng = place.getLatLng();
                        updateGeobuyLocation(latLng.latitude, latLng.longitude, place);
                        lat = String.valueOf(latLng.latitude);
                        lon = String.valueOf(latLng.longitude);
                        location_text.setText(place.getName());
                        getHomeBanners();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }

    }

    private void updateGeobuyLocation(double lat, double lon, Place place){
        SharedPreferences.Editor editor = sessionManager.getEditor();
        editor.putString("lat", String.valueOf(lat));
        editor.putString("lon", String.valueOf(lon));
        editor.putString("place", String.valueOf(place.getName()));
        sessionManager.put(editor);
    }


    private void goToCurrentLocationOffers() {
        if(isInCurrentFragment()) {
            //requestToTurnOnGPS ();
            openPlacePicker();
            /*try {
                if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        GeobuyConstants.MIN_TIME_BW_UPDATES,
                        GeobuyConstants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }

    }



    public void nativeupdatePosition(Location location) {
        this.location = getLastBestLocation();
        getHomeBanners();
    }
    private void getS(Status status) throws IntentSender.SendIntentException {
        status.startResolutionForResult(this.getActivity(), GeobuyConstants.HOME_REQUEST_LOCATION);
    }

    private void showView (View... views){
        for(View v: views){
            v.setVisibility(View.VISIBLE);
        }
    }


    private void hideView (View... views){
        for(View v: views){
            v.setVisibility(View.GONE);
        }
    }


    private void toast(String s) {
        if(isInCurrentFragment()){
            Toast.makeText(this.getActivity(), s, Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * @return the last know best location
     */
    private Location getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //toast("Not granted");
        }
        locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                GeobuyConstants.MIN_TIME_BW_UPDATES,
                GeobuyConstants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    private boolean isInCurrentFragment(){
        return mainActivity.getModule().equalsIgnoreCase("HOMEFRAGMENT");
    }

    private View.OnClickListener locationSelectListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlacePicker();
            }
        };
    }


    private void openPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        builder.setLatLngBounds(GeobuyConstants.GEOBUY_LAT_LNG_BOUNDS);
        try {
            startActivityForResult(builder.build(mainActivity), GeobuyConstants.PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

}
