package fr.charlesmj.ouichat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {

    // On commence nos variables
    private RecyclerView recyclerViewPosts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabNewPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On déclare nos variables pour le fil des posts
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts); // Liste des posts
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Actualisation des posts
        fabNewPost = findViewById(R.id.fabNewPost); // Bouton pour créer un nouveau post


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
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}