import { PluginListenerHandle } from '@capacitor/core';

export interface ScannerSetupOptions {
  enableCode128: boolean;
  enableCode39: boolean;
  enableEan13: boolean;
  enableCenterDecode: boolean;
}
export interface ScannerClaimResult {
  success: boolean;
}
export interface ScannerSuccessResult {
  data: string;
  charset: string;
  codeId: string;
  aimId: string;
}
export type ScannerSuccessListener = (state: ScannerSuccessResult) => void;

export interface ScannerErrorResult {}
export type ScannerErrorListener = (state: ScannerErrorResult) => void;

export interface ScannerTriggerResult {
  state: boolean;
}
export type ScannerTriggerListener = (state: ScannerTriggerResult) => void;

export interface HoneywellScannerPlugin {
  addListener(
    eventName: 'onHoneywellScannerSuccess',
    listenerFunc: ScannerSuccessListener,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  addListener(
    eventName: 'onHoneywellScannerError',
    listenerFunc: ScannerErrorListener,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  addListener(
    eventName: 'onHoneywellScannerTrigger',
    listenerFunc: ScannerTriggerListener,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  removeAllListeners(): Promise<void>;

  initScanner(): Promise<void>;
  claimScanner(options: ScannerSetupOptions): Promise<ScannerClaimResult>;
  releaseScanner(): Promise<ScannerClaimResult>;
}
