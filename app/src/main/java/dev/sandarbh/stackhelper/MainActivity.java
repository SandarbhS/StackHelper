package dev.sandarbh.stackhelper;

import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    private String query,baseUrl;
    final private String TAG = "DEBUG : ", NOTHING_TO_SHOW = "Nothing to show.";
    private boolean exit = false;

    private List<Post> postsList;
    private CustomToast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBox = findViewById(R.id.search_box);
        searchButton = findViewById(R.id.search_button);

        init();
        initRecyclerView();

    }

    private void init() {
        baseUrl = "https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=relevance&q=";
        postsList = new ArrayList<Post>();
        toast = new CustomToast(this);

        emptyStatus = findViewById(R.id.empty);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = searchBox.getText().toString().trim();
                if(q.isEmpty()){
                    toast.showToast("Nothing to search.");
                }

                else if(!isConnectedToNetwork()){
                    toast.showToast("No internet connection.");
                }
                else{
                    query = baseUrl+q+"&site=stackoverflow&filter=!9Z(-wwK0y";

                    Log.e(TAG, "Query URL : "+query);
                    postsList.clear();
                    fetchResults();
                }
            }
        });
    }

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
                            JSONObject parent = new JSONObject(res);
                            JSONArray array = parent.getJSONArray("items");
                            Post newPost;

                            for (int i = 0; i < array.length(); ++i) {
                                newPost = new Post(array.getJSONObject(i));
                                postsList.add(newPost);
                            }

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
        rAdapter = new RecyclerAdapter(this, postsList);
        results.setAdapter(rAdapter);

        results.setLayoutManager(new LinearLayoutManager(this));
    }

    private void toggleEmptyStatus(){
        if(!postsList.isEmpty()){
            emptyStatus.setText("");
            emptyStatus.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else{
            emptyStatus.setText(NOTHING_TO_SHOW);
            emptyStatus.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    private boolean isConnectedToNetwork(){

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null) {
            Log.e("DEBUG", "OK");
            return true;
        }
        else
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
