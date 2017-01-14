package imspv.lycee.physics.DTO;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TopicsData {

    JSONArray data;

    Context context;


    public TopicsData(Context context, String json) {
        try {
            this.data = new JSONObject(json).getJSONArray("response");
        } catch (JSONException e) {
            this.data = new JSONArray();
        }
        this.context = context;
    }


    public int getTopicsSize(){
        return data.length();
    }

    public String getTopicsText(int position) {
        try {
            return data.getJSONObject(position).getString("topic");
        } catch (JSONException e) {
            return "null";
        }
    }

    public int getSubtopicsSize(int pos) throws JSONException {
        return data.getJSONObject(pos).getJSONArray("subtopics").length();
    }

    public String getSubtopicText(int topic, int position) {
        try {
            return data.getJSONObject(topic).getJSONArray("subtopics").getJSONObject(position).getString("subtopic");
        } catch (JSONException e) {
            return "null";
        }
    }

    public String getSubtopicID(int topic, int position) {
        try {
            return data.getJSONObject(topic).getJSONArray("subtopics").getJSONObject(position).getString("unique_id");
        } catch (JSONException e) {
            return "null";
        }
    }


}
