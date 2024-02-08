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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.Locale;
import java.util.Objects;

public class ActivityShowStudentData extends AppCompatActivity implements View.OnClickListener, rvInterface {



    //all student data is displayed in a searchable list
    //the list can be filtered
    //when clicked, the student data can be edited
    //new students can be added
    //students can also be removed



    //define all buttons (here so that they can be used in the onClick method)
    Button btnHelp;
    Button btnGoBack;
    Button btnFilters;
    Button btnAdd;

    //retrieve data from the student database
    ArrayList<Student> allStudents;

    //define variables used in the popup
    //used to search through the student list
    SearchView searchView;
    //used to dynamically display all student names
    rvAdapterStudents adapter;

    //list of students that will be displayed
    ArrayList<Student> toDisplay = new ArrayList<>();
    //filters
    int sortingType = 0;
    boolean dp2s = true;
    boolean dp1s = true;
    boolean predps = true;
    boolean locals = true;
    boolean nonLocals = true;
    boolean onCampus = true;
    boolean offCampus = true;
    boolean allowCow = true;
    boolean allowChicken = true;
    boolean allowVisitorCenter = true;
    boolean exemptCow = true;
    boolean exemptChicken = true;
    boolean exemptVisitorCenter = true;

    //loading screen
    ProgressDialog loading;

