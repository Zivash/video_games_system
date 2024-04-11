package com.example.videogamessystem.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.videogamessystem.apiservice.DataService;
import com.example.videogamessystem.R;
import com.example.videogamessystem.models.Game;
import com.example.videogamessystem.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.ViewHolder> {
    private final String userId;
    private final List<Game> myGamesDataList;
    private final Context context;
    private final String action;

    public GamesAdapter(String userId, List<Game> myGamesDataList, FragmentActivity activity, String action) {
        this.userId = userId;
        this.myGamesDataList = myGamesDataList;
        this.context = activity;
        this.action = action;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.game_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Game myGamesData = myGamesDataList.get(position);
        holder.textViewName.setText(myGamesData.getName());

        if(action.equals("Newest Games"))
        {
            holder.textViewSelection.setText(myGamesData.getReleasedDate());
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = context.getDrawable(R.drawable.date_icon);
            holder.textViewSelection.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
        else {
            String rating = String.valueOf(myGamesData.getRating());
            holder.textViewSelection.setText(rating);
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = context.getDrawable(R.drawable.star_icon);
            holder.textViewSelection.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }

        String url = myGamesData.getGameImage();
        Glide.with(context).load(url).into(holder.gameImage);

        DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference("users").child(userId);
        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    SetHeartImage(Objects.requireNonNull(user), myGamesData.getId(), holder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.itemView.setOnClickListener(v -> {
            int gameId = myGamesData.getId();
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.BlueOkButtonAlertDialog);
            builder.setTitle("Game Description");
            builder.setPositiveButton("OK", (dialogInterface, i) -> {

            });
            try {
                String description = DataService.getGameDescription(gameId);
                builder.setMessage(description);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            AlertDialog dialog = builder.create();
            dialog.show();
            Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            okButton.setTextColor(ContextCompat.getColor(v.getContext(), R.color.blue));
        });

        holder.heartButton.setOnClickListener(v -> {
            int gameID = myGamesData.getId();

            databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        handleHeartImage(Objects.requireNonNull(user), gameID, holder);
                        user.handleFavoriteID(gameID);

                        databaseReferenceUser.setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                });
                    } else {
                        Log.d("User Info", "User not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("Error", "Database Error: " + databaseError.getMessage());
                }
            });
        });
    }

    private static void handleHeartImage(User user, int gameID, @NonNull ViewHolder holder) {
        if (user.getFavoriteGamesId().contains(gameID)) {
            holder.heartButton.setImageResource(R.drawable.favorite_empty_icon);
        } else {
            holder.heartButton.setImageResource(R.drawable.favorite_fill_icon);
        }
    }

    private static void SetHeartImage(User user, int gameId, @NonNull ViewHolder holder) {
        if (user.getFavoriteGamesId().contains(gameId)) {
            holder.heartButton.setImageResource(R.drawable.favorite_fill_icon);
        }
        else
        {
            holder.heartButton.setImageResource(R.drawable.favorite_empty_icon);
        }
    }

    @Override
    public int getItemCount() {
        return myGamesDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView gameImage;
        private final TextView textViewName;
        private final TextView textViewSelection;
        private final ImageButton heartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gameImage = itemView.findViewById(R.id.imageview);
            textViewName = itemView.findViewById(R.id.textName);
            textViewSelection = itemView.findViewById(R.id.textSelection);
            heartButton = itemView.findViewById(R.id.heartButton);
        }
    }
}