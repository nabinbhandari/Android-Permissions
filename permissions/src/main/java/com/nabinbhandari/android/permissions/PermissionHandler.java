package com.nabinbhandari.android.permissions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * The class for handling permission callbacks.
 * <p>
 * Created on 7/16/2017 on 3:42 PM
 *
 * @author Nabin Bhandari
 */
@SuppressWarnings("WeakerAccess")
public abstract class PermissionHandler {

    private static final String TAG = "PermissionHandler";

    /**
     * This method will be called if permission(s) are granted.
     */
    public abstract void onGranted();

    /**
     * This method will be called if permission(s) have just been granted.
     */
    public void onJustGranted() {
        Log.d(TAG, "Permission(s) just granted.");
        onGranted();
    }

    /**
     * This method will be called if some of the requested permissions have been denied.
     *
     * @param context           The context for showing the default toast.
     * @param deniedPermissions the list of permissions which have not been allowed yet.
     */
    public void onDenied(Context context, List<String> deniedPermissions) {
        StringBuilder builder = new StringBuilder();
        builder.append("Denied:");
        for (String permission : deniedPermissions) {
            builder.append(" ");
            builder.append(permission);
        }
        Log.d(TAG, builder.toString());
        Toast.makeText(context, "Permission Denied.", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will be called if some permissions have been set not to ask again.
     *
     * @param permissionsWhichCantBeAsked the list of permissions which have been set not to ask again.
     * @return The overrider of this method should return true if no further action is needed,
     * and should return false if the default action is to be taken, i.e. send user to settings.
     */
    public boolean onSetNotToAskAgain(List<String> permissionsWhichCantBeAsked) {
        StringBuilder builder = new StringBuilder();
        builder.append("Set not to ask again:");
        for (String permission : permissionsWhichCantBeAsked) {
            builder.append(" ");
            builder.append(permission);
        }
        Log.d(TAG, builder.toString());
        return false;
    }

    /**
     * When some permissions are set not to ask again and a dialog is shown to ask to go to settings,
     * this method will be called if the user chooses either to go to settings or cancels the dialog.
     *
     * @param gone              true if user has gone to settings, false if the dialog is cancelled.
     * @param context           the context for showing the default toast.
     * @param deniedPermissions the list of permissions which have not been allowed yet.
     * @param requestCode       the request code using which intent has been started to go to settings.
     */
    public void onGoneToSettings(boolean gone, Context context, List<String> deniedPermissions,
                                 int requestCode) {
        Log.d(TAG, "Gone to settings: " + gone + (gone ? " code:" + requestCode : ""));
        if (!gone) {
            onDenied(context, deniedPermissions);
        }
    }

}
