package com.reactnativebeaconmgr;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import org.jetbrains.annotations.Nullable;
import android.util.Log;
import android.os.RemoteException;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.module.annotations.ReactModule;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ReactModule(name = BeaconMgrModule.NAME)
public class BeaconMgrModule extends ReactContextBaseJavaModule {
    public static final String NAME = "BeaconMgr";
    private static final int RUNNING_AVG_RSSI_FILTER = 0;
    private static final int ARMA_RSSI_FILTER = 1;
    private BeaconManager mBeaconManager;
    private Context mApplicationContext;
    private ReactApplicationContext mReactContext;

    public BeaconMgrModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Log.d(NAME, "BeaconMgrModule - started");
        this.mReactContext = reactContext;
    }

    @ReactMethod
    public void setup() {
        this.mApplicationContext = this.mReactContext.getApplicationContext();
        this.mBeaconManager = BeaconManager.getInstanceForApplication(mApplicationContext);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.bind(beaconConsumer);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("SUPPORTED", BeaconTransmitter.SUPPORTED);
        constants.put("NOT_SUPPORTED_MIN_SDK", BeaconTransmitter.NOT_SUPPORTED_MIN_SDK);
        constants.put("NOT_SUPPORTED_BLE", BeaconTransmitter.NOT_SUPPORTED_BLE);
        constants.put("NOT_SUPPORTED_CANNOT_GET_ADVERTISER_MULTIPLE_ADVERTISEMENTS", BeaconTransmitter.NOT_SUPPORTED_CANNOT_GET_ADVERTISER_MULTIPLE_ADVERTISEMENTS);
        constants.put("NOT_SUPPORTED_CANNOT_GET_ADVERTISER", BeaconTransmitter.NOT_SUPPORTED_CANNOT_GET_ADVERTISER);
        constants.put("RUNNING_AVG_RSSI_FILTER",RUNNING_AVG_RSSI_FILTER);
        constants.put("ARMA_RSSI_FILTER",ARMA_RSSI_FILTER);
        return constants;
    }


    @ReactMethod
    public void setHardwareEqualityEnforced(Boolean e) {
        Beacon.setHardwareEqualityEnforced(e.booleanValue());
    }

    public void bindManager() {
        if (!mBeaconManager.isBound(beaconConsumer)) {
            Log.d(NAME, "BeaconMgrModule - bindManager: ");
            mBeaconManager.bind(beaconConsumer);
        }
    }

    public void unbindManager() {
        if (mBeaconManager.isBound(beaconConsumer)) {
            Log.d(NAME, "BeaconMgrModule - unbindManager: ");
            mBeaconManager.unbind(beaconConsumer);
        }
    }

    @ReactMethod
    public void addParser(String parser, Callback resolve, Callback reject) {
        try {
            Log.d(NAME, "BeaconMgrModule - addParser: " + parser);
            unbindManager();
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(parser));
            bindManager();
            resolve.invoke();
        } catch (Exception e) {
            reject.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void removeParser(String parser, Callback resolve, Callback reject) {
        try {
            Log.d(NAME, "BeaconMgrModule - removeParser: " + parser);
            unbindManager();
            mBeaconManager.getBeaconParsers().remove(new BeaconParser().setBeaconLayout(parser));
            bindManager();
            resolve.invoke();
        } catch (Exception e) {
            reject.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void setBackgroundScanPeriod(int period) {
        mBeaconManager.setBackgroundScanPeriod((long) period);
    }

    @ReactMethod
    public void setBackgroundBetweenScanPeriod(int period) {
        mBeaconManager.setBackgroundBetweenScanPeriod((long) period);
    }

    @ReactMethod
    public void setForegroundScanPeriod(int period) {
        mBeaconManager.setForegroundScanPeriod((long) period);
    }

    @ReactMethod
    public void setForegroundBetweenScanPeriod(int period) {
        mBeaconManager.setForegroundBetweenScanPeriod((long) period);
    }

    @ReactMethod
    public void setRssiFilter(int filterType, double avgModifier) {
        String logMsg = "Could not set the rssi filter.";
        if (filterType==RUNNING_AVG_RSSI_FILTER){
            logMsg="Setting filter RUNNING_AVG";
            BeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
        if (avgModifier>0){
            RunningAverageRssiFilter.setSampleExpirationMilliseconds((long) avgModifier);
            logMsg+=" with custom avg modifier";
        }
            } else if (filterType==ARMA_RSSI_FILTER){
            logMsg="Setting filter ARMA";
            BeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
            if (avgModifier>0){
                ArmaRssiFilter.setDEFAULT_ARMA_SPEED(avgModifier);
                logMsg+=" with custom avg modifier";
            }
        }
        Log.d(NAME, logMsg);
    }

  @ReactMethod
  public void checkTransmissionSupported(Callback callback) {
      int result = BeaconTransmitter.checkTransmissionSupported(mReactContext);
      callback.invoke(result);
  }

  @ReactMethod
  public void getMonitoredRegions(Callback callback) {
      WritableArray array = new WritableNativeArray();
      for (Region region: mBeaconManager.getMonitoredRegions()) {
          WritableMap map = new WritableNativeMap();
          map.putString("identifier", region.getUniqueId());
          map.putString("uuid", region.getId1().toString());
          map.putInt("major", region.getId2() != null ? region.getId2().toInt() : 0);
          map.putInt("minor", region.getId3() != null ? region.getId3().toInt() : 0);
          array.pushMap(map);
      }
      callback.invoke(array);
  }

  @ReactMethod
  public void getRangedRegions(Callback callback) {
      WritableArray array = new WritableNativeArray();
      for (Region region: mBeaconManager.getRangedRegions()) {
          WritableMap map = new WritableNativeMap();
          map.putString("region", region.getUniqueId());
          map.putString("uuid", region.getId1().toString());
          array.pushMap(map);
      }
      callback.invoke(array);
  }

  /***********************************************************************************************
   * BeaconConsumer
   **********************************************************************************************/
    private final BeaconConsumer beaconConsumer = new BeaconConsumer() {
        @Override
        public void onBeaconServiceConnect() {
            mBeaconManager.addRangeNotifier(mRangeNotifier);
            mBeaconManager.addMonitorNotifier(mMonitorNotifier);
            sendEvent("onBeaconServiceConnect", null);
        }

        @Override
        public Context getApplicationContext() {
            return mReactContext.getApplicationContext();
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

    /***********************************************************************************************
    * Monitoring
    **********************************************************************************************/
    @ReactMethod
    public void startMonitoring(String regionId, String beaconUuid, int minor, int major, final Promise promise) {
        Log.d(NAME, "startMonitoring, monitoringRegionId: " + regionId + ", monitoringBeaconUuid: " + beaconUuid + ", minor: " + minor + ", major: " + major);
        try {
            Region region = createRegion(
            regionId,
            beaconUuid,
            String.valueOf(minor).equals("-1") ? "" : String.valueOf(minor),
            String.valueOf(major).equals("-1") ? "" : String.valueOf(major)
            );
            mBeaconManager.startMonitoringBeaconsInRegion(region);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("MONITORING_ERROR", e.getLocalizedMessage()); 
        }
    }

  private MonitorNotifier mMonitorNotifier = new MonitorNotifier() {
      @Override
      public void didEnterRegion(Region region) {
          sendEvent("regionDidEnter", createMonitoringResponse(region));
      }

      @Override
      public void didExitRegion(Region region) {
          sendEvent("regionDidExit", createMonitoringResponse(region));
      }

      @Override
      public void didDetermineStateForRegion(int i, Region region) {
          String state = "unknown";
          switch (i) {
              case MonitorNotifier.INSIDE:
                  state = "inside";
                  break;
              case MonitorNotifier.OUTSIDE:
                  state = "outside";
                  break;
              default:
                  break;
          }
          WritableMap map = createMonitoringResponse(region);
          map.putString("state", state);
          sendEvent("didDetermineState", map);
      }
  };

  private WritableMap createMonitoringResponse(Region region) {
      WritableMap map = new WritableNativeMap();
      map.putString("identifier", region.getUniqueId());
      map.putString("uuid", region.getId1() != null ? region.getId1().toString() : "");
      map.putInt("major", region.getId2() != null ? region.getId2().toInt() : 0);
      map.putInt("minor", region.getId3() != null ? region.getId3().toInt() : 0);
      return map;
  }

    @ReactMethod
    public void stopMonitoring(String regionId, String beaconUuid, int minor, int major, final Promise promise) {
        Region region = createRegion(
        regionId,
        beaconUuid,
        String.valueOf(minor).equals("-1") ? "" : String.valueOf(minor),
        String.valueOf(major).equals("-1") ? "" : String.valueOf(major)
        );

        try {
            mBeaconManager.stopMonitoringBeaconsInRegion(region);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("MONITORING_ERROR", e.getLocalizedMessage()); 
        }
    }

  /***********************************************************************************************
   * Ranging
   **********************************************************************************************/
  @ReactMethod
  public void startRanging(String regionId, String beaconUuid, int minor, int major, final Promise promise) {
      Log.d(NAME, "startRanging, rangingRegionId: " + regionId + ", rangingBeaconUuid: " + beaconUuid);
      try {
          Region region = createRegion(
            regionId,
            beaconUuid,
            String.valueOf(minor).equals("-1") ? "" : String.valueOf(minor),
            String.valueOf(major).equals("-1") ? "" : String.valueOf(major)
          );

          mBeaconManager.startRangingBeaconsInRegion(region);
          promise.resolve(true);
      } catch (Exception e) {
          promise.reject("RANGING_ERROR", e.getLocalizedMessage());
      }
  }

  private RangeNotifier mRangeNotifier = new RangeNotifier() {
      @Override
      public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.d(NAME, "rangingConsumer didRangeBeaconsInRegion, beacons: " + beacons.toString());
        Log.d(NAME, "rangingConsumer didRangeBeaconsInRegion, region: " + region.toString());
        sendEvent("beaconsDidRange", createRangingResponse(beacons, region));
      }
  };

  private WritableMap createRangingResponse(Collection<Beacon> beacons, Region region) {
      WritableMap map = Arguments.createMap();
      map.putString("identifier", region.getUniqueId());
      WritableArray a = new WritableNativeArray();
      for (Beacon beacon : beacons) {
          WritableMap b = new WritableNativeMap();
          b.putString("uuid", beacon.getId1().toString());
          if (beacon.getIdentifiers().size() > 2) {
              b.putInt("major", beacon.getId2().toInt());
              b.putInt("minor", beacon.getId3().toInt());
          }
          b.putInt("rssi", beacon.getRssi());
          if(beacon.getDistance() == Double.POSITIVE_INFINITY
                    || Double.isNaN(beacon.getDistance())
                    || beacon.getDistance() == Double.NaN
                    || beacon.getDistance() == Double.NEGATIVE_INFINITY){
                b.putDouble("distance", 999.0);
                b.putString("proximity", "far");
            }else {
                b.putDouble("distance", beacon.getDistance());
                b.putString("proximity", BeaconUtils.distanceToProximity(beacon.getRssi(), beacon.getDistance()));
            }
          a.pushMap(b);
      }
      map.putArray("beacons", a);
      return map;
  }

    @ReactMethod
    public void stopRanging(String regionId, String beaconUuid, int minor, int major, final Promise promise) {
        Region region = createRegion(
            regionId,
            beaconUuid,
            String.valueOf(minor).equals("-1") ? "" : String.valueOf(minor),
            String.valueOf(major).equals("-1") ? "" : String.valueOf(major)
        );
        try {
            mBeaconManager.stopRangingBeaconsInRegion(region);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("RANGING_ERROR", e.getLocalizedMessage());
        }
    }

  @ReactMethod
  public void requestStateForRegion(String regionId, String beaconUuid, int minor, int major) {
      Region region = createRegion(
        regionId,
        beaconUuid,
        String.valueOf(minor).equals("-1") ? "" : String.valueOf(minor),
        String.valueOf(major).equals("-1") ? "" : String.valueOf(major)
      );
      mBeaconManager.requestStateForRegion(region);
  }


  /***********************************************************************************************
   * Utils
   **********************************************************************************************/
    private void sendEvent(String eventName, @Nullable WritableMap params) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }

    private Region createRegion(String regionId, String beaconUuid) {
        Identifier id1 = (beaconUuid == null) ? null : Identifier.parse(beaconUuid);
        return new Region(regionId, id1, null, null);
    }

    private Region createRegion(String regionId, String beaconUuid, String minor, String major) {
        Identifier id1 = (beaconUuid == null) ? null : Identifier.parse(beaconUuid);
        return new Region(
        regionId,
        id1,
        major.length() > 0 ? Identifier.parse(major) : null,
        minor.length() > 0 ? Identifier.parse(minor) : null
        );
    }
}
