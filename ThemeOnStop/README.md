# 测试透明主题对 Activity#onStop 方法调用的影响


1. `AndroidManifest.xml` 声明 `Main2Activity` 主题透明
2. `MainActivity` 启动 `Main2Activity`
3. `MainActivity` 的 `onStop` 方法不会被调用

即使 `Main2Activity` 在其 `onCreate` 方法里重设非透明的 Theme 也是同样的情况。