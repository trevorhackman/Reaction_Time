package hackman.trevor.reactiontimetest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

// This app is rather shitty
// If I ever decide to improve it, I will do so in an entirely new project named ReactionTime2
// But w/ the same package name and keystore so I can upload a 2.0 of the same app
// My capabilities have just improved so much none of the code here is useful, easier to restart
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void onStartClick(View view) {
        startActivity(new Intent(this, BROGActivity.class));
    }

    public void onHighScoresClick(View view) {
        startActivity(new Intent(this, HighScoresActivity.class));
    }

    public void onMoreGamesClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=pub:Hackman"));
        startActivity(intent);
    }
}
