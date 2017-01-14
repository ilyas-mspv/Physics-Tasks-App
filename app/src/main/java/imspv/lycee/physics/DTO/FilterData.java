package imspv.lycee.physics.DTO;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FilterData {


    JSONArray data;

    Context context;


    public FilterData(Context context, String json) {
        try {
            this.data = new JSONObject(json).getJSONArray("response");
        } catch (JSONException e) {
            this.data = new JSONArray();
        }
        this.context = context;
    }

    public int getClassSize(){
        return data.length();
    }

    public String getClassText(int position) {
        try {
            return data.getJSONObject(position).getString("class");
        } catch (JSONException e) {
            return "null";
        }
    }

    public int getTopicSize(int pos) throws JSONException {
        return data.getJSONObject(pos).getJSONArray("topics").length();
    }

    public String getTopicsText(int topic, int position) {
        try {
            return data.getJSONObject(topic).getJSONArray("topics").getJSONObject(position).getString("topic");
        } catch (JSONException e) {
            return "null";
        }
    }

    public int getSubtopicSize(int class_pos,int position) throws JSONException {
        return  data.getJSONObject(class_pos).getJSONArray("topics")
                .getJSONObject(position).getJSONArray("subtopics").length();
    }
    public String getSubtopicText(int classes, int topic,int subtopic) throws JSONException {
        return data.getJSONObject(classes).getJSONArray("topics").getJSONObject(topic)
                .getJSONArray("subtopics").getJSONObject(subtopic).getString("subtopic");
    }

    public int getUniqueID(int classes, int topic, int subtopic) throws JSONException {
        return data.getJSONObject(classes).getJSONArray("topics").getJSONObject(topic)
                .getJSONArray("subtopics").getJSONObject(subtopic).getInt("unique_id");
    }

}
