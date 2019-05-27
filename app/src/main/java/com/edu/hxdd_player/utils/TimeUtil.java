package com.edu.hxdd_player.utils;

import android.util.Log;

import com.edu.hxdd_player.BuildConfig;

/**
 * 计时工具类，时间单位为秒
 */
public class TimeUtil {
    private static final String tag = "TimeUtil";
    private volatile long accumulativeTime;//单位秒
    private Thread thread;
    private boolean start;
    private boolean ispause;
    private TimeUtilCallback mCallback;

    public interface TimeUtilCallback {
        void time(long time);//单位秒
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
                        Thread.sleep(1000);
                        if (!ispause) {
                            accumulativeTime ++;
                            if (mCallback != null) {
                                mCallback.time(accumulativeTime);
                            }
                            if (BuildConfig.DEBUG) {
                                Log.i(tag, Thread.currentThread().getName() + "----" + accumulativeTime);
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
}
