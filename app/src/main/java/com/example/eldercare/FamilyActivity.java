package com.example.eldercare;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FamilyActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fabAdd;

    ArrayList<Contact> contactList;
    ContactAdapter adapter;

    SharedPreferences sharedPreferences;

    private static final int CALL_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);

        recyclerView = findViewById(R.id.recyclerContacts);
        fabAdd = findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactList = new ArrayList<>();
        adapter = new ContactAdapter(this, contactList);
        recyclerView.setAdapter(adapter);

        sharedPreferences = getSharedPreferences("contacts", MODE_PRIVATE);

        loadContacts();

        fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_contact, null);

        EditText etName = view.findViewById(R.id.etName);
        EditText etPhone = view.findViewById(R.id.etPhone);

        new AlertDialog.Builder(this)
                .setTitle("Add Contact")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {

                    String name = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();

                    // ✅ Validation
                    if (name.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    contactList.add(new Contact(name, phone));
                    adapter.notifyDataSetChanged();

                    saveContacts();

                    Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveContacts() {
        JSONArray jsonArray = new JSONArray();

        for (Contact c : contactList) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("name", c.getName());
                obj.put("phone", c.getPhone());
                jsonArray.put(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sharedPreferences.edit().putString("data", jsonArray.toString()).apply();
    }

    private void loadContacts() {
        String data = sharedPreferences.getString("data", "");

        if (!data.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    contactList.add(new Contact(
                            obj.getString("name"),
                            obj.getString("phone")
                    ));
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CALL_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted! Try calling again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}