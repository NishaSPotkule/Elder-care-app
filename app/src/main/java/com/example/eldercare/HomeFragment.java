package com.example.eldercare;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    CardView family,medicine;
    View sosBtn;

    FusedLocationProviderClient fusedLocationClient;

    private static final int SMS_PERMISSION_CODE = 101;
    private static final int LOCATION_PERMISSION_CODE = 102;

    public HomeFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sosBtn = view.findViewById(R.id.sosCard);
        family = view.findViewById(R.id.familycard);
        medicine=view.findViewById(R.id.medicineCard);



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        family.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FamilyActivity.class)));
        medicine.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MedsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        sosBtn.setOnClickListener(v -> checkPermissionsAndSendSOS());

        return view;
    }


    private void checkPermissionsAndSendSOS() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE);
            return;
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
            return;
        }

        triggerSOS();
    }


    @SuppressLint("MissingPermission")
    private void triggerSOS() {

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            double lat = 0.0, lng = 0.0;

            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }

            String locationLink = "https://maps.google.com/?q=" + lat + "," + lng;

            SharedPreferences sharedPreferences =
                    getActivity().getSharedPreferences("contacts", MODE_PRIVATE);

            String data = sharedPreferences.getString("data", "");

            if (data.isEmpty()) {
                Toast.makeText(getContext(), "No contacts saved!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(data);

                String message = "🚨 EMERGENCY!\nI need help.\nMy location:\n" + locationLink;


                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject obj = jsonArray.getJSONObject(i);
                    String phone = obj.getString("phone");

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, message, null, null);
                }


                if (jsonArray.length() > 0) {
                    String firstNumber = jsonArray.getJSONObject(0).getString("phone");

                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + firstNumber));
                    startActivity(callIntent);
                }

                Toast.makeText(getContext(), "SOS with Location Sent!", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == SMS_PERMISSION_CODE || requestCode == LOCATION_PERMISSION_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissionsAndSendSOS();
            } else {
                Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}