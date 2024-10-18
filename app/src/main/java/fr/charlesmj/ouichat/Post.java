package fr.charlesmj.ouichat;

import java.sql.Time;
import java.util.Date;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;


public class Post {
    private String content;
    private DocumentReference user_id; // Sur Firestore on stocke l'id de l'utilisateur qui est également le nom du document dans users
    private int likes;
    private Timestamp date; // On utilise Timestamp pour la date car sur Firestore c'est un Timestamp
    private int score;
    private int comments;

    public Post() {
        this.date = Timestamp.now(); // Des qu'on crée un post, on met la date actuelle
    }

    public Post(String content, DocumentReference user_id, int likes, Timestamp date, int score, int comments) {
        this.content = content;
        this.user_id = user_id;
        this.likes = likes;
        this.date = date;
        this.score = score;
        this.comments = comments;
    }

    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}

    public DocumentReference getUser_id() {return user_id;}
    public void setUser_id(DocumentReference user_id) {this.user_id = user_id;}

    public int getLikes() {return likes;}
    public void setLikes(int likes) {this.likes = likes;}

    public Timestamp getDate() {return date;}

    public int getScore() {return score;}
    public void setScore(int score) {this.score = score;}

    public int getComments() {return comments;}
    public void setComments(int comments) {this.comments = comments;}
}