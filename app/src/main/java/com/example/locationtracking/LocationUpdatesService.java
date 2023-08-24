package com.example.locationtracking;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.locationtracking.activities.MainActivity;
import com.example.locationtracking.api.ApiClient;
import com.example.locationtracking.api.ApiEndPoint;
import com.example.locationtracking.util.UserDataManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LocationUpdatesService extends Service {

    private static final String PACKAGE_NAME = "com.bg.crm.utils.LocationUpdatesService";

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "channel_01";

    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification";

    private final IBinder mBinder = new LocalBinder();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = (60 * 1000);

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;

    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderClient}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    /**
     * The current location.
     */
    private Location mLocation;

    public LocationUpdatesService() {
    }

    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "Location Service", NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("Service started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        // startForeground(NOTIFICATION_ID, getNotification());
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Timber.i("in onBind()");
        // stopForeground(true);
        startForeground(NOTIFICATION_ID, getNotification());
        mChangingConfiguration = false;
        return mBinder;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Timber.i("in onRebind()");
        // stopForeground(true);
        startForeground(NOTIFICATION_ID, getNotification());
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public boolean onUnbind(Intent intent) {
        Timber.i("Last client unbound from service");
        startForeground(NOTIFICATION_ID, getNotification());
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    @SuppressLint("ThrowableNotAtBeginning")
    public void requestLocationUpdates() {
        Timber.i("Requesting location updates");
        LocationUtil.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            LocationUtil.setRequestingLocationUpdates(this, false);
            Timber.e("Lost location permission. Could not request updates. %s", unlikely);
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    public void removeLocationUpdates() {
        Timber.i("Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            LocationUtil.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            LocationUtil.setRequestingLocationUpdates(this, true);
            Timber.e("Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private Notification getNotification() {

        Geocoder geocoder;
        List<Address> addresses;
        String address = "";
        String fullAddress = "";
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            fullAddress = "Address : " + address
                    + " City : " + city
                    + " State : " + state
                    + " Country : " + country
                    + " PostalCode : " + postalCode
                    + " KnownName : " + knownName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timber.e(fullAddress);

        Intent intent = new Intent(this, LocationUpdatesService.class);

        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .addAction(R.mipmap.ic_launcher_circle, getString(R.string.launch_activity), activityPendingIntent)
                .addAction(R.mipmap.ic_launcher_circle, getString(R.string.remove_location_updates), servicePendingIntent)
                .setContentText(address)
                .setContentTitle(LocationUtil.getLocationTitle(this))
                .setOngoing(true)
                .setSilent(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher_circle)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setTicker(address)
                .setWhen(System.currentTimeMillis());

        return builder.build();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    mLocation = task.getResult();
                } else {
                    Timber.tag(TAG).w("Failed to get location.");
                }
            });
        } catch (SecurityException unlikely) {
            Timber.e("Lost location permission.%s", unlikely.getMessage());
        }
    }

    private String getAddress(double latitude, double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        String address = "";
        String fullAddress = "";
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            fullAddress = "Address : " + address
                    + " City : " + city
                    + " State : " + state
                    + " Country : " + country
                    + " PostalCode : " + postalCode
                    + " KnownName : " + knownName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timber.e(fullAddress);
        return address;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void onNewLocation(Location location) {
        Timber.i("New location: %s", location);
        mLocation = location;
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        // LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        updateLocation(mLocation);
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }

    private void updateLocation(Location location) {
        ApiEndPoint apiService = ApiClient.getClient().create(ApiEndPoint.class);

        String address = getAddress(location.getLatitude(), location.getLongitude());
        String accessToken = UserDataManager.getKeyAccessToken(getApplicationContext());
        Call<ResponseBody> call = apiService.storeLocation(location.getLatitude(), location.getLongitude(),address,"Bearer " + accessToken);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Timber.e(response.toString());
                    ResponseBody appResponse = response.body();
                    if (appResponse != null) {
                        Timber.e("Updated To Serve");
                    } else {
                        Timber.e("Failed To Update");
                    }
                } else {
                    Timber.e("Failed To Update");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Timber.e("onFailure: %s", t.getMessage());
            }
        });
    }

    private String getDeviceName() {
        return "Brand : " + Build.BRAND
                + " , Model : " + Build.MODEL
                + " , OS : " + Build.VERSION.RELEASE;

    }

    private void createLocationRequest() {
        String interval = UserDataManager.getKeyTimeInterval(getApplicationContext());
        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, Integer.parseInt(interval) * UPDATE_INTERVAL_IN_MILLISECONDS).build();
    }

    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}
