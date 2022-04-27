package com.iot.smarthomeapp;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iot.smarthomeapp.fragment.AccountFragment;
import com.iot.smarthomeapp.fragment.ChangePasswordFragment;
import com.iot.smarthomeapp.fragment.DevicesFragment;
import com.iot.smarthomeapp.fragment.HomeFragment;
import com.iot.smarthomeapp.fragment.SettingFragment;

public class HomeNavigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_DEVICES = 1;
    private static final int FRAGMENT_SETTING = 2;
    private static final int FRAGMENT_PROFILE = 3;
    private static final int FRAGMENT_CHANGE_PASSWORD = 1;

    private int currentFragment  = FRAGMENT_HOME;

    private long backPressedTime;
    private Toast backToast;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new HomeFragment());
        navigationView.setCheckedItem(R.id.nav_home);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        View headerView = navigationView.getHeaderView(0);
        TextView userMail = headerView.findViewById(R.id.userMail);
        TextView userName = headerView.findViewById(R.id.userName);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;
                    userMail.setText(email);
                    userName.setText(fullName);
                }else {
                    Toast.makeText(HomeNavigation.this, "Can't get user data!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeNavigation.this, "Can't get user data!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
        case R.id.nav_home:
            if(currentFragment != FRAGMENT_HOME){
                replaceFragment(new HomeFragment());
                currentFragment = FRAGMENT_HOME;
            }
            break;
        case R.id.nav_devices:
            if(currentFragment != FRAGMENT_DEVICES){
                replaceFragment(new DevicesFragment());
                currentFragment = FRAGMENT_DEVICES;
            }
            break;
        case R.id.nav_setting:
            if(currentFragment != FRAGMENT_SETTING){
                replaceFragment(new SettingFragment());
                currentFragment = FRAGMENT_SETTING;
            }
            break;
        case R.id.nav_account:
            if(currentFragment != FRAGMENT_PROFILE){
                replaceFragment(new AccountFragment());
                currentFragment = FRAGMENT_PROFILE;
            }
            break;
        case R.id.nav_change_password:
            if(currentFragment != FRAGMENT_CHANGE_PASSWORD){
                replaceFragment(new ChangePasswordFragment());
                currentFragment = FRAGMENT_CHANGE_PASSWORD;
            }
            break;
        case R.id.nav_logout:
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeNavigation.this, MainActivity.class));
            break;
        default:
            replaceFragment(new HomeFragment());
            currentFragment = FRAGMENT_HOME;
            break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else{
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel();
                finishAffinity();
                return;
            } else {
                backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_layout, fragment);
        transaction.commit();
    }
}