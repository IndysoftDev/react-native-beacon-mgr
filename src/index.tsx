import { NativeModules } from 'react-native';

type BeaconMgrType = {
  multiply(a: number, b: number): Promise<number>;
};

const { BeaconMgr } = NativeModules;

export default BeaconMgr as BeaconMgrType;
