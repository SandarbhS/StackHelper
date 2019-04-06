package dev.sandarbh.stackhelper;

import android.annotation.SuppressLint;
import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//A custom class to hold the data of the owner of the Post.
public class Post {

    public String qTitle,qBody,qURL;
    public Integer answerCount,viewCount,votesCount,activity;
    public long dateCreated;
    public User user;
    public boolean isAnswered;
    public JSONArray tags;

    private final String TAG = "DEBUG";

    public Post(JSONObject data) throws JSONException {

        JSONObject owner = new JSONObject(data.getJSONObject("owner").toString());
        try {
            user = new User(owner);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        qURL = data.getString("link");

        //Unescapes the characters from HTML text
        qTitle = Html.fromHtml(data.getString("title")).toString();
        qBody = Html.fromHtml(data.getString("body_markdown")).toString();

        answerCount = data.getInt("answer_count");
        votesCount = data.getInt("score");
        viewCount = data.getInt("view_count");
        activity = data.getInt("last_activity_date");
        dateCreated = data.getLong("creation_date");

        isAnswered =data.getBoolean("is_answered");

        tags = data.getJSONArray("tags");
    }

    //Converts the date to human-readable and required format
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

    //To convert 1000 to 1k and 1000000 to 1M
    public String getFormattedValue(Integer originalValue){
        Double value = originalValue*1.0;
        char suffix = ' ';
        int pos;
        String format;
        if(Math.abs(value)>1000000) {
            value /= 1000000;
            suffix = 'M';
        }
        else if(Math.abs(value)>1000){
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
