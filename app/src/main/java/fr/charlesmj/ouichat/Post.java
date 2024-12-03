package fr.charlesmj.ouichat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;


public class Post {
    private String post_id;
    private String content;
    private DocumentReference user_id; // Sur Firestore on stocke l'id de l'utilisateur qui est également le nom du document dans users
    private int likes;
    private Timestamp date; // On utilise Timestamp pour la date car sur Firestore c'est un Timestamp
    private List<DocumentReference> likedBy = new ArrayList<DocumentReference>();
    public Post() {
        // Constructeur vide pour Firestore
    }

    public Post(String content, DocumentReference user_id, int likes, Timestamp date, String post_id) {
        this.content = content;
        this.user_id = user_id;
        this.likes = likes;
        this.date = date;
        this.post_id = post_id;
    }

    public Post(String content, DocumentReference user_id, int likes, Timestamp date, String post_id, List<DocumentReference> likedBy) {
        this.content = content;
        this.user_id = user_id;
        this.likes = likes;
        this.date = date;
        this.post_id = post_id;
        this.likedBy = likedBy;
    }

    // Les getters

    public String getContent() {return content;}
    public DocumentReference getUser_id() {return user_id;}
    public int getLikes() {return likes;}
    public Timestamp getDate() {return date;}
    public String getPostId() {return post_id;}
    public List<DocumentReference> getLikedBy() {return likedBy;}

    // Pour le score on va utilise la formule suivante: score = (30 - age) * likes
    public int getScore() {
        Date DateActuelle = new Date(); // On prend la date du moment
        Date DatePost = this.date.toDate(); // On convertit le Timestamp en Date car c'est pas le même format

        long difference_date = Math.abs(DateActuelle.getTime() - DatePost.getTime());
        long age_long = TimeUnit.DAYS.convert(difference_date, TimeUnit.MILLISECONDS);
        int age = (int) age_long; // On convertit en int car on a besoin d'un int pour la formule

        return (30 - age) * this.likes;
    }

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