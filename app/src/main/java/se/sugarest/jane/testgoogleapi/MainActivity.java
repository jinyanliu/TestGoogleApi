package se.sugarest.jane.testgoogleapi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

// the implementation of LocationListener is only needed when you want to listen the location update.
// locationListener is not needed for get lastLocation service.
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final static int REQUEST_LOCATION = 2;

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLatitudeTextView = (TextView) findViewById(R.id.tv_latitude_number);
        mLongitudeTextView = (TextView) findViewById(R.id.tv_longitude_number);
        buildGoogleApiClinet();
    }

    protected synchronized void buildGoogleApiClinet() {
        // Create and setup GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the Client
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect the client
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    // Make sure to use android.Manifest instead of my.app.package.Manifest
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
//            /**
//             * requestLocationUpdates
//             */
//            mLocationRequest = LocationRequest.create();
//            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            mLocationRequest.setInterval(1000); // Update location every second
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            /**
             * getLastLocation
             *
             * Provides a simple way of getting a device's location and is well suited for
             * applications that do not require a fine-grained location and that do not need location
             * updates. Gets the best and most recent location currently available, which may be null
             * in rare cases when a location is not available.
             */
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitudeTextView.setText(String.valueOf(mLastLocation.getLatitude()));
                mLongitudeTextView.setText(String.valueOf(mLastLocation.getLongitude()));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                /**
//                 * requestLocationUpdates
//                 */
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                /**
                 * getLastLocation
                 */
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    mLatitudeTextView.setText(String.valueOf(mLastLocation.getLatitude()));
                    mLongitudeTextView.setText(String.valueOf(mLastLocation.getLongitude()));
                }
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(this, "Location Permissions denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Called by Google Play services if the connection to GoogleApiClient drops because of an
     * error.
     */
    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "GoogleApiClient connection has been suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.toString());
        String latitude = Double.toString(location.getLatitude());
        String longitude = Double.toString(location.getLongitude());
        mLatitudeTextView.setText(latitude);
        mLongitudeTextView.setText(longitude);
    }
}