    //show list appearances
    DataSnapshot dsLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_student_data);

        //set onClick listeners to all buttons
        btnHelp = findViewById(R.id.ShowStudentDataBtnHelp);
        btnHelp.setOnClickListener(this);
        btnGoBack = findViewById(R.id.ShowStudentDataBtnGoBack);
        btnGoBack.setOnClickListener(this);
        btnAdd = findViewById(R.id.ShowStudentDataBtnAdd);
        btnAdd.setOnClickListener(this);
        btnFilters = findViewById(R.id.ShowStudentDataBtnFilter);
        btnFilters.setOnClickListener(this);

        //collect data from database of all students and store it in a alphabetically sorted array of Student objects
        //show a loading window while the data is collected from the database
        loading = ProgressDialog.show(this,"Loading","Please wait");
        //initialize the array
        allStudents = new ArrayList<>();
        toDisplay = new ArrayList<>();
        //fill the array and display the chosen data in a clickable recycler view
        FirebaseDatabase.getInstance().getReference().get().addOnCompleteListener(task -> databaseTasks(task.getResult()));
    }
    //onCreate ends

    //when the buttons with on-click listeners are clicked:
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
            //go back to the 'check student data' activity
            Intent intent = new Intent(this, ActivityViewEdit.class);
            startActivity(intent);
        }

        //when the 'filters' button is clicked:
        if (view == btnFilters) {
            //define the pop-up, close button
            View popup = View.inflate(this, R.layout.popup_students_filter, null);
            Button btnX = popup.findViewById(R.id.popupShowStudentDataFilterBtnX);

            //check sorting type and set buttons
            Button sortAZ = popup.findViewById(R.id.popupShowStudentDataFilterBtnAZ);
            Button sortZA = popup.findViewById(R.id.popupShowStudentDataFilterBtnZA);
            Button sortAscend = popup.findViewById(R.id.popupShowStudentDataFilterBtnAscend);
            Button sortDescend = popup.findViewById(R.id.popupShowStudentDataFilterBtnDescend);
            if (sortingType == 0) {
                SpannableString sort = new SpannableString("A-Z");
                sort.setSpan(new UnderlineSpan(), 0, sort.length(), 0);
                sortAZ.setText(sort);
            }
            if (sortingType == 1) {
                SpannableString sort = new SpannableString("Z-A");
                sort.setSpan(new UnderlineSpan(), 0, sort.length(), 0);
                sortZA.setText(sort);
            }
            if (sortingType == 2) {
                SpannableString sort = new SpannableString("Ascend.");
                sort.setSpan(new UnderlineSpan(), 0, sort.length(), 0);
                sortAscend.setText(sort);
            }
            if (sortingType == 3) {
                SpannableString sort = new SpannableString("Descend.");
                sort.setSpan(new UnderlineSpan(), 0, sort.length(), 0);
                sortDescend.setText(sort);
            }

            //set sort button listeners
            sortAZ.setOnClickListener(v -> {
                sortingType = 0;
                SpannableString sort = new SpannableString("A-Z");
                sort.setSpan(new UnderlineSpan(), 0, sort.length(), 0);
                sortAZ.setText(sort);
                sortZA.setText("Z-A");
                sortAscend.setText(R.string.popup_show_student_data_filter_btn_ascend);
                sortDescend.setText(R.string.popup_show_student_data_filter_btn_descend);

                //sort the array A-Z by first name (selection sort)
                for (int round = 0; round < toDisplay.size() - 1; round++) {
                    String last = "A";
                    int index = 0;
                    for (int i = 0; i < toDisplay.size() - round; i++) {
                        if (toDisplay.get(i).getFirstName().compareTo(last) > 0) {
                            last = toDisplay.get(i).getFirstName();
                            index = i;
                        }
                    }
                    Student temp = toDisplay.get(toDisplay.size() - 1 - round);
                    toDisplay.set(toDisplay.size() - 1 - round, toDisplay.get(index));
                    toDisplay.set(index, temp);
                }

                activateRecyclerView();
            });
            sortZA.setOnClickListener(v -> {
                sortingType = 1;
                SpannableString sort = new SpannableString("Z-A");
                sort.setSpan(new UnderlineSpan(), 0, sort.length(), 0);
                sortZA.setText(sort);
                sortAZ.setText("A-Z");
                sortAscend.setText(R.string.popup_show_student_data_filter_btn_ascend);
                sortDescend.setText(R.string.popup_show_student_data_filter_btn_descend);

                //sort the array Z-A by first name (selection sort)
                for (int round = 0; round < toDisplay.size() - 1; round++) {
                    String first = "Z";
                    int index = 0;
                    for (int i = 0; i < toDisplay.size() - round; i++) {
                        if (toDisplay.get(i).getFirstName().compareTo(first) < 0) {
                            first = toDisplay.get(i).getFirstName();
                            index = i;
                        }
                    }
                    Student temp = toDisplay.get(toDisplay.size() - 1 - round);
                    toDisplay.set(toDisplay.size() - 1 - round, toDisplay.get(index));
                    toDisplay.set(index, temp);
                }

                activateRecyclerView();
            });
            sortAscend.setOnClickListener(v -> {
                sortingType = 2;
                SpannableString sort = new SpannableString("Ascend.");
                sort.setSpan(new UnderlineSpan(), 0, sort.length(), 0);
                sortAscend.setText(sort);
                sortZA.setText("Z-A");
                sortAZ.setText("A-Z");
                sortDescend.setText(R.string.popup_show_student_data_filter_btn_descend);

                //sort the array by number done and latest done (selection sort)
                for (int round = 0; round < toDisplay.size() - 1; round++) {
                    int max = 0;
                    int index = 0;
                    for (int i = 0; i < toDisplay.size() - round; i++) {
                        if (toDisplay.get(i).getNumberDone() > max || (toDisplay.get(i).getNumberDone() == max &&
                                toDisplay.get(index).getLatestDone().before(toDisplay.get(i).getLatestDone()))) {
                            max = toDisplay.get(i).getNumberDone();
                            index = i;
                        }
                    }
                    Student temp = toDisplay.get(toDisplay.size() - 1 - round);
                    toDisplay.set(toDisplay.size() - 1 - round, toDisplay.get(index));
                    toDisplay.set(index, temp);
                }

                activateRecyclerView();
            });
            sortDescend.setOnClickListener(v -> {
                sortingType = 3;
                SpannableString sort = new SpannableString("Descend.");
                sort.setSpan(new UnderlineSpan(), 0, sort.length(), 0);
                sortDescend.setText(sort);
                sortZA.setText("Z-A");
                sortAscend.setText(R.string.popup_show_student_data_filter_btn_ascend);
                sortAZ.setText("A-Z");

                //sort the array by number done and latest done in descending order (selection sort)
                for (int round = 0; round < toDisplay.size() - 1; round++) {
                    int min = 100;
                    int index = 0;
                    for (int i = 0; i < toDisplay.size() - round; i++) {
                        if (toDisplay.get(i).getNumberDone() < min || (toDisplay.get(i).getNumberDone() == min &&
                                !toDisplay.get(index).getLatestDone().before(toDisplay.get(i).getLatestDone()))) {
                            min = toDisplay.get(i).getNumberDone();
                            index = i;
                        }
                    }
                    Student temp = toDisplay.get(toDisplay.size() - 1 - round);
                    toDisplay.set(toDisplay.size() - 1 - round, toDisplay.get(index));
                    toDisplay.set(index, temp);
                }

                activateRecyclerView();
            });

            //filter checkboxes
            CheckBox dp2 = popup.findViewById(R.id.checkboxDp2);
            dp2.setChecked(dp2s);
            dp2.setOnCheckedChangeListener((buttonView, isChecked) -> {
                dp2s = isChecked;
                reFilterList();
            });
            CheckBox dp1 = popup.findViewById(R.id.checkboxDp1);
            dp1.setChecked(dp1s);
            dp1.setOnCheckedChangeListener((buttonView, isChecked) -> {
                dp1s = isChecked;
                reFilterList();
            });
            CheckBox predp = popup.findViewById(R.id.checkboxPredp);
            predp.setChecked(predps);
            predp.setOnCheckedChangeListener((buttonView, isChecked) -> {
                predps = isChecked;
                reFilterList();
            });
            CheckBox local = popup.findViewById(R.id.checkboxLocal);
            local.setChecked(locals);
            local.setOnCheckedChangeListener((buttonView, isChecked) -> {
                locals = isChecked;
                reFilterList();
            });
            CheckBox nonLocal = popup.findViewById(R.id.checkboxNonlocal);
            nonLocal.setChecked(nonLocals);
            nonLocal.setOnCheckedChangeListener((buttonView, isChecked) -> {
                nonLocals = isChecked;
                reFilterList();
            });
            CheckBox OnCampus = popup.findViewById(R.id.checkboxOncampus);
            OnCampus.setChecked(onCampus);
            OnCampus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                onCampus = isChecked;
                reFilterList();
            });
            CheckBox OffCampus = popup.findViewById(R.id.checkboxOffcampus);
            OffCampus.setChecked(offCampus);
            OffCampus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                offCampus = isChecked;
                reFilterList();
            });
            CheckBox allowedCow = popup.findViewById(R.id.checkboxAllowCow);
            allowedCow.setChecked(allowCow);
            allowedCow.setOnCheckedChangeListener((buttonView, isChecked) -> {
                allowCow = isChecked;
                reFilterList();
            });
            CheckBox allowedChicken = popup.findViewById(R.id.checkboxAllowChicken);
            allowedChicken.setChecked(allowChicken);
            allowedChicken.setOnCheckedChangeListener((buttonView, isChecked) -> {
                allowChicken = isChecked;
                reFilterList();
            });
            CheckBox allowedVisitorCenter = popup.findViewById(R.id.checkboxAllowVC);
            allowedVisitorCenter.setChecked(allowVisitorCenter);
            allowedVisitorCenter.setOnCheckedChangeListener((buttonView, isChecked) -> {
                allowVisitorCenter = isChecked;
                reFilterList();
            });
            CheckBox exemptFromCow = popup.findViewById(R.id.checkboxExemptCow);
            exemptFromCow.setChecked(exemptCow);
            exemptFromCow.setOnCheckedChangeListener((buttonView, isChecked) -> {
                exemptCow = isChecked;
                reFilterList();
            });
            CheckBox exemptFromChicken = popup.findViewById(R.id.checkboxExemptChicken);
            exemptFromChicken.setChecked(exemptChicken);
            exemptFromChicken.setOnCheckedChangeListener((buttonView, isChecked) -> {
                exemptChicken = isChecked;
                reFilterList();
            });
            CheckBox exemptFromVisitorCenter = popup.findViewById(R.id.checkboxExemptVC);
            exemptFromVisitorCenter.setChecked(exemptVisitorCenter);
            exemptFromVisitorCenter.setOnCheckedChangeListener((buttonView, isChecked) -> {
                exemptVisitorCenter = isChecked;
                reFilterList();
            });

            //display pop-up
            //makes pop-up height depend on the screen size
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            int width = displayMetrics.widthPixels - 80;
            final PopupWindow popupWindow = new PopupWindow(popup, width, height,true); //'focusable:true' allows to close pop-up by touch out of the pop-up window
            popupWindow.setElevation(200); //adds a shadow to the pop-up
            popupWindow.showAtLocation(findViewById(R.id.ShowStudentDataRecyclerView), Gravity.CENTER, 0, 0);

            //close popup through the close button
            btnX.setOnClickListener(v -> popupWindow.dismiss());
        }

        //when the 'add' button is clicked:
        if (view == btnAdd) {
            //show a popup like the edit one but blank
            //on add check if everything is filled in
            //add to database, all students, to display

            Student student = new Student();

            //define the pop-up, close button
            View popup = View.inflate(this, R.layout.popup_edit_student, null);
            Button btnX = popup.findViewById(R.id.popupEditStudentBtnX);

            Button btnNewStudent = popup.findViewById(R.id.popupEditStudentBtnRemove);

            btnNewStudent.setText(R.string.popup_edit_student_btn_add);


            //checkboxes, edit texts, buttons
            EditText editTxtFirstName = popup.findViewById(R.id.popupEditStudentEditTxtFirstName);
            EditText editTxtLastName = popup.findViewById(R.id.popupEditStudentEditTxtLastName);
            EditText editTxtEmail = popup.findViewById(R.id.popupEditStudentEditTxtEmail);
            Button btnGradeDP2 = popup.findViewById(R.id.popupEditStudentBtnDP2);
            Button btnGradeDP1 = popup.findViewById(R.id.popupEditStudentBtnDP1);
            Button btnGradePreDP = popup.findViewById(R.id.popupEditStudentBtnPreDP);
            Button btnLocal = popup.findViewById(R.id.popupEditStudentBtnLocal);
            Button btnNonLocal = popup.findViewById(R.id.popupEditStudentBtnNonLocal);
            CheckBox checkboxCow = popup.findViewById(R.id.popupEditStudentCheckBoxCow);
            CheckBox checkboxChicken = popup.findViewById(R.id.popupEditStudentCheckBoxChicken);
            CheckBox checkboxVisitorCenter = popup.findViewById(R.id.popupEditStudentCheckBoxVisitorCenter);

            //hide unneeded layouts
            popup.findViewById(R.id.popupEditStudentLayoutOnCampus).setVisibility(View.GONE);
            popup.findViewById(R.id.popupEditStudentLayoutNumberDone).setVisibility(View.GONE);
            popup.findViewById(R.id.popupEditStudentLayoutLatestDone).setVisibility(View.GONE);

            //fill in all student information
            //name, email, and number done
            editTxtFirstName.setText(student.getFirstName(), TextView.BufferType.EDITABLE);
            editTxtLastName.setText(student.getLastName(), TextView.BufferType.EDITABLE);
            editTxtEmail.setText(student.getEmail(), TextView.BufferType.EDITABLE);
            //set grade
            if (student.getGrade().equals("dp2")) {
                SpannableString grade = new SpannableString("DP2");
                grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
                btnGradeDP2.setText(grade);
            }
            if (student.getGrade().equals("dp1")) {
                SpannableString grade = new SpannableString("DP1");
                grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
                btnGradeDP1.setText(grade);
            }
            if (student.getGrade().equals("predp")) {
                SpannableString grade = new SpannableString("Pre-DP");
                grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
                btnGradePreDP.setText(grade);
            }
            //set local
            if (student.isLocal()) {
                SpannableString local = new SpannableString("Yes");
                local.setSpan(new UnderlineSpan(), 0, local.length(), 0);
                btnLocal.setText(local);
            }
            else {
                SpannableString local = new SpannableString("No");
                local.setSpan(new UnderlineSpan(), 0, local.length(), 0);
                btnNonLocal.setText(local);
            }
            //set restrictions
            if (!student.allowCow()) {checkboxCow.setChecked(true);}
            if (!student.allowChicken()) {checkboxChicken.setChecked(true);}
            if (!student.allowVisitorCenter()) {checkboxVisitorCenter.setChecked(true);}

            //display pop-up
            //makes pop-up height depend on the screen size
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            int width = displayMetrics.widthPixels - 80;
            final PopupWindow popupWindow = new PopupWindow(popup, width, height,true); //'focusable:true' allows to close pop-up by touch out of the pop-up window
            popupWindow.setElevation(200); //adds a shadow to the pop-up
            popupWindow.showAtLocation(findViewById(R.id.ShowStudentDataRecyclerView), Gravity.CENTER, 0, 0);

            //get response from grade, local, on campus buttons
            //grades
            btnGradeDP2.setOnClickListener(v -> {
                student.setGrade("dp2");
                //change underlined button
                SpannableString grade = new SpannableString("DP2");
                grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
                btnGradeDP2.setText(grade);
                btnGradeDP1.setText(R.string.dp1);
                btnGradePreDP.setText(R.string.pre_dp);
            });
            btnGradeDP1.setOnClickListener(v -> {
                student.setGrade("dp1");
                //change underlined button
                SpannableString grade = new SpannableString("DP1");
                grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
                btnGradeDP1.setText(grade);
                btnGradeDP2.setText(R.string.dp2);
                btnGradePreDP.setText(R.string.pre_dp);
            });
            btnGradePreDP.setOnClickListener(v -> {
                student.setGrade("predp");
                //change underlined button
                SpannableString grade = new SpannableString("Pre-DP");
                grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
                btnGradePreDP.setText(grade);
                btnGradeDP1.setText(R.string.dp1);
                btnGradeDP2.setText(R.string.dp2);
            });
            //local
            btnLocal.setOnClickListener(v -> {
                student.setLocal(1);
                //change underlined button
                SpannableString local = new SpannableString("Yes");
                local.setSpan(new UnderlineSpan(), 0, local.length(), 0);
                btnLocal.setText(local);
                btnNonLocal.setText(R.string.no);
            });
            btnNonLocal.setOnClickListener(v -> {
                student.setLocal(0);
                //change underlined button
                SpannableString local = new SpannableString("No");
                local.setSpan(new UnderlineSpan(), 0, local.length(), 0);
                btnNonLocal.setText(local);
                btnLocal.setText(R.string.yes);
            });

            //close popup through the close button
            btnX.setOnClickListener(v -> popupWindow.dismiss());

            //add the student through add button
            btnNewStudent.setOnClickListener(v -> {
                //restriction -> all information has to be filled in
                if (editTxtFirstName.getText().toString().equals("") || Objects.isNull(editTxtFirstName.getText().toString()) ||
                        editTxtLastName.getText().toString().equals("") || Objects.isNull(editTxtLastName.getText().toString()) ||
                        editTxtEmail.getText().toString().equals("") || Objects.isNull(editTxtEmail.getText().toString()) ||
                        student.getGrade().equals("") || Objects.isNull(student.getGrade())) {
                    Toast.makeText(this, "Please fill in all information about the student", Toast.LENGTH_SHORT).show();
                }
                else {
                    //set id, name, email
                    student.setFirstName(editTxtFirstName.getText().toString());
                    student.setLastName(editTxtLastName.getText().toString());
                    String email = editTxtEmail.getText().toString();
                    student.setEmail(email);
                    student.setId(email.split("@")[0].split("\\.")[0] + "_" + email.split("@")[0].split("\\.")[1].charAt(0));

                    //set restrictions
                    if (checkboxCow.isChecked()) {student.setCow(0);}
                    else {student.setCow(1);}
                    if (checkboxChicken.isChecked()) {student.setChicken(0);}
                    else {student.setChicken(1);}
                    if (checkboxVisitorCenter.isChecked()) {student.setVisitorCenter(0);}
                    else {student.setVisitorCenter(1);}

                    //transfer everything to the database
                    //set strings and integers
                    FirebaseDatabase.getInstance().getReference("Student_Info").child("First_names").child(student.getId()).setValue(student.getFirstName());
                    FirebaseDatabase.getInstance().getReference("Student_Info").child("Last_names").child(student.getId()).setValue(student.getLastName());
                    FirebaseDatabase.getInstance().getReference("Student_Info").child("Emails").child(student.getId()).setValue(student.getEmail());
                    FirebaseDatabase.getInstance().getReference("Student_Info").child("Grades").child(student.getId()).setValue(student.getGrade());
                    FirebaseDatabase.getInstance().getReference("Student_Info").child("Numbers_done").child(student.getId()).setValue(String.valueOf(student.getNumberDone()));
                    FirebaseDatabase.getInstance().getReference("Student_Info").child("Latest_done").child(student.getId()).setValue(student.getLatestDone().toString());
                    //set booleans
                    FirebaseDatabase.getInstance().getReference("Student_Info").child("Locals").child(student.getId()).setValue(booleanToString(student.isLocal()));
                    FirebaseDatabase.getInstance().getReference("Student_Info").child("Cow_restrictions").child(student.getId()).setValue(booleanToString(student.allowCow()));
                    FirebaseDatabase.getInstance().getReference("Student_Info").child("Chicken_restrictions").child(student.getId()).setValue(booleanToString(student.allowChicken()));
                    FirebaseDatabase.getInstance().getReference("Student_Info").child("Visitor_center_restrictions").child(student.getId()).setValue(booleanToString(student.allowVisitorCenter()));

                    allStudents.add(student);
                    toDisplay.add(student);

                    reFilterList();
                    activateRecyclerView();

                    popupWindow.dismiss();

                    Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //method used to store the data from the student database in arrays of Student objects and display anyone chose prior
    public void databaseTasks(DataSnapshot dataSnapshot) {
        dsLists = dataSnapshot.child("List_Archive");
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
            String date = new Date().toString();
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

        toDisplay.addAll(allStudents);

        //sort the chosen student array alphabetically by first name (selection sort)
        for (int round = 0; round < toDisplay.size() - 1; round++) {
            String last = "A";
            int index = 0;
            for (int i = 0; i < toDisplay.size() - round; i++) {
                if (toDisplay.get(i).getFirstName().compareTo(last) > 0) {
                    last = toDisplay.get(i).getFirstName();
                    index = i;
                }
            }
            Student temp = toDisplay.get(toDisplay.size() - 1 - round);
            toDisplay.set(toDisplay.size() - 1 - round, toDisplay.get(index));
            toDisplay.set(index, temp);
        }

        //remove the loading window
        loading.dismiss();

        //to start display all student data
        activateRecyclerView();
    }

    public void activateRecyclerView() {
        //set search view
        searchView = findViewById(R.id.ShowStudentDataSearchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        //show number displayed
        TextView numberDisplayed = findViewById(R.id.ShowStudentDataTxtNumberDisplayed);
        numberDisplayed.setText(String.valueOf(toDisplay.size()));

        //recycler view
        RecyclerView recyclerView = findViewById(R.id.ShowStudentDataRecyclerView);
        //toDisplay array has to be set up before adapter
        adapter = new rvAdapterStudents(this, toDisplay, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //method used for the search view
    private void filterList(String input) {
        //filter chosen array based on user input (compares to name in dutyList)
        ArrayList<Student> filteredList = new ArrayList<>();
        assert toDisplay != null;
        for (Student student : toDisplay) {
            if (student.getFullName().toLowerCase(Locale.ROOT).contains(input.toLowerCase(Locale.ROOT))) {
                filteredList.add(student);
            }
        }

        //replace the list with the filtered list
        adapter.setFilteredList(filteredList);
        //show number displayed
        TextView numberDisplayed = findViewById(R.id.ShowStudentDataTxtNumberDisplayed);
        numberDisplayed.setText(String.valueOf(filteredList.size()));
    }

    //method for clicking on recycler view items
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemClick(Student student) {
        //shows student information
        //allows to edit student information
        //allows to permanently remove a student

        //define the pop-up, close button, and TextView
        View popup = View.inflate(this, R.layout.popup_edit_student, null);
        Button btnX = popup.findViewById(R.id.popupEditStudentBtnX);
        Button btnRemove = popup.findViewById(R.id.popupEditStudentBtnRemove);
        Button btnUpdate = popup.findViewById(R.id.popupEditStudentBtnUpdate);

        btnUpdate.setVisibility(View.VISIBLE);

        //checkboxes, edit texts, buttons
        EditText editTxtFirstName = popup.findViewById(R.id.popupEditStudentEditTxtFirstName);
        EditText editTxtLastName = popup.findViewById(R.id.popupEditStudentEditTxtLastName);
        EditText editTxtEmail = popup.findViewById(R.id.popupEditStudentEditTxtEmail);
        Button btnGradeDP2 = popup.findViewById(R.id.popupEditStudentBtnDP2);
        Button btnGradeDP1 = popup.findViewById(R.id.popupEditStudentBtnDP1);
        Button btnGradePreDP = popup.findViewById(R.id.popupEditStudentBtnPreDP);
        Button btnLocal = popup.findViewById(R.id.popupEditStudentBtnLocal);
        Button btnNonLocal = popup.findViewById(R.id.popupEditStudentBtnNonLocal);
        TextView txtOnCampus = popup.findViewById(R.id.popupEditStudentTxtOnCampus);
        TextView txtNumberDone = popup.findViewById(R.id.popupEditStudentTxtNumberDone);
        TextView txtLatestDone = popup.findViewById(R.id.popupEditStudentTxtLatestDone);
        CheckBox checkboxCow = popup.findViewById(R.id.popupEditStudentCheckBoxCow);
        CheckBox checkboxChicken = popup.findViewById(R.id.popupEditStudentCheckBoxChicken);
        CheckBox checkboxVisitorCenter = popup.findViewById(R.id.popupEditStudentCheckBoxVisitorCenter);

        //fill in all student information
        //name, email, number done, and latest done
        editTxtFirstName.setText(student.getFirstName(), TextView.BufferType.EDITABLE);
        editTxtLastName.setText(student.getLastName(), TextView.BufferType.EDITABLE);
        editTxtEmail.setText(student.getEmail(), TextView.BufferType.EDITABLE);
        txtNumberDone.setText(String.valueOf(student.getNumberDone()));
        txtLatestDone.setText(student.getLatestDone().toString());
        //set grade
        if (student.getGrade().equals("dp2")) {
            SpannableString grade = new SpannableString("DP2");
            grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
            btnGradeDP2.setText(grade);
        }
        if (student.getGrade().equals("dp1")) {
            SpannableString grade = new SpannableString("DP1");
            grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
            btnGradeDP1.setText(grade);
        }
        if (student.getGrade().equals("predp")) {
            SpannableString grade = new SpannableString("Pre-DP");
            grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
            btnGradePreDP.setText(grade);
        }
        //set local
        if (student.isLocal()) {
            SpannableString local = new SpannableString("Yes");
            local.setSpan(new UnderlineSpan(), 0, local.length(), 0);
            btnLocal.setText(local);
        } else {
            SpannableString local = new SpannableString("No");
            local.setSpan(new UnderlineSpan(), 0, local.length(), 0);
            btnNonLocal.setText(local);
        }
        //set on campus
        if (student.isOnCampus()) {txtOnCampus.setText(R.string.yes);}
        else {txtOnCampus.setText(R.string.no);}

        //set restrictions
        if (!student.allowCow()) {checkboxCow.setChecked(true);}
        if (!student.allowChicken()) {checkboxChicken.setChecked(true);}
        if (!student.allowVisitorCenter()) {checkboxVisitorCenter.setChecked(true);}

        //display pop-up
        //makes pop-up height depend on the screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        int width = displayMetrics.widthPixels - 80;
        final PopupWindow popupWindow = new PopupWindow(popup, width, height, true); //'focusable:true' allows to close pop-up by touch out of the pop-up window
        popupWindow.setElevation(200); //adds a shadow to the pop-up
        popupWindow.showAtLocation(findViewById(R.id.ShowStudentDataRecyclerView), Gravity.CENTER, 0, 0);

        //get response from buttons: grade, local, on campus, latest done
        //grades
        btnGradeDP2.setOnClickListener(v -> {
            student.setGrade("dp2");
            //change underlined button
            SpannableString grade = new SpannableString("DP2");
            grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
            btnGradeDP2.setText(grade);
            btnGradeDP1.setText(R.string.dp1);
            btnGradePreDP.setText(R.string.pre_dp);
        });
        btnGradeDP1.setOnClickListener(v -> {
            student.setGrade("dp1");
            //change underlined button
            SpannableString grade = new SpannableString("DP1");
            grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
            btnGradeDP1.setText(grade);
            btnGradeDP2.setText(R.string.dp2);
            btnGradePreDP.setText(R.string.pre_dp);
        });
        btnGradePreDP.setOnClickListener(v -> {
            student.setGrade("predp");
            //change underlined button
            SpannableString grade = new SpannableString("Pre-DP");
            grade.setSpan(new UnderlineSpan(), 0, grade.length(), 0);
            btnGradePreDP.setText(grade);
            btnGradeDP1.setText(R.string.dp1);
            btnGradeDP2.setText(R.string.dp2);
        });
        //local
        btnLocal.setOnClickListener(v -> {
            student.setLocal(1);
            //change underlined button
            SpannableString local = new SpannableString("Yes");
            local.setSpan(new UnderlineSpan(), 0, local.length(), 0);
            btnLocal.setText(local);
            btnNonLocal.setText(R.string.no);
        });
        btnNonLocal.setOnClickListener(v -> {
            student.setLocal(0);
            //change underlined button
            SpannableString local = new SpannableString("No");
            local.setSpan(new UnderlineSpan(), 0, local.length(), 0);
            btnNonLocal.setText(local);
            btnLocal.setText(R.string.yes);
        });

        //get responses from checkboxes: cow, chicken, visitor center exemptions
        checkboxCow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {student.setCow(0);}
            else {student.setCow(1);}
        });
        checkboxChicken.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {student.setChicken(0);}
            else {student.setChicken(1);}
        });
        checkboxVisitorCenter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {student.setVisitorCenter(0);}
            else {student.setVisitorCenter(1);}
        });


        //close popup through the close button
        btnX.setOnClickListener(v -> popupWindow.dismiss());

        //button to remove a student completely
        btnRemove.setOnClickListener(v -> {
            //close current popup, will show again later
            popupWindow.dismiss();

            //warn the user
            //define the pop-up, confirm and close buttons, and TextView
            View popupWarn = View.inflate(this, R.layout.popup_warning, null);
            Button btnConfirm = popupWarn.findViewById(R.id.popupWarningBtn);
            Button btnWarnX = popupWarn.findViewById(R.id.popupWarningBtnX);
            TextView popupTxt = popupWarn.findViewById(R.id.popupWarningTxt);

            //make the TextView scrollable (in case the text does no fully fit into the window)
            popupTxt.setMovementMethod(new ScrollingMovementMethod());

            //set warning text
            popupTxt.setText(R.string.show_student_data_popup_warning);
            popupTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            popupTxt.setTextColor(this.getResources().getColor(R.color.text_color));

            //display pop-up
            //makes pop-up height and width depend on the screen size and content
            DisplayMetrics displayMetricsWarn = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetricsWarn);
            int heightWarn = ViewGroup.LayoutParams.WRAP_CONTENT;
            int widthWarn = displayMetricsWarn.widthPixels - 150;
            final PopupWindow popupWindowWarn = new PopupWindow(popupWarn, widthWarn, heightWarn, false); //'focusable:true' allows to close pop-up by touch out of the pop-up window
            popupWindowWarn.setElevation(200); //adds a shadow to the pop-up
            popupWindowWarn.showAtLocation(btnRemove, Gravity.CENTER, 0, 0);

            //move on through confirm button
            btnConfirm.setOnClickListener(viewConfirm -> {
                //remove the student from the array, database, and recycler view
                FirebaseDatabase.getInstance().getReference("Student_Info").child("First_names").child(student.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("Student_Info").child("Last_names").child(student.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("Student_Info").child("Emails").child(student.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("Student_Info").child("Grades").child(student.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("Student_Info").child("Numbers_done").child(student.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("Student_Info").child("Latest_done").child(student.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("Student_Info").child("Locals").child(student.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("Student_Info").child("On_campus").child(student.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("Student_Info").child("Cow_restrictions").child(student.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("Student_Info").child("Chicken_restrictions").child(student.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("Student_Info").child("Visitor_center_restrictions").child(student.getId()).removeValue();

                allStudents.remove(student);
                toDisplay.remove(student);
                activateRecyclerView();

                popupWindowWarn.dismiss();
                popupWindow.dismiss();
            });

            //close popup through button
            btnWarnX.setOnClickListener(viewX -> {
                popupWindowWarn.dismiss();
                popupWindow.showAtLocation(findViewById(R.id.ShowStudentDataRecyclerView), Gravity.CENTER, 0, 0);
            });
        });

        //button to update the student information
        btnUpdate.setOnClickListener(v -> {
            //restriction: no blank fields can be left
            if (editTxtFirstName.getText().toString().equals("") || Objects.isNull(editTxtFirstName.getText().toString())
                    || editTxtLastName.getText().toString().equals("") || Objects.isNull(editTxtLastName.getText().toString())
                    || editTxtEmail.getText().toString().equals("") || Objects.isNull(editTxtEmail.getText().toString())) {
                Toast.makeText(this, "Please fill in all information about the student", Toast.LENGTH_SHORT).show();
            }
            else {
                student.setFirstName(editTxtFirstName.getText().toString());
                student.setLastName(editTxtLastName.getText().toString());
                student.setEmail(editTxtEmail.getText().toString());

                popupWindow.dismiss();

                updateStudent(student);
            }
        });
    }

    @Override
    public void onItemClick(DutyList list) {}

    public String booleanToString(boolean b) {
        if (b) {return "1";}
        else {return "0";}
    }

    public void updateStudent(Student student) {
        //determine student indexes
        int indexAll = allStudents.indexOf(student);

        //transfer everything to the database
        //set strings and integers
        FirebaseDatabase.getInstance().getReference("Student_Info").child("First_names").child(student.getId()).setValue(student.getFirstName());
        FirebaseDatabase.getInstance().getReference("Student_Info").child("Last_names").child(student.getId()).setValue(student.getLastName());
        FirebaseDatabase.getInstance().getReference("Student_Info").child("Emails").child(student.getId()).setValue(student.getEmail());
        FirebaseDatabase.getInstance().getReference("Student_Info").child("Grades").child(student.getId()).setValue(student.getGrade());
        //set booleans
        FirebaseDatabase.getInstance().getReference("Student_Info").child("Locals").child(student.getId()).setValue(booleanToString(student.isLocal()));
        FirebaseDatabase.getInstance().getReference("Student_Info").child("Cow_restrictions").child(student.getId()).setValue(booleanToString(student.allowCow()));
        FirebaseDatabase.getInstance().getReference("Student_Info").child("Chicken_restrictions").child(student.getId()).setValue(booleanToString(student.allowChicken()));
        FirebaseDatabase.getInstance().getReference("Student_Info").child("Visitor_center_restrictions").child(student.getId()).setValue(booleanToString(student.allowVisitorCenter()));

        //update array and recycler view
        allStudents.set(indexAll, student);
        reFilterList();

        FirebaseDatabase.getInstance().getReference("Student_Info").child("IDs").removeValue();
        for (int i = 1; i <= allStudents.size(); i++) {
            FirebaseDatabase.getInstance().getReference("Student_Info").child("IDs").child(String.valueOf(i)).setValue(allStudents.get(i - 1).getId());
        }
    }

    public void reFilterList() {
        //make toDisplay array a copy of allStudents array
        toDisplay.clear();
        toDisplay.addAll(allStudents);

        //remove all unnecessary students based on the user's filter choices
        for (Student student : allStudents) {
            if (!dp2s && student.getGrade().equals("dp2") ||
                    !dp1s && student.getGrade().equals("dp1") ||
                    !predps && student.getGrade().equals("predp") ||
                    !locals && student.isLocal() ||
                    !nonLocals && !student.isLocal() ||
                    !onCampus && student.isOnCampus() ||
                    !offCampus && !student.isOnCampus() ||
                    !allowCow && student.allowCow() ||
                    !allowChicken && student.allowChicken() ||
                    !allowVisitorCenter && student.allowVisitorCenter() ||
                    !exemptCow && !student.allowCow() ||
                    !exemptChicken && !student.allowChicken() ||
                    !exemptVisitorCenter && !student.allowVisitorCenter()) {
                toDisplay.remove(student);
            }
        }

        //fix order of the list in the recycler view
        if (sortingType == 0) {
            //sort the array A-Z by first name (selection sort)
            for (int round = 0; round < toDisplay.size() - 1; round++) {
                String last = "A";
                int index = 0;
                for (int i = 0; i < toDisplay.size() - round; i++) {
                    if (toDisplay.get(i).getFirstName().compareTo(last) > 0) {
                        last = toDisplay.get(i).getFirstName();
                        index = i;
                    }
                }
                Student temp = toDisplay.get(toDisplay.size() - 1 - round);
                toDisplay.set(toDisplay.size() - 1 - round, toDisplay.get(index));
                toDisplay.set(index, temp);
            }
        }
        if (sortingType == 1) {
            //sort the array Z-A by first name (selection sort)
            for (int round = 0; round < toDisplay.size() - 1; round++) {
                String first = "Z";
                int index = 0;
                for (int i = 0; i < toDisplay.size() - round; i++) {
                    if (toDisplay.get(i).getFirstName().compareTo(first) < 0) {
                        first = toDisplay.get(i).getFirstName();
                        index = i;
                    }
                }
                Student temp = toDisplay.get(toDisplay.size() - 1 - round);
                toDisplay.set(toDisplay.size() - 1 - round, toDisplay.get(index));
                toDisplay.set(index, temp);
            }
        }
        if (sortingType == 2) {
            //sort the array by number done in ascending order(selection sort)
            for (int round = 0; round < toDisplay.size() - 1; round++) {
                int max = 0;
                int index = 0;
                for (int i = 0; i < toDisplay.size() - round; i++) {
                    if (toDisplay.get(i).getNumberDone() > max) {
                        max = toDisplay.get(i).getNumberDone();
                        index = i;
                    }
                }
                Student temp = toDisplay.get(toDisplay.size() - 1 - round);
                toDisplay.set(toDisplay.size() - 1 - round, toDisplay.get(index));
                toDisplay.set(index, temp);
            }
        }
        if (sortingType == 3) {
            //sort the array by number done in descending order(selection sort)
            for (int round = 0; round < toDisplay.size() - 1; round++) {
                int min = 100;
                int index = 0;
                for (int i = 0; i < toDisplay.size() - round; i++) {
                    if (toDisplay.get(i).getNumberDone() < min) {
                        min = toDisplay.get(i).getNumberDone();
                        index = i;
                    }
                }
                Student temp = toDisplay.get(toDisplay.size() - 1 - round);
                toDisplay.set(toDisplay.size() - 1 - round, toDisplay.get(index));
                toDisplay.set(index, temp);
            }
        }

        //notify user if no students fit the chosen filter
        if (toDisplay.size() == 0) {Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show();}

        activateRecyclerView();
    }
}