package android.application.duties_for_all;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.duties_for_all.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.Random;


public class ActivityVerification extends AppCompatActivity implements View.OnClickListener {



    //What is done in this activity:
    //the user is asked to type in the verification code, which is known only to the school staff
    //in case the user wants to change the code, a pin is sent to the staff email
    //the pin is valid for 5 minutes
    //if the user inputs the correct pin in the shown popup, they can create a new code



    //define all buttons (here so that they can be used in the onClick method)
    Button btnHelp;
    Button btnResetCode;

    //reset code
    TextView txtTimer;
    int timeLeft;
    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();
    Runnable runnableCode;

    //when the activity is started:
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //set onClick listeners to all buttons
        btnHelp = findViewById(R.id.VerificationBtnHelp);
        btnHelp.setOnClickListener(this);
        btnResetCode = findViewById(R.id.VerificationBtnResetCode);
        btnResetCode.setOnClickListener(this);

        //get the hash for the code in the database
        FirebaseDatabase.getInstance().getReference("Verification_code").get().addOnCompleteListener(task -> {
           String code = task.getResult().getValue(String.class);
            //check verification code (define EditText and set onKeyListener)
            EditText editTxtCode = findViewById(R.id.VerificationEditTxtCode);
            editTxtCode.setOnKeyListener((v, keyCode, event) -> { //sets what happens when the 'enter' key is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //when key is pressed
                    String  input = editTxtCode.getText().toString(); //gets input

                    //define the box around the EditText
                    TextView codeBox = findViewById(R.id.VerificationCodeBox);
                    //check if the input matches the verification code
                    if (String.valueOf(input.hashCode()).equals(code)) {
                        codeBox.setBackgroundTintList(null); //removes the 'incorrect' tint from the box (has effect only if it is not the first attempt)
                        Toast.makeText(ActivityVerification.this, "Verification Successful", Toast.LENGTH_SHORT).show(); //informs the user of successful verification
                        //go to the 'home' activity
                        Intent intent = new Intent(ActivityVerification.this, ActivityHome.class);
                        startActivity(intent);
                        finish();
                    }
                    //if the input does not match the verification code
                    else {
                        codeBox.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.incorrect)); //makes the code box red
                        editTxtCode.setText("", TextView.BufferType.EDITABLE); //removes the text input
                        Toast.makeText(ActivityVerification.this, "Incorrect Code", Toast.LENGTH_SHORT).show(); //informs user of unsuccessful verification
                    }
                    return true;
                }
                return false;
            });
        });
    }
    //onCreate ends

    //when the buttons with on-click listeners are clicked:
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        //when the 'help' button is clicked:
        if (view == btnHelp) {
            //create the link to the document
            Uri uri = Uri.parse("https://docs.google.com/document/d/1TGHnD-JMTUmTtkx_ax5IOEbM4mjHodezsYier7DWnq8/edit?usp=sharing"); //missing 'http://' causes an error
            //open the link
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        //when the 'view verification code' button is clicked:
        if (view == btnResetCode) {
            //send email with a temporary PIN
            Random random = new Random();
            String randomCode = random.ints(48, 58) //only numbers (unicode indexes)
                    .limit(8)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            String message = getString(R.string.verification_str_message_start) + "   " + randomCode + getString(R.string.verification_str_message_end);

            JavaMailAPI javaMailAPI = new JavaMailAPI("boardingstafftest@gmail.com", getString(R.string.verification_str_subject), message);

            javaMailAPI.execute();

            //initialize a popup to change the verification code
            View popup = View.inflate(this, R.layout.popup_reset_code, null);
            TextView txtInstr = popup.findViewById(R.id.popupResetCodeInstr);
            Button btnX = popup.findViewById(R.id.popupResetCodeBtnX);
            EditText editTxtPin = popup.findViewById(R.id.popupResetCodeEditTxtPin);
            EditText editTxtCode1 = popup.findViewById(R.id.popupResetCodeEditTxtCode1);
            EditText editTxtCode2 = popup.findViewById(R.id.popupResetCodeEditTxtCode2);
            txtTimer = popup.findViewById(R.id.popupResetCodeTxtTimer);
            Button btnConfirm = popup.findViewById(R.id.popupResetCodeBtnConfirm);

            timeLeft = 301;
            handler.removeCallbacks(runnableCode);

            editTxtPin.setOnKeyListener((v, keyCode, event) -> { //sets what happens when the 'enter' key is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //when key is pressed
                    String  input = editTxtPin.getText().toString(); //gets input

                    if (input.equals(randomCode) && timeLeft != 0) {
                        editTxtCode1.setVisibility(View.VISIBLE);
                        editTxtCode2.setVisibility(View.VISIBLE);
                        btnConfirm.setVisibility(View.VISIBLE);
                        editTxtPin.setVisibility(View.GONE);
                        txtTimer.setVisibility(View.GONE);
                        handler.removeCallbacks(runnableCode);

                        txtInstr.setText(getString(R.string.popup_reset_code_instr2));
                    }
                    else if (input.equals(randomCode)) {
                        editTxtPin.setText("", TextView.BufferType.EDITABLE);
                        Toast.makeText(this, "The temporary PIN has expired", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        editTxtPin.setText("", TextView.BufferType.EDITABLE);
                        Toast.makeText(this, "The PIN does not match", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
                return false;
            });

            // Define the code block to be executed
            runnableCode = new Runnable() {
                public void run() {
                    // Do something here on the main thread
                    timeLeft = timeLeft - 1;
                    int sec = timeLeft % 60;
                    int min = (timeLeft - sec) / 60;
                    String time;
                    if (sec < 10) {time = min + ":0" + sec;}
                    else {time = min + ":" + sec;}
                    txtTimer.setText(time);
                    if (timeLeft > 0) {
                        // Repeat this the same runnable code block again
                        // 'this' is referencing the Runnable object
                        handler.postDelayed(this, 1000);
                    }
                }
            };
            // Start the initial runnable task by posting through the handler
            handler.post(runnableCode);

            //crete new code
            btnConfirm.setOnClickListener(v -> {
                String input1 = editTxtCode1.getText().toString();
                if (!input1.equals(editTxtCode2.getText().toString()) ||
                        input1.equals("") || Objects.isNull(input1)) {
                    editTxtCode1.setText("", TextView.BufferType.EDITABLE);
                    editTxtCode2.setText("", TextView.BufferType.EDITABLE);
                    Toast.makeText(this, "The codes do not match", Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseDatabase.getInstance().getReference("Verification_code").setValue(String.valueOf(input1.hashCode()));
                    Toast.makeText(ActivityVerification.this, "Verification code changed successfully", Toast.LENGTH_SHORT).show(); //informs the user of successful verification
                    //go to the 'home' activity
                    Intent intent = new Intent(ActivityVerification.this, ActivityHome.class);
                    startActivity(intent);
                    finish();
                }
            });

            //display pop-up
            //makes pop-up height and width depend on the screen size
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            int width = displayMetrics.widthPixels - 150;
            PopupWindow popupWindow = new PopupWindow(popup, width, height, true); //'true' to close pop-up by touch out of pop-up window
            popupWindow.setElevation(200); //adds shadow to pop-up
            popupWindow.showAtLocation(btnResetCode, Gravity.CENTER, 0, 0);

            //close pop-up through button and reset button text
            btnX.setOnClickListener(view1 -> popupWindow.dismiss());
        }
    }
}