package android.application.duties_for_all;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duties_for_all.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityHome extends AppCompatActivity implements View.OnClickListener {



    //What is done in this activity:
    //the user is asked whether they want to create a new dutyList, edit an existing dutyList, view/edit student/dutyList data



    //define all buttons (here so that they can be used in the onClick method)
    Button btnCreate;
    Button btnTrack;
    Button btnViewEdit;
    Button btnLogOut;
    Button btnHelp;

    //for syncing latest done and number done
    ArrayList<Student> allStudents = new ArrayList<>();
    DataSnapshot dsLists;

    //when the activity is started:
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //set onClick listeners to all buttons
        btnCreate = findViewById(R.id.HomeBtnCreate);
        btnCreate.setOnClickListener(this);
        btnTrack = findViewById(R.id.HomeBtnTrack);
        btnTrack.setOnClickListener(this);
        btnViewEdit = findViewById(R.id.HomeBtnData);
        btnViewEdit.setOnClickListener(this);
        btnLogOut = findViewById(R.id.HomeBtnLogOut);
        btnLogOut.setOnClickListener(this);
        btnHelp = findViewById(R.id.HomeBtnHelp);
        btnHelp.setOnClickListener(this);

        //sync latest done and numbers done
        FirebaseDatabase.getInstance().getReference().get().addOnCompleteListener(task -> {
            DataSnapshot dsStudents = task.getResult().child("Student_Info");
            dsLists = task.getResult().child("List_Archive");

            //create Student objects based on the database and add them to an array
            for (DataSnapshot ds : dsStudents.child("Emails").getChildren()) {
                Student student = new Student(ds.getKey());
                student.setNumberDone(0);
                student.setLatestDone(new Date("Aug 1, 2022"));

                allStudents.add(student);
            }

            //update numbers done and latest done based on all lists in list archive
            numberAndLatestDone();

            //update numbers and latest done in the database
            for (Student student : allStudents) {
                FirebaseDatabase.getInstance().getReference("Student_Info").child("Numbers_done").
                        child(student.getId()).setValue(String.valueOf(student.getNumberDone()));
                FirebaseDatabase.getInstance().getReference("Student_Info").child("Latest_done").
                        child(student.getId()).setValue(student.getLatestDone().toString());
            }
        });
    }
    //onCreate ends

    //when the buttons with onClick listeners are clicked:
    @Override
    public void onClick(View view) {
        //when the 'create' button is clicked:
        if (view == btnCreate) {
            //go to 'create list' activity
            Intent intent = new Intent(this, ActivityCreateList.class);
            startActivity(intent);
        }

        //when the 'track' button is clicked:
        if (view == btnTrack) {
            //go to 'edit list' activity
            Intent intent = new Intent(this, ActivityTrack.class);
            startActivity(intent);
        }

        //when the 'view or edit' button is clicked:
        if (view == btnViewEdit) {
            //go to 'view edit' activity
            Intent intent = new Intent(this, ActivityViewEdit.class);
            startActivity(intent);
        }

        //when the 'log out' button is clicked:
        if (view == btnLogOut) {
            //close the application
            finishAffinity();
            System.exit(0);
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

    //count how many times each student is in the lists
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void numberAndLatestDone() {
        ArrayList<DutyList> lists = new ArrayList<>();
        for (DataSnapshot listDS : dsLists.getChildren()) {lists.add(getListData(listDS));}
        for (Student student : allStudents) {
            for (DutyList list : lists) {
                if (list.getStudents().contains(student) && !list.getExtraStudents().contains(student)) {student.add(list.getType(), list.getDate());}
            }
        }
    }
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
    //method to identify a student using their email
    public Student getStudentById(String id) {
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getId().equals(id)) {
                return allStudents.get(i);
            }
        }
        return null;
    }
}