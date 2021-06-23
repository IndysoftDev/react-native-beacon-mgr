package com.reactnativebeaconmgr;

import android.util.Log;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.RemoteException;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;


@ReactModule(name = BeaconMgrModule.NAME)
public class BeaconMgrModule extends ReactContextBaseJavaModule {
    public static final String NAME = "BeaconMgr";
    private final ReactApplicationContext mReactContext;
    private final List<Region> regionRanging;
    private final List<Region> regionMonitoring;
    private final BeaconManager mBeaconManager;

    //EVENT CONSTANTS
    private static final String ON_BEACON_SERVICE_CONNECT = "onBeaconServiceConnect";

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }    

    public BeaconMgrModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Log.d(NAME, "BeaconMgrModule - started");
        this.mReactContext = reactContext;
        this.regionRanging = new ArrayList<>();
        this.regionMonitoring = new ArrayList<>();
        this.mBeaconManager = BeaconManager.getInstanceForApplication(reactContext);
    }

    //SEND EVENT FUNCS
    private void sendEvent(String eventName, @Nullable WritableArray array) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, array);
    }

    private void sendEvent(String eventName, @Nullable String string) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, string);
    }

    //EXAMPLE METHOD, DELETE AFTER DEV
    @ReactMethod
    public void multiply(int a, int b, Promise promise) {
        promise.resolve(a * b);
    }

    public static native int nativeMultiply(int a, int b);

    private final BeaconConsumer beaconConsumer = new BeaconConsumer() {

        @Override
        public void onBeaconServiceConnect() {
            sendEvent(ON_BEACON_SERVICE_CONNECT, null);
        }

        @Override
        public Context getApplicationContext() {
            return reactContext.getApplicationContext();
        }

        @Override
        public void unbindService(ServiceConnection serviceConnection) {
            mReactContext.unbindService(serviceConnection);
        }

        @Override
        public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
            return mReactContext.bindService(intent, serviceConnection, i);
        }

    };

    
}
