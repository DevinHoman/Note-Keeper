package com.example.notekeeper;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;

public class NoteUploaderJobService extends JobService {
    public static final String EXTRA_DATA_URI = "com.example.notekeeper.DATA_URI";
    private NoteUploader noteUploader;

    public NoteUploaderJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        AsyncTask<JobParameters ,Void,Void> task = new AsyncTask<JobParameters, Void, Void>() {
            @Override
            protected Void doInBackground(JobParameters... backgroundParameters) {
                JobParameters jobParameters1 = backgroundParameters[0];

                String stringDataUri = jobParameters1.getExtras().getString(EXTRA_DATA_URI);
                Uri dataUir = Uri.parse(stringDataUri);
                noteUploader.doUpload(dataUir);

                if(!noteUploader.isCancelled())
                    jobFinished(jobParameters1,false);
                return null;
            }
        };

        noteUploader = new NoteUploader(this);
        task.execute(jobParameters);



        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        noteUploader.cancel();
        //Rescheduling the work
        return true;
    }


}
