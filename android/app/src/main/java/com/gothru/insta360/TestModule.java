package com.gothru.insta360;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.arashivision.sdkcamera.camera.InstaCameraManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.app.Application;

import com.arashivision.sdkcamera.InstaCameraSDK;
import com.gothru.insta360.util.CameraBindNetworkManager;

public class TestModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private static TestModule sInstance;

    public interface CameraCallback {
        void onCameraOpened();
        void onCameraError(String errorMessage);
    }

    public TestModule(ReactApplicationContext context) {
        super(context);
        this.reactContext = context;
        sInstance = this;
    }

    @Override
    public String getName() {
        return "TestModule";
    }

    @ReactMethod()
    public void initInsta360(){
        Log.d("TestingModule", "Init SDK");
        Application application = (Application) getReactApplicationContext().getApplicationContext();
        InstaCameraSDK.init(application);
    }

    @ReactMethod
    public  void connectByWifi(){
        InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_WIFI);
    }


    public void openCamera(CameraCallback callback) {
        try {
            InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_WIFI);
            callback.onCameraOpened();
        } catch (Exception e) {
            callback.onCameraError(e.getMessage());
        }
    }


    @ReactMethod
    public void connectByUSB(){
        InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_USB);
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String getStringValue(String ability) {
        return "Megalodon " + ability;
    }

    @ReactMethod
    public void createTestEvent(String name, String location) {
        Log.d("TestingModule", "Create event called with name: " + name
                + " and location: " + location);
    }

    @ReactMethod
    public void createAlert(String caption) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getCurrentActivity());
        builder.setTitle("Hello judul")
                .setMessage("Hellow brooo panjang")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Actions to be performed when OK button is clicked
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public static TestModule getInstance() {
        return sInstance;
    }

}
