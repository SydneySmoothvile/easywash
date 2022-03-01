package com.example.cbrapp.Database;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cbrapp.Common.Common;
import com.example.cbrapp.Interface.ICountItemInServicesListener;

import java.util.List;

public class DatabaseUtils {

    public static void getAllItemFromServices(ServicesDatabase db){
        GetAllServicesAsync task = new GetAllServicesAsync(db);
        task.execute(Common.currentUser.getMobile_number());
    }

    public static void insertToServices(ServicesDatabase db, ServicesItem...servicesItems){
        InsertToServicesAsync task = new InsertToServicesAsync(db);
        task.execute(servicesItems);
    }

    public static void countItemInServices(ServicesDatabase db, ICountItemInServicesListener iCountItemInServicesListener){
        CountItemInServicesAsync task = new CountItemInServicesAsync(db, iCountItemInServicesListener);
        task.execute();
    }

    /*
    ================================================================================================
    ASYNC TASK DEFINE
    ================================================================================================
    */

    private static class GetAllServicesAsync extends AsyncTask<String, Void, Void>{

        ServicesDatabase db;
        public GetAllServicesAsync(ServicesDatabase servicesDatabase) {
            db = servicesDatabase;
        }

        @Override
        protected Void doInBackground(String... strings) {
            getAllItemFromServicesByUserPhone(db, strings[0]);
            return null;
        }

        private void getAllItemFromServicesByUserPhone(ServicesDatabase db, String userPhone) {
            List<ServicesItem> servicesItems = db.servicesDAO().getAllItemFromServices(userPhone);
            Log.d("COUNT_SERVICES",""+servicesItems.size());
        }
    }

    private static class InsertToServicesAsync extends AsyncTask<ServicesItem, Void, Void>{

        ServicesDatabase db;
        public InsertToServicesAsync(ServicesDatabase servicesDatabase) {
            db = servicesDatabase;
        }

        @Override
        protected Void doInBackground(ServicesItem... servicesItems) {
            inserToServices(db, servicesItems[0]);
            return null;
        }

        private void inserToServices(ServicesDatabase db, ServicesItem servicesItem) {
            try{
                db.servicesDAO().insert(servicesItem);
            }catch (SQLiteConstraintException exception){
                ServicesItem updateServicesItem = db.servicesDAO().getProductInServices(servicesItem.getProductId(),
                        Common.currentUser.getMobile_number());
                updateServicesItem.setProductQuantity(updateServicesItem.getProductQuantity()+1);
                db.servicesDAO().update(updateServicesItem);
            }
        }

    }

    private static class CountItemInServicesAsync extends AsyncTask<Void, Void, Integer>{

        ServicesDatabase db;
        ICountItemInServicesListener listener;
        public CountItemInServicesAsync(ServicesDatabase servicesDatabase, ICountItemInServicesListener iCountItemInServicesListener) {
            db = servicesDatabase;
            listener = iCountItemInServicesListener;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return Integer.parseInt(String.valueOf(countItemInServicesRun(db)));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            listener.onServicesItemCountSuccess(integer.intValue());
        }

        private int countItemInServicesRun(ServicesDatabase db) {
            return db.servicesDAO().countItemInServices(Common.currentUser.getMobile_number());
        }
    }
}
