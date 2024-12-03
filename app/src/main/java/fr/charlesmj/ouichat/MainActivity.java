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
package fr.charlesmj.ouichat;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

/**
 * MainActivity/Accueil est la classe principale qui gère l'affichage des posts.
 * Elle permet également à l'utilisateur de naviguer vers d'autres activités.
 * On va y utilisé les objets de la classe Adapter pour afficher les posts.
 * @see Adapter
 * Mais également les objets de la classe Post pour récupérer les données des posts.
 * @see Post
 */
public class MainActivity extends AppCompatActivity {

    // On commence nos variables
    private RecyclerView recyclerViewPosts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabNewPost;
    private Adapter postAdapter;
    private ArrayList<Post> postList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference postsRef = db.collection("posts");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On déclare nos variables pour le fil des posts
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts); // Liste des posts
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Actualisation des posts
        fabNewPost = findViewById(R.id.fabNewPost); // Bouton pour créer un nouveau post

        postList = new ArrayList<>(); // On initialise la liste des posts
        postAdapter = new Adapter(postList,this); // On initialise l'adapter pour les posts

        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPosts.setAdapter(postAdapter);

        loadPosts();
        swipeRefreshLayout.setOnRefreshListener(this::loadPosts);

        fabNewPost.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WriteActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // Ici on configure la bar de navigation inférieure
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

    /**
     * La méthode loadPosts charge les posts depuis Firestore et les affiche dans la RecyclerView.
     */
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