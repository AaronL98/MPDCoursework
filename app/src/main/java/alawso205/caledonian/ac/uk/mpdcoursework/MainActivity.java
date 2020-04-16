package alawso205.caledonian.ac.uk.mpdcoursework;


import android.app.DatePickerDialog;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import alawso205.caledonian.ac.uk.mpdcoursework.Helper.BottomSheetModal;
import alawso205.caledonian.ac.uk.mpdcoursework.Helper.OnParseComplete;
import alawso205.caledonian.ac.uk.mpdcoursework.Helper.RSSItemAdapter;
import alawso205.caledonian.ac.uk.mpdcoursework.Helper.RSSItemType;
import alawso205.caledonian.ac.uk.mpdcoursework.Helper.XMLHelper;
import alawso205.caledonian.ac.uk.mpdcoursework.Model.RSSItem;
import androidx.appcompat.app.AppCompatActivity;

/*
* Aaron Lawson (S1624910)
* */

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, BottomSheetModal.BottomSheetListener
{

    private ArrayList<RSSItem> roadworks = new ArrayList<>();
    private ArrayList<RSSItem> plannedRoadworks = new ArrayList<>();
    private ArrayList<RSSItem> currentIncidents = new ArrayList<>();

    private ListView listView;
    private RSSItemAdapter adapter;
    private EditText searchBar;
    private TextView listDate;
    private TextView listHeading;

    private GoogleMap map;

    private FloatingActionButton dateFab;
    private FloatingActionButton contentFab;

    // Traffic Scotland URLs
    private String currentIncidentsSource = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String roadworksSource = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String plannedRoadworksSource = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";

    private boolean mapReady = false;
    private boolean mapFocused = false;
    private RSSItemType activeItemType = RSSItemType.ROADWORKS;

    private Date filterDate;
    private String filterString;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        searchBar = findViewById(R.id.searchBar);
        dateFab = findViewById(R.id.dateFab);
        contentFab = findViewById(R.id.contentFab);

        listDate = findViewById(R.id.listDate);
        listHeading = findViewById(R.id.listHeading);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);


        dateFab.bringToFront();
        contentFab.bringToFront();


        //Unfocus the search bar when clicking elsewhere.
        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //Submit/done on searchbar edittext
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    filterString = searchBar.getText().toString();
                    if(searchBar.getText().toString().isEmpty())
                        filterString = null;
                    filterData();
                }
                return false;
            }
        });

        //Calendar listener
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            //Detect date set
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Log.i("Selected date: ", ""+myCalendar.getTime().toString());


                filterDate = myCalendar.getTime();
                filterData();
                DateFormat format = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH);
                listDate.setText(format.format(myCalendar.getTime()));
            }

        };

        dateFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        contentFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                BottomSheetModal bsm = new BottomSheetModal();
                bsm.show(getSupportFragmentManager(), "bottomSheetModal ");
            }
        });


        //Fix listview scrolling up and down within a bottom sheet
        listView.setOnTouchListener(new ListView.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow NestedScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow NestedScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        }
    });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<RSSItem> oneItem = new ArrayList<>();
                oneItem.add(getActiveItems().get(position));
                updateMap(activeItemType, oneItem, map);
                mapFocused = true;
            }
        });

    }

    //When map is ready to be used
    @Override
    public void onMapReady(final GoogleMap map){
        this.map = map;
        //Set map ready to true
        map.setMaxZoomPreference(17.0f);
        mapReady = true;

        //Load in the data from the XML.
        //Load Current incidents to its array list
        new XMLHelper(currentIncidentsSource, RSSItemType.CURRENT_INCIDENTS, new OnParseComplete() {
            @Override
            public void onParseComplete(ArrayList<RSSItem> items) {
                Log.i("XMLHelper", "Loaded current incidents");
                currentIncidents = items;
            }
        });

        //Load roadworks to its array list
        new XMLHelper(roadworksSource, RSSItemType.ROADWORKS, new OnParseComplete() {
            @Override
            public void onParseComplete(ArrayList<RSSItem> items) {
                Log.i("XMLHelper", "Loaded roadworks");
                roadworks = items;
                //The default active type of items to display, so populate these on the bottom sheet as the onCreate runs
                updateMap(RSSItemType.CURRENT_INCIDENTS, items, map);
            }
        });

        //Load planned roadworks to its array list
        new XMLHelper(plannedRoadworksSource, RSSItemType.PLANNED_ROADWORKS, new OnParseComplete() {
            @Override
            public void onParseComplete(ArrayList<RSSItem> items) {
                Log.i("XMLHelper", "Loaded planned roadworks");
                plannedRoadworks = items;
            }
        });



        /****/
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mapFocused == true){
                    updateMap(activeItemType, getActiveItems(), map);
                    mapFocused = false;
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public ArrayList<RSSItem> getActiveItems(){
        ArrayList<RSSItem> list = new ArrayList<RSSItem>();
        switch(activeItemType){
            case CURRENT_INCIDENTS:
                list = currentIncidents;
                break;
            case ROADWORKS:
                list = roadworks;
                break;
            case PLANNED_ROADWORKS:
                list = plannedRoadworks;
                break;
        }
        return list;
    }

    public void updateMap(RSSItemType type, ArrayList<RSSItem> items, GoogleMap map){
        //Clear the map
        map.clear();

        //Set the list in the bottom sheet
        adapter = new RSSItemAdapter(MainActivity.this, items);
        listView.setAdapter(adapter);


        //Create new bounds
        LatLngBounds.Builder b = new LatLngBounds.Builder();

        //Set the markers on the map
        for(RSSItem item : items){
            LatLng latlng = new LatLng(item.getLat(),item.getLng());
            map.addMarker(new MarkerOptions().position(latlng).title(item.getTitle()));
            b.include(latlng);
        }

        if(items.size() > 0){
            LatLngBounds bounds = b.build();
            //Zoom on the map to fit all the markers
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        }


        String message = "Showing data for " + items.size() + " items";
        if(filterString != null)
            message += " '" + filterString + "'";

        if(filterDate != null){
            DateFormat format = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH);
            message += " during " + format.format(filterDate);

        }
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    //RSS type selected, on click of item in bottomsheetmodal
    @Override
    public void onClicked(RSSItemType rssItemType) {
        activeItemType = rssItemType;
        updateMap(rssItemType, getActiveItems(), map);

        listDate.setText("");
        switch(rssItemType){
            case PLANNED_ROADWORKS:
                listHeading.setText("Planned Roadworks");
                break;
            case ROADWORKS:
                listHeading.setText("Roadworks");
                break;
            case CURRENT_INCIDENTS:
                listHeading.setText("Current Incidents");
                break;
        }

    }

    //Filter the data on the map, with the stored global variables of the search and date conditions
    public void filterData(){
        //Make new temp list
        ArrayList<RSSItem> filteredList = new ArrayList<RSSItem>();

        //Loop over every item in the active list/map and add it to the local list if it meets condition
        for(RSSItem loopItem : getActiveItems()){
            //If a search condition has been entered and matches loop item, or if no search condition is entered:
            if(loopItem
                    .getTitle()
                    .toLowerCase()
                    .contains(
                            searchBar.getText()
                                    .toString()
                                    .toLowerCase())
                    || filterString == null){

                if(filterDate != null &&
                        loopItem
                                .getStartDate().before(filterDate) &&
                        loopItem
                                .getEndDate().after(filterDate))
                {
                    //Add the item
                    filteredList.add(loopItem);
                }else if(filterDate == null){
                    //Also add the item
                    filteredList.add(loopItem);
                }

            }
        }

        //Update the map with the new list
        updateMap(activeItemType, filteredList, map);
    }


} /**End of MainActivity**/