package com.example.cbrapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.example.cbrapp.Fragments.BookingFragment;
import com.example.cbrapp.Fragments.HomeFragment;
import com.example.cbrapp.Fragments.ProfileFragment;

import example.cbrapp.R;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //interface declaration
    private UserPhoneNumberListener listener;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    ImageView userProfilePic;
    TextView userName, carNumberPlate;
    FirebaseFirestore fstore;

    public static String customerName, userMobileNumber, userCarNumberPlate, userEmail;

    //user unique ID from firebase
    String userID;

    /**
     *declaration of string url
     **/

    /**private Button signOut; **/
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    public interface UserPhoneNumberListener {
        void userPhoneNumberSent(String input);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout =findViewById(R.id.drawer_main);
        navigationView = findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        //load default fragment

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new HomeFragment());
        fragmentTransaction.commit();

        //get user information
        getUserInfo();

        //store customerPhoneNumber

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        /**
         * checks if user is logged in
         **/
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        updateDrawerHeader();

        //get firestore instance
        fstore = FirebaseFirestore.getInstance();

        //get user unique ID from fire store
        userID = auth.getCurrentUser().getUid();

    }

    //sign out method
    public void signOut() {
        auth.signOut();
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(menuItem.getItemId() == R.id.action_home){
            //go to home dashboard
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new HomeFragment());
            fragmentTransaction.commit();
        }
        if(menuItem.getItemId() == R.id.action_profile){
            //open profile fragment
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new ProfileFragment());
            fragmentTransaction.commit();
        }
        if(menuItem.getItemId() == R.id.action_car_wash){
            //open cash wash booking
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new BookingFragment());
            fragmentTransaction.commit();

        }
        if(menuItem.getItemId() == R.id.action_log_out){
            signOut();
        }

        return true;
    }

    public void updateDrawerHeader(){
        navigationView = findViewById(R.id.main_navigation_view);
        View headerView = navigationView.getHeaderView(0);

        //retrieve user Text view
        userProfilePic = headerView.findViewById(R.id.profile_pic);
        userName = headerView.findViewById(R.id.profile_name);
        carNumberPlate = headerView.findViewById(R.id.car_number);

        //get documents collections from firestore
        DocumentReference documentReference = fstore.collection("users").document(userID);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                userName.setText(documentSnapshot.getString("username"));
                carNumberPlate.setText(documentSnapshot.getString("car_number_plate"));
                userMobileNumber = documentSnapshot.getString("mobile_number");
            }
        });

    }

    public void getUserInfo() {
        /**
         * get user information
         **/
        //init firebase
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseUser user = fauth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();
        userID = fauth.getCurrentUser().getUid();

        //get documents collections from firestore
        DocumentReference documentReference = fstore.collection("users").document(userID);

        //get user information
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                customerName = documentSnapshot.getString("username");
                userMobileNumber = documentSnapshot.getString("mobile_number");
                userCarNumberPlate = documentSnapshot.getString("car_number_plate");
                userEmail = documentSnapshot.getString("email");
                CustomerNumber customerNumber = com.example.cbrapp.CustomerNumber.getInstance();
                customerNumber.setData(documentSnapshot.getString("mobile_number"));

            }
        });
    }

}