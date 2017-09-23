package kmckee90.emaapp;


import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.view.View;

import kmckee90.emaapp.db.dbManager;
import kmckee90.emaapp.phd.HoldButton;
import kmckee90.emaapp.phd.HoldButtonBipolar;
import kmckee90.emaapp.phd.HoldButtonInterface;


public class InputScreen extends AppCompatActivity {

    static String TAG = "Button Outputs";
    public dbManager dB = new dbManager(this);
    private long timestamp_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_screen);

        final HoldButton item1, item2, item3, item4;
        final HoldButtonBipolar item5, item6;

        timestamp_open = System.currentTimeMillis();

        item1 = (HoldButton) findViewById(R.id.holdButton1);
        item1.setIndicatorType(Indicators.NONE);
        item1.setRetry(false);
        item1.setName("Anxiety");


        item2 = (HoldButton) findViewById(R.id.holdButton2);
        item2.setIndicatorType(Indicators.GRAPHIC);
        item2.setName("Anger");


        item3 = (HoldButton) findViewById(R.id.holdButton3);
        item3.setIndicatorType(Indicators.TIMER);
        item3.setName("Safety");
        item3.setRetry(true);


        item4 = (HoldButton) findViewById(R.id.holdButton4);
        item4.setIndicatorType(Indicators.NONE);
        item4.setName("Joy");
        item4.setVibrateTime(20000);


        item5 = (HoldButtonBipolar) findViewById(R.id.holdButtonBipolar1);
        item5.setIndicatorType(Indicators.TIMER);
        item5.setText("Sad", "Happy");
        item5.setName("Mood");


        item6 = (HoldButtonBipolar) findViewById(R.id.holdButtonBipolar2);
        item6.setIndicatorType(Indicators.GRAPHIC);
        item6.setText("Disagree", "Agree");
        item6.setName("Agreement");




        final Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager clearNoti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                clearNoti.cancel(1);

                HoldButtonInterface[] allItems = {item1, item2, item3, item4, item5, item6};
                dB.open();
                Log.i("DB", "Successfully opened db");
                dB.enterData(allItems, timestamp_open);
                Log.i("DB", "Successfully wrote data");
                dB.close();
                Log.i("DB", "Successfully closed db");


                finish();
            }
        });



    }


    //Prevent back button from finishing the activity:
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // Before 2.0
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}