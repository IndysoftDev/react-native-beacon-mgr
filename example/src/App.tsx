import * as React from 'react';

import { StyleSheet, View, Text, DeviceEventEmitter } from 'react-native';
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
    BeaconMgr.setup();
    BeaconMgr.addEstimotes(onSuccess, onFail);
  }, []);

  React.useEffect(() => {
    BeaconMgr.startRanging(
      'REGION1',
      '42a0e5cc-12bf-4ae9-be4e-2dbea495fc9b',
      onSuccess,
      onFail
    );

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
