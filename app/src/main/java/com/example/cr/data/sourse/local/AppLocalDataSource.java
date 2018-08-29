package com.example.cr.data.sourse.local;

import com.example.cr.data.entity.Recording;
import com.example.cr.data.sourse.DataSource;
import com.example.cr.data.sourse.local.dao.RecordingDao;
import com.example.cr.util.AppExecutors;

import java.util.List;

public class AppLocalDataSource implements DataSource {


    private static final String TAG = AppLocalDataSource.class.getName();

    // TODO: доклад по оператору volatile

    private static volatile AppLocalDataSource INSTANCE;

    private AppExecutors mExecutors;

    private RecordingDao mRecordingDao;


    private AppLocalDataSource(AppExecutors appExecutors,
                               RecordingDao recordingDao) {
        mExecutors = appExecutors;
        mRecordingDao = recordingDao;

    }

    /**
     * Singleton pattern
     */
    public static AppLocalDataSource
    getInstance(AppExecutors appExecutors, RecordingDao recordingDao) {
        if (INSTANCE == null) {
            synchronized (AppLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppLocalDataSource
                            (appExecutors, recordingDao);
                }
            }
        }

        return INSTANCE;
    }

    @Override
    public void getAllRecordings(GetRecordingsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Recording> recordings =
                        mRecordingDao.getAllRecordings();

                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onRecordingsLoaded(recordings);
                    }
                });
            }
        };

        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getRecordingById(int id, RecordingLoadedCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Recording recording = mRecordingDao.getRecordingById(id);
                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onRecordingLoaded(recording);
                    }
                });
            }
        };

        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getRecordingByName(String name, RecordingLoadedCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Recording recording = mRecordingDao.getRecordingByName(name);
                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onRecordingLoaded(recording);
                    }
                });
            }
        };

        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRecording(Recording recording, SaveCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mRecordingDao.saveRecording(recording);
                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSaved();
                    }
                });
            }
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteRecording(Recording recording, DeleteCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mRecordingDao.deleteRecording(recording);
                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDeleted();
                    }
                });
            }
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getSearchResults(String string, GetRecordingsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Recording> recordings = mRecordingDao.getSearchResults(string);
                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onRecordingsLoaded(recordings);
                    }
                });
            }
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllRecordings(DeleteCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mRecordingDao.deleteAll();
                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDeleted();
                    }
                });
            }
        };
        mExecutors.diskIO().execute(runnable);
    }
}
