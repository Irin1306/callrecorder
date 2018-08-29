package com.example.cr.data;

import android.content.Context;

import com.example.cr.data.sourse.DataRepository;
import com.example.cr.data.sourse.local.AppLocalDataSource;
import com.example.cr.data.sourse.local.LocalDatabase;
import com.example.cr.util.AppExecutors;

public class AppDataInjector {
    public static DataRepository provideDataRepository(Context context) {
        LocalDatabase database = LocalDatabase.getInstance(context);
        return DataRepository.getInstance(
                AppLocalDataSource.getInstance(
                        new AppExecutors(),
                        database.recordingDao()

                )
        );
    }

}
