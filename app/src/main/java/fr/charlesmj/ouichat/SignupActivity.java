package fr.charlesmj.ouichat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference users = database.getReference("users");
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
        final String email = et_email.getText().toString().trim();
        final String first_name = et_first_name.getText().toString().trim();
        final String last_name = et_last_name.getText().toString().trim();
        final String password = et_password.getText().toString().trim();

        // Validate input fields
        if (email.isEmpty() || first_name.isEmpty() || last_name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate user ID from email
        String[] parts = email.split("@");
        if (parts.length != 2) {
            Toast.makeText(this, "Format d'email invalide", Toast.LENGTH_SHORT).show();
            return;
        }
        String id = parts[1] + "." + parts[0];

        // Check if user already exists
        users.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SignupActivity.this, "Cet utilisateur existe déjà", Toast.LENGTH_SHORT).show();
                } else {
                    // Create new user
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirst_name(first_name);
                    newUser.setLast_name(last_name);
                    newUser.setPassword(password);
                    newUser.setId(id);

                    users.child(id).setValue(newUser, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(SignupActivity.this, "Utilisateur créé avec succès", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                intent.putExtra("EMAIL", email);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignupActivity.this, "Erreur lors de la création de l'utilisateur: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignupActivity.this, "Erreur de base de données: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}