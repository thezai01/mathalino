package com.example.math_game;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Handler handle;
    // COLOR ID
    public static int correct, wrong, normal, darkNormal, errorColor, grayNormal;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
       super.onViewCreated(view,savedInstanceState);

        handle = new Handler();

        correct = ContextCompat.getColor(getActivity(), R.color.button_correct);
        wrong = ContextCompat.getColor(getActivity(), R.color.button_wrong);
        normal = ContextCompat.getColor(getActivity(), R.color.blue);
        darkNormal =  ContextCompat.getColor(getActivity(), R.color.dark_blue);
        errorColor = ContextCompat.getColor(getActivity(), R.color.error);
        grayNormal = ContextCompat.getColor(getActivity(), R.color.seekbar_bg);

        SharedPreferences sharedPrefer = getActivity().getSharedPreferences("mathalino", MODE_PRIVATE);
        String username = sharedPrefer.getString("username", "");
        String gender = sharedPrefer.getString("gender","");
        TextView txtUName = (TextView) getView().findViewById(R.id.textviewUsername);
        txtUName.setText(username);
        CircleImageView imgUserPfp = (CircleImageView) getView().findViewById(R.id.imgViewUserPfp);

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        CircleImageView txtImgUserPfp = headerView.findViewById(R.id.imgNavUserPfp);

        if(gender.equals("Male")){
            imgUserPfp.setImageResource(R.drawable.icon_boy);
            txtImgUserPfp.setImageResource(R.drawable.icon_boy);
        }else{
            imgUserPfp.setImageResource(R.drawable.icon_girl);
            txtImgUserPfp.setImageResource(R.drawable.icon_girl);
        }


        TextView viewProfile = (TextView) getView().findViewById(R.id.textbtnHomeEditProfile);
        viewProfile.setOnClickListener(v ->{
            FragmentTransaction fragmentTransaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new UserEditFragment());
            fragmentTransaction.commit();
        });

        CardView scoreboard = (CardView) getView().findViewById(R.id.cardViewScoreboard);
        scoreboard.setOnClickListener(v ->{
            FragmentTransaction fragmentTransaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new ScoreboardFragment());
            fragmentTransaction.commit();
        });

        CardView brainTeaser = (CardView) getView().findViewById(R.id.cardViewBrainTeaser);
        brainTeaser.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), GameNum1.class);
            startActivity(intent);
            handle.postDelayed(getActivity()::finish, 0);
        });

        CardView orderOfOperation = (CardView) getView().findViewById(R.id.cardviewOrderOperation);
        orderOfOperation.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), GameNum3.class);
            intent.putExtra("game", "game2");
            startActivity(intent);
            handle.postDelayed(getActivity()::finish, 0);
        });

        CardView numberPatterns = (CardView) getView().findViewById(R.id.cardViewNumberPatterns);
        numberPatterns.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), GameNum3.class);
            intent.putExtra("game", "game3");
            startActivity(intent);
            handle.postDelayed(getActivity()::finish, 0);
        });

        CardView primeAndComposite = (CardView) getView().findViewById(R.id.cardViewPrimeComposite);
        primeAndComposite.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), GameNum4.class);
            startActivity(intent);
            handle.postDelayed(getActivity()::finish, 0);
        });

        CardView gcfAndLcd = (CardView) getView().findViewById(R.id.cardViewGCFnLCD);
        gcfAndLcd.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), GameNum5n6.class);
            intent.putExtra("game", "game5");
            startActivity(intent);
            handle.postDelayed(getActivity()::finish, 0);
        });

        CardView ratioAndProportion = (CardView) getView().findViewById(R.id.cardViewRNP);
        ratioAndProportion.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), GameNum5n6.class);
            intent.putExtra("game", "game6");
            startActivity(intent);
            handle.postDelayed(getActivity()::finish, 0);
        });

        CardView geometry = (CardView) getView().findViewById(R.id.cardViewGeometry);
        geometry.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), GameNum7.class);
            startActivity(intent);
            handle.postDelayed(getActivity()::finish, 0);
        });

    }

}