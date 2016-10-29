package imspv.lycee.physics.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import imspv.lycee.physics.DTO.Topic;
import imspv.lycee.physics.R;
import imspv.lycee.physics.helper.JSONParser;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class FilterActivity extends AppCompatActivity {

    JSONParser jParser = new JSONParser();
    private static String url_all_tasks = "http://physics.atlascience.ru/filter.php";
    private ProgressDialog pDialog;

    Spinner topics_spinner, subtopics_spinner, class_spinner;
    Topic topicsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initToolbar();


        class_spinner = (Spinner) findViewById(R.id.classes_spinner);
        topics_spinner = (Spinner) findViewById(R.id.topics_spinner);
        subtopics_spinner= (Spinner) findViewById(R.id.subtopics_spinner);


        new LoadTopics().execute();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void populateClass(){
        List<String> classL = new ArrayList<>();

        for (int i = 0; i < topicsData.getClassSize(); i++ ){
        classL.add(topicsData.getClassText(i));
        }

        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(getApplicationContext(),
            android.R.layout.simple_spinner_item, classL);

        HintSpinner<String> hintSpinnerClass = new HintSpinner<>(
            class_spinner,
            new HintAdapter<String>(this, R.string.hint_topics_spinner, classL),
            new HintSpinner.Callback<String>() {
                @Override
                public void onItemSelected(int position, String itemAtPosition) {
                    try {
                        populateTopic(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        );
        hintSpinnerClass.init();
    }

    private  void populateTopic(final int class_position) throws JSONException {
        List<String> topicL = new ArrayList<>();

        for (int i = 0; i < topicsData.getTopicSize(class_position); i++){
            topicL.add(topicsData.getTopicsText(class_position,i));
        }

        ArrayAdapter<String> topicAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item,topicL);
        HintSpinner<String> hintSpinnerTopic = new HintSpinner<>(
                topics_spinner,
                new HintAdapter<String>(this, R.string.hint_topics_spinner, topicL),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        try {
                            populateSubtopic(class_position,position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        hintSpinnerTopic.init();
    }

    private void populateSubtopic(int class_pos,int topic_position) throws JSONException {
        List<String> subtopicL = new ArrayList<>();

        for (int i = 0; i < topicsData.getSubtopicSize(class_pos,topic_position); i++){
            subtopicL.add(topicsData.getSubtopicText(class_pos,topic_position,i));
        }
        ArrayAdapter<String> subtopicAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item,subtopicL);
        HintSpinner<String> hintSpinnerSubtopic = new HintSpinner<>(
                subtopics_spinner,
                new HintAdapter<String>(this, R.string.hint_subtopics_spinner, subtopicL),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {

                        Toast.makeText(getApplicationContext(),"YEEAAAAHHHH",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        hintSpinnerSubtopic.init();
    }

    class LoadTopics extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> paramss = new ArrayList<NameValuePair>();
            JSONObject json = jParser.makeHttpRequest(url_all_tasks,"GET",paramss);
            topicsData = new Topic(FilterActivity.this, json.toString());

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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateClass();
                }
            });
        }
    }
}
