import * as React from 'react';

import { StyleSheet, View, Text, PermissionsAndroid } from 'react-native';
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
    BeaconMgr.addIBeacon(onSuccess, onFail);
  }, []);

  React.useEffect(() => {
    BeaconMgr.addRangingListener((data) => console.log('Found beacons!', data));

    return () => BeaconMgr.removeRangingListener(() => console.log('Removed'));
  }, []);

  React.useEffect(() => {
    (async () => {
      await BeaconMgr.startRanging(
        'REGION1',
        'B9407F30-F5F8-466E-AFF9-25556B57FE6D',
        56640,
        34612
      );
    })();
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
