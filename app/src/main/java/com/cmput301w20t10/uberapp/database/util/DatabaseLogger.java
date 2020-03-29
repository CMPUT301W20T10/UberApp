package com.cmput301w20t10.uberapp.database.util;

import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Class that helps print logs with line numbers and other details
 * Reference:
 *  Author: Juan
 *  Link: stackoverflow.com/a/5916374
 *
 * @author Allan Manuba
 * @version 1.0.1
 */
public class DatabaseLogger {

    /**
     * Prints a log message in the debug console
     *
     * @param exception should always be new Exception()
     * @param message   additional message
     *
     * @version 1.0.1
     */
    public static void error(Exception exception, String message, @Nullable Exception error) {
        StackTraceElement element = exception.getStackTrace()[0];
        Log.e("Tomate", element.getClassName() + "/" +
                element.getMethodName() + ":" + element.getLineNumber() + ": " + message,
                error);
    }

    public static void debug(Exception exception, String message) {
        StackTraceElement element = exception.getStackTrace()[0];
        Log.d("Tomate", element.getClassName() + "/" +
                element.getMethodName() + ":" + element.getLineNumber() + ": " + message);
    }
}
