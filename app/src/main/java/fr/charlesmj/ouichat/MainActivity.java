// Version: 1.0
package fr.charlesmj.ouichat;

// Bibliothèques Android
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// Bibliothèques Firestore
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

// Bibliothèques Java
import java.util.ArrayList;

/**
 * Cette classe représente une activité principale de l'application.
 * Elle gère l'affichage des posts et la navigation entre les différentes sections de l'application.
 *
 * @author cAptive
 * @version 1.0
 * @see WriteActivity
 * @see SearchActivity
 * @see ProfileActivity
 * @see ProfileActivityLogon
 * @see SignupActivity
 * @see LoginActivity
 */
public class MainActivity extends AppCompatActivity {
    // Declaration des variables
    private RecyclerView recyclerViewPosts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabNewPost;
    private Adapter postAdapter;
    private ArrayList<Post> postList;

    // Initialisation de Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference postsRef = db.collection("posts");

    @Override
    // Méthode onCreate qui est appelée au lancement de l'activité
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declaration des variables
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts); // Liste des posts
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Actualisation des posts
        fabNewPost = findViewById(R.id.fabNewPost); // Bouton pour créer un nouveau post

        // Initialisation du RecyclerView et notre Adapter
        postList = new ArrayList<>();
        postAdapter = new Adapter(postList,this);

        // Configuration du RecyclerView
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPosts.setAdapter(postAdapter);

        // Chargement des posts avec la méthode loadPosts
        loadPosts();
        swipeRefreshLayout.setOnRefreshListener(this::loadPosts);

        // Bouton pour créer un nouveau post qui redirige vers WriteActivity
        fabNewPost.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WriteActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // Barre de navigation inférieure
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true; // On ne fait rien car home est MainActivity.java
            } else if (itemId == R.id.navigation_search) {
                startActivity(new Intent(this, SearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // On vérifie si l'utilisateur est connecté pour afficher le bon profil
                SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
                if (isLoggedIn) {
                    startActivity(new Intent(this, ProfileActivityLogon.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else {
                    startActivity(new Intent(this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    // La méthode loadPosts charge les posts depuis Firestore et les affiche dans la RecyclerView.
    private void loadPosts() {
        postsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        post.setPost_id(document.getId());
                        postList.add(post);
                    }

                    // On trie les posts par score
                    postList.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

                    postAdapter.refresh(postList);
                    swipeRefreshLayout.setRefreshing(false);
                });
    }
}