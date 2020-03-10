# Intent Flags 测试

## FLAG_ACTIVITY_FORWARD_RESULT

If set and this intent is being used to launch a new activity from an existing one, then th  e reply target of the existing activity will be transfered to the new activity. This way the new activity can call Activity.setResult(int) and have that result sent back to the reply target of the original activity.

如果在 Intent 中设置此 flag 从现有的 activity 去开启一个新的 activity ，现有的 activty 将会把回复的目标转移给新 activity. 新 activity 可以调用 setResult() 将结果发送给现有 activity 的回复目标。

例如：A 通过 startActivityForResult 启动 B，B 启动 C，但 B 为过渡页可以 finish 了，A 在期望 C 把结果返回。这种情况，B 可以在启动 C 的时候加入该flag。


## FLAG_ACTIVITY_PREVIOUS_IS_TOP

If set and this intent is being used to launch a new activity from an existing one, the current activity will not be counted as the top activity for deciding whether the new intent should be delivered to the top instead of starting a new one. The previous activity will be used as the top, with the assumption being that the current activity will finish itself immediately.

如果当前的 activity 在开启新 activity 的 intent 设置此 flag， 当前 activity 之前的 activity 将被当视为 top，当前的 activity 将立即结束。

例如：栈中情况 A,B,C，C 启动 D 时使用此标志，在启动时 C 不会被当成栈顶 Activity，而是 B 作为栈顶启动 D，然后 C 会 finish()。经常与 FLAG_ACTIVITY_FORWARD_RESULT 一起配合使用。

## 参考

- [Android 的任务栈 Task 与启动模式](https://mp.weixin.qq.com/s/V1pPrW1UAz13lJU3rlLfNA)
- [android_forward_result](https://gist.github.com/mcelotti/cc1fc8b8bc1224c2f145)
