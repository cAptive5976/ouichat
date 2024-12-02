package fr.charlesmj.ouichat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<Post> posts;
    private FirebaseFirestore db;
    private String currentUserId;


    public Adapter(ArrayList<Post> posts) {
        this.posts = posts;
        this.currentUserId = currentUserId;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post item = posts.get(position);

        // Partie gestion de date du post
        Timestamp timestamp = item.getDate();
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        holder.date.setText(sdf.format(date));

        // Partie gestion de l'utilisateur
        DocumentReference userRef = item.getUser_id();
        userRef.get().addOnSuccessListener(documentSnapshot -> { // Ici on utilise la reference comme une clé étrangère en SQL pour afficher le nom et prenom de l'utilisateur
            String firstName = documentSnapshot.getString("first_name");
            String lastName = documentSnapshot.getString("last_name");
            String id = documentSnapshot.getId();
            holder.username.setText(String.format("%s %s (#%s)", firstName, lastName, id)); // On affiche le nom et prénom de l'utilisateur, on precise l'id quand on a deux personne avec le même nom
        });

        // Partie contenu du post
        holder.content.setText(item.getContent());

        // Partie gestion des likes
        holder.likeCount.setText(String.valueOf(item.getLikes()));

        // Charger l'état initial du bouton "like"
        db.collection("posts").document(item.getPostId()).get().addOnSuccessListener(documentSnapshot -> {
            List<String> likedBy = (List<String>) documentSnapshot.get("likedBy");
            if (likedBy != null && likedBy.contains(currentUserId)) {
                holder.likeIcon.setSelected(true);
            } else {
                holder.likeIcon.setSelected(false);
            }
        });
        holder.likeIcon.setOnClickListener(v -> {
            DocumentReference postRef = db.collection("posts").document(item.getPostId());
            postRef.get().addOnSuccessListener(documentSnapshot -> {
                List<String> likedBy = (List<String>) documentSnapshot.get("likedBy");
                if (likedBy == null) likedBy = new ArrayList<>();
                if (likedBy.contains(currentUserId)) {
                    // Si déjà liké, retirer le like
                    likedBy.remove(currentUserId);
                    postRef.update("likedBy", likedBy, "likes", FieldValue.increment(-1));
                    item.setLikes(item.getLikes() - 1);
                    holder.likeCount.setText(String.valueOf(item.getLikes()));
                    holder.likeIcon.setSelected(false); // État "non liké"
                } else {
                    // Ajouter un like
                    likedBy.add(currentUserId);
                    postRef.update("likedBy", likedBy, "likes", FieldValue.increment(1));
                    item.setLikes(item.getLikes() + 1);
                    holder.likeCount.setText(String.valueOf(item.getLikes()));
                    holder.likeIcon.setSelected(true); // État "liké"
                }
            });
        });
    }

    // Méthode pour actualiser la liste des posts
    public void refresh(List<Post> posts) {
        this.posts = new ArrayList<>(posts);
        notifyDataSetChanged();
    }

    // Méthode de filtrage de message pour la recherche
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

    // Méthode pour compter le nombre de posts obligatoire car ReyclerView.Adapter est une classe abstraite
    @Override
    public int getItemCount() {
        return posts.size();
    }
}