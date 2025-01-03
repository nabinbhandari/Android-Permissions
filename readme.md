Android Runtime Permission Library
==================================
Easily handle runtime permissions in android.

 * Very short code.
 * Handle "don't ask again" condition (In newer versions, multiple denials from user lead to this).
 * Can request from any context (Activity, Service, Fragment, etc).
 * Can check multiple permissions at once.
 * Light weight (13 KB).
 * Used by hundreds of developers.
 * Quick support.
 * Open source and fully customizable.

Dependency:
-----------

**Gradle (MavenCentral)**
```
implementation 'com.nabinbhandari.android:permissions:4.0.0'
```

Usage:
------

First declare your permissions in the manifest.
Example:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

**Single permission:**
```java
Permissions.check(this/*context*/, Manifest.permission.CALL_PHONE, null, new PermissionHandler() {
    @Override
    public void onGranted() {
        // do your task.
    }
});
```

**Multiple permissions:**
```java
String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS};
Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
    @Override
    public void onGranted() {
        // do your task.
    }
});
```

**Customized permissions request:**
```java
String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
String rationale = "Please provide location permission so that you can ...";
Permissions.Options options = new Permissions.Options()
        .setRationaleDialogTitle("Info")
        .setSettingsDialogTitle("Warning");

Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
    @Override
    public void onGranted() {
        // do your task.
    }

    @Override
    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
        // permission denied, block the feature.
    }
});
```

 * You can also override other methods like onDenied, onJustBlocked, etc if you want to change the default behaviour.
 * Dialog messages and texts can be modified by building the options parameter.
 * See documentation in the source code for more customizations.
 
---

**If you find this library useful, please consider starring this repository from the top of this page.**
[![](https://i.imgur.com/oSLuE0e.png)](#)

---

## LICENSE
    Copyright 2025 Nabin Bhandari

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Developed by:
Nabin Bhandari  
[Email](mailto:bnabin51@gmail.com) | [Facebook](https://facebook.com/bnabin51) | [Paypal](https://paypal.me/bnabin51)
