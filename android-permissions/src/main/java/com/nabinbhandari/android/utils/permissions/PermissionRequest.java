package com.nabinbhandari.android.utils.permissions;

import android.app.Activity;

/**
 * <pre>
 * Represents an object for permission request.
 * Created on 6/11/2017 on 10:14 PM
 * </pre>
 *
 * @author Nabin Bhandari
 */
class PermissionRequest {

    Activity activity;
    int requestCode;
    PermissionListener permissionListener;

    /**
     * Creates an object representing a permission request.
     *
     * @param activity           The activity via which permissions are requested.
     * @param requestCode        Unique request code to handle permission requests.
     * @param permissionListener The permission Listener object for handling callbacks.
     */
    PermissionRequest(Activity activity, int requestCode, PermissionListener permissionListener) {
        this.activity = activity;
        this.requestCode = requestCode;
        this.permissionListener = permissionListener;
    }

}
