package com.example.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;

public class MedsFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton fab;

    MedicineAdapter adapter;
    ArrayList<Medicine> list = new ArrayList<>();

    AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_meds, container, false);

        recyclerView = view.findViewById(R.id.recyclerMedicine);
        fab = view.findViewById(R.id.fabAddMed);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MedicineAdapter(requireContext(), list);
        recyclerView.setAdapter(adapter);

        db = AppDatabase.getInstance(requireContext());

        fab.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), AddMedicineActivity.class));
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        list.clear();

        List<Medicine> medicines = db.medicineDao().getAll();

        if (medicines != null) {


            for (Medicine m : medicines) {
                m.taken = false;
                db.medicineDao().update(m);
            }

            list.addAll(medicines);
        }

        adapter.notifyDataSetChanged();
    }
}