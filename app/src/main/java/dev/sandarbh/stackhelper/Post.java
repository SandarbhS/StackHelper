package dev.sandarbh.stackhelper;

import android.annotation.SuppressLint;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Post {

    public String qTitle,qBody,dateCreated;
    public Integer answerCount,viewCount,votesCount;
    public User user;
    public boolean isAnswered;
    public JSONArray tags;

    private final String TAG = "DEBUG";

    public Post(JSONObject data) throws JSONException {

        Log.e(TAG, "Post: Creating post");

        JSONObject owner = new JSONObject(data.getJSONObject("owner").toString());
        try {
            user = new User(owner);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        qTitle = Html.fromHtml(data.getString("title")).toString();
        qBody = Html.fromHtml(data.getString("body_markdown")).toString();

        answerCount = data.getInt("answer_count");
        votesCount = data.getInt("score");
        viewCount = data.getInt("view_count");

        dateCreated = getFormattedValue(data.getLong("creation_date"));

        isAnswered =data.getBoolean("is_answered");

        tags = data.getJSONArray("tags");
    }

    public String getFormattedValue(long value){
        Calendar cal = Calendar.getInstance();
        Date date = new Date(value*1000);
        cal.setTime(new Date(value*1000));

        Integer day,year;
        String month,time,format;

        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.ENGLISH);
        year = cal.get(Calendar.YEAR);

        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm");
        time = df.format(date.getTime());

        format = "asked "+month+" "+day+" '"+year.toString().substring(2)+" at "+time;

        return format;
    }

    public String getFormattedValue(Integer originalValue){
        Double value = originalValue*1.0;
        char suffix = ' ';
        int pos;
        String format;
        if(value>1000000) {
            value /= 1000000;
            suffix = 'M';
        }
        else if(value>1000){
            value/=1000;
            suffix = 'k';
        }

        format = value.toString();
        pos = format.indexOf('.');
        if(format.charAt(pos+1)=='0')
            format = format.substring(0,pos);

        else format = format.substring(0,format.indexOf('.')+2);

        return (format+suffix).trim();
    }
}
