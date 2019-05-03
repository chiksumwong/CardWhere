package com.cs.cardwhere.Models;

public class Card {

    private String firebase_id;
    private String user_id;
    private String company;
    private String name;
    private String tel;
    private String email;
    private String address;
    private String image_uri;

    public Card(){

    }

    public Card(String firebase_id, String user_id, String company, String name, String tel, String email, String address, String image_uri) {
        this.firebase_id = firebase_id;
        this.user_id = user_id;
        this.company = company;
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.address = address;
        this.image_uri = image_uri;
    }

    public Card(String firebase_id, String user_id, String company, String name, String tel, String email, String address) {
        this.firebase_id = firebase_id;
        this.user_id = user_id;
        this.company = company;
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.address = address;
    }

    public String getFirebase_id() {
        return firebase_id;
    }

    public void setFirebase_id(String firebase_id) {
        this.firebase_id = firebase_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }
}
