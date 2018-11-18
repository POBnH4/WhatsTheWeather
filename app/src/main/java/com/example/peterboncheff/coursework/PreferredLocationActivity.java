package com.example.peterboncheff.coursework;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//add recyclerView.setNestedScrollingEnabled(false); to your RecyclerView

import static android.widget.AdapterView.*;
import static com.example.peterboncheff.coursework.MainActivity.*;

public class PreferredLocationActivity extends AppCompatActivity
        implements View.OnClickListener, Observer<List<Module>>, OnItemClickListener {

    public static final int DEFAULT_SPINNER_SELECTION_INDEX = 0;

    private ModuleDao moduleDao;
    private Spinner spinner;
    private ModuleAdapter spinnerAdapter;
    private TextView currentCityTemperature, currentLastUpdated;
    private String addInputText = "";
    private ModuleViewModel moduleViewModel;
    private Module selectedModule;

    private static ModuleRepository INSTANCE;
    private ModuleDatabase database;
    private LiveData<List<Module>> observableLocationList;


//    private PreferredLocationActivity(final ModuleDatabase database){
//        this.database = database;
//        this.observableLocationList = this.database.moduleDao().getAllModules();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred_location);
        init();

    }

    private void init() {

        Button btnAdd = findViewById(R.id.btn_add);
        Button btnDel = findViewById(R.id.btn_deleteSelected);
        Button btnUpdate = findViewById(R.id.btn_updateSelected);
        Button buttonSetMainBackground = findViewById(R.id.buttonSetMainCity);

        buttonSetMainBackground.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        ModuleDatabase db = ModuleDatabase.getDatabase(this);
        this.moduleDao = db.moduleDao();

        this.currentCityTemperature = findViewById(R.id.currentCityTemperature);
        this.currentLastUpdated = findViewById(R.id.currentCityLastUpdated);

        setSpinner();
        this.moduleViewModel = ViewModelProviders.of(this).get(ModuleViewModel.class);
        updateModulesSpinner();
    }

    private void setSpinner(){
        spinnerAdapter = new ModuleAdapter(this, R.layout.module_spinner_item, new ArrayList<Module>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.sp_modules);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(DEFAULT_SPINNER_SELECTION_INDEX);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Object item = parent.getItemAtPosition(position);
                if(item instanceof Module) taskLoadUp(((Module) item).getReference());
                else Log.d("hallo", "Error in PreferredLocationActivity, Spinner on item selected");
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        updateModulesSpinner();
    }

    private void addModule(Module module){
        moduleViewModel.insertModule(module);
    }

    private void deleteModule(Module module){
        moduleViewModel.deleteModule(module);
    }

    private void updateModule(Module module){
        moduleViewModel.updateModule(module);
    }

    private void updateModulesSpinner(){
        final String message = "Retrieving modules";
        Log.d("hallo", message);
       // moduleViewModel.getAllModules().observe(this,this);
    }

    private void refreshModulesSpinner(List<Module> modules){
        spinnerAdapter.clear();
        spinnerAdapter.addAll(modules);
        spinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        view.setSelected(true);
        selectedModule = spinnerAdapter.getItem(position);
    }

    @Override
    public void onChanged(@Nullable List<Module> modules) {
        refreshModulesSpinner(modules);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateModulesSpinner();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            final int SCQF_CREDITS = 10;
            showAddDialog(false); // adding new value, not updating -> false;
            Module m = new Module();
            m.setReference(addInputText);
            m.setCreatedOn(new Date());
            m.setScqfCredits(SCQF_CREDITS);
//            InsertModuleTask task = new InsertModuleTask();
//            task.execute(m);
        } else if (v.getId() == R.id.btn_deleteSelected) {
            Object selectedObj = spinner.getSelectedItem();
            if (selectedObj != null) {
                Module selectedModule = (Module) selectedObj;
                DeleteModuleTask task = new DeleteModuleTask();
                task.execute(selectedModule);
            }
        } else if (v.getId() == R.id.btn_updateSelected) {
            Object selectedObj = spinner.getSelectedItem();
            if (selectedObj != null) {
                Module selectedModule = (Module) selectedObj;
                showAddDialog(true); // updating an existing value -> true;
                taskLoadUp(addInputText);
                selectedModule.setReference(addInputText);
                UpdateModuleTask task = new UpdateModuleTask();
                task.execute(selectedModule);
            }
        } else if (v.getId() == R.id.buttonSetMainCity) {
            //set current city to be on main page;
        }
    }

//    private void updateModulesSpinner() {
//        Log.d("hallo", "Retrieving modules");
//        RetrieveAllModulesTask task = new RetrieveAllModulesTask();
//        task.execute();
//    }


    private void showAddDialog(boolean isUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(isUpdate) builder.setTitle("Update your location");
        else builder.setTitle("Set new location");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addInputText = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

//    @SuppressLint("StaticFieldLeak")
//    class RetrieveAllModulesTask extends AsyncTask<Void, Void, List<Module>> {
//
//        @Override
//        protected List<Module> doInBackground(Void... voids) {
//            return moduleDao.getAllModules();
//        }
//
//        @Override
//        protected void onPostExecute(List<Module> modules) {
//            super.onPostExecute(modules);
//            refreshModulesSpinner(modules);
//        }
//    }

    @SuppressLint("StaticFieldLeak")
    static
    class InsertModuleTask extends AsyncTask<Module, Void, Void> {
        @Override
        protected Void doInBackground(Module... module) {

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            //updateModulesSpinner();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DeleteModuleTask extends AsyncTask<Module, Void, Void> {

        @Override
        protected Void doInBackground(Module... modules) {
            moduleDao.deleteModules(modules);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateModulesSpinner();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class UpdateModuleTask extends AsyncTask<Module, Void, Void> {

        @Override
        protected Void doInBackground(Module... modules) {
            moduleDao.updateModules(modules);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateModulesSpinner();
        }
    }


    private void taskLoadUp(String query) {
        if (checkNetworkAvailability(getApplicationContext())) {
            PreferredLocationActivity.DownloadTask task = new DownloadTask();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            return MainActivity.execute(BEGINNING_API_HTTP + urls[0] + END_OF_API_HTTP);
        }

        @Override
        protected void onPostExecute(String str) {
            try {
                final JSONObject json = new JSONObject(str);
                final JSONObject main = json.getJSONObject("main");
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                final String currentTime = dateFormat.format(date);
                currentCityTemperature.setText(String.format("%s°", main.getDouble(API_JSON_TEMP)));
                currentLastUpdated.setText(String.format("Last updated: %s", currentTime));
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), INVALID_CITY, Toast.LENGTH_SHORT).show();
            }
        }
    }


}


