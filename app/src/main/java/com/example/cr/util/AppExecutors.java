package com.example.cr.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class AppExecutors {

    private static final int THREAD_COUNT = 2;

    private final Executor diskIO;

    private final Executor mainThread;

    AppExecutors(Executor diskIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
    }

    public AppExecutors() {
        this(
                new DiskIOThreadExecutor(),
                new MainThreadExecutor()
        );
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainTheadHandler =
                new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainTheadHandler.post(command);
        }
    }
}
