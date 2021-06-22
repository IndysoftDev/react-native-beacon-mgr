package com.reactnativebeaconmgr;

import android.util.Log;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.RemoteException;

import java.util.Collection;

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
    private BeaconManager mBeaconManager;
    private Context mApplicationContext;
    private ReactApplicationContext mReactContext;

    public BeaconMgrModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Log.d(NAME, "BeaconsAndroidModule - started");
        this.mReactContext = reactContext;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }     

    private void sendEvent(String eventName, @Nullable WritableArray array) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, array);
    }

   
    // Example method
    // See https://reactnative.dev/docs/native-modules-android
    @ReactMethod
    public void multiply(int a, int b, Promise promise) {
        promise.resolve(a * b);
    }

    public static native int nativeMultiply(int a, int b);

    @Override
    public void initialize() {
        this.mApplicationContext = this.mReactContext.getApplicationContext();
        this.mBeaconManager = BeaconManager.getInstanceForApplication(mApplicationContext);
        // need to bind at instantiation so that service loads (to test more)
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
    }

    @ReactMethod
    public void setHardwareEqualityEnforced(Boolean e) {
        Beacon.setHardwareEqualityEnforced(e.booleanValue());
    }

    //  @Override
    // public void onBeaconServiceConnect() {

    //     RangeNotifier rangeNotifier = new RangeNotifier() {
    //        @Override
    //        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
    //         if (beacons.size() > 0) {
    //             Log.d(NAME, "rangingConsumer didRangeBeaconsInRegion, beacons: " + beacons.toString());
    //             Log.d(NAME, "rangingConsumer didRangeBeaconsInRegion, region: " + region.toString());
    //             sendEvent(mReactContext, "beaconsDidRange", createRangingResponse(beacons, region));
    //         }
    //        }

    //     };
    //     try {
    //         mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
    //         mBeaconManager.addRangeNotifier(rangeNotifier);
    //     } catch (RemoteException e) {   }
    // }

}
