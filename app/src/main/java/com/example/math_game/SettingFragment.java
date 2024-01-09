package com.example.math_game;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Switch hasSound;
    SeekBar musicVolume, sfxVolume, masterVolume;
    Button save, close;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    void init(){
        hasSound = getView().findViewById(R.id.switch_sounds);
        masterVolume = getView().findViewById(R.id.seekB_masterV);
        musicVolume = getView().findViewById(R.id.seekB_musicV);
        sfxVolume = getView().findViewById(R.id.seekB_soundE);

        hasSound.setChecked(SoundManager.hasSounds);
        masterVolume.setProgress(Math.round(SoundManager.masterVolume));
        musicVolume.setProgress(Math.round(SoundManager.musicVolume));
        sfxVolume.setProgress(Math.round(SoundManager.sfxVolume));

        save = getView().findViewById(R.id.btnSettingSave);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSoundSettings();
                Toast.makeText(requireContext(), "Successfully Changed Volume Settings!", Toast.LENGTH_SHORT).show();
            }
        });

        close = getView().findViewById(R.id.btnSettingsClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), Navigation.class);
                startActivity(intent);
            }
        });
    }

    void changeSoundSettings(){
        SoundManager.hasSounds = hasSound.isChecked();
        SoundManager.masterVolume = masterVolume.getProgress();
        SoundManager.musicVolume = musicVolume.getProgress();
        SoundManager.sfxVolume = sfxVolume.getProgress();
    }
}