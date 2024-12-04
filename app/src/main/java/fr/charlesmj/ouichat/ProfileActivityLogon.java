// Version: 1.0
package fr.charlesmj.ouichat;

// Bibliothéques de base d'Android
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

// Bibliothéques affichage liste
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Bibliothéques Firebase
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

// Bibliothéques Java
import java.util.ArrayList;

/**
 * Cette classe représente l'activité de profil pour un utilisateur connecté dans l'application OuiChat.
 * Elle gère l'affichage des informations de l'utilisateur, la liste de ses posts, et les fonctionnalités
 * de déconnexion. L'activité utilise SharedPreferences pour stocker les données de session de l'utilisateur
 * et Firestore pour récupérer les posts.
 *
 * @author cAptive
 * @version 1.0
 * @see MainActivity
 * @see ProfileActivity
 * @see LoginActivity
 * @see Post
 * @see Adapter
 */
public class ProfileActivityLogon extends AppCompatActivity{
    // Déclaration des variables
    MaterialButton btn_logout;
    TextView tv_first_name;
    TextView tv_last_name;
    TextView tv_email;
    TextView tv_id;

    // Declaration recyclerView, adapter et liste de posts
    private RecyclerView recyclerViewPosts;
    private Adapter postAdapter;
    private ArrayList<Post> postList;

    // Initialisation de la base de données Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference postsRef = db.collection("posts");

    // Initialisation des SharedPreferences (base de données locale)
    SharedPreferences prefs;

    // Méthode onCreate au lancement de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_logon);

        // Récupération des SharedPreferences
        prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Affichage des informations de l'utilisateur dans les TextView
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
        tv_id.setText("Identifiant : " + "#" + userId);

        // Initialisation de la liste des posts et de l'adapter
        postList = new ArrayList<>();
        postAdapter = new Adapter(postList, this);

        // Initialisation du recyclerView
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPosts.setAdapter(postAdapter);

        // Récupération de la liste des posts de l'utilisateur connecté (userRef/userId)
        DocumentReference userRef = db.collection("users").document(userId);
        loadPosts(userRef);

        // Bouton de déconnexion
        btn_logout = findViewById(R.id.btnLogout);
        btn_logout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });

        // Bar de navigation inférieure
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

    // Méthode pour charger les posts de l'utilisateur
    private void loadPosts(DocumentReference userRef) {
        postsRef.whereEqualTo("user_id", userRef).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // On vide la liste des posts avant utilisation
                    postList.clear();

                    // On ajoute les posts à la liste qui correspond à l'utilisateur
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        post.setPost_id(document.getId());
                        postList.add(post);
                    }

                    // On trie les posts par score
                    postList.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

                    // On actualise la liste des posts
                    postAdapter.refresh(postList);
                });
    }
}
