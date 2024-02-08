package android.application.duties_for_all;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.duties_for_all.R;

import java.util.Locale;
import java.util.Objects;

public class ActivityBlockDescription extends AppCompatActivity implements View.OnClickListener {



    //What is done in this activity:
    //for each block the user selects the time, number of people, and which grades should be included
    //they can describe the block in any order and re-describe blocks if needed
    //they cannot proceed to the next stage unless all information has been filled in
    //the next activity is chosen based on whether all blocks have been described



    //retrieve data passed on from the previous activities
    int type;
    String date;
    int nBlocks;
    String title;
    String[] times;
    int[] nPeoples;
    boolean[] predps;
    boolean[] dp1s;
    boolean[] dp2s;
    boolean[] locals;
    boolean[] nonLocals;

    //define all buttons and checkboxes (here so that they can be used in the onClick method)
    Button btnHelp;
    Button btnGoBack;
    Button btnNext;
    Button btnBlockIndex;
    Button btnTime;
    CheckBox checkboxPreDP;
    CheckBox checkboxDP1;
    CheckBox checkboxDP2;
    CheckBox checkboxLocal;
    CheckBox checkboxNonLocal;

    //define block index to use in arrays
    int blockIndex;

    //define parameters asked in this activity
    String time;
    int numberOfPeople;
    boolean includePreDP;
    boolean includeDP1;
    boolean includeDP2;
    boolean includeLocals;
    boolean includeNonLocals;

    //create the time variables for the time picker
    int minute, hour;

