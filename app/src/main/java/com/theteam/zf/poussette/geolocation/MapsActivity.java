    package com.theteam.zf.poussette.geolocation;

    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Color;
    import android.location.Address;
    import android.location.Criteria;
    import android.location.Geocoder;
    import android.location.Location;
    import android.location.LocationManager;
    import android.os.Bundle;
    import android.support.v4.app.FragmentActivity;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.google.android.gms.common.ConnectionResult;
    import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
    import com.google.android.gms.common.GooglePlayServicesRepairableException;
    import com.google.android.gms.common.GooglePlayServicesUtil;
    import com.google.android.gms.common.api.GoogleApiClient;
    import com.google.android.gms.location.places.Place;
    import com.google.android.gms.location.places.Places;
    import com.google.android.gms.location.places.ui.PlacePicker;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.BitmapDescriptorFactory;
    import com.google.android.gms.maps.model.CameraPosition;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.google.android.gms.maps.model.Polyline;
    import com.google.android.gms.maps.model.PolylineOptions;
    import com.theteam.zf.poussette.R;

    import java.io.IOException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;

    public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

        private GoogleMap mMap; // Might be null if Google Play services APK is not available.
        public static double currLatitude = 0, currLongtitude = 0, currAltitude = 0;
        public static List<Position> positionsHistory = new ArrayList<Position>();
        private TextView distanceTextView,timeTextView;
        private Button placeNearbyButton;
        private GoogleApiClient googleApiClient;

        public final int PLACE_PICKER_REQUEST = 1;

        //private GMapV2Direction gMapV2Direction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //gMapV2Direction = new GMapV2Direction();
        setContentView(R.layout.activity_maps);
        getMyCurrentPosition();

        TextView addressTextView = (TextView) findViewById(R.id.addressTextView);
        addressTextView.setText("Adresse :\r\n "+getAdresseString());

        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        placeNearbyButton = (Button) findViewById(R.id.placeNearbyButton);

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setPlaceNearbyListener();
        setUpMapIfNeeded();
        showItinerary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyCurrentPosition();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        //getMyCurrentPosition();
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);

        Log.v("*", "***********************************************************************");
        Log.v("positions ", currLatitude + " long " + currLongtitude);
        //       currLatitude =  mMap.getMyLocation().getLatitude();
        //       currLongtitude =  mMap.getMyLocation().getLongitude();
        mMap.addMarker(new MarkerOptions().position(new LatLng(currLatitude, currLongtitude))
                .title("Your baby is here!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.stroller_marker)));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(currLatitude, currLongtitude)).zoom(17).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }


    @SuppressWarnings("ResourceType")
    private void getMyCurrentPosition() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        MyCurrentLocationListener myCurrentLocationListener = new MyCurrentLocationListener();
        //String provider = locationManager.getBestProvider(criteria,false);
        String provider = LocationManager.NETWORK_PROVIDER;
        if(provider!= null && !provider.equals("")){

            Location location = locationManager.getLastKnownLocation(provider);

            //locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), 100, 0, myCurrentLocationListener);
            locationManager.requestLocationUpdates(provider, 2000, 0, myCurrentLocationListener);

            if(location != null){

                myCurrentLocationListener.onLocationChanged(location);

            }else{
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }
    }


    public static void setLocation(double longitude,double latitude,double attitude){
        currLongtitude = longitude;
        currLatitude = latitude;
        currAltitude = attitude;
    }

    public String getAdresseString(){
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(currLatitude,currLongtitude,5);

            if(!addresses.isEmpty()){
                Address address = addresses.get(0);

                String addressLine = address.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = address.getLocality();//!=null?address.getLocality():"";
                String state = address.getAdminArea();//!=null? address.getAdminArea():"";
                String country = address.getCountryName();//!=null?address.getCountryName():"";
                String road = address.getSubThoroughfare();//!=null?address.getSubThoroughfare():"";
                //String knownName = address.getFeatureName(); // Only if available else return NULL

                return (addressLine+"\r\n"+(road)+" "+city+" "+state+", "+country).replaceAll("null|null | null| null ","");
            }else{
                Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

        @Override
        protected void onStart() {
            super.onStart();
            googleApiClient.connect();
        }

        @Override
        protected void onStop() {
            googleApiClient.disconnect();
            super.onStop();
        }


        private void setPlaceNearbyListener(){
            placeNearbyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    //builder.setLatLngBounds(new LatLngBounds(new LatLng(36.742452, 3.053782),new LatLng(36.735196, 3.026810)));
                    try {
                        startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), MapsActivity.this, 0);
                    } catch (GooglePlayServicesNotAvailableException e) {
                        Toast.makeText(MapsActivity.this, "Google Play Services is not available.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if(requestCode==PLACE_PICKER_REQUEST){
                if(resultCode==RESULT_OK){
                    Place place = PlacePicker.getPlace(data, this);
                    String toastMsg = String.format("Place: %s", place.getName());
                    Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                }
            }else{
                super.onActivityResult(requestCode, resultCode, data);
            }

        }

        private void showItinerary(){

         List<Position> list = MapsActivity.positionsHistory ;
         Calendar calendar= Calendar.getInstance();
         calendar.set(2015,7,21,00,10,00);
         MapsActivity.positionsHistory.clear();

         MapsActivity.positionsHistory.add(new Position(36.750366, 3.013435,0,calendar.getTime()));
         MapsActivity.positionsHistory.add(new Position(36.749837, 3.012921,0,null));
         MapsActivity.positionsHistory.add(new Position(36.749536, 3.012621,0,null));
         MapsActivity.positionsHistory.add(new Position(36.749306, 3.012424,0,null));
         MapsActivity.positionsHistory.add(new Position(36.748905, 3.012357,0,null));
         MapsActivity.positionsHistory.add(new Position(36.748567, 3.012295,0,null));
         MapsActivity.positionsHistory.add(new Position(36.748256, 3.012223,0,null));
         MapsActivity.positionsHistory.add(new Position(36.747933, 3.012113,0,null));
         MapsActivity.positionsHistory.add(new Position(36.747827, 3.011333,0,null));calendar.set(2015, 7, 21, 00, 35, 00);
         MapsActivity.positionsHistory.add(new Position(36.747868, 3.010743,0,calendar.getTime()));

         if(!positionsHistory.isEmpty()){
             //Document document = gMapV2Direction.getDocument(new LatLng(36.7170131, 3.1809057), new LatLng(36.5170131, 3.1909057), GMapV2Direction.MODE_WALKING);
             PolylineOptions polygonOptions = new PolylineOptions().width(40).color(Color.CYAN);

             for (Position posi : MapsActivity.positionsHistory ){
                 polygonOptions.add(posi.getLatLng());
             }

             MarkerOptions markerB = new MarkerOptions();
             markerB.position(list.get(list.size() - 1).getLatLng());
             markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

             Polyline polyline = mMap.addPolyline(polygonOptions);
             mMap.addMarker(markerB);

             distanceTextView.setText("Distance marchée : " + getDistance(list.get(0), list.get(list.size() - 1))+" Mètres");
             timeTextView.setText("Temps : " + getTime(list.get(0), list.get(list.size() - 1)) + " Minutes.");
         }

     }




      private double rad(double x){
          return  x * Math.PI /180;
      }

        private double getDistance(Position p1,Position p2){
            Double distance= new Double(0);
            int earthR = 6378137;
            double dLat = rad(p2.getLatitude()-p1.getLatitude());
            double dLong = rad(p2.getLongitude()-p1.getLongitude());
            double a = Math.sin(dLat / 2)* Math.sin(dLat / 2)+
            Math.cos(rad(p1.getLatitude()))* Math.cos(rad(p2.getLatitude()))
            * Math.sin(dLong / 2)* Math.sin(dLong / 2);

            double c = 2* Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            distance = earthR*c;

            return  distance.intValue() ;
        }

        private String getTime(Position p1,Position p2){
            return new SimpleDateFormat("mm").format(new Date(p2.getDate().getTime()-p1.getDate().getTime()));
        }

        @Override
        public void onConnected(Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.v("connectivity suspension", "suspended");
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.v("connectivity prob", "there is one");
        }
    }
