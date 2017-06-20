Android Runtime Permission Library
==================================
Easily handle runtime permissions in android.

 * Short code.
 * Handle "don't ask again" condition.
 * Open source.
 * Light weight.

Dependency:
-----------

**Gradle (Jcenter)**
```
compile 'com.nabinbhandari.android.utils.permissions:android-permissions:1.0'
```
    
    
**Maven**
```
<dependency>
  <groupId>com.nabinbhandari.android.utils.permissions</groupId>
  <artifactId>android-permissions</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```    

Usage:
------

```java
Permissions.runPermissionCheck(this, 2002, "Permission needed..",
        new PermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode) {
                //do task
            }

            @Override
            public void onPermissionDenied(int requestCode) {
                //show error
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
```

```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Permissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
}
```

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
