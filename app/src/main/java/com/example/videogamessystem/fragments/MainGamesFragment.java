package com.example.videogamessystem.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.videogamessystem.R;
import java.text.MessageFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainGamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainGamesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public MainGamesFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainGamesFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static MainGamesFragment newInstance(String param1, String param2) {
        MainGamesFragment fragment = new MainGamesFragment();
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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_games, container, false);

        TextView textView = view.findViewById(R.id.helloText);
        textView.setText(MessageFormat.format("Hello, {0}!", requireArguments().getString("name")));

        Button topRatingsButton = view.findViewById(R.id.topRatingButton);
        Button newestGamesButton = view.findViewById(R.id.newestButton);
        Button myFavoritesButton = view.findViewById(R.id.favoritesButton);
        Button searchByButton = view.findViewById(R.id.searchByButton);

        topRatingsButton.setOnClickListener(v -> handleClickButton(view, "Top Rating Games"));

        newestGamesButton.setOnClickListener(v -> handleClickButton(view, "Newest Games"));

        myFavoritesButton.setOnClickListener(v -> handleClickButton(view, "Favorite Games"));


        searchByButton.setOnClickListener(v -> {
            Bundle selectedButtonBundle = new Bundle();
            selectedButtonBundle.putString("userId", requireArguments().getString("userId"));
            Navigation.findNavController(view).navigate(R.id.action_mainGamesFragment_to_searchByFragment, selectedButtonBundle);
        });

        return view;
    }

    private void handleClickButton(View view, String action)
    {
        Bundle selectedButtonBundle = new Bundle();
        selectedButtonBundle.putString("action", action);
        selectedButtonBundle.putString("userId", requireArguments().getString("userId"));
        Navigation.findNavController(view).navigate(R.id.action_mainGamesFragment_to_gamesListFragment, selectedButtonBundle);
    }
}