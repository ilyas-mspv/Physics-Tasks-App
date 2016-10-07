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

    public String getTopicText(int position) {
        try {
            return data.getJSONObject(position).getString("topic");
        } catch (JSONException e) {
            return "null";
        }
    }

    public String getSubtopicText(int topic, int position) {
        try {
            return data.getJSONObject(topic).getJSONArray("subtopics").getJSONObject(position).getString("subtopic");
        } catch (JSONException e) {
            return "null";
        }
    }

}
