package com.mattieapps.flashlight;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tjeannin.apprate.AppRate;


public class MainActivity extends ActionBarActivity {

    ImageButton mLightOnOffBtn;
    Button mMoreAppsBtn;

    private Camera mCamera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Camera.Parameters mParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AppRate(this)
                .setMinDaysUntilPrompt(7)
                .setMinLaunchesUntilPrompt(10)
                .init();

        // flash switch button
        mLightOnOffBtn = (ImageButton) findViewById(R.id.lightOnOffBtn);
        mMoreAppsBtn = (Button) findViewById(R.id.moreAppsBtn);


        // First check if device is supporting flashlight or not
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            new MaterialDialog.Builder(this)
                    .title("Error")
                    .content("Sorry, your device doesn't support a flash!")
                    .positiveText("Exit")
                    .callback(new MaterialDialog.Callback() {
                        @Override
                        public void onNegative(MaterialDialog materialDialog) {
                            return;
                        }

                        @Override
                        public void onPositive(MaterialDialog materialDialog) {
                            finish();
                        }
                    })
                    .show();
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
    }

    // Get the camera
    private void getCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mParameters = mCamera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
