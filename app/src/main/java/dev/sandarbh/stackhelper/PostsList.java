package dev.sandarbh.stackhelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//The Custom class to handle all the sorting and ordering tasks neatly.
public class PostsList {
    public List<Post> current, relevanceSorted;     /*The 'current' List is the list currently displayed while 'relevanceSorted' holds
                                                      the originally fetched relevance sorted list*/
    private boolean desc = true;                    //a flag to identify the sorting order

    public PostsList(){
        current = new ArrayList<>();
        relevanceSorted = new ArrayList<>();
    }

    public void populateList(JSONObject parent) throws JSONException {
        JSONArray array = parent.getJSONArray("items");
        Post newPost;

        for (int i = 0; i < array.length(); ++i) {
            newPost = new Post(array.getJSONObject(i));
            current.add(newPost);
        }
        relevanceSorted = current;
    }

    public void clear(){
        current.clear();
        relevanceSorted.clear();
    }

    public void sortList(String sort,String order){
        desc = order.equals("desc");
        switch (sort){
            case "relevance" : current = relevanceSorted; orderList(order);         //Get the relevance sorted list and reorder it.
            break;

            case "votes" : Collections.sort(current, (o1, o2) -> (desc ? o2.votesCount.compareTo(o1.votesCount) : o1.votesCount.compareTo(o2.votesCount)));
            break;

            case "activity" : Collections.sort(current, (o1, o2) -> (desc ? o2.votesCount.compareTo(o1.activity) : o1.votesCount.compareTo(o2.activity)));
            break;

            case "creation" : Collections.sort(current, (o1, o2) -> (int)(desc ? o2.votesCount-o1.dateCreated : o1.votesCount-o2.dateCreated));
        }
    }

    public void orderList(String order){
        if((order.equals("desc") && !desc)||(order.equals("asc") && desc)){
                Collections.reverse(current);
                desc = !desc;
        }
    }
}
