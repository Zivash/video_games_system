package com.example.videogamessystem.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.videogamessystem.apiservice.DataService;
import com.example.videogamessystem.models.Game;
import com.example.videogamessystem.adapters.GamesAdapter;
import com.example.videogamessystem.R;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GamesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamesListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<Game> gamesList = null;

    public GamesListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GamesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GamesListFragment newInstance(String param1, String param2) {
        GamesListFragment fragment = new GamesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DataService dataService = new DataService();
        String action = requireArguments().getString("action");
        String userId = requireArguments().getString("userId");

        if (Objects.requireNonNull(action).equals("Search By"))
        {
            assert getArguments() != null;
            String queryUrl = requireArguments().getString("queryUrl");
            try {
                gamesList = dataService.createListSearchBy(queryUrl);
                if (gamesList.size() > 0) {
                    GamesAdapter myGameAdapter = new GamesAdapter(userId, gamesList, (FragmentActivity) getContext(), action);
                    recyclerView.setAdapter(myGameAdapter);
                } else {
                    LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.search_empty, container, false);
                    handleEmptyList(recyclerView, linearLayout);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return view;
        }
        else {
            try {

                CompletableFuture<Void> createFavoriteGamesFuture = dataService.createListByAction(Objects.requireNonNull(action), userId);
                createFavoriteGamesFuture.thenAccept(result -> {
                    gamesList = dataService.getGames();
                    if (gamesList.size() > 0) {
                        GamesAdapter myGameAdapter = new GamesAdapter(userId, gamesList, (FragmentActivity) getContext(), action);
                        recyclerView.setAdapter(myGameAdapter);
                    } else {
                        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.favorites_empty, container, false);
                        handleEmptyList(recyclerView, linearLayout);
                    }
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });

                return view;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void handleEmptyList(RecyclerView recyclerView, LinearLayout linearLayout) {
        LinearLayout parent = (LinearLayout) recyclerView.getParent();
        int index = parent.indexOfChild(recyclerView);
        parent.removeView(recyclerView);
        parent.addView(linearLayout, index, recyclerView.getLayoutParams());
    }
}
