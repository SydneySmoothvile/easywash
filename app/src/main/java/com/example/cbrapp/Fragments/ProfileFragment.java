package com.example.cbrapp.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import example.cbrapp.R;

public class ProfileFragment extends Fragment {

    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    FirebaseUser user;
    FirebaseDatabase fdatabase;
    DatabaseReference databaseReference;
    //storage
    StorageReference storageReference;
    //path where images of user profile will be stored
    String storagePath = "Users_Profile_Imgs/";

    //progress dialog
    ProgressDialog pd;

    //Permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    //uri of picked image
    Uri image_uri;
    //checking profile photo
    String profilePhoto;

    //Array of Permissions to be request
    String cameraPermissions[];
    String storagePermissions[];

    //user unique ID from firebase
    String userID, profileImage;

    ImageView profileIv;
    TextView nameTv, emailTv, phoneNumberTv, carNumberPlateTv;
    private Button updateProfileBtn;
    private EditText inputEmail, inputUserName, inputMobileNumber, inputCarNumberPlate;

    public static String customerPhoneNumber = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view;
        view = inflater.inflate(R.layout.fragment_profile,container, false);

        //init firebase
        fauth = FirebaseAuth.getInstance();
        user = fauth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();
        userID = fauth.getCurrentUser().getUid();

        //init array of permissions
        cameraPermissions = new String[]{Manifest.
                permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init progress dialog
        pd = new ProgressDialog(getActivity());


        //get documents collections from firestore
        DocumentReference documentReference = fstore.collection("users").document(userID);

        //init views
        profileIv = view.findViewById(R.id.user_profile_image);

        //init edit text view
        nameTv = view.findViewById(R.id.user_name_profile);
        emailTv = view.findViewById(R.id.user_email_profile);
        phoneNumberTv = view.findViewById(R.id.user_phone_No);
        carNumberPlateTv = view.findViewById(R.id.user_car_No_plate);

        //init update button
        updateProfileBtn = view.findViewById(R.id.update_button);

        //display user information
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //User user = documentSnapshot.toObject(User.class);
                nameTv.setText(documentSnapshot.getString("username"));
                emailTv.setText(documentSnapshot.getString("email"));
                phoneNumberTv.setText(documentSnapshot.getString("mobile_number"));
                carNumberPlateTv.setText(documentSnapshot.getString("car_number_plate"));

            }
        });

        TextView  phoneNumber = (TextView) view.findViewById(R.id.user_phone_No);
        customerPhoneNumber = phoneNumber.getText().toString();


        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showEditProfileDialog();
                String firstName = nameTv.getText().toString();
                String email = emailTv.getText().toString();
                String phone = phoneNumberTv.getText().toString();
                String carplatenumber = carNumberPlateTv.getText().toString();

            }
        });

        return view;
    }

}
