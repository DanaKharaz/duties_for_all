package android.application.duties_for_all;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duties_for_all.R;

public class ActivityCreateList extends AppCompatActivity implements View.OnClickListener {



    //What is done in this activity:
    //the user chooses which type of duty dutyList they want to create (dining hall, animal, other)



    //define all buttons (here so that they can be used in the onClick method)
    Button btnDiningHall;
    Button btnAnimal;
    Button btnOther;
    Button btnHelp;
    Button btnGoBack;

    //when the activity is started:
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        //set onClick listeners to all buttons
        btnDiningHall = findViewById(R.id.CreateListBtnDiningHall);
        btnDiningHall.setOnClickListener(this);
        btnAnimal = findViewById(R.id.CreateListBtnAnimal);
        btnAnimal.setOnClickListener(this);
        btnOther = findViewById(R.id.CreateListBtnOther);
        btnOther.setOnClickListener(this);
        btnHelp = findViewById(R.id.CreateListBtnHelp);
        btnHelp.setOnClickListener(this);
        btnGoBack = findViewById(R.id.CreateListBtnGoBack);
        btnGoBack.setOnClickListener(this);
    }
    //onCreate ends

    //when the buttons with onClick listeners are clicked:
    @Override
    public void onClick(View view) {
        int type; //used to pass on the type of the dutyList

        //when the 'dining hall' button is clicked:
        if (view == btnDiningHall) {
            type = 0; //0 means dining hall

            //go to 'dutyList info 1' activity
            Intent intent = new Intent(this, ActivityListInfo.class);
            intent.putExtra("type", type); //pass on type
            startActivity(intent);
        }

        //when the 'animal' button is clicked:
        if (view == btnAnimal) {
            //type here is not assigned as it will be done in the 'create animal list' activity

            //go to 'create animal dutyList' activity
            Intent intent = new Intent(this, ActivityCreateAnimalList.class);
            startActivity(intent);
        }

        //when the 'other' button is clicked:
        if (view == btnOther) {
            type = 4; //4 means other

            //go to 'list info' activity
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
            //return to 'home' activity
            Intent intent = new Intent(this, ActivityHome.class);
            startActivity(intent);
        }
    }
}