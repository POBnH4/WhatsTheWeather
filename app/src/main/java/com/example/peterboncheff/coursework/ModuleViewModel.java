package com.example.peterboncheff.coursework;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

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
