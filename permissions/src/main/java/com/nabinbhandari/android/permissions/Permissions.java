package com.nabinbhandari.android.permissions;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * <pre>
 * Helper class for handling runtime permissions.
 * Created on 6/11/2017 on 9:32 PM
 * </pre>
 *
 * @author Nabin Bhandari
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Permissions {

    static boolean loggingEnabled = true;

    /**
     * Disable logs.
     */
    public static void disableLogging() {
        loggingEnabled = false;
    }

    static void log(String message) {
        if (loggingEnabled) Log.d("Permissions", message);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void proceed(Context context, ArrayList<String> permissions, String rationale,
                                Options options, PermissionHandler handler) {
        boolean allPermissionProvided = true;
        for (String aPermission : permissions) {
            if (context.checkSelfPermission(aPermission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionProvided = false;
                break;
            }
        }

        if (allPermissionProvided) {
            handler.onGranted();
            log("Permission(s) " + (PermissionsActivity.permissionHandler == null ?
                    "already granted." : "just granted from settings."));
            PermissionsActivity.permissionHandler = null;
        } else {
            PermissionsActivity.permissionHandler = handler;
            Intent intent = new Intent(context, PermissionsActivity.class);
            intent.putExtra(PermissionsActivity.EXTRA_PERMISSIONS, permissions);
            intent.putExtra(PermissionsActivity.EXTRA_RATIONALE, rationale);
            intent.putExtra(PermissionsActivity.EXTRA_OPTIONS, options);
            context.startActivity(intent);
        }
    }

    /**
     * Check/Request permissions and calls the callback methods of permission handler accordingly.
     *
     * @param context           Android context.
     * @param rationale         Explanation to be shown to user if s/he has denied permission
     *                          earlier. If this parameter is null, permissions will be requested
     *                          without showing the rationale dialog.
     * @param permissionHandler The permission handler object for handling callbacks of various
     *                          user actions such as permission granted, permission denied, etc.
     * @param permission        The permission to request.
     * @param otherPermissions  (Optional) Other permissions if multiple permissions are to be
     *                          requested at once.
     * @deprecated Use {@link #check} instead.
     */
    public static void runPermissionCheck(final Context context, String rationale,
                                          final PermissionHandler permissionHandler,
                                          String permission, String... otherPermissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionHandler.onGranted();
            log("Android version < 23");
        } else {
            ArrayList<String> permissions = new ArrayList<>();
            permissions.add(permission);
            Collections.addAll(permissions, otherPermissions);
            proceed(context, permissions, rationale, null, permissionHandler);
        }
    }

    public static void check(final Context context, String[] permissions, String rationale,
                             Options options, final PermissionHandler handler) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            handler.onGranted();
            log("Android version < 23");
        } else {
            ArrayList<String> permissionsList = new ArrayList<>();
            Collections.addAll(permissionsList, permissions);
            proceed(context, permissionsList, rationale, options, handler);
        }
    }

    public static void check(Context context, String permission, PermissionHandler handler) {
        check(context, new String[]{permission}, null, null, handler);
    }

    public static void check(Context context, String permission, String rationale,
                             PermissionHandler handler) {
        check(context, new String[]{permission}, rationale, null, handler);
    }

    public static void check(Context context, String[] permissions, PermissionHandler handler) {
        check(context, permissions, null, null, handler);
    }

    /**
     * Options to customize while requesting permissions.
     */
    public static class Options implements Serializable {
        String settingsText = "Settings";
        String rationaleDialogTitle = "Permissions Required";
        String settingsDialogTitle = "Permissions Required";
        String settingsDialogMessage = "Required permission(s) have been set" +
                " not to ask again! Please provide them from settings.";
        boolean sendBlockedToSettings = true;

        /**
         * Sets the button text for "settings" while asking user to go to settings.
         *
         * @return same instance.
         */
        public Options setSettingsText(String settingsText) {
            this.settingsText = settingsText;
            return this;
        }

        /**
         * Sets the title text for permission rationale dialog.
         *
         * @return same instance.
         */
        public Options setRationaleDialogTitle(String rationaleDialogTitle) {
            this.rationaleDialogTitle = rationaleDialogTitle;
            return this;
        }

        /**
         * Sets the title text of the dialog which asks user to go to settings, in the case when
         * permission(s) have been set not to ask again.
         *
         * @return same instance.
         */
        public Options setSettingsDialogTitle(String settingsDialogTitle) {
            this.settingsDialogTitle = settingsDialogTitle;
            return this;
        }

        /**
         * Sets the message of the dialog which asks user to go to settings, in the case when
         * permission(s) have been set not to ask again.
         *
         * @return same instance.
         */
        public Options setSettingsDialogMessage(String settingsDialogMessage) {
            this.settingsDialogMessage = settingsDialogMessage;
            return this;
        }

        /**
         * In the case the user has previously set some permissions not to ask again, if this flag
         * is true the user will be prompted to go to settings and provide the permissions otherwise
         * the method {@link PermissionHandler#onDenied(Context, ArrayList)} will be invoked
         * directly.
         *
         * @return same instance.
         */
        public Options sendDontAskAgainToSettings(boolean send) {
            sendBlockedToSettings = send;
            return this;
        }
    }

}
