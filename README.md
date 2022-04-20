# Table of contents

- [About](#about)
- [Installation](#installation)
- [Usage](#usage)
- [Methods](#methods)

## About

Honeywell delivers a SDK for using the hardware scanner on Android. 
This plugin is far away from using all possibilities from the SDK. It has been developed for my own use case, which is:

* Initialize the scanner
* Claim the scanner resources
* Listen to trigger & scan events for Code39 barcodes
* Release scanner resources

This plugin is intended to be used in ionic capacitor projects.

**Warning**: This is my first capacitor plugin :)

## Installation

```bash

npm install capacitor-honeywell-scanner-plugin

npx cap sync

```

## Usage

Import everything needed:

    import {
    	HoneywellScanner,
    	ScannerClaimResult,
    	ScannerTriggerResult,
    	ScannerErrorResult,
    	ScannerSuccessResult,
    } from  'capacitor-honeywell-scanner-plugin';

Initialize & Claim the scanner:

    HoneywellScanner.initScanner().then(() => {
    	HoneywellScanner.claimScanner({
    		enableCode128: false,
    		enableCode39: true,
    		enableEan13: false,
    		enableCenterDecode: true,
    	}).then((claimResult: ScannerClaimResult) => {
    		if (claimResult.success) {
    			this.addScannerListeners();
    		} else {
    			this.logger.debug('initScanner() failed to claim');
    		}
    	});
    });

Attach listeners:

    private async addScannerListeners() {
    	await HoneywellScanner.addListener(
    		'onHoneywellScannerTrigger',
    		(triggerResult: ScannerTriggerResult) => {
    			console.log('onHoneywellScannerTrigger:', triggerResult.state);
    		}
    	);
    	await HoneywellScanner.addListener(
    		'onHoneywellScannerError',
    		(errorResult: ScannerErrorResult) => {
    			console.log('onHoneywellScannerError');
    		}
    	);
    	await HoneywellScanner.addListener(
    		'onHoneywellScannerSuccess',
    		(successResult: ScannerSuccessResult) => {
    			console.log('onHoneywellScannerSuccess:',JSON.stringify(successResult));
    		}
    	); 
    }

Release:

    await HoneywellScanner.removeAllListeners();
    await HoneywellScanner.releaseScanner();



## Methods

### initScanner

    initScanner(): Promise<void>;

Returns if scanner is initialized.
Not doing anything on failure right now.

### claimScanner 

    claimScanner(options: ScannerSetupOptions): Promise<ScannerClaimResult>;

Claims the scanner resources and does some initializations (**far away from completeness**).

Setup options:

    export interface ScannerSetupOptions {
	    enableCode128: boolean;
	    enableCode39: boolean;
	    enableEan13: boolean;
	    enableCenterDecode: boolean;
    }

Result:

    export  interface  ScannerClaimResult {
	    success: boolean;
    }

Success is **true** if claim was successfull. On failure **false**.

### releaseScanner 

    releaseScanner(): Promise<ScannerClaimResult>;

Releases the scanner resources.

Result:

    export  interface  ScannerClaimResult {
	    success: boolean;
    }

Success is **true** if release was successfull. On failure **false**.

### addListener: onHoneywellScannerTrigger

    await  HoneywellScanner.addListener(
	    'onHoneywellScannerTrigger',
	    (triggerResult: ScannerTriggerResult) => {
		    console.log('onHoneywellScannerTrigger: ', triggerResult.state);
	    }
    );

Attaches the listener for trigger events. Fires on each hardware scanner button press.

Result:

    export  interface  ScannerTriggerResult {
	    state: boolean;
    }

**true** if button is pressed. **false** if released.

### addListener: onHoneywellScannerError

    await  HoneywellScanner.addListener(
	    'onHoneywellScannerError',
	    (triggerResult: ScannerErrorResult) => {
		    console.log('onHoneywellScannerError:');
	    }
    );

Attaches the listener for scan failure events. Fires if a barcode could not been read (dirty, damaged, not enabled...)

### addListener: onHoneywellScannerSuccess

    await  HoneywellScanner.addListener(
	    'onHoneywellScannerSuccess',
	    (triggerResult: ScannerSuccessResult) => {
		    console.log('onHoneywellScannerSuccess',JSON.stringify(successResult));
	    }
    );

Attaches the listener for scan success events. Fires if a barcode could been read.

Result:

    export interface ScannerSuccessResult {
	    data: string;
	    charset: string;
	    codeId: string;
	    aimId: string;
    }

Data contains the read barcode value.
