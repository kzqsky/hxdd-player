package com.edu.hxdd_player.utils;

import android.support.annotation.IntDef;
import android.support.annotation.RestrictTo;
import android.util.Log;

import com.edu.hxdd_player.BuildConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * 计时工具类，时间单位为秒
 */
public class TimeUtil {
    public static final int DEFAULT = 0;
    public static final int ONE_HALFE = 1;
    public static final int ONE_SEVEN_FIVE = 2;
    public static final int TWO = 3;

    private static final String tag = "TimeUtil";
    private volatile long accumulativeTime;//单位秒
    private Thread thread;
    private boolean start;
    private boolean ispause;
    private TimeUtilCallback mCallback;

    private int[] timeIntervalArray = {1000, 750, 600, 500};//1000为默认1秒，800为1.5倍速，600为1.75倍速，500为2倍速
    private int timeInterval;
    public interface TimeUtilCallback {
        void time(long time);//单位秒
    }

    public TimeUtil() {
        timeInterval = timeIntervalArray[DEFAULT];
    }

    public TimeUtil(@TimeType int type) {
        timeInterval = timeIntervalArray[type];
    }

    public void setCallback(TimeUtilCallback callback) {
        this.mCallback = callback;
    }

    public long getAccumulativeTime() {
        return accumulativeTime;
    }

    public void start() {
        try {
            stop();
            start = true;
            ispause = false;
            accumulativeTime = 0;
            thread = new Thread(() -> {
                try {
                    while (start || thread.interrupted()) {
                        Thread.sleep(timeInterval);
                        if (!ispause) {
                            accumulativeTime ++;
                            if (mCallback != null) {
                                mCallback.time(accumulativeTime);
                            }
                            if (BuildConfig.DEBUG) {
                                Log.i(tag, Thread.currentThread().getName() + "----" + accumulativeTime +"---当前时间间隔" + timeInterval);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(tag, Thread.currentThread().getName() + "已经是Exception状态了，我要退出了");
                }
                Log.i(tag, Thread.currentThread().getName() + "已经退出了");
            });
            thread.start();
        } catch (Exception e) {
            Log.i(tag, e.getMessage());
            stop();
        }
    }

    public void pause() {
        if (thread != null) {
            try {
                ispause = true;
                Log.i(tag, "已经是pause状态了");
            } catch (Exception e) {
                Log.i(tag, e.getMessage());
                stop();
            }
        }
    }

    public void resume() {
        if (thread != null) {
            try {
                ispause = false;
                Log.i(tag, "已经是resume状态了");
            } catch (Exception e) {
                Log.i(tag, e.getMessage());
                stop();
            }
        }
    }

    public void stop() {
        start = false;
        ispause = true;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    /**
     * public static final int DEFAULT = 0;
     * public static final int ONE_HALFE = 1;
     * public static final int ONE_SEVEN_FIVE = 2;
     * public static final int TWO = 3;
     * 只能传这4个值
     * @param type
     */
    public void setTimeInterval(@TimeType int type) {
        this.timeInterval = timeIntervalArray[type];
    }


    @RestrictTo(LIBRARY_GROUP)
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {DEFAULT, ONE_HALFE, ONE_SEVEN_FIVE,TWO})
    public @interface TimeType {}
}
