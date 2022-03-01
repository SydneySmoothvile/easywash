package com.example.cbrapp.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ServicesDAO {
    @Query("SELECT * FROM Services WHERE userPhone=:userPhone")
    List<ServicesItem> getAllItemFromServices(String userPhone);

    @Query("SELECT COUNT(*) FROM Services WHERE userPhone=:userPhone")
    int countItemInServices(String userPhone);

    @Query("SELECT * from Services where productId=:productId AND userPhone=:userPhone")
    ServicesItem getProductInServices(String productId, String userPhone);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insert(ServicesItem...services);

    @Update(onConflict = OnConflictStrategy.FAIL)
    void update(ServicesItem service);

    @Delete
    void delete(ServicesItem servicesItem);

    @Query("DELETE FROM Services WHERE userPhone=:userPhone")
    void clearServices(String userPhone);


}