    //when the activity is started:
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_description);

        //retrieve data passed on from previous activities
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getInt("type");
            date = extras.getString("date");
            nBlocks = extras.getInt("nBlocks");
            title = extras.getString("title");
            times = extras.getStringArray("times");
            nPeoples = extras.getIntArray("nPeoples");
            predps = extras.getBooleanArray("predps");
            dp1s = extras.getBooleanArray("dp1s");
            dp2s = extras.getBooleanArray("dp2s");
            locals = extras.getBooleanArray("locals");
            nonLocals = extras.getBooleanArray("nonlocals");
        }

        //set onClick listeners to all buttons except the time button
        btnHelp = findViewById(R.id.BlockDescriptionBtnHelp);
        btnHelp.setOnClickListener(this);
        btnGoBack = findViewById(R.id.BlockDescriptionBtnGoBack);
        btnGoBack.setOnClickListener(this);
        btnNext = findViewById(R.id.BlockDescriptionBtnNext);
        btnNext.setOnClickListener(this);
        btnBlockIndex = findViewById(R.id.BlockDescriptionBtnBlockIndex);
        btnBlockIndex.setOnClickListener(this);
        btnTime = findViewById(R.id.BlockDescriptionBtnTime);

        //set all checkboxes
        checkboxPreDP = findViewById(R.id.BlockDescriptionCheckBoxPreDP);
        checkboxDP1 = findViewById(R.id.BlockDescriptionCheckBoxDP1);
        checkboxDP2 = findViewById(R.id.BlockDescriptionCheckBoxDP2);
        checkboxLocal = findViewById(R.id.BlockDescriptionCheckBoxLocals);
        checkboxNonLocal = findViewById(R.id.BlockDescriptionCheckBoxNonLocals);

        //set all checkboxes as checked
        checkboxPreDP.setChecked(true);
        checkboxDP1.setChecked(true);
        checkboxDP2.setChecked(true);
        checkboxLocal.setChecked(true);
        checkboxNonLocal.setChecked(true);

        //get number of people (define EditText and set onKeyListener)
        EditText editTxtNumberOfPeople = findViewById(R.id.BlockDescriptionEditTxtNumberOfPeople);
        editTxtNumberOfPeople.setOnKeyListener((v, keyCode, event) -> { //sets what is done when the 'enter' key is pressed
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                //when the key is pressed
                int input = Integer.parseInt(editTxtNumberOfPeople.getText().toString());

                //define the box around the EditText
                TextView numberOfPeopleBox = findViewById(R.id.BlockDescriptionNumberOfPeopleBox);
                //check if the input is withing the given range (1-20)
                if (input > 20 || input < 1) { //the input is out of range
                    //makes the input box red
                    numberOfPeopleBox.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.incorrect));
                    //inform the user that the input is out of range
                    Toast.makeText(ActivityBlockDescription.this, "Number of people cannot be greater than 20", Toast.LENGTH_SHORT).show();
                    editTxtNumberOfPeople.setText(null, TextView.BufferType.EDITABLE); //removes the input text
                    numberOfPeople = 0; //resets the number of people
                }
                else { //the input is in range
                    //removes the 'incorrect' tint from the box (has effect only if it is not the first attempt)
                    numberOfPeopleBox.setBackgroundTintList(null);
                    numberOfPeople = input; //sets the number of people
                }
                return true;
            }
            return false;
        });

        //automatically set block index (to the first block that is not yet filled)
        for (int i = 0; i < nBlocks; i++) {
            if (Objects.isNull(times[i])) {
                blockIndex = i + 1;
                break;
            }
        }

        //if the user went back from sorting all arrays should be reset
        if (blockIndex == 0) {
            times = new String[nBlocks];
            nPeoples = new int[nBlocks];
            predps = new boolean[nBlocks];
            dp1s = new boolean[nBlocks];
            dp2s = new boolean[nBlocks];
            locals = new boolean[nBlocks];
            nonLocals = new boolean[nBlocks];

            blockIndex = 1;
        }

        //display the block index on the block index button
        SpannableString index = new SpannableString("Block: " + blockIndex);
        index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
        btnBlockIndex.setText(index);
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

        //when the 'go back' button is clicked:
        if (view == btnGoBack) {
            //go back to create dutyList
            Intent intent = new Intent(this, ActivityCreateList.class);
            startActivity(intent);
        }

        //when the 'next' button is clicked:
        if (view == btnNext) {
            //define 'include' booleans based on the checkboxes
            includePreDP = checkboxPreDP.isChecked();
            includeDP1 = checkboxDP1.isChecked();
            includeDP2 = checkboxDP2.isChecked();
            includeLocals = checkboxLocal.isChecked();
            includeNonLocals = checkboxNonLocal.isChecked();

            //restriction: can move on to the next block/activity only if time and number of people are filled in and at least one checkbox is checked
            if (Objects.isNull(time) || Objects.isNull(numberOfPeople) || numberOfPeople == 0 ||
                    (!includePreDP && !includeDP1 && !includeDP2) ||
                    (!includeLocals && !includeNonLocals)) { //not everything is completed
                //tell the user to fill in all needed information
                Toast.makeText(ActivityBlockDescription.this, "Please fill in all information", Toast.LENGTH_SHORT).show();
            }
            else { //everything is completed
                //add time, number of people, and including parameters to the arrays with the index corresponding with the block index
                times[blockIndex - 1] = time;
                nPeoples[blockIndex - 1] = numberOfPeople;
                predps[blockIndex - 1] = includePreDP;
                dp1s[blockIndex - 1] = includeDP1;
                dp2s[blockIndex - 1] = includeDP2;
                locals[blockIndex - 1] = includeLocals;
                nonLocals[blockIndex - 1] = includeNonLocals;
                //check if all blocks have been described
                boolean allDone = true;
                for (int i = 0; i < nBlocks; i++) {
                    if (Objects.isNull(times[i])) {
                        allDone = false;
                    }
                }

                //choose next activity based on allDone
                Intent intent;
                if (allDone) {
                    //go to 'student information' once all blocks have been described
                    intent = new Intent(this, ActivitySorting.class);
                }
                else {
                    //restart 'block description' in some blocks have not been described yet
                    intent = new Intent(this, ActivityBlockDescription.class);
                }
                //pass on data to the next activity
                intent.putExtra("type", type);
                intent.putExtra("date", date);
                intent.putExtra("nBlocks", nBlocks);
                intent.putExtra("title",title);
                intent.putExtra("times", times);
                intent.putExtra("nPeoples", nPeoples);
                intent.putExtra("predps", predps);
                intent.putExtra("dp1s", dp1s);
                intent.putExtra("dp2s", dp2s);
                intent.putExtra("locals", locals);
                intent.putExtra("nonlocals", nonLocals);
                intent.putExtra("went back", false); //to show that the user didn't come back from dutyList created

                startActivity(intent);
            }
        }

        //when the 'block index' button is clicked
        if (view == btnBlockIndex) { //opens a pop-up where the user chooses which block they'll be describing
            //define the pop-up, add all buttons and icons to arrays
            View popup = View.inflate(this, R.layout.popup_block_index, null);

            //display the pop-up
            int height = LinearLayout.LayoutParams.WRAP_CONTENT; //makes pop-up height depend on the text length
            final PopupWindow popupWindow = new PopupWindow(popup, 950, height, true); //'focusable:true' allows to close pop-up by touch out of the pop-up window
            popupWindow.setElevation(200); //adds a shadow to the pop-up
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            //define buttons and text view (V/X)
            Button btnIndex1 = popup.findViewById(R.id.popupBlockIndexBtn1);
            Button btnIndex2 = popup.findViewById(R.id.popupBlockIndexBtn2);
            Button btnIndex3 = popup.findViewById(R.id.popupBlockIndexBtn3);
            Button btnIndex4 = popup.findViewById(R.id.popupBlockIndexBtn4);
            Button btnIndex5 = popup.findViewById(R.id.popupBlockIndexBtn5);
            Button btnIndex6 = popup.findViewById(R.id.popupBlockIndexBtn6);
            Button btnIndex7 = popup.findViewById(R.id.popupBlockIndexBtn7);
            Button btnIndex8 = popup.findViewById(R.id.popupBlockIndexBtn8);
            Button btnIndex9 = popup.findViewById(R.id.popupBlockIndexBtn9);
            Button btnIndex10 = popup.findViewById(R.id.popupBlockIndexBtn10);
            TextView txtIndex1 = popup.findViewById(R.id.popupBlockIndexTxt1);
            TextView txtIndex2 = popup.findViewById(R.id.popupBlockIndexTxt2);
            TextView txtIndex3 = popup.findViewById(R.id.popupBlockIndexTxt3);
            TextView txtIndex4 = popup.findViewById(R.id.popupBlockIndexTxt4);
            TextView txtIndex5 = popup.findViewById(R.id.popupBlockIndexTxt5);
            TextView txtIndex6 = popup.findViewById(R.id.popupBlockIndexTxt6);
            TextView txtIndex7 = popup.findViewById(R.id.popupBlockIndexTxt7);
            TextView txtIndex8 = popup.findViewById(R.id.popupBlockIndexTxt8);
            TextView txtIndex9 = popup.findViewById(R.id.popupBlockIndexTxt9);
            TextView txtIndex10 = popup.findViewById(R.id.popupBlockIndexTxt10);

            //assign invisibility and  + set button listeners
            if (nBlocks == 1) {
                //listeners
                btnIndex1.setOnClickListener(v -> {
                    blockIndex = 1;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });

                //icons
                btnIndex1.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[0])) {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}

                //hide unnecessary buttons
                btnIndex2.setVisibility(View.GONE);
                txtIndex2.setVisibility(View.GONE);
                btnIndex3.setVisibility(View.GONE);
                txtIndex3.setVisibility(View.GONE);
                btnIndex4.setVisibility(View.GONE);
                txtIndex4.setVisibility(View.GONE);
                btnIndex5.setVisibility(View.GONE);
                txtIndex5.setVisibility(View.GONE);
                btnIndex6.setVisibility(View.GONE);
                txtIndex6.setVisibility(View.GONE);
                btnIndex7.setVisibility(View.GONE);
                txtIndex7.setVisibility(View.GONE);
                btnIndex8.setVisibility(View.GONE);
                txtIndex8.setVisibility(View.GONE);
                btnIndex9.setVisibility(View.GONE);
                txtIndex9.setVisibility(View.GONE);
                btnIndex10.setVisibility(View.GONE);
                txtIndex10.setVisibility(View.GONE);
            }
            if (nBlocks == 2) {
                //listeners
                btnIndex1.setOnClickListener(v -> {
                    blockIndex = 1;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex2.setOnClickListener(v -> {
                    blockIndex = 2;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });

                //icons
                btnIndex1.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[0])) {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex2.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[1])) {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}

                //hide unnecessary buttons
                btnIndex3.setVisibility(View.GONE);
                txtIndex3.setVisibility(View.GONE);
                btnIndex4.setVisibility(View.GONE);
                txtIndex4.setVisibility(View.GONE);
                btnIndex5.setVisibility(View.GONE);
                txtIndex5.setVisibility(View.GONE);
                btnIndex6.setVisibility(View.GONE);
                txtIndex6.setVisibility(View.GONE);
                btnIndex7.setVisibility(View.GONE);
                txtIndex7.setVisibility(View.GONE);
                btnIndex8.setVisibility(View.GONE);
                txtIndex8.setVisibility(View.GONE);
                btnIndex9.setVisibility(View.GONE);
                txtIndex9.setVisibility(View.GONE);
                btnIndex10.setVisibility(View.GONE);
                txtIndex10.setVisibility(View.GONE);
            }
            if (nBlocks == 3) {
                //listeners
                btnIndex1.setOnClickListener(v -> {
                    blockIndex = 1;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex2.setOnClickListener(v -> {
                    blockIndex = 2;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex3.setOnClickListener(v -> {
                    blockIndex = 3;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });

                //icons
                btnIndex1.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[0])) {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex2.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[1])) {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex3.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[2])) {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}

                //hide unnecessary buttons
                btnIndex4.setVisibility(View.GONE);
                txtIndex4.setVisibility(View.GONE);
                btnIndex5.setVisibility(View.GONE);
                txtIndex5.setVisibility(View.GONE);
                btnIndex6.setVisibility(View.GONE);
                txtIndex6.setVisibility(View.GONE);
                btnIndex7.setVisibility(View.GONE);
                txtIndex7.setVisibility(View.GONE);
                btnIndex8.setVisibility(View.GONE);
                txtIndex8.setVisibility(View.GONE);
                btnIndex9.setVisibility(View.GONE);
                txtIndex9.setVisibility(View.GONE);
                btnIndex10.setVisibility(View.GONE);
                txtIndex10.setVisibility(View.GONE);
            }
            if (nBlocks == 4) {
                //listeners
                btnIndex1.setOnClickListener(v -> {
                    blockIndex = 1;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex2.setOnClickListener(v -> {
                    blockIndex = 2;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex3.setOnClickListener(v -> {
                    blockIndex = 3;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex4.setOnClickListener(v -> {
                    blockIndex = 4;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });

                //icons
                btnIndex1.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[0])) {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex2.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[1])) {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex3.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[2])) {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex4.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[3])) {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}

                //hide unnecessary buttons
                btnIndex5.setVisibility(View.GONE);
                txtIndex5.setVisibility(View.GONE);
                btnIndex6.setVisibility(View.GONE);
                txtIndex6.setVisibility(View.GONE);
                btnIndex7.setVisibility(View.GONE);
                txtIndex7.setVisibility(View.GONE);
                btnIndex8.setVisibility(View.GONE);
                txtIndex8.setVisibility(View.GONE);
                btnIndex9.setVisibility(View.GONE);
                txtIndex9.setVisibility(View.GONE);
                btnIndex10.setVisibility(View.GONE);
                txtIndex10.setVisibility(View.GONE);
            }
            if (nBlocks == 5) {
                //listeners
                btnIndex1.setOnClickListener(v -> {
                    blockIndex = 1;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex2.setOnClickListener(v -> {
                    blockIndex = 2;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex3.setOnClickListener(v -> {
                    blockIndex = 3;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex4.setOnClickListener(v -> {
                    blockIndex = 4;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex5.setOnClickListener(v -> {
                    blockIndex = 5;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });

                //icons
                btnIndex1.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[0])) {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex2.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[1])) {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex3.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[2])) {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex4.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[3])) {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex5.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[4])) {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}

                //hide unnecessary buttons
                btnIndex6.setVisibility(View.GONE);
                txtIndex6.setVisibility(View.GONE);
                btnIndex7.setVisibility(View.GONE);
                txtIndex7.setVisibility(View.GONE);
                btnIndex8.setVisibility(View.GONE);
                txtIndex8.setVisibility(View.GONE);
                btnIndex9.setVisibility(View.GONE);
                txtIndex9.setVisibility(View.GONE);
                btnIndex10.setVisibility(View.GONE);
                txtIndex10.setVisibility(View.GONE);
            }
            if (nBlocks == 6) {
                //listeners
                btnIndex1.setOnClickListener(v -> {
                    blockIndex = 1;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex2.setOnClickListener(v -> {
                    blockIndex = 2;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex3.setOnClickListener(v -> {
                    blockIndex = 3;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex4.setOnClickListener(v -> {
                    blockIndex = 4;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex5.setOnClickListener(v -> {
                    blockIndex = 5;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex6.setOnClickListener(v -> {
                    blockIndex = 6;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });

                //icons
                btnIndex1.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[0])) {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex2.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[1])) {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex3.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[2])) {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex4.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[3])) {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex5.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[4])) {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex6.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[5])) {txtIndex6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}

                //hide unnecessary buttons
                btnIndex7.setVisibility(View.GONE);
                txtIndex7.setVisibility(View.GONE);
                btnIndex8.setVisibility(View.GONE);
                txtIndex8.setVisibility(View.GONE);
                btnIndex9.setVisibility(View.GONE);
                txtIndex9.setVisibility(View.GONE);
                btnIndex10.setVisibility(View.GONE);
                txtIndex10.setVisibility(View.GONE);
            }
            if (nBlocks == 7) {
                //listeners
                btnIndex1.setOnClickListener(v -> {
                    blockIndex = 1;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex2.setOnClickListener(v -> {
                    blockIndex = 2;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex3.setOnClickListener(v -> {
                    blockIndex = 3;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex4.setOnClickListener(v -> {
                    blockIndex = 4;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex5.setOnClickListener(v -> {
                    blockIndex = 5;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex6.setOnClickListener(v -> {
                    blockIndex = 6;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex7.setOnClickListener(v -> {
                    blockIndex = 7;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });

                //icons
                btnIndex1.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[0])) {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex2.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[1])) {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex3.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[2])) {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex4.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[3])) {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex5.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[4])) {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex6.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[5])) {txtIndex6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex7.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[6])) {txtIndex7.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex7.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}

                //hide unnecessary buttons
                btnIndex8.setVisibility(View.GONE);
                txtIndex8.setVisibility(View.GONE);
                btnIndex9.setVisibility(View.GONE);
                txtIndex9.setVisibility(View.GONE);
                btnIndex10.setVisibility(View.GONE);
                txtIndex10.setVisibility(View.GONE);
            }
            if (nBlocks == 8) {
                //listeners
                btnIndex1.setOnClickListener(v -> {
                    blockIndex = 1;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex2.setOnClickListener(v -> {
                    blockIndex = 2;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex3.setOnClickListener(v -> {
                    blockIndex = 3;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex4.setOnClickListener(v -> {
                    blockIndex = 4;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex5.setOnClickListener(v -> {
                    blockIndex = 5;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex6.setOnClickListener(v -> {
                    blockIndex = 6;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex7.setOnClickListener(v -> {
                    blockIndex = 7;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex8.setOnClickListener(v -> {
                    blockIndex = 8;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });

                //icons
                btnIndex1.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[0])) {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex2.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[1])) {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex3.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[2])) {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex4.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[3])) {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex5.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[4])) {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex6.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[5])) {txtIndex6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex7.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[6])) {txtIndex7.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex7.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex8.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[7])) {txtIndex8.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex8.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}

                //hide unnecessary buttons
                btnIndex9.setVisibility(View.GONE);
                txtIndex9.setVisibility(View.GONE);
                btnIndex10.setVisibility(View.GONE);
                txtIndex10.setVisibility(View.GONE);
            }
            if (nBlocks == 9) {
                //listeners
                btnIndex1.setOnClickListener(v -> {
                    blockIndex = 1;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex2.setOnClickListener(v -> {
                    blockIndex = 2;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex3.setOnClickListener(v -> {
                    blockIndex = 3;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex4.setOnClickListener(v -> {
                    blockIndex = 4;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex5.setOnClickListener(v -> {
                    blockIndex = 5;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex6.setOnClickListener(v -> {
                    blockIndex = 6;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex7.setOnClickListener(v -> {
                    blockIndex = 7;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex8.setOnClickListener(v -> {
                    blockIndex = 8;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex9.setOnClickListener(v -> {
                    blockIndex = 9;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });

                //icons
                btnIndex1.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[0])) {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex2.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[1])) {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex3.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[2])) {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex4.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[3])) {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex5.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[4])) {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex6.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[5])) {txtIndex6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex7.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[6])) {txtIndex7.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex7.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex8.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[7])) {txtIndex8.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex8.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex9.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[8])) {txtIndex9.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex9.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}

                //hide unnecessary buttons
                btnIndex10.setVisibility(View.GONE);
                txtIndex10.setVisibility(View.GONE);
            }
            if (nBlocks == 10) {
                //listeners
                btnIndex1.setOnClickListener(v -> {
                    blockIndex = 1;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex2.setOnClickListener(v -> {
                    blockIndex = 2;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex3.setOnClickListener(v -> {
                    blockIndex = 3;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex4.setOnClickListener(v -> {
                    blockIndex = 4;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex5.setOnClickListener(v -> {
                    blockIndex = 5;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex6.setOnClickListener(v -> {
                    blockIndex = 6;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex7.setOnClickListener(v -> {
                    blockIndex = 7;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex8.setOnClickListener(v -> {
                    blockIndex = 8;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex9.setOnClickListener(v -> {
                    blockIndex = 9;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });
                btnIndex10.setOnClickListener(v -> {
                    blockIndex = 10;
                    SpannableString index = new SpannableString("Block: " + blockIndex);
                    index.setSpan(new UnderlineSpan(), 0, index.length(), 0);
                    btnBlockIndex.setText(index);
                    popupWindow.dismiss();
                });

                //icons
                btnIndex1.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[0])) {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex2.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[1])) {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex3.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[2])) {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex4.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[3])) {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex5.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[4])) {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex6.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[5])) {txtIndex6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex7.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[6])) {txtIndex7.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex7.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex8.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[7])) {txtIndex8.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex8.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex9.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[8])) {txtIndex9.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex9.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
                btnIndex10.setVisibility(View.VISIBLE);
                if (Objects.isNull(times[9])) {txtIndex10.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_x, 0, 0, 0);}
                else {txtIndex10.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0);}
            }
        }
    }

    //method for the time button
    public void openTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, chosenHour, chosenMin) -> {
            //get the chosen time from the time picker
            hour = chosenHour;
            minute = chosenMin;
            //format the selected time to set as the time button text
            time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            btnTime.setText(time);
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show(); //opens a clock where the user can choose the time
    }
}