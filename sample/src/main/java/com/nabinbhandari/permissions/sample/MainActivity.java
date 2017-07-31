package com.nabinbhandari.permissions.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void requestPhone(View view) {
        Permissions.runPermissionCheck(this, "Phone permission is required because...",
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(MainActivity.this, "Phone granted.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, "Phone denied.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public boolean onBlocked(Context context, ArrayList<String> blockedList) {
                        Toast.makeText(context, "Phone blocked.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public void onJustBlocked(Context context, ArrayList<String> justBlockedList,
                                              ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, "Phone just blocked.",
                                Toast.LENGTH_SHORT).show();
                    }
                }, Manifest.permission.CALL_PHONE);
    }

    public void requestCamera(View view) {
        Permissions.runPermissionCheck(this, "Camera and storage permissions are required because...",
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(MainActivity.this, "Camera+Storage granted.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, "Camera+Storage Denied:\n" + Arrays.toString(deniedPermissions.toArray()),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public boolean onBlocked(Context context, ArrayList<String> blockedList) {
                        Toast.makeText(context, "Camera+Storage blocked:\n" + Arrays.toString(blockedList.toArray()),
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public void onJustBlocked(Context context, ArrayList<String> justBlockedList,
                                              ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, "Camera+Storage just blocked:\n" + Arrays.toString(deniedPermissions.toArray()),
                                Toast.LENGTH_SHORT).show();
                    }
                }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }

}
