package com.example.peterboncheff.coursework;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ModuleAdapter extends ArrayAdapter<Module> {

    private final List<Module> modules;
    private final LayoutInflater inflater;
    private final int resource;

    public ModuleAdapter(@NonNull Context context, int resource, @NonNull List<Module> objects) {
        super(context, resource, objects);
        this.modules = objects;
        this.resource = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createModuleView(position, convertView, parent);
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createModuleView(position, convertView, parent);
    }

    /**
     * This method creates the {@link View} that will be used to display the module
     * @param position
     * @param convertView
     * @param parent
     * @return
     */


    private View createModuleView(int position, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(resource, parent, false);
        TextView tv = view.findViewById(R.id.tv_reference);
        Module module = modules.get(position);
        tv.setText(module.getReference());
        return view;
    }
}
