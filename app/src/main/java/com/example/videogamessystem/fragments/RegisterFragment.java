package com.example.videogamessystem.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.videogamessystem.models.User;
import com.example.videogamessystem.activities.MainActivity;
import com.example.videogamessystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public RegisterFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> regFunc(view));

        return view;
    }

    private void regFunc(View view) {
        String email = ((EditText) view.findViewById(R.id.textEmail)).getText().toString();
        String name = ((EditText) view.findViewById(R.id.textName)).getText().toString();
        String password = ((EditText) view.findViewById(R.id.textPassword)).getText().toString();

        MainActivity.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "The registration was successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        saveUserToDatabase(Objects.requireNonNull(user), name);
                        Bundle saveNameBundle = new Bundle();
                        saveNameBundle.putString("name", name);
                        saveNameBundle.putString("userId", user.getUid());
                        Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_mainGamesFragment, saveNameBundle);
                    } else {
                        Toast.makeText(getActivity(), "The registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(FirebaseUser user, String name) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        User newUser = new User(user.getEmail(), name);
        databaseReference.child(user.getUid()).setValue(newUser);
    }
}