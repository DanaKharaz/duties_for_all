package android.application.duties_for_all;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duties_for_all.R;

import java.util.ArrayList;

public class rvAdapterTemplates extends RecyclerView.Adapter<rvAdapterTemplates.MyViewHolder> {
    //define everything
    private static rvInterface rvInterface = null;
    private final Context context;
    protected ArrayList<DutyList> lists;

    //constructor
    public rvAdapterTemplates(Context context, ArrayList<DutyList> lists, rvInterface rvInterface){
        this.context = context;
        this.lists = lists;
        rvAdapterTemplates.rvInterface = rvInterface;
    }

    @NonNull
    @Override
    public rvAdapterTemplates.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //this is where the layout is inflated (giving a specific look to the rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rv_row_templates, parent, false);

        return new rvAdapterTemplates.MyViewHolder(view, rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull rvAdapterTemplates.MyViewHolder holder, int position) {
        //assigning values to views created in the row layout based on position of the recycler view
        DutyList list = lists.get(position);
        holder.bind(list); //use bind method instead of setting text directly to allow accurate searching
    }

    @Override
    public int getItemCount() {
        //recycler view gets the number of items that needs to be displayed
        return lists.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        //getting views from the row layout
        //(similar to onCreate method)

        //define everything
        //private final TextView tvName;
        private final TextView title;
        private final ArrayList<TextView> times = new ArrayList<>();
        private final ArrayList<TextView> nPeoples = new ArrayList<>();
        private final ArrayList<LinearLayout> layouts = new ArrayList<>();
        private DutyList currentList; //allows view holder keep reference to the bound item

        public MyViewHolder(@NonNull View itemView, rvInterface rvInterface) {
            super(itemView);
            //set views and listeners
            title = itemView.findViewById(R.id.rowTemplatesTxtTitle);
            times.add(itemView.findViewById(R.id.rowTemplatesTxtTime1));
            nPeoples.add(itemView.findViewById(R.id.rowTemplatesTxtN1));
            layouts.add(itemView.findViewById(R.id.rowTemplatesLayout1));
            times.add(itemView.findViewById(R.id.rowTemplatesTxtTime2));
            nPeoples.add(itemView.findViewById(R.id.rowTemplatesTxtN2));
            layouts.add(itemView.findViewById(R.id.rowTemplatesLayout2));
            times.add(itemView.findViewById(R.id.rowTemplatesTxtTime3));
            nPeoples.add(itemView.findViewById(R.id.rowTemplatesTxtN3));
            layouts.add(itemView.findViewById(R.id.rowTemplatesLayout3));
            times.add(itemView.findViewById(R.id.rowTemplatesTxtTime4));
            nPeoples.add(itemView.findViewById(R.id.rowTemplatesTxtN4));
            layouts.add(itemView.findViewById(R.id.rowTemplatesLayout4));
            times.add(itemView.findViewById(R.id.rowTemplatesTxtTime5));
            nPeoples.add(itemView.findViewById(R.id.rowTemplatesTxtN5));
            layouts.add(itemView.findViewById(R.id.rowTemplatesLayout5));
            times.add(itemView.findViewById(R.id.rowTemplatesTxtTime6));
            nPeoples.add(itemView.findViewById(R.id.rowTemplatesTxtN6));
            layouts.add(itemView.findViewById(R.id.rowTemplatesLayout6));
            times.add(itemView.findViewById(R.id.rowTemplatesTxtTime7));
            nPeoples.add(itemView.findViewById(R.id.rowTemplatesTxtN7));
            layouts.add(itemView.findViewById(R.id.rowTemplatesLayout7));
            times.add(itemView.findViewById(R.id.rowTemplatesTxtTime8));
            nPeoples.add(itemView.findViewById(R.id.rowTemplatesTxtN8));
            layouts.add(itemView.findViewById(R.id.rowTemplatesLayout8));
            times.add(itemView.findViewById(R.id.rowTemplatesTxtTime9));
            nPeoples.add(itemView.findViewById(R.id.rowTemplatesTxtN9));
            layouts.add(itemView.findViewById(R.id.rowTemplatesLayout9));
            times.add(itemView.findViewById(R.id.rowTemplatesTxtTime10));
            nPeoples.add(itemView.findViewById(R.id.rowTemplatesTxtN10));
            layouts.add(itemView.findViewById(R.id.rowTemplatesLayout10));
            itemView.setOnClickListener(this);
        }

        void bind(DutyList list) { //allows view holder bind data being displayed
            title.setText(list.getTitle());
            for (int i = 0; i < list.getnBlocks(); i++) {
                layouts.get(i).setVisibility(View.VISIBLE);
                times.get(i).setText(list.getBlocks().get(i).getTime());
                nPeoples.get(i).setText(String.valueOf(list.getBlocks().get(i).getnPeople()));
            }

            currentList = list; //keep reference to the current list (template)
        }

        @Override
        public void onClick(View v) {
            if (rvInterface != null) {
                //pass current list (template) to activity
                rvInterface.onItemClick(currentList);
            }
        }
    }
}
