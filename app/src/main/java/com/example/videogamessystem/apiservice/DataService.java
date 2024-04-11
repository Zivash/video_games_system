package com.example.videogamessystem.apiservice;

import android.os.StrictMode;

import androidx.annotation.NonNull;

import com.example.videogamessystem.models.Game;
import com.example.videogamessystem.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataService {
    private final List<Game> games = new ArrayList<>();
    private User currentUser;

    public List<Game> getGames() {
        return games;
    }

    private void createRatingAndNewestList(String action) throws IOException {
        int pageNumber = 1;
        int count = 0;
        while (count != 100) {
            String queryForGames = null;
            switch (action) {
                case "Top Rating Games":
                    queryForGames = "https://api.rawg.io/api/games?key=2efe707696da4f3c913e206ef53ffbcc&ordering=-rating&page=" + pageNumber;
                    break;
                case "Newest Games":
                    LocalDate currentDate = null;
                    int year = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        currentDate = LocalDate.now();
                        year = currentDate.getYear();
                    }
                    queryForGames = "https://api.rawg.io/api/games?dates=" + year + "-01-01," + currentDate + "&key=2efe707696da4f3c913e206ef53ffbcc&ordering=-released&page=" + pageNumber;
                    break;
            }

            JsonObject obj = getJsonObject(queryForGames);

            JsonArray rootObj = obj.get("results").getAsJsonArray();


            for (JsonElement gameDetails : rootObj) {
                JsonObject game = gameDetails.getAsJsonObject();
                int gameId = game.get("id").getAsInt();
                String name = game.get("name").toString().replace("\"", "");
                String imageUrl = game.get("background_image").toString().replace("\"", "");
                if (imageUrl.equals("null")) {
                    continue;
                } else {
                    count++;
                }

                if (action.equals("Top Rating Games")) {
                    double rating = game.get("rating").getAsDouble();
                    games.add(new Game(gameId, imageUrl, name, rating));
                } else if (action.equals("Newest Games")) {
                    String released = game.get("released").toString().replace("\"", "");
                    games.add(new Game(gameId, imageUrl, name, released));
                }
                if (count == 100) {
                    break;
                }
            }

            pageNumber++;
        }
    }


    public CompletableFuture<Void> createListByAction(String action, String userId) throws InterruptedException {

        CompletableFuture<Void> future = new CompletableFuture<>();
        if (action.equals("Favorite Games")) {
            DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference("users").child(userId);
            databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        currentUser = dataSnapshot.getValue(User.class);
                        try {
                            createFavoriteGamesList();

                            future.complete(null);
                        } catch (IOException e) {
                            future.completeExceptionally(e);
                        }
                    } else {
                        future.completeExceptionally(new IllegalStateException("User data not found"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
        } else {
            try {
                createRatingAndNewestList(action);
                future.complete(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return future;
    }

    private void createFavoriteGamesList() throws IOException {
        List<Integer> favoriteGames = currentUser.getFavoriteGamesId();

        for (int gameId : favoriteGames) {
            String queryForGames = "https://api.rawg.io/api/games/" + gameId + "?key=2efe707696da4f3c913e206ef53ffbcc";

            JsonObject obj = getJsonObject(queryForGames);

            String name = obj.get("name").toString().replace("\"", "");
            String imageUrl = obj.get("background_image").toString().replace("\"", "");
            double rating = obj.get("rating").getAsDouble();

            games.add(new Game(gameId, imageUrl, name, rating));
        }
    }


    public List<Game> createListSearchBy(String queryUrl) throws IOException {

        for (int i = 1; i < 6; i++) {

            JsonObject obj = getJsonObject(queryUrl + i);

            JsonArray resultsJson = obj.get("results").getAsJsonArray();

            for (JsonElement gameDetails : resultsJson) {
                JsonObject game = gameDetails.getAsJsonObject();
                int gameId = game.get("id").getAsInt();
                String name = game.get("name").toString().replace("\"", "");
                String imageUrl = game.get("background_image").toString().replace("\"", "");
                if (imageUrl.equals("null")) {
                    continue;
                }

                double rating = game.get("rating").getAsDouble();
                games.add(new Game(gameId, imageUrl, name, rating));
            }
            if (obj.get("next").toString().equals("null")) {
                break;
            }
        }

        return games;
    }

    public static String getGameDescription(int gameId) throws IOException {
        String queryForGames = "https://api.rawg.io/api/games/" + gameId + "?key=2efe707696da4f3c913e206ef53ffbcc";

        JsonObject obj = getJsonObject(queryForGames);
        String description = obj.get("description_raw").toString().replace("\\n", "").replace("\\r", "");
        if(description.equals("\"\""))
        {
            return "There is no description for the game.";
        }
        else
        {
            return description;
        }
    }

    private static JsonObject getJsonObject(String queryForGames) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = new URL(queryForGames);

        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));

        return root.getAsJsonObject();
    }
}