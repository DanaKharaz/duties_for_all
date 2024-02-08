package android.application.duties_for_all;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duties_for_all.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ActivityListCreated extends AppCompatActivity implements View.OnClickListener  {



    //What is done in this activity:
    //the user is able to view the created dutyList or save the dutyList as a jpeg image
    //when the list is viewed or exported as an image, the extra students appear the same as others
    //the database is updated (list added/edited in the list archive, numbers done are updated)



    //define all buttons (here so that they can be used in the onClick method)
    Button btnViewList;
    Button btnExportImage;
    Button btnHomePage;
    Button btnGoBack;
    Button btnHelp;

    //retrieve data passed on from the previous activities
    String from;
    int type;
    String date;
    int nBlocks;
    String title;
    String[] times;
    int[] nPeoples;
    boolean[] predps;
    boolean[] dp1s;
    boolean[] dp2s;
    boolean includeLocals;
    boolean includeNonLocals;
    ArrayList<String> block1Sorted;
    ArrayList<String> block2Sorted;
    ArrayList<String> block3Sorted;
    ArrayList<String> block4Sorted;
    ArrayList<String> block5Sorted;
    ArrayList<String> block6Sorted;
    ArrayList<String> block7Sorted;
    ArrayList<String> block8Sorted;
    ArrayList<String> block9Sorted;
    ArrayList<String> block10Sorted;
    ArrayList<ArrayList<String>> allBlocksSorted = new ArrayList<>();
    ArrayList<Integer> block1Extra = new ArrayList<>();
    ArrayList<Integer> block2Extra = new ArrayList<>();
    ArrayList<Integer> block3Extra = new ArrayList<>();
    ArrayList<Integer> block4Extra = new ArrayList<>();
    ArrayList<Integer> block5Extra = new ArrayList<>();
    ArrayList<Integer> block6Extra = new ArrayList<>();
    ArrayList<Integer> block7Extra = new ArrayList<>();
    ArrayList<Integer> block8Extra = new ArrayList<>();
    ArrayList<Integer> block9Extra = new ArrayList<>();
    ArrayList<Integer> block10Extra = new ArrayList<>();
    ArrayList<ArrayList<Integer>> extraStudents = new ArrayList<>();

    //update database
    int nStudents = 0;

    //retrieve data from the student database
    ArrayList<Student> allStudents = new ArrayList<>();

    //set sorted array of chosen students
    ArrayList<ArrayList<String>> chosenStudentsNames = new ArrayList<>();

    //arrays used to create the table displaying the dutyList
    ArrayList<TextView> txtBlock1 = new ArrayList<>();
    ArrayList<TextView> txtBlock2 = new ArrayList<>();
    ArrayList<TextView> txtBlock3 = new ArrayList<>();
    ArrayList<TextView> txtBlock4 = new ArrayList<>();
    ArrayList<TextView> txtBlock5 = new ArrayList<>();
    ArrayList<TextView> txtBlock6 = new ArrayList<>();
    ArrayList<TextView> txtBlock7 = new ArrayList<>();
    ArrayList<TextView> txtBlock8 = new ArrayList<>();
    ArrayList<TextView> txtBlock9 = new ArrayList<>();
    ArrayList<TextView> txtBlock10 = new ArrayList<>();
    ArrayList<ArrayList<TextView>> txtAllBlocks = new ArrayList<>();
    ArrayList<TextView> txtTimes = new ArrayList<>();

    //define popup to view dutyList and/or export image
    View popup;

    //define layout to export it as an image
    LinearLayout layoutList;

    //keep reference to the list archive database
    DataSnapshot dsLists;

    //when the activity is started:
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_created);

        //retrieve data passed on from previous activities
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            from = extras.getString("from");
            type = extras.getInt("type");
            date = extras.getString("date");
            nBlocks = extras.getInt("nBlocks");
            title = extras.getString("title");
            times = extras.getStringArray("times");
            nPeoples = extras.getIntArray("nPeoples");
            if (from.equals("sorting")) {
                predps = extras.getBooleanArray("predps");
                dp1s = extras.getBooleanArray("dp1s");
                dp2s = extras.getBooleanArray("dp2s");
                includeLocals = extras.getBoolean("locals");
                includeNonLocals = extras.getBoolean("nonlocals");
            }
            block1Sorted = extras.getStringArrayList("block1Sorted");
            block2Sorted = extras.getStringArrayList("block2Sorted");
            block3Sorted = extras.getStringArrayList("block3Sorted");
            block4Sorted = extras.getStringArrayList("block4Sorted");
            block5Sorted = extras.getStringArrayList("block5Sorted");
            block6Sorted = extras.getStringArrayList("block6Sorted");
            block7Sorted = extras.getStringArrayList("block7Sorted");
            block8Sorted = extras.getStringArrayList("block8Sorted");
            block9Sorted = extras.getStringArrayList("block9Sorted");
            block10Sorted = extras.getStringArrayList("block10Sorted");
            block1Extra = extras.getIntegerArrayList("block1Extra");
            block2Extra = extras.getIntegerArrayList("block2Extra");
            block3Extra = extras.getIntegerArrayList("block3Extra");
            block4Extra = extras.getIntegerArrayList("block4Extra");
            block5Extra = extras.getIntegerArrayList("block5Extra");
            block6Extra = extras.getIntegerArrayList("block6Extra");
            block7Extra = extras.getIntegerArrayList("block7Extra");
            block8Extra = extras.getIntegerArrayList("block8Extra");
            block9Extra = extras.getIntegerArrayList("block9Extra");
            block10Extra = extras.getIntegerArrayList("block10Extra");
        }
        //put arrays in all blocks sorted
        allBlocksSorted.add(block1Sorted);
        allBlocksSorted.add(block2Sorted);
        allBlocksSorted.add(block3Sorted);
        allBlocksSorted.add(block4Sorted);
        allBlocksSorted.add(block5Sorted);
        allBlocksSorted.add(block6Sorted);
        allBlocksSorted.add(block7Sorted);
        allBlocksSorted.add(block8Sorted);
        allBlocksSorted.add(block9Sorted);
        allBlocksSorted.add(block10Sorted);

        //combine Boolean arrays about extra students into a 2D array
        extraStudents.add(block1Extra);
        extraStudents.add(block2Extra);
        extraStudents.add(block3Extra);
        extraStudents.add(block4Extra);
        extraStudents.add(block5Extra);
        extraStudents.add(block6Extra);
        extraStudents.add(block7Extra);
        extraStudents.add(block8Extra);
        extraStudents.add(block9Extra);
        extraStudents.add(block10Extra);

        //determine number of students
        for (int i : nPeoples) {nStudents = nStudents + i;}

        //set onClick listeners to all buttons
        btnViewList = findViewById(R.id.ListCreatedBtnViewList);
        btnViewList.setOnClickListener(this);
        btnExportImage = findViewById(R.id.ListCreatedBtnExportImage);
        btnExportImage.setOnClickListener(this);
        btnHomePage = findViewById(R.id.ListCreatedBtnMainMenu);
        btnHomePage.setOnClickListener(this);
        btnGoBack = findViewById(R.id.ListCreatedBtnGoBack);
        btnGoBack.setOnClickListener(this);
        btnHelp = findViewById(R.id.ListCreatedBtnHelp);
        btnHelp.setOnClickListener(this);

        //fix instruction if needed
        if (from.equals("editing")) {
            TextView txtInstr = findViewById(R.id.ListCreatedTxtInstr);
            txtInstr.setText(getResources().getString(R.string.list_created_instr2));
        }

        //collect data from the database of all students
        FirebaseDatabase.getInstance().getReference().get().addOnCompleteListener(task -> databaseTask(task.getResult()));
    }
    //onCreate ends

    //when the buttons with on-click listeners are clicked:
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        //when the 'view dutyList' button is clicked:
        if (view == btnViewList) {
            //display pop-up
            //makes pop-up height depend on the screen size
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels - 150;
            int width = displayMetrics.widthPixels - 150;
            final PopupWindow popupWindow = new PopupWindow(popup, width, height,true); //'true' to close pop-up by touch out of pop-up window
            popupWindow.setElevation(200); //adds shadow to pop-up
            popupWindow.showAtLocation(view, Gravity.TOP, 0, 170);

            //close pop-up through button
            Button btnX = popup.findViewById(R.id.popupFinishedListBtnX);
            btnX.setOnClickListener(v -> popupWindow.dismiss());
        }

        //when the 'export image' button is clicked:
        if (view == btnExportImage) {
            //define the layout
            layoutList = popup.findViewById(R.id.popupFinishedListLayoutList);

            //save layout as an image
            try {
                saveImage();
            }
            catch (IOException e) {
                Toast.makeText(this, "Failed to save image, please try again later", Toast.LENGTH_SHORT).show();
            }
        }

        //when the 'home page' button is clicked:
        if (view == btnHomePage){
            //go to the home activity
            Intent intent = new Intent(this, ActivityHome.class);
            startActivity(intent);
        }

        //when the 'go back' button is clicked:
        if (view == btnGoBack){
            Intent intent;
            if (from.equals("sorting")) {
                //go to sorting, which will display the dutyList how it was sorted
                intent = new Intent(this, ActivitySorting.class);
                intent.putExtra("predps", predps);
                intent.putExtra("dp1s", dp1s);
                intent.putExtra("dp2s", dp2s);
                intent.putExtra("locals", includeLocals);
                intent.putExtra("nonlocals", includeNonLocals);
            }
            else {
                //go to editing, which will display the dutyList how it was sorted
                intent = new Intent(this, ActivityEditing.class);
            }

            //pass back data
            intent.putExtra("type", type);
            intent.putExtra("date", date);
            intent.putExtra("nBlocks", nBlocks);
            intent.putExtra("title", title);
            intent.putExtra("times", times);
            intent.putExtra("nPeoples", nPeoples);
            //pass back an indicator the the user went back and the sorted arrays of students
            intent.putExtra("went back", true);
            intent.putStringArrayListExtra("block1Sorted", allBlocksSorted.get(0));
            intent.putStringArrayListExtra("block2Sorted", allBlocksSorted.get(1));
            intent.putStringArrayListExtra("block3Sorted", allBlocksSorted.get(2));
            intent.putStringArrayListExtra("block4Sorted", allBlocksSorted.get(3));
            intent.putStringArrayListExtra("block5Sorted", allBlocksSorted.get(4));
            intent.putStringArrayListExtra("block6Sorted", allBlocksSorted.get(5));
            intent.putStringArrayListExtra("block7Sorted", allBlocksSorted.get(6));
            intent.putStringArrayListExtra("block8Sorted", allBlocksSorted.get(7));
            intent.putStringArrayListExtra("block9Sorted", allBlocksSorted.get(8));
            intent.putStringArrayListExtra("block10Sorted", allBlocksSorted.get(9));
            intent.putIntegerArrayListExtra("block1Extra", extraStudents.get(0));
            intent.putIntegerArrayListExtra("block2Extra", extraStudents.get(1));
            intent.putIntegerArrayListExtra("block3Extra", extraStudents.get(2));
            intent.putIntegerArrayListExtra("block4Extra", extraStudents.get(3));
            intent.putIntegerArrayListExtra("block5Extra", extraStudents.get(4));
            intent.putIntegerArrayListExtra("block6Extra", extraStudents.get(5));
            intent.putIntegerArrayListExtra("block7Extra", extraStudents.get(6));
            intent.putIntegerArrayListExtra("block8Extra", extraStudents.get(7));
            intent.putIntegerArrayListExtra("block9Extra", extraStudents.get(8));
            intent.putIntegerArrayListExtra("block10Extra", extraStudents.get(9));

            startActivity(intent);
        }

        //when the 'help' button is clicked:
        if (view == btnHelp) {
            //create the link to the document
            Uri uri = Uri.parse("https://docs.google.com/document/d/1TGHnD-JMTUmTtkx_ax5IOEbM4mjHodezsYier7DWnq8/edit?usp=sharing"); //missing 'http://' causes an error
            //open the link
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    //method used to store the data from the student database in arrays
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void databaseTask(DataSnapshot dataSnapshot) {
        DataSnapshot dsStudents = dataSnapshot.child("Student_Info");
        dsLists = dataSnapshot.child("List_Archive");

        //create Student objects based on the database and add them to an array
        for (DataSnapshot ds : dsStudents.child("Emails").getChildren()) {
            Student student = new Student(ds.getKey());
            student.setFirstName(dsStudents.child("First_names").child(student.getId()).getValue(String.class));
            student.setLastName(dsStudents.child("Last_names").child(student.getId()).getValue(String.class));

            allStudents.add(student);
        }

        //determine chosen students
        for (int i = 0; i < 10; i++) {
            if (!allBlocksSorted.get(i).isEmpty()) {
                chosenStudentsNames.add(new ArrayList<>());
                for (int j = 0; j < allBlocksSorted.get(i).size(); j++) {
                    chosenStudentsNames.get(i).add(getStudentById(allBlocksSorted.get(i).get(j)).getFullName());
                }
            }
        }

        //initialize the dutyList in the popup to view list, save image, and update the database
        initializePopup();

        //update the list archive database
        updateDB();
    }

    //method to identify a student using their id
    public Student getStudentById(String id) {
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getId().equals(id)) {
                return allStudents.get(i);
            }
        }
        return null;
    }

    //initialize the table on the popup
    public void initializePopup() {
        //define everything
        popup = View.inflate(this, R.layout.popup_finished_list, null);

        //students
        //block 1
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S1));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S2));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S3));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S4));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S5));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S6));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S7));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S8));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S9));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S10));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S11));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S12));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S13));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S14));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S15));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S16));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S17));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S18));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S19));
        txtBlock1.add(popup.findViewById(R.id.popupFinishedListTxtStudentB1S20));
        //block 2
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S1));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S2));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S3));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S4));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S5));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S6));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S7));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S8));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S9));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S10));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S11));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S12));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S13));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S14));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S15));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S16));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S17));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S18));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S19));
        txtBlock2.add(popup.findViewById(R.id.popupFinishedListTxtStudentB2S20));
        //block 3
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S1));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S2));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S3));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S4));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S5));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S6));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S7));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S8));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S9));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S10));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S11));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S12));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S13));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S14));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S15));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S16));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S17));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S18));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S19));
        txtBlock3.add(popup.findViewById(R.id.popupFinishedListTxtStudentB3S20));
        //block 4
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S1));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S2));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S3));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S4));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S5));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S6));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S7));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S8));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S9));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S10));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S11));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S12));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S13));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S14));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S15));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S16));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S17));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S18));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S19));
        txtBlock4.add(popup.findViewById(R.id.popupFinishedListTxtStudentB4S20));
        //block 5
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S1));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S2));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S3));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S4));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S5));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S6));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S7));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S8));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S9));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S10));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S11));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S12));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S13));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S14));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S15));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S16));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S17));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S18));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S19));
        txtBlock5.add(popup.findViewById(R.id.popupFinishedListTxtStudentB5S20));
        //block 6
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S1));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S2));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S3));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S4));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S5));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S6));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S7));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S8));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S9));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S10));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S11));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S12));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S13));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S14));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S15));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S16));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S17));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S18));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S19));
        txtBlock6.add(popup.findViewById(R.id.popupFinishedListTxtStudentB6S20));
        //block 7
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S1));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S2));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S3));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S4));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S5));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S6));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S7));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S8));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S9));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S10));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S11));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S12));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S13));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S14));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S15));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S16));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S17));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S18));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S19));
        txtBlock7.add(popup.findViewById(R.id.popupFinishedListTxtStudentB7S20));
        //block 8
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S1));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S2));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S3));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S4));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S5));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S6));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S7));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S8));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S9));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S10));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S11));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S12));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S13));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S14));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S15));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S16));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S17));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S18));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S19));
        txtBlock8.add(popup.findViewById(R.id.popupFinishedListTxtStudentB8S20));
        //block 9
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S1));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S2));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S3));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S4));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S5));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S6));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S7));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S8));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S9));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S10));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S11));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S12));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S13));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S14));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S15));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S16));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S17));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S18));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S19));
        txtBlock9.add(popup.findViewById(R.id.popupFinishedListTxtStudentB9S20));
        //block 10
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S1));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S2));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S3));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S4));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S5));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S6));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S7));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S8));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S9));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S10));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S11));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S12));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S13));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S14));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S15));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S16));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S17));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S18));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S19));
        txtBlock10.add(popup.findViewById(R.id.popupFinishedListTxtStudentB10S20));
        //all blocks
        txtAllBlocks.add(txtBlock1);
        txtAllBlocks.add(txtBlock2);
        txtAllBlocks.add(txtBlock3);
        txtAllBlocks.add(txtBlock4);
        txtAllBlocks.add(txtBlock5);
        txtAllBlocks.add(txtBlock6);
        txtAllBlocks.add(txtBlock7);
        txtAllBlocks.add(txtBlock8);
        txtAllBlocks.add(txtBlock9);
        txtAllBlocks.add(txtBlock10);
        //times
        txtTimes.add(popup.findViewById(R.id.popupFinishedListTxtTime1));
        txtTimes.add(popup.findViewById(R.id.popupFinishedListTxtTime2));
        txtTimes.add(popup.findViewById(R.id.popupFinishedListTxtTime3));
        txtTimes.add(popup.findViewById(R.id.popupFinishedListTxtTime4));
        txtTimes.add(popup.findViewById(R.id.popupFinishedListTxtTime5));
        txtTimes.add(popup.findViewById(R.id.popupFinishedListTxtTime6));
        txtTimes.add(popup.findViewById(R.id.popupFinishedListTxtTime7));
        txtTimes.add(popup.findViewById(R.id.popupFinishedListTxtTime8));
        txtTimes.add(popup.findViewById(R.id.popupFinishedListTxtTime9));
        txtTimes.add(popup.findViewById(R.id.popupFinishedListTxtTime10));

        //fine nBlocks
        int n = nBlocks;

        //set dutyList title (if the type is 'other'/4)
        TextView txtTitle = popup.findViewById(R.id.popupFinishedListTxtTitle);
        TextView txtTitleExtra = popup.findViewById(R.id.popupFinishedListTxtTitleExtra);
        if (type == 4) { //set title if the type is 'other'
            if (n == 1) { //in case there is only one block - the dutyList has only one column
                txtTitle.setVisibility(View.GONE);
                txtTitleExtra.setText(title);
            }
            else { //in case there is more than one block - the dutyList has two columns
                txtTitle.setText(title);
                txtTitleExtra.setVisibility(View.GONE);
            }
        }
        else {
            //remove the title text view if the type is not 'other'
            txtTitle.setVisibility(View.GONE);
            txtTitleExtra.setVisibility(View.GONE);
        }

        //set dutyList date
        TextView txtDate = popup.findViewById(R.id.popupFinishedListTxtDate);
        TextView txtDateExtra = popup.findViewById(R.id.popupFinishedListTxtDateExtra);
        if (n == 1) { //in case there is only one block - the dutyList has only one column
            txtDate.setVisibility(View.GONE);
            txtDateExtra.setText(date);
        }
        else { //in case there is more than one block - the dutyList has two columns
            txtDate.setText(date);
            txtDateExtra.setVisibility(View.GONE);
        }

        //set visibilities to most buttons and time labels
        for (int i = 10; i > n; i--) {
            for (int j = 0; j < 20; j++) {
                txtAllBlocks.get(i - 1).get(j).setVisibility(View.GONE);
                txtTimes.get(i - 1).setVisibility(View.GONE);
            }
        }
        //set visibilities to student buttons
        for (int i = 0; i < n; i++) {
            for (int j = 20; j > nPeoples[i]; j--) {
                txtAllBlocks.get(i).get(j - 1).setVisibility(View.GONE);
            }
        }

        //set times
        for (int i = 0; i < n; i++) {
            txtTimes.get(i).setText(times[i]);
        }

        //set student names
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < nPeoples[i]; j++) {
                txtAllBlocks.get(i).get(j).setTextColor(getResources().getColor(R.color.background_color));
                txtAllBlocks.get(i).get(j).setText(chosenStudentsNames.get(i).get(j));
            }
        }
    }

    //convert the layout to a bitmap and save in to the device
    public void saveImage() throws IOException {
        //convert the layout to bitmap
        layoutList.setDrawingCacheEnabled(true);
        layoutList.buildDrawingCache();
        layoutList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = layoutList.getDrawingCache();

        //save bitmap to device
        save(bitmap);
    }
    //save the bitmap to device as a jpeg file
    public void save(Bitmap bitmap) throws IOException {
        //define file pathname
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(root + "/Download");

        //find the dutyList type
        String listType = "";
        if (type == 0) {listType = "dining hall";}
        else if (type == 1) {listType = "cow";}
        else if (type == 2) {listType = "chicken";}
        else if (type == 3) {listType = "visitor center";}
        else if (type == 4) {listType = "other";}

        //define file name
        String fileName = "Duty list for " + date + " (" + listType + ").jpg";

        //create file based on pathname and file name
        File myFile = new File(file, fileName);

        //download the bitmap as jpeg
        try {
            //create the file
            FileOutputStream fileOutputStream = new FileOutputStream(myFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

            //save the file
            fileOutputStream.flush();
            fileOutputStream.close();

            //notify the user that the file was saved
            Toast.makeText(this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

            layoutList.setDrawingCacheEnabled(false);
        }
        catch (Exception e) {
            //in case something goes wrong notify the user
            Toast.makeText(this, "Please check the list once more before saving", Toast.LENGTH_SHORT).show();
        }
    }

    //update the database with all changes (numbers done and list object)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateDB() {

        //find the dutyList type
        String listType = "";
        if (type == 0) {listType = "dining hall";}
        else if (type == 1) {listType = "cow";}
        else if (type == 2) {listType = "chicken";}
        else if (type == 3) {listType = "visitor center";}
        else if (type == 4) {listType = "other";}

        //add list to the list archive generating a unique key
        String key = date + " (" + listType + ")";

        //clear the list record in case an existing one is being redone
        FirebaseDatabase.getInstance().getReference("List_Archive").child(key).removeValue();

        FirebaseDatabase.getInstance().getReference("List_Archive").child(key).child("date").setValue(date);
        FirebaseDatabase.getInstance().getReference("List_Archive").child(key).child("title").setValue(title);
        FirebaseDatabase.getInstance().getReference("List_Archive").child(key).child("type").setValue(String.valueOf(type));
        FirebaseDatabase.getInstance().getReference("List_Archive").child(key).child("number_of_students").setValue(String.valueOf(nStudents));
        FirebaseDatabase.getInstance().getReference("List_Archive").child(key).child("number_of_blocks").setValue(String.valueOf(nBlocks));

        //add each student present in the list
        int index = 0;
        for (int i = 0; i < 10; i++) {
            if (!allBlocksSorted.get(i).isEmpty()) {
                String blockKey = i + 1 + " - " + txtTimes.get(i).getText();
                for (int j = 0; j < allBlocksSorted.get(i).size(); j++) {
                    index = index + 1;
                    FirebaseDatabase.getInstance().getReference("List_Archive").child(key).child(blockKey).
                            child(String.valueOf(index)).child("id").setValue(allBlocksSorted.get(i).get(j));
                    if (extraStudents.get(i).get(j) == 1) {FirebaseDatabase.getInstance().getReference("List_Archive").
                            child(key).child(blockKey).child(String.valueOf(index)).child("as_extra").setValue("1");
                    }
                    else {
                        FirebaseDatabase.getInstance().getReference("List_Archive").child(key).child(blockKey).child(String.valueOf(index)).
                                child("as_extra").setValue("0");
                    }
                }
            }
        }
    }
}