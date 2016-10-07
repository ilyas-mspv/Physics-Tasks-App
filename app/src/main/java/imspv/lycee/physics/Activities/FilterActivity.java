package imspv.lycee.physics.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import imspv.lycee.physics.DTO.Subtopic;
import imspv.lycee.physics.DTO.Topic;
import imspv.lycee.physics.DTO.TopicsData;
import imspv.lycee.physics.R;
import imspv.lycee.physics.helper.JSONParser;

public class FilterActivity extends AppCompatActivity {

    JSONParser jParser = new JSONParser();
    private static String url_all_tasks = "http://physics.atlascience.ru/filter.php";
    private static final String TAG_RESPONSE = "success";
    JSONArray response = null;
//TODO make a correct implements
    private ArrayList<Topic> topics;
    private ArrayList<Subtopic> subtopics;
    Spinner topics_spinner, subtopics_spinner;

    private ProgressDialog pDialog;

    TopicsData topicsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        new  LoadTopics().execute();

        initSpinners();
        initToolbar();
    }

    private void initSpinners() {
        topics_spinner = (Spinner) findViewById(R.id.topics_spinner);
        subtopics_spinner= (Spinner) findViewById(R.id.subtopics_spinner);

        topics = new ArrayList<Topic>();
        subtopics = new ArrayList<Subtopic>();

        topics_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    class LoadTopics extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> paramss = new ArrayList<NameValuePair>();
            JSONObject json = jParser.makeHttpRequest(url_all_tasks,"GET",paramss);

            topicsData = new TopicsData(FilterActivity.this, json.toString());

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FilterActivity.this);
            pDialog.setMessage("Fetching data..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();

            populateSpinner();
        }

        private void populateSpinner() {
            List<String> topicsL = new ArrayList<String>();
            List <String> subtopicsL = new ArrayList<>();

            for (int i = 0; i < topics.size(); i++){
                for (int ii = 0; ii< subtopics.size();ii++){
                    topicsL.add(topicsData.getTopicText(i));
                    subtopicsL.add(topicsData.getSubtopicText(i,ii));
                }
            }
            ArrayAdapter<String> topicsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_item, topicsL);
            ArrayAdapter<String> subtopicsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_item, subtopicsL);

            subtopicsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            topicsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            topics_spinner.setAdapter(topicsAdapter);
            subtopics_spinner.setAdapter(subtopicsAdapter);
        }
    }

}
