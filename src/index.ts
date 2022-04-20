import { registerPlugin } from '@capacitor/core';

import type { HoneywellScannerPlugin } from './definitions';

const HoneywellScanner = registerPlugin<HoneywellScannerPlugin>('HoneywellScanner', {
  web: () => import('./web').then(m => new m.HoneywellScannerWeb()),
});

export * from './definitions';
export { HoneywellScanner };
