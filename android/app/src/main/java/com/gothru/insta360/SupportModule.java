package com.gothru.insta360;

import android.app.Application;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.arashivision.sdkcamera.InstaCameraSDK;
import com.arashivision.sdkcamera.camera.InstaCameraManager;
import com.arashivision.sdkcamera.camera.callback.ICameraChangedCallback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SupportModule  extends ReactContextBaseJavaModule implements ICameraChangedCallback {
    private final ReactApplicationContext reactContext;
    private static SupportModule sInstance;

    private Camera mCamera;

    public SupportModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        sInstance = this;
    }

    private Map<String, SurfaceView> surfaceViewRegistry = new HashMap<>();
    public void registerSurfaceView(String surfaceViewId, SurfaceView surfaceView) {
        surfaceViewRegistry.put(surfaceViewId, surfaceView);
    }

    public void unregisterSurfaceView(String surfaceViewId) {
        surfaceViewRegistry.remove(surfaceViewId);
    }

    @NonNull
    @Override
    public String getName() {
        return "SupportModule";
    }

    @ReactMethod
    public void initSdk(Promise promise) {
        try {
            Application application = (Application) getReactApplicationContext().getApplicationContext();
            InstaCameraSDK.init(application);
            promise.resolve("SDK initialization successful");
        } catch (Exception e) {
            promise.reject("SDK initialization failed", e);
        }
    }

    @ReactMethod
    public void startCameraPreview(String surfaceViewId, ReadableMap options, Promise promise) {
        SurfaceView surfaceView = surfaceViewRegistry.get(surfaceViewId);
        if (surfaceView != null) {
            if (getReactApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                mCamera = Camera.open();
                if (mCamera != null) {
                    try {
                        mCamera.setPreviewDisplay(surfaceView.getHolder());
                        mCamera.startPreview();
                        promise.resolve("Camera preview started successfully.");
                    } catch (IOException e) {
                        releaseCamera();
                        promise.reject("CAMERA_START_ERROR", "Failed to start camera preview: " + e.getMessage());
                    }
                } else {
                    promise.reject("CAMERA_OPEN_ERROR", "Failed to open camera.");
                }
            } else {
                promise.reject("NO_CAMERA_FEATURE", "Device does not support camera feature.");
            }
        } else {
            promise.reject("SURFACE_VIEW_NOT_FOUND", "SurfaceView with ID " + surfaceViewId + " not found.");
        }
    }


    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
    @ReactMethod
    public void openCameraWifi(Promise promise) {
        try {
            InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_WIFI);
            promise.resolve("Camera opened via WiFi");
        } catch (Exception e) {
            promise.reject("Failed to open camera via WiFi", e);
        }
    }

    @ReactMethod
    public void openCameraUSB(Promise promise) {
        try {
            InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_USB);
            promise.resolve("Camera opened via USB");
        } catch (Exception e) {
            promise.reject("Failed to open camera via USB", e);
        }
    }


    @ReactMethod
    public void getCameraConnectedType(Promise promise) {
        try {
            int type = InstaCameraManager.getInstance().getCameraConnectedType();
            promise.resolve(type);
        } catch (Exception e) {
            promise.reject("Failed to get camera connected type", e);
        }
    }

    @ReactMethod
    public void isCameraConnected(Promise promise) {
        try {
            boolean isConnected = InstaCameraManager.getInstance().getCameraConnectedType() != InstaCameraManager.CONNECT_TYPE_NONE;
            promise.resolve(isConnected);
        } catch (Exception e) {
            promise.reject("Failed to check camera connection", e);
        }
    }

    @ReactMethod
    public void closeCamera(Promise promise) {
        try {
            InstaCameraManager.getInstance().closeCamera();
            promise.resolve("Camera closed");
        } catch (Exception e) {
            promise.reject("Failed to close camera", e);
        }
    }

    @ReactMethod
    public void registerCameraChangedCallback(Promise promise) {
        try {
            InstaCameraManager.getInstance().registerCameraChangedCallback(this);
            promise.resolve("Callback registered");
        } catch (Exception e) {
            promise.reject("Failed to register callback", e);
        }
    }

    @ReactMethod
    public void unregisterCameraChangedCallback(Promise promise) {
        try {
            InstaCameraManager.getInstance().unregisterCameraChangedCallback(this);
            promise.resolve("Callback unregistered");
        } catch (Exception e) {
            promise.reject("Failed to unregister callback", e);
        }
    }

    @ReactMethod
    public void startNormalCapture(boolean captureRaw, Promise promise) {
        try {
            InstaCameraManager.getInstance().startNormalCapture(captureRaw);
            promise.resolve("Normal capture started");
        } catch (Exception e) {
            promise.reject("Failed to start normal capture", e);
        }
    }

    @ReactMethod
    public void startHDRCapture(boolean captureRaw, Promise promise) {
        try {
            InstaCameraManager.getInstance().startHDRCapture(captureRaw);
            promise.resolve("HDR capture started");
        } catch (Exception e) {
            promise.reject("Failed to start HDR capture", e);
        }
    }


    @ReactMethod
    public void startIntervalShooting(Promise promise) {
        try {
            InstaCameraManager.getInstance().startIntervalShooting();
            promise.resolve("Interval shooting started");
        } catch (Exception e) {
            promise.reject("Failed to start interval shooting", e);
        }
    }

    @ReactMethod
    public void stopIntervalShooting(Promise promise) {
        try {
            InstaCameraManager.getInstance().stopIntervalShooting();
            promise.resolve("Interval shooting stopped");
        } catch (Exception e) {
            promise.reject("Failed to stop interval shooting", e);
        }
    }

    @ReactMethod
    public void startNormalRecord(Promise promise) {
        try {
            InstaCameraManager.getInstance().startNormalRecord();
            promise.resolve("Normal record started");
        } catch (Exception e) {
            promise.reject("Failed to start normal record", e);
        }
    }

    @ReactMethod
    public void stopNormalRecord(Promise promise) {
        try {
            InstaCameraManager.getInstance().stopNormalRecord();
            promise.resolve("Normal record stopped");
        } catch (Exception e) {
            promise.reject("Failed to stop normal record", e);
        }
    }

    @ReactMethod
    public void startHDRRecord(Promise promise) {
        try {
            InstaCameraManager.getInstance().startHDRRecord();
            promise.resolve("HDR record started");
        } catch (Exception e) {
            promise.reject("Failed to start HDR record", e);
        }
    }

    @ReactMethod
    public void stopHDRRecord(Promise promise) {
        try {
            InstaCameraManager.getInstance().stopHDRRecord();
            promise.resolve("HDR record stopped");
        } catch (Exception e) {
            promise.reject("Failed to stop HDR record", e);
        }
    }

    @ReactMethod
    public void setTimeLapseInterval(int intervalMs, Promise promise) {
        try {
            InstaCameraManager.getInstance().setTimeLapseInterval(intervalMs);
            promise.resolve("Time lapse interval set");
        } catch (Exception e) {
            promise.reject("Failed to set time lapse interval", e);
        }
    }

    @ReactMethod
    public void startTimeLapse(Promise promise) {
        try {
            InstaCameraManager.getInstance().startTimeLapse();
            promise.resolve("Time lapse started");
        } catch (Exception e) {
            promise.reject("Failed to start time lapse", e);
        }
    }

    @ReactMethod
    public void stopTimeLapse(Promise promise) {
        try {
            InstaCameraManager.getInstance().stopTimeLapse();
            promise.resolve("Time lapse stopped");
        } catch (Exception e) {
            promise.reject("Failed to stop time lapse", e);
        }
    }

    @ReactMethod
    public void getCurrentCaptureType(Promise promise) {
        try {
            int type = InstaCameraManager.getInstance().getCurrentCaptureType();
            promise.resolve(type);
        } catch (Exception e) {
            promise.reject("Failed to get current capture type", e);
        }
    }

}
