package android.application.duties_for_all;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duties_for_all.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ActivitySorting extends AppCompatActivity implements View.OnClickListener, rvInterface {



    //What is done in this activity:
    //all student data is collected from the database and put into an array of students
    //the array is sorted based on teh number of duties done and how recently students had duties
    //students are selected for the list based on the previously defined list parameters and teh sorted array of everyone
    //a GUI table is initialized and the list is displayed
    //each cell with a student name is a button that opens a pop-up with a searchable list of students allowing to change the cell
    //the pop-up has 2 (or 3) "tabs": students chosen for the list (and not already in the table), all students (to add extra duties),
    //and all students to be added regularly (if there are no enough people to fill the list)
    //the user can rearrange the list (switch people around or clear the whole list and manually arrange everyone)
    //extra people can be added to the list, their names are in a different color
    //the user cannot proceed if there are empty cells in the table



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
    boolean wentBack = false;

    //retrieve data from the student database
    ArrayList<Student> allStudents = new ArrayList<>();

    //define help, next, and go back buttons (here so that they can be used in the onClick method)
    Button btnHelp;
    Button btnGoBack;
    Button btnNext;
    Button btnClearList;

    //variable used to create the dutyList
    int studentNumber;
    ArrayList<Block> blocks;
    ArrayList<Student> students;
    DutyList dutyList;
    boolean enough = true;

    //define variables used in the popup
    //used to search through the student dutyList
    SearchView popupSearchView;
    PopupWindow popupWindow = new PopupWindow();
    Button currentBtn;
    //used to dynamically display all student names
    rvAdapterStudents adapter;
    //set student list for recycler view(s)
    ArrayList<Student> studentList = new ArrayList<>();
    int tab = 2; //1 - everyone, 2 - chosen, 3 - extra
    ArrayList<Student> extra = new ArrayList<>();

    //for people in existing lists on the same day
    ArrayList<Student> sameDay = new ArrayList<>();

    //arrays used to create the table displaying the dutyList
    ArrayList<Button> btnBlock1 = new ArrayList<>();
    ArrayList<Button> btnBlock2 = new ArrayList<>();
    ArrayList<Button> btnBlock3 = new ArrayList<>();
    ArrayList<Button> btnBlock4 = new ArrayList<>();
    ArrayList<Button> btnBlock5 = new ArrayList<>();
    ArrayList<Button> btnBlock6 = new ArrayList<>();
    ArrayList<Button> btnBlock7 = new ArrayList<>();
    ArrayList<Button> btnBlock8 = new ArrayList<>();
    ArrayList<Button> btnBlock9 = new ArrayList<>();
    ArrayList<Button> btnBlock10 = new ArrayList<>();
    ArrayList<ArrayList<Button>> btnAllBlocks = new ArrayList<>();
    ArrayList<TextView> txtTimes = new ArrayList<>();
    //for onItemClick
    Student chosenStudent;

    //arrays used to create the final dutyList (gather information from table (10 arrays of emails))
    ArrayList<String> block1Sorted = new ArrayList<>();
    ArrayList<String> block2Sorted = new ArrayList<>();
    ArrayList<String> block3Sorted = new ArrayList<>();
    ArrayList<String> block4Sorted = new ArrayList<>();
    ArrayList<String> block5Sorted = new ArrayList<>();
    ArrayList<String> block6Sorted = new ArrayList<>();
    ArrayList<String> block7Sorted = new ArrayList<>();
    ArrayList<String> block8Sorted = new ArrayList<>();
    ArrayList<String> block9Sorted = new ArrayList<>();
    ArrayList<String> block10Sorted = new ArrayList<>();
    ArrayList<ArrayList<String>> allBlocksSorted = new ArrayList<>();
    //arrays to record information about extra students in the list
    ArrayList<String> addedExtra = new ArrayList<>();
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

    //loading screen
    ProgressDialog loading;

    //when the activity is started:
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting);
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
            wentBack = extras.getBoolean("went back");

            //in case the user went back, get the sorted arrays of students for the dutyList
            if (wentBack) {
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

        //combine Integer arrays about extra students into a 2D array
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
        //fill array with 0s - as if no extra people are in the list
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                extraStudents.get(i).add(0);
            }
        }

        //set onClick listeners to help, go back, and next buttons
        btnHelp = findViewById(R.id.SortingBtnHelp);
        btnHelp.setOnClickListener(this);
        btnGoBack = findViewById(R.id.SortingBtnGoBack);
        btnGoBack.setOnClickListener(this);
        btnNext = findViewById(R.id.SortingBtnNext);
        btnNext.setOnClickListener(this);
        btnClearList = findViewById(R.id.SortingBtnClearList);
        btnClearList.setOnClickListener(this);

        //show a loading window while the data is collected from the database
        loading = ProgressDialog.show(this,"Loading","Please wait");

        //collect data from the database of all students
        FirebaseDatabase.getInstance().getReference().get().addOnCompleteListener(task -> databaseTask(task.getResult()));
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

        //when the 'gp back' button is clicked:
        if (view == btnGoBack) {
            //go back to the sorting type activity
            Intent intent = new Intent(this, ActivityListInfo.class);
            //pass back data
            intent.putExtra("type", type);

            startActivity(intent);
        }

        //when the 'next' button is clicked:
        if (view == btnNext) {
            //check if there are any blank cells in the table (the dutyList is not full)
            boolean blankCells = false;
            boolean doubles = false;
            for (int i = 0; i < dutyList.getnBlocks(); i++) {
                int blanks = 0;
                ArrayList<String> unique = new ArrayList<>();
                for (int j = 0; j < dutyList.getBlocks().get(i).getnPeople(); j++) {
                    if (btnAllBlocks.get(i).get(j).getText().equals("")) {
                        blanks++;
                        blankCells = true;
                    }
                    else {
                        if (!unique.contains(btnAllBlocks.get(i).get(j).getText())) {
                            unique.add((String) btnAllBlocks.get(i).get(j).getText());
                        }
                    }
                }
                if (unique.size() + blanks != dutyList.getBlocks().get(i).getnPeople()) {
                    doubles = true;
                }
            }

            if (blankCells) {
                Toast.makeText(this, "Some cells appear to be empty", Toast.LENGTH_SHORT).show();
            }
            else if (doubles) {
                Toast.makeText(this, "Some students appear multiple times in the same block", Toast.LENGTH_SHORT).show();
            }
            else {
                //collect data from the table
                //clear existing arrays
                for (int k = 0; k < allBlocksSorted.size(); k++) {
                    allBlocksSorted.get(k).clear();
                }
                //fill block arrays with student emails as ids
                for (int i = 0; i < dutyList.getnBlocks(); i++) {
                    for (int j = 0; j < dutyList.getBlocks().get(i).getnPeople(); j++) {
                        allBlocksSorted.get(i).add(getStudentByFullName((String) btnAllBlocks.get(i).get(j).getText()).getId());
                        if (btnAllBlocks.get(i).get(j).getTextColors().getDefaultColor() == getResources().getColor(R.color.box_color)) {
                            extraStudents.get(i).set(j, 1);
                        }
                        else {
                            extraStudents.get(i).set(j, 0);
                        }
                    }
                }

                //go to dutyList created activity
                Intent intent = new Intent(this, ActivityListCreated.class);
                //pass on data to the next activity
                intent.putExtra("from", "sorting");
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
                intent.putExtra("nonlocals", locals);
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
        }

        //when the 'clear list' button is clicked:
        if (view == btnClearList) {
            studentList.addAll(students);
            for (int i = 0; i < dutyList.getnBlocks(); i++) {
                for (int j = 0; j < dutyList.getBlocks().get(i).getnPeople(); j++) {
                    btnAllBlocks.get(i).get(j).setText("");
                }
            }
        }
    }

    //method used to store the data from the student database in arrays
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void databaseTask(DataSnapshot dataSnapshot) {
        DataSnapshot dsStudents = dataSnapshot.child("Student_Info");
        //create Student objects based on the database and add them to an array
        for (DataSnapshot ds : dsStudents.child("Emails").getChildren()) {
            Student student = new Student(ds.getKey());
            student.setEmail(dsStudents.child("Emails").child(student.getId()).getValue(String.class));
            student.setFirstName(dsStudents.child("First_names").child(student.getId()).getValue(String.class));
            student.setLastName(dsStudents.child("Last_names").child(student.getId()).getValue(String.class));
            student.setGrade(dsStudents.child("Grades").child(student.getId()).getValue(String.class));
            student.setLocal(Integer.parseInt(Objects.requireNonNull(dsStudents.child("Locals").child(student.getId()).getValue(String.class))));
            student.setCow(Integer.parseInt(Objects.requireNonNull(dsStudents.child("Cow_restrictions").child(student.getId()).getValue(String.class))));
            student.setChicken(Integer.parseInt(Objects.requireNonNull(dsStudents.child("Chicken_restrictions").child(student.getId()).getValue(String.class))));
            student.setVisitorCenter(Integer.parseInt(Objects.requireNonNull(dsStudents.child("Visitor_center_restrictions").child(student.getId()).getValue(String.class))));
            student.setNumberDone(Integer.parseInt(Objects.requireNonNull(dsStudents.child("Numbers_done").child(student.getId()).getValue(String.class))));
            student.setLatestDone(new Date(Objects.requireNonNull(dsStudents.child("Latest_done").child(student.getId()).getValue(String.class))));
            //set on campus
            if (dataSnapshot.child("On_Campus").child(date).exists()) {
                DataSnapshot dsOnCampus = dataSnapshot.child("On_Campus").child(date);
                for (DataSnapshot dsStudent : dsOnCampus.getChildren()) {
                    if (Objects.equals(dsStudent.getKey(), student.getId())) {
                        student.setOnCampus(Integer.parseInt(Objects.requireNonNull(dsStudent.getValue(String.class))));
                    }
                }
            }
            else {student.setOnCampus(1);}

            allStudents.add(student);
        }

        //sort the extra student array alphabetically by first name (selection sort)
        extra.addAll(allStudents);
        for (int round = 0; round < extra.size() - 1; round++) {
            String last = "A";
            int index = 0;
            for (int i = 0; i < extra.size() - round; i++) {
                if (extra.get(i).getFirstName().compareTo(last) > 0) {
                    last = extra.get(i).getFirstName();
                    index = i;
                }
            }
            Student temp = extra.get(extra.size() - 1 - round);
            extra.set(extra.size() - 1 - round, extra.get(index));
            extra.set(index, temp);
        }

        //find anyone who already has duties on that day
        DataSnapshot dsArchive = dataSnapshot.child("List_Archive");
        for (DataSnapshot dsList : dsArchive.getChildren()) {
            if (Objects.equals(dsList.child("date").getValue(String.class), date) &&
                    Integer.parseInt(Objects.requireNonNull(dsList.child("type").getValue(String.class))) != type) {
                sameDay.addAll(getListData(dsList).getStudents());
            }
        }

        //create the list using the student data collected from the database
        createList();
    }

    //choose people for the list
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createList() {
        //find number of students in the list and create array of Block objects
        studentNumber = 0;
        blocks = new ArrayList<>();
        for (int i = 0; i < nBlocks; i++) {
            studentNumber = studentNumber + nPeoples[i];
            Block block = new Block(times[i], nPeoples[i], predps[i], dp1s[i], dp2s[i], locals[i], nonLocals[i]);
            blocks.add(block);
        }

        //create the DutyList object
        dutyList = new DutyList(new Date(date), studentNumber, nBlocks, type, title);

        //sort allStudents by number done and latest done (selection sort)
        for (int round = 0; round < allStudents.size() - 1; round++) {
            int max = 0;
            int index = 0;
            for (int i = 0; i < allStudents.size() - round; i++) {
                if (allStudents.get(i).getNumberDone() > max || (allStudents.get(i).getNumberDone() == max &&
                        allStudents.get(index).getLatestDone().before(allStudents.get(i).getLatestDone()))) {
                    max = allStudents.get(i).getNumberDone();
                    index = i;
                }
            }
            Student temp = allStudents.get(allStudents.size() - 1 - round);
            allStudents.set(allStudents.size() - 1 - round, allStudents.get(index));
            allStudents.set(index, temp);
        }

        //choose people for the blocks and create students array for the DutyList
        students = new ArrayList<>();
        for (Block block : blocks) {
            //go through each block and create arrays of students
            ArrayList<Student> people = new ArrayList<>(block.getnPeople());
            for (int j = 0; j < block.getnPeople(); j++) {
                //add to people in Block
                //if the dutyList is already sorted by user (the user went back from dutyList created)
                if (wentBack) {
                    people.add(getStudentById(allBlocksSorted.get(blocks.indexOf(block)).get(j)));
                    students.add(getStudentById(allBlocksSorted.get(blocks.indexOf(block)).get(j)));
                    continue;
                }
                //if the dutyList is not yet determined/sorted
                for (Student student : allStudents) {
                    //go through all students in the database and add all that fit the selected criteria to the block
                    //check if the student fits overall list criteria (type restrictions, on campus availability, no duties on the same day)
                    if (((dutyList.getType() == 1 && student.allowCow()) ||
                            (dutyList.getType() == 2 && student.allowChicken()) ||
                            (dutyList.getType() == 3 && student.allowVisitorCenter()) ||
                            dutyList.getType() == 0 || dutyList.getType() == 4) &&
                            student.isOnCampus()) {
                        if (((student.getGrade().equals("predp") && block.isPredp()) ||
                                (student.getGrade().equals("dp1") && block.isDp1()) ||
                                (student.getGrade().equals("dp2") && block.isDp2())) &&
                                ((student.isLocal() && block.isLocals()) ||
                                (!student.isLocal() && block.isNonLocals())) &&
                                !students.contains(student) &&
                                !sameDay.contains(student)) {
                            people.add(student);
                            students.add(student);
                            break;
                        }
                    }
                }
            }
            block.setPeople(people);
        }

        //check if there are enough people (no - let the user know what to do)
        if (students.size() < dutyList.getnStudents()) { //not enough people
            loading.dismiss();

            //define the pop-up, re-do button, and TextView
            View popup = View.inflate(this, R.layout.popup_warning, null);
            Button btnReDo = popup.findViewById(R.id.popupWarningBtn);
            Button btnProceed = popup.findViewById(R.id.popupWarningBtnProceed);
            Button btnX = popup.findViewById(R.id.popupWarningBtnX);
            TextView popupTxt = popup.findViewById(R.id.popupWarningTxt);

            //make the TextView scrollable (in case the text does no fully fit into the window)
            popupTxt.setMovementMethod(new ScrollingMovementMethod());

            //set warning text
            popupTxt.setText(R.string.not_enough_people_warning);
            popupTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            popupTxt.setTextColor(this.getResources().getColor(R.color.text_color));

            //set button text
            btnReDo.setText(R.string.btn_redo);

            //display pop-up
            //makes pop-up height and width depend on the screen size and content
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            int width = displayMetrics.widthPixels - 150;
            final PopupWindow popupWindow = new PopupWindow(popup, width, height, false); //'focusable:false' forbids to close pop-up by touch out of the pop-up window
            popupWindow.setElevation(200); //adds a shadow to the pop-up
            popupWindow.showAtLocation(btnNext, Gravity.CENTER, 0, 0);

            //restart process through re-do button
            btnReDo.setOnClickListener(v -> {
                //go to create list
                Intent intent = new Intent(this, ActivityCreateList.class);
                startActivity(intent);
            });

            //proceed button
            btnProceed.setVisibility(View.VISIBLE);
            btnProceed.setOnClickListener(v -> {
                enough = false;
                popupWindow.dismiss();
            });

            //close popup through button
            btnX.setOnClickListener(v -> {
                loading.show();
                popupWindow.dismiss();
            });
        }
        //add blocks and students to dutyList
        dutyList.setBlocks(blocks);
        dutyList.setStudents(students);

        //set the displayed table
        transferDataToTable(dutyList);
    }

    //hide unnecessary cells in the table, set date and times, and assigned students
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void transferDataToTable(DutyList dutyList) {
        //initialize arrays
        initializeBtnArrays();
        initializeTxtArray();

        //fine nBlocks
        int n = dutyList.getnBlocks();

        //set dutyList title (if the type is 'other'/4)
        TextView txtTitle = findViewById(R.id.SortingTxtTitle);
        TextView txtTitleExtra = findViewById(R.id.SortingTxtTitleExtra);
        if (type == 4) { //set title if the type is 'other'
            if (n == 1) { //in case there is only one block - the dutyList has only one column
                txtTitleExtra.setVisibility(View.VISIBLE);
                txtTitleExtra.setText(dutyList.getTitle());
            }
            else { //in case there is more than one block - the dutyList has two columns
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText(dutyList.getTitle());
            }
        }

        //set dutyList date
        TextView txtDate = findViewById(R.id.SortingTxtDate);
        TextView txtDateExtra = findViewById(R.id.SortingTxtDateExtra);
        if (n == 1) { //in case there is only one block - the dutyList has only one column
            txtDateExtra.setVisibility(View.VISIBLE);
            txtDateExtra.setText(dutyList.getDate().toString());
        }
        else { //in case there is more than one block - the dutyList has two columns
            txtDate.setVisibility(View.VISIBLE);
            txtDate.setText(dutyList.getDate().toString());
        }

        //set visibilities to student buttons and time labels (and time values)
        for (int i = 0; i < n; i++) {
            txtTimes.get(i).setVisibility(View.VISIBLE);
            txtTimes.get(i).setText(dutyList.getBlocks().get(i).getTime());

            for (int j = 0; j < dutyList.getBlocks().get(i).getnPeople(); j++) {
                btnAllBlocks.get(i).get(j).setVisibility(View.VISIBLE);
            }
        }

        //set students, popups, and button texts
        //set all student onClick listeners
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < dutyList.getBlocks().get(i).getnPeople(); j++) {
                if (wentBack) {
                    if (extraStudents.get(i).get(j) == 1) {
                        btnAllBlocks.get(i).get(j).setTextColor(getResources().getColor(R.color.box_color));
                    }
                    else {
                        btnAllBlocks.get(i).get(j).setTextColor(getResources().getColor(R.color.background_color));
                    }
                }
                else {
                    btnAllBlocks.get(i).get(j).setTextColor(getResources().getColor(R.color.background_color));
                }

                if (dutyList.getBlocks().get(i).getPeople().size() <= j) {btnAllBlocks.get(i).get(j).setText("");}
                else {btnAllBlocks.get(i).get(j).setText(dutyList.getBlocks().get(i).getPeople().get(j).getFullName());}

                //set time
                String time = dutyList.getBlocks().get(i).getTime();

                //add popup to choose a student
                btnAllBlocks.get(i).get(j).setOnClickListener(v -> {
                    //nullify chosen student
                    chosenStudent = null;

                    //identify button
                    currentBtn = findViewById(v.getId());

                    //define pop-up and views
                    View popup = View.inflate(this, R.layout.popup_choose_people, null);
                    Button btnX = popup.findViewById(R.id.popupChoosePeopleBtnX);
                    TextView lbl = popup.findViewById(R.id.popupChoosePeopleLbl);
                    popupSearchView = popup.findViewById(R.id.popupChoosePeopleSearchView);
                    Button btnRemoveStudent = popup.findViewById(R.id.popupChoosePeopleBtnRemove);
                    btnRemoveStudent.setVisibility(View.VISIBLE);
                    Button btnEveryone = popup.findViewById(R.id.popupChoosePeopleBtnEveryone);
                    if (enough) {btnEveryone.setVisibility(View.GONE);}
                    Button btnChosen = popup.findViewById(R.id.popupChoosePeopleBtnChosen);
                    Button btnExtra = popup.findViewById(R.id.popupChoosePeopleBtnExtra);
                    if (tab == 1) {
                        SpannableString btnTab = new SpannableString("Everyone");
                        btnTab.setSpan(new UnderlineSpan(), 0, btnTab.length(), 0);
                        btnEveryone.setText(btnTab);
                        btnExtra.setText(R.string.popup_choose_people_extra);
                        btnChosen.setText(R.string.popup_choose_people_chosen);
                    }
                    if (tab == 2) {
                        SpannableString btnTab = new SpannableString("Chosen");
                        btnTab.setSpan(new UnderlineSpan(), 0, btnTab.length(), 0);
                        btnChosen.setText(btnTab);
                        btnExtra.setText(R.string.popup_choose_people_extra);
                        btnEveryone.setText(R.string.popup_choose_people_everyone);
                    }
                    if (tab == 3) {
                        SpannableString btnTab = new SpannableString("Extra");
                        btnTab.setSpan(new UnderlineSpan(), 0, btnTab.length(), 0);
                        btnExtra.setText(btnTab);
                        btnChosen.setText(R.string.popup_choose_people_chosen);
                        btnEveryone.setText(R.string.popup_choose_people_everyone);
                    }
                    btnEveryone.setOnClickListener(v1 -> {
                        tab = 1;
                        SpannableString btnTab = new SpannableString("Everyone");
                        btnTab.setSpan(new UnderlineSpan(), 0, btnTab.length(), 0);
                        btnEveryone.setText(btnTab);
                        btnExtra.setText(R.string.popup_choose_people_extra);
                        btnChosen.setText(R.string.popup_choose_people_chosen);

                        //recycler view
                        RecyclerView recyclerView = popup.findViewById(R.id.popupChoosePeopleRecyclerView);
                        //studentList array has to be set up before adapter
                        adapter = new rvAdapterStudents(this, allStudents, this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    });
                    btnChosen.setOnClickListener(v1 -> {
                        tab = 2;
                        SpannableString btnTab = new SpannableString("Chosen");
                        btnTab.setSpan(new UnderlineSpan(), 0, btnTab.length(), 0);
                        btnChosen.setText(btnTab);
                        btnExtra.setText(R.string.popup_choose_people_extra);
                        btnEveryone.setText(R.string.popup_choose_people_everyone);

                        //recycler view
                        RecyclerView recyclerView = popup.findViewById(R.id.popupChoosePeopleRecyclerView);
                        //studentList array has to be set up before adapter
                        adapter = new rvAdapterStudents(this, studentList, this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    });
                    btnExtra.setOnClickListener(v1 -> {
                        tab = 3;
                        SpannableString btnTab = new SpannableString("Extra");
                        btnTab.setSpan(new UnderlineSpan(), 0, btnTab.length(), 0);
                        btnExtra.setText(btnTab);
                        btnChosen.setText(R.string.popup_choose_people_chosen);
                        btnEveryone.setText(R.string.popup_choose_people_everyone);

                        //recycler view
                        RecyclerView recyclerView = popup.findViewById(R.id.popupChoosePeopleRecyclerView);
                        //studentList array has to be set up before adapter
                        adapter = new rvAdapterStudents(this, extra, this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    });

                    //search view
                    popupSearchView.clearFocus();
                    popupSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {return false;}
                        @Override
                        public boolean onQueryTextChange(String newText) {
                            if (tab == 1) {filterList(newText, allStudents);}
                            if (tab == 2) {filterList(newText, studentList);}
                            if (tab == 3) {filterList(newText, extra);}
                            return false;
                        }
                    });

                    //set text view
                    lbl.setText(time);

                    //display pop-up
                    //makes pop-up height and width depend on the screen size
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels - 150;
                    int width = displayMetrics.widthPixels - 150;
                    popupWindow = new PopupWindow(popup, width, height, true); //'true' to close pop-up by touch out of pop-up window
                    popupWindow.setElevation(200); //adds shadow to pop-up
                    popupWindow.showAtLocation(v, Gravity.TOP, 0, 170);

                    //close pop-up through button and reset button text
                    btnX.setOnClickListener(view -> {
                        //close popup window
                        popupWindow.dismiss();
                    });

                    //remove student
                    btnRemoveStudent.setOnClickListener(view -> {
                        //find what student is in that place
                        String fullName = (String) currentBtn.getText();

                        if (fullName.equals("")) {
                            Toast.makeText(this, "No student was chosen to remove", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            chosenStudent = getStudentByFullName(fullName);
                            //remove button text
                            currentBtn.setText("");
                            //add chosen student back to the dutyList
                            studentList.add(chosenStudent);
                            //close popup window
                            popupWindow.dismiss();
                        }
                    });

                    //notify the user to remove students to put them somewhere else
                    if (studentList.size() == 0 && tab == 2) {
                        Toast.makeText(this, "Please remove a student first if you would like to switch them", Toast.LENGTH_SHORT).show();
                    }

                    if (tab == 1) {
                        //recycler view
                        RecyclerView recyclerView = popup.findViewById(R.id.popupChoosePeopleRecyclerView);
                        //studentList array has to be set up before adapter
                        adapter = new rvAdapterStudents(this, allStudents, this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    }
                    if (tab == 2) {
                        //recycler view
                        RecyclerView recyclerView = popup.findViewById(R.id.popupChoosePeopleRecyclerView);
                        //studentList array has to be set up before adapter
                        adapter = new rvAdapterStudents(this, studentList, this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    }
                    if (tab == 3) {
                        //recycler view
                        RecyclerView recyclerView = popup.findViewById(R.id.popupChoosePeopleRecyclerView);
                        //studentList array has to be set up before adapter
                        adapter = new rvAdapterStudents(this, extra, this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    }
                });
            }
        }

        loading.dismiss();
    }

    //method to identify a student using their full name
    public Student getStudentByFullName(String name) {
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getFullName().equals(name)) {
                return allStudents.get(i);
            }
        }
        return null;
    }

    //method to identify a student by id
    public  Student getStudentById(String id) {
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getId().equals(id)) {
                return allStudents.get(i);
            }
        }
        return null;
    }

    //create a dutyList object from a record in the database
    //get data from each list in list archive
    @RequiresApi(api = Build.VERSION_CODES.N)
    public DutyList getListData(DataSnapshot listDS) {
        int nBlocks = Integer.parseInt(Objects.requireNonNull(listDS.child("number_of_blocks").getValue(String.class)));
        int nStudents = Integer.parseInt(Objects.requireNonNull(listDS.child("number_of_students").getValue(String.class)));
        int type = Integer.parseInt(Objects.requireNonNull(listDS.child("type").getValue(String.class)));
        Date date = new Date(Objects.requireNonNull(listDS.child("date").getValue(String.class)));

        DutyList dutyList = new DutyList(date, nStudents, type);

        //add students to the list
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<Student> extraStudents = new ArrayList<>();
        int i = 1;
        int blockIndex = 0;
        for (DataSnapshot dsBlock : listDS.getChildren()) {
            if (blockIndex < nBlocks) {
                while (dsBlock.child(String.valueOf(i)).exists()) {
                    students.add(getStudentById(dsBlock.child(String.valueOf(i)).child("id").getValue(String.class)));
                    if (Integer.parseInt(Objects.requireNonNull(dsBlock.child(String.valueOf(i)).
                            child("as_extra").getValue(String.class))) == 1) {
                        extraStudents.add(getStudentById(dsBlock.child(String.valueOf(i)).child("id").getValue(String.class)));
                    }
                    i++;
                }
                blockIndex++;
            }
        }

        //add students and extra students to dutyList
        dutyList.setStudents(students);
        dutyList.setExtraStudents(extraStudents);

        return dutyList;
    }

    //method for clicking on recycler view items
    @Override
    public void onItemClick(Student student) {
        chosenStudent = student;
        if (sameDay.contains(chosenStudent)) {
            //define the pop-up, confirm and close buttons, and TextView
            View popup = View.inflate(this, R.layout.popup_warning, null);
            Button btnConfirm = popup.findViewById(R.id.popupWarningBtn);
            Button btnX = popup.findViewById(R.id.popupWarningBtnX);
            TextView popupTxt = popup.findViewById(R.id.popupWarningTxt);

            //make the TextView scrollable (in case the text does no fully fit into the window)
            popupTxt.setMovementMethod(new ScrollingMovementMethod());

            //set warning text
            popupTxt.setText(R.string.sorting_popup_warning);
            popupTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            popupTxt.setTextColor(this.getResources().getColor(R.color.text_color));

            //display pop-up
            //makes pop-up height and width depend on the screen size and content
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            int width = displayMetrics.widthPixels - 150;
            final PopupWindow popupWindowWarn = new PopupWindow(popup, width, height,true); //'focusable:true' allows to close pop-up by touch out of the pop-up window
            popupWindowWarn.setElevation(200); //adds a shadow to the pop-up
            popupWindowWarn.showAtLocation(btnClearList, Gravity.CENTER, 0, 0);

            //move on through confirm button
            btnConfirm.setOnClickListener(v -> {
                //in case the spot is currently empty
                if (currentBtn.getText().equals("")) {
                    //remove the student from the button they're on (if applicable)
                    for (int g = 0; g < dutyList.getnBlocks(); g++) {
                        for (int h = 0; h < dutyList.getBlocks().get(g).getnPeople(); h++) {
                            if (btnAllBlocks.get(g).get(h).getText().equals(chosenStudent.getFullName()) && tab != 1) {
                                btnAllBlocks.get(g).get(h).setText("");
                            }
                        }
                    }
                    //set button text color
                    if (tab == 3) {
                        currentBtn.setTextColor(getResources().getColor(R.color.box_color));
                    } else {
                        currentBtn.setTextColor(getResources().getColor(R.color.background_color));
                    }
                }
                //in case there is another student in the spot
                else {
                    //find current student in the spot
                    Student currentStudent = getStudentByFullName((String) currentBtn.getText());
                    //remove the current student from the extra array if they are in it
                    if (currentBtn.getTextColors().getDefaultColor() == getResources().getColor(R.color.box_color)) {
                        addedExtra.remove(currentStudent.getId());
                    }
                    //add current student back to the dutyList if they are not an extra student
                    if (currentBtn.getTextColors().getDefaultColor() == getResources().getColor(R.color.background_color)) {
                        studentList.add(currentStudent);
                    }
                    //remove the current student from the extra array if they are in it
                    addedExtra.remove(currentStudent.getId());
                    //set button text color
                    if (tab == 3) {
                        currentBtn.setTextColor(getResources().getColor(R.color.box_color));
                        addedExtra.add(chosenStudent.getId()); //add the student to the extra list if applicable
                    } else {
                        currentBtn.setTextColor(getResources().getColor(R.color.background_color));
                    }
                    //remove the student from the button they're on (if applicable)
                    for (int g = 0; g < dutyList.getnBlocks(); g++) {
                        for (int h = 0; h < dutyList.getBlocks().get(g).getnPeople(); h++) {
                            if (btnAllBlocks.get(g).get(h).getText().equals(chosenStudent.getFullName()) && !addedExtra.contains(chosenStudent.getId())) {
                                btnAllBlocks.get(g).get(h).setText("");
                            }
                        }
                    }
                }
                //set button text and remove the chosen student from the student list (is applicable)
                currentBtn.setText(chosenStudent.getFullName());
                studentList.remove(chosenStudent);
                popupWindowWarn.dismiss();
                popupWindow.dismiss();
            });

            //close popup through button
            btnX.setOnClickListener(v -> popupWindowWarn.dismiss());
        }
        else {
            //in case the spot is currently empty
            if (currentBtn.getText().equals("")) {
                //remove the student from the button they're on (if applicable)
                if (tab == 2) {
                    for (int g = 0; g < dutyList.getnBlocks(); g++) {
                        for (int h = 0; h < dutyList.getBlocks().get(g).getnPeople(); h++) {
                            if (btnAllBlocks.get(g).get(h).getText().equals(chosenStudent.getFullName())) {
                                btnAllBlocks.get(g).get(h).setText("");
                            }
                        }
                    }
                }
                //set button text color
                if (tab == 3) {currentBtn.setTextColor(getResources().getColor(R.color.box_color));}
                else {currentBtn.setTextColor(getResources().getColor(R.color.background_color));}
            }
            //in case there is another student in the spot
            else {
                //find current student in the spot
                Student currentStudent = getStudentByFullName((String) currentBtn.getText());
                int a = 0;
                for (int g = 0; g < dutyList.getnBlocks(); g++) {
                    for (int h = 0; h < dutyList.getBlocks().get(g).getnPeople(); h++) {
                        if (btnAllBlocks.get(g).get(h).getText().equals(currentStudent.getFullName())) {a++;}
                    }
                }
                //remove the current student from the extra array if they are in it
                if (a == 1 && currentBtn.getTextColors().getDefaultColor() == getResources().getColor(R.color.box_color)) {
                    addedExtra.remove(currentStudent.getId());
                }
                //add current student back to the dutyList if they are not an extra student or are in the list more than once
                if (a == 1 && currentBtn.getTextColors().getDefaultColor() == getResources().getColor(R.color.background_color)) {
                    studentList.add(currentStudent);
                }
                //set button text color
                if (tab == 3) {
                    currentBtn.setTextColor(getResources().getColor(R.color.box_color));
                    addedExtra.add(chosenStudent.getId()); //add the student to the extra list if applicable
                } else {
                    currentBtn.setTextColor(getResources().getColor(R.color.background_color));
                }
                //remove the student from the button they're on (if applicable)
                if (tab == 2) {
                    for (int g = 0; g < dutyList.getnBlocks(); g++) {
                        for (int h = 0; h < dutyList.getBlocks().get(g).getnPeople(); h++) {
                            if (btnAllBlocks.get(g).get(h).getText().equals(chosenStudent.getFullName()) && !addedExtra.contains(chosenStudent.getId())) {
                                btnAllBlocks.get(g).get(h).setText("");
                            }
                        }
                    }
                }
            }
            //set button text and remove the chosen student from the student list (is applicable)
            currentBtn.setText(chosenStudent.getFullName());
            studentList.remove(chosenStudent);
            popupWindow.dismiss();
        }
    }

    @Override
    public void onItemClick(DutyList list) {}

    //filter method for the search view
    public void filterList(String text, ArrayList<Student> studentList) {
        //filter chosen array based on user input (compares to name in dutyList)
        ArrayList<Student> filteredList = new ArrayList<>();
        assert studentList != null;
        for (Student student : studentList) {
            if (student.getFullName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                    student.getFirstName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                    student.getLastName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                filteredList.add(student);
            }
        }

        //replace the list with the filtered list
        adapter.setFilteredList(filteredList);
    }

    //set all button arrays
    public void initializeBtnArrays() {
        //add all table buttons to dedicated arrays
        //block 1
        btnBlock1.add(findViewById(R.id.SortingBtnB1S1));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S2));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S3));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S4));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S5));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S6));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S7));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S8));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S9));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S10));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S11));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S12));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S13));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S14));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S15));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S16));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S17));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S18));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S19));
        btnBlock1.add(findViewById(R.id.SortingBtnB1S20));
        //block 2
        btnBlock2.add(findViewById(R.id.SortingBtnB2S1));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S2));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S3));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S4));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S5));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S6));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S7));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S8));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S9));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S10));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S11));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S12));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S13));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S14));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S15));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S16));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S17));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S18));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S19));
        btnBlock2.add(findViewById(R.id.SortingBtnB2S20));
        //block 3
        btnBlock3.add(findViewById(R.id.SortingBtnB3S1));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S2));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S3));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S4));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S5));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S6));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S7));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S8));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S9));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S10));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S11));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S12));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S13));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S14));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S15));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S16));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S17));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S18));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S19));
        btnBlock3.add(findViewById(R.id.SortingBtnB3S20));
        //block 4
        btnBlock4.add(findViewById(R.id.SortingBtnB4S1));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S2));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S3));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S4));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S5));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S6));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S7));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S8));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S9));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S10));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S11));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S12));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S13));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S14));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S15));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S16));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S17));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S18));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S19));
        btnBlock4.add(findViewById(R.id.SortingBtnB4S20));
        //block 5
        btnBlock5.add(findViewById(R.id.SortingBtnB5S1));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S2));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S3));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S4));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S5));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S6));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S7));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S8));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S9));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S10));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S11));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S12));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S13));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S14));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S15));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S16));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S17));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S18));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S19));
        btnBlock5.add(findViewById(R.id.SortingBtnB5S20));
        //block 6
        btnBlock6.add(findViewById(R.id.SortingBtnB6S1));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S2));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S3));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S4));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S5));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S6));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S7));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S8));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S9));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S10));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S11));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S12));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S13));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S14));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S15));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S16));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S17));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S18));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S19));
        btnBlock6.add(findViewById(R.id.SortingBtnB6S20));
        //block 7
        btnBlock7.add(findViewById(R.id.SortingBtnB7S1));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S2));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S3));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S4));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S5));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S6));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S7));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S8));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S9));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S10));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S11));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S12));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S13));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S14));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S15));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S16));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S17));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S18));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S19));
        btnBlock7.add(findViewById(R.id.SortingBtnB7S20));
        //block 8
        btnBlock8.add(findViewById(R.id.SortingBtnB8S1));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S2));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S3));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S4));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S5));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S6));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S7));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S8));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S9));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S10));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S11));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S12));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S13));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S14));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S15));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S16));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S17));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S18));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S19));
        btnBlock8.add(findViewById(R.id.SortingBtnB8S20));
        //block 9
        btnBlock9.add(findViewById(R.id.SortingBtnB9S1));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S2));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S3));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S4));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S5));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S6));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S7));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S8));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S9));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S10));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S11));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S12));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S13));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S14));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S15));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S16));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S17));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S18));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S19));
        btnBlock9.add(findViewById(R.id.SortingBtnB9S20));
        //block 10
        btnBlock10.add(findViewById(R.id.SortingBtnB10S1));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S2));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S3));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S4));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S5));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S6));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S7));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S8));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S9));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S10));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S11));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S12));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S13));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S14));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S15));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S16));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S17));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S18));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S19));
        btnBlock10.add(findViewById(R.id.SortingBtnB10S20));
        //all blocks
        btnAllBlocks.add(btnBlock1);
        btnAllBlocks.add(btnBlock2);
        btnAllBlocks.add(btnBlock3);
        btnAllBlocks.add(btnBlock4);
        btnAllBlocks.add(btnBlock5);
        btnAllBlocks.add(btnBlock6);
        btnAllBlocks.add(btnBlock7);
        btnAllBlocks.add(btnBlock8);
        btnAllBlocks.add(btnBlock9);
        btnAllBlocks.add(btnBlock10);
    }

    //set the text view array
    public void initializeTxtArray() {
        //add all time texts to the array
        txtTimes.add(findViewById(R.id.SortingTxtTime1));
        txtTimes.add(findViewById(R.id.SortingTxtTime2));
        txtTimes.add(findViewById(R.id.SortingTxtTime3));
        txtTimes.add(findViewById(R.id.SortingTxtTime4));
        txtTimes.add(findViewById(R.id.SortingTxtTime5));
        txtTimes.add(findViewById(R.id.SortingTxtTime6));
        txtTimes.add(findViewById(R.id.SortingTxtTime7));
        txtTimes.add(findViewById(R.id.SortingTxtTime8));
        txtTimes.add(findViewById(R.id.SortingTxtTime9));
        txtTimes.add(findViewById(R.id.SortingTxtTime10));
    }
}