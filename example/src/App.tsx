import * as React from 'react';

import {
  StyleSheet,
  View,
  Text,
  DeviceEventEmitter,
  PermissionsAndroid,
} from 'react-native';
import BeaconMgr from 'react-native-beacon-mgr';

export default function App() {
  const [result] = React.useState<number | undefined>();

  const onSuccess = () => {
    console.log('add');
    return;
  };

  const onFail = () => {
    console.log('fail');
    return;
  };

  React.useEffect(() => {
    (async () => {
      try {
        await PermissionsAndroid.requestMultiple([
          PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
          PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION,
        ]);
      } catch (err) {
        console.warn(err);
      }
    })();
  }, []);

  React.useEffect(() => {
    BeaconMgr.setup();
    BeaconMgr.addEstimotes(onSuccess, onFail);
  }, []);

  React.useEffect(() => {
    BeaconMgr.startRanging('REGION1', onSuccess, onFail);

    DeviceEventEmitter.addListener('beaconsDidRange', (data) => {
      console.log('Found beacons!', data);
    });
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
