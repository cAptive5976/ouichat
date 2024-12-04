// Version: 1.0
package fr.charlesmj.ouichat;

// Bibliothèques Android
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Bibliothèques Firestore
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

// Bibliothèques Java
import java.util.ArrayList;

/**
 * Cette classe représente l'activité de recherche dans l'application OuiChat.
 * Elle permet aux utilisateurs de rechercher des posts en temps réel en utilisant
 * un SearchView et affiche les résultats filtrés dans un RecyclerView.
 * @author cAptive
 * @version 1.0
 * @see Post
 * @see Adapter
 * @see MainActivity
 * @see ProfileActivityLogon
 * @see RecyclerView
 */
public class SearchActivity extends AppCompatActivity {
    // Déclaration des variables
    private SearchView searchView;
    private RecyclerView search_results;
    private Adapter postAdapter;
    private ArrayList<Post> postList;
    private ArrayList<Post> filteredPostList;

    // Initialisation de Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference postsRef = db.collection("posts");


    @Override
    // Méthode onCreate qui est appelée au lancement de l'activité
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialisation des éléments de l'interface depuis le layout xml
        searchView = findViewById(R.id.searchView);
        search_results = findViewById(R.id.recyclerViewPosts);

        // Initialisation des listes et de l'adapter pour le RecyclerView
        postList = new ArrayList<>();
        filteredPostList = new ArrayList<>();
        postAdapter = new Adapter(postList,this);

        // Configuration du RecyclerView et de l'Adapter
        search_results.setLayoutManager(new LinearLayoutManager(this));
        search_results.setAdapter(postAdapter);

        // Ajout d'un listener sur le SearchView pour filtrer les posts
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Cette méthode permet de réagir uniquement à la validation de la recherche donc pas en temps reel
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPosts(newText);
                return true; // Cette méthode permet de réagir en temps réel à la recherche
            }
        });

        // Barrre de navigation inférieure
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_search) {
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
        bottomNavigationView.setSelectedItemId(R.id.navigation_search);
    }

    // Méthode de filtrage des posts
    private void filterPosts(String search) {
        postsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    filteredPostList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        post.setPost_id(document.getId());
                        if (post.getContent().toLowerCase().contains(search.toLowerCase())) { // On filtre les posts par contenu, on compare (contains) en minuscule (toLowerCase) avec la recherche
                            filteredPostList.add(post);
                        }
                    }

                    // On trie les posts par score et on informe l'adapter du tri
                    filteredPostList.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
                    postAdapter.filter(filteredPostList);
                });
    }
}
