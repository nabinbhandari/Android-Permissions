package com.nabinbhandari.android.utils.permissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Helper class for handling runtime permissions.
 * Created on 6/11/2017 on 9:32 PM
 * </pre>
 *
 * @author Nabin Bhandari
 */
public class Permissions {

    /**
     * List of pending permission requests.
     */
    private static List<PermissionRequest> requests = new ArrayList<>();

    /**
     * @return pending permission request for given activity and request code.
     */
    private static PermissionRequest findPermissionRequest(Activity activity, int requestCode) {
        for (PermissionRequest permissionRequest : requests) {
            if (requestCode == permissionRequest.requestCode && activity.equals(permissionRequest.activity)) {
                return permissionRequest;
            }
        }
        return null;
    }

    /**
     * Runs permission check and calls the callback methods of permission listener accordingly.
     *
     * @param activity           Activity via which permission is to be requested.
     * @param requestCode        Unique request code.
     * @param rationale          Statement to be shown to user if s/he has denied permission earlier.
     * @param permissionListener The permission Listener object for handling callbacks.
     * @param permission         The permission to request.
     * @param otherPermissions   Other permissions if multiple permissions are to be requested at once.
     */
    public static void runPermissionCheck(final Activity activity, final int requestCode, String rationale,
                                          final PermissionListener permissionListener,
                                          String permission, String... otherPermissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionListener.onPermissionGranted(requestCode);
        } else {
            boolean allPermissionProvided = true;
            final List<String> unGrantedPermissions = new ArrayList<>();
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                allPermissionProvided = false;
                unGrantedPermissions.add(permission);
            }
            for (String otherPermission : otherPermissions) {
                if (activity.checkSelfPermission(otherPermission) == PackageManager.PERMISSION_DENIED) {
                    allPermissionProvided = false;
                    unGrantedPermissions.add(otherPermission);
                }
            }
            if (allPermissionProvided) {
                permissionListener.onPermissionGranted(requestCode);
            } else {
                boolean rationaleShouldBeShown = false;
                for (String unGrantedPermission : unGrantedPermissions) {
                    if (activity.shouldShowRequestPermissionRationale(unGrantedPermission)) {
                        rationaleShouldBeShown = true;
                        break;
                    }
                }
                if (rationaleShouldBeShown) {
                    new AlertDialog.Builder(activity).setTitle("Permission Required")
                            .setMessage(rationale)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(activity, unGrantedPermissions, requestCode,
                                            permissionListener);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    permissionListener.onPermissionDenied(requestCode);
                                }
                            })
                            .setCancelable(false)
                            .create().show();
                } else {
                    requestPermissions(activity, unGrantedPermissions, requestCode, permissionListener);
                }
            }
        }
    }

    /**
     * Requests the provided list of permissions.
     *
     * @param activity           The activity via which permissions are to be requested.
     * @param permissions        The list of permissions to be requested.
     * @param requestCode        Unique request code for handling permission requests.
     * @param permissionListener The permission Listener object for handling callbacks.
     */
    private static void requestPermissions(Activity activity, List<String> permissions,
                                           int requestCode, PermissionListener permissionListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionListener.onPermissionGranted(requestCode);
        } else {
            activity.requestPermissions(permissions.toArray(new String[0]), requestCode);
            requests.add(new PermissionRequest(activity, requestCode, permissionListener));
        }
    }

    /**
     * <p>
     * This method must be called inside the {@link android.app.Activity#onRequestPermissionsResult(int, String[], int[])} as follows.</p>
     * <pre><code>
     * public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     *     super.onRequestPermissionsResult(requestCode, permissions, grantResults);
     *     Permissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
     * }
     * </code></pre>
     *
     * @param activity     The activity
     * @param requestCode  unique request code.
     * @param permissions  the array of permissions
     * @param grantResults the results of permission request.
     */
    public static void onRequestPermissionsResult(Activity activity, int requestCode,
                                                  String[] permissions, int[] grantResults) {
        PermissionRequest request = findPermissionRequest(activity, requestCode);
        if (request != null) {
            requests.remove(request);
            PermissionListener permissionListener = request.permissionListener;

            boolean allPermissionsGranted = true;
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
            }

            if (allPermissionsGranted) {
                permissionListener.onPermissionGranted(requestCode);
            } else {
                handlePermissionsSetToNeverAskAgain(activity, permissions, request);
            }
        }
    }

    /**
     * Helper function to handle the case where some permissions have been set by user not to ask again.
     * <br/>This method first checks if some of the permissions are forever denied(set never to ask again);
     * if so an alert dialog will be shown to tell user to open settings and grant the required permissions.
     * <p>
     * <br/><br/><strong>Note: This function must be called from inside the onRequestPermissionsResult() method.</strong>
     *
     * @param activity    The activity for checking permissions.
     * @param permissions The array of permissions passed in the callback function onRequestPermissionsResult().
     * @param request     Permission request object.
     */
    private static void handlePermissionsSetToNeverAskAgain(final Activity activity, String[] permissions,
                                                            final PermissionRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (activity.shouldShowRequestPermissionRationale(permission)) {
                    //denied
                    Log.e("denied", permission);
                    request.permissionListener.onPermissionDenied(request.requestCode);
                    break;
                } else {
                    if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                        //allowed
                        Log.e("allowed", permission);
                    } else {
                        //set to never ask again
                        Log.e("set to never ask again", permission);
                        new AlertDialog.Builder(activity).setTitle("Permission Required")
                                .setMessage("Permission forcefully denied! Please provide permissions from settings.")
                                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    @SuppressWarnings("InlinedAPI")
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", activity.getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        activity.startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        request.permissionListener.onPermissionDenied(request.requestCode);
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                        break;
                    }
                }
            }
        }
    }

}
