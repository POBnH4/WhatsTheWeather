package com.example.peterboncheff.coursework;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

@SuppressLint("Registered")
public class ModuleLiveDataObserver extends AppCompatActivity implements Observer<List<Module>> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceData) {
        super.onCreate(savedInstanceData);
        //Get a ModuleDao for the ModuleDatabase
        //query for all of the Modules from the database
        //Register as an observer with allModules;
        ModuleDao moduleDao = ModuleDatabase.getDatabase(getApplicationContext()).moduleDao();
        LiveData<List<Module>> allModules = moduleDao.getAllModules();
        allModules.observe(this,this);

    }
    @Override
    public void onChanged(@Nullable List<Module> modules) {

    }
}
