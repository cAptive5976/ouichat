package fr.charlesmj.ouichat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ProfileActivityLogon extends AppCompatActivity{

    MaterialButton btn_logout;
    TextView tv_first_name;
    TextView tv_last_name;
    TextView tv_email;
    TextView tv_id;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_logon);
        prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);


        tv_first_name = findViewById(R.id.userfName);
        String firstName = prefs.getString("first_name", "");
        tv_first_name.setText(prefs.getString("first_name", ""));
        tv_first_name.setText(firstName);

        tv_last_name = findViewById(R.id.userlName);
        String lastName = prefs.getString("last_name", "");
        tv_last_name.setText(prefs.getString("last_name", ""));
        tv_last_name.setText(lastName);

        tv_email = findViewById(R.id.userEmail);
        String email = prefs.getString("email", "");
        tv_email.setText(prefs.getString("email", ""));
        tv_email.setText("Adresse email : " + email);

        tv_id = findViewById(R.id.userId);
        String userId = prefs.getString("userId", "");
        tv_id.setText(prefs.getString("userId", ""));
        tv_id.setText("Identifiant : " + userId);

        btn_logout = findViewById(R.id.btnLogout);
        btn_logout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_search) {
                startActivity(new Intent(this, SearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
    }
}
