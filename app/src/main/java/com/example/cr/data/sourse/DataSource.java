package com.example.cr.data.sourse;

import com.example.cr.data.entity.Recording;

import java.util.List;

public interface DataSource {

    void getAllRecordings(GetRecordingsCallback callback);

    void getRecordingById(int id, RecordingLoadedCallback callback);

    void getRecordingByName(String name, RecordingLoadedCallback callback);

    void saveRecording(Recording recording, SaveCallback callback);

    void deleteRecording(Recording recording, DeleteCallback callback);

    void getSearchResults(String string, GetRecordingsCallback callback);

    void deleteAllRecordings(DeleteCallback callback);

    interface SaveCallback {
        void onSaved();
    }

    interface DeleteCallback {
        void onDeleted();
    }

    interface GetRecordingsCallback {
        void onRecordingsLoaded(List<Recording> recordings);
    }

    interface RecordingLoadedCallback {
        void onRecordingLoaded(Recording recording);
    }


}
