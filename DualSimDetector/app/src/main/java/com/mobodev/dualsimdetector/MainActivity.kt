package com.mobodev.dualsimdetector

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class MainActivity : AppCompatActivity() {
    private lateinit var tv: TextView
    private lateinit var tv2: TextView

    // ///////////////////////////////////
    var ISDOUBLE: String? = null
    var SIMCARD: String? = null
    var SIMCARD_1: String? = null
    var SIMCARD_2: String? = null
    var isDouble = false

    // //////////////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv = findViewById(R.id.text);
        tv2 = findViewById(R.id.text2);

        tv2.text = "不知道哪个卡可用！";

        getNumber();
    }

    private fun getNumber() {
        val subscriptionManager = getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

        val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val phoneNumber1 = tm.line1Number

        // String phoneNumber2 = tm.getGroupIdLevel1();
        initIsDoubleTelephone(this)
        if (isDouble) {
            // tv.setText("这是双卡手机！");
            tv.text = "本机号码是： $phoneNumber1 这是双卡手机！"
        } else {
            // tv.setText("这是单卡手机");
            tv.text = "本机号码是： $phoneNumber1 这是单卡手机"
        }
    }

    private fun initIsDoubleTelephone(context: Context) {
        isDouble = true
        var method: Method? = null
        var result0: Any? = null
        var result1: Any? = null
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            // 只要在反射getSimStateGemini 这个函数时报了错就是单卡手机（这是我自己的经验，不一定全正确）
            method = TelephonyManager::class.java.getMethod("getSimStateGemini", *arrayOf<Class<*>?>(Int::class.javaPrimitiveType))
            // 获取SIM卡1
            result0 = method.invoke(tm, arrayOf<Any>(0))
            // 获取SIM卡2
            result1 = method.invoke(tm, arrayOf<Any>(1))
        } catch (e: SecurityException) {
            isDouble = false
            e.printStackTrace()
            // System.out.println("1_ISSINGLETELEPHONE:"+e.toString());
        } catch (e: NoSuchMethodException) {
            isDouble = false
            e.printStackTrace()
            // System.out.println("2_ISSINGLETELEPHONE:"+e.toString());
        } catch (e: IllegalArgumentException) {
            isDouble = false
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            isDouble = false
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            isDouble = false
            e.printStackTrace()
        } catch (e: Exception) {
            isDouble = false
            e.printStackTrace()
            // System.out.println("3_ISSINGLETELEPHONE:"+e.toString());
        }
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = sp.edit()
        if (isDouble) {
            // 保存为双卡手机
            editor.putBoolean(ISDOUBLE, true)
            // 保存双卡是否可用
            // 如下判断哪个卡可用.双卡都可以用
            if (result0.toString() == "5" && result1.toString() == "5") {
                if (sp.getString(SIMCARD, "2") != "0" && sp.getString(SIMCARD, "2") != "1") {
                    editor.putString(SIMCARD, "0")
                }
                editor.putBoolean(SIMCARD_1, true)
                editor.putBoolean(SIMCARD_2, true)
                tv2!!.text = "双卡可用"
            } else if (result0.toString() != "5" && result1.toString() == "5") { // 卡二可用
                if (sp.getString(SIMCARD, "2") != "0" && sp.getString(SIMCARD, "2") != "1") {
                    editor.putString(SIMCARD, "1")
                }
                editor.putBoolean(SIMCARD_1, false)
                editor.putBoolean(SIMCARD_2, true)
                tv2!!.text = "卡二可用"
            } else if (result0.toString() == "5" && result1.toString() != "5") { // 卡一可用
                if (sp.getString(SIMCARD, "2") != "0" && sp.getString(SIMCARD, "2") != "1") {
                    editor.putString(SIMCARD, "0")
                }
                editor.putBoolean(SIMCARD_1, true)
                editor.putBoolean(SIMCARD_2, false)
                tv2!!.text = "卡一可用"
            } else { // 两个卡都不可用(飞行模式会出现这种种情况)
                editor.putBoolean(SIMCARD_1, false)
                editor.putBoolean(SIMCARD_2, false)
                tv2!!.text = "飞行模式"
            }
        } else {
            // 保存为单卡手机
            editor.putString(SIMCARD, "0")
            editor.putBoolean(ISDOUBLE, false)
        }
        editor.commit()
    }
}