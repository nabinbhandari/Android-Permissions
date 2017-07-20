package com.nabinbhandari.android.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * Helper class for handling runtime permissions.
 * Created on 6/11/2017 on 9:32 PM
 * </pre>
 *
 * @author Nabin Bhandari
 */
public class Permissions extends Activity {

    /**
     * Dialog title for showing permission rationale, or while asking user to go to settings.
     */
    public static String dialogTitle = "Permission Required";

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
     * If a user has forcefully denied some permissions and has been sent to settings, and if
     * permission should not be checked again after returning, this flag should be set to false.
     */
    public static boolean recheckPermissionsAfterSettings = true;

    private static final int RC_SETTINGS = 6739;
    private static final int RC_PERMISSION = 6937;
    private static final String TAG = "Permissions";
    private static final String EXTRA_PERMISSIONS = "extra_permissions";

    private static boolean cleanHandlerOnDestroy = true;
    private static PermissionHandler permissionHandler;

    private String[] permissions;

    /**
     * Runs permission check and calls the callback methods of permission listener accordingly.
     *
     * @param activity          Activity via which permission is to be requested.
     * @param rationale         Statement to be shown to user if s/he has denied permission earlier.
     *                          If this parameter is null, a rationale will not be shown.
     * @param permissionHandler The permission Listener object for handling callbacks.
     * @param permission        The permission to request.
     * @param otherPermissions  Other permissions if multiple permissions are to be requested at once.
     */
    public static void runPermissionCheck(final Activity activity, String rationale,
                                          final PermissionHandler permissionHandler,
                                          String permission, String... otherPermissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionHandler.onGranted();
            Log.d(TAG, "Android version < 23");
        } else {
            boolean allPermissionProvided = true;
            final List<String> deniedPermissions = new ArrayList<>();
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionProvided = false;
                deniedPermissions.add(permission);
            }
            for (String otherPermission : otherPermissions) {
                if (activity.checkSelfPermission(otherPermission) != PackageManager.PERMISSION_GRANTED) {
                    allPermissionProvided = false;
                    deniedPermissions.add(otherPermission);
                }
            }
            if (allPermissionProvided) {
                permissionHandler.onGranted();
                Log.d(TAG, "Permission(s) already granted.");
            } else {
                boolean rationaleShouldBeShown = false;
                if (TextUtils.isEmpty(rationale)) {
                    rationaleShouldBeShown = false;
                } else for (String unGrantedPermission : deniedPermissions) {
                    if (activity.shouldShowRequestPermissionRationale(unGrantedPermission)) {
                        rationaleShouldBeShown = true;
                        break;
                    }
                }
                if (rationaleShouldBeShown) {
                    new AlertDialog.Builder(activity).setTitle(dialogTitle)
                            .setMessage(rationale)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(activity, deniedPermissions, permissionHandler);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    permissionHandler.onDenied(activity, deniedPermissions);
                                }
                            })
                            .setCancelable(false).create().show();
                } else {
                    requestPermissions(activity, deniedPermissions, permissionHandler);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void requestPermissions(Activity activity, List<String> permissions,
                                           PermissionHandler permissionHandler) {
        cleanHandlerOnDestroy = false;
        Permissions.permissionHandler = permissionHandler;
        Intent intent = new Intent(activity, Permissions.class);
        intent.putExtra(EXTRA_PERMISSIONS, permissions.toArray(new String[0]));
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(EXTRA_PERMISSIONS)) {
            permissions = getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null
                    && permissions.length > 0) {
                getWindow().setStatusBarColor(0);
                requestPermissions(permissions, RC_PERMISSION);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cleanHandlerOnDestroy = true;
                    }
                }, 1000);
            } else finish();
        } else finish();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissionHandler == null) {
            finish();
            return;
        }
        if (grantResults.length == 0) {
            permissionHandler.onDenied(this, Arrays.asList(this.permissions));
            finish();
        } else {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                permissionHandler.onJustGranted();
                finish();
            } else {
                handlePermissionsSetNotToAskAgain(permissions, permissionHandler);
            }
        }
    }

    /**
     * Helper function to handle the case where some permissions have been set by user not to ask again.
     * <br/>This method first checks if some of the permissions are forever denied(set never to ask again);
     * if so an alert dialog will be shown to tell user to open settings and grant the required permissions.
     * <p>
     * <br/>
     * <strong>Note: This function must be called from inside the onRequestPermissionsResult() method.</strong>
     *
     * @param permissions The array of permissions passed in the callback function onRequestPermissionsResult().
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void handlePermissionsSetNotToAskAgain(String[] permissions,
                                                   final PermissionHandler permissionHandler) {
        final List<String> deniedPermissions = new ArrayList<>();
        List<String> permissionsWhichCantBeAsked = new ArrayList<>();
        for (String permission : permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                Log.d(TAG, permission + " denied.");
                deniedPermissions.add(permission);
            } else if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Cant't ask " + permission + " again.");
                permissionsWhichCantBeAsked.add(permission);
                deniedPermissions.add(permission);
            } //else the particular permission(not all) has just been allowed.
        }

        if (permissionsWhichCantBeAsked.size() == 0) {
            permissionHandler.onDenied(this, deniedPermissions);
            finish();
        } else if (!permissionHandler.onSetNotToAskAgain(this, permissionsWhichCantBeAsked)) {
            final Permissions activity = this;
            new AlertDialog.Builder(this).setTitle(dialogTitle)
                    .setMessage(forceDeniedDialogMessage)
                    .setPositiveButton(settingsText, new DialogInterface.OnClickListener() {
                        @Override
                        @SuppressWarnings("InlinedAPI")
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", activity.getPackageName(), null));
                            if (recheckPermissionsAfterSettings) {
                                startActivityForResult(intent, RC_SETTINGS);
                            } else {
                                startActivity(intent);
                                finish();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            permissionHandler.onDenied(activity, deniedPermissions);
                            finish();
                        }
                    })
                    .setCancelable(false).create().show();
        } else finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SETTINGS && permissionHandler != null) {
            checkPermissionsAfterSettings(this, permissionHandler, permissions);
        } else finish();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void checkPermissionsAfterSettings(Activity activity, PermissionHandler permissionHandler,
                                                      String[] permissions) {
        boolean allPermissionProvided = true;
        final List<String> deniedPermissions = new ArrayList<>();
        for (String otherPermission : permissions) {
            if (activity.checkSelfPermission(otherPermission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionProvided = false;
                deniedPermissions.add(otherPermission);
            }
        }
        if (allPermissionProvided) {
            Log.d(TAG, "Permission(s) just granted from settings.");
            permissionHandler.onJustGranted();
        } else {
            Log.d(TAG, "Permission(s) not provided from settings, asking again.");
            requestPermissions(activity, deniedPermissions, permissionHandler);
        }
        activity.finish();
    }

    @Override
    protected void onDestroy() {
        if (cleanHandlerOnDestroy) {
            permissionHandler = null;
        }
        super.onDestroy();
    }

}
