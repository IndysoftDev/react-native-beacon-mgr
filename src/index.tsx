import { NativeModules } from 'react-native';

type BeaconMgrType = {
  setup(): void;
  //multiply(a: number, b: number): Promise<number>;
};

const { BeaconMgr } = NativeModules;

export default BeaconMgr as BeaconMgrType;
