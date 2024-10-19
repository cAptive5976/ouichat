package fr.charlesmj.ouichat;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;


public class Post {
    private String content;
    private DocumentReference user_id; // Sur Firestore on stocke l'id de l'utilisateur qui est également le nom du document dans users
    private int likes;
    private Timestamp date; // On utilise Timestamp pour la date car sur Firestore c'est un Timestamp
    private int comments;

    public Post() {
        this.date = Timestamp.now(); // Des qu'on crée un post, on met la date actuelle
    }

    public Post(String content, DocumentReference user_id, int likes, Timestamp date, int comments) {
        this.content = content;
        this.user_id = user_id;
        this.likes = likes;
        this.date = date;
        this.comments = comments;
    }

    // Partie recupération des données pour l'affichage des posts

    public String getContent() {return content;}
    public DocumentReference getUser_id() {return user_id;}
    public int getLikes() {return likes;}
    public int getComments() {return comments;}
    public Timestamp getDate() {return date;}

    // Pour le score on va utilise la formule suivante: score = (30 - age) * likes
    public int getScore() {
        Date DateActuelle = new Date(); // On prend la date du moment
        Date DatePost = this.date.toDate(); // On convertit le Timestamp en Date car c'est pas le même format

        long difference_date = Math.abs(DateActuelle.getTime() - DatePost.getTime());
        long age_long = TimeUnit.DAYS.convert(difference_date, TimeUnit.MILLISECONDS);
        int age = (int) age_long; // On convertit en int car on a besoin d'un int pour la formule

        return (30 - age) * this.likes;
    }

    // Partie création et interaction avec les posts

    public void setContent(String content) {this.content = content;}
    public void setLikes(int likes) {this.likes = likes;}
    public void setComments(int comments) {this.comments = comments;}
}