package fr.charlesmj.ouichat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextInputEditText et_email;
    private TextInputEditText et_first_name;
    private TextInputEditText et_last_name;
    private TextInputEditText et_password;
    private MaterialButton btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_email = findViewById(R.id.et_email);
        et_first_name = findViewById(R.id.et_first_name);
        et_last_name = findViewById(R.id.et_last_name);
        et_password = findViewById(R.id.et_password);
        btn_signup = findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignupClick();
            }
        });
    }

    private void onSignupClick() {
        String email = et_email.getText().toString().trim();
        String first_name = et_first_name.getText().toString().trim();
        String last_name = et_last_name.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        // On vérifie que tous les champs sont remplis
        if (email.isEmpty() || first_name.isEmpty() || last_name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // On génère l'ID Firestore de l'utilisateur avec la règle compte@domaine -> domaine.compte avec un split
        String[] parts = email.split("@");
        if (parts.length != 2) {
            Toast.makeText(this, "Format d'email invalide", Toast.LENGTH_SHORT).show();
            return;
        }
        String id = parts[1] + "." + parts[0];


        // Partie communiquant avec Firestore
        db.collection("users").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) { // Si l'utilisateur existe déjà
                    Toast.makeText(SignupActivity.this, "Cet utilisateur existe déjà", Toast.LENGTH_SHORT).show();
                } else { // Sinon, on crée l'utilisateur
                    User newUser = new User(); // On crée un nouvel utilisateur de type objet User
                    newUser.setEmail(email);
                    newUser.setFirst_name(first_name);
                    newUser.setLast_name(last_name);
                    newUser.setPassword(password);

                    db.collection("users").document(id).set(newUser)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(SignupActivity.this, "Utilisateur créé avec succès", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                intent.putExtra("EMAIL", email);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(SignupActivity.this, "Erreur lors de la création de l'utilisateur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(SignupActivity.this, "Erreur lors de la vérification de l'utilisateur: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
