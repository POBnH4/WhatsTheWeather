package com.example.peterboncheff.coursework;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.widget.AdapterView.OnItemClickListener;
import static android.widget.AdapterView.OnItemSelectedListener;
import static android.widget.EditText.OnEditorActionListener;
import static com.example.peterboncheff.coursework.MainActivity.*;
import static java.lang.String.format;

public class PreferredLocationActivity extends AppCompatActivity
        implements View.OnClickListener, Observer<List<Module>>, OnItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int DEFAULT_SPINNER_SELECTION_INDEX = 0;

    private EditText searchField;
    private TextView prefLocationLastUpdated;
    private TextView dayOneName, dayTwoName, dayThreeName, dayFourName, dayFiveName;
    private TextView dayOneTemp, dayTwoTemp, dayThreeTemp, dayFourTemp, dayFiveTemp;
    private TextView dayOneWindSpeed, dayTwoWindSpeed, dayThreeWindSpeed, dayFourWindSpeed, dayFiveWindSpeed;
    private TextView dayOneHumidity, dayTwoHumidity, dayThreeHumidity, dayFourHumidity, dayFiveHumidity;
    private TextView dayOnePressure, dayTwoPressure, dayThreePressure, dayFourPressure, dayFivePressure;
    private TextView dayOneWindDirection, dayTwoWindDirection, dayThreeWindDirection, dayFourWindDirection, dayFiveWindDirection;


    private final String[] types = {"Temp", "Wind S", "Humidity", "Pressure", "Wind D"};
    private final int TEMPERATURE_INDEX = 0, WIND_SPEED_INDEX = 1, HUMIDITY_INDEX = 2, PRESSURE_INDEX = 3, WIND_DIRECTION_INDEX = 4;

    private String selectedType;
    private Spinner spinner, typeSpinner;
    private ModuleAdapter spinnerAdapter;
    private TextView currentCityTemperature, currentLastUpdated;
    private String addInputText = "";
    private ModuleViewModel moduleViewModel;
    private Module selectedModule;
    private String EMPTY = "";

    private static ModuleRepository INSTANCE;
    private ModuleDatabase database;
    private LiveData<List<Module>> observableLocationList;

    private List<Double> temperatureHourlyForecast = new ArrayList<>();
    private List<String> humidityHourlyForecast = new ArrayList<>();
    private List<String> pressureHourlyForecast = new ArrayList<>();
    private List<String> windHourlyForecast = new ArrayList<>();
    private List<String> windDirectionForecast = new ArrayList<>();
    private List<String> hoursForecast = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private final String SHARED_PREF_PREFERENCE_ACTIVITY = "preferenceActivity";
    private final String SAVED_SPINNER_POSITION = "spinnerPosition";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred_location);
        init();
        setUpSharedPreferences();
        callSharedPreferences();
    }

    private void init() {
        setVariables();
        Button btnAdd = findViewById(R.id.btn_add);
        Button btnDel = findViewById(R.id.btn_deleteSelected);
        Button btnUpdate = findViewById(R.id.btn_updateSelected);

        btnAdd.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        ModuleDatabase db = ModuleDatabase.getDatabase(this);

        ModuleDao moduleDao = db.moduleDao();
        this.moduleViewModel = ViewModelProviders.of(this).get(ModuleViewModel.class);
        setSpinner();
        setTypeSpinner();
        setSearchField();
        updateModulesSpinner();
    }

    private void setVariables() {

        this.searchField = findViewById(R.id.preferenceLayoutSearch);
        this.typeSpinner = findViewById(R.id.typesSpinner);
        this.prefLocationLastUpdated = findViewById(R.id.prefLocationLastUpdated);

        this.dayOneName = findViewById(R.id.prefLocationDayOne);
        this.dayTwoName = findViewById(R.id.prefLocationDayTwo);
        this.dayThreeName = findViewById(R.id.prefLocationDayThree);
        this.dayFourName = findViewById(R.id.prefLocationDayFour);
        this.dayFiveName = findViewById(R.id.prefLocationDayFive);

        this.dayOneTemp = findViewById(R.id.prefLocationDayOneTemp);
        this.dayTwoTemp = findViewById(R.id.prefLocationDayTwoTemp);
        this.dayThreeTemp = findViewById(R.id.prefLocationDayThreeTemp);
        this.dayFourTemp = findViewById(R.id.prefLocationDayFourTemp);
        this.dayFiveTemp = findViewById(R.id.prefLocationDayFiveTemp);

        this.dayOneHumidity = findViewById(R.id.prefLocationDayOneHumidity);
        this.dayTwoHumidity = findViewById(R.id.prefLocationDayTwoHumidity);
        this.dayThreeHumidity = findViewById(R.id.prefLocationDayThreeHumidity);
        this.dayFourHumidity = findViewById(R.id.prefLocationDayFourHumidity);
        this.dayFiveHumidity = findViewById(R.id.prefLocationDayFiveHumidity);

        this.dayOnePressure = findViewById(R.id.prefLocationDayOnePressure);
        this.dayTwoPressure = findViewById(R.id.prefLocationDayTwoPressure);
        this.dayThreePressure = findViewById(R.id.prefLocationDayThreePressure);
        this.dayFourPressure = findViewById(R.id.prefLocationDayFourPressure);
        this.dayFivePressure = findViewById(R.id.prefLocationDayFivePressure);

        this.dayOneWindSpeed = findViewById(R.id.prefLocationDayOneWind);
        this.dayTwoWindSpeed = findViewById(R.id.prefLocationDayTwoWind);
        this.dayThreeWindSpeed = findViewById(R.id.prefLocationDayThreeWind);
        this.dayFourWindSpeed = findViewById(R.id.prefLocationDayFourWind);
        this.dayFiveWindSpeed = findViewById(R.id.prefLocationDayFiveWind);

        this.dayOneWindDirection = findViewById(R.id.prefLocationDayOneWindDirection);
        this.dayTwoWindDirection = findViewById(R.id.prefLocationDayTwoWindDirection);
        this.dayThreeWindDirection = findViewById(R.id.prefLocationDayThreeWindDirection);
        this.dayFourWindDirection = findViewById(R.id.prefLocationDayFourWindDirection);
        this.dayFiveWindDirection = findViewById(R.id.prefLocationDayFiveWindDirection);

    }

    private void setSpinner() {
        spinnerAdapter = new ModuleAdapter(this, R.layout.module_spinner_item, new ArrayList<Module>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.sp_modules);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(DEFAULT_SPINNER_SELECTION_INDEX);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Object item = parent.getItemAtPosition(position);
                final String ERROR_MESSAGE = "Error in PreferredLocationActivity, Spinner on item selected";
                if (item instanceof Module) {
                    taskLoadUp(((Module) item).getReference());
                    saveCurrentData(((Module) item).getReference(), position);
                } else Log.d(TAG, ERROR_MESSAGE);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        updateModulesSpinner();
    }

    private void setTypeSpinner() {
        ArrayAdapter typeSpinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(Arrays.asList(types)));
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner = findViewById(R.id.typesSpinner);
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinner.setSelection(DEFAULT_SPINNER_SELECTION_INDEX);

        typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == TEMPERATURE_INDEX) selectedType = types[TEMPERATURE_INDEX];
                else if (position == WIND_SPEED_INDEX) selectedType = types[WIND_SPEED_INDEX];
                else if (position == HUMIDITY_INDEX) selectedType = types[HUMIDITY_INDEX];
                else if (position == PRESSURE_INDEX) selectedType = types[PRESSURE_INDEX];
                else if (position == WIND_DIRECTION_INDEX) selectedType = types[WIND_DIRECTION_INDEX];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do something?
            }
        });
    }

    private void setSearchField() {
        this.searchField = findViewById(R.id.preferenceLayoutSearch);
        this.searchField.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (selectedType.equals(types[TEMPERATURE_INDEX])) searchForTempValueInArrayList(searchField.getText().toString());
                    else if (selectedType.equals(types[WIND_SPEED_INDEX])) searchForTypeValueInArrayList(searchField.getText().toString());
                    else if (selectedType.equals(types[HUMIDITY_INDEX])) searchForTypeValueInArrayList(searchField.getText().toString());
                    else if (selectedType.equals(types[PRESSURE_INDEX])) searchForTypeValueInArrayList(searchField.getText().toString());
                    else if (selectedType.equals(types[WIND_DIRECTION_INDEX])) searchForTypeValueInArrayList(searchField.getText().toString());
                }
                return true;
            }
        });
    }

    private void setUpSharedPreferences(){
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.sharedPreferences = getSharedPreferences(SHARED_PREF_PREFERENCE_ACTIVITY, Context.MODE_PRIVATE);
    }


    private void callSharedPreferences(){
        final String DEFAULT_LAST_UPDATED = "";
        this.sharedPreferences = getSharedPreferences(SHARED_PREF_PREFERENCE_ACTIVITY,Context.MODE_PRIVATE);
        String DEFAULT_CITY = "Dubai";
        this.sharedPreferences.getString(SAVED_CURRENT_LOCATION, DEFAULT_CITY);
        this.sharedPreferences.getString(SAVED_LAST_UPDATED, DEFAULT_LAST_UPDATED);
        int position =  this.sharedPreferences.getInt(SAVED_SPINNER_POSITION, DEFAULT_SPINNER_SELECTION_INDEX);
        this.spinner.setSelection(position);
        String currentCity = this.sharedPreferences.getString(SAVED_CURRENT_LOCATION, DEFAULT_CITY);
        Log.d(TAG, "Call shared preferences " + currentCity + " called!");
        taskLoadUp(currentCity);
    }

    private void saveCurrentData(String currentLocation, int spinnerPosition) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(SAVED_CURRENT_LOCATION, currentLocation);
        editor.putString(SAVED_LAST_UPDATED, MainActivity.CURRENT_TIME);
        editor.putInt(SAVED_SPINNER_POSITION, spinnerPosition);
        //saving just location and last updated since I can just parse the name of the city
        // and the data will be updated(if we still have internet connection);
        editor.apply();

        //Module mainModule = new Module();
        //m.setReference();
        //mainModule.setCreatedOn(new Date());
        //mainModule.setScqfCredits(1);
        //task.execute(m);
    }

    private void searchForTypeValueInArrayList(String value) {
        String closeLowerTime = "", closeHigherValueTime = "", exactValueTime = "";
        String closeLowerValueSaved = "", closeHigherValueSaved = "";
        final int CURRENT_VALUE = 0;
        List<String> list;
        if(selectedType.equals(types[WIND_DIRECTION_INDEX])) list = windDirectionForecast;
        else if(selectedType.equals(types[WIND_SPEED_INDEX])) list = windHourlyForecast;
        else if(selectedType.equals(types[HUMIDITY_INDEX])) list = humidityHourlyForecast;
        else list = pressureHourlyForecast;

        for (int i = 0; i < list.size(); i++) {
            String selectedWind = list.get(i);
            if (selectedWind.equals(value)) {
                exactValueTime = hoursForecast.get(i);
                final int LAST_INDEX = list.size() - 1;
                if (i == LAST_INDEX) break;
                else i++;
            }
            if (selectedWind.compareTo(value) < CURRENT_VALUE) {
                closeLowerValueSaved = list.get(i);
                closeLowerTime = hoursForecast.get(i);
            }
            if (selectedWind.compareTo(value) > CURRENT_VALUE) {
                closeHigherValueSaved = list.get(i);
                closeHigherValueTime = hoursForecast.get(i);
            }
        }
//        if (closeLowerValueSaved.equals(EMPTY) || closeHigherValueSaved.equals(EMPTY) || value.equals(EMPTY)) {
//            final String INVALID_INPUT = "Invalid input for wind speed.";
//            Toast.makeText(this, INVALID_INPUT, Toast.LENGTH_SHORT).show();
//        } else {
            showSearchTempDialog(closeLowerTime, closeHigherValueTime, exactValueTime, closeLowerValueSaved, closeHigherValueSaved, value);
//        }
    }

    private void searchForTempValueInArrayList(String value) {
        double closeLowerValue = Byte.MAX_VALUE, closeHigherValue = Byte.MIN_VALUE;
        String closeLowerTime = "", closeHigherValueTime = "", exactValueTime = "";
        double closeLowerValueSaved = 0.0, closeHigherValueSaved = 0.0;
        try {
            double neededValue = Double.parseDouble(value);
            for (int i = 0; i < temperatureHourlyForecast.size(); i++) {
                double selectedTemperature = temperatureHourlyForecast.get(i);
                if (selectedTemperature == neededValue) {
                    exactValueTime = hoursForecast.get(i);
                    final int LAST_INDEX = temperatureHourlyForecast.size() - 1;
                    if (i == LAST_INDEX) break;
                    else i++;
                }
                if (Math.max(neededValue, selectedTemperature) != selectedTemperature && (neededValue - selectedTemperature) < closeLowerValue) {
                    closeLowerValue = (neededValue - selectedTemperature);
                    closeLowerValueSaved = temperatureHourlyForecast.get(i);
                    closeLowerTime = hoursForecast.get(i);
                }
                if (Math.max(neededValue, selectedTemperature) != neededValue && (neededValue - selectedTemperature) > closeHigherValue) {
                    closeHigherValue = (neededValue - selectedTemperature);
                    closeHigherValueSaved = temperatureHourlyForecast.get(i);
                    closeHigherValueTime = hoursForecast.get(i);
                }
            }
            showSearchTempDialog(closeLowerTime, closeHigherValueTime, exactValueTime,
                    String.valueOf(closeLowerValueSaved), String.valueOf(closeHigherValueSaved), value);
        } catch (Exception e) {
            e.printStackTrace();
            final String INVALID_INPUT = "Invalid input for temperature.";
            Toast.makeText(this, INVALID_INPUT, Toast.LENGTH_SHORT).show();
        }
    }

    private void showSearchTempDialog(String closeLowerValueTime, String closeHigherValueTime, String exactValueTime,
                                      String closeLower, String closeHigher, String neededValue) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String DIALOG_OK_BUTTON_TEXT = "OK";
        final TextView text = new TextView(this);
        final String TITLE = "\t\t\t\t\tResults for " + neededValue;
        final String NOT_FOUND = "Not found";
        builder.setTitle(TITLE);
        final String asd = "\t\tClosest higher value is: %s" +
                "\n\t\tClosest lower value is: %s" +
                "\n\t\tYour value: %s" +
                "\n\t\tClosest higher time is at: %s" +
                "\n\t\tClosest lower time is at: %s" +
                "\n\t\tYour value time is at: %s";

        if (selectedType.equals(types[TEMPERATURE_INDEX]))
            text.setText(String.format(asd, closeHigher, closeLower, neededValue, closeLowerValueTime,
                    closeHigherValueTime, exactValueTime.equals(EMPTY) ? NOT_FOUND : exactValueTime));
        else if (selectedType.equals(types[WIND_SPEED_INDEX]))
            text.setText(String.format(asd, closeHigher, closeLower, neededValue, closeLowerValueTime,
                    closeHigherValueTime, exactValueTime.equals(EMPTY) ? NOT_FOUND : exactValueTime));
        else if (selectedType.equals(types[HUMIDITY_INDEX]))
            text.setText(String.format(asd, closeHigher, closeLower, neededValue, closeLowerValueTime,
                    closeHigherValueTime, exactValueTime.equals(EMPTY) ? NOT_FOUND : exactValueTime));
        else if (selectedType.equals(types[PRESSURE_INDEX]))
            text.setText(String.format(asd, closeHigher, closeLower, neededValue, closeLowerValueTime,
                    closeHigherValueTime, exactValueTime.equals(EMPTY) ? NOT_FOUND : exactValueTime));
        else if (selectedType.equals(types[WIND_DIRECTION_INDEX]))
            text.setText(String.format(asd, closeHigher, closeLower, neededValue, closeLowerValueTime,
                    closeHigherValueTime, exactValueTime.equals(EMPTY) ? NOT_FOUND : exactValueTime));


        builder.setView(text);
        builder.setPositiveButton(DIALOG_OK_BUTTON_TEXT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String SET_EMPTY = "";
                searchField.setText(SET_EMPTY);
            }
        });

        builder.show();
    }


    private void addModule(Module module) {

        moduleViewModel.insertModule(module);
        //refresh data/spinner;
    }

    private void deleteModule(Module module) {
        moduleViewModel.deleteModule(module);
        //refresh data/spinner;

    }

    private void updateModule(Module module) {
        moduleViewModel.updateModule(module);
        //refresh data/spinner;
    }

    private void updateModulesSpinner() {
        final String message = "Retrieving modules";
        Log.d(TAG, message);
        moduleViewModel.getAllModules().observe(this, this);
    }

    private void refreshModulesSpinner(List<Module> modules) {
        spinnerAdapter.clear();
        spinnerAdapter.addAll(modules);
        spinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            this.selectedModule = m;
            addModule(m);
        } else if (v.getId() == R.id.btn_deleteSelected) {
            Object selectedObj = spinner.getSelectedItem();
            this.selectedModule = (Module) selectedObj;
            if (selectedObj != null) {
                Module selectedModule = (Module) selectedObj;
                deleteModule(selectedModule);
            }
        } else if (v.getId() == R.id.btn_updateSelected) {
            Object selectedObj = spinner.getSelectedItem();
            if (selectedObj != null) {
                Module selectedModule = (Module) selectedObj;
                showAddDialog(true); // updating an existing value -> true;
                selectedModule.setReference(addInputText);
                updateModule(selectedModule);
                this.selectedModule = selectedModule;
                taskLoadUp(addInputText);
            }
        }
    }

    private void showAddDialog(boolean isUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final String UPDATE_TITLE = "Update your location", SET_LOCATION_TITLE = "Set new location";
        final String DIALOG_OK_BUTTON_TEXT = "Add", DIALOG_CANCEL_BUTTON_TEXT = "Cancel";

        if (isUpdate) builder.setTitle(UPDATE_TITLE);
        else builder.setTitle(SET_LOCATION_TITLE);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(DIALOG_OK_BUTTON_TEXT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addInputText = input.getText().toString();
            }
        });
        builder.setNegativeButton(DIALOG_CANCEL_BUTTON_TEXT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void taskLoadUp(String query) {
        if (checkNetworkAvailability(getApplicationContext())) {
            PreferredLocationActivity.DownloadTask task = new DownloadTask();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        int newValue = sharedPreferences.getInt(key, Integer.MAX_VALUE);
        Log.d(TAG, String.format("Key %s change to %d", key, newValue));
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            return MainActivity.execute(BEGINNING_API_HTTP + urls[SELECT_FIRST] + END_OF_API_HTTP);
        }

        @Override
        protected void onPostExecute(String query) {
            try {
                final JSONObject json = new JSONObject(query);
                final JSONObject forecastDateInfo = json.getJSONArray(API_FORECAST_LIST).getJSONObject(GET_CURRENT_DAY);
                final String GET_DATE = forecastDateInfo.getString(API_SELECT_DATE);
                setCurrentTime();
                setDaysOfWeekNames(GET_DATE);
                setTextViews(json, GET_DATE);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), INVALID_CITY, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setCurrentTime() {
        this.prefLocationLastUpdated.setText(format(LAST_UPDATED_FORMAT, CURRENT_TIME));
    }

    private void setTextViews(JSONObject json, String GET_DATE) {
        final String API_JSON_WIND_DIRECTION = "deg";
        int selector = 0;
        int daysCovered = 0; // once daysCovered becomes 5 it breaks the loop
        String dateCheckStore = GET_DATE;
        try {
            for (int day = GET_CURRENT_DAY; day < ITERATE_IN_ADVANCE; day++) {
                String date = json.getJSONArray(API_FORECAST_LIST).getJSONObject(day).getString(API_SELECT_DATE);
                final JSONObject forecastMain = json.getJSONArray(API_FORECAST_LIST).getJSONObject(day).getJSONObject(API_SELECT_MAIN_ARRAY);
                final JSONObject forecastWind = json.getJSONArray(API_FORECAST_LIST).getJSONObject(day).getJSONObject(API_SELECT_WIND);

                String humidity = forecastMain.getString(API_JSON_HUMIDITY), pressure = forecastMain.getString(API_JSON_PRESSURE);
                String windSpeed = forecastWind.getString(API_JSON_WIND_SPEED), windDirection = forecastWind.getString(API_JSON_WIND_DIRECTION);
                double temperature = forecastMain.getDouble(API_JSON_TEMP);

                addForecasts(temperature, humidity, pressure, windSpeed, windDirection, date);
                if (checkDateValidity(date, dateCheckStore)) {
                    dateCheckStore = date;
                    setFields(selector, temperature, windSpeed, pressure, windDirection, humidity);
                    selector++;
                    daysCovered++;
                    if (daysCovered == DAYS_REQUIRED) {
                        setFields(selector, temperature, windSpeed, pressure, windDirection, humidity);
                        break;
                    }
                }
            }
            addForecastsToDatabase();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setFields(int selector, double temperature, String windSpeed, String pressure, String windDirection, String humidity) {
        setTempTextFields(selector, temperature, dayOneTemp, dayTwoTemp, dayThreeTemp, dayFourTemp, dayFiveTemp);
        setWindTextFields(selector, windSpeed, dayOneWindSpeed, dayTwoWindSpeed, dayThreeWindSpeed, dayFourWindSpeed, dayFiveWindSpeed);
        setPressureTextFields(selector, pressure, dayOnePressure, dayTwoPressure, dayThreePressure, dayFourPressure, dayFivePressure);
        setWindDirectionTextFields(selector, windDirection, dayOneWindDirection, dayTwoWindDirection, dayThreeWindDirection, dayFourWindDirection, dayFiveWindDirection);
        setHumidityTextFields(selector, humidity, dayOneHumidity, dayTwoHumidity, dayThreeHumidity, dayFourHumidity, dayFiveHumidity);
    }

    private void addForecasts(double temperature, String humidity, String pressure, String windSpeed, String windDirection, String date) {
        this.temperatureHourlyForecast.add(temperature);
        this.humidityHourlyForecast.add(humidity);
        this.pressureHourlyForecast.add(pressure);
        this.windHourlyForecast.add(windSpeed);
        this.windDirectionForecast.add(windDirection);
        this.hoursForecast.add(date);
    }

    private void addForecastsToDatabase() {
        //Module.Forecasts.setTemperatureForecasts(temperatureHourlyForecast);
        //this.selectedModule.setHumidityForecasts(humidityHourlyForecast);
        //this.selectedModule.setAirPressureForecasts(pressureHourlyForecast);
        //this.selectedModule.setWindSpeedForecasts(windHourlyForecast);
        //this.selectedModule.setWindDirectionForecasts(windHourlyForecast);
        //this.selectedModule.setHoursForecast(hoursForecast);
    }

    private void setWindTextFields(int day, String value, TextView... textViews) {
        textViews[day].setText(value);
    }

    private void setTempTextFields(int day, double value, TextView... textViews) {
        textViews[day].setText(String.format("%s°C", value));
    }

    private void setPressureTextFields(int day, String value, TextView... textViews) {
        textViews[day].setText(value);
    }

    private void setWindDirectionTextFields(int day, String value, TextView... textViews) {
        textViews[day].setText(String.format("%s°", value));
    }

    private void setHumidityTextFields(int day, String value, TextView... textViews) {
        textViews[day].setText(String.format("%s%%", value));
    }

    private void setDaysOfWeekNames(final String GET_DATE) {
        parseDayOfWeekName(GET_DATE, dayOneName, dayTwoName, dayThreeName, dayThreeName, dayFourName, dayFiveName);
    }
}
