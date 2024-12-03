package fr.charlesmj.ouichat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    private RecyclerView search_results;
    private Adapter postAdapter;
    private ArrayList<Post> postList;
    private ArrayList<Post> filteredPostList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference postsRef = db.collection("posts");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);

        postList = new ArrayList<>();
        filteredPostList = new ArrayList<>();
        postAdapter = new Adapter(postList,this);

        search_results = findViewById(R.id.recyclerViewPosts);
        search_results.setLayoutManager(new LinearLayoutManager(this));
        search_results.setAdapter(postAdapter);

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
