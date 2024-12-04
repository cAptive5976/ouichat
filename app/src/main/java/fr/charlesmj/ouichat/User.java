package fr.charlesmj.ouichat;

/**
 * Cette classe représente un utilisateur dans l'application OuiChat.
 * Elle sert de modèle pour faire la liaison entre un document de la collection "users" de Firestore
 * et un objet Java. Elle permet de manipuler les données des utilisateurs
 * de manière structurée et simplifiée.
 * @author cAptive
 * @version 1.0
 * @see MainActivity
 * @see ProfileActivity
 * @see ProfileActivityLogon
 * @see LoginActivity
 * @see SignupActivity
 * @see Adapter
 */
public class User {
    // Déclaration des variables
    private String email;
    private String first_name;
    private String last_name;
    private String password;

    // Constructeur vide requis par Firestore
    public User() {}

    // Constructeur avec les paramètres nécessaires
    public User(String email, String first_name, String last_name, String password) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
    }

    // Getters
    public String getEmail() {
        return email;
    }
    public String getFirst_name() {
        return first_name;
    }
    public String getLast_name() {
        return last_name;
    }
    public String getPassword() {
        return password;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
