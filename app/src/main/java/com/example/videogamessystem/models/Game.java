package com.example.videogamessystem.models;

public class Game {
    private final String gameImage;
    private String name;
    private double rating;
    private String releasedDate;
    private int id;

    public Game(int id, String gameImage, String name, double rating) {
        this.id = id;
        this.gameImage = gameImage;
        this.name = name;
        this.rating = rating;
    }

    public Game(int id, String gameImage, String name, String releasedDate) {
        this.id = id;
        this.gameImage = gameImage;
        this.name = name;
        this.releasedDate = releasedDate;
    }

    public int getId() {
        return id;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public String getGameImage() {
        return gameImage;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

}