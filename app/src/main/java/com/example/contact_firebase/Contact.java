package com.example.contact_firebase;
public class Contact {
    private String id;
    private String nom;
    private String tel;
    public Contact() {} // Firebase a besoin d'un constructeur vide
    public Contact(String id, String nom, String tel) {
        this.id = id;
        this.nom = nom;
        this.tel = tel;
    }
    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getTel() { return tel; }
    public void setId(String id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setTel(String tel) { this.tel = tel; }
}
