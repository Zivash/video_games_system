package com.example.videogamessystem.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String name;
    private final List<Integer> favoriteGamesId = new ArrayList<>();

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public User() {
    }

    public void handleFavoriteID(int id) {
        if (!favoriteGamesId.contains(id)) {
            this.favoriteGamesId.add(id);
        } else {
            this.favoriteGamesId.remove(Integer.valueOf(id));
        }
    }

    public String getName() {
        return name;
    }

    public List<Integer> getFavoriteGamesId() {
        return favoriteGamesId;
    }

}