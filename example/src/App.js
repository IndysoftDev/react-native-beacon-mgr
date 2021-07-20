import * as React from 'react';

import { StyleSheet, View, Text, PermissionsAndroid } from 'react-native';
import BeaconMgr from 'react-native-beacon-mgr';

const regions = [
  {
    name: 'TEST1',
    beacons: [
      { description: 'Sams Office', uuid: '', major: '', minor: '' },
      { description: 'Conference Room', uuid: '', major: '', minor: '' },
    ],
  },
];

export default function App() {
  const [closest, setClosest] = React.useState();
  const distanceRef = React.useRef(null);

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
    BeaconMgr.addRangingListener((data) => {
      if (
        !distanceRef.current ||
        data?.closest?.distance < distanceRef.current
      ) {
        distanceRef.current = data.closest.distance;
        setClosest(data.closest);
      }
    });

    return () => BeaconMgr.removeRangingListener(() => console.log('Removed'));
  }, []);

  React.useEffect(() => {
    (async () => {
      await BeaconMgr.startRanging(
        'REGION1',
        'B9407F30-F5F8-466E-AFF9-25556B57FE6D',
        -1,
        -1
      );
    })();
  }, []);

  return (
    <View style={styles.container}>
      {closest && (
        <Text>Result: {`major: ${closest.major} minor: ${closest.minor}`}</Text>
      )}
      <Text>
        Distance:
        {distanceRef.current}
      </Text>
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
