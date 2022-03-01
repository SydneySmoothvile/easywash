package com.example.cbrapp.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.example.cbrapp.Adapter.MyGarageAdapter;
import com.example.cbrapp.Common.Common;
import com.example.cbrapp.Common.SpacesItemDecoration;
import com.example.cbrapp.Interface.IAllGarageLoadListener;
import com.example.cbrapp.Interface.IBranchLoadListener;
import com.example.cbrapp.Model.Garage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import example.cbrapp.R;

public class BookingStep1Fragment extends Fragment implements IAllGarageLoadListener, IBranchLoadListener {
    //Variables
    CollectionReference allGarageRef;
    CollectionReference branchRef;

    IAllGarageLoadListener iAllGarageLoadListener;
    IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.recycler_garage)
    RecyclerView recyclerView;

    Unbinder unbinder;

    AlertDialog dialog;

    static BookingStep1Fragment instance;

    public static BookingStep1Fragment getInstance(){
        if (instance==null){
            instance = new BookingStep1Fragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allGarageRef = FirebaseFirestore.getInstance().collection("Garages");
        iAllGarageLoadListener = this;
        iBranchLoadListener = this;
        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if(Common.USER_MOBILE_NUMBER != null)
            Toast.makeText(getActivity(),"SUCCESS!! ",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(),"Error!! ",Toast.LENGTH_SHORT).show();

        View itemView = inflater.inflate(R.layout.fragment_booking_step_one,container,false);
        unbinder = ButterKnife.bind(this, itemView);

        initView();
        LoadAllGarage() ;


        return itemView;
    }

    private void initView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.addItemDecoration(new SpacesItemDecoration(4));

    }

    private void LoadAllGarage() {
        allGarageRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<String> list = new ArrayList<>();
                            list.add(("Please Choose Location"));
                            for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                                list.add(documentSnapshot.getId());
                            iAllGarageLoadListener.onAllGarageLoadSuccess(list);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iAllGarageLoadListener.onAllGarageLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllGarageLoadSuccess(List<String> areaNameList) {
        spinner.setItems(areaNameList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position>0){
                    loadBranchOfLocation(item.toString());
                }else {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

    }

    private void loadBranchOfLocation(String locationName) {
        dialog.show();
        Common.location = locationName;

        branchRef = FirebaseFirestore.getInstance()
                .collection("Garages")
                .document(locationName)
                .collection("Branch");

        branchRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            List<Garage> list = new ArrayList<>();
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                        Garage garage = documentSnapshot.toObject(Garage.class);
                        garage.setGarageId(documentSnapshot.getId());
                        list.add(garage);
                    }
                    iBranchLoadListener.onBranchLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBranchLoadListener.onBranchLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllGarageLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBranchLoadSuccess(List<Garage> garageList) {
        MyGarageAdapter adapter = new MyGarageAdapter(getActivity(), garageList);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

        dialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
