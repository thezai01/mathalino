package com.example.math_game;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.method.*;
import android.view.*;
import android.widget.*;

import com.google.android.material.navigation.NavigationView;

import java.text.*;
import java.util.*;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserEditFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DatePickerDialog dpDialog;
    Button UPBday, UPConfirm;
    EditText eTxtName, eTxtUsername, eTxtPass, eTxtGender;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM / dd / yyyy", Locale.getDefault());
    SQLiteDatabase db;
    Cursor cursor;

    SharedPreferences sharedPrefer;

    String textDate[] = {""}, username, userDate;
    String[] dateMonth;
    private int cYear = calendar.get(Calendar.YEAR), cMonth = calendar.get(Calendar.MONTH), cDay = calendar.get(Calendar.DAY_OF_MONTH),  age[] = {0};

    private static Dialog errorDialog, confirmDialog;

    public UserEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserEditFragment newInstance(String param1, String param2) {
        UserEditFragment fragment = new UserEditFragment();
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
        return inflater.inflate(R.layout.fragment_user_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        errorDialog = new Dialog(requireActivity());
        confirmDialog = new Dialog(requireActivity());

        sharedPrefer = getActivity().getSharedPreferences("mathalino", MODE_PRIVATE);
        username = sharedPrefer.getString("username", "");
        eTxtName = getView().findViewById(R.id.edtTextUPName);
        eTxtUsername = getView().findViewById(R.id.edtTextUPUName);
        eTxtPass = getView().findViewById(R.id.edtTxtUPPassword);
        eTxtGender = getView().findViewById(R.id.edtTxtUPGender);

        CircleImageView imgUserPfp= getView().findViewById(R.id.imgViewUPProfile);

        eTxtUsername.setText(username);

        // DATABASE
        db = getActivity().openOrCreateDatabase("accDetails", Context.MODE_PRIVATE, null);
        cursor = db.rawQuery("SELECT * FROM tableAcc WHERE username ='" + username + "'", null);

        cursor.moveToFirst();
        eTxtName.setText(cursor.getString(2));
        eTxtPass.setText(cursor.getString(1));
        userDate = cursor.getString(3);
        eTxtGender.setText(cursor.getString(5));
        if(cursor.getString(5).equals("Male")) imgUserPfp.setImageResource(R.drawable.icon_boy);
        else imgUserPfp.setImageResource(R.drawable.icon_girl);
        age[0] = cursor.getInt(4);
        cursor.close();

        // FOR DATE PICKER
        dateMonth = getResources().getStringArray(R.array.dateMonth);
        UPBday = getView().findViewById(R.id.btnUPBday);
        UPBday.setText(userDate);
        setCDate();
        dpDialog = CommonUtils.initDatePicker(requireActivity(), dpDialog, calendar, textDate, dateMonth, age, cYear, cMonth, cDay, UPBday);
        UPBday.setOnClickListener(v ->  dpDialog.show());

        CheckBox UPShowPass= getView().findViewById(R.id.chckBoxUPShowPass);
        UPShowPass.setOnClickListener(v -> {
            if(UPShowPass.isChecked())  eTxtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else  eTxtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        });

        ImageView exit = (ImageView) getView().findViewById(R.id.imgViewUPExit);
        exit.setOnClickListener(v -> returnHome());

        Button UPCancel = getView().findViewById(R.id.btnUserProfileCancel);
        UPCancel.setOnClickListener(v -> returnHome());

        UPConfirm = (Button) getView().findViewById(R.id.btnUserProfileSave);
        UPConfirm.setOnClickListener(v -> editProfile());

    }

    // SETTING DATE-PICKER DEFAULT, USER BIRTHDATE
    public void setCDate(){
        try {
            Date date = dateFormat.parse(userDate);

            cMonth = date.getMonth();
            cDay = date.getDate();
            cYear = date.getYear() + 1900;

        } catch (ParseException e) {throw new RuntimeException(e);}
    }

    // EDITING USER PROFILE
    public void editProfile(){

        cursor = db.rawQuery("SELECT * FROM tableAcc WHERE username ='" + eTxtUsername.getText() + "'", null);
        if(cursor.getCount() >= 1 && !(String.valueOf(eTxtUsername.getText()).equals(username))){
            CommonUtils.errorMsg(errorDialog, requireActivity(),R.layout.activity_error_msg, "The username you entered already exists." );
            cursor.close();
            return;
        }
        cursor.close();

        if(!CommonUtils.passChecker(String.valueOf(eTxtPass.getText()))){
            CommonUtils.errorMsg(errorDialog, requireActivity(),R.layout.activity_error_msg, "The password should be 8-15 characters and with atleast one number." );
            return;
        }

        cursor = db.rawQuery("SELECT * FROM tableAcc WHERE username ='" + username + "'", null);
        cursor.moveToFirst();

        // ADD VALUES TO CONTENT VALUES
        ContentValues values = new ContentValues();
        values.put("username", String.valueOf(eTxtUsername.getText()));
        values.put("uPass", String.valueOf(eTxtPass.getText()));
        values.put("uName", String.valueOf(eTxtName.getText()));
        values.put("uBday", String.valueOf(UPBday.getText()));

        // UPDATE HOME FRAGMENT
        SharedPreferences.Editor editor = sharedPrefer.edit();
        editor.putString("username", String.valueOf(eTxtUsername.getText()));
        editor.apply();

        // UPDATE NAV BAR
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView txtUName = headerView.findViewById(R.id.txtNavHeaderName);
        txtUName.setText(String.valueOf(eTxtUsername.getText()));

        // UPDATE DB
        db.update("tableAcc",
                values,
                "username = ?",
                new String[]{username});
        cursor.close();
        username = sharedPrefer.getString("username", "");

        CommonUtils.welcome(confirmDialog, requireActivity(), R.layout.activity_success,"Edit Success!","EDIT PROFILE",
                result -> { if (result == 1) returnHome(); });
    }

    public void returnHome(){
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new HomeFragment());
        fragmentTransaction.commit();
    }

}