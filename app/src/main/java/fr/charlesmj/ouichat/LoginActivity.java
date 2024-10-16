package fr.charlesmj.ouichat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText et_email;
    private TextInputEditText et_password;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        MaterialButton btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick(v);
            }
        });
    }

    public void onLoginClick(View view) {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        // Requête Firestore pour chercher un utilisateur avec cet email
        usersRef.whereEqualTo("email", email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                User user = document.toObject(User.class);
                                if (user.getPassword().equals(password)) {
                                    // On enregistre l'utilisateur dans les SharedPreferences pour qu'il reste connecté
                                    SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
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
