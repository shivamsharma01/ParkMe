package com.android.parkme.common;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.parkme.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindSlotsFragment extends Fragment {
    private static final String TAG = "FindSlotsFragment";
    RecyclerView recyclerView;
    List<String> SlotsNumbers;
    List<String> SlotsAvailability;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_slots, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        SlotsNumbers = new ArrayList<>();
        Collections.addAll(SlotsNumbers, getResources().getStringArray(R.array.array_slots));

        SlotsAvailability = new ArrayList<>();
        Collections.addAll(SlotsAvailability, getResources().getStringArray(R.array.array_slots_availability));

        RecyclerAdaptor recyclerAdaptor = new RecyclerAdaptor(SlotsNumbers, SlotsAvailability);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdaptor);

        
        return view;
    }

    public class RecyclerAdaptor extends RecyclerView.Adapter<ViewHolderClass> {
        List<String> slotsNumbers, slotsAvailability;

        public RecyclerAdaptor(List<String> slotsNumbers, List<String> slotsAvailability) {
            this.slotsNumbers = slotsNumbers;
            this.slotsAvailability = slotsAvailability;
        }

        @NonNull
        @Override
        public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolderClass(LayoutInflater.from(parent.getContext()).inflate(R.layout.slots_details, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FindSlotsFragment.ViewHolderClass holder, int position) {
            holder.bind(this.slotsNumbers.get(position), this.slotsAvailability.get(position));
        }


        @Override
        public int getItemCount() {
            return slotsNumbers.size();
        }

    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        TextView slotNumberTextView, slotAvailabilityTextView;

        public ViewHolderClass(View itemView) {
            super(itemView);
            slotNumberTextView = itemView.findViewById(R.id.slotNumber);
            slotAvailabilityTextView = itemView.findViewById(R.id.slotAvailability);
        }

        public void bind(String slotNumber, String availability) {
            slotNumberTextView.setText(slotNumber);
            slotAvailabilityTextView.setText(availability);
            if (availability.toLowerCase().equals("occupied")) {
                slotAvailabilityTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                slotAvailabilityTextView.setEnabled(false);
            } else {
                slotAvailabilityTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
                slotAvailabilityTextView.setEnabled(true);
                slotAvailabilityTextView.setOnClickListener(view -> {
                    final String[] Options = {"Book", "Cancel"};
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Continue with Booking " + slotNumber);
                    builder.setPositiveButton("Confirm Booking", (dialog, which) -> {
                        Toast.makeText(getActivity(), "Booked " + slotNumber, Toast.LENGTH_LONG).show();
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        Toast.makeText(getActivity(), "Booking Canceled", Toast.LENGTH_LONG).show();
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
            }
        }
    }
}