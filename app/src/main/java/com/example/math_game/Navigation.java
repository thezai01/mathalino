package com.example.math_game;

import static com.example.math_game.CommonUtils.sharedPrefer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

public class Navigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    Handler handle;
    Dialog confirmDialog;
    SQLiteDatabase db;

    NavigationView navigationView;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        sharedPrefer = getSharedPreferences("mathalino", MODE_PRIVATE);
        CommonUtils.username = sharedPrefer.getString("username", "");

        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView txtUName = headerView.findViewById(R.id.txtNavHeaderName);
        txtUName.setText(CommonUtils.username);

        handle = new Handler();
        confirmDialog = new Dialog(this);

        db = openOrCreateDatabase("accDetails", Context.MODE_PRIVATE, null);
        cursor = db.rawQuery("SELECT uExp FROM tableAcc WHERE username ='" + CommonUtils.username + "'", null);
        cursor.moveToFirst();
        int userExp = cursor.getInt(0);
        int lvlExpLvl = getResources().getInteger(R.integer.lvl_exp);
        int lvl = 1;
        setHeader(userExp, lvlExpLvl, lvl, headerView);
        cursor.close();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        TextView exit = findViewById(R.id.txt_exit);
        exit.setOnClickListener(v ->{
                 this.finish();
                System.exit(0);
        });


    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int getId = item.getItemId();

        if(getId == R.id.nav_home){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }else if(getId == R.id.nav_setting){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
        }else if(getId == R.id.nav_tutorial){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TutorialFragment()).commit();
        }else if(getId == R.id.txt_exit){
            showConfirm(R.layout.activity_confirm_msg, "Logout","Are you sure about that?");
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.findFragmentById(R.id.fragment_container);
    }

    // CONFIRM DIALOG BOX
    public void showConfirm(int view, String title, String msg){
        ImageView exitIcon;
        Button cancel_btn, confirm_btn;
        TextView changeTitle, changeMsg;

        View.OnClickListener commonClickListener = v -> {
            if(getCurrentFragment().getClass().equals(HomeFragment.class)) navigationView.getMenu().getItem(0).setChecked(true);
            else if(getCurrentFragment().getClass().equals(SettingFragment.class)) navigationView.getMenu().getItem(1).setChecked(true);
            else if(getCurrentFragment().getClass().equals(TutorialFragment.class)) navigationView.getMenu().getItem(2).setChecked(true);
            confirmDialog.dismiss();
        };

        confirmDialog.setContentView(view);
        exitIcon = confirmDialog.findViewById(R.id.success_exit);
        exitIcon.setOnClickListener(v -> {
            exitIcon.setColorFilter(ContextCompat.getColor(this, R.color.error), PorterDuff.Mode.SRC_IN);
            commonClickListener.onClick(v);
        });

        changeTitle = confirmDialog.findViewById(R.id.success_title);
        changeTitle.setText(title);
        changeMsg = confirmDialog.findViewById(R.id.success_msg);
        changeMsg.setText(msg);

        cancel_btn = confirmDialog.findViewById(R.id.dialog_cancel);
        cancel_btn.setOnClickListener(commonClickListener);

        confirm_btn = confirmDialog.findViewById(R.id.success_ok);
        confirm_btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            confirmDialog.dismiss();
            handle.postDelayed(this::finish, 0);
        });

        confirmDialog.show();
    }

    // SETTING UP NAVIGATION HEADER'S DATA (USER LVL, USER EXP, USER TITLE)
    public void setHeader(int userExp, int lvlExpLvl, int lvl, View headerView){
        while(userExp >= lvlExpLvl){
            lvl++;
            userExp -= lvlExpLvl;
            lvlExpLvl *= 2;
        }
        TextView txtLevel = headerView.findViewById(R.id.txtNavUserLvl);
        txtLevel.setText("Level "+lvl);
        TextView txtExp = headerView.findViewById(R.id.txtNavLvlXp);
        txtExp.setText(userExp+"/"+lvlExpLvl+" exp");

        ProgressBar progBarLvl = headerView.findViewById(R.id.progBarNavLvl);
        progBarLvl.setMax(lvlExpLvl);
        progBarLvl.setProgress(userExp);

        int title = lvl/5;
        String titleName[] = getResources().getStringArray(R.array.nameTitle);
        TextView txtTitle = headerView.findViewById(R.id.txtNavHeaderUserTitle);
        txtTitle.setText(titleName[title]);
    }
}