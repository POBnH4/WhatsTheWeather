package com.example.peterboncheff.coursework;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import static com.example.peterboncheff.coursework.MainActivity.TAG;

//NEVER pass context into ViewModel instances;
public class ModuleViewModel extends AndroidViewModel {
    private ModuleRepository moduleRepository;
    private LiveData<List<Module>> allModules;
    public ModuleViewModel(@NonNull Application application) {
        super(application);
        moduleRepository = ModuleRepository.getInstance(ModuleDatabase.getDatabase(application.getApplicationContext()));
        allModules = moduleRepository.getModules();

    }

    //expose Livedata so the UI can observe it;
    public LiveData<List<Module>> getAllModules(){

        Log.d (TAG, "all modules " +allModules);

        return allModules;
    }
    public void insertModule(Module module){
        moduleRepository.insert(module);
    }
    public void updateModule(Module module){
        moduleRepository.update(module);
    }
    public void deleteModule(Module module){
        moduleRepository.delete(module);
    }
}
