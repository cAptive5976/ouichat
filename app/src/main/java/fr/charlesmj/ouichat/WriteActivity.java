package fr.charlesmj.ouichat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WriteActivity extends AppCompatActivity {
    private TextInputEditText write_post;
    private MaterialButton btn_post;
    private MaterialButton btn_cancel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference postsRef = db.collection("posts");
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        write_post = findViewById(R.id.write_post);
        btn_post = findViewById(R.id.btn_post);
        btn_cancel = findViewById(R.id.btn_cancel);

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostClick();
            }
        });

        btn_cancel.setOnClickListener(v -> {
            Intent intent = new Intent(WriteActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    public void onPostClick() {
        // On commence par récupérer les informations qui décrivent le post dans le Firestore et l'objet Post
        String content = write_post.getText().toString().trim();
        String internal_id = prefs.getString("userId", "");
        String post_id = UUID.randomUUID().toString();
        DocumentReference userId = db.collection("users").document(internal_id);
        Timestamp date = Timestamp.now();
        int likes = 0;

        if (content.isEmpty()) { // On vérifie que le contenu du post n'est pas vide
            Toast.makeText(this, "Vous ne pouvez écrire un post vide", Toast.LENGTH_SHORT).show();
            return;
        }

        // On ajoute le post à la collection des posts en passant par l'objet Post
        Post post = new Post(content, userId, likes, date, post_id);

        // Ensuite on appelle la map qui indique a Firestore comment déclarer les champs

        postsRef.document(post_id).set(post.mapping());

        // On redirige l'utilisateur vers la page principale
        Intent intent = new Intent(WriteActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
