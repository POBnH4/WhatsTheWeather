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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

import static android.support.v7.widget.SearchView.OnClickListener;
import static android.support.v7.widget.SearchView.OnQueryTextListener;
import static com.example.peterboncheff.coursework.LoginActivity.USE_CURRENT_USER_DATA;
import static java.lang.String.format;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity implements OnClickListener, Serializable {

    /**
     * USE ?
     */
    enum Days {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    }

    private static final int TOTAL_TABS = 3, PLUS_ONE_POSITION = 1, NO_MORE_DATA = -1;
    private static final String SNACK_BAR_TEXT = "Replace with your own action", ACTION_TEST = "Action";
    private final String TOAST_TEXT = "Couldn't find the weather";
    private final String DEFAULT_CITY = "Dubai";
    static final String NEW_LINE = "\n";
    static final String DATE_FORMAT = "hh:mm:ss", INVALID_CITY = "Couldn't find city", TAG = "hallo";
    static final String API_JSON_TEMP = "temp", INTERNET_ERROR = "No internet connection";
    static final String BEGINNING_API_HTTP = "http://api.openweathermap.org/data/2.5/forecast?q=";
    static final String END_OF_API_HTTP = "&units=metric&appid=421389c958155f9c8ed14a5b0010c202";
    //http://api.openweathermap.org/data/2.5/forecast?q=     &units=metric&appid=421389c958155f9c8ed14a5b0010c202

    private final String GREY_COLOR = "#95a5a5", RED_COLOR = "#e84c3d", YELLOW_COLOR = "#dccc0b", BLUE_COLOR = "#125c67";
    private final String DARK_RED_COLOR_TYPE_ONE = "#a31f13", DARK_RED_COLOR_TYPE_TWO = "#db2b1b", BRIGHT_RED_COLOR_TYPE_ONE = "#ea594b";
    private final String BRIGHT_RED_COLOR_TYPE_TWO = "#ec6659", DARK_GREY_COLOR_TYPE_ONE = "#6e8181", DARK_GREY_COLOR_TYPE_TWO = "#7d9090";
    private final String BRIGHT_GREY_COLOR_TYPE_ONE = "#adbaba", BRIGHT_GREY_COLOR_TYPE_TWO = "#bdc8c8", DARK_YELLOW_COLOR_TYPE_ONE = "#a09407";
    private final String DARK_YELLOW_COLOR_TYPE_TWO = "#beb009", BRIGHT_YELLOW_COLOR_TYPE_ONE = "#f3e213", BRIGHT_YELLOW_COLOR_TYPE_TWO = "#f5e631";
    private final String DARK_BLUE_COLOR_TYPE_ONE = "#2a6a74", DARK_BLUE_COLOR_TYPE_TWO = "#2a6a74", BRIGHT_BLUE_COLOR_TYPE_ONE = "#1a8c9b";
    private final String BRIGHT_BLUE_COLOR_TYPE_TWO = "#518289";

    private final String SCATTERED_CLOUDS = "scattered clouds", BROKEN_CLOUDS = "broken clouds", FEW_CLOUDS = "few clouds";
    private final String SHOWER_RAIN = "shower rain", RAIN = "rain", THUNDERSTORM = "thunderstorm";
    private final String ONE_DECIMAL_FORMAT_PATTERN = "#.0", DATE_FORMAT_PATTERN = "yyyy-MM-dd hh:mm:ss";
    private final DecimalFormat ONE_DECIMAL_PLACE = new DecimalFormat(ONE_DECIMAL_FORMAT_PATTERN);
    private final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    private final String TEMP_FORMAT_CELSIUS = "%s°";
    static boolean DISPLAY_TEMPERATURE_IN_CELSIUS = true;

    //Celsius to Fahrenheit formula -> (1°C × 9/5) + 32 = 33.8°F | symbol -> &#8457;

    final String API_JSON_DESCRIPTION = "description", API_JSON_HUMIDITY = "humidity", API_FORECAST_LIST = "list", API_SELECT_DATE = "dt_txt";
    final String API_JSON_PRESSURE = "pressure", API_JSON_COUNTRY = "country", API_JSON_WIND_SPEED = "speed", API_SELECT_CITY = "city";
    final String API_SELECT_MAIN_ARRAY = "main", API_SELECT_WIND = "wind", API_SELECT_WEATHER = "weather", API_NAME_OF_CITY = "name";
    final int GET_CURRENT_DAY = 0, SELECT_FIRST = 0;

    private ActionBar actionBar;

    private ConstraintLayout mainLayout;
    private Button refreshButton;
    private TextView currentLocation, lastUpdated, currentTemperature, humidityTextField, pressureTextField, windTextField;
    private ImageView mainIcon;
    private HorizontalScrollView daysOfTheWeekHorizontalScrollView;
    private String currentLocationIs = "Dubai", currentLocationIsCheck = "Dubai";

    private LinearLayout dayOfWeekOneLayout, dayOfWeekTwoLayout, dayOfWeekThreeLayout, dayOfWeekFourLayout, dayOfWeekFiveLayout;
    private TextView dayOfWeekOneName, dayOfWeekTwoName, dayOfWeekThreeName, dayOfWeekFourName, dayOfWeekFiveName;
    private TextView daysOfWeekOneTemp, daysOfWeekTwoTemp, daysOfWeekThreeTemp, daysOfWeekFourTemp, daysOfWeekFiveTemp;
    private ImageView dayOfWeekOneIcon, dayOfWeekTwoIcon, dayOfWeekThreeIcon, dayOfWeekFourIcon, dayOfWeekFiveIcon;

    private User currentUser;
    private ModuleDao moduleDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        final String HEADER_TITLE = "";
        setTitle(HEADER_TITLE);
        setContentView(R.layout.activity_main);
        taskLoadUp(DEFAULT_CITY);
        init();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchPref = sharedPref.getBoolean(SettingsActivity.KEY_PREF_EXAMPLE_SWITCH, false);
        //SharedPreferences preferences = getSharedPreferences(SettingsFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

        //set up database;
        ModuleDatabase db = ModuleDatabase.getDatabase(this);
        this.moduleDao = db.moduleDao();

        Log.d(TAG, Boolean.toString(switchPref) + "-> switch changed");
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
        this.daysOfTheWeekHorizontalScrollView = findViewById(R.id.daysOfTheWeek);

        this.refreshButton.setOnClickListener(this);
        this.refreshButton.setBackgroundResource(R.drawable.ic_refresh);

        this.actionBar = getSupportActionBar();
        final int SET_HEADER_ELEVATION = 0;
        assert this.actionBar != null;
        this.actionBar.setElevation(SET_HEADER_ELEVATION);

        Intent intent = getIntent();
        this.currentUser = (User) intent.getSerializableExtra(USE_CURRENT_USER_DATA);

        setDaysOfWeekVariables();
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
        MenuItem actionBarSpinner = menu.findItem(R.id.actionBarSpinner);
        // spinner stuff continue.....
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
        this.dayOfWeekThreeLayout = findViewById(R.id.dayOfWeekThreeLayout);
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
        Log.d(TAG, "FAHRENHEIT TO CEL: " + temperatureInF);
        return ONE_DECIMAL_PLACE.format(formula);
    }

    private String convertToFahrenheit(String temperatureInCelsius) {
        double temperatureInC = Double.parseDouble(temperatureInCelsius.substring(0, temperatureInCelsius.length() - 1));
        double formula = (temperatureInC * 9 / 5) + 32; // For instance, (1°C × 9/5) + 32 = 33.8°F;
        Log.d(TAG, "CELSIUS TO FAHREN: " + temperatureInC);

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

            final String CURRENT_LOCATION_FORMAT = "%s,%s", LAST_UPDATED_FORMAT = "Last updated %s";
            final String HUMIDITY_FORMAT = "%s", PRESSURE_FORMAT = "%s hPa", SEPARATOR = ", ";

            final Date DATE = new Date();
            final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);
            final String CURRENT_TIME = SIMPLE_DATE_FORMAT.format(DATE);
            try {
                JSONObject json = new JSONObject(query);
                JSONObject forecastMain = json.getJSONArray(API_FORECAST_LIST).getJSONObject(GET_CURRENT_DAY).getJSONObject(API_SELECT_MAIN_ARRAY);
                JSONObject forecastWeather = json.getJSONArray(API_FORECAST_LIST).getJSONObject(GET_CURRENT_DAY).getJSONArray(API_SELECT_WEATHER).getJSONObject(SELECT_FIRST);
                JSONObject forecastWind = json.getJSONArray(API_FORECAST_LIST).getJSONObject(GET_CURRENT_DAY).getJSONObject(API_SELECT_WIND);
                JSONObject forecastDateInfo = json.getJSONArray(API_FORECAST_LIST).getJSONObject(GET_CURRENT_DAY);
                JSONObject city = json.getJSONObject(API_SELECT_CITY);

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

                setPhoneBackgroundColor(forecastWeather.getString(API_JSON_DESCRIPTION), checkDayOrNight(CURRENT_TIME));
                saveCurrentLocation(CURRENT_LOCATION, CURRENT_TIME, TEMPERATURE, HUMIDITY, PRESSURE, WIND_SPEED);
                Log.d(TAG, "calendar " + GET_DATE);

                parseDayOfWeekName(GET_DATE);
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
        final int DAYS_REQUIRED = 6, GET_CURRENT_DAY = 0;
        final int ITERATE_IN_ADVANCE = 100; //since the data in the json file is for every 3 hours,
        // we iterate taking only one forecast for a day, thus till we get the data for 5 different days
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
    private boolean checkDateValidity(String date, String dateCheckStore) {
        final int DATE_INDEX_ONE = 0, DATE_LAST_INDEX = 10; //the json file parses date that
        // is in a format yyyy-MM-dd hh-mm-ss so we cut of the part that we don't need -> "  hh-mm-ss";
        return !date.substring(DATE_INDEX_ONE, DATE_LAST_INDEX).equals(dateCheckStore.substring(DATE_INDEX_ONE, DATE_LAST_INDEX));
    }

    /**
     * parse day of the week for a certain date in the past
     */
    private void parseDayOfWeekName(String forecastDateInfo) {
        try {
            final Date date = dateFormat.parse(forecastDateInfo);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            setDaysOfWeekNames(calendar.get(Calendar.DAY_OF_WEEK));
            Log.d(TAG, "calendar " + calendar.get(Calendar.DAY_OF_WEEK));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "calendar error");

        }
    }

    /**
     * set horizontal front view days of week text
     */
    private void setDaysOfWeekNames(int day) {
        final int DAYS_IN_A_WEEK = 7, FIRST_DAY_OF_THE_WEEK = 1;
        this.dayOfWeekOneName.setText(getNameOfWeekDay(day));
        this.dayOfWeekTwoName.setText(getNameOfWeekDay((day >= DAYS_IN_A_WEEK) ? day = FIRST_DAY_OF_THE_WEEK : ++day));
        this.dayOfWeekThreeName.setText(getNameOfWeekDay((day >= DAYS_IN_A_WEEK) ? day = FIRST_DAY_OF_THE_WEEK : ++day));
        this.dayOfWeekFourName.setText(getNameOfWeekDay((day >= DAYS_IN_A_WEEK) ? day = FIRST_DAY_OF_THE_WEEK : ++day));
        Log.d(TAG, "Before -> " + dayOfWeekFiveName.getText().toString());
        this.dayOfWeekFiveName.setText(getNameOfWeekDay((day >= DAYS_IN_A_WEEK) ? FIRST_DAY_OF_THE_WEEK : ++day));
        Log.d(TAG, "Now -> " + dayOfWeekFiveName.getText().toString());
    }

    /**
     * Return name of the day
     */
    private String getNameOfWeekDay(int day) {
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
     * Save main activity variables/icons/colors for current user
     */
    private void saveCurrentLocation(String currentLocation, String lastUpdated,
                                     double currentTemperature, String humidity, String pressure, String windSpeed) {

        //Module mainModule = new Module();
        //m.setReference();
        //mainModule.setCreatedOn(new Date());
        //mainModule.setScqfCredits(1);
        //MainActivity.InsertModuleTask task = new MainActivity();
        //task.execute(m);
    }

    /**
     * if the time is 20:00 - 6:00 set night icons, else day icons
     */
    private boolean checkDayOrNight(String time) {
        final int NIGHT_TIME = 20, MORNING_TIME = 6; //20:00 represents 08:00PM and 6 represents 06:00AM;
        final int FIRST_HOUR_CHARACTER = 0, SECOND_HOUR_CHARACTER = 1, CHECK_WHETHER_TIME_IS_AM = 0;
        int hour = time.charAt(FIRST_HOUR_CHARACTER) + time.charAt(SECOND_HOUR_CHARACTER);
        if (time.charAt(FIRST_HOUR_CHARACTER) == CHECK_WHETHER_TIME_IS_AM) hour = time.charAt(SECOND_HOUR_CHARACTER);
        return hour >= NIGHT_TIME || hour <= MORNING_TIME; // true = night, false = day;
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
            if (night) {
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
        return ((ConnectivityManager) Objects.requireNonNull(context.getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo() != null;
    }

    /**
     * Get and parse the json info
     */
    static String execute(String http) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(http);
            connection = (HttpURLConnection) url.openConnection();
            final String CONTENT_TYPE = "content-type", APPLICATION_JSON_CHARSET = "application/json; charset=utf-8";
            final String CONTENT_LANGUAGE = "Content-Language", LANGUAGE = "en-GB";
            connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON_CHARSET);
            connection.setRequestProperty(CONTENT_LANGUAGE, LANGUAGE);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(false);

            InputStream in;
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) in = connection.getErrorStream();
            else in = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
                response.append(NEW_LINE);
            }
            bufferedReader.close();
            return valueOf(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

}
