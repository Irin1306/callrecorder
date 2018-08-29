package com.example.cr.data.sourse;

import com.example.cr.data.entity.Recording;

import java.util.List;

public class DataRepository implements DataSource {

    private static DataRepository INSTANCE = null;

    private final DataSource mLocal;

    // prevent direct initialisation
    private DataRepository(DataSource appLocalDataSource) {
        mLocal = appLocalDataSource;
    }

    // light singleton pattern
    public static DataRepository getInstance
    (DataSource appLocalDataSource) {
        if (INSTANCE == null) {

            INSTANCE = new DataRepository(appLocalDataSource);

        }

        return INSTANCE;
    }


    @Override
    public void getAllRecordings(final GetRecordingsCallback callback) {
        mLocal.getAllRecordings(new GetRecordingsCallback() {
            @Override
            public void onRecordingsLoaded(List<Recording> recordings) {
                callback.onRecordingsLoaded(recordings);
            }
        });
    }

    @Override
    public void getRecordingById(int id, RecordingLoadedCallback callback) {
        mLocal.getRecordingById(id, new RecordingLoadedCallback() {
            @Override
            public void onRecordingLoaded(Recording recording) {
                callback.onRecordingLoaded(recording);
            }
        });
    }

    @Override
    public void getRecordingByName(String name, RecordingLoadedCallback callback) {
        mLocal.getRecordingByName(name, new RecordingLoadedCallback() {
            @Override
            public void onRecordingLoaded(Recording recording) {
                callback.onRecordingLoaded(recording);
            }
        });
    }

    @Override
    public void saveRecording(Recording recording, SaveCallback callback) {
        mLocal.saveRecording(recording, new SaveCallback() {
            @Override
            public void onSaved() {
                callback.onSaved();
            }
        });
    }

    @Override
    public void deleteRecording(Recording recording, DeleteCallback callback) {
        mLocal.deleteRecording(recording, new DeleteCallback() {
            @Override
            public void onDeleted() {
                callback.onDeleted();
            }
        });
    }

    @Override
    public void getSearchResults(String string, GetRecordingsCallback callback) {
        mLocal.getSearchResults(string, new GetRecordingsCallback() {
            @Override
            public void onRecordingsLoaded(List<Recording> recordings) {
                callback.onRecordingsLoaded(recordings);
            }
        });
    }

    @Override
    public void deleteAllRecordings(DeleteCallback callback) {
        mLocal.deleteAllRecordings(new DeleteCallback() {
            @Override
            public void onDeleted() {
                callback.onDeleted();
            }
        });
    }

}
