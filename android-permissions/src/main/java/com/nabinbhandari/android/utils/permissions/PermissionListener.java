package com.nabinbhandari.android.utils.permissions;

/**
 * <pre>
 * The permission Listener interface for handling permission callbacks.
 * Created by Nabin Bhandari on 6/11/2017 on 9:42 PM
 * </pre>
 *
 * @author Nabin Bhandari
 */
@SuppressWarnings("WeakerAccess")
public interface PermissionListener {

    /**
     * This function will be invoked if permission was already allowed or has just been granted.
     *
     * @param requestCode Unique request code provided while requesting permission(s).
     */
    void onPermissionGranted(int requestCode);

    /**
     * This function will be invoked if the user denies the permission.
     *
     * @param requestCode Unique request code provided while requesting permission(s).
     */
    void onPermissionDenied(int requestCode);

}
