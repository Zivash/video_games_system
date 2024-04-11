package com.example.videogamessystem.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.videogamessystem.activities.MainActivity;
import com.example.videogamessystem.R;
import com.example.videogamessystem.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogInFragment extends Fragment {
    private User currentUser;
    private String userID;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public LogInFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogInFragment newInstance(String param1, String param2) {
        LogInFragment fragment = new LogInFragment();
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
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        Button loginButton = view.findViewById(R.id.loginButton);
        Button registerButton = view.findViewById(R.id.signupButton);

        loginButton.setOnClickListener(v -> loginFunc(view));
        registerButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_logInFragment_to_registerFragment));

        return view;
    }

    private void loginFunc(View view) {

        String email = ((EditText) view.findViewById(R.id.textEmail)).getText().toString();
        String password = ((EditText) view.findViewById(R.id.textPassword)).getText().toString();

        MainActivity.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Sign in successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = MainActivity.mAuth.getCurrentUser();
                        userID = Objects.requireNonNull(user).getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    currentUser = dataSnapshot.getValue(User.class);
                                    processCurrentUser(currentUser, view);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Sign in failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processCurrentUser(User currentUser, View view) {
        if (currentUser != null) {
            Bundle saveNameBundle = new Bundle();
            saveNameBundle.putString("name", currentUser.getName());
            saveNameBundle.putString("userId", userID);
            Navigation.findNavController(view).navigate(R.id.action_logInFragment_to_mainGamesFragment, saveNameBundle);
        } else {
            Log.e("Firebase", "currentUser is null");
        }
    }
}