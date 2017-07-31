Android Runtime Permission Library
==================================
Easily handle runtime permissions in android.

 * Short code.
 * Can check multiple permissions at once.
 * Handle "don't ask again" condition.
 * Light weight (12 KB).
 * Open source and fully customizable.

Dependency:
-----------

**Gradle (Jcenter)**
```
compile 'com.nabinbhandari.android:permissions:3.5'
```


**Maven**
```
<dependency>
  <groupId>com.nabinbhandari.android</groupId>
  <artifactId>permissions</artifactId>
  <version>3.5</version>
  <type>pom</type>
</dependency>
```

Usage:
------

**Single permission:**
```java
Permissions.check(this, Manifest.permission.CALL_PHONE, null,
        new PermissionHandler() {
            @Override
            public void onGranted() {
                //do your task.
            }
        });

````

**Multiple permissions:**
```java
Permissions.check(this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},
        "Camera and storage permissions are required because...", new Permissions.Options()
                .setSettingsDialogTitle("Warning!").setRationaleDialogTitle("Info"),
        new PermissionHandler() {
            @Override
            public void onGranted() {
                //do your task
            }
        });
```

 * You can also override other methods like onDenied, onJustBlocked, etc if you want to change the default behaviour.
 * Dialog messages and texts can be modified by building the options parameter.
 * See documentation in the source code for more customizations.

## LICENSE

    Copyright 2017 Nabin Bhandari

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
