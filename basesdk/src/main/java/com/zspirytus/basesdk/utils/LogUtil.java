package com.zspirytus.basesdk.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * 日志工具类
 * Created by ZSpirytus on 2018/8/11.
 */

public class LogUtil {

    private LogUtil() {
        throw new AssertionError("must not get class: " + this.getClass().getSimpleName() + " Instance!");
    }

    private static final int DEBUG = 1;
    private static final int RELEASE = 2;
    private static final int LEVEL = DEBUG;

    public static void e(String TAG, String message) {
        if (LEVEL == DEBUG) {
            Log.e(TAG, message);
        }
    }

    public static void out(Object message) {
        if (LEVEL == DEBUG) {
            System.out.println(message);
        }
    }

    public static void err(Object message) {
        if (LEVEL == DEBUG) {
            System.err.println(message);
        }
    }

    public static void log(Context context, String fileName, String msg) {
        PrintStream stream;
        try {
            File file = createLogFile(context, fileName);
            stream = new PrintStream(file);
            String nowDate = TimeUtil.getNowDateTime();
            stream.append("-------------------------------------").append(nowDate).append("-------------------------------------\n");
            stream.append(msg);
            stream.append("-------------------------------------").append(nowDate).append("-------------------------------------\n");
            stream.flush();
            stream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void log(Context context, String fileName, Throwable e) {
        PrintStream stream;
        try {
            File file = createLogFile(context, fileName);
            stream = new PrintStream(file);
            String nowDate = TimeUtil.getNowDateTime();
            stream.append("-------------------------------------").append(nowDate).append("-------------------------------------\n");
            e.printStackTrace(stream);
            stream.append("-------------------------------------").append(nowDate).append("-------------------------------------\n\n\n");
            stream.flush();
            stream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File createLogFile(Context context, String fileName) throws IOException {
        File logFileDir = new File(context.getFilesDir(), "log");
        logFileDir.mkdir();
        File logFile = new File(logFileDir, fileName);
        logFile.createNewFile();
        return logFile;
    }

}
