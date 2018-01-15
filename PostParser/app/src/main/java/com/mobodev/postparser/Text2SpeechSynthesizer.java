package com.mobodev.postparser;

import android.content.Context;
import android.media.AudioManager;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import io.github.shaobin0604.awesomelog.Log;

/**
 * Created by bshao on 1/13/18.
 */

public final class Text2SpeechSynthesizer implements SpeechSynthesizerListener {

    private static final String TAG = "Text2SpeechSynthesizer";

    private static final String LOCAL_DIR_NAME = "bd_tts";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";

    private static final String APP_ID = "9761633";
    private static final String API_KEY = "8yGZ7iGyjHKVSUeguF1M5fet";
    private static final String SECRET_KEY = "5de786d81857e64889e1c6736c332328";

    private static final int STATUS_OK = 0;

    private final WeakReference<Context> mContextRef;
    private final Object mInitialLock;
    private volatile boolean mInitialized;
    private boolean mInMaxVolume; // 暂时不考虑同时多个语音合成调用时，对系统声音大小的不同要求
    private int mLastSystemVolumeValue;


    public Text2SpeechSynthesizer(Context context) {
        mContextRef = new WeakReference<>(context);
        mInitialLock = new Object();
    }


    public void speakSync(String text, boolean inMaxVolume) {
        Log.d("[speakSync] text: " + text + ", inMaxVolume: " + inMaxVolume);
        initIfNeeded();
        mInMaxVolume = inMaxVolume;
        final int status = SpeechSynthesizer.getInstance().speak(text, text);
        if (status != STATUS_OK) {
            Log.e("[speakSync] error, status: " + status);
        }
    }

    private void initIfNeeded() {
        // 先判断 mInitialized 字段，否则只要调用该方法，就需要申请"锁"
        if (mInitialized) {
            return;
        }

        synchronized (mInitialLock) {
            if (mInitialized) {
                return;
            }
            Log.d("[initIfNeeded] init start");

            Context context = mContextRef.get();
            if (context == null) {
                Log.e("[initIfNeeded] context is null");
                return;
            }

            initLocalModelFile();

            SpeechSynthesizer speechSynthesizer = SpeechSynthesizer.getInstance();
            speechSynthesizer.setContext(context);
            speechSynthesizer.setAppId(APP_ID);
            speechSynthesizer.setApiKey(API_KEY, SECRET_KEY);

            speechSynthesizer.setSpeechSynthesizerListener(this);

            speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE,
                    getLocalModelFilePath(context, TEXT_MODEL_NAME));
            speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                    getLocalModelFilePath(context, SPEECH_FEMALE_MODEL_NAME));
            speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
            speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); // 音量, [0. 9]
            speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "7"); // 语速, [0. 9]
            speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5"); // 语调, [0, 9]
            speechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);

            speechSynthesizer.initTts(TtsMode.ONLINE);

            mInitialized = true;

            Log.d("[initIfNeeded] init end");
        }
    }

    private void initLocalModelFile() {
        Log.d("[initLocalModelFile] start");
        Context context = mContextRef.get();
        if (context == null) {
            Log.e("[initLocalModelFile] context is null");
            return;
        }
        File dir = new File(context.getFilesDir(), LOCAL_DIR_NAME);
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e("[initLocalModelFile] fail, fail to create dir");
            return;
        }
        boolean success = copyAssetsToFile(context, SPEECH_FEMALE_MODEL_NAME,
                getLocalModelFilePath(context, SPEECH_FEMALE_MODEL_NAME));
        success &= copyAssetsToFile(context, TEXT_MODEL_NAME, getLocalModelFilePath(context, TEXT_MODEL_NAME));
        Log.d("[initLocalModelFile] end, success: " + success);
    }

    private String getLocalModelFilePath(Context context, String name) {
        File dir = new File(context.getFilesDir(), LOCAL_DIR_NAME);
        return new File(dir, name).getAbsolutePath();
    }

    private boolean copyAssetsToFile(Context context, String assetsResourceName,
            String localFilePath) {
        File file = new File(localFilePath);
        final int bufferSize = 1024;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getResources().getAssets().open(assetsResourceName);
            if (file.exists() && file.length() == is.available()) {
                // 如果文件已经拷贝过，则不再拷贝
                return true;
            }
            fos = new FileOutputStream(localFilePath);
            byte[] buffer = new byte[bufferSize];
            int size = 0;
            while ((size = is.read(buffer, 0, bufferSize)) >= 0) {
                fos.write(buffer, 0, size);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fos);
        }
    }

    @Override
    public void onSynthesizeStart(String utteranceId) {
        Log.d("[onSynthesizeStart] utteranceId: " + utteranceId);
    }

    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] bytes, int progress) {
        Log.d("[onSynthesizeDataArrived] utteranceId: " + utteranceId);
    }

    @Override
    public void onSynthesizeFinish(String utteranceId) {
        Log.d("[onSynthesizeFinish] utteranceId: " + utteranceId);
    }

    @Override
    public void onSpeechStart(String utteranceId) {
        Log.d("[onSpeechStart] utteranceId: " + utteranceId);
        if (mInMaxVolume) {
            Context context = mContextRef.get();
            if (context == null) {
                Log.e("[onSpeechStart] context is null");
                return;
            }
            setMaxSystemVolume(context);
        }
    }

    private void setMaxSystemVolume(Context context) {
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mLastSystemVolumeValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
    }

    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        //do noting
    }

    @Override
    public void onSpeechFinish(String utteranceId) {
        if (mInMaxVolume) {
            Context context = mContextRef.get();
            if (context == null) {
                Log.e("[onSpeechFinish] context is null");
            } else {
                resetSystemVolume(context);
            }
            mInMaxVolume = false;
        }
        Log.d("[onSpeechFinish] utteranceId: " + utteranceId);
    }

    @Override
    public void onError(String utteranceId, SpeechError error) {
        if (mInMaxVolume) {
            Context context = mContextRef.get();
            if (context == null) {
                Log.e("[onError] context is null");
            } else {
                resetSystemVolume(context);
            }
            mInMaxVolume = false;
        }
        Log.e("[onError] utteranceId: " + utteranceId
                + "error: + (" + error.code + ")" + error.description);
    }

    private void resetSystemVolume(Context context) {
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mLastSystemVolumeValue, 0);
    }
}
