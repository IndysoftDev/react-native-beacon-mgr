package com.reactnativebeaconmgr;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.List;

class BeaconUtils {
  
  static String distanceToProximity(int rssi, double distance) {
    if (rssi == 0) {
      return "unknown";
    }

    if (distance < 1.0) {
      return "immediate";
    }

    if (distance < 3.0) {
      return "near";
    }

    return "far";
  }

  static WritableArray beaconsToArray(List<Beacon> beacons) {
    if (beacons == null) {
      return new WritableNativeArray();
    }

    WritableArray list;
    list = new WritableNativeArray();
    

    for (Beacon beacon : beacons) {
      WritableMap map = beaconToMap(beacon);
      list.pushMap(map);
    }

    return list;
  }

  static WritableMap beaconToMap(Beacon beacon) {
    WritableMap map;
    map = new WritableNativeMap();
    
    map.putString("uuid", beacon.getId1().toString().toUpperCase());
    map.putInt("major", beacon.getId2().toInt());
    map.putInt("minor", beacon.getId3().toInt());
    map.putInt("rssi", beacon.getRssi());
    map.putInt("txPower", beacon.getTxPower());
    map.putDouble("accuracy", beacon.getDistance());
    map.putString("proximity", distanceToProximity(beacon.getRssi(), beacon.getDistance()));
    map.putString("macAddress", beacon.getBluetoothAddress());

    return map;
  }

  static WritableMap regionToMap(Region region) {
    WritableMap map;
    map = new WritableNativeMap();
    
    map.putString("identifier", region.getUniqueId());
    if (region.getId1() != null) {
      map.putString("uuid", region.getId1().toString().toUpperCase());
    }
    if (region.getId2() != null) {
      map.putInt("major", region.getId2().toInt());
    }
    if (region.getId3() != null) {
      map.putInt("minor", region.getId3().toInt());
    }

    return map;
  }

};
