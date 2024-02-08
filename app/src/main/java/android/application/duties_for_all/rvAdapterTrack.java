package android.application.duties_for_all;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duties_for_all.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class rvAdapterTrack extends RecyclerView.Adapter<rvAdapterTrack.MyViewHolder> {
    //define everything
    private final Context context;
    protected ArrayList<Student> students;
    private final ArrayList<ArrayList<Boolean>> onCampus;
    ArrayList<Boolean> checkIn;
    ArrayList<Date> days;
    String grade;

    //constructor
    public rvAdapterTrack(Context context, ArrayList<Student> students, ArrayList<ArrayList<Boolean>> onCampus, ArrayList<Boolean> checkIn, ArrayList<Date> days, String grade){
        this.context = context;
        this.students = students;
        this.onCampus = new ArrayList<>();
        this.onCampus.addAll(onCampus);
        this.checkIn = new ArrayList<>();
        this.checkIn.addAll(checkIn);
        this.days = new ArrayList<>();
        this.days.addAll(days);
        this.grade = grade;
    }

    @NonNull
    @Override
    public rvAdapterTrack.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //this is where the layout is inflated (giving a specific look to the rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rv_row_track, parent, false);

        return new rvAdapterTrack.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull rvAdapterTrack.MyViewHolder holder, int position) {
        //assigning values to views created in the row layout based on position of the recycler view
        Student student = students.get(position);
        holder.bind(student, position, onCampus, checkIn, days); //use bind method instead of setting text directly to allow accurate searching
    }

    @Override
    public int getItemCount() {
        //recycler view gets the number of items that needs to be displayed
        return students.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //getting views from the row layout
        //(similar to onCreate method)

        //define everything
        //private final TextView tvName;
        private final TextView txtName;
        ArrayList<TextView> txtDays = new ArrayList<>();

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //set views and listeners
            txtName = itemView.findViewById(R.id.rowTrackTxtName);
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay1));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay2));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay3));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay4));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay5));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay6));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay7));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay8));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay9));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay10));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay11));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay12));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay13));
            txtDays.add(itemView.findViewById(R.id.rowTrackTxtDay14));
        }

        void bind(Student student, int position, ArrayList<ArrayList<Boolean>> onCampus, ArrayList<Boolean> checkIn, ArrayList<Date> days) { //allows view holder bind data being displayed
            //set student name
            txtName.setText(student.getFullName());
            if (checkIn.get(position)) {txtName.setTextColor(itemView.getContext().getResources().getColor(R.color.background_color));}
            else {txtName.setTextColor(itemView.getContext().getResources().getColor(R.color.text_color));}

            //display student's presence
            for (int i = 0; i < 14; i++) {
                TextView currentTxt = txtDays.get(i);
                Drawable drawable;
                if (onCampus.get(i).get(position)) {
                    drawable = ResourcesCompat.getDrawable(itemView.getContext().getResources(), R.drawable.table_cell_background, null);
                }
                else {
                    drawable = ResourcesCompat.getDrawable(itemView.getContext().getResources(), R.drawable.table_cell_track_background, null);
                }
                currentTxt.setBackground(drawable);
            }

            txtName.setOnClickListener(v -> {
                if (checkIn.get(position)) {
                    txtName.setTextColor(itemView.getContext().getResources().getColor(R.color.text_color));
                    checkIn.set(position, false);
                }
                else {
                    txtName.setTextColor(itemView.getContext().getResources().getColor(R.color.background_color));
                    checkIn.set(position, true);
                }

                syncDatabase(student, position, onCampus, checkIn, days);
            });
            for (int i = 0; i < 14; i++) {
                txtDays.get(i).setOnClickListener(v -> {
                    TextView currentTxt = itemView.findViewById(v.getId());
                    Drawable drawable;
                    if (onCampus.get(txtDays.indexOf(currentTxt)).get(position)) {
                        drawable = ResourcesCompat.getDrawable(itemView.getContext().getResources(), R.drawable.table_cell_track_background, null);
                        onCampus.get(txtDays.indexOf(currentTxt)).set(position, false);
                    }
                    else {
                        drawable = ResourcesCompat.getDrawable(itemView.getContext().getResources(), R.drawable.table_cell_background, null);
                        onCampus.get(txtDays.indexOf(currentTxt)).set(position, true);
                    }
                    currentTxt.setBackground(drawable);

                    syncDatabase(student, position, onCampus, checkIn, days);
                });
            }
            //allows view holder keep reference to the bound item
        }

        //sync all changes with the database
        public void syncDatabase(Student student, int position, ArrayList<ArrayList<Boolean>> onCampus, ArrayList<Boolean> checkIn, ArrayList<Date> days) {
            //sync check in
            Date date = days.get(0);
            if (checkIn.get(position)) {
                FirebaseDatabase.getInstance().getReference("Check_In").child(date.toString()).
                        child(student.getId()).setValue("1");
            }
            else {
                FirebaseDatabase.getInstance().getReference("Check_In").child(date.toString()).
                        child(student.getId()).setValue("0");
            }

            //sync on campus
            for (int i = 0; i < 14; i++) {
                if (onCampus.get(i).get(position)) {
                    FirebaseDatabase.getInstance().getReference("On_Campus").child(days.get(i).toString()).
                            child(student.getId()).setValue("1");
                }
                else {
                    FirebaseDatabase.getInstance().getReference("On_Campus").child(days.get(i).toString()).
                            child(student.getId()).setValue("0");
                }
            }
        }
    }
}
