package imspv.lycee.physics.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import imspv.lycee.physics.DTO.Subtopic;
import imspv.lycee.physics.DTO.Topic;
import imspv.lycee.physics.DTO.TopicsData;
import imspv.lycee.physics.R;
import imspv.lycee.physics.helper.JSONParser;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class CreateTask extends AppCompatActivity {

    ProgressDialog pDialog;
    private static String url_create_task ="http://physics.atlascience.ru/create_task.php";
    private static String url_filter = "http://physics.atlascience.ru/filter1.php";
    private static final String TAG_SUCCESS = "success";

    //TODO add other type of data, like a photo uploading, add creating topic/subtopic and complexity.
    //editText
    JSONParser jParser = new JSONParser();
    EditText inputTitle;
    EditText inputTask;

    //spinners
    private ArrayList<Topic> topics;
    private ArrayList<Subtopic> subtopics;
    Spinner inputComplexity,topics_spinner, subtopics_spinner;
    TopicsData topicsData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        inits();
        new LoadTopics().execute();


        Button createBTN = (Button) findViewById(R.id.create_task_btn);
        createBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateNewTask().execute();
            }
        });
    }
    public void setUI() {
        populateSpinner();
    }

    private void inits() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputTitle = (EditText) findViewById(R.id.task_title_edit_text);
        inputTask = (EditText) findViewById(R.id.task_edit_text);
        inputComplexity = (Spinner) findViewById(R.id.spinner);

        topics_spinner = (Spinner) findViewById(R.id.topics_spinner_create);
        subtopics_spinner = (Spinner) findViewById(R.id.subtopics_spinner_create);


    }

    private void populateSpinner() {
        List<String> topicsL = new ArrayList<String>();

        for (int i = 0; i < topicsData.getTopicsSize(); i++){
            topicsL.add(topicsData.getTopicsText(i));
        }
        ArrayAdapter<String> topicsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, topicsL);

        HintSpinner<String> hintSpinnerTopics = new HintSpinner<>(
                topics_spinner,
                new HintAdapter<String>(this, R.string.hint_topics_spinner, topicsL),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        populateSpinnerTwo(position);
                    }
                }
        );
        hintSpinnerTopics.init();
    }

    private void populateSpinnerTwo(int position) {
        List<String> topicsL = new ArrayList<String>();

        try {
            for (int i = 0; i < topicsData.getSubtopicsSize(position); i++){
                topicsL.add(topicsData.getSubtopicText(position, i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HintSpinner<String> hintSpinnerSubTopics = new HintSpinner<>(
                subtopics_spinner,
                new HintAdapter<String>(this, R.string.hint_subtopics_spinner, topicsL),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        // TODO: 15.10.2016 make filter by class too.
                    }
                }
        );
        hintSpinnerSubTopics.init();
    }

    class CreateNewTask extends AsyncTask<String,String,String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateTask.this);
            pDialog.setMessage(getString(R.string.creating));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {


            String title = inputTitle.getText().toString();
            String task = inputTask.getText().toString();
            String complexity = inputComplexity.getSelectedItem().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("title",title));
            params.add(new BasicNameValuePair("task",task));
            params.add(new BasicNameValuePair("complexity",complexity));

            JSONObject json = jParser.makeHttpRequest(url_create_task,"POST",params);

            try{
                int success = json.getInt(TAG_SUCCESS);
                if(success ==1){
                    startActivity(new Intent(getApplicationContext(), AllTasksActivity.class));
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
        }
    }

    class LoadTopics extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> paramss = new ArrayList<NameValuePair>();
            JSONObject json = jParser.makeHttpRequest(url_filter,"GET",paramss);
            Log.d("MYLOGS", json.toString());
            topicsData = new TopicsData(CreateTask.this, json.toString());

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateTask.this);
            pDialog.setMessage("Fetching data..");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();

            setUI();
        }
    }
}
