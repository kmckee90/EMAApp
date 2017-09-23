package kmckee90.emaapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import kmckee90.emaapp.db.dbManager;
import kmckee90.emaapp.notification.AlarmReceiver;




public class MainActivity extends AppCompatActivity {
    static String TAG = "Button outputs";
    AlarmReceiver alarm = new AlarmReceiver();

    public dbManager dB = new dbManager(this);
    String url = "file://kevin-pc/Users/Public/EMAapp%20Data/";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        Button toSurvey1 = (Button) (findViewById(R.id.toSurvey1));
        toSurvey1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InputScreen.class);
                startActivity(intent);
                finish();
            }
        });

        Button enableAlarm = (Button)(findViewById(R.id.enableAlarm));
        enableAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarm.setAlarm(view.getContext());
           }
        });

        Button disableAlarm = (Button)(findViewById(R.id.disableAlarm));
        disableAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarm.cancelAlarm(view.getContext());
            }
        });

        Button viewDB = (Button)(findViewById(R.id.viewDB));
        viewDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dB.outputDB();
            }
        });

        Button uploadDB = (Button)(findViewById(R.id.uploadDB));
        uploadDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });

        Button exportCSV = (Button)(findViewById(R.id.exportCSV));
        exportCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dB.exportCSV(view.getContext());
            }
        });

        final Button deleteDB = (Button)(findViewById(R.id.deleteDB));
        deleteDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dB.deleteDB();
            }
        });


    }

    public void uploadData(){
    }



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://kmckee90.emaapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://kmckee90.emaapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
