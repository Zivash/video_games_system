package com.example.videogamessystem.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.videogamessystem.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchByFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchByFragment extends Fragment {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public SearchByFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchByFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchByFragment newInstance(String param1, String param2) {
        SearchByFragment fragment = new SearchByFragment();
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
    public void onPause() {
        super.onPause();
        EditText searchText = requireView().findViewById(R.id.textSeacrh);
        searchText.setText("");
        EditText publisherText = requireView().findViewById(R.id.textPublisher);
        publisherText.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_by, container, false);

        EditText searchText = view.findViewById(R.id.textSeacrh);
        EditText publisherNameText = view.findViewById(R.id.textPublisher);

        Button datesButton = view.findViewById(R.id.datesButton);
        Button genresButton = view.findViewById(R.id.genresButton);
        Button platformsButton = view.findViewById(R.id.platformsButton);
        Button searchButton = view.findViewById(R.id.searchButton);

        datesButton.setOnClickListener(v -> createPickerDates((datesButton)));

        String[] genresArray = {"Action", "Adventure", "Indie", "Platformer", "Puzzle", "Shooter", "Simulation", "Sports", "Strategy"};
        boolean[] genresPicked = new boolean[genresArray.length];
        ArrayList<Integer> selectedGenres = new ArrayList<>();

        genresButton.setOnClickListener(v -> createMultiChoices(genresButton, genresArray, genresPicked, selectedGenres, "genres"));

        String[] platformsArray = {"Android", "iOS", "Linux", "macOS", "PC", "PlayStation 3", "Playstation 4", "PlayStation 5", "Web", "Wii", "Xbox 360", "Xbox One"};
        boolean[] platformsPicked = new boolean[platformsArray.length];
        ArrayList<Integer> selectedPlatforms = new ArrayList<>();

        Map<String, Integer> platformsDict = createPlatformsDict();

        platformsButton.setOnClickListener(v -> createMultiChoices(platformsButton, platformsArray, platformsPicked, selectedPlatforms, "platforms"));

        StringBuilder searchQuery = new StringBuilder("https://api.rawg.io/api/games?key=2efe707696da4f3c913e206ef53ffbcc");

        searchButton.setOnClickListener(v -> {

            String gameName = searchText.getText().toString().replace(" ", "-");
            if (!gameName.isEmpty()) {
                searchQuery.append("&search=").append(gameName).append("&search_exact");
            }

            String publisherName = publisherNameText.getText().toString().replace(" ", "-");
            if (!publisherName.isEmpty()) {
                searchQuery.append("&publishers=").append(publisherName);
            }

            String releasedDates = datesButton.getText().toString();
            if (!releasedDates.equals("Select released dates")) {
                releasedDates = releasedDates.replace(" - ", ",");
                searchQuery.append("&dates=").append(releasedDates);
            }

            String genres = genresButton.getText().toString();
            if (!genres.equals("Select genres")) {
                genres = genres.replace(", ", ",");
                searchQuery.append("&genres=").append(genres);
            }

            String platforms = platformsButton.getText().toString();

            if (!platforms.equals("Select platforms")) {
                StringBuilder valuesByKey = new StringBuilder();
                String[] parts = platforms.split(", ");
                for (String part : parts) {
                    valuesByKey.append(platformsDict.get(part)).append(",");
                }
                valuesByKey.deleteCharAt(valuesByKey.length() - 1);

                searchQuery.append("&platforms=").append(valuesByKey);
            }

            String searchQueryUrl = searchQuery.append("&page=").toString().toLowerCase();

            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryUrl", searchQueryUrl);
            queryBundle.putString("action", "Search By");
            queryBundle.putString("userId", requireArguments().getString("userId"));
            Navigation.findNavController(view).navigate(R.id.action_searchByFragment_to_gamesListFragment, queryBundle);
        });

        return view;
    }

    private void createPickerDates(Button datesButton) {
        android.util.Pair<Long, Long> androidPair = android.util.Pair.create(
                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                MaterialDatePicker.todayInUtcMilliseconds()
        );

        Pair<Long, Long> androidxPair = new Pair<>(androidPair.first, androidPair.second);

        MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(androidxPair).setTheme(R.style.ThemeOverlay_App_MaterialCalendar).build();
        datePicker.show(requireActivity().getSupportFragmentManager(), "range");

        datePicker.addOnPositiveButtonClickListener(selection -> {

            long startDateInMillis = selection.first;
            long endDateInMillis = selection.second;

            Date startDate = new Date(startDateInMillis);
            Date endDate = new Date(endDateInMillis);

            String formattedStartDate = dateFormat.format(startDate);
            String formattedEndDate = dateFormat.format(endDate);

            datesButton.setText(formattedStartDate + " - " + formattedEndDate);
        });

    }

    private void createMultiChoices(Button genresButton, String[] optionsArray, boolean[] optionsPicked, ArrayList<Integer> selectedOptions, String request) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyCheckBox);

        builder.setTitle("Select " + request);

        builder.setCancelable(false);

        builder.setMultiChoiceItems(optionsArray, optionsPicked, (dialogInterface, index, clicked) -> {
            if (clicked) {

                selectedOptions.add(index);
                Collections.sort(selectedOptions);
            } else {
                selectedOptions.remove(Integer.valueOf(index));
            }
        });

        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (int index = 0; index < selectedOptions.size(); index++) {
                stringBuilder.append(optionsArray[selectedOptions.get(index)]);
                if (index != selectedOptions.size() - 1) {
                    stringBuilder.append(", ");
                }
            }

            genresButton.setText(stringBuilder.toString());
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setNeutralButton("Clear All", (dialogInterface, i) -> {

            for (int index = 0; index < optionsPicked.length; index++) {
                optionsPicked[index] = false;
                selectedOptions.clear();
                genresButton.setText("Selected " + request);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button cancelButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button clearAllButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);

        okButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
        cancelButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
        clearAllButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
    }

    private Map<String, Integer> createPlatformsDict() {
        Map<String, Integer> platformsDict = new HashMap<>();

        platformsDict.put("Android", 21);
        platformsDict.put("iOS", 3);
        platformsDict.put("Linux", 6);
        platformsDict.put("macOS", 5);
        platformsDict.put("PC", 4);
        platformsDict.put("PlayStation 3", 16);
        platformsDict.put("PlayStation 4", 18);
        platformsDict.put("PlayStation 5", 187);
        platformsDict.put("Web", 171);
        platformsDict.put("Wii", 11);
        platformsDict.put("Xbox 360", 14);
        platformsDict.put("Xbox One", 1);

        return platformsDict;
    }
}