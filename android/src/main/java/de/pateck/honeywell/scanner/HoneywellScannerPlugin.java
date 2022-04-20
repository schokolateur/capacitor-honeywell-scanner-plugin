package de.pateck.honeywell.scanner;

import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.TriggerStateChangeEvent;

import java.util.HashMap;
import java.util.Map;

@CapacitorPlugin(name = "HoneywellScanner")
public class HoneywellScannerPlugin extends Plugin implements BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener {
    private BarcodeReader _barcodeReader;
    private AidcManager _aidcManager;
    private int _scannerState = -1;
    private boolean _scannerInitialized = false;
    private boolean _lastFailureState = false;
    private boolean _lastSuccessState = false;
    private HoneywellScanner implementation = new HoneywellScanner();


    @PluginMethod
    public void initScanner(PluginCall call) {
        Log.d("HoneywellScanner","initScanner()");
        AidcManager.create(this.getActivity(), new AidcManager.CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                Log.d("HoneywellScanner","initScanner() onCreated");
                _aidcManager = aidcManager;
                _barcodeReader = _aidcManager.createBarcodeReader();
                JSObject ret = new JSObject();
                call.resolve(ret);
            }
        });
    }

    @PluginMethod
    public void claimScanner(PluginCall call) {
        Log.d("HoneywellScanner","claimScanner()");

        try {

            // register barcode listener
            _barcodeReader.addBarcodeListener(this);

            // set the trigger mode to client control
            _barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);

            // register trigger state change listener
            _barcodeReader.addTriggerListener(this);

            Map<String, Object> properties = new HashMap<>();
            // set symbologies on/off
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, call.getBoolean("enableCode128"));
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, call.getBoolean("enableCode39"));
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, call.getBoolean("enableEan13"));
            // turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, call.getBoolean("enableCenterDecode"));

            // apply the settings
            _barcodeReader.setProperties(properties);

            _barcodeReader.claim();
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
            Log.d("HoneywellScanner","claimScanner() claimed");
        } catch(Exception e) {
            Log.e("HoneywellScanner","claimScanner() failed",e);
            JSObject ret = new JSObject();
            ret.put("success", false);
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void releaseScanner(PluginCall call) {
        Log.d("HoneywellScanner","releaseScanner()");

        try {
            _barcodeReader.release();
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
            Log.d("HoneywellScanner","releaseScanner() released");
        } catch(Exception e) {
            Log.e("HoneywellScanner","releaseScanner() failed",e);
            JSObject ret = new JSObject();
            ret.put("success", false);
            call.resolve(ret);
        }
    }

    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
        Log.d("HoneywellScanner","onBarcodeEvent()");
        if (!_lastSuccessState) {
            JSObject ret = new JSObject();
            ret.put("data", barcodeReadEvent.getBarcodeData());
            ret.put("charset", barcodeReadEvent.getCharset().name());
            ret.put("codeId", barcodeReadEvent.getCodeId());
            ret.put("aimId", barcodeReadEvent.getAimId());
            notifyListeners("onHoneywellScannerSuccess", ret);
            _lastSuccessState = true;
        }
    }

    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        Log.d("HoneywellScanner","onFailureEvent()");
        if (!_lastFailureState) {
            JSObject ret = new JSObject();
            notifyListeners("onHoneywellScannerError", ret);
            _lastFailureState = true;
        }
    }


    private boolean _lastTriggerState = false;
    public void onTriggerEvent(TriggerStateChangeEvent triggerStateChangeEvent) {
        Log.d("HoneywellScanner","onTriggerEvent()");
        try {
            if (triggerStateChangeEvent.getState() && !_lastTriggerState) {
                //reset states
                Log.d("HoneywellScanner","onTriggerEvent() reset states");
                _lastFailureState = false;
                _lastSuccessState = false;
            }
            _barcodeReader.aim(triggerStateChangeEvent.getState());
            _barcodeReader.light(triggerStateChangeEvent.getState());
            _barcodeReader.decode(triggerStateChangeEvent.getState());

            if (triggerStateChangeEvent.getState() != _lastTriggerState) {
                JSObject ret = new JSObject();
                ret.put("state", triggerStateChangeEvent.getState());
                notifyListeners("onHoneywellScannerTrigger", ret);
                _lastTriggerState = !_lastTriggerState;
            }

        } catch(Exception e) {
            Log.e("HoneywellScanner","onTriggerEvent() failed",e);
        }
    }
}
