import { WebPlugin } from '@capacitor/core';

import type {
  ScannerClaimResult,
  HoneywellScannerPlugin,
  ScannerSetupOptions,
} from './definitions';

export class HoneywellScannerWeb
  extends WebPlugin
  implements HoneywellScannerPlugin {
  async initScanner(): Promise<void> {}
  async claimScanner(
    options: ScannerSetupOptions,
  ): Promise<ScannerClaimResult> {
    return { success: false };
  }
  async releaseScanner(): Promise<ScannerClaimResult> {
    return { success: false };
  }
}
