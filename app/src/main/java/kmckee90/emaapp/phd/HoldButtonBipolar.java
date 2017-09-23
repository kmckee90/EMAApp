package kmckee90.emaapp.phd;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import kmckee90.emaapp.Indicators;
import kmckee90.emaapp.R;

/**
 * Created by Work on 9/2/2016.
 */
public class HoldButtonBipolar extends RelativeLayout implements HoldButtonInterface {

    private static final String TAG = "HoldButton";

    //Components
    public Button itemButtonNeg;
    public Button itemButtonPos;
    public Chronometer itemTimer;
    public ProgressBar animCircle;

    //Vibration feedback
    public Vibrator vib;
    private long vibTime = 20;

    //Skip button
    public TextView skipX;
    private boolean skip = false;

    //Properties
    private String itemName;
    private boolean retries = false;

    //Data output
    private double pressDuration;
    private long timestamp;

    //Indicator style
    private Indicators indic = Indicators.TIMER;

    public void setIndicatorType(Indicators type){
        indic = type;
        if(type!= Indicators.TIMER){
            removeView(itemTimer);
        }
    }

    //CONSTRUCTORS
    public HoldButtonBipolar(Context context) {
        super(context);
        init(super.getContext());
    }
    public HoldButtonBipolar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(super.getContext());
    }
    public HoldButtonBipolar(Context context, AttributeSet attrs, int defStyle) {
        super(context);
        init(super.getContext());
    }

    //Initialization method
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_holdbutton_bipolar, this);
        vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        animCircle = new ProgressBar(context);
        animCircle.setIndeterminate(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        animCircle.setLayoutParams(params);
        itemTimer = (Chronometer) findViewById(R.id.itemTimer);
    }

    //NEW METHOD FOR BIPOLAR
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initHoldButtonNeg();
        initHoldButtonPos();
        initSkipButton();

        if(indic != Indicators.TIMER){
            removeView(itemTimer);
        }


    }


    //NEW METHOD FOR BIPOLAR
    protected void initHoldButtonNeg(){
        itemButtonNeg = (Button)this.findViewById(R.id.itemButtonNeg);
        itemButtonNeg.setOnTouchListener(new OnTouchListener() {
            private long startTime;
            private long endTime;
            private double duration;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    startTime = System.nanoTime();
                    Log.i(TAG, "****Button down****");
                    Indicate(true);
                    timestamp = System.currentTimeMillis();
                    if(!retries) {
                        itemButtonPos.setEnabled(false);
                    }
                }

                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    endTime = System.nanoTime();
                    duration = (double)TimeUnit.MILLISECONDS.convert(endTime-startTime, TimeUnit.NANOSECONDS)/1000 * -1;
                    Log.i(TAG, Double.toString(duration));
                    Log.i(TAG, "****Button up****");
                    pressDuration = duration;
                    Indicate(false);
                    if(!retries) {
                        itemButtonNeg.setEnabled(false);
                    }
                }
                return false;
            }
        });

    }
    //NEW METHOD FOR BIPOLAR
    protected void initHoldButtonPos(){
        itemButtonPos = (Button)this.findViewById(R.id.itemButtonPos);
        itemButtonPos.setOnTouchListener(new OnTouchListener() {
            private long startTime;
            private long endTime;
            private double duration;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    startTime = System.nanoTime();
                    Log.i(TAG, "****Button down****");
                    Indicate(true);
                    timestamp = System.currentTimeMillis();
                    if(!retries) {
                        itemButtonNeg.setEnabled(false);
                    }
                }

                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    endTime = System.nanoTime();
                    duration = (double)TimeUnit.MILLISECONDS.convert(endTime-startTime, TimeUnit.NANOSECONDS)/1000;
                    Log.i(TAG, Double.toString(duration));
                    Log.i(TAG, "****Button up****");
                    pressDuration = duration;
                    Indicate(false);
                    if(!retries) {
                        itemButtonPos.setEnabled(false);
                    }
                }
                return false;
            }
        });

    }

    protected void initSkipButton(){
        skipX = (TextView)(findViewById(R.id.skipX));
        skipX.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!skip) {
                    skipX.setTextColor(Color.RED);
                    skip = true;
                } else {
                    skipX.setTextColor(Color.LTGRAY);
                    skip = false;
                }
            }
        });
    }


    protected void Indicate(boolean enabled){
        if(enabled) {
            vib.vibrate(vibTime);
            switch (indic) {
                case NONE:
                    break;
                case TIMER:
                    itemTimer.setBase(SystemClock.elapsedRealtime());
                    itemTimer.start();
                    break;
                case GRAPHIC:
                    addView(animCircle);
                    break;
            }
        }else{
            vib.cancel();
            vib.vibrate(20);
            switch (indic) {
                case NONE:
                    break;
                case TIMER:
                    itemTimer.stop();;
                    break;
                case GRAPHIC:
                    removeView(animCircle);
                    break;
            }
        }
    }



    public double getDuration(){
        return(pressDuration);
    }

    public void setText(String neg, String pos){
        itemButtonNeg.setText(neg);
        itemButtonPos.setText(pos);
    }

    public void setVibrateTime(int time){
        vibTime = time;
    }

    public void setRetry(boolean enable){
        retries = enable;
    }

    public String getName(){
        return(itemName);
    }

    public void setName(String name){
        itemName = name;
    }

    public boolean isFlagged(){
        return(skip);
    }

    //NOTE: RETURNS TIMESTAMP OF MOST RECENT TOUCH IF RETRIES ARE ENABLED
    public long getTimestamp(){
        return(timestamp);
    }

}