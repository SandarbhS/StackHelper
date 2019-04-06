package dev.sandarbh.stackhelper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText searchBox;
    private TextView emptyStatus;
    private ImageButton searchButton;
    private RecyclerView results;
    private RecyclerAdapter rAdapter;
    private Spinner sortSpinner,orderSpinner;

    private String query,baseUrl,sort,order;
    final private String TAG = "DEBUG : ";
    private boolean exit = false;

    private PostsList postsList;      //A custom class with 2 Lists
    private CustomToast toast;        //A custom toast class for a good looking and easy to handle toasts


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Widgets

        searchBox = findViewById(R.id.search_box);
        searchButton = findViewById(R.id.search_button);
        sortSpinner = findViewById(R.id.sort);
        orderSpinner = findViewById(R.id.order);

        init();                 //Initializes variables and sets OnClickListener for Search button
        initSpinners();         //Initializes spinners with adapters
        initRecyclerView();     //Initializes Recycler View

    }

    private void init() {
        sort="";
        order="";
        baseUrl = "https://api.stackexchange.com/2.2/search/advanced?";

        toast = new CustomToast(this);
        postsList = new PostsList();

        emptyStatus = findViewById(R.id.empty);

        searchButton.setOnClickListener(v -> {
            hideKeyboard(this);        //Hides the Soft Keyboard

            String q = searchBox.getText().toString().trim();
            if(q.isEmpty()){
                toast.showToast("Nothing to search.");
            }

            else if(!isConnectedToNetwork()){
                toast.showToast("No internet connection.");
            }
            else{
                query = baseUrl+"order="+order+"&sort=relevance&q="+q+"&site=stackoverflow&filter=!9Z(-wwK0y";  /*always fetch the list relevance sorted
                                                                                                                then locally modify according to filters*/
                Log.d(TAG, "Query URL : "+query);
                postsList.clear();
                fetchResults();
            }
        });

        searchBox.setOnEditorActionListener((v, actionId, event) -> {

            if(actionId== EditorInfo.IME_ACTION_GO){
                searchButton.performClick();
                return true;
            }
            return false;
        });
    }

    //The main function that uses OkHttp Client to fetch results using stackexchange api.
    private void fetchResults() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(query).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                toast.showToast("Network Error. Please try again.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String res = response.body().string();
                    if (res != null) {
                        try {
                            JSONObject parent = new JSONObject(res);            //JSON PARSING
                            postsList.populateList(parent);
                            if(!sort.equals("relevance"))
                                postsList.sortList(sort,order);

                            runOnUiThread(() -> {
                                toggleEmptyStatus();
                                rAdapter.notifyDataSetChanged();
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


    }

    private void initRecyclerView() {
        results = findViewById(R.id.results);
        rAdapter = new RecyclerAdapter(this, postsList.current);
        results.setAdapter(rAdapter);

        results.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initSpinners(){
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this, R.array.sortOptions, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if(!sort.equals(selected)){
                    sort = selected;
                    if(!postsList.current.isEmpty()){
                        postsList.sortList(sort,order);         //sorts the already fetched list without making a new request
                        rAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(this, R.array.orderOptions, android.R.layout.simple_spinner_item);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(orderAdapter);

        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if(!order.equals(selected)){
                    order = selected;
                    if(!postsList.current.isEmpty()){
                        postsList.orderList(order);                //orders the already fetched list without making a new request
                        rAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Toggle the visibility of the "Nothing to Show" Textview.
    private void toggleEmptyStatus(){
        if(!postsList.current.isEmpty()){
            emptyStatus.setText("");
            emptyStatus.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else{
            emptyStatus.setText(getResources().getString(R.string.nothing_to_show));
            emptyStatus.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    //Checks internet connection
    private boolean isConnectedToNetwork(){

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null) {
            Log.d("DEBUG", "Network OK");
            return true;
        }
            return false;
    }

    @Override
    public void onBackPressed() {
        if(exit)
            finish();

        toast.showToast("Press back again to exit.");
        exit = true;
        new Handler().postDelayed(() -> exit = false,3000);
    }
}
