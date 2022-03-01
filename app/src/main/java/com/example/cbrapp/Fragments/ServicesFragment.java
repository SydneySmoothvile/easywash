package com.example.cbrapp.Fragments;

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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.cbrapp.Adapter.MyServiceItemAdapter;
import com.example.cbrapp.Common.SpacesItemDecoration;
import com.example.cbrapp.Interface.IServicesDataLoadListener;
import com.example.cbrapp.Model.ServicesItem;
import example.cbrapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ServicesFragment extends Fragment implements IServicesDataLoadListener {

    //Firebase Firestore collection reference
    CollectionReference servicesItemRef;

    //interfaces
    IServicesDataLoadListener iServicesDataLoadListener;

    Unbinder unbinder;

    @BindView(R.id.chip_group)
    ChipGroup chipGroup;
    @BindView(R.id.chip_wash)
    Chip chip_wash;
    @OnClick(R.id.chip_wash)
    void washChipClick(){
        setSelectedChip(chip_wash);
        loadServicesItem("Wash");
    }
    @BindView(R.id.recycler_items)
    RecyclerView recycler_items;

    private void loadServicesItem(String itemMenu) {
        ///Services/Wash/Items
        servicesItemRef = FirebaseFirestore.getInstance().collection("Services")
                .document(itemMenu)
                .collection("Items");
        //get Data
        servicesItemRef.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iServicesDataLoadListener.onServicesDataLoadFailure(e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<ServicesItem> servicesItems = new ArrayList<>();
                    for(DocumentSnapshot itemSnapShot:task.getResult()){
                        ServicesItem servicesItem = itemSnapShot.toObject(ServicesItem.class);
                        servicesItems.add(servicesItem);
                    }
                    iServicesDataLoadListener.onServicesDataLoadSuccess(servicesItems);
                }
            }
        });
    }

    private void setSelectedChip(Chip chip) {
        //set color
        for(int i=0; i<chipGroup.getChildCount(); i++){
            Chip chipItem = (Chip)chipGroup.getChildAt(i);
            if(chipItem.getId() != chip.getId())// If not selected
            {
                chipItem.setChipBackgroundColorResource(android.R.color.darker_gray);
                chipItem.setTextColor(getResources().getColor(android.R.color.white));
            }
            else //if selected
            {
                chipItem.setChipBackgroundColorResource(android.R.color.holo_orange_dark);
                chipItem.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    @BindView(R.id.chip_paint)
    Chip chip_paint;
    @OnClick(R.id.chip_paint)
    void paintChipClick(){
        setSelectedChip(chip_paint);
        loadServicesItem("Paint");
    }

    @BindView(R.id.chip_systems)
    Chip chip_systems;
    @OnClick(R.id.chip_systems)
    void systemsChipClick(){
        setSelectedChip(chip_systems);
        loadServicesItem("Systems");
    }


    public ServicesFragment(){

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView;
        itemView = inflater.inflate(R.layout.fragment_services,container, false);

        unbinder = ButterKnife.bind(this, itemView);
        iServicesDataLoadListener = this;

        //Default load
        loadServicesItem("Wash");

        initView();

        return itemView;
    }

    private void initView() {
        recycler_items.setHasFixedSize(true);
        recycler_items.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycler_items.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onServicesDataLoadSuccess(List<ServicesItem> servicesItemList) {
        MyServiceItemAdapter adapter = new MyServiceItemAdapter(getContext(), servicesItemList);
        recycler_items.setAdapter(adapter);
    }

    @Override
    public void onServicesDataLoadFailure(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

