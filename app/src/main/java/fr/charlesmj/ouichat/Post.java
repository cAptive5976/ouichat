// Version: 1.0
package fr.charlesmj.ouichat;

// Bibliothèques Java
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// Bibliothèques Firebase
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

/**
 * Cette classe représente un post dans l'application OuiChat.
 * Elle sert de modèle pour faire la liaison entre un document Firestore
 * et un objet Java. Elle permet de manipuler les données des posts
 * de manière structurée et simplifiée.
 *
 * @author cAptive
 * @version 1.0
 * @see MainActivity
 * @see ProfileActivityLogon
 * @see SearchActivity
 * @see Adapter
 */
public class Post {
    // Déclaration des variables
    private String post_id;
    private String content;
    private DocumentReference user_id; // Sur Firestore on stocke l'id de l'utilisateur qui est également le nom du document dans users
    private int likes;
    private Timestamp date; // On utilise Timestamp pour la date car sur Firestore c'est un Timestamp
    private List<DocumentReference> likedBy = new ArrayList<>();

    // Constructeur vide pour Firestore
    public Post() {}

    // Constructeur avec tous les paramètres
    public Post(String content, DocumentReference user_id, int likes, Timestamp date, String post_id) {
        this.content = content;
        this.user_id = user_id;
        this.likes = likes;
        this.date = date;
        this.post_id = post_id;
    }

    // Constructeur avec tous les paramètres et la liste des utilisateurs qui ont liké
    public Post(String content, DocumentReference user_id, int likes, Timestamp date, String post_id, List<DocumentReference> likedBy) {
        this.content = content;
        this.user_id = user_id;
        this.likes = likes;
        this.date = date;
        this.post_id = post_id;
        this.likedBy = likedBy;
    }

    // Getters
    public String getContent() {return content;}
    public DocumentReference getUser_id() {return user_id;}
    public int getLikes() {return likes;}
    public Timestamp getDate() {return date;}
    public String getPostId() {return post_id;}
    public List<DocumentReference> getLikedBy() {return likedBy;}

    // Pour le score on va utilise la formule suivante: score = (30 - age) * likes
    public int getScore() {
        Date DateActuelle = new Date(); // On prend la date du moment
        Date DatePost = this.date.toDate(); // On converti le Timestamp en Date car c'est pas le même format

        long difference_date = Math.abs(DateActuelle.getTime() - DatePost.getTime());
        long age_long = TimeUnit.DAYS.convert(difference_date, TimeUnit.MILLISECONDS);
        int age = (int) age_long; // On converti en int car on a besoin d'un int pour la formule

        return (30 - age) * this.likes;
    }

    // Setters

    public void setLikes (int likes) {this.likes = likes;}
    public void setLikedBy (List<DocumentReference> likedBy) {this.likedBy = likedBy;}
    public void setPost_id(String post_id) {this.post_id = post_id;}

    // Partie mapping

    public Map<String, Object> mapping() {
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("content", getContent());
        postMap.put("user_id", getUser_id());
        postMap.put("likes", getLikes());
        postMap.put("date", getDate());
        postMap.put("post_id", getPostId());
        postMap.put("likedBy", getLikedBy() != null ? getLikedBy() : new ArrayList<>());
        return postMap;
    }
}