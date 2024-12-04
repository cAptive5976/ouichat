// Version: 1.0
package fr.charlesmj.ouichat;

// Bibliothèques Android
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

// Bibliothèques Firestore
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

// Bibliothèques Java
import java.util.UUID;

/**
 * Cette classe représente l'activité de création de post dans l'application OuiChat.
 * Elle permet aux utilisateurs de rédiger et publier un nouveau post, qui sera enregistré
 * dans la base de données Firestore.
 * @author cAptive
 * @version 1.0
 * @see MainActivity
 * @see Post
 */
public class WriteActivity extends AppCompatActivity {
    // Déclaration des variables
    private TextInputEditText write_post;
    private MaterialButton btn_post;
    private MaterialButton btn_cancel;

    // Initialisation de Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference postsRef = db.collection("posts");

    // Initialisation des SharedPreferences (base de données locale)
    SharedPreferences prefs;

    @Override
    // Méthode onCreate qui est appelée au lancement de l'activité
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        // Initialisation des SharedPreferences
        prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Récupération des éléments de l'interface utilisateur depuis le fichier XML
        write_post = findViewById(R.id.write_post);
        btn_post = findViewById(R.id.btn_post);
        btn_cancel = findViewById(R.id.btn_cancel);

        // Boutton de publication du post
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostClick();
            }
        });

        // Boutton d'annulation de la publication
        btn_cancel.setOnClickListener(v -> {
            Intent intent = new Intent(WriteActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    // Méthode onPostClick qui est appelée lorsqu'un utilisateur clique sur le bouton "Publier"
    public void onPostClick() {
        // Récupération des données utilisateur
        String content = write_post.getText().toString().trim();
        String internal_id = prefs.getString("userId", "");
        String post_id = UUID.randomUUID().toString();
        DocumentReference userId = db.collection("users").document(internal_id);

        // Déclaration de la date et du nombre de likes (0)
        Timestamp date = Timestamp.now();
        int likes = 0;

        // Avertissement si le contenu du post est vide
        if (content.isEmpty()) { // On vérifie que le contenu du post n'est pas vide
            Toast.makeText(this, "Vous ne pouvez écrire un post vide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Déclaration d'un nouvel objet Post
        Post post = new Post(content, userId, likes, date, post_id);

        // Enregistrement du post dans Firestore via un mapping
        postsRef.document(post_id).set(post.mapping());

        // Redirection vers l'activité principale
        Intent intent = new Intent(WriteActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
