package com.gothru.insta360;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.arashivision.insta360.basecamera.camera.BaseCamera;
import com.arashivision.insta360.basecamera.camera.CameraManager;
import com.arashivision.insta360.basemedia.ui.player.capture.CapturePlayerView;
import com.arashivision.insta360.basecamera.camera.BaseCamera.ConnectType;
import com.arashivision.sdkcamera.InstaCameraSDK;
import com.arashivision.sdkcamera.camera.InstaCameraManager;
import com.arashivision.sdkcamera.camera.callback.ICameraChangedCallback;
import com.arashivision.sdkcamera.camera.callback.ICameraOperateCallback;
import com.arashivision.sdkcamera.camera.callback.ICaptureStatusListener;
import com.arashivision.sdkmedia.InstaMediaSDK;
import com.arashivision.sdkmedia.work.WorkUtils;
import com.arashivision.sdkmedia.work.WorkWrapper;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;

public class SupportModule  extends ReactContextBaseJavaModule implements ICameraChangedCallback {
    private final ReactApplicationContext reactContext;
    private static SupportModule sInstance;
    private List<WorkWrapper> allList = new ArrayList<>();
    private List<WorkWrapper> shownList = new ArrayList<>();
    private Camera mCamera;
    private CapturePlayerView mCapturePlayerView;
    private final Context mContext;
    public interface CameraCallback {
        void onCameraOpened(String successMessage);
        void onCameraFailed(String errorMessage);
    }

    @ReactMethod
    public void setData(List<WorkWrapper> workList) {
        allList.clear();
        allList.addAll(workList);
        showAll();
    }

    private int showAll() {
        shownList.clear();
        shownList.addAll(allList);
//        return shownList.size();
        return shownList == null ? 0 : shownList.size();
    }


    public SupportModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        mContext = reactContext.getApplicationContext();
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
//        try {
//            Application application = (Application) getReactApplicationContext().getApplicationContext();
//            InstaCameraSDK.init(application);
//            InstaMediaSDK.init(application);
//            promise.resolve("SDK initialization successful");
//        } catch (Exception e) {
//            promise.reject("SDK initialization failed", e);
//        }

        try {
            Application application = (Application) mContext;
            InstaCameraSDK.init(application);
            InstaMediaSDK.init(application);
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
    public void openCamera(){
        CameraManager.getInstance().tryOpenCamera(ConnectType.WIFI);
    }

    @ReactMethod
    public void openCameraWifi() {
        InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_WIFI);
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
    public void getPhotos() {

        List<WorkWrapper> list = WorkUtils.getAllCameraWorks(
                InstaCameraManager.getInstance().getCameraHttpPrefix(),
                InstaCameraManager.getInstance().getCameraInfoMap(),
                InstaCameraManager.getInstance().getAllUrlList(),
                InstaCameraManager.getInstance().getRawUrlList()
        );
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

    @ReactMethod
    public void calibrateGyro(Promise promise) {
        try {
            InstaCameraManager.getInstance().calibrateGyro(new ICameraOperateCallback() {
                @Override
                public void onSuccessful() {
                    promise.resolve("Gyro calibration successful");
                }

                @Override
                public void onFailed() {
                    promise.reject("Gyro calibration failed");
                }

                @Override
                public void onCameraConnectError() {
                    promise.reject("Camera connection error during gyro calibration");
                }
            });
        } catch (Exception e) {
            promise.reject("Exception during gyro calibration", e);
        }
    }

    @ReactMethod
    public void formatStorage(Promise promise) {
        try {
            InstaCameraManager.getInstance().formatStorage(new ICameraOperateCallback() {
                @Override
                public void onSuccessful() {
                    promise.resolve("Storage formatting successful");
                }

                @Override
                public void onFailed() {
                    promise.reject("Storage formatting failed");
                }

                @Override
                public void onCameraConnectError() {
                    promise.reject("Camera connection error during storage formatting");
                }
            });
        } catch (Exception e) {
            promise.reject("Exception during storage formatting", e);
        }
    }

    @ReactMethod
    public void deleteFiles(String[] urls, Promise promise) {
        try {
            List<String> fileUrls = Arrays.asList(urls);
            InstaCameraManager.getInstance().deleteFileList(fileUrls, new ICameraOperateCallback() {
                @Override
                public void onSuccessful() {
                    promise.resolve("Files deleted successfully");
                }

                @Override
                public void onFailed() {
                    promise.reject("Failed to delete files");
                }

                @Override
                public void onCameraConnectError() {
                    promise.reject("Camera connection error during file deletion");
                }
            });
        } catch (Exception e) {
            promise.reject("Exception during file deletion", e);
        }
    }

    @ReactMethod
    public void setCaptureStatusListener(Promise promise) {
        try {
            InstaCameraManager.getInstance().setCaptureStatusListener(new ICaptureStatusListener() {
                @Override
                public void onCaptureStarting() {
                    promise.resolve("Capture is starting");
                }

                @Override
                public void onCaptureWorking() {
                    promise.resolve("Capture is ongoing");
                }

                @Override
                public void onCaptureStopping() {
                    promise.resolve("Capture is stopping");
                }

                @Override
                public void onCaptureFinish(String[] filePaths) {
                    promise.resolve("Capture finished");
                }

                @Override
                public void onCaptureCountChanged(int captureCount) {
                    promise.resolve("Capture count changed: " + captureCount);
                }

                @Override
                public void onCaptureTimeChanged(long captureTime) {
                    promise.resolve("Capture time changed: " + captureTime);
                }
            });
        } catch (Exception e) {
            promise.reject("Error setting capture status listener", e);
        }
    }
    @ReactMethod
    public void onCameraStatusChanged(boolean enabled) {
        System.out.println("Camera status changed. Enabled: " + enabled);
    }

    @ReactMethod
    public void onCameraConnectError(int errorCode) {
        System.out.println("Camera connection failed. Error code: " + errorCode);
    }

    @ReactMethod
    public void onCameraSDCardStateChanged(boolean enabled) {
        System.out.println("SD card state changed. Enabled: " + enabled);
    }

    @ReactMethod
    public void onCameraStorageChanged(long freeSpace, long totalSpace) {
        System.out.println("Camera storage changed. Free space: " + freeSpace + " Total space: " + totalSpace);
    }

    @ReactMethod
    public void onCameraBatteryLow() {
        System.out.println("Low battery notification.");
    }

    @ReactMethod
    public void onCameraBatteryUpdate(int batteryLevel, boolean isCharging) {
        System.out.println("Battery level: " + batteryLevel + " Charging: " + isCharging);
    }

}
