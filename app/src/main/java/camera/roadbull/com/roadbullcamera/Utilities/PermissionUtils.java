package camera.roadbull.com.roadbullcamera.Utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import camera.roadbull.com.roadbullcamera.Constants.Constants;
import camera.roadbull.com.roadbullcamera.R;

/**
 * Created by kapilbakshi on 06/06/17.
 */

public class PermissionUtils {


    public static boolean evaluatePermissionsResults(Activity activity, String[] permissions, int[] grantResults,
                                                     String settingsMessage, String dialogMessage) {
        boolean allPermissionGranted = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                allPermissionGranted = false;
                break;
            }
        }
        if (allPermissionGranted) {
            return true;
        } else {
            ArrayList<String> permissionsList = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED &&
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    permissionsList.add(permission);
                }
            }
            if (permissionsList.isEmpty()) {
                PermissionUtils.navigateToSettingsScreen(activity, settingsMessage);
            } else {
                PermissionUtils.showMessageOKCancel(activity,permissionsList,dialogMessage);
            }
            return false;
        }
    }

    private static void navigateToSettingsScreen(Context context, String permissions) {
        MainUtils.showToast(context,context.getString(R.string.permission_settings,permissions), Toast.LENGTH_SHORT);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void showMessageOKCancel(final Activity activity, final ArrayList<String> permissions, final String message) {
        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PermissionUtils.askForPermission(activity,permissions);
            }
        };
        new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle)
                .setMessage(message)
                .setPositiveButton(R.string.give_permission, okListener)
                .setCancelable(false)
                .create()
                .show();
    }

    public static void askForPermission(Activity activity, List<String> permissions) {
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]),
                    Constants.PERMISSION_REQUEST);
        }
    }

    public static boolean checkCameraStoragePermissionGranted(Activity activity) {
        ArrayList<String> mandatoryPermissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23) {
            addPermission(activity,mandatoryPermissions, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA);
        }
        if (!mandatoryPermissions.isEmpty()) {
            MainUtils.showToast(activity,activity.getResources().getString(R.string.permisssion_request_camera),Toast.LENGTH_SHORT);
            askForPermission(activity,mandatoryPermissions);
        }
        return mandatoryPermissions.isEmpty();
    }

    public static void addPermission(Activity activity, List<String> permissionsList, String... permissions) {
        for (String permission : permissions) {
            if (isPermissionDenied(activity, permission)) {
                permissionsList.add(permission);
            }
        }
    }

    public static boolean isPermissionDenied(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;
    }
}
