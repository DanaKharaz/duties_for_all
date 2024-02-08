package android.application.duties_for_all;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duties_for_all.R;

public class ActivityMain extends AppCompatActivity {



    //What is done in this activity:
    //the 'welcome' screen is shown for for 2.5 seconds
    //after the time is up, the 'home' activity starts automatically



    //when the activity is started:
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //make the layout full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set delay
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //after the time is up:
            Intent intent = new Intent(ActivityMain.this, ActivityVerification.class);
            startActivity(intent);
            //if the user clicks '<' in the next activity, they should exit the app and not come back here
            finish();
        }, 2500); //time of delay (in milliseconds)
    }
    //onCreate ends
}