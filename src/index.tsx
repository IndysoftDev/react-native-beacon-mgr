import { NativeModules, NativeEventEmitter } from 'react-native';

const { BeaconMgr } = NativeModules;

const evtEmitter = new NativeEventEmitter(BeaconMgr);

//Setup
type setupFn = () => void;

const setup: setupFn = () => BeaconMgr.setup();

//Beacon Parsers
const IBEACON: string = 'm:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24';
const ESTIMOTE: string = 'm:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24';
// const ALTBEACON: string = 'm:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25';

type addParser = (resolve: () => any, reject: () => any) => void;

const addEstimotes: addParser = (resolve: () => any, reject: () => any) =>
  BeaconMgr.addParser(ESTIMOTE, resolve, reject);

const addIBeacon: addParser = (resolve: () => any, reject: () => any) =>
  BeaconMgr.addParser(IBEACON, resolve, reject);

// Ranging and Monitoring Listeners
type addListener = (cb: (args: any[]) => void) => void;

type removeListener = (cb: (args: any[]) => void) => void;

const addRangingListener: addListener = (listener) =>
  evtEmitter.addListener('beaconsDidRange', listener);

const removeRangingListener: removeListener = (listener) =>
  evtEmitter.removeListener('beaconsDidRange', listener);

const addMonitoringListener: addListener = (listener) =>
  evtEmitter.addListener('didDetermineState', listener);

const removeMonitoringListener: removeListener = (listener) =>
  evtEmitter.removeListener('didDetermineState', listener);

//Start/Stop Ranging and Montoring Funcs
type startStopFn = (
  identifier: string,
  uuid?: string,
  minor?: number,
  major?: number
) => Promise<any>;

const startRanging: startStopFn = (
  identifier: string,
  uuid?: string,
  minor?: number,
  major?: number
) => BeaconMgr.startRanging(identifier, uuid, minor, major);

const stopRanging: startStopFn = (
  identifier: string,
  uuid?: string,
  minor?: number,
  major?: number
) => BeaconMgr.stopRanging(identifier, uuid, minor, major);

const startMonitoring: startStopFn = (
  identifier: string,
  uuid?: string,
  minor?: number,
  major?: number
) => BeaconMgr.startMonitoring(identifier, uuid, minor, major);

const stopMonitoring: startStopFn = (
  identifier: string,
  uuid?: string,
  minor?: number,
  major?: number
) => BeaconMgr.stopMonitoring(identifier, uuid, minor, major);

export default {
  setup,
  startRanging,
  stopRanging,
  startMonitoring,
  stopMonitoring,
  addEstimotes,
  addIBeacon,
  addRangingListener,
  removeRangingListener,
  addMonitoringListener,
  removeMonitoringListener,
};
