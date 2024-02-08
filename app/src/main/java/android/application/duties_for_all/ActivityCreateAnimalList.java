package android.application.duties_for_all;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duties_for_all.R;

public class ActivityCreateAnimalList extends AppCompatActivity implements View.OnClickListener {



    //What is done in this activity:
    //the user chooses which type of animal duty list they want to create (cow, chicken, visitor center)



    //define all buttons (here so that they can be used in the onClick method)
    Button btnCow;
    Button btnChicken;
    Button btnVisitorCenter;
    Button btnHelp;
    Button btnGoBack;

    //when the activity is started:
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_animal_list);

        //set onClick listeners to all buttons
        btnCow = findViewById(R.id.CreateAnimalListBtnCow);
        btnCow.setOnClickListener(this);
        btnChicken = findViewById(R.id.CreateAnimalListBtnChicken);
        btnChicken.setOnClickListener(this);
        btnVisitorCenter = findViewById(R.id.CreateAnimalListBtnVisitorCenter);
        btnVisitorCenter.setOnClickListener(this);
        btnHelp = findViewById(R.id.CreateAnimalListBtnHelp);
        btnHelp.setOnClickListener(this);
        btnGoBack = findViewById(R.id.CreateAnimalListBtnGoBack);
        btnGoBack.setOnClickListener(this);
    }
    //onCreate ends

    //when the buttons with onClick listeners are clicked:
    @Override
    public void onClick(View view) {
        int type; //used to pass on the type of the dutyList

        //when the 'Cow' button is clicked:
        if (view == btnCow) {
            type = 1; //1 means cow

            //go to 'list info 1' activity
            Intent intent = new Intent(this, ActivityListInfo.class);
            intent.putExtra("type", type); //pass on type
            startActivity(intent);
        }

        //when the 'chicken' button is clicked:
        if (view == btnChicken) {
            type = 2; //2 means chicken

            //go to 'list info 1' activity
            Intent intent = new Intent(this, ActivityListInfo.class);
            intent.putExtra("type", type); //pass on type
            startActivity(intent);
        }

        //when the 'visitor center' button is clicked:
        if (view == btnVisitorCenter) {
            type = 3; //3 means visitor center

            //go to 'list info 1' activity
            Intent intent = new Intent(this, ActivityListInfo.class);
            intent.putExtra("type", type); //pass on type
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

        //when the 'go back' button is clicked:
        if (view == btnGoBack) {
            //return to 'create list' activity
            Intent intent = new Intent(this, ActivityCreateList.class);
            startActivity(intent);
        }
    }
}