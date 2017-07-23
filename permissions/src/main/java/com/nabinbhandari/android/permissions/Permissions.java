package com.nabinbhandari.android.permissions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

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
@SuppressWarnings("WeakerAccess")
public class Permissions {

    /**
     * Dialog title for showing permission rationale, or while asking user to go to settings.
     */
    public static String dialogTitle = "Permission Required";

    /**
     * In the case the user has previously set some permissions not to ask again, if this flag is
     * true the user will be prompted to go to settings and provide the permissions otherwise the
     * method {@link PermissionHandler#onDenied(Context, ArrayList)} will be invoked directly.
     */
    public static boolean sendSetNotToAskAgainToSettings = true;

    /**
     * The button text for "settings" while asking user to go to settings.
     */
    public static String settingsText = "Settings";

    /**
     * The message to be shown in the dialog if some permissions have been set not to ask again.
     */
    public static String forceDeniedDialogMessage = "Required permission(s) have been set" +
            " not to ask again! Please provide them from settings.";

    /**
     * Flag indicating whether to show debug logs or not.
     */
    public static boolean showLogs = true;

    /**
     * Runs permission check and calls the callback methods of permission listener accordingly.
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

            boolean allPermissionProvided = true;
            for (String aPermission : permissions) {
                if (context.checkSelfPermission(aPermission) != PackageManager.PERMISSION_GRANTED) {
                    allPermissionProvided = false;
                    break;
                }
            }

            if (allPermissionProvided) {
                permissionHandler.onGranted();
                log("Permission(s) " + (PermissionsActivity.permissionHandler == null ?
                        "already granted." : "just granted from settings."));
                PermissionsActivity.permissionHandler = null;
            } else {
                PermissionsActivity.permissionHandler = permissionHandler;
                Intent intent = new Intent(context, PermissionsActivity.class);
                intent.putExtra(PermissionsActivity.EXTRA_PERMISSIONS, permissions);
                intent.putExtra(PermissionsActivity.EXTRA_RATIONALE, rationale);
                context.startActivity(intent);
            }
        }
    }

    static void log(String message) {
        if (showLogs) Log.d("Permissions", message);
    }

}
