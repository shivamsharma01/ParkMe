package com.android.parkme.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.parkme.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class FindSlotsFragment extends Fragment {
    private static final String TAG = "FindSlotsFragment";
    RecyclerView recyclerView;
    ArrayList<String> SlotsNumbers;
    ArrayList<String> SlotsAvailability;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_slots, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        SlotsNumbers = new ArrayList<>();
        Collections.addAll(SlotsNumbers, getResources().getStringArray(R.array.array_slots));

        SlotsAvailability = new ArrayList<>();
        Collections.addAll(SlotsAvailability, getResources().getStringArray(R.array.array_slots_availability));

        RecyclerAdaptor recyclerAdaptor = new RecyclerAdaptor(getContext(), SlotsNumbers, SlotsAvailability, slotsOnClickListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdaptor);

        return view;
    }
    SlotsOnClickListener slotsOnClickListener = new SlotsOnClickListener() {
        @Override
        public void respond(int position, String slotsNumber, String slotsAvailability) {
        }
    };
    public interface SlotsOnClickListener{
        void respond(int position,String slotsNumber,String slotsAvailability);
    }

    public class RecyclerAdaptor extends RecyclerView.Adapter {
        Context context;
        ArrayList slotsNumbers, slotsAvailability;
        private SlotsOnClickListener slotsOnClickListener;
        public int posVal;

        public RecyclerAdaptor(Context context, ArrayList slotsNumbers, ArrayList slotsAvailability, SlotsOnClickListener slotsOnClickListener){
            this.context = context;
            this.slotsNumbers = slotsNumbers;
            this.slotsAvailability = slotsAvailability;
            this.slotsOnClickListener = slotsOnClickListener;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slots_details, parent, false);
            RecyclerAdaptor.ViewHolderClass viewHolderClass = new RecyclerAdaptor.ViewHolderClass(view, slotsOnClickListener);
            return viewHolderClass;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            posVal = position;
            RecyclerAdaptor.ViewHolderClass viewHolderClass = (RecyclerAdaptor.ViewHolderClass) holder;
            viewHolderClass.slotsNumbers.setText(slotsNumbers.get(position).toString());
            viewHolderClass.slotsAvailability.setText(slotsAvailability.get(position).toString());
            if(slotsAvailability.get(position).toString().equals("Occupied")){
                viewHolderClass.slotsAvailability.setTextColor(ContextCompat.getColor(context, R.color.red));
                viewHolderClass.slotsAvailability.setClickable(false);
            }
            else{
                viewHolderClass.slotsAvailability.setTextColor(ContextCompat.getColor(context, R.color.blue));
                viewHolderClass.slotsAvailability.setClickable(true);
            }

            viewHolderClass.slotsAvailability.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Toast.makeText(getActivity(), slotsNumbers.get(position).toString() + " clicked", Toast.LENGTH_SHORT).show();
                    final String[] Options = {"Book", "Cancel"};
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Continue with Booking " + slotsNumbers.get(position).toString());
                    // add the buttons
                    builder.setPositiveButton("Confirm Booking", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //DB update with slot number --> Occupied
                            Toast.makeText(getActivity(), "Booked " + slotsNumbers.get(position).toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //No DB update
                            Toast.makeText(getActivity(), "Booking Canceled", Toast.LENGTH_LONG).show();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return slotsNumbers.size();
        }

        public class ViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView slotsAvailability, slotsNumbers;
            public ViewHolderClass(@NonNull View itemView, SlotsOnClickListener slotsOnClickListenerLocal) {
                super(itemView);
                slotsNumbers = itemView.findViewById(R.id.slotNumber);
                slotsAvailability = itemView.findViewById(R.id.slotAvailability);
                slotsOnClickListener = slotsOnClickListenerLocal;
                slotsAvailability.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                slotsOnClickListener.respond(getAdapterPosition(), slotsNumbers.getText().toString(), slotsAvailability.getText().toString());
            }
        }
    }
}