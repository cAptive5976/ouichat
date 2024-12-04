// Version: 1.0
package fr.charlesmj.ouichat;

// Bibliothèques Android
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

/**
 * Cette classe représente l'activité de profil de l'application OuiChat.
 * Elle gère l'affichage et les interactions de l'écran de profil, notamment
 * les boutons de connexion et d'inscription.
 *
 * @author cAptive
 * @version 1.0
 * @see MainActivity
 * @see ProfileActivityLogon
 * @see LoginActivity
 * @see SignupActivity
 */
public class ProfileActivity extends AppCompatActivity{
    // On déclare les variables des boutons
    private MaterialButton btn_login;
    private MaterialButton btn_signup;

    // Méthode onCreate au lancement de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // On initialise les boutons
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);

        // On ajoute les actions aux boutons, pour rediriger vers les activités correspondantes
        btn_login.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(0, 0);
        });
        btn_signup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            overridePendingTransition(0, 0);
        });

        // Barre de navigation inférieure
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
