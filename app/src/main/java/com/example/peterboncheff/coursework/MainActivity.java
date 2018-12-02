package com.example.peterboncheff.coursework;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import static android.support.v7.widget.SearchView.OnClickListener;
import static android.support.v7.widget.SearchView.OnQueryTextListener;
import static java.lang.String.format;

public class MainActivity extends AppCompatActivity implements OnClickListener, Serializable, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int TOTAL_TABS = 3, PLUS_ONE_POSITION = 1, NO_MORE_DATA = -1;
    private static final String SNACK_BAR_TEXT = "Replace with your own action", ACTION_TEST = "Action";
    private final String TOAST_TEXT = "Couldn't find the weather";
    static final String NEW_LINE = "\n";
    static final String DATE_FORMAT = "hh:mm:ss", INVALID_CITY = "Couldn't find city", TAG = "hallo";
    static final String API_JSON_TEMP = "temp", INTERNET_ERROR = "No internet connection";
    static final String BEGINNING_API_HTTP = "http://api.openweathermap.org/data/2.5/forecast?q=";
    static final String END_OF_API_HTTP = "&units=metric&appid=421389c958155f9c8ed14a5b0010c202";
    //http://api.openweathermap.org/data/2.5/forecast?q=     &units=metric&appid=421389c958155f9c8ed14a5b0010c202 link for API

    private final String GREY_COLOR = "#95a5a5", RED_COLOR = "#e84c3d", YELLOW_COLOR = "#dccc0b", BLUE_COLOR = "#125c67";
    private final String DARK_RED_COLOR_TYPE_ONE = "#a31f13", DARK_RED_COLOR_TYPE_TWO = "#db2b1b", BRIGHT_RED_COLOR_TYPE_ONE = "#ea594b";
    private final String BRIGHT_RED_COLOR_TYPE_TWO = "#ec6659", DARK_GREY_COLOR_TYPE_ONE = "#6e8181", DARK_GREY_COLOR_TYPE_TWO = "#7d9090";
    private final String BRIGHT_GREY_COLOR_TYPE_ONE = "#adbaba", BRIGHT_GREY_COLOR_TYPE_TWO = "#bdc8c8", DARK_YELLOW_COLOR_TYPE_ONE = "#a09407";
    private final String DARK_YELLOW_COLOR_TYPE_TWO = "#beb009", BRIGHT_YELLOW_COLOR_TYPE_ONE = "#f3e213", BRIGHT_YELLOW_COLOR_TYPE_TWO = "#f5e631";
    private final String DARK_BLUE_COLOR_TYPE_ONE = "#2a6a74", DARK_BLUE_COLOR_TYPE_TWO = "#2a6a74", BRIGHT_BLUE_COLOR_TYPE_ONE = "#1a8c9b";
    private final String BRIGHT_BLUE_COLOR_TYPE_TWO = "#518289";

    final static String API_JSON_DESCRIPTION = "description", API_JSON_HUMIDITY = "humidity", API_FORECAST_LIST = "list", API_SELECT_DATE = "dt_txt";
    final static String API_JSON_PRESSURE = "pressure", API_JSON_COUNTRY = "country", API_JSON_WIND_SPEED = "speed", API_SELECT_CITY = "city";
    final static String API_SELECT_WIND = "wind", API_SELECT_WEATHER = "weather", API_NAME_OF_CITY = "name";
    final static String API_SELECT_MAIN_ARRAY = "main";

    final static String SAVED_CURRENT_LOCATION = "currentLocation", SAVED_LAST_UPDATED = "lastUpdated";
    final String SAVED_HUMIDITY = "humidity", SAVED_PRESSURE = "pressure", SAVED_WIND_SPEED = "windSpeed";
    final String SAVED_CURRENT_TEMP = "temp";

    private final String SCATTERED_CLOUDS = "scattered clouds", BROKEN_CLOUDS = "broken clouds", FEW_CLOUDS = "few clouds";
    private final String SHOWER_RAIN = "shower rain", RAIN = "rain", THUNDERSTORM = "thunderstorm";
    private static final String ONE_DECIMAL_FORMAT_PATTERN = "#.0", DATE_FORMAT_PATTERN = "yyyy-MM-dd hh:mm:ss";
    private static final Date DATE = new Date();
    private static final DecimalFormat ONE_DECIMAL_PLACE = new DecimalFormat(ONE_DECIMAL_FORMAT_PATTERN);
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    private final String TEMP_FORMAT_CELSIUS = "%s°";
    static boolean DISPLAY_TEMPERATURE_IN_CELSIUS = true;
    final static int DAYS_REQUIRED = 6, GET_CURRENT_DAY = 0;
    final static int ITERATE_IN_ADVANCE = 100; //Random big number, since the data in the json file is for every 3 hours,
    // we iterate taking only one forecast for a day, thus till we get the data for 5 different days

    private final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);
    final static String CURRENT_TIME = SIMPLE_DATE_FORMAT.format(DATE);

    final static String CURRENT_LOCATION_FORMAT = "%s,%s", LAST_UPDATED_FORMAT = "Last updated %s";
    private final static String HUMIDITY_FORMAT = "%s", PRESSURE_FORMAT = "%s hPa", SEPARATOR = ", ";

    final static int SELECT_FIRST = 0;

    private SharedPreferences sharedPrefs;
    private ActionBar actionBar;

    private ConstraintLayout mainLayout;
    private Button refreshButton;
    private TextView currentLocation, lastUpdated, currentTemperature, humidityTextField, pressureTextField, windTextField;
    private ImageView mainIcon;
    private String currentLocationIs = "Dubai", currentLocationIsCheck = "Dubai";

    private LinearLayout dayOfWeekOneLayout, dayOfWeekTwoLayout, dayOfWeekFourLayout, dayOfWeekFiveLayout;
    private TextView dayOfWeekOneName, dayOfWeekTwoName, dayOfWeekThreeName, dayOfWeekFourName, dayOfWeekFiveName;
    private TextView daysOfWeekOneTemp, daysOfWeekTwoTemp, daysOfWeekThreeTemp, daysOfWeekFourTemp, daysOfWeekFiveTemp;
    private ImageView dayOfWeekOneIcon, dayOfWeekTwoIcon, dayOfWeekThreeIcon, dayOfWeekFourIcon, dayOfWeekFiveIcon;

    private User currentUser;
    private final String SHARED_PREF_MAIN_ACTIVITY = "sharedPrefMain";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String HEADER_TITLE = "";
        setTitle(HEADER_TITLE);
        setContentView(R.layout.activity_main);
        init();
        setUpSharedPreferences();
        setUpDatabase();
        callSharedPreferences();
    }

    private void init() {
        this.mainLayout = findViewById(R.id.mainLayout);
        this.currentLocation = findViewById(R.id.currentLocation);
        this.lastUpdated = findViewById(R.id.lastUpdatedTextField);
        this.humidityTextField = findViewById(R.id.windTextView);
        this.pressureTextField = findViewById(R.id.pressureTextView);

        this.windTextField = findViewById(R.id.humidityTextView);
        this.currentTemperature = findViewById(R.id.currentTemperature);
        this.mainIcon = findViewById(R.id.mainIcon);
        this.refreshButton = findViewById(R.id.refreshButton);

        this.refreshButton.setOnClickListener(this);
        this.refreshButton.setBackgroundResource(R.drawable.ic_refresh);

        this.actionBar = getSupportActionBar();
        final int SET_HEADER_ELEVATION = 0;
        assert this.actionBar != null;
        this.actionBar.setElevation(SET_HEADER_ELEVATION);

        //Intent intent = getIntent();
        // this.currentUser = (User) intent.getSerializableExtra(USE_CURRENT_USER_DATA);

        setDaysOfWeekVariables();
    }

    private void setUpDatabase(){
        ModuleDatabase db = ModuleDatabase.getDatabase(this);
        ModuleDao moduleDao = db.moduleDao();
    }

    private void setUpSharedPreferences(){
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.sharedPrefs = getSharedPreferences(SHARED_PREF_MAIN_ACTIVITY, Context.MODE_PRIVATE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        int newValue = sharedPreferences.getInt(key, Integer.MAX_VALUE);
        Log.d(TAG, String.format("Key %s change to %d", key, newValue));
    }

    private void callSharedPreferences(){
        final String DEFAULT_LAST_UPDATED = "",DEFAULT_CITY = "Dubai";
        this.sharedPrefs = getSharedPreferences(SHARED_PREF_MAIN_ACTIVITY,Context.MODE_PRIVATE);
        this.sharedPrefs.getString(SAVED_CURRENT_LOCATION, DEFAULT_CITY);
        this.sharedPrefs.getString(SAVED_LAST_UPDATED, DEFAULT_LAST_UPDATED);
        String currentCity = this.sharedPrefs.getString(SAVED_CURRENT_LOCATION, DEFAULT_CITY);
        Log.d(TAG, "Call shared preferences " + currentCity + " called!");
        taskLoadUp(currentCity);
    }

    /**
     * Set up options menu and implement search using the input that is in the SearchView
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.actionBarMenuSearch).getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                taskLoadUp(query);
                final String SET_AS_EMPTY = "";
                searchView.setQuery(SET_AS_EMPTY, false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.preferencesItem:
                final Intent intent = new Intent(getApplicationContext(), PreferredLocationActivity.class);
                startActivity(intent);
                break;
            case R.id.settingsItem:
                final Intent intentTwo = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intentTwo);
                break;
            case R.id.logOut:
                final Intent intentThree = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentThree);
                break;
        }
        return true;
    }

    private void setDaysOfWeekVariables() {
        this.dayOfWeekOneLayout = findViewById(R.id.dayOfWeekOneLayout);
        this.dayOfWeekTwoLayout = findViewById(R.id.dayOfWeekTwoLayout);
        this.dayOfWeekFourLayout = findViewById(R.id.dayOfWeekFourLayout);
        this.dayOfWeekFiveLayout = findViewById(R.id.dayOfWeekFiveLayout);

        this.dayOfWeekOneName = findViewById(R.id.dayOfWeekOneName);
        this.dayOfWeekTwoName = findViewById(R.id.dayOfWeekTwoName);
        this.dayOfWeekThreeName = findViewById(R.id.dayOfWeekThreeName);
        this.dayOfWeekFourName = findViewById(R.id.dayOfWeekFourName);
        this.dayOfWeekFiveName = findViewById(R.id.dayOfWeekFiveName);

        this.dayOfWeekOneIcon = findViewById(R.id.dayOfWeekOneIcon);
        this.dayOfWeekTwoIcon = findViewById(R.id.dayOfWeekTwoIcon);
        this.dayOfWeekThreeIcon = findViewById(R.id.dayOfWeekThreeIcon);
        this.dayOfWeekFourIcon = findViewById(R.id.dayOfWeekFourIcon);
        this.dayOfWeekFiveIcon = findViewById(R.id.dayOfWeekFiveIcon);

        this.daysOfWeekOneTemp = findViewById(R.id.dayOfWeekOneTemp);
        this.daysOfWeekTwoTemp = findViewById(R.id.dayOfWeekTwoTemp);
        this.daysOfWeekThreeTemp = findViewById(R.id.dayOfWeekThreeTemp);
        this.daysOfWeekFourTemp = findViewById(R.id.dayOfWeekFourTemp);
        this.daysOfWeekFiveTemp = findViewById(R.id.dayOfWeekFiveTemp);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == refreshButton.getId()) taskLoadUp(currentLocationIs);
    }

    private void convertTemperature() {
        if (!DISPLAY_TEMPERATURE_IN_CELSIUS) {
            String fahrenheitSymbol = "%°F", currentTemperatureText = this.currentTemperature.getText().toString();
            this.currentTemperature.setText(format(fahrenheitSymbol, convertToFahrenheit(currentTemperatureText)));
        } else {
            this.currentTemperature.setText(format(TEMP_FORMAT_CELSIUS, convertToCelsius(this.currentTemperature.getText().toString())));
        }
    }

    private String convertToCelsius(String temperatureInFahrenheit) {
        double temperatureInF = Double.parseDouble(temperatureInFahrenheit.substring(0, temperatureInFahrenheit.length() - 1));
        double formula = (temperatureInF - 32) * 5 / 9; // For instance, (33.8°F − 32) × 5/9 = 1°C;
        //Log.d(TAG, "FAHRENHEIT TO CEL: " + temperatureInF);
        return ONE_DECIMAL_PLACE.format(formula);
    }

    private String convertToFahrenheit(String temperatureInCelsius) {
        double temperatureInC = Double.parseDouble(temperatureInCelsius.substring(0, temperatureInCelsius.length() - 1));
        double formula = (temperatureInC * 9 / 5) + 32; // For instance, (1°C × 9/5) + 32 = 33.8°F;
        //Log.d(TAG, "CELSIUS TO FAHRENHEIT: " + temperatureInC);
        return ONE_DECIMAL_PLACE.format(formula);
    }


    /**
     * If there is internet connection take the input and parse it forward
     */
    private void taskLoadUp(String query) {
        if (checkNetworkAvailability(getApplicationContext())) {
            APIDownloaderAndImplementer task = new APIDownloaderAndImplementer();
            currentLocationIsCheck = query;
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class APIDownloaderAndImplementer extends AsyncTask<String, Void, String> {
        /**
         * Take url and pass forward;
         */
        @Override
        protected String doInBackground(String... urls) {

            final int GET_FIRST_URL = 0;
            return MainActivity.execute(BEGINNING_API_HTTP + urls[GET_FIRST_URL] + END_OF_API_HTTP);
        }

        /**
         * Using the parsed JSON from an external source -> set the variables/icons/colors
         */
        @Override
        protected void onPostExecute(String query) {
            try {
                final JSONObject json = new JSONObject(query);
                final JSONObject forecastMain = json.getJSONArray(API_FORECAST_LIST).getJSONObject(GET_CURRENT_DAY).getJSONObject(API_SELECT_MAIN_ARRAY);
                final JSONObject forecastWeather = json.getJSONArray(API_FORECAST_LIST).getJSONObject(GET_CURRENT_DAY).getJSONArray(API_SELECT_WEATHER).getJSONObject(SELECT_FIRST);
                final JSONObject forecastWind = json.getJSONArray(API_FORECAST_LIST).getJSONObject(GET_CURRENT_DAY).getJSONObject(API_SELECT_WIND);
                final JSONObject forecastDateInfo = json.getJSONArray(API_FORECAST_LIST).getJSONObject(GET_CURRENT_DAY);
                final JSONObject city = json.getJSONObject(API_SELECT_CITY);

                final String CITY = city.getString(API_NAME_OF_CITY).toUpperCase(), COUNTRY_CODE = city.getString(API_JSON_COUNTRY);
                final String HUMIDITY = forecastMain.getString(API_JSON_HUMIDITY), PRESSURE = forecastMain.getString(API_JSON_PRESSURE);
                final String WIND_SPEED = forecastWind.getString(API_JSON_WIND_SPEED), CURRENT_LOCATION = CITY + SEPARATOR + COUNTRY_CODE;
                final String GET_DATE = forecastDateInfo.getString(API_SELECT_DATE);
                final double TEMPERATURE = forecastMain.getDouble(API_JSON_TEMP);

                currentLocation.setText(format(CURRENT_LOCATION_FORMAT, CITY, COUNTRY_CODE));
                lastUpdated.setText(format(LAST_UPDATED_FORMAT, CURRENT_TIME));
                currentTemperature.setText(format(TEMP_FORMAT_CELSIUS, ONE_DECIMAL_PLACE.format(TEMPERATURE)));
                humidityTextField.setText(format(HUMIDITY_FORMAT, HUMIDITY));
                pressureTextField.setText(format(PRESSURE_FORMAT, PRESSURE));
                windTextField.setText(WIND_SPEED);

                setPhoneBackgroundColor(forecastWeather.getString(API_JSON_DESCRIPTION), checkDayOrNight());
                saveCurrentLocation(CURRENT_LOCATION, TEMPERATURE, HUMIDITY, PRESSURE, WIND_SPEED);

                parseDayOfWeekName(GET_DATE, dayOfWeekOneName, dayOfWeekTwoName, dayOfWeekThreeName, dayOfWeekFourName, dayOfWeekFiveName);
                setHorizontalViewAttributes(json, GET_DATE);
                currentLocationIs = currentLocationIsCheck; // if the location is invalid -> an exception
                // will be thrown and currentLocationIs WON'T be set to be a location that is invalid;

                //convertTemperature();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), INVALID_CITY, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Set horizontal attributes using parsed json data
     */
    private void setHorizontalViewAttributes(JSONObject json, final String GET_DATE) {
        int daysCovered = 0; // once daysCovered becomes 5 it breaks the loop
        String dateCheckStore = GET_DATE;
        try {
            for (int day = GET_CURRENT_DAY; day < ITERATE_IN_ADVANCE; day++) {
                String date = json.getJSONArray(API_FORECAST_LIST).getJSONObject(day).getString(API_SELECT_DATE);
                if (checkDateValidity(date, dateCheckStore)) {
                    dateCheckStore = date;
                    JSONObject forecastMain = json.getJSONArray(API_FORECAST_LIST).getJSONObject(day).getJSONObject(API_SELECT_MAIN_ARRAY);
                    JSONObject forecastWeather = json.getJSONArray(API_FORECAST_LIST).getJSONObject(day).getJSONArray(API_SELECT_WEATHER).getJSONObject(SELECT_FIRST);
                    changeDaysOfWeekInfo(forecastWeather.getString(API_JSON_DESCRIPTION), day, forecastMain);
                    daysCovered++;
                    if (daysCovered == DAYS_REQUIRED) break;
                    Log.d(TAG, json.getJSONArray(API_FORECAST_LIST).getJSONObject(day).getString(API_SELECT_DATE));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes sure that the data is for each day not for every 3 hours
     */
    static boolean checkDateValidity(String date, String dateCheckStore) {
        final int DATE_INDEX_ONE = 0, DATE_LAST_INDEX = 10; //the json file parses date that
        // is in a format yyyy-MM-dd hh-mm-ss so we cut of the part that we don't need -> "  hh-mm-ss";
        return !date.substring(DATE_INDEX_ONE, DATE_LAST_INDEX).equals(dateCheckStore.substring(DATE_INDEX_ONE, DATE_LAST_INDEX));
    }

    /**
     * parse day of the week for a certain date in the past
     */
    static void parseDayOfWeekName(String forecastDateInfo, TextView... textViews) {
        try {
            ArrayList<TextView> array = new ArrayList<>(Arrays.asList(textViews));
            final Date date = dateFormat.parse(forecastDateInfo);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            setDaysOfWeekNames(calendar.get(Calendar.DAY_OF_WEEK), array);
            Log.d(TAG, "calendar " + calendar.get(Calendar.DAY_OF_WEEK));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "calendar error");
        }
    }

    /**
     * set horizontal front view days of week text
     */
    private static void setDaysOfWeekNames(int day, ArrayList<TextView> textViews) {
        final int DAYS_IN_A_WEEK = 7, FIRST_DAY_OF_THE_WEEK = 1;
        for (int i = 0; i < textViews.size(); i++) {
            if (i == 0) textViews.get(i).setText(getNameOfWeekDay(day));
            else textViews.get(i)
                    .setText(getNameOfWeekDay((day >= DAYS_IN_A_WEEK) ? day = FIRST_DAY_OF_THE_WEEK : ++day));
        }
    }

    /**
     * Return name of the day
     */
    private static String getNameOfWeekDay(int day) {
        final int FIRST_DAY_OF_THE_WEEK = 1, SECOND_DAY_OF_THE_WEEK = 2, THIRD_DAY_OF_THE_WEEK = 3;
        final int FOURTH_DAY_OF_THE_WEEK = 4, FIFTH_DAY_OF_THE_WEEK = 5, SIXTH_DAY_OF_THE_WEEK = 6;
        final String SUNDAY = "\t\tSunday", MONDAY = "\t\tMonday", TUESDAY = "\t\tTuesday", WEDNESDAY = "\t Wednesday";
        final String THURSDAY = "\t\tThursday", FRIDAY = "\t\t\tFriday", SATURDAY = "\t\tSaturday";
        switch (day) {
            case FIRST_DAY_OF_THE_WEEK:
                return SUNDAY;
            case SECOND_DAY_OF_THE_WEEK:
                return MONDAY;
            case THIRD_DAY_OF_THE_WEEK:
                return TUESDAY;
            case FOURTH_DAY_OF_THE_WEEK:
                return WEDNESDAY;
            case FIFTH_DAY_OF_THE_WEEK:
                return THURSDAY;
            case SIXTH_DAY_OF_THE_WEEK:
                return FRIDAY;
            default:
                return SATURDAY;
        }
    }

    /**
     * Shared Preferences main activity variables/icons/colors for current user
     */
    private void saveCurrentLocation(String currentLocation, double currentTemperature, String humidity, String pressure, String windSpeed) {

        SharedPreferences.Editor editor = this.sharedPrefs.edit();
        editor.putString(SAVED_CURRENT_LOCATION, currentLocation);
        editor.putString(SAVED_LAST_UPDATED, MainActivity.CURRENT_TIME);
        editor.putString(SAVED_CURRENT_TEMP, String.valueOf(currentTemperature));
        editor.putString(SAVED_HUMIDITY, humidity);
        editor.putString(SAVED_PRESSURE, pressure);
        editor.putString(SAVED_WIND_SPEED, windSpeed);
        editor.apply();

        //Module mainModule = new Module();
        //m.setReference();
        //mainModule.setCreatedOn(new Date());
        //mainModule.setScqfCredits(1);
        //task.execute(m);
    }

    /**
     * if the time is 20:00 - 6:00 set night icons, else day icons
     */
    private boolean checkDayOrNight() {
        final int NIGHT_TIME = 20, MORNING_TIME = 6; //20:00 represents 08:00PM and 6 represents 06:00AM;
        final int FIRST_HOUR_CHARACTER = 0, SECOND_HOUR_CHARACTER = 1, CHECK_WHETHER_TIME_IS_AM = 0;
        int hour = MainActivity.CURRENT_TIME.charAt(FIRST_HOUR_CHARACTER) + MainActivity.CURRENT_TIME.charAt(SECOND_HOUR_CHARACTER);
        if (MainActivity.CURRENT_TIME.charAt(FIRST_HOUR_CHARACTER) == CHECK_WHETHER_TIME_IS_AM) hour = MainActivity.CURRENT_TIME.charAt(SECOND_HOUR_CHARACTER);
        //return hour >= NIGHT_TIME || hour <= MORNING_TIME; // true = night, false = day;
        return false;
    }

    /**
     * set appropriate colors for horizontal scroll view
     */
    private void setDaysOfWeekColor(String currentMainColor) {
        switch (currentMainColor) {
            //skipping this.dayOfWeekThreeLayout for design purposes;
            case RED_COLOR:
                this.dayOfWeekOneLayout.setBackgroundColor(Color.parseColor(DARK_RED_COLOR_TYPE_TWO));
                this.dayOfWeekTwoLayout.setBackgroundColor(Color.parseColor(DARK_RED_COLOR_TYPE_ONE));
                this.dayOfWeekFourLayout.setBackgroundColor(Color.parseColor(BRIGHT_RED_COLOR_TYPE_ONE));
                this.dayOfWeekFiveLayout.setBackgroundColor(Color.parseColor(BRIGHT_RED_COLOR_TYPE_TWO));
                break;
            case YELLOW_COLOR:
                this.dayOfWeekOneLayout.setBackgroundColor(Color.parseColor(DARK_YELLOW_COLOR_TYPE_TWO));
                this.dayOfWeekTwoLayout.setBackgroundColor(Color.parseColor(DARK_YELLOW_COLOR_TYPE_ONE));
                this.dayOfWeekFourLayout.setBackgroundColor(Color.parseColor(BRIGHT_YELLOW_COLOR_TYPE_ONE));
                this.dayOfWeekFiveLayout.setBackgroundColor(Color.parseColor(BRIGHT_YELLOW_COLOR_TYPE_TWO));
                break;
            case BLUE_COLOR:
                this.dayOfWeekOneLayout.setBackgroundColor(Color.parseColor(DARK_BLUE_COLOR_TYPE_TWO));
                this.dayOfWeekTwoLayout.setBackgroundColor(Color.parseColor(DARK_BLUE_COLOR_TYPE_ONE));
                this.dayOfWeekFourLayout.setBackgroundColor(Color.parseColor(BRIGHT_BLUE_COLOR_TYPE_ONE));
                this.dayOfWeekFiveLayout.setBackgroundColor(Color.parseColor(BRIGHT_BLUE_COLOR_TYPE_TWO));
                break;
            default:
                this.dayOfWeekOneLayout.setBackgroundColor(Color.parseColor(DARK_GREY_COLOR_TYPE_TWO));
                this.dayOfWeekTwoLayout.setBackgroundColor(Color.parseColor(DARK_GREY_COLOR_TYPE_ONE));
                this.dayOfWeekFourLayout.setBackgroundColor(Color.parseColor(BRIGHT_GREY_COLOR_TYPE_ONE));
                this.dayOfWeekFiveLayout.setBackgroundColor(Color.parseColor(BRIGHT_GREY_COLOR_TYPE_TWO));
                break;
        }
    }

    /**
     * change temperature for days of week horizontal scroll view
     */
    private void changeDaysOfWeekInfo(String weather, int day, JSONObject forecast) {
        ImageView imageView;
        final String MINIMUM_TEMPERATURE = "temp_min", MAXIMUM_TEMPERATURE = "temp_max", STRING_FORMAT = "\t  %s°/%s°";
        final int DAY_ONE = 0, DAY_TWO = 1, DAY_THREE = 2, DAY_FOUR = 3;
        try {
            final double GET_MIN_TEMP = forecast.getDouble(MINIMUM_TEMPERATURE), GET_MAX_TEMP = forecast.getDouble(MAXIMUM_TEMPERATURE);
            final String MIN_TEMP = ONE_DECIMAL_PLACE.format(GET_MIN_TEMP), MAX_TEMP = ONE_DECIMAL_PLACE.format(GET_MAX_TEMP);
            switch (day) {
                case DAY_ONE:
                    this.daysOfWeekOneTemp.setText(format(STRING_FORMAT, MIN_TEMP, MAX_TEMP));
                    imageView = dayOfWeekOneIcon;
                    break;
                case DAY_TWO:
                    this.daysOfWeekTwoTemp.setText(format(STRING_FORMAT, MIN_TEMP, MAX_TEMP));
                    imageView = dayOfWeekTwoIcon;
                    break;
                case DAY_THREE:
                    this.daysOfWeekThreeTemp.setText(format(STRING_FORMAT, MIN_TEMP, MAX_TEMP));
                    imageView = dayOfWeekThreeIcon;
                    break;
                case DAY_FOUR:
                    this.daysOfWeekFourTemp.setText(format(STRING_FORMAT, MIN_TEMP, MAX_TEMP));
                    imageView = dayOfWeekFourIcon;
                    break;
                default: // day 5 or else
                    this.daysOfWeekFiveTemp.setText(format(STRING_FORMAT, MIN_TEMP, MAX_TEMP));
                    imageView = dayOfWeekFiveIcon;
                    break;
            }
            setDaysOfTheWeekHorizontalScrollView(weather, imageView);
        } catch (JSONException e) {
            e.printStackTrace();
            final String LEAK_WARNING = "json leak in changeDaysOfWeekInfo() method";
            Log.d(TAG, LEAK_WARNING);
        }
    }

    /**
     * Set Horizontal scroll View icons
     */
    private void setDaysOfTheWeekHorizontalScrollView(String weather, ImageView dayIcon) {
        if (weather.equalsIgnoreCase(SCATTERED_CLOUDS) || weather.equalsIgnoreCase(BROKEN_CLOUDS))
            dayIcon.setImageResource(R.drawable.cloudy);
        else if (weather.equalsIgnoreCase(FEW_CLOUDS))
            dayIcon.setImageResource(R.drawable.partly_cloudy);
        else if (weather.equalsIgnoreCase(SHOWER_RAIN) || weather.equalsIgnoreCase(RAIN) || weather.equalsIgnoreCase(THUNDERSTORM))
            dayIcon.setImageResource(R.drawable.rainy);
        else dayIcon.setImageResource(R.drawable.sunnyy);
    }

    /**
     * weather variable is taken from the parsed JSON, whatever the weather is -> set the background color and the main icon
     */
    private void setPhoneBackgroundColor(String weather, boolean night) {
        String currentMainColor;
        if (weather.equalsIgnoreCase(SCATTERED_CLOUDS) || weather.equalsIgnoreCase(BROKEN_CLOUDS)) {
            mainLayout.setBackgroundColor(Color.parseColor(BLUE_COLOR));
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(BLUE_COLOR)));
            mainIcon.setImageResource(R.drawable.cloudy);
            currentMainColor = BLUE_COLOR;
        } else if (weather.equalsIgnoreCase(FEW_CLOUDS)) {
            if (!night) {
                currentMainColor = YELLOW_COLOR;
                mainLayout.setBackgroundColor(Color.parseColor(YELLOW_COLOR));
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(YELLOW_COLOR)));
            } else {
                currentMainColor = RED_COLOR;
                mainLayout.setBackgroundColor(Color.parseColor(RED_COLOR));
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(RED_COLOR)));
            }
            mainIcon.setImageResource(R.drawable.partly_cloudy);
        } else if (weather.equalsIgnoreCase(SHOWER_RAIN) || weather.equalsIgnoreCase(RAIN) || weather.equalsIgnoreCase(THUNDERSTORM)) {
            mainLayout.setBackgroundColor(Color.parseColor(GREY_COLOR));
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(GREY_COLOR)));
            mainIcon.setImageResource(R.drawable.rainy);
            currentMainColor = GREY_COLOR;
        } else {
            if (night) {
                mainLayout.setBackgroundColor(Color.parseColor(BLUE_COLOR));
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(BLUE_COLOR)));
                mainIcon.setImageResource(R.drawable.night);
                currentMainColor = BLUE_COLOR;
            } else {
                mainLayout.setBackgroundColor(Color.parseColor(RED_COLOR));
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(RED_COLOR)));
                mainIcon.setImageResource(R.drawable.sunnyy);
                currentMainColor = RED_COLOR;
            }
        }
        setDaysOfWeekColor(currentMainColor);
    }

    /**
     * Check for internet connection
     */
    static boolean checkNetworkAvailability(Context context) {
        return ((ConnectivityManager)
                Objects.requireNonNull
                        (context.getSystemService(Context.CONNECTIVITY_SERVICE)))
                                                .getActiveNetworkInfo() != null;
    }

    /**
     * Get and parse the json info
     */
    static String execute(String http) {
        HttpConnection httpConnection = new HttpConnection(http);
        return httpConnection.connectAndParseJson();
    }
}
