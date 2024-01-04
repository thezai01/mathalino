package com.example.math_game;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScoreboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int[] txtScoreID = { R.id.txtViewBTHS, R.id.txtViewBTAS, R.id.txtViewOOOHS, R.id.txtViewOOOAS,
            R.id.txtViewNPHS, R.id.txtViewNPAS, R.id.txtViewPCHS, R.id.txtViewPCAS,
            R.id.txtViewGALHS, R.id.txtViewGALAS, R.id.txtViewRAPHS, R.id.txtViewRAPAS,
            R.id.txtViewGHS, R.id.txtViewGAS
    };

    TextView[] txtScore = new TextView[14];
    float[] scoreAve = new float[7];

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScoreboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScoreboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScoreboardFragment newInstance(String param1, String param2) {
        ScoreboardFragment fragment = new ScoreboardFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scoreboard, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPrefer = getActivity().getSharedPreferences("mathalino", MODE_PRIVATE);
        String username = sharedPrefer.getString("username", "");
        String gender = sharedPrefer.getString("gender","");
        TextView SBUser = getView().findViewById(R.id.txtScoreboardUsername);
        SBUser.setText(username);
        CircleImageView imgUserPfp = getView().findViewById(R.id.imgSBUserPfp);
        if(gender.equals("Male"))imgUserPfp.setImageResource(R.drawable.icon_boy);
        else imgUserPfp.setImageResource(R.drawable.icon_girl);

        TextView viewProfile = (TextView) getView().findViewById(R.id.textbtnEditProfile);
        viewProfile.setOnClickListener(v ->{
            FragmentTransaction fragmentTransaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new UserEditFragment());
            fragmentTransaction.commit();

        });

        for (int i = 0; i < txtScoreID.length; i++) txtScore[i] = getView().findViewById(txtScoreID[i]);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        float[] scoreSummary = dbHelper.dbScoreSummary();

        for (int i = 0; i < scoreSummary.length - 1; i++) txtScore[i].setText(String.valueOf((int) scoreSummary[i]));



    }

}