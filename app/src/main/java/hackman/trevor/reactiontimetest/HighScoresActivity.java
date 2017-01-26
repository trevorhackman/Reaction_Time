package hackman.trevor.reactiontimetest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

import static hackman.trevor.library.MyDialog.createAlertDialog;

public class HighScoresActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    TextView a,hs1,hs2,hs3,hs4,hs5,hs6,hs7,hs8,hs9,hs10, clear;
    CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, cb9, cb10;
    boolean[] checkBoxes; // Keeps track of which checkBoxes are checked and which aren't
    SharedPreferences sharedPreferences;
    String spacing = " ";
    int dialogTitle = R.string.dialogue_title_delete_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        // Get all textViews and checkboxes needed
        // a = (TextView) findViewById(R.id.textViewA);
        hs1 = (TextView) findViewById(R.id.textViewHS1);
        hs2 = (TextView) findViewById(R.id.textViewHS2);
        hs3 = (TextView) findViewById(R.id.textViewHS3);
        hs4 = (TextView) findViewById(R.id.textViewHS4);
        hs5 = (TextView) findViewById(R.id.textViewHS5);
        hs6 = (TextView) findViewById(R.id.textViewHS6);
        hs7 = (TextView) findViewById(R.id.textViewHS7);
        hs8 = (TextView) findViewById(R.id.textViewHS8);
        hs9 = (TextView) findViewById(R.id.textViewHS9);
        hs10 = (TextView) findViewById(R.id.textViewHS10);
        clear = (TextView) findViewById(R.id.textView9);
        cb1 = (CheckBox) findViewById(R.id.checkBox);
        cb2 = (CheckBox) findViewById(R.id.checkBox2);
        cb3 = (CheckBox) findViewById(R.id.checkBox3);
        cb4 = (CheckBox) findViewById(R.id.checkBox4);
        cb5 = (CheckBox) findViewById(R.id.checkBox5);
        cb6 = (CheckBox) findViewById(R.id.checkBox6);
        cb7 = (CheckBox) findViewById(R.id.checkBox7);
        cb8 = (CheckBox) findViewById(R.id.checkBox8);
        cb9 = (CheckBox) findViewById(R.id.checkBox9);
        cb10 = (CheckBox) findViewById(R.id.checkBox10);

        // Adds shared listener to every checkbox
        cb1.setOnCheckedChangeListener(this);
        cb2.setOnCheckedChangeListener(this);
        cb3.setOnCheckedChangeListener(this);
        cb4.setOnCheckedChangeListener(this);
        cb5.setOnCheckedChangeListener(this);
        cb6.setOnCheckedChangeListener(this);
        cb7.setOnCheckedChangeListener(this);
        cb8.setOnCheckedChangeListener(this);
        cb9.setOnCheckedChangeListener(this);
        cb10.setOnCheckedChangeListener(this);

        checkBoxes = new boolean[10];
        sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        updateText();

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

    // Goes to menu when menu is clicked
    public void onMenuClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    // Method called when clear TextView is clicked during any state
    public void onClearDataClick(View view) {
        createAlertDialog(this, dialogTitle, R.string.dialogue_message_delete, R.string.dialogue_delete, R.string.dialogue_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    // Positive response
                    public void onClick(DialogInterface dialogInterface, int which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (anyChecked()) {
                            for (int i = 9; i > -1; i--) // Top down to stop the cascading of scores after one deletion to change which score gets deleted in following deletion
                                if (checkBoxes[i]) deleteHighScore(i);
                            clear.setText(R.string.clear_all_data);
                            cb1.setChecked(false);
                            cb2.setChecked(false);
                            cb3.setChecked(false);
                            cb4.setChecked(false);
                            cb5.setChecked(false);
                            cb6.setChecked(false);
                            cb7.setChecked(false);
                            cb8.setChecked(false);
                            cb9.setChecked(false);
                            cb10.setChecked(false);
                            for (int i = 0; i < 10; i++) checkBoxes[i] = false;
                            updateText();
                        }
                        else {
                            editor.clear();
                            editor.apply();
                        }
                        updateText();

                    }
                });
    }

    @Override // This method is called anytime a checkbox is checked or unchecked. It updates checkBoxes and updates clear textView
    public void onCheckedChanged(CompoundButton box, boolean isChecked) {
        int id;
        if (box.getId() == R.id.checkBox) id = 0;
        else if (box.getId() == R.id.checkBox2) id = 1;
        else if (box.getId() == R.id.checkBox3) id = 2;
        else if (box.getId() == R.id.checkBox4) id = 3;
        else if (box.getId() == R.id.checkBox5) id = 4;
        else if (box.getId() == R.id.checkBox6) id = 5;
        else if (box.getId() == R.id.checkBox7) id = 6;
        else if (box.getId() == R.id.checkBox8) id = 7;
        else if (box.getId() == R.id.checkBox9) id = 8;
        else id = 9;
        checkBoxes[id] = isChecked;


        if (anyChecked()) {
            clear.setText(R.string.clear_selected);
            dialogTitle = R.string.dialogue_title_delete_selected;
        }
        else {
            clear.setText(R.string.clear_all_data);
            dialogTitle = R.string.dialogue_title_delete_all;
        }
    }

    // Updates text of average and high scores textViews to reflect data changes
    private void updateText() {
        // if (getDouble("AVERAGE") != -1) a.setText(String.format(Locale.getDefault(), "%.3f", getDouble("AVERAGE"))); else a.setText("---");
        if (getDouble("HIGH_SCORE0") != -1) hs1.setText(String.format(Locale.getDefault(), "%.3f", getDouble("HIGH_SCORE0"))); else hs1.setText(" --- ");
        if (getDouble("HIGH_SCORE1") != -1) hs2.setText(String.format(Locale.getDefault(), "%.3f", getDouble("HIGH_SCORE1"))); else hs2.setText(" --- ");
        if (getDouble("HIGH_SCORE2") != -1) hs3.setText(String.format(Locale.getDefault(), "%.3f", getDouble("HIGH_SCORE2"))); else hs3.setText(" --- ");
        if (getDouble("HIGH_SCORE3") != -1) hs4.setText(String.format(Locale.getDefault(), "%.3f", getDouble("HIGH_SCORE3"))); else hs4.setText(" --- ");
        if (getDouble("HIGH_SCORE4") != -1) hs5.setText(String.format(Locale.getDefault(), "%.3f", getDouble("HIGH_SCORE4"))); else hs5.setText(" --- ");
        if (getDouble("HIGH_SCORE5") != -1) hs6.setText(String.format(Locale.getDefault(), "%.3f", getDouble("HIGH_SCORE5"))); else hs6.setText(" --- ");
        if (getDouble("HIGH_SCORE6") != -1) hs7.setText(String.format(Locale.getDefault(), "%.3f", getDouble("HIGH_SCORE6"))); else hs7.setText(" --- ");
        if (getDouble("HIGH_SCORE7") != -1) hs8.setText(String.format(Locale.getDefault(), "%.3f", getDouble("HIGH_SCORE7"))); else hs8.setText(" --- ");
        if (getDouble("HIGH_SCORE8") != -1) hs9.setText(String.format(Locale.getDefault(), "%.3f", getDouble("HIGH_SCORE8"))); else hs9.setText(" --- ");
        if (getDouble("HIGH_SCORE9") != -1) hs10.setText(String.format(Locale.getDefault(), "%.3f", getDouble("HIGH_SCORE9"))); else hs10.setText(" --- ");
    }

    private void deleteHighScore(int which) {
        for (int i = which; i < 19; i++) { // Move each score down one position to replace and fill deleted score
            storeDouble("HIGH_SCORE" + i, getDouble("HIGH_SCORE" + (i + 1)));
        }
        // Delete duplicate last value
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("HIGH_SCORE19");
        editor.apply();
    }

    // Checks to see if any checkboxes are checked. Returns true if any are, false otherwise
    private boolean anyChecked() {
        boolean anyChecked = false;
        for (int i = 0; i < 10; i++)
            if (checkBoxes[i]) { anyChecked = true; break; }
        return anyChecked;
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
