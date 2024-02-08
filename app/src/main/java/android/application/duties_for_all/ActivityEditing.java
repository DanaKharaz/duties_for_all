package android.application.duties_for_all;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ActivityEditing extends AppCompatActivity implements View.OnClickListener, rvInterface {



    //What is done in this activity:
    //the user chooses a duty list to edit
    //extra people can be added, people can be switched around, and any student can be added
    //students can be removed, leaving a blank spot in the list that will be removed (not displayed) in the final list
    //whole blocks can be removed as well
    //the students are arranged in the same way as in the sorting activity



    //define change, help, next, and go back buttons (here so that they can be used in the onClick method)
    Button btnChange;
    Button btnGoBack;
    Button btnNext;
    Button btnHelp;
    Button btnClearList;

    //variable used to create the dutyList
    int studentNumber;
    ArrayList<Block> blocks;
    ArrayList<Student> students;
    DutyList dutyList;

    //database references
    DataSnapshot dsStudents;
    DataSnapshot dsListArchive;
    DataSnapshot dsOnCampusFull;

    //create a date picker for the date button
    DatePickerDialog datePickerDialog;
    boolean dateSet = false;

    //define variables used in the popup
    //used to search through the student dutyList
    SearchView popupSearchView;
    PopupWindow popupWindow = new PopupWindow();
    Button currentBtn;
    //used to dynamically display all student names
    rvAdapterStudents adapter;
    //set student list for recycler view(s)
    ArrayList<Student> studentList = new ArrayList<>();
    int tab = 1; //1 - everyone, 2 - chosen, 3 - extra
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

    //variables for the list
    boolean wentBack;
    int nBlocks;
    int nStudents;
    String title;
    String[] times;
    int[] nPeoples;

    //retrieve data from the student database
    ArrayList<Student> allStudents = new ArrayList<>();

    //variables used in the choose pop-up
    int type = -1;
    Date date;

    //loading screen
    ProgressDialog loading;

    //when the activity is started:
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);
        //retrieve data passed on from previous activities
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getInt("type");
            date = new Date(extras.getString("date"));
            nBlocks = extras.getInt("nBlocks");
            title = extras.getString("title");
            times = extras.getStringArray("times");
            nPeoples = extras.getIntArray("nPeoples");
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

        //set onClick listeners to change, help, go back, and next buttons
        btnChange = findViewById(R.id.EditingBtnChangeList);
        btnChange.setOnClickListener(this);
        btnGoBack = findViewById(R.id.EditingBtnGoBack);
        btnGoBack.setOnClickListener(this);
        btnNext = findViewById(R.id.EditingBtnNext);
        btnNext.setOnClickListener(this);
        btnHelp = findViewById(R.id.EditingBtnHelp);
        btnHelp.setOnClickListener(this);
        btnClearList = findViewById(R.id.EditingBtnClearList);
        btnClearList.setOnClickListener(this);

        //set today's day as the initial one
        if (Objects.isNull(date)) {date = new Date();}

        //show a loading window while the data is collected from the database
        loading = ProgressDialog.show(this,"Loading","Please wait");

        //collect data from the database of all students
        FirebaseDatabase.getInstance().getReference().get().addOnCompleteListener(task -> databaseTask(task.getResult()));
    }

    //method used to store the data from the student database in arrays
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void databaseTask(DataSnapshot dataSnapshot) {
        dsStudents = dataSnapshot.child("Student_Info");
        dsListArchive = dataSnapshot.child("List_Archive");
        dsOnCampusFull = dataSnapshot.child("On_Campus");

        //see which list needs to be displayed
        if (!wentBack) {
            loading.dismiss();
            setPopup();
        }

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
            if (dataSnapshot.child("On_Campus").child(date.toString()).exists()) {
                DataSnapshot dsOnCampus = dataSnapshot.child("On_Campus").child(date.toString());
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

        if (wentBack) {
            //determine the list's key in the database
            String listKey = "";
            if (type == 0) {listKey = date.toString() + " (dining hall)";}
            if (type == 1) {listKey = date.toString() + " (cow)";}
            if (type == 2) {listKey = date.toString() + " (chicken)";}
            if (type == 3) {listKey = date.toString() + " (visitor center)";}
            if (type == 4) {listKey = date.toString() + " (other)";}

            if (dsListArchive.child(listKey).exists()) {
                popupWindow.dismiss();

                getListData(dsListArchive.child(listKey));
            }
            else {
                loading.dismiss();

                Toast.makeText(this, "List not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //when the buttons with onClick listeners are clicked:
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        //when the 'clear list' button is clicked:
        if (view == btnClearList) {
            studentList.addAll(students);
            for (int i = 0; i < dutyList.getnBlocks(); i++) {
                for (int j = 0; j < dutyList.getBlocks().get(i).getnPeople(); j++) {
                    btnAllBlocks.get(i).get(j).setText("");
                }
            }
        }

        //when the 'change list' button is clicked:
        if (view == btnChange) {
            setPopup();
        }

        //when the 'go back' button is clicked:
        if (view == btnGoBack) {
            Intent intent = new Intent(this, ActivityViewEdit.class);
            startActivity(intent);
        }

        //when the 'next' button is clicked:
        if (view == btnNext) {
            //check if there are any blank cells in the table (the dutyList is not full)
            boolean blankCells = false;
            for (int i = 0; i < dutyList.getnBlocks(); i++) {
                for (int j = 0; j < dutyList.getBlocks().get(i).getnPeople(); j++) {
                    if (btnAllBlocks.get(i).get(j).getText().equals("")) {
                        blankCells = true;
                    }
                }
            }

            if (!blankCells) {
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

                //go to list created activity
                Intent intent = new Intent(this, ActivityListCreated.class);
                //pass on data to the next activity
                intent.putExtra("from", "editing");
                intent.putExtra("type", type);
                intent.putExtra("date", date.toString());
                intent.putExtra("nBlocks", nBlocks);
                intent.putExtra("title",title);
                intent.putExtra("times", times);
                intent.putExtra("nPeoples", nPeoples);
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
            else {
                //define the pop-up, confirm and close buttons, and TextView
                View popup = View.inflate(this, R.layout.popup_warning, null);
                Button btnConfirm = popup.findViewById(R.id.popupWarningBtn);
                Button btnX = popup.findViewById(R.id.popupWarningBtnX);
                TextView popupTxt = popup.findViewById(R.id.popupWarningTxt);

                //make the TextView scrollable (in case the text does no fully fit into the window)
                popupTxt.setMovementMethod(new ScrollingMovementMethod());

                //set warning text
                popupTxt.setText(R.string.editing_popup_warning);
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

                //reshape the list and move on through confirm button
                btnConfirm.setOnClickListener(v -> {
                    for (int i = 0; i < nBlocks; i++) {
                        for (int j = 0; j < nPeoples[i]; j++) {
                            if (allBlocksSorted.get(i).get(j).equals("")) {
                                extraStudents.get(i).remove(j);
                                allBlocksSorted.get(i).remove(j);
                                nPeoples[i]--;
                            }
                        }
                        ArrayList<String> tempBlock = new ArrayList<>(allBlocksSorted.get(i));
                        tempBlock.removeIf(Objects::isNull);
                        tempBlock.removeIf(String::isEmpty);

                        //fix as extra array
                        ArrayList<Integer> tempExtra = new ArrayList<>();
                        for (int j = 0; j < extraStudents.get(i).size(); j++) {
                            for (String email : tempBlock) {tempExtra.add(extraStudents.get(i).get(allBlocksSorted.get(i).indexOf(email)));}
                        }

                        extraStudents.set(i, tempExtra);
                        allBlocksSorted.set(i, tempBlock);

                        //remove empty blocks if needed
                        if (allBlocksSorted.get(i).size() == 0) {
                            allBlocksSorted.remove(i);
                            allBlocksSorted.add(new ArrayList<>());
                            extraStudents.remove(i);
                            extraStudents.add(new ArrayList<>());
                            nBlocks--;
                            String[] tempTimes = new String[nBlocks];
                            int[] tempPeoples = new int[nBlocks];
                            for (int n = 0; n < nBlocks; n++) {
                                if (n < i) {
                                    tempTimes[n] = times[n];
                                    tempPeoples[n] = nPeoples[n];
                                }
                                else {
                                    tempTimes[n] = times[n + 1];
                                    tempPeoples[n] = nPeoples[n + 1];
                                }
                            }
                            times = tempTimes;
                            nPeoples = tempPeoples;
                        }
                    }

                    //go to list created activity
                    Intent intent = new Intent(this, ActivityListCreated.class);
                    //pass on data to the next activity
                    intent.putExtra("from", "editing");
                    intent.putExtra("type", type);
                    intent.putExtra("date", date.toString());
                    intent.putExtra("nBlocks", nBlocks);
                    intent.putExtra("title",title);
                    intent.putExtra("times", times);
                    intent.putExtra("nPeoples", nPeoples);
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
                });

                //close popup through button
                btnX.setOnClickListener(v -> popupWindow.dismiss());
            }
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

    //create a dutyList object from a record in the database
    //get data from each list in list archive
    @RequiresApi(api = Build.VERSION_CODES.N)
    public DutyList getArchivedListData(DataSnapshot listDS) {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getListData(DataSnapshot listDS) {
        nBlocks = Integer.parseInt(Objects.requireNonNull(listDS.child("number_of_blocks").getValue(String.class)));
        nStudents = Integer.parseInt(Objects.requireNonNull(listDS.child("number_of_students").getValue(String.class)));
        title = listDS.child("title").getValue(String.class);

        //initialize time and nPeople arrays
        times = new String[nBlocks];
        nPeoples = new int[nBlocks];

        //add students to blocks sorted arrays
        int i = 1;
        int blockIndex = 0;
        for (DataSnapshot dsBlock : listDS.getChildren()) {
            if (blockIndex < nBlocks) {
                allBlocksSorted.get(blockIndex).clear();
                extraStudents.get(blockIndex).clear();

                int count = 0;
                while (dsBlock.child(String.valueOf(i)).exists()) {
                    count++;
                    String key = dsBlock.getKey();
                    assert key != null;
                    String[] keyArray = key.split("");
                    times[blockIndex] = keyArray[keyArray.length - 5] + keyArray[keyArray.length - 4] + keyArray[keyArray.length - 3] +
                            keyArray[keyArray.length - 2] + keyArray[keyArray.length - 1];
                    allBlocksSorted.get(blockIndex).add(dsBlock.child(String.valueOf(i)).child("id").getValue(String.class));
                    extraStudents.get(blockIndex).add(Integer.parseInt(Objects.requireNonNull(dsBlock.child(String.valueOf(i)).
                            child("as_extra").getValue(String.class))));
                    if (extraStudents.get(blockIndex).get(count - 1) == 1) {
                        addedExtra.add(allBlocksSorted.get(blockIndex).get(count - 1));
                    }
                    i++;
                }
                nPeoples[blockIndex] = count;
                blockIndex++;
            }
        }

        dutyList = new DutyList(date, nStudents, nBlocks, type, title);

        //create the blocks array
        blocks = new ArrayList<>();
        for (int j = 0; j < nBlocks; j++) {
            studentNumber = studentNumber + nPeoples[j];
            Block block = new Block(times[j], nPeoples[j]);
            blocks.add(block);
        }

        //transfer student data to the DutyList object
        students = new ArrayList<>();
        for (Block block : blocks) {
            //go through each block and create arrays of students
            ArrayList<Student> people = new ArrayList<>(block.getnPeople());
            for (int b = 0; b < block.getnPeople(); b++) {
                people.add(getStudentById(allBlocksSorted.get(blocks.indexOf(block)).get(b)));
                students.add(getStudentById(allBlocksSorted.get(blocks.indexOf(block)).get(b)));
            }
            block.setPeople(people);
        }

        //add blocks and students to dutyList
        dutyList.setBlocks(blocks);
        dutyList.setStudents(students);

        //set the displayed table
        transferDataToTable(dutyList);
    }

    //onClick method for the date button
    public void openDatePicker(View view) {
        //open a calender where the user can choose the date
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setPopup() {
        dateSet = false;

        //define the pop-up, close button, and TextView
        View popup = View.inflate(this, R.layout.popup_choose_list, null);
        Button btnDH = popup.findViewById(R.id.popupChooseListBtnDiningHall);
        Button btnOther = popup.findViewById(R.id.popupChooseListBtnOther);
        Button btnCow = popup.findViewById(R.id.popupChooseListBtnCow);
        Button btnChicken = popup.findViewById(R.id.popupChooseListBtnChicken);
        Button btnVC = popup.findViewById(R.id.popupChooseListBtnVisitorCenter);
        Button btnContinue = popup.findViewById(R.id.popupChooseListBtnContinue);
        Button btnDate = popup.findViewById(R.id.popupChooseListBtnDate);

        //initialize the date picker dialog
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            //get the chosen date from the date picker
            month = month + 1;
            date = new Date(day, month, year);
            btnDate.setText(date.toString());
            dateSet = true;
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);

        //display pop-up
        //makes pop-up height depend on the screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popup, width, height,false); //'focusable:false' doesn't allow to close pop-up by touch out of the pop-up window
        popupWindow.setElevation(200); //adds a shadow to the pop-up
        popupWindow.showAtLocation(btnChange, Gravity.CENTER, 0, 0);

        //set type buttons
        if (type == 0) {
            SpannableString strType = new SpannableString(getResources().getString(R.string.dining_hall));
            strType.setSpan(new UnderlineSpan(), 0, strType.length(), 0);
            btnDH.setText(strType);
            btnOther.setText(getResources().getString(R.string.other));
            btnCow.setText(getResources().getString(R.string.cow));
            btnChicken.setText(getResources().getString(R.string.chicken));
            btnVC.setText(getResources().getString(R.string.visitor_center));
        }
        else if (type == 1) {
            SpannableString strType = new SpannableString(getResources().getString(R.string.cow));
            strType.setSpan(new UnderlineSpan(), 0, strType.length(), 0);
            btnCow.setText(strType);
            btnOther.setText(getResources().getString(R.string.other));
            btnDH.setText(getResources().getString(R.string.dining_hall));
            btnChicken.setText(getResources().getString(R.string.chicken));
            btnVC.setText(getResources().getString(R.string.visitor_center));
        }
        else if (type == 2) {
            SpannableString strType = new SpannableString(getResources().getString(R.string.chicken));
            strType.setSpan(new UnderlineSpan(), 0, strType.length(), 0);
            btnChicken.setText(strType);
            btnOther.setText(getResources().getString(R.string.other));
            btnCow.setText(getResources().getString(R.string.cow));
            btnDH.setText(getResources().getString(R.string.dining_hall));
            btnVC.setText(getResources().getString(R.string.visitor_center));
        }
        else if (type == 3) {
            SpannableString strType = new SpannableString(getResources().getString(R.string.visitor_center));
            strType.setSpan(new UnderlineSpan(), 0, strType.length(), 0);
            btnVC.setText(strType);
            btnOther.setText(getResources().getString(R.string.other));
            btnCow.setText(getResources().getString(R.string.cow));
            btnChicken.setText(getResources().getString(R.string.chicken));
            btnDH.setText(getResources().getString(R.string.dining_hall));
        }
        else if (type == 4) {
            SpannableString strType = new SpannableString(getResources().getString(R.string.other));
            strType.setSpan(new UnderlineSpan(), 0, strType.length(), 0);
            btnOther.setText(strType);
            btnDH.setText(getResources().getString(R.string.dining_hall));
            btnCow.setText(getResources().getString(R.string.cow));
            btnChicken.setText(getResources().getString(R.string.chicken));
            btnVC.setText(getResources().getString(R.string.visitor_center));
        }
        else {
            btnOther.setText(getResources().getString(R.string.other));
            btnCow.setText(getResources().getString(R.string.cow));
            btnChicken.setText(getResources().getString(R.string.chicken));
            btnVC.setText(getResources().getString(R.string.visitor_center));
            btnDH.setText(getResources().getString(R.string.dining_hall));
        }

        //change type buttons
        btnDH.setOnClickListener(v -> {
            type = 0;
            SpannableString strType = new SpannableString(getResources().getString(R.string.dining_hall));
            strType.setSpan(new UnderlineSpan(), 0, strType.length(), 0);
            btnDH.setText(strType);
            btnOther.setText(getResources().getString(R.string.other));
            btnCow.setText(getResources().getString(R.string.cow));
            btnChicken.setText(getResources().getString(R.string.chicken));
            btnVC.setText(getResources().getString(R.string.visitor_center));
        });
        btnCow.setOnClickListener(v -> {
            type = 1;
            SpannableString strType = new SpannableString(getResources().getString(R.string.cow));
            strType.setSpan(new UnderlineSpan(), 0, strType.length(), 0);
            btnCow.setText(strType);
            btnOther.setText(getResources().getString(R.string.other));
            btnDH.setText(getResources().getString(R.string.dining_hall));
            btnChicken.setText(getResources().getString(R.string.chicken));
            btnVC.setText(getResources().getString(R.string.visitor_center));
        });
        btnChicken.setOnClickListener(v -> {
            type = 2;
            SpannableString strType = new SpannableString(getResources().getString(R.string.chicken));
            strType.setSpan(new UnderlineSpan(), 0, strType.length(), 0);
            btnChicken.setText(strType);
            btnOther.setText(getResources().getString(R.string.other));
            btnCow.setText(getResources().getString(R.string.cow));
            btnDH.setText(getResources().getString(R.string.dining_hall));
            btnVC.setText(getResources().getString(R.string.visitor_center));
        });
        btnVC.setOnClickListener(v -> {
            type = 3;
            SpannableString strType = new SpannableString(getResources().getString(R.string.visitor_center));
            strType.setSpan(new UnderlineSpan(), 0, strType.length(), 0);
            btnVC.setText(strType);
            btnOther.setText(getResources().getString(R.string.other));
            btnCow.setText(getResources().getString(R.string.cow));
            btnChicken.setText(getResources().getString(R.string.chicken));
            btnDH.setText(getResources().getString(R.string.dining_hall));
        });
        btnOther.setOnClickListener(v -> {
            type = 4;
            SpannableString strType = new SpannableString(getResources().getString(R.string.other));
            strType.setSpan(new UnderlineSpan(), 0, strType.length(), 0);
            btnOther.setText(strType);
            btnDH.setText(getResources().getString(R.string.dining_hall));
            btnCow.setText(getResources().getString(R.string.cow));
            btnChicken.setText(getResources().getString(R.string.chicken));
            btnVC.setText(getResources().getString(R.string.visitor_center));
        });

        //confirm the chosen list
        btnContinue.setOnClickListener(v -> {
            if (type == -1 || !dateSet) {
                Toast.makeText(this, "Please choose the list type and date", Toast.LENGTH_SHORT).show();
            }
            else {
                //determine the list's key in the database
                String listKey = "";
                if (type == 0) {listKey = date.toString() + " (dining hall)";}
                if (type == 1) {listKey = date.toString() + " (cow)";}
                if (type == 2) {listKey = date.toString() + " (chicken)";}
                if (type == 3) {listKey = date.toString() + " (visitor center)";}
                if (type == 4) {listKey = date.toString() + " (other)";}

                if (dsListArchive.child(listKey).exists()) {
                    popupWindow.dismiss();

                    //find anyone who already has duties on that day
                    for (DataSnapshot dsList : dsListArchive.getChildren()) {
                        if (Objects.equals(dsList.child("date").getValue(String.class), date.toString()) &&
                                Integer.parseInt(Objects.requireNonNull(dsList.child("type").getValue(String.class))) != type) {
                            sameDay.addAll(getArchivedListData(dsList).getStudents());
                        }
                    }

                    getListData(dsListArchive.child(listKey));
                }
                else {
                    Toast.makeText(this, "List not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        TextView txtTitle = findViewById(R.id.EditingTxtTitle);
        TextView txtTitleExtra = findViewById(R.id.EditingTxtTitleExtra);
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
        TextView txtDate = findViewById(R.id.EditingTxtDate);
        TextView txtDateExtra = findViewById(R.id.EditingTxtDateExtra);
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
                //set starting text to buttons - from created dutyList
                if (extraStudents.get(i).get(j) == 1) {
                    btnAllBlocks.get(i).get(j).setTextColor(getResources().getColor(R.color.box_color));
                }
                else {
                    btnAllBlocks.get(i).get(j).setTextColor(getResources().getColor(R.color.background_color));
                }
                //
                if (Objects.isNull(dutyList.getBlocks().get(i).getPeople().get(j))) {
                    Toast.makeText(this, String.valueOf(j), Toast.LENGTH_SHORT).show(); //3
                }
                btnAllBlocks.get(i).get(j).setText(dutyList.getBlocks().get(i).getPeople().get(j).getFullName());

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
                    Button btnChosen = popup.findViewById(R.id.popupChoosePeopleBtnChosen);
                    Button btnExtra = popup.findViewById(R.id.popupChoosePeopleBtnExtra);
                    if (tab == 1) {
                        SpannableString btnTab = new SpannableString("Everyone");
                        btnTab.setSpan(new UnderlineSpan(), 0, btnTab.length(), 0);
                        btnEveryone.setText(btnTab);
                        btnChosen.setText(R.string.popup_choose_people_chosen);
                        btnExtra.setText(R.string.popup_choose_people_extra);
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
                        btnEveryone.setText(R.string.popup_choose_people_everyone);
                        btnChosen.setText(R.string.popup_choose_people_chosen);
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
                    popupWindow = new PopupWindow(popup, 950, 2050, true); //'true' to close pop-up by touch out of pop-up window
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
                        chosenStudent = getStudentByFullName(fullName);
                        //remove button text
                        currentBtn.setText("");
                        //remove student from arrays
                        for (ArrayList<String> block : allBlocksSorted) {
                            if (block.contains(chosenStudent.getId())) {
                                block.set(block.indexOf(chosenStudent.getId()), "");
                            }
                        }
                        //add chosen student back to the dutyList
                        studentList.add(chosenStudent);
                        //close popup window
                        popupWindow.dismiss();
                    });

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

    //method to identify a student by email
    public  Student getStudentById(String id) {
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getId().equals(id)) {
                return allStudents.get(i);
            }
        }
        return null;
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
        btnBlock1.add(findViewById(R.id.EditingBtnB1S1));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S2));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S3));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S4));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S5));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S6));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S7));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S8));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S9));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S10));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S11));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S12));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S13));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S14));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S15));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S16));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S17));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S18));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S19));
        btnBlock1.add(findViewById(R.id.EditingBtnB1S20));
        //block 2
        btnBlock2.add(findViewById(R.id.EditingBtnB2S1));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S2));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S3));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S4));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S5));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S6));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S7));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S8));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S9));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S10));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S11));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S12));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S13));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S14));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S15));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S16));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S17));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S18));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S19));
        btnBlock2.add(findViewById(R.id.EditingBtnB2S20));
        //block 3
        btnBlock3.add(findViewById(R.id.EditingBtnB3S1));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S2));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S3));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S4));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S5));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S6));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S7));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S8));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S9));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S10));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S11));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S12));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S13));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S14));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S15));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S16));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S17));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S18));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S19));
        btnBlock3.add(findViewById(R.id.EditingBtnB3S20));
        //block 4
        btnBlock4.add(findViewById(R.id.EditingBtnB4S1));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S2));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S3));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S4));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S5));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S6));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S7));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S8));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S9));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S10));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S11));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S12));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S13));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S14));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S15));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S16));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S17));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S18));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S19));
        btnBlock4.add(findViewById(R.id.EditingBtnB4S20));
        //block 5
        btnBlock5.add(findViewById(R.id.EditingBtnB5S1));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S2));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S3));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S4));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S5));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S6));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S7));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S8));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S9));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S10));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S11));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S12));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S13));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S14));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S15));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S16));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S17));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S18));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S19));
        btnBlock5.add(findViewById(R.id.EditingBtnB5S20));
        //block 6
        btnBlock6.add(findViewById(R.id.EditingBtnB6S1));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S2));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S3));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S4));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S5));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S6));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S7));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S8));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S9));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S10));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S11));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S12));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S13));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S14));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S15));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S16));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S17));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S18));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S19));
        btnBlock6.add(findViewById(R.id.EditingBtnB6S20));
        //block 7
        btnBlock7.add(findViewById(R.id.EditingBtnB7S1));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S2));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S3));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S4));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S5));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S6));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S7));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S8));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S9));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S10));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S11));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S12));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S13));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S14));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S15));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S16));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S17));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S18));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S19));
        btnBlock7.add(findViewById(R.id.EditingBtnB7S20));
        //block 8
        btnBlock8.add(findViewById(R.id.EditingBtnB8S1));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S2));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S3));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S4));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S5));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S6));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S7));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S8));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S9));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S10));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S11));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S12));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S13));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S14));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S15));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S16));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S17));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S18));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S19));
        btnBlock8.add(findViewById(R.id.EditingBtnB8S20));
        //block 9
        btnBlock9.add(findViewById(R.id.EditingBtnB9S1));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S2));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S3));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S4));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S5));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S6));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S7));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S8));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S9));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S10));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S11));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S12));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S13));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S14));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S15));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S16));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S17));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S18));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S19));
        btnBlock9.add(findViewById(R.id.EditingBtnB9S20));
        //block 10
        btnBlock10.add(findViewById(R.id.EditingBtnB10S1));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S2));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S3));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S4));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S5));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S6));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S7));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S8));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S9));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S10));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S11));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S12));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S13));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S14));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S15));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S16));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S17));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S18));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S19));
        btnBlock10.add(findViewById(R.id.EditingBtnB10S20));
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
        txtTimes.add(findViewById(R.id.EditingTxtTime1));
        txtTimes.add(findViewById(R.id.EditingTxtTime2));
        txtTimes.add(findViewById(R.id.EditingTxtTime3));
        txtTimes.add(findViewById(R.id.EditingTxtTime4));
        txtTimes.add(findViewById(R.id.EditingTxtTime5));
        txtTimes.add(findViewById(R.id.EditingTxtTime6));
        txtTimes.add(findViewById(R.id.EditingTxtTime7));
        txtTimes.add(findViewById(R.id.EditingTxtTime8));
        txtTimes.add(findViewById(R.id.EditingTxtTime9));
        txtTimes.add(findViewById(R.id.EditingTxtTime10));
    }
}