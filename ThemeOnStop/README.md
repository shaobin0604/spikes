# 测试透明主题对 Activity#onStop 方法调用的影响


1. `AndroidManifest.xml` 声明 `Main2Activity` 主题透明
2. `MainActivity` 启动 `Main2Activity`
3. `MainActivity` 的 `onStop` 方法不会被调用。即使 `Main2Activity` 在其 `onCreate` 方法里重设非透明的 Theme 也是同样的情况。
4. `Main3Activity` 在 `AndroidManifest.xml` 声明为非透明主题，在其 `onCreate`
   方法里重设为透明主题，结果无效，显示为 **黑色**

**结论**

系统是否调用前一个被覆盖 `Activity` 的 `onStop` 方法只取决于覆盖 `Activity` 在 `AndroidManifest.xml` 中声明的主题是否透明，而与代码调用 `setTheme` 设置的主题无关
