package com.falconssoft.woodysystem.email;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;


import com.falconssoft.woodysystem.SettingsFile;
import com.falconssoft.woodysystem.stage_one.AddNewRaw;

import java.util.Collections;
import java.util.List;

public class SendMailTask extends AsyncTask {

      private ProgressDialog statusDialog;
    private AddNewRaw sendMailActivity;
    String path;

    public SendMailTask(AddNewRaw activity,String path) {
        sendMailActivity = activity;
        this.path=path;

    }

    protected void onPreExecute() {
        statusDialog = new ProgressDialog(sendMailActivity);
        statusDialog.setMessage("Getting ready...");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("SendMailTask", "About to instantiate GMail...");
            publishProgress("Processing input....");
            GMail androidEmail = new GMail(args[0].toString()
                    , args[1].toString()
                    , Collections.singletonList(args[2].toString())
                    , args[3].toString()
                    ,args[4].toString()
                 , (List<String>) args[5]);
            publishProgress("Preparing mail message....");
            androidEmail.createEmailMessage();
            publishProgress("Sending email....");
            androidEmail.sendEmail();
            publishProgress("Email Sent.");
            Log.i("SendMailTask", "Mail Sent.");
        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(Object result) {
        sendMailActivity.clear();
        sendMailActivity.deleteTempFolder(path);
       statusDialog.dismiss();
    }

}
