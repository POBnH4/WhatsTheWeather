package com.example.peterboncheff.coursework;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ModuleRepository {

    private static ModuleRepository INSTANCE; //Singleton;
    private ModuleDatabase database;
    private LiveData<List<Module>> observableModules;
    private ModuleRepository(final ModuleDatabase database){
        this.database = database;
        this.observableModules = this.database.moduleDao().getAllModules();
    }

    public static ModuleRepository getInstance(final ModuleDatabase database){
        if(INSTANCE == null){
            synchronized (ModuleRepository.class){
                if(INSTANCE == null){
                    INSTANCE = new ModuleRepository(database);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<Module>> getModules(){
        return observableModules;
    }

    public void insert(Module module){
        new InsertModuleTask().execute(module);
    }
    public void delete(Module module){
        new DeleteModuleTask().execute(module);
    }
    public void update(Module module){
        new UpdateModuleTask().execute(module);
    }

    @SuppressLint("StaticFieldLeak")
    class InsertModuleTask extends AsyncTask<Module, Void, Void> {

        @Override
        protected Void doInBackground(Module... modules) {
            database.moduleDao().insertModules(modules);
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DeleteModuleTask extends AsyncTask<Module, Void, Void>{

        @Override
        protected Void doInBackground(Module... modules) {
            database.moduleDao().deleteModules(modules);
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class UpdateModuleTask extends AsyncTask<Module, Void, Void>{

        @Override
        protected Void doInBackground(Module... modules) {
            database.moduleDao().updateModules(modules);
            return null;
        }
    }


}
