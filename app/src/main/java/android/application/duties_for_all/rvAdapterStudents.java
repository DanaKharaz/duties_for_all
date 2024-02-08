package android.application.duties_for_all;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duties_for_all.R;

import java.util.ArrayList;

public class rvAdapterStudents extends RecyclerView.Adapter<rvAdapterStudents.MyViewHolder> {
    //define everything
    private static rvInterface rvInterface = null;
    private final Context context;
    protected ArrayList<Student> students;

    //constructor
    public rvAdapterStudents(Context context, ArrayList<Student> students, rvInterface rvInterface){
        this.context = context;
        this.students = students;
        rvAdapterStudents.rvInterface = rvInterface;
    }

    @NonNull
    @Override
    public rvAdapterStudents.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //this is where the layout is inflated (giving a specific look to the rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rv_row_students, parent, false);

        return new rvAdapterStudents.MyViewHolder(view, rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull rvAdapterStudents.MyViewHolder holder, int position) {
        //assigning values to views created in the row layout based on position of the recycler view
        Student student = students.get(position);
        holder.bind(student); //use bind method instead of setting text directly to allow accurate searching
    }

    @Override
    public int getItemCount() {
        //recycler view gets the number of items that needs to be displayed
        return students.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        //getting views from the row layout
        //(similar to onCreate method)

        //define everything
        //private final TextView tvName;
        private final TextView name;
        private final TextView grade;
        private final TextView email;
        private final TextView local;
        private final TextView onCampus;
        private final TextView numberDone;
        private final TextView restrictions;
        private final TextView latestDone;
        private Student currentStudent; //allows view holder keep reference to the bound item

        public MyViewHolder(@NonNull View itemView, rvInterface rvInterface) {
            super(itemView);
            //set views and listeners
            name = itemView.findViewById(R.id.rowViewTxtName);
            grade = itemView.findViewById(R.id.rowViewTxtGrade);
            email = itemView.findViewById(R.id.rowViewTxtEmail);
            local = itemView.findViewById(R.id.rowViewTxtLocal);
            onCampus = itemView.findViewById(R.id.rowViewTxtOnCampus);
            numberDone = itemView.findViewById(R.id.rowViewTxtNumberDone);
            restrictions = itemView.findViewById(R.id.rowViewTxtRestrictions);
            latestDone = itemView.findViewById(R.id.rowViewTxtLatestDone);
            itemView.setOnClickListener(this);
        }

        void bind(Student student) { //allows view holder bind data being displayed
            name.setText(student.getFullName());
            if (student.getGrade().equals("predp")) {grade.setText(R.string.pre_dp);}
            if (student.getGrade().equals("dp1")) {grade.setText(R.string.dp1);}
            if (student.getGrade().equals("dp2")) {grade.setText(R.string.dp2);}
            email.setText(student.getEmail());
            String localYesNo;
            if (student.isLocal()) {localYesNo = "Yes";}
            else {localYesNo = "No";}
            local.setText(localYesNo);
            String onCampusYesNo;
            if (student.isOnCampus()) {onCampusYesNo = "Yes";}
            else {onCampusYesNo = "No";}
            onCampus.setText(onCampusYesNo);
            numberDone.setText(String.valueOf(student.getNumberDone()));
            String restrictedFrom;
            if (!student.allowCow() && !student.allowChicken() && !student.allowVisitorCenter()) {restrictedFrom = "all animal duties";}
            else if (student.allowCow() && student.allowChicken() && !student.allowVisitorCenter()) {restrictedFrom = "visitor center";}
            else if (student.allowCow() && !student.allowChicken() && student.allowVisitorCenter()) {restrictedFrom = "chicken";}
            else if (!student.allowCow() && student.allowChicken() && student.allowVisitorCenter()) {restrictedFrom = "cow";}
            else if (!student.allowCow() && !student.allowChicken() && student.allowVisitorCenter()) {restrictedFrom = "cow, chicken";}
            else if (!student.allowCow() && student.allowChicken() && !student.allowVisitorCenter()) {restrictedFrom = "cow, visitor center";}
            else if (student.allowCow() && !student.allowChicken() && !student.allowVisitorCenter()) {restrictedFrom = "chicken, visitor center";}
            else {restrictedFrom = "none";}
            restrictions.setText(restrictedFrom);
            latestDone.setText(student.getLatestDone().toString());


            currentStudent = student; //keep reference to the current student
        }

        @Override
        public void onClick(View v) {
            if (rvInterface != null) {
                //pass current student to activity
                rvInterface.onItemClick(currentStudent);
            }
        }
    }

    //change dutyList to filtered dutyList based on user input (filtering happens in ActivityAddExtra activity)
    public void  setFilteredList(ArrayList<Student> filteredList) {
        this.students = filteredList;
        notifyDataSetChanged();
    }
}