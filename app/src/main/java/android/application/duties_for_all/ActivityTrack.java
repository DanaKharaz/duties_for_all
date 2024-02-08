package android.application.duties_for_all;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duties_for_all.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityTrack extends AppCompatActivity implements View.OnClickListener {



    //What is done in this activity:
    //a table showing the students' on-campus availability is displayed
    //the user can mark the students not on campus on certain days (14 consecutive days are displayed at once)
    //the user may mark students as 'checked in' on a certain day
    //the students are shown by grade
    //the database is updated each time a cell is clicked
    //every 5 seconds the whole table is synced with the database



    //define all buttons (here so that they can be used in the onClick method)
    Button btnHelp;
    Button btnGoBack;
    TextView btnDay1;
    TextView txtPredp;
    TextView txtDp1;
    TextView txtDp2;

    //variables for the table
    ArrayList<TextView> txtDays;
    ArrayList<ArrayList<Boolean>> onCampus;
    ArrayList<Boolean> checkIn;
    ArrayList<Student> allStudents;
    ArrayList<Date> days;
    int selectedGrade = 0;

    //loading screen
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        //set button and text listeners
        btnHelp = findViewById(R.id.TrackBtnHelp);
        btnHelp.setOnClickListener(this);
        btnGoBack = findViewById(R.id.TrackBtnGoBack);
        btnGoBack.setOnClickListener(this);
        txtPredp = findViewById(R.id.TrackTxtPredp);
        txtPredp.setOnClickListener(this);
        txtDp1 = findViewById(R.id.TrackTxtDp1);
        txtDp1.setOnClickListener(this);
        txtDp2 = findViewById(R.id.TrackTxtDp2);
        txtDp2.setOnClickListener(this);
        btnDay1 = findViewById(R.id.TrackBtnDay1);
        btnDay1.setOnClickListener(this);

        //set predp as default grade
        SpannableString grade = new SpannableString(getResources().getString(R.string.pre_dp));
        grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
        txtPredp.setText(grade);

        //set the first date as the current date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Date date = new Date(day, month + 1, year);
        btnDay1.setText(date.toString());
        //add all date texts to an array
        txtDays = new ArrayList<>();
        txtDays.add(findViewById(R.id.TrackTxtDay2));
        txtDays.add(findViewById(R.id.TrackTxtDay3));
        txtDays.add(findViewById(R.id.TrackTxtDay4));
        txtDays.add(findViewById(R.id.TrackTxtDay5));
        txtDays.add(findViewById(R.id.TrackTxtDay6));
        txtDays.add(findViewById(R.id.TrackTxtDay7));
        txtDays.add(findViewById(R.id.TrackTxtDay8));
        txtDays.add(findViewById(R.id.TrackTxtDay9));
        txtDays.add(findViewById(R.id.TrackTxtDay10));
        txtDays.add(findViewById(R.id.TrackTxtDay11));
        txtDays.add(findViewById(R.id.TrackTxtDay12));
        txtDays.add(findViewById(R.id.TrackTxtDay13));
        txtDays.add(findViewById(R.id.TrackTxtDay14));
        //set dates to all text views and add to days array
        days = new ArrayList<>();
        days.add(date);
        for (int i = 0; i < 13; i++) {
            txtDays.get(i).setText(date.getNext().toString());
            date = date.getNext();
            days.add(date);
        }

        //initialize arrays
        onCampus = new ArrayList<>();
        checkIn = new ArrayList<>();
        allStudents = new ArrayList<>();

        //show a loading window while the data is collected from the database
        loading = ProgressDialog.show(this,"Loading","Please wait");

        //collect database data and construct the UI based on it
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //what repeats every 5 seconds
                sync();
            }
        };

        timer.schedule(task, 5000, 5000);
    }
    //onCreate ends

    public void sync() {
        //collect data from database of all students and store it in a alphabetically sorted array of Student objects
        FirebaseDatabase.getInstance().getReference("Student_Info").get().addOnCompleteListener(task -> databaseTasks(task.getResult()));
    }

    //when the buttons with onClick listeners are clicked:
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
            Intent intent = new Intent(this, ActivityHome.class);
            startActivity(intent);
        }

        //when the date button is clicked:
        if (view == btnDay1) {
            //initialize date picker
            DatePickerDialog datePickerDialog;
            DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
                //get the chosen date from the date picker
                month = month + 1;
                Date date = new Date(day, month, year);
                btnDay1.setText(date.toString());
                //update text views and days array
                days.clear();
                days.add(date);
                for (int i = 0; i < 13; i++) {
                    txtDays.get(i).setText(date.getNext().toString());
                    date = date.getNext();
                    days.add(date);
                }

                loading.show();

                //set default values to the database if needed
                setDefaults();

                //adjust array values according to the database
                updateArrays();
            };
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
            //open a calender where the user can choose the date
            datePickerDialog.show();
        }

        //when predp, dp1, or dp2 is clicked:
        if (view == txtPredp) {
            loading.show();

            SpannableString gradeTxt = new SpannableString(getResources().getString(R.string.pre_dp));
            gradeTxt.setSpan(new UnderlineSpan(), 0, gradeTxt.length(), 0);
            txtPredp.setText(gradeTxt);
            txtDp1.setText(getResources().getString(R.string.dp1));
            txtDp2.setText(getResources().getString(R.string.dp2));

            selectedGrade = 0;
            updateRecyclerView(0);
        }
        if (view == txtDp1) {
            loading.show();

            SpannableString gradeTxt = new SpannableString(getResources().getString(R.string.dp1));
            gradeTxt.setSpan(new UnderlineSpan(), 0, gradeTxt.length(), 0);
            txtDp1.setText(gradeTxt);
            txtPredp.setText(getResources().getString(R.string.pre_dp));
            txtDp2.setText(getResources().getString(R.string.dp2));

            selectedGrade = 1;
            updateRecyclerView(1);
        }
        if (view == txtDp2) {
            loading.show();

            SpannableString gradeTxt = new SpannableString(getResources().getString(R.string.dp2));
            gradeTxt.setSpan(new UnderlineSpan(), 0, gradeTxt.length(), 0);
            txtDp2.setText(gradeTxt);
            txtDp1.setText(getResources().getString(R.string.dp1));
            txtPredp.setText(getResources().getString(R.string.pre_dp));

            selectedGrade = 2;
            updateRecyclerView(2);
        }
    }

    //method used to store the data from the student database in arrays of Student objects and display anyone chose prior
    public void databaseTasks(DataSnapshot dsStudents) {
        //create Student objects based on the database and add them to an array
        allStudents.clear();
        checkIn.clear();
        onCampus.clear();
        for (DataSnapshot ds : dsStudents.child("Emails").getChildren()) {
            Student student = new Student(ds.getKey());
            student.setFirstName(dsStudents.child("First_names").child(student.getId()).getValue(String.class));
            student.setLastName(dsStudents.child("Last_names").child(student.getId()).getValue(String.class));
            student.setGrade(dsStudents.child("Grades").child(student.getId()).getValue(String.class));

            allStudents.add(student);
        }

        //sort the chosen student array alphabetically by first name (selection sort)
        for (int round = 0; round < allStudents.size() - 1; round++) {
            String last = "A";
            int index = 0;
            for (int i = 0; i < allStudents.size() - round; i++) {
                if (allStudents.get(i).getFirstName().compareTo(last) > 0) {
                    last = allStudents.get(i).getFirstName();
                    index = i;
                }
            }
            Student temp = allStudents.get(allStudents.size() - 1 - round);
            allStudents.set(allStudents.size() - 1 - round, allStudents.get(index));
            allStudents.set(index, temp);
        }

        //initialize check in and on campus arrays
        for (int i = 0; i < 14; i++) {
            onCampus.add(new ArrayList<>());
        }
        for (int j = 0; j < allStudents.size(); j++) {
            for (int i = 0; i < 14; i++) {
                onCampus.get(i).add(true);
            }
            checkIn.add(false);
        }

        //fill check in and on campus databases with default values if needed
        setDefaults();
    }

    //method for the recycler view
    public void updateRecyclerView(int grade) {
        //split student information by grades
        ArrayList<Student> predp = new ArrayList<>();
        ArrayList<ArrayList<Boolean>> onCampusPredp = new ArrayList<>();
        ArrayList<Boolean> checkInPredp = new ArrayList<>();
        ArrayList<Student> dp1 = new ArrayList<>();
        ArrayList<ArrayList<Boolean>> onCampusDp1 = new ArrayList<>();
        ArrayList<Boolean> checkInDp1 = new ArrayList<>();
        ArrayList<Student> dp2 = new ArrayList<>();
        ArrayList<ArrayList<Boolean>> onCampusDp2 = new ArrayList<>();
        ArrayList<Boolean> checkInDp2 = new ArrayList<>();
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getGrade().equals("predp")) {
                predp.add(allStudents.get(i));
                checkInPredp.add(checkIn.get(i));
                for (int j = 0; j < onCampus.size(); j++) {
                    onCampusPredp.add(new ArrayList<>());
                    onCampusPredp.get(j).add(onCampus.get(j).get(i));
                }
            }
        }
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getGrade().equals("dp1")) {
                dp1.add(allStudents.get(i));
                checkInDp1.add(checkIn.get(i));
                for (int j = 0; j < onCampus.size(); j++) {
                    onCampusDp1.add(new ArrayList<>());
                    onCampusDp1.get(j).add(onCampus.get(j).get(i));
                }
            }
        }
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getGrade().equals("dp2")) {
                dp2.add(allStudents.get(i));
                checkInDp2.add(checkIn.get(i));
                for (int j = 0; j < onCampus.size(); j++) {
                    onCampusDp2.add(new ArrayList<>());
                    onCampusDp2.get(j).add(onCampus.get(j).get(i));
                }
            }
        }
        rvAdapterTrack adapterPredp = new rvAdapterTrack(this, predp, onCampusPredp, checkInPredp, days, "predp");
        rvAdapterTrack adapterDp1 = new rvAdapterTrack(this, dp1, onCampusDp1, checkInDp1, days, "dp1");
        rvAdapterTrack adapterDp2 = new rvAdapterTrack(this, dp2, onCampusDp2, checkInDp2, days, "dp2");
        RecyclerView recyclerView = findViewById(R.id.TrackRecyclerViewNames);
        if (grade == 0) {
            recyclerView.setAdapter(adapterPredp);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        if (grade == 1) {
            recyclerView.setAdapter(adapterDp1);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        if (grade == 2) {
            recyclerView.setAdapter(adapterDp2);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        loading.dismiss();
    }

    //method to set default values to the database branches for check in and on campus
    public void setDefaults() {
        FirebaseDatabase.getInstance().getReference().get().addOnCompleteListener(task -> {
            DataSnapshot dsEverything = task.getResult();
            //check in
            DataSnapshot dsCheckIn = dsEverything.child("Check_In");
            Date date = days.get(0);
            for (int i = 0; i < allStudents.size(); i++) {
                if (!dsCheckIn.child(date.toString()).child(allStudents.get(i).getId()).exists()) {
                    FirebaseDatabase.getInstance().getReference("Check_In").child(date.toString()).child(allStudents.get(i).getId()).setValue("0");
                }
            }
            //on campus
            DataSnapshot dsOnCampus = dsEverything.child("On_Campus");
            for (Date chosenDay : days) {
                for (int i = 0; i < allStudents.size(); i++) {
                    if (!dsOnCampus.child(chosenDay.toString()).child(allStudents.get(i).getId()).exists()) {
                        FirebaseDatabase.getInstance().getReference("On_Campus").child(chosenDay.toString()).
                                child(allStudents.get(i).getId()).setValue("1");
                    }
                }
            }

            //adjust array values according to the database
            updateArrays();
        });
    }

    public void updateArrays() {
        //go through db see if anyone was marked checked in or not on campus by someone else
        //go through the array and add its data to db
        FirebaseDatabase.getInstance().getReference().get().addOnCompleteListener(task -> {
            DataSnapshot dsEverything = task.getResult();
            //check in
            DataSnapshot dsCheckIn = dsEverything.child("Check_In");
            Date date = days.get(0);
            for (int i = 0; i < allStudents.size(); i++) {
                if (Objects.equals(dsCheckIn.child(date.toString()).child(allStudents.get(i).getId()).getValue(String.class), "1")) {
                    checkIn.set(i, true);
                }
                else {checkIn.set(i, false);}
            }

            //on campus
            DataSnapshot dsOnCampus = dsEverything.child("On_Campus");
            for (Date chosenDay : days) {
                for (int i = 0; i < allStudents.size(); i++) {
                    if (Objects.equals(dsOnCampus.child(chosenDay.toString()).child(allStudents.get(i).getId()).getValue(String.class), "0")) {
                        onCampus.get(days.indexOf(chosenDay)).set(i, false);
                    }
                    else {onCampus.get(days.indexOf(chosenDay)).set(i, true);}
                }
            }

            if (selectedGrade == 0) {
                //update recycler view with predp
                SpannableString gradeTxt = new SpannableString(getResources().getString(R.string.pre_dp));
                gradeTxt.setSpan(new UnderlineSpan(), 0, gradeTxt.length(), 0);
                txtPredp.setText(gradeTxt);
                txtDp1.setText(getResources().getString(R.string.dp1));
                txtDp2.setText(getResources().getString(R.string.dp2));

                updateRecyclerView(0);
            }
            if (selectedGrade == 1) {
                SpannableString gradeTxt = new SpannableString(getResources().getString(R.string.dp1));
                gradeTxt.setSpan(new UnderlineSpan(), 0, gradeTxt.length(), 0);
                txtDp1.setText(gradeTxt);
                txtPredp.setText(getResources().getString(R.string.pre_dp));
                txtDp2.setText(getResources().getString(R.string.dp2));

                updateRecyclerView(1);
            }
            if (selectedGrade == 2) {
                SpannableString gradeTxt = new SpannableString(getResources().getString(R.string.dp2));
                gradeTxt.setSpan(new UnderlineSpan(), 0, gradeTxt.length(), 0);
                txtDp2.setText(gradeTxt);
                txtDp1.setText(getResources().getString(R.string.dp1));
                txtPredp.setText(getResources().getString(R.string.pre_dp));

                updateRecyclerView(2);
            }
        });
    }
}