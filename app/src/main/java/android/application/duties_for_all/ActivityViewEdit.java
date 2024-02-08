package android.application.duties_for_all;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.duties_for_all.R;

public class ActivityViewEdit extends AppCompatActivity implements View.OnClickListener {



    //What is done in this activity:
    //the user chooses to view or edit student data, templates, or existing lists



    //define all buttons (here so that they can be used in the onClick method)
    Button btnStudents;
    Button btnLists;
    Button btnTemplates;
    Button btnGoBack;
    Button btnHelp;

    //when the activity is started:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit);

        //set onClick listeners to all buttons
        btnStudents = findViewById(R.id.ViewEditBtnStudents);
        btnStudents.setOnClickListener(this);
        btnLists = findViewById(R.id.ViewEditBtnLists);
        btnLists.setOnClickListener(this);
        btnTemplates = findViewById(R.id.ViewEditBtnTemplates);
        btnTemplates.setOnClickListener(this);
        btnGoBack = findViewById(R.id.ViewEditBtnGoBack);
        btnGoBack.setOnClickListener(this);
        btnHelp = findViewById(R.id.ViewEditBtnHelp);
        btnHelp.setOnClickListener(this);
    }
    //onCreate ends

    //when the buttons with onClick listeners are clicked:
    @Override
    public void onClick(View view) {
        //when the 'students' button is clicked:
        if (view == btnStudents) {
            //go to 'check student data' activity
            Intent intent = new Intent(this, ActivityShowStudentData.class);
            startActivity(intent);
        }

        //when the 'lists' button is clicked:
        if (view == btnLists) {
            //go to 'view list data' activity
            Intent intent = new Intent(this, ActivityEditing.class);
            startActivity(intent);
        }

        //when the 'templates' button is clicked:
        if (view == btnTemplates) {
            //go to 'templates' activity
            Intent intent = new Intent(this, ActivityTemplates.class);
            //indicate origin
            intent.putExtra("from", "edit");

            startActivity(intent);
        }

        //when the 'go back' button is clicked:
        if (view == btnGoBack) {
            //go back to 'home' activity
            Intent intent = new Intent(this, ActivityHome.class);
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
}