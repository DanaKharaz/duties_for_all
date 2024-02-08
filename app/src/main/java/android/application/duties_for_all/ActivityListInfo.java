package android.application.duties_for_all;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ActivityListInfo extends AppCompatActivity implements View.OnClickListener {



    //What is done in this activity:
    //the user is asked for the date and number of blocks of the dutyList
    //instead they can choose to use one of the templates
    //they are allowed to proceed to the next activity only if all of the information above has been filled in or they chose a template



    //retrieve data passed on from the previous activity
    int type;

    //define all buttons (here so that they can be used in the onClick method)
    Button btnHelp;
    Button btnGoBack;
    Button btnNext;
    Button btnTemplates;
    Button btnDate;

    //define parameters asked in this activity
    Date date;
    int nBlocks;
    String title = "";

    //create a date picker for the date button
    DatePickerDialog datePickerDialog;

    //past list data
    ArrayList<String> listKeys = new ArrayList<>();

    //when the activity is started:
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_info);

        //retrieve data passed on from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {type = extras.getInt("type");}

        //set onClick listeners to all buttons except the date button
        btnHelp = findViewById(R.id.ListInfoBtnHelp);
        btnHelp.setOnClickListener(this);
        btnGoBack = findViewById(R.id.ListInfoBtnGoBack);
        btnGoBack.setOnClickListener(this);
        btnNext = findViewById(R.id.ListInfoBtnNext);
        btnNext.setOnClickListener(this);
        btnTemplates = findViewById(R.id.ListInfoBtnTemplates);

        //hide unneeded GUI elements and collect list information based on the list type
        if (type == 4) {
            //hide templates button
            btnTemplates.setVisibility(View.GONE);

            //get title
            EditText editTxtTitle = findViewById(R.id.ListInfoEditTxtTitle);
            editTxtTitle.setOnKeyListener((v, keyCode, event) -> { //sets what is done when the 'enter' key is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //when the key is pressed
                    String input = editTxtTitle.getText().toString();

                    //define the box around the EditText
                    TextView titleBox = findViewById(R.id.ListInfoTitleBox);
                    //check if the input is too long (over 25 characters)
                    if (input.length() > 25) { //the input is too long
                        Toast.makeText(ActivityListInfo.this, "The title is too long", Toast.LENGTH_SHORT).show(); //informs the user that the input is too long
                        titleBox.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.incorrect)); //makes the input box red
                        editTxtTitle.setText(null, TextView.BufferType.EDITABLE); //removes the text input
                    }
                    else {
                        titleBox.setBackgroundTintList(null); //removes the 'incorrect' tint from the box (has effect only if it is not the first attempt)
                        title = input; //sets the title
                    }
                    return true;
                }
                return false;
            });
        }
        else {
            //hide title fields
            findViewById(R.id.ListInfoLayoutTitleEditTxt).setVisibility(View.GONE);
            findViewById(R.id.ListInfoLblTitle).setVisibility(View.GONE);

            btnTemplates.setOnClickListener(this);
        }

        //format and initialize the date button
        initDatePicker(); //initializes the date picker
        btnDate = findViewById(R.id.ListInfoBtnDate);
        btnDate.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.box_color));

        //get number of blocks (define EditText and set onKeyListener)
        EditText editTxtNumberOfBlocks = findViewById(R.id.ListInfoEditTxtNumberOfBlocks);
        editTxtNumberOfBlocks.setOnKeyListener((v, keyCode, event) -> { //sets what is done when the 'enter' key is pressed
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                //when the key is pressed
                int input = Integer.parseInt(editTxtNumberOfBlocks.getText().toString());

                //define the box around the EditText
                TextView numberOfBlocksBox = findViewById(R.id.ListInfoNumberOfBlocksBox);
                //check if the input is withing the given range (1-10)
                if (input > 10 || input < 1) { //the input is out of range
                    Toast.makeText(ActivityListInfo.this, "Number of blocks out of range", Toast.LENGTH_SHORT).show(); //informs the user that the input is out of range
                    numberOfBlocksBox.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.incorrect)); //makes the input box red
                    editTxtNumberOfBlocks.setText(null, TextView.BufferType.EDITABLE); //removes the text input
                    nBlocks = 0; //resets the number of blocks
                }
                else { //the input is in range
                    numberOfBlocksBox.setBackgroundTintList(null); //removes the 'incorrect' tint from the box (has effect only if it is not the first attempt)
                    nBlocks = input; //sets the number of blocks
                }
                return true;
            }
            return false;
        });

        //collect data from the database of all past lists
        FirebaseDatabase.getInstance().getReference("List_Archive").get().addOnCompleteListener(task -> databaseTask(task.getResult()));
    }
    //onCreate ends

    //method used to store the past list data from the database in arrays
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void databaseTask(DataSnapshot dataSnapshot) {
        //add all list keys (list date and type)
        for (DataSnapshot list : dataSnapshot.getChildren()) {
            listKeys.add(list.getKey());
        }
    }

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
            //go back to Create DutyList
            Intent intent = new Intent(this, ActivityCreateList.class);
            startActivity(intent);
        }

        //when the 'next' button is clicked:
        if (view == btnNext) {
            //warn the user if a list of the same type for the same day already exists
            //find the dutyList type
            String listType = "";
            if (type == 0) {listType = "dining hall";}
            else if (type == 1) {listType = "cow";}
            else if (type == 2) {listType = "chicken";}
            else if (type == 3) {listType = "visitor center";}
            else if (type == 4) {listType = "other";}
            //add list to the list archive generating a unique key
            String possibleKey = date + " (" + listType + ")";
            //check if a list with this key already exists in the database
            if (listKeys.contains(possibleKey)) {
                //define the pop-up, confirm and close buttons, and TextView
                View popup = View.inflate(this, R.layout.popup_warning, null);
                Button btnConfirm = popup.findViewById(R.id.popupWarningBtn);
                Button btnX = popup.findViewById(R.id.popupWarningBtnX);
                TextView popupTxt = popup.findViewById(R.id.popupWarningTxt);

                //make the TextView scrollable (in case the text does no fully fit into the window)
                popupTxt.setMovementMethod(new ScrollingMovementMethod());

                //set warning text
                popupTxt.setText(R.string.list_info_popup_warning);
                popupTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                popupTxt.setTextColor(this.getResources().getColor(R.color.text_color));

                //display pop-up
                //makes pop-up height and width depend on the screen size and content
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                int width = displayMetrics.widthPixels - 150;
                final PopupWindow popupWindow = new PopupWindow(popup, width, height,true); //'focusable:true' allows to close pop-up by touch out of the pop-up window
                popupWindow.setElevation(200); //adds a shadow to the pop-up
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                //move on through confirm button
                btnConfirm.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ActivityBlockDescription.class);
                    //pass on data to the next activity
                    intent.putExtra("type", type);
                    intent.putExtra("date", date.toString());
                    intent.putExtra("nBlocks", nBlocks);
                    intent.putExtra("title", "");
                    //create arrays to later create the dutyList itself and pass them on to the next activity
                    String[] times = new String[nBlocks];
                    intent.putExtra("times", times);
                    int[] nPeoples = new int[nBlocks];
                    intent.putExtra("nPeoples", nPeoples);
                    boolean[] predps = new boolean[nBlocks];
                    intent.putExtra("predps", predps);
                    boolean[] dp1s = new boolean[nBlocks];
                    intent.putExtra("dp1s", dp1s);
                    boolean[] dp2s = new boolean[nBlocks];
                    intent.putExtra("dp2s", dp2s);
                    boolean[] locals = new boolean[nBlocks];
                    intent.putExtra("locals", locals);
                    boolean[] nonLocals = new boolean[nBlocks];
                    intent.putExtra("nonlocals", nonLocals);

                    //go to the block description activity
                    startActivity(intent);
                });

                //close popup through button
                btnX.setOnClickListener(v -> popupWindow.dismiss());
            }
            else {
                //restriction: the user cannot go to the next activity if some fields have no been filled in (and no template was chosen)
                if ((Objects.isNull(date) || Objects.isNull(nBlocks) || nBlocks == 0 || title.equals("")) && type == 4) { //if any fields are blank ("other" list)
                    Toast.makeText(this, "Please select date and add number of blocks and title", Toast.LENGTH_SHORT).show(); //informs the user to fill in all fields
                }
                else if ((Objects.isNull(date) || Objects.isNull(nBlocks) || nBlocks == 0) && type != 4) { //if any fields are blank (all other types)
                    Toast.makeText(this, "Please select date and add number of blocks", Toast.LENGTH_SHORT).show(); //informs the user to fill in all fields
                }
                else { //if no fields are blank
                    Intent intent = new Intent(this, ActivityBlockDescription.class);
                    //pass on data to the next activity
                    intent.putExtra("type", type);
                    intent.putExtra("date", date.toString());
                    intent.putExtra("nBlocks", nBlocks);
                    intent.putExtra("title", title);
                    //create arrays to later create the dutyList itself and pass them on to the next activity
                    String[] times = new String[nBlocks];
                    intent.putExtra("times", times);
                    int[] nPeoples = new int[nBlocks];
                    intent.putExtra("nPeoples", nPeoples);
                    boolean[] predps = new boolean[nBlocks];
                    intent.putExtra("predps", predps);
                    boolean[] dp1s = new boolean[nBlocks];
                    intent.putExtra("dp1s", dp1s);
                    boolean[] dp2s = new boolean[nBlocks];
                    intent.putExtra("dp2s", dp2s);
                    boolean[] locals = new boolean[nBlocks];
                    intent.putExtra("locals", locals);
                    boolean[] nonLocals = new boolean[nBlocks];
                    intent.putExtra("nonlocals", nonLocals);

                    //go to the block description activity
                    startActivity(intent);
                }
            }
        }

        //when the 'templates' button is clicked:
        if (view == btnTemplates) {
            //warn the user if a list of the same type for the same day already exists
            //find the dutyList type
            String listType = "";
            if (type == 0) {listType = "dining hall";}
            else if (type == 1) {listType = "cow";}
            else if (type == 2) {listType = "chicken";}
            else if (type == 3) {listType = "visitor center";}
            else if (type == 4) {listType = "other";}
            //add list to the list archive generating a unique key
            String possibleKey = date + " (" + listType + ")";
            //check if a list with this key already exists in the database
            if (listKeys.contains(possibleKey)) {
                //define the pop-up, confirm and close buttons, and TextView
                View popup = View.inflate(this, R.layout.popup_warning, null);
                Button btnConfirm = popup.findViewById(R.id.popupWarningBtn);
                Button btnX = popup.findViewById(R.id.popupWarningBtnX);
                TextView popupTxt = popup.findViewById(R.id.popupWarningTxt);

                //make the TextView scrollable (in case the text does no fully fit into the window)
                popupTxt.setMovementMethod(new ScrollingMovementMethod());

                //set warning text
                popupTxt.setText(R.string.list_info_popup_warning);
                popupTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                popupTxt.setTextColor(this.getResources().getColor(R.color.text_color));

                //display pop-up
                //makes pop-up height and width depend on the screen size and content
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                int width = displayMetrics.widthPixels - 150;
                final PopupWindow popupWindow = new PopupWindow(popup, width, height,true); //'focusable:true' allows to close pop-up by touch out of the pop-up window
                popupWindow.setElevation(200); //adds a shadow to the pop-up
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                //move on through confirm button
                btnConfirm.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ActivityTemplates.class);
                    //pass on information to the next activity
                    intent.putExtra("from", "info"); //indicates origin since "templates" is accessed from multiple activities
                    intent.putExtra("type", type);
                    intent.putExtra("date", date.toString());

                    startActivity(intent);
                });

                //close popup through button
                btnX.setOnClickListener(v -> popupWindow.dismiss());
            }
            else {
                //the date has to be chosen first
                if (Objects.isNull(date)) {
                    Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, ActivityTemplates.class);
                    //pass on information to the next activity
                    intent.putExtra("from", "info"); //indicates origin since "templates" is accessed from multiple activities
                    intent.putExtra("type", type);
                    intent.putExtra("date", date.toString());

                    startActivity(intent);
                }
            }
        }
    }

    //all methods for the date button
    //onClick method for the date button
    public void openDatePicker(View view) {
        //open a calender where the user can choose the date
        datePickerDialog.show();
    }
    //initialize the date picker dialog
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            //get the chosen date from the date picker
            month = month + 1;
            date = new Date(day, month, year);
            btnDate.setText(date.toString());
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
    }
}