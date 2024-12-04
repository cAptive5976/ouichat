// Version: 1.0
package fr.charlesmj.ouichat;

// Bibliothèques Android
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

// Bibliothèques Firebase
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Cette classe représente une activité de connexion à l'application.
 * Elle gère la connexion d'un utilisateur à l'application et intéragit avec Firestore.
 *
 * @author cAptive
 * @version 1.0
 * @see MainActivity
 * @see WriteActivity
 * @see SearchActivity
 * @see ProfileActivity
 * @see ProfileActivityLogon
 * @see SignupActivity
 */
public class LoginActivity extends AppCompatActivity {
    // Déclaration des variables
    private TextInputEditText et_email;
    private TextInputEditText et_password;

    // Initialisation de Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("users");

    // Méthode onCreate qui est appelée au lancement de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Récupération des éléments du layout xml
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        MaterialButton btn_login = findViewById(R.id.btn_login);

        // Ajout d'un listener sur le bouton de connexion
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            // On délègue onClick à la méthode onLoginClick pour plus de clarté
            public void onClick(View v) {
                onLoginClick(v);
            }
        });
    }

    // On crée une méthode onLoginClick pour gérer la connexion qui est déléguée par le listener
    public void onLoginClick(View view) {
        // Déclaration des variables email et mot de passe
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        // Requête Firestore pour chercher un utilisateur avec cet email
        usersRef.whereEqualTo("email", email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Condition de fonctionnement de la base de données
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            // Condition d'existance de l'utilisateur
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                User user = document.toObject(User.class);
                                if (user.getPassword().equals(password)) {
                                    // Condition de vérification du mot de passe

                                    // On enregistre l'utilisateur dans les SharedPreferences pour qu'il reste connecté
                                    SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();

                                    // On envoit les données de l'utilisateur dans les SharedPreferences
                                    editor.putString("userId", document.getId());
                                    editor.putString("email", user.getEmail());
                                    editor.putString("first_name", user.getFirst_name());
                                    editor.putString("last_name", user.getLast_name());
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.apply();

                                    // Puis on le redirige vers son profil
                                    Intent intent = new Intent(LoginActivity.this, ProfileActivityLogon.class);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Cet utilisateur n'existe pas", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Erreur dans la base de données : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
