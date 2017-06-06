package camera.roadbull.com.roadbullcamera.Utilities;

/**
 * Created by kapilbakshi on 06/06/17.
 */

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainUtils {


    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return df.format(c.getTime());
    }

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
    }

    public static void showToast(Context cnt, String text, int duration) {
        Toast toast = Toast.makeText(cnt, text, duration);
        toast.show();
    }
}
