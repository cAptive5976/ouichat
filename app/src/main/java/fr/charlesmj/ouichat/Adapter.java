// Version: 1.0
package fr.charlesmj.ouichat;

// Bibliothèques Android
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Bibliothèques Firestore
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

// Bibliothèques Java
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Cette classe représente un adapter personnalisé pour RecyclerView.
 * Elle est responsable de la création et de la liaison des ViewHolders,
 * ainsi que de la gestion des données à afficher dans la liste.
 *
 * @author cAptive
 * @version 1.0
 * @see MainActivity
 * @see SearchActivity
 * @see ProfileActivityLogon
 * @see RecyclerView.Adapter
 * @see RecyclerView.ViewHolder
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<Post> posts;
    private FirebaseFirestore db;
    private String currentUserId;
    private SharedPreferences prefs;

    // Constructeur de l'Adapter
    public Adapter(ArrayList<Post> posts, Context context) {
        this.posts = posts;
        this.prefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getString("userId", null); // Récupérer l'ID utilisateur depuis SharedPreferences
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    // Crée et initialise le ViewHolder et sa vue associée à partir du layout XML
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    // Affiche les données à la position spécifiée
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Déclaration de l'objet Post
        Post item = posts.get(position);

        // Partie gestion de date du post
        Timestamp timestamp = item.getDate();
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        holder.date.setText(sdf.format(date));

        // Partie gestion de l'utilisateur
        DocumentReference userRef = item.getUser_id();
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            String firstName = documentSnapshot.getString("first_name");
            String lastName = documentSnapshot.getString("last_name");
            String id = documentSnapshot.getId();
            holder.username.setText(String.format("%s %s (#%s)", firstName, lastName, id));
        });

        // Partie contenu du post
        holder.content.setText(item.getContent());

        // Partie gestion des likes
        holder.likeCount.setText(String.valueOf(item.getLikes()));

        // Vérification de l'état du like dans la base de données
        if (currentUserId != null) {
            DocumentReference currentUserRef = db.collection("users").document(currentUserId);

            // Vérification dans Firestore si l'utilisateur a déjà liké
            db.collection("posts").document(item.getPostId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<DocumentReference> likedBy = (List<DocumentReference>) documentSnapshot.get("likedBy");
                            boolean isLiked = likedBy != null && likedBy.contains(currentUserRef);
                            holder.likeIcon.setSelected(isLiked);
                        }
                    });
        }

        // Désactiver le bouton si l'utilisateur n'est pas connecté
        holder.likeIcon.setEnabled(currentUserId != null);

        holder.likeIcon.setOnClickListener(v -> {
            if (currentUserId == null) {
                return; // Ne rien faire si l'utilisateur n'est pas connecté
            }

            // Rechercher l'utilisateur dans Firestore via son ID
            db.collection("users").document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            DocumentReference currentUserRef = documentSnapshot.getReference();

                            // Mettre à jour l'état du like
                            DocumentReference postRef = db.collection("posts").document(item.getPostId());
                            SharedPreferences.Editor editor = prefs.edit();
                            boolean liked = prefs.getBoolean("liked_" + item.getPostId(), false);

                            if (liked) {
                                // Si déjà liké, retirer le like
                                editor.putBoolean("liked_" + item.getPostId(), false);
                                item.setLikes(item.getLikes() - 1);
                                postRef.update("likes", FieldValue.increment(-1));
                                postRef.update("likedBy", FieldValue.arrayRemove(currentUserRef)); // Retirer de likedBy
                                holder.likeIcon.setSelected(false);
                            } else {
                                // Ajouter un like
                                editor.putBoolean("liked_" + item.getPostId(), true);
                                item.setLikes(item.getLikes() + 1);
                                postRef.update("likes", FieldValue.increment(1));
                                postRef.update("likedBy", FieldValue.arrayUnion(currentUserRef)); // Ajouter à likedBy
                                holder.likeIcon.setSelected(true);
                            }

                            editor.apply();
                            holder.likeCount.setText(String.valueOf(item.getLikes()));
                        }
                    });
        });
    }

    // Méthode pour actualiser la liste des posts
    public void refresh(List<Post> posts) {
        this.posts = new ArrayList<>(posts);
        notifyDataSetChanged();
    }

    // Méthode pour filtrer les messages pour la recherche
    public void filter(List<Post> filteredPosts) {
        this.posts = new ArrayList<>(filteredPosts);
        notifyDataSetChanged();
    }

    // Partie ViewHolder, déclaration des éléments de la vue
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView likeIcon, profileIcon;
        TextView username, date, content, likeCount;

        public ViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.username);
            date = view.findViewById(R.id.date);
            content = view.findViewById(R.id.content);
            likeIcon = view.findViewById(R.id.like_icon);
            likeCount = view.findViewById(R.id.like_count);
            profileIcon = view.findViewById(R.id.pfp);
        }
    }

    // Méthode pour compter le nombre de posts, obligatoire pour l'Adapter
    @Override
    public int getItemCount() {
        return posts.size();
    }
}
