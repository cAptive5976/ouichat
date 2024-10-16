package fr.charlesmj.ouichat;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity{

    private MaterialButton btn_login;
    private MaterialButton btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btn_login = findViewById(R.id.btn_login); // Bouton pour se connecter
        btn_signup = findViewById(R.id.btn_signup); // Bouton pour s'inscrire

        btn_login.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        btn_signup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
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
