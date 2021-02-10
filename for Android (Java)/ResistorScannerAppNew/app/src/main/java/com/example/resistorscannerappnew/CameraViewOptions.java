package com.example.resistorscannerappnew;

import android.content.Context;
import android.hardware.Camera;

import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;

import java.util.List;

public class CameraViewOptions extends JavaCameraView {

    public CameraViewOptions(Context context, int cameraId) {
        super(context, cameraId);
    }

    protected boolean initializeCamera(int width, int height)
    {
        boolean ret = super.initializeCamera(width, height);

        Camera.Parameters params = mCamera.getParameters();

        List<String> FocusModes = params.getSupportedFocusModes();
        if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
        {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        else if(FocusModes != null && ((List) FocusModes).contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        List<String> FlashModes = params.getSupportedFlashModes();
        if(FlashModes != null && FlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH))
        {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }


        mCamera.setParameters(params);

        return ret;
    }
}

