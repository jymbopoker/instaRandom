package com.example.instagramRandom.domains;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class InstaPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String shortcode;
    private String username;

    public InstaPhoto() {
    }


    public InstaPhoto(String shortcode) {
        this.shortcode = shortcode;
    }


    public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
