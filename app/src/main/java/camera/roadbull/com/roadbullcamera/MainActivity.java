package camera.roadbull.com.roadbullcamera;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import camera.roadbull.com.roadbullcamera.Constants.Constants;
import camera.roadbull.com.roadbullcamera.Utilities.MainUtils;
import camera.roadbull.com.roadbullcamera.Utilities.PermissionUtils;
import camera.roadbull.com.roadbullcamera.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding activityMainBinding;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setListeners();
    }

    private void setListeners() {
        activityMainBinding.contentMain.captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtils.checkCameraStoragePermissionGranted(MainActivity.this)) {
                    captureImage();
                }
            }
        });
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = getOutputMediaFileUri();
        if (fileUri == null) {
            return;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri() {
        imageFile = getOutputMediaFile();
        return MainUtils.getUriForFile(this, imageFile);
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + Constants.FOLDER_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File file = new File(mediaStorageDir.getPath() + File.separator +
                Constants.IMAGE_FILE_NAME +
                MainUtils.getCurrentTime()+  ".jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constants.PERMISSION_REQUEST) {
           boolean permsGiven = PermissionUtils.evaluatePermissionsResults(this, permissions, grantResults,
                    getString(R.string.camera_storage), getString(R.string.permisssion_request_camera));
            if(permsGiven) {
                captureImage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                displayPreviewImage();
            }
            else {
                MainUtils.showToast(this,this.getResources().getString(R.string.image_not_captured), Toast.LENGTH_SHORT);
            }
        }
    }
    /**
     * Google recommends to use glide library as it manages most of the things ,
     * i.e displaying large sized images to prevent Out Of Memory Error
     */
    private void displayPreviewImage() {
        if(imageFile!= null && imageFile.exists()){
            Uri imageUri = Uri.fromFile(imageFile);
            Glide.with(this)
                    .load(imageUri)
                    .into(activityMainBinding.contentMain.previewImageView);
        }
    }

}


