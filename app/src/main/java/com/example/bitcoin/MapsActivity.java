package com.example.bitcoin;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.bitcoin.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapsActivity";
    int LOCATION_REQUEST_CODE = 10001;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
//                Log.d(TAG, location.toString());
                loc.remove();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                options = new MarkerOptions().position(latLng).title("your location: " + "Lat: " + location.getLatitude() + " long: " + location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.0f));
                loc = mMap.addMarker(new MarkerOptions().position(latLng).title("your location: " + "Lat: " + location.getLatitude() + " long: " + location.getLongitude()));
                currentLoc= new LatLng(location.getLatitude(),location.getLongitude());
            }
        }
    };


    private GoogleMap mMap;
    Marker loc; // your location marker
    LatLng currentLoc; // obecna lokalizacja
    Marker loot; // znacznik przez nas postawiony
    HashMap<String,MarkLoc> markers = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(2000); // najszybsza zmiana lokalizacji
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkSettingsAndStartLocationUpdates();
//            getLastLocation();
        } else {
            askLocationPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //settings of device are satisfied, we can start location updates
                startLocationUpdates();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MapsActivity.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "no permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
//                getLastLocation();
                checkSettingsAndStartLocationUpdates();
            } else {
                // permission not granted
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadMarkers();
        mMap.setOnMarkerClickListener(this);

        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        // Add a marker in Sydney and move the camera
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                loc = new MarkerOptions().position(latLng).title("your location: " + "Lat: " + location.getLatitude() + " long: " + location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.0f));
                loc = googleMap.addMarker(new MarkerOptions().position(latLng).title("your location: " + "Lat: " + location.getLatitude() + " long: " + location.getLongitude()));
                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
            }
        });

    }
    // zwraca obecna lokalizacje w formie obietku LatLng.
    @SuppressLint("MissingPermission")
    public LatLng getCurLoc(){
        Task<Location> loc = fusedLocationProviderClient.getLastLocation();
        loc.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

            }

        });
        return currentLoc;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //ŻEBY WRÓCIĆ NA EKRAN GŁÓWNY
        startActivity(intent);
    }
    public void setMarker(View v){
        EditText mess = findViewById(R.id.edtMessage);
        EditText eth = findViewById(R.id.edtEth);
        BigDecimal value = new BigDecimal(eth.getText().toString());
        try{
            TransactionReceipt receipt = Transfer.sendFunds(MainActivity.web3, MainActivity.credentials,MainActivity.zero_acc_credentials.getAddress(), value, Convert.Unit.ETHER).send();
            MarkLoc marl = new MarkLoc(MainActivity.credentials.getAddress(),mess.getText().toString(),String.valueOf(value),currentLoc.latitude,currentLoc.longitude);
            String id = receipt.getTransactionHash();
            Marker mar = mMap.addMarker(new MarkerOptions().position(currentLoc).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_ethereum_icon_purple)).title(mess.getText().toString()+ ", Value : "+eth.getText().toString() +", Transaction"+id));
            markers.put(id,marl);
            MainActivity.dataref.child(id).setValue(marl);
            Toast.makeText(this, "You have created new Eth location!,Transaction successful: " +receipt.getTransactionHash(), Toast.LENGTH_LONG).show();
        }
        catch(Exception e){
            ShowToast("low balance or you did't load wallet");

        }
    }
    public void ShowToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
    public void loadMarkers(){
        MainActivity.dataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    MarkLoc marklo = new MarkLoc(snap.child("creator").getValue().toString(),
                            snap.child("message").getValue().toString(),
                            snap.child("value").getValue().toString(),
                            Double.parseDouble(snap.child("lat").getValue().toString()),
                            Double.parseDouble(snap.child("longi").getValue().toString()));
                    markers.put(snap.getKey(),marklo);
                    LatLng pos = new LatLng(marklo.getLat(),marklo.getLongi());
                    mMap.addMarker(new MarkerOptions().position(pos).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_ethereum_icon_purple)).
                            title(marklo.getMessage()+ ", Value: "+marklo.getValue()+", Transaction"+snap.getKey()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        TextView txt = findViewById(R.id.edtmarker);
        txt.setText(marker.getTitle());
        loot=marker;
        return false;
    }
    public void getEthFromMark(View v){
        TextView txt = findViewById(R.id.edtmarker);
        String text =txt.getText().toString();
        String key = text.substring(text.indexOf("Transaction")+11);
        MarkLoc mar = markers.get(key);
        float[] results = new float[1];
        Location.distanceBetween(currentLoc.latitude,currentLoc.longitude,mar.getLat(),mar.getLongi(),results);
        if(txt.getText()=="") ShowToast("You must click on marker near you first");
        if(results[0]>200){
            ShowToast("You need to come closer");
        }
        else{
            try{
                TransactionReceipt receipt = Transfer.sendFunds(MainActivity.web3, MainActivity.zero_acc_credentials,MainActivity.credentials.getAddress(), new BigDecimal(markers.get(key).getValue()), Convert.Unit.ETHER).send();
                MainActivity.dataref.child(key).removeValue();
                mMap.clear();
                loadMarkers();
                txt.setText("");
                Toast.makeText(this, "Transaction successful: " +receipt.getTransactionHash(), Toast.LENGTH_LONG).show();
            }
            catch(Exception e){
                ShowToast("You need to load wallet first or come closer to marker");

            }
        }
    }
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}