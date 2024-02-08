package android.application.duties_for_all;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duties_for_all.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ActivityTemplates extends AppCompatActivity implements View.OnClickListener, rvInterface {



    //What is done in this activity:
    //all existing templates are displayed
    //a user coming from 'list info' activity can use one of the templates to create a list
    //a user coming from 'view edit' activity can edit the template information through a pop-up
    //templates can be created or deleted



    //define all buttons (here so that they can be used in the onClick method)
    Button btnHelp;
    Button btnGoBack;
    Button btnNew;
    Button btnDelete;

    //used to dynamically display all templates
    rvAdapterTemplates adapter;
    RecyclerView recyclerView;

    //all template data
    ArrayList<DutyList> templates = new ArrayList<>();

    //get information from the previous activity
    String from;
    int type;
    String date;
    int nBlocks;

    //popup
    EditText editTxtTitle;
    EditText editTxtNumOfBlocks;
    ArrayList<TableRow> rows = new ArrayList<>();
    ArrayList<TextView> txtIndices = new ArrayList<>();
    ArrayList<Button> btnTimes = new ArrayList<>();
    ArrayList<EditText> editTxtPeople = new ArrayList<>();
    int minute, hour;
    boolean remove = false;
    DutyList currentList;
    String prevTitle;

    //loading screen
    ProgressDialog loading;

    //deleting a template
    boolean delete = false;

    //when the activity is started:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates);

        //retrieve data passed on from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            from = extras.getString("from");

            if (from.equals("info")) {
                type = extras.getInt("type");
                date = extras.getString("date");
            }
        }

        //set onClick listeners to all buttons except the time button
        btnHelp = findViewById(R.id.TemplatesBtnHelp);
        btnHelp.setOnClickListener(this);
        btnGoBack = findViewById(R.id.TemplatesBtnGoBack);
        btnGoBack.setOnClickListener(this);
        btnNew = findViewById(R.id.TemplatesBtnNew);
        btnNew.setOnClickListener(this);
        btnDelete = findViewById(R.id.TemplatesBtnDelete);
        btnDelete.setOnClickListener(this);

        //show a loading window while the data is collected from the database
        loading = ProgressDialog.show(this,"Loading","Please wait");

        //get all template data
        FirebaseDatabase.getInstance().getReference("Templates").get().addOnCompleteListener(task -> databaseTask(task.getResult()));
    }

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
            Intent intent;
            if (from.equals("edit")) {intent = new Intent(this, ActivityViewEdit.class);}
            else {intent = new Intent(this, ActivityListInfo.class);}
            startActivity(intent);
        }

        //when the 'create template' button is clicked:
        if (view == btnNew) {
            currentList = new DutyList();

            //define the pop-up, close button, and TextView
            View popup = View.inflate(this, R.layout.popup_edit_template, null);
            Button btnX = popup.findViewById(R.id.popupEditTemplateBtnX);
            editTxtTitle = popup.findViewById(R.id.popupEditTemplateEditTxtTitle);
            editTxtNumOfBlocks = popup.findViewById(R.id.popupEditTemplateEditTxtNumOfBlocks);
            Button btnAdd = popup.findViewById(R.id.popupEditTemplateBtnAdd);
            Button btnRemove = popup.findViewById(R.id.popupEditTemplateBtnRemove);
            Button btnCreate = popup.findViewById(R.id.popupEditTemplateBtnUpdate);
            btnCreate.setText(getResources().getString(R.string.popup_edit_template_btn_create));
            editTxtTitle.setText(currentList.getTitle());
            rows.clear();
            rows.add(popup.findViewById(R.id.popupEditTemplateBlock1));
            rows.add(popup.findViewById(R.id.popupEditTemplateBlock2));
            rows.add(popup.findViewById(R.id.popupEditTemplateBlock3));
            rows.add(popup.findViewById(R.id.popupEditTemplateBlock4));
            rows.add(popup.findViewById(R.id.popupEditTemplateBlock5));
            rows.add(popup.findViewById(R.id.popupEditTemplateBlock6));
            rows.add(popup.findViewById(R.id.popupEditTemplateBlock7));
            rows.add(popup.findViewById(R.id.popupEditTemplateBlock8));
            rows.add(popup.findViewById(R.id.popupEditTemplateBlock9));
            rows.add(popup.findViewById(R.id.popupEditTemplateBlock10));
            txtIndices.clear();
            txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN1));
            txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN2));
            txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN3));
            txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN4));
            txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN5));
            txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN6));
            txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN7));
            txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN8));
            txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN9));
            txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN10));
            btnTimes.clear();
            btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime1));
            btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime2));
            btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime3));
            btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime4));
            btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime5));
            btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime6));
            btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime7));
            btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime8));
            btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime9));
            btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime10));
            editTxtPeople.clear();
            editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN1));
            editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN2));
            editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN3));
            editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN4));
            editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN5));
            editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN6));
            editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN7));
            editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN8));
            editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN9));
            editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN10));

            //display template data
            setTable();

            //add and remove button listeners
            btnAdd.setOnClickListener(v -> {
                int n = currentList.getnBlocks();
                if (n == 10) {
                    Toast.makeText(this, "A list cannot have more than 10 blocks", Toast.LENGTH_SHORT).show();
                }
                else {
                    currentList.setnBlocks(n + 1);
                    rows.get(n).setVisibility(View.VISIBLE);

                    //change blocks array
                    currentList.getBlocks().add(new Block());

                    setTable();
                }
            });
            btnRemove.setOnClickListener(v -> {
                int n = currentList.getnBlocks();
                if (n == 1 || n == 0) {
                    Toast.makeText(this, "A list has to have at least 1 block", Toast.LENGTH_SHORT).show();
                }
                else {
                    remove = true;

                    currentList.setnBlocks(n - 1);
                    Toast.makeText(this, "Please click on the number of the block you would like to remove", Toast.LENGTH_SHORT).show();

                    //remove the chosen block, transferring all information up to keep the numbers in order
                    for (int i = 0; i < n; i++) {
                        txtIndices.get(i).setOnClickListener(v1 -> {
                            if (remove) {
                                //move the next blocks' information up
                                for (int j = txtIndices.indexOf(v1); j < currentList.getnBlocks(); j++) {
                                    currentList.getBlocks().set(j, currentList.getBlocks().get(j + 1));
                                }
                                currentList.getBlocks().remove(currentList.getnBlocks());

                                //remove the last block
                                rows.get(currentList.getnBlocks()).setVisibility(View.GONE);
                                remove = false;

                                //update table
                                setTable();
                            }
                        });
                    }
                }
            });

            //display pop-up
            //makes pop-up height and width depend on the screen size
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels - 150;
            int width = displayMetrics.widthPixels - 150;
            final PopupWindow popupWindow = new PopupWindow(popup, width, height,true); //'focusable:true' allows to close pop-up by touch out of the pop-up window
            popupWindow.setElevation(200); //adds a shadow to the pop-up
            popupWindow.showAtLocation(recyclerView, Gravity.TOP, 0, 170);

            //close pop-up through the close button
            btnX.setOnClickListener(v -> {
                //close popup
                popupWindow.dismiss();
            });

            //update the template through the update button
            btnCreate.setOnClickListener(v -> {
                //check if everything is filled
                //title and number of people
                boolean allFilled = !editTxtTitle.getText().toString().equals("") && !Objects.isNull(editTxtTitle.getText().toString()) &&
                        !editTxtNumOfBlocks.getText().toString().equals("") && !Objects.isNull(editTxtNumOfBlocks.getText().toString()) &&
                        !editTxtNumOfBlocks.getText().toString().equals("0");
                //block information
                for (int i = 0; i < currentList.getnBlocks(); i++) {
                    if (editTxtPeople.get(i).getText().toString().equals("") || Objects.isNull(editTxtPeople.get(i).getText().toString()) ||
                            btnTimes.get(i).getText().toString().equals("") || Objects.isNull(btnTimes.get(i).getText().toString()) ||
                            btnTimes.get(i).getText().toString().equals(getResources().getString(R.string.popup_edit_template_btn_select))) {
                        allFilled = false;
                    }
                }

                if (allFilled) {
                    //update the database
                    currentList.setTitle(editTxtTitle.getText().toString());
                    //clear the existing record (if the list exists)
                    for (DutyList t : templates) {
                        if (t.getTitle().equals(currentList.getTitle())) {FirebaseDatabase.getInstance().getReference("Templates").child(prevTitle).removeValue();}
                    }
                    //write the new one
                    int n = currentList.getnBlocks();
                    FirebaseDatabase.getInstance().getReference("Templates").child(currentList.getTitle()).child("number_of_blocks").setValue(String.valueOf(n));
                    for (int i = 1; i <= n; i++) {
                        FirebaseDatabase.getInstance().getReference("Templates").child(currentList.getTitle()).child(String.valueOf(i)).child("people").
                                setValue(String.valueOf(currentList.getBlocks().get(i - 1).getnPeople()));
                        FirebaseDatabase.getInstance().getReference("Templates").child(currentList.getTitle()).child(String.valueOf(i)).child("time").
                                setValue(currentList.getBlocks().get(i - 1).getTime());
                    }
                    //close popup
                    popupWindow.dismiss();

                    //sort blocks by time (selection sort)
                    for (int round = 0; round < currentList.getBlocks().size() - 1; round++) {
                        String maxTime = currentList.getBlocks().get(0).getTime();
                        int maxIndex = 0;
                        for (int i = 0; i < currentList.getBlocks().size() - round; i++) {
                            if (laterThan(currentList.getBlocks().get(i).getTime(), maxTime)) {
                                maxTime = currentList.getBlocks().get(i).getTime();
                                maxIndex = i;
                            }
                        }
                        Block temp = currentList.getBlocks().get(currentList.getBlocks().size() - 1 - round);
                        currentList.getBlocks().set(currentList.getBlocks().size() - 1 - round, currentList.getBlocks().get(maxIndex));
                        currentList.getBlocks().set(maxIndex, temp);
                    }

                    templates.add(currentList);

                    //update the recycler view
                    activateRecyclerView();
                }
                else {Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();}
            });
        }

        //when the 'delete template' button is clicked:
        if (view == btnDelete) {
            delete = true;
            Toast.makeText(this, "Please click on the template you would like to permanently delete", Toast.LENGTH_SHORT).show();
        }
    }

    //method used to store the data from the template database
    public void databaseTask(DataSnapshot dataSnapshot) {
        //create DutyList objects based on the database and add the to the array
        for (DataSnapshot dsTemplate : dataSnapshot.getChildren()) {
            String title = dsTemplate.getKey();
            int nBlocks = Integer.parseInt(Objects.requireNonNull(dsTemplate.child("number_of_blocks").getValue(String.class)));
            ArrayList<Block> blocks = new ArrayList<>();

            for (int j = 1; j <=nBlocks; j++) {
                String time = dsTemplate.child(String.valueOf(j)).child("time").getValue(String.class);
                int nPeople = Integer.parseInt(Objects.requireNonNull(dsTemplate.child(String.valueOf(j)).child("people").getValue(String.class)));
                blocks.add(new Block(time, nPeople));
            }

            templates.add(new DutyList(title, nBlocks, blocks));
        }

        //display template data
        activateRecyclerView();
    }

    public void activateRecyclerView() {
        //recycler view
        recyclerView = findViewById(R.id.TemplatesRecyclerView);
        //templates array has to be set up before adapter
        adapter = new rvAdapterTemplates(this, templates, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loading.dismiss();
    }

    @Override
    public void onItemClick(Student student) {}

    //when a template is clicked
    @Override
    public void onItemClick(DutyList list) {
        currentList = list;
        prevTitle = list.getTitle();

        if (delete) {
            FirebaseDatabase.getInstance().getReference("Templates").child(prevTitle).removeValue();
            templates.remove(currentList);
            delete = false;
            //update the recycler view
            activateRecyclerView();
        }
        else {
            //open pop-up to edit template (from view/edit)
            if (from.equals("edit")) {
                //define the pop-up, close button, and TextView
                View popup = View.inflate(this, R.layout.popup_edit_template, null);
                Button btnX = popup.findViewById(R.id.popupEditTemplateBtnX);
                editTxtTitle = popup.findViewById(R.id.popupEditTemplateEditTxtTitle);
                editTxtTitle.setText(currentList.getTitle());
                editTxtNumOfBlocks = popup.findViewById(R.id.popupEditTemplateEditTxtNumOfBlocks);
                Button btnAdd = popup.findViewById(R.id.popupEditTemplateBtnAdd);
                Button btnRemove = popup.findViewById(R.id.popupEditTemplateBtnRemove);
                Button btnUpdate = popup.findViewById(R.id.popupEditTemplateBtnUpdate);
                rows.clear();
                rows.add(popup.findViewById(R.id.popupEditTemplateBlock1));
                rows.add(popup.findViewById(R.id.popupEditTemplateBlock2));
                rows.add(popup.findViewById(R.id.popupEditTemplateBlock3));
                rows.add(popup.findViewById(R.id.popupEditTemplateBlock4));
                rows.add(popup.findViewById(R.id.popupEditTemplateBlock5));
                rows.add(popup.findViewById(R.id.popupEditTemplateBlock6));
                rows.add(popup.findViewById(R.id.popupEditTemplateBlock7));
                rows.add(popup.findViewById(R.id.popupEditTemplateBlock8));
                rows.add(popup.findViewById(R.id.popupEditTemplateBlock9));
                rows.add(popup.findViewById(R.id.popupEditTemplateBlock10));
                txtIndices.clear();
                txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN1));
                txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN2));
                txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN3));
                txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN4));
                txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN5));
                txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN6));
                txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN7));
                txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN8));
                txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN9));
                txtIndices.add(popup.findViewById(R.id.popupEditTemplateBlockN10));
                btnTimes.clear();
                btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime1));
                btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime2));
                btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime3));
                btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime4));
                btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime5));
                btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime6));
                btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime7));
                btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime8));
                btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime9));
                btnTimes.add(popup.findViewById(R.id.popupEditTemplateBtnTime10));
                editTxtPeople.clear();
                editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN1));
                editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN2));
                editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN3));
                editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN4));
                editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN5));
                editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN6));
                editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN7));
                editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN8));
                editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN9));
                editTxtPeople.add(popup.findViewById(R.id.popupEditTemplateEditTxtN10));

                //display template data
                setTable();

                //add and remove button listeners
                btnAdd.setOnClickListener(v -> {
                    int n = currentList.getnBlocks();
                    if (n == 10) {
                        Toast.makeText(this, "A list cannot have more than 10 blocks", Toast.LENGTH_SHORT).show();
                    } else {
                        currentList.setnBlocks(n + 1);
                        rows.get(n).setVisibility(View.VISIBLE);

                        //change blocks array
                        currentList.getBlocks().add(new Block());

                        setTable();
                    }
                });
                btnRemove.setOnClickListener(v -> {
                    int n = currentList.getnBlocks();
                    if (n == 1) {
                        Toast.makeText(this, "A list has to have at least 1 block", Toast.LENGTH_SHORT).show();
                    } else {
                        remove = true;

                        currentList.setnBlocks(n - 1);
                        Toast.makeText(this, "Please click on the number of the block you would like to remove", Toast.LENGTH_SHORT).show();

                        //remove the chosen block, transferring all information up to keep the numbers in order
                        for (int i = 0; i < n; i++) {
                            txtIndices.get(i).setOnClickListener(v1 -> {
                                if (remove) {
                                    //move the next blocks' information up
                                    for (int j = txtIndices.indexOf(v1); j < currentList.getnBlocks(); j++) {
                                        currentList.getBlocks().set(j, currentList.getBlocks().get(j + 1));
                                    }
                                    currentList.getBlocks().remove(currentList.getnBlocks());

                                    //remove the last block
                                    rows.get(currentList.getnBlocks()).setVisibility(View.GONE);
                                    remove = false;

                                    //update table
                                    setTable();
                                }
                            });
                        }
                    }
                });

                //display pop-up
                //makes pop-up height and width depend on the screen size
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels - 150;
                int width = displayMetrics.widthPixels - 150;
                final PopupWindow popupWindow = new PopupWindow(popup, width, height, true); //'focusable:true' allows to close pop-up by touch out of the pop-up window
                popupWindow.setElevation(200); //adds a shadow to the pop-up
                popupWindow.showAtLocation(recyclerView, Gravity.TOP, 0, 170);

                //close pop-up through the close button
                btnX.setOnClickListener(v -> {
                    //close popup
                    popupWindow.dismiss();
                });

                //update the template through the update button
                btnUpdate.setOnClickListener(v -> {
                    //check if everything is filled
                    //title and number of people
                    boolean allFilled = !editTxtTitle.getText().toString().equals("") && !Objects.isNull(editTxtTitle.getText().toString()) &&
                            !editTxtNumOfBlocks.getText().toString().equals("") && !Objects.isNull(editTxtNumOfBlocks.getText().toString());
                    //block information
                    for (int i = 0; i < currentList.getnBlocks(); i++) {
                        if (editTxtPeople.get(i).getText().toString().equals("") || Objects.isNull(editTxtPeople.get(i).getText().toString()) ||
                                currentList.getBlocks().get(i).getnPeople() == 0 ||
                                btnTimes.get(i).getText().toString().equals("") || Objects.isNull(btnTimes.get(i).getText().toString()) ||
                                btnTimes.get(i).getText().toString().equals(getResources().getString(R.string.popup_edit_template_btn_select))) {
                            allFilled = false;
                        }
                    }

                    if (allFilled) {
                        //update the database
                        currentList.setTitle(editTxtTitle.getText().toString());
                        //clear the existing record
                        FirebaseDatabase.getInstance().getReference("Templates").child(prevTitle).removeValue();
                        //write the new one
                        int n = currentList.getnBlocks();
                        FirebaseDatabase.getInstance().getReference("Templates").child(currentList.getTitle()).child("number_of_blocks").setValue(String.valueOf(n));
                        for (int i = 1; i <= n; i++) {
                            FirebaseDatabase.getInstance().getReference("Templates").child(currentList.getTitle()).child(String.valueOf(i)).child("people").
                                    setValue(String.valueOf(currentList.getBlocks().get(i - 1).getnPeople()));
                            FirebaseDatabase.getInstance().getReference("Templates").child(currentList.getTitle()).child(String.valueOf(i)).child("time").
                                    setValue(currentList.getBlocks().get(i - 1).getTime());
                        }
                        //close popup
                        popupWindow.dismiss();

                        //sort blocks by time (selection sort)
                        for (int round = 0; round < currentList.getBlocks().size() - 1; round++) {
                            String maxTime = currentList.getBlocks().get(0).getTime();
                            int maxIndex = 0;
                            for (int i = 0; i < currentList.getBlocks().size() - round; i++) {
                                if (laterThan(currentList.getBlocks().get(i).getTime(), maxTime)) {
                                    maxTime = currentList.getBlocks().get(i).getTime();
                                    maxIndex = i;
                                }
                            }
                            Block temp = currentList.getBlocks().get(currentList.getBlocks().size() - 1 - round);
                            currentList.getBlocks().set(currentList.getBlocks().size() - 1 - round, currentList.getBlocks().get(maxIndex));
                            currentList.getBlocks().set(maxIndex, temp);
                        }

                        //update the recycler view
                        activateRecyclerView();
                    }
                    else {Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();}
                });
            }


            //use the template to create a list (from info)
            if (from.equals("info")) {
                nBlocks = list.getnBlocks();
                String[] times = new String[nBlocks];
                int[] nPeoples = new int[nBlocks];
                for (int i = 0; i < nBlocks; i++) {
                    times[i] = list.getBlocks().get(i).getTime();
                    nPeoples[i] = list.getBlocks().get(i).getnPeople();
                }

                //check which grade and locals/non-locals need to be included
                View popup = View.inflate(this, R.layout.popup_use_template, null);
                Button btnX = popup.findViewById(R.id.popupUseTemplateBtnX);
                Button btnContinue = popup.findViewById(R.id.popupUseTemplateBtnContinue);
                CheckBox[][] checkBoxes = setCheckBoxes(popup);

                //set times
                TextView[] txtTimes = new TextView[10];
                txtTimes[0] = popup.findViewById(R.id.popupUseTemplateTxtTime1);
                txtTimes[1] = popup.findViewById(R.id.popupUseTemplateTxtTime2);
                txtTimes[2] = popup.findViewById(R.id.popupUseTemplateTxtTime3);
                txtTimes[3] = popup.findViewById(R.id.popupUseTemplateTxtTime4);
                txtTimes[4] = popup.findViewById(R.id.popupUseTemplateTxtTime5);
                txtTimes[5] = popup.findViewById(R.id.popupUseTemplateTxtTime6);
                txtTimes[6] = popup.findViewById(R.id.popupUseTemplateTxtTime7);
                txtTimes[7] = popup.findViewById(R.id.popupUseTemplateTxtTime8);
                txtTimes[8] = popup.findViewById(R.id.popupUseTemplateTxtTime9);
                txtTimes[9] = popup.findViewById(R.id.popupUseTemplateTxtTime10);
                for (int t = 0; t < nBlocks; t++) {
                    txtTimes[t].setText(times[t]);
                }

                //hide unnecessary columns
                LinearLayout[] layouts = new LinearLayout[10];
                layouts[0] = popup.findViewById(R.id.popupUseTemplateLayoutCol1);
                layouts[1] = popup.findViewById(R.id.popupUseTemplateLayoutCol2);
                layouts[2] = popup.findViewById(R.id.popupUseTemplateLayoutCol3);
                layouts[3] = popup.findViewById(R.id.popupUseTemplateLayoutCol4);
                layouts[4] = popup.findViewById(R.id.popupUseTemplateLayoutCol5);
                layouts[5] = popup.findViewById(R.id.popupUseTemplateLayoutCol6);
                layouts[6] = popup.findViewById(R.id.popupUseTemplateLayoutCol7);
                layouts[7] = popup.findViewById(R.id.popupUseTemplateLayoutCol8);
                layouts[8] = popup.findViewById(R.id.popupUseTemplateLayoutCol9);
                layouts[9] = popup.findViewById(R.id.popupUseTemplateLayoutCol10);
                for (int l = nBlocks; l < 10; l++) {
                    layouts[l].setVisibility(View.GONE);
                }

                //display pop-up
                //makes pop-up height depend on content and width depend on the screen size
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                int width = displayMetrics.widthPixels - 150;
                final PopupWindow popupWindow = new PopupWindow(popup, width, height, true); //'focusable:true' allows to close pop-up by touch out of the pop-up window
                popupWindow.setElevation(200); //adds a shadow to the pop-up
                popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);

                //close popup through button
                btnX.setOnClickListener(v -> popupWindow.dismiss());

                //move on through button
                btnContinue.setOnClickListener(v -> {
                    //initialize boolean arrays
                    boolean[] dp2s = new boolean[nBlocks];
                    boolean[] dp1s = new boolean[nBlocks];
                    boolean[] predps = new boolean[nBlocks];
                    boolean[] locals = new boolean[nBlocks];
                    boolean[] nonLocals = new boolean[nBlocks];

                    boolean allBlocks = true;

                    for (int i = 0; i < nBlocks; i++) {
                        dp2s[i] = checkBoxes[0][i].isChecked();
                        dp1s[i] = checkBoxes[1][i].isChecked();
                        predps[i] = checkBoxes[2][i].isChecked();
                        locals[i] = checkBoxes[3][i].isChecked();
                        nonLocals[i] = checkBoxes[4][i].isChecked();

                        if (!(dp2s[i] || dp1s[i] || predps[i]) || !(locals[i] || nonLocals[i])) {
                            allBlocks = false;
                        }
                    }

                    if (allBlocks) {
                        Intent intent = new Intent(this, ActivitySorting.class);
                        //pass on data to the next activity
                        intent.putExtra("type", type);
                        intent.putExtra("date", date);
                        intent.putExtra("nBlocks", nBlocks);
                        intent.putExtra("title", "");
                        intent.putExtra("times", times);
                        intent.putExtra("nPeoples", nPeoples);
                        intent.putExtra("predps", predps);
                        intent.putExtra("dp1s", dp1s);
                        intent.putExtra("dp2s", dp2s);
                        intent.putExtra("locals", locals);
                        intent.putExtra("nonlocals", nonLocals);
                        intent.putExtra("went back", false); //to show that the user didn't come back from dutyList created

                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "At least 1 grade and local/non-local has to be chosen for each block", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    //method to update the popup (edit template)
    public void setTable() {
        editTxtNumOfBlocks.setText(String.valueOf(currentList.getnBlocks()));
        editTxtNumOfBlocks.setOnKeyListener((v, keyCode, event) -> { //sets what happens when the 'enter' key is pressed
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                //when key is pressed
                int input = Integer.parseInt(editTxtNumOfBlocks.getText().toString());

                //check if the number of blocks is in the allowed range
                if (input > 10 || input < 1) {
                    Toast.makeText(this, "Number of blocks out of range", Toast.LENGTH_SHORT).show(); //informs the user that the input is out of range
                    editTxtNumOfBlocks.setText(String.valueOf(currentList.getnBlocks()), TextView.BufferType.EDITABLE); //removes the text input
                }
                else {
                    //create new blocks or delete extra blocks
                    if (input > currentList.getnBlocks()) { //blocks need to be added
                        for (int i = currentList.getnBlocks(); i < input; i++) {
                            rows.get(i).setVisibility(View.VISIBLE);
                            //change blocks array
                            currentList.getBlocks().add(new Block());
                            currentList.setnBlocks(input);
                        }
                        setTable();
                    }
                    if (input < currentList.getnBlocks()) { //blocks need to be removed
                        for (int i = currentList.getnBlocks() - 1; i >= input; i--) {
                            Toast.makeText(this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                            currentList.getBlocks().remove(i);
                            rows.get(i).setVisibility(View.GONE);
                            currentList.setnBlocks(input);
                        }
                        setTable();
                    }
                }
                return true;
            }
            return false;
        });
        for (int i = 0; i < currentList.getnBlocks(); i++) {
            rows.get(i).setVisibility(View.VISIBLE);
            btnTimes.get(i).setText(currentList.getBlocks().get(i).getTime());
            if (currentList.getBlocks().get(i).getnPeople() == 0) {editTxtPeople.get(i).setText("");}
            else {editTxtPeople.get(i).setText(String.valueOf(currentList.getBlocks().get(i).getnPeople()));}
        }

        //listeners number of people edit texts
        for (int i = 0; i < currentList.getnBlocks(); i++) {
            editTxtPeople.get(i).setOnKeyListener((v, keyCode, event) -> { //sets what happens when the 'enter' key is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //when key is pressed
                    int input = Integer.parseInt(editTxtPeople.get(editTxtPeople.indexOf(v)).getText().toString());

                    //check if the input is withing the given range (1-20)
                    if (input > 20 || input < 1) { //the input is out of range
                        Toast.makeText(this, "Number of people out of range", Toast.LENGTH_SHORT).show(); //informs the user that the input is out of range
                        editTxtPeople.get(editTxtPeople.indexOf(v)).setText(null, TextView.BufferType.EDITABLE); //removes the text input
                        currentList.getBlocks().get(editTxtPeople.indexOf(v)).setnPeople(0); //resets the number of people
                    }
                    else { //the input is in range
                        currentList.getBlocks().get(editTxtPeople.indexOf(v)).setnPeople(input); //sets the number of people
                    }
                    return true;
                }
                return false;
            });
        }
    }

    //method for the time button
    public void openTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, chosenHour, chosenMin) -> {
            //get the chosen time from the time picker
            hour = chosenHour;
            minute = chosenMin;
            //format the selected time to set as the time button text
            String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            btnTimes.get(btnTimes.indexOf(view)).setText(time);
            currentList.getBlocks().get(btnTimes.indexOf(view)).setTime(time);
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show(); //opens a clock where the user can choose the time
    }

    //compare times to find the earlier block
    public boolean laterThan(String t1, String t2) {
        String[] a1 = t1.split(":");
        String[] a2 = t2.split(":");
        if (Integer.parseInt(a1[0]) > Integer.parseInt(a2[0])) {return true;}
        if (Integer.parseInt(a1[0]) == Integer.parseInt(a2[0])) {return Integer.parseInt(a1[1]) > Integer.parseInt(a2[1]);}
        return false;
    }

    //create a 2D array of checkboxes to use the template
    public CheckBox[][] setCheckBoxes(View popup) {
        CheckBox[][] checkBoxes = new CheckBox[5][10];
        //dp2s
        checkBoxes[0][0] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP21);
        checkBoxes[0][1] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP22);
        checkBoxes[0][2] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP23);
        checkBoxes[0][3] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP24);
        checkBoxes[0][4] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP25);
        checkBoxes[0][5] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP26);
        checkBoxes[0][6] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP27);
        checkBoxes[0][7] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP28);
        checkBoxes[0][8] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP29);
        checkBoxes[0][9] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP210);
        //dp2s
        checkBoxes[1][0] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP11);
        checkBoxes[1][1] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP12);
        checkBoxes[1][2] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP13);
        checkBoxes[1][3] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP14);
        checkBoxes[1][4] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP15);
        checkBoxes[1][5] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP16);
        checkBoxes[1][6] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP17);
        checkBoxes[1][7] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP18);
        checkBoxes[1][8] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP19);
        checkBoxes[1][9] = popup.findViewById(R.id.popupUseTemplateCheckBoxDP110);
        //pre-dps
        checkBoxes[2][0] = popup.findViewById(R.id.popupUseTemplateCheckBoxPreDP1);
        checkBoxes[2][1] = popup.findViewById(R.id.popupUseTemplateCheckBoxPreDP2);
        checkBoxes[2][2] = popup.findViewById(R.id.popupUseTemplateCheckBoxPreDP3);
        checkBoxes[2][3] = popup.findViewById(R.id.popupUseTemplateCheckBoxPreDP4);
        checkBoxes[2][4] = popup.findViewById(R.id.popupUseTemplateCheckBoxPreDP5);
        checkBoxes[2][5] = popup.findViewById(R.id.popupUseTemplateCheckBoxPreDP6);
        checkBoxes[2][6] = popup.findViewById(R.id.popupUseTemplateCheckBoxPreDP7);
        checkBoxes[2][7] = popup.findViewById(R.id.popupUseTemplateCheckBoxPreDP8);
        checkBoxes[2][8] = popup.findViewById(R.id.popupUseTemplateCheckBoxPreDP9);
        checkBoxes[2][9] = popup.findViewById(R.id.popupUseTemplateCheckBoxPreDP10);
        //locals
        checkBoxes[3][0] = popup.findViewById(R.id.popupUseTemplateCheckBoxLocals1);
        checkBoxes[3][1] = popup.findViewById(R.id.popupUseTemplateCheckBoxLocals2);
        checkBoxes[3][2] = popup.findViewById(R.id.popupUseTemplateCheckBoxLocals3);
        checkBoxes[3][3] = popup.findViewById(R.id.popupUseTemplateCheckBoxLocals4);
        checkBoxes[3][4] = popup.findViewById(R.id.popupUseTemplateCheckBoxLocals5);
        checkBoxes[3][5] = popup.findViewById(R.id.popupUseTemplateCheckBoxLocals6);
        checkBoxes[3][6] = popup.findViewById(R.id.popupUseTemplateCheckBoxLocals7);
        checkBoxes[3][7] = popup.findViewById(R.id.popupUseTemplateCheckBoxLocals8);
        checkBoxes[3][8] = popup.findViewById(R.id.popupUseTemplateCheckBoxLocals9);
        checkBoxes[3][9] = popup.findViewById(R.id.popupUseTemplateCheckBoxLocals10);
        //non-locals
        checkBoxes[4][0] = popup.findViewById(R.id.popupUseTemplateCheckBoxNonLocals1);
        checkBoxes[4][1] = popup.findViewById(R.id.popupUseTemplateCheckBoxNonLocals2);
        checkBoxes[4][2] = popup.findViewById(R.id.popupUseTemplateCheckBoxNonLocals3);
        checkBoxes[4][3] = popup.findViewById(R.id.popupUseTemplateCheckBoxNonLocals4);
        checkBoxes[4][4] = popup.findViewById(R.id.popupUseTemplateCheckBoxNonLocals5);
        checkBoxes[4][5] = popup.findViewById(R.id.popupUseTemplateCheckBoxNonLocals6);
        checkBoxes[4][6] = popup.findViewById(R.id.popupUseTemplateCheckBoxNonLocals7);
        checkBoxes[4][7] = popup.findViewById(R.id.popupUseTemplateCheckBoxNonLocals8);
        checkBoxes[4][8] = popup.findViewById(R.id.popupUseTemplateCheckBoxNonLocals9);
        checkBoxes[4][9] = popup.findViewById(R.id.popupUseTemplateCheckBoxNonLocals10);

        return checkBoxes;
    }
}