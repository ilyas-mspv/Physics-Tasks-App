package imspv.lycee.physics.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import imspv.lycee.physics.DTO.FilterData;
import imspv.lycee.physics.R;
import imspv.lycee.physics.helper.JSONParser;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class FilterActivity extends AppCompatActivity {

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_UNIQUE = "unique_id";
    public static FilterData topicsData;
    private static String url_filter = "http://physics.atlascience.ru/filter.php";
    private static String url_find = "http://physics.atlascience.ru/find_tasks.php";
    private static String url_unique = "http://physics.atlascience.ru/get_unique_id.php";
    JSONParser jParser = new JSONParser();
    JSONArray subtopics = null;
    Spinner topics_spinner, subtopics_spinner, class_spinner;
    FilterData topics_data;
    int unique_id;
    int un_id;
    Button filter_btn;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initToolbar();


        class_spinner = (Spinner) findViewById(R.id.classes_spinner);
        topics_spinner = (Spinner) findViewById(R.id.topics_spinner);
        subtopics_spinner= (Spinner) findViewById(R.id.subtopics_spinner);

        filter_btn = (Button) findViewById(R.id.filter_btn);
        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new FindTasks().execute();
            }
        });


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
            new HintAdapter<String>(this, R.string.hint_class_spinner, classL),
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
    private void populateSubtopic(final int class_pos, final int topic_position) throws JSONException {
        List<String> subtopicL = new ArrayList<>();

        for (int i = 0; i < topicsData.getSubtopicSize(class_pos,topic_position); i++){
            subtopicL.add(topicsData.getSubtopicText(class_pos,topic_position,i));
            unique_id =  topicsData.getUniqueID(class_pos,topic_position,i);
        }
        ArrayAdapter<String> subtopicAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item,subtopicL);
        HintSpinner<String> hintSpinnerSubtopic = new HintSpinner<>(
                subtopics_spinner,
                new HintAdapter<String>(this, R.string.hint_subtopics_spinner, subtopicL),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        try {
                            //TODO Do correctly
                            final int id = topics_data.getUniqueID(class_pos,topic_position,position);
                            Toast.makeText(getApplicationContext(),"Unique is: "+ id,Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        hintSpinnerSubtopic.init();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    private void sendUnique_id() {
        Intent intent = new Intent(getApplicationContext(),FoundTasks.class);

        intent.putExtra(TAG_UNIQUE,unique_id);

        startActivityForResult(intent,100);
    }

    class LoadTopics extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> paramss = new ArrayList<NameValuePair>();
            JSONObject json = jParser.makeHttpRequest(url_filter,"GET",paramss);
            JSONObject js = jParser.makeHttpRequest(url_unique,"GET",paramss);
            topicsData = new FilterData(FilterActivity.this, json.toString());
            topics_data = new FilterData(FilterActivity.this, js.toString());
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

    class FindTasks extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            //GET UNIQUE_ID FROM SERVER
            List<NameValuePair> paramsss = new ArrayList<NameValuePair>();
            JSONObject jn= jParser.makeHttpRequest(url_filter,"GET",paramsss);
            Log.d("JSON IS",jn.toString());


            //FIND BY UNIQUE_ID
            List<NameValuePair> paramss = new ArrayList<NameValuePair>();
            paramss.add(new BasicNameValuePair(TAG_UNIQUE,String.valueOf(un_id)));
            JSONObject json = jParser.makeHttpRequest(url_find,"GET",paramss);
            topicsData = new FilterData(FilterActivity.this, json.toString());

            try{
                int success = json.getInt(TAG_SUCCESS);
                if(success ==1){
                    sendUnique_id();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

}
