package com.moonware.cameraplus;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;
import android.content.Context;
import android.content.res.AssetManager;


/**
 * This class echoes a string called from JavaScript.
 */

public class CameraPlus extends CordovaPlugin {

    /** Common tag used for logging statements. */
    private static final String LOGTAG = "CameraPlus";

    /** Cordova Actions. */

    private static final String ACTION_START_CAMERA = "startCamera";
    private static final String ACTION_STOP_CAMERA = "stopCamera";

    private static final String ACTION_GET_JPEG_IMAGE = "getJpegImage";
    private static final String ACTION_GET_VIDEO_FORMATS = "getVideoFormats";
    private static final String ACTION_SET_VIDEO_FORMATS = "setVideoFormat";


    @Override
    public boolean execute(String action, final JSONArray inputs, final CallbackContext callbackContext) throws JSONException {
        PluginResult result = null;
        if (ACTION_START_CAMERA.equals(action)) {
            result = startCamera(inputs, callbackContext);
        } else if (ACTION_STOP_CAMERA.equals(action)) {
            result = stopCamera(inputs, callbackContext);

        } else if (ACTION_GET_JPEG_IMAGE.equals(action)) {
            //run async
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    PluginResult result = getJpegImage(inputs, callbackContext);
                    if(result != null) callbackContext.sendPluginResult( result );
                }
            });
        } else if (ACTION_GET_VIDEO_FORMATS.equals(action)) {
            result = getVideoFormats(inputs, callbackContext);

        } else if (ACTION_SET_VIDEO_FORMATS.equals(action)) {
            result = setVideoFormats(inputs, callbackContext);

        } else {
            Log.d(LOGTAG, String.format("Invalid action passed: %s", action));
            result = new PluginResult(Status.INVALID_ACTION);
        }

        if(result != null) callbackContext.sendPluginResult( result );

        return true;
    }


    private PluginResult startCamera(JSONArray inputs, CallbackContext callbackContext) throws JSONException{
        Log.w(LOGTAG, "startCamera");

        //get the desired camera
        int cameraId = inputs.getInt(0);

         // initialize the camera manager :)
         CameraManager.init(cordova.getActivity().getApplicationContext());
         startCapture(cameraId);

         return null;
    }

    private PluginResult stopCamera(JSONArray inputs, CallbackContext callbackContext) {
        Log.w(LOGTAG, "stopCamera");

        // stop Capturing but do not we cannot free the CameraManager :s
        stopCapture();

        return null;
    }

    private PluginResult getJpegImage(JSONArray inputs, CallbackContext callbackContext){
    	Log.w(LOGTAG, "getJpegImage");

        byte[] bArray = null;

        //wait a bit for the first image
        for (int i = 0; i < 10; i++){
            bArray = CameraManager.lastFrame();
            if(bArray != null){ break; }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.w(LOGTAG, "Waiting for first image...");
        }

        if (bArray != null)
        {
        	Log.w(LOGTAG, "Received " + String.valueOf(bArray.length) + " bytes...");

        	String imageEncoded = Base64.encodeToString(bArray,Base64.NO_WRAP);

        	//Log.e("LOOK", imageEncoded);

        	callbackContext.success( imageEncoded );
        }
        else
        {
        	callbackContext.error(0);
        }

        return null;
    }

    private PluginResult getVideoFormats(JSONArray inputs, CallbackContext callbackContext) {
        Log.w(LOGTAG, "getVideoFormats");

        callbackContext.success( "0" );
        return null;
    }

    private PluginResult setVideoFormats(JSONArray inputs, CallbackContext callbackContext) {
        Log.w(LOGTAG, "setVideoFormats");

        callbackContext.success( "false" );
        return null;
    }

    private boolean startCapture(int cameraId){
        Log.w(LOGTAG, "startCapture");

        if (false){
            CameraManager.setDesiredPreviewSize(1280, 720);
        } else {
            CameraManager.setDesiredPreviewSize(800, 480);
        }

        try {
			CameraManager.get().openDriver(cameraId);
		} catch (IOException e) {
			Log.w(LOGTAG, "Exception in openDriver");
		}

        //CameraManager.get().startPreview();

        return true;
    }

    private boolean stopCapture(){
        Log.w(LOGTAG, "stopCapture");

        CameraManager.get().stopPreview();

        try {
			CameraManager.get().closeDriver();
		} catch (Exception e) {
			Log.w(LOGTAG, "Exception in closeDriver");
		}

        return true;
    }

    /**
     * Called when the system is about to start resuming a previous activity.
     *
     * @param multitasking		Flag indicating if multitasking is turned on for app
     */
    public void onPause(boolean multitasking) {
    	//if(! multitasking) __stopServer();
    }

    /**
     * Called when the activity will start interacting with the user.
     *
     * @param multitasking		Flag indicating if multitasking is turned on for app
     */
    public void onResume(boolean multitasking) {
    	//if(! multitasking) __startServer();
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    public void onDestroy() {
    	//__stopServer();
    }
}
