package com.mattieapps.flashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.tjeannin.apprate.AppRate;


public class MainActivity extends Activity {

    private ImageButton mLightOnOffBtn;
    private Button mMoreAppsBtn, mShareBtn;

    private Camera mCamera;
    private boolean isFlashOn;
    private boolean hasFlash;
    private Camera.Parameters mParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AppRate(this)
                .setMinDaysUntilPrompt(2)
                .setMinLaunchesUntilPrompt(10)
                .init();

        // flash switch button
        mLightOnOffBtn = (ImageButton) findViewById(R.id.lightOnOffBtn);
        mMoreAppsBtn = (Button) findViewById(R.id.moreAppsBtn);
        mShareBtn = (Button) findViewById(R.id.shareBtn);


        // First check if device is supporting flashlight or not
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sorry, your device doesn't support a flash!");
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();

            return;
        }

        // get the camera
        getCamera();

        // displaying button image
        toggleButtonImage();


        // Switch button click event to toggle flash on/off
        mLightOnOffBtn.setOnClickListener(new View.OnClickListener() {

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.lightBackground);

            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    // turn off flash
                    turnOffFlash();
                    layout.setBackgroundColor(Color.parseColor("#7f8c8d"));
                } else {
                    // turn on flash
                    turnOnFlash();
                    layout.setBackgroundColor(Color.parseColor("#95a5a6"));
                }
            }
        });

        mMoreAppsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "market://search?q=pub:Mattie Apps";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "I found this cool, new app for Android!");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Check out this awesome Android flashlight app I found! \nDownload it here at: http://bit.ly/androidflashlightapp");
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    // Get the camera
    private void getCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mParameters = mCamera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera failed to Open.\n ", e.getMessage());
            }
        }
    }


    // Turning On flash
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (mCamera == null || mParameters == null) {
                return;
            }

            mParameters = mCamera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParameters);
            mCamera.startPreview();
            isFlashOn = true;

            // changing button/switch image
            toggleButtonImage();
        }

    }


    // Turning Off flash
    private void turnOffFlash() {
        if (isFlashOn) {
            if (mCamera == null || mParameters == null) {
                return;
            }

            mParameters = mCamera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParameters);
            mCamera.stopPreview();
            isFlashOn = false;

            // changing button/switch image
            toggleButtonImage();
        }
    }

    /*
     * Toggle switch button images
     * changing image states to on / off
     * */
    private void toggleButtonImage(){
        if(isFlashOn){
            mLightOnOffBtn.setImageResource(R.drawable.light_on);
        }else{
            mLightOnOffBtn.setImageResource(R.drawable.light_off);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOffFlash();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on resume turn on the flash
        if(hasFlash)
            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera mParameters
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

}
