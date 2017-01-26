package hackman.trevor.reactiontimetest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class BROGActivity extends AppCompatActivity implements View.OnTouchListener {
    TextView head, body, menu, averageText, average, highScoreText, highScore;
    RelativeLayout relativeLayout;
    double time;
    Timer timer;
    Context context = this;
    long greenTime, liftTime;
    SharedPreferences sharedPreferences;

    static int currentColor;
    final static int SET_RED = 0, RED = 0;
    final static int SET_ORANGE = 1, ORANGE = 1;
    final static int SET_GREEN = 2, GREEN = 2;
    final static int SET_BLUE2 = 3, BLUE2 = 3;
    final static MyHandler handler = new MyHandler();
    private static class MyHandler extends Handler {
        public void handleMessage(Message message) {
            final int what = message.what;
            switch (what) {
                case SET_RED: runner.setRed(); break;
                case SET_ORANGE: runner.setOrange(); break;
                case SET_GREEN: runner.setGreen(); break;
                case SET_BLUE2: runner.setBlue2(); break;
            }
        }
    }
    static Runner runner;
    private class Runner {
        private void setRed() { BROGActivity.this.setRed(); }
        private void setOrange() { BROGActivity.this.setOrange(); }
        private void setGreen() { BROGActivity.this.setGreen(); }
        private void setBlue2() { BROGActivity.this.setBlue2(); }
    }

    /*
    final Handler handler = new Handler() {
        public void handleMessage(Message message) {
            final int what = message.what;
            switch (what) {
                case SET_RED: setRed(); break;
                case SET_ORANGE: setOrange(); break;
                case SET_GREEN: setGreen(); break;
                case SET_BLUE2: setBlue2(); break;
            }
        }
    };
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brog);
        runner = new Runner();

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        relativeLayout.setOnTouchListener(this);
        head = (TextView) findViewById(R.id.textView);
        body = (TextView) findViewById(R.id.textView2);
        menu = (TextView) findViewById(R.id.textView3);
        averageText = (TextView) findViewById(R.id.textView4);
        average = (TextView) findViewById(R.id.textView5);
        highScoreText = (TextView) findViewById(R.id.textView6);
        highScore = (TextView) findViewById(R.id.textView7);

        time = Math.random() * 4 + 1;
        currentColor = -1;
        sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        if (getDouble("AVERAGE") != -1) average.setText(String.format(Locale.getDefault(), "%.3f", getDouble("AVERAGE"))); else average.setText("---");
        if (getDouble("HIGH_SCORE0") != -1) highScore.setText(String.format(Locale.getDefault(),"%.3f",getDouble("HIGH_SCORE0"))); else highScore.setText("---");

        final AdView adview = (AdView) findViewById(R.id.adView);
        adview.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adview.setBackgroundColor(0xff000000);
            }
        });
        // For testing ads
        //AdRequest adRequest = new AdRequest.Builder()
        //        .addTestDevice("DF2263A18BA1357BBF95A6821C4C8DE8")
        //        .build();
        // For real ads
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && (currentColor == -1 || currentColor == ORANGE || currentColor == BLUE2)) {
            handler.sendEmptyMessage(SET_RED);
            time = Math.random() * 4 + 1;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    if (currentColor == RED) handler.sendEmptyMessage(SET_GREEN);
                    timer.cancel();
                    this.cancel();
                }
            }, (long) (time * 1000));
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) { // ACTION_CANCEL only occurs when another view takes over, I have no other view so shouldn't need to check for it, but just in case I will in case a 3rd party app or interface somehow causes it
            liftTime = System.nanoTime(); // To get more accurate time, acquire it immediately before even the conditional checks
            if (currentColor == RED) handler.sendEmptyMessage(SET_ORANGE);
            // Checking screen color directly instead of currentColor variable to try to remove extremely rare situations where player lifts finger so fast that currentColor hasn't been set to green yet
            else if (((ColorDrawable)view.getBackground()).getColor() == ContextCompat.getColor(this, R.color.colorGreen)) {
                    handler.sendEmptyMessage(SET_BLUE2);
            }
        }
        return true;
    }

    private void setRed() {
        setBackgroundColor(R.color.colorRed);
        head.setText(R.string.pressed);
        body.setText(R.string.wait_until_the_screen_turns_green_to_release);
        menu.setVisibility(View.INVISIBLE);
        currentColor = RED;
    }

    private void setOrange() {
        setBackgroundColor(R.color.colorOrange);
        head.setText(R.string.released_early);
        body.setText(R.string.you_released_too_early_press_and_hold_your_finger_to_the_screen_to_try_again);
        menu.setVisibility(View.VISIBLE);
        currentColor = ORANGE;
        timer.cancel();
    }

    private void setGreen() {
        setBackgroundColor(R.color.colorGreen);
        greenTime = System.nanoTime();
        currentColor = GREEN;
        head.setText(R.string.release_now);
        body.setText("");
        menu.setVisibility(View.INVISIBLE);
    }

    private void setBlue2() {
        setBackgroundColor(R.color.colorBlue);
        double doubleTime = ((int)((liftTime - greenTime)/1000000.0 + 0.5))/1000.0;
        saveTime(doubleTime);
        String time = "" + String.format(Locale.getDefault(), "%.3f", doubleTime);
        head.setText(time);
        String you_took = getString(R.string.you_took);
        String seconds_to_react_press_and_hold_to_try_again = getString(R.string.seconds_to_react_press_and_hold_to_try_again);
        body.setText(you_took + String.format(Locale.getDefault(), "%.3f", doubleTime) + seconds_to_react_press_and_hold_to_try_again);
        menu.setVisibility(View.VISIBLE);
        average.setText(String.format(Locale.getDefault(), "%.3f", getDouble("AVERAGE")));
        highScore.setText(String.format(Locale.getDefault(), "%.3f",getDouble("HIGH_SCORE0")));
        currentColor = BLUE2;

    }

    private void setBackgroundColor(final int color) {
        relativeLayout.setBackgroundColor(ContextCompat.getColor(this, color));
    }

    public void onMenuButtonClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void saveTime(double time) {
        // Retrieve last 10 times
        double[] last10 = new double[10];
        for (int i = 0; i < 10; i++) {
            last10[i] = getDouble("LAST" + i);
        }

        // Retrieve top 10 high scores
        double[] highScores20 = new double[20];
        for (int i = 0; i < 20; i++) {
            highScores20[i] = getDouble("HIGH_SCORE" + i);
        }

        // Shift all last times up one position
        for (int i = 8; i > -1; i--) {
            last10[i + 1] = last10[i];
        }

        // Add new time as the most recent
        last10[0] = time;

        // Check to see if new time is a new high score and if so shift and insert
        for (int i = 0; i < 20; i++) {
            if (time < highScores20[i] || highScores20[i] == -1) {
                //Shift all old high scores up one position
                for (int j = 18; j > i - 1; j--) {
                    highScores20[j + 1] = highScores20[j];
                }
                //Add new high score
                highScores20[i] = time;
                break;
            }
        }

        // Calculate new average for as many last as there are
        double average = 0;
        int counter = 0;
        for (int i = 0; i < 10; i++) {
            if (last10[i] != -1) {
                average += last10[i];
                counter++;
            }
        }
        if (counter != 0) average /= counter;

        // Store new average
        storeDouble("AVERAGE", average);

        // Store new last 10
        for (int i = 0; i < 10; i ++) {
            storeDouble("LAST" + i, last10[i]);
        }

        // Store new high scores if there is one
        for (int i = 0; i < 20; i ++) {
            storeDouble("HIGH_SCORE" + i, highScores20[i]);
        }
    }

    // Gets double from sharedPreferences (Which can't store doubles so efficient lossless conversion to and from long is done)
    private double getDouble(String key) {
        return Double.longBitsToDouble(sharedPreferences.getLong(key, Double.doubleToRawLongBits(-1)));
    }

    // Stores double in sharedPreferences
    private void storeDouble(String key, double d) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, Double.doubleToRawLongBits(d));
        editor.apply();
    }
}
