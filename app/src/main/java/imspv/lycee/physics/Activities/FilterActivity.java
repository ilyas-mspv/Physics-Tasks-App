package imspv.lycee.physics.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
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

public class FilterActivity extends AppCompatActivity {

    JSONParser jParser = new JSONParser();
    private static String url_all_tasks = "http://physics.atlascience.ru/filter.php";
    private static final String TAG_RESPONSE = "success";
    JSONArray response = null;

    private ArrayList<Topic> topics;
    private ArrayList<Subtopic> subtopics;
    Spinner topics_spinner, subtopics_spinner, class_spinner;

    private ProgressDialog pDialog;

    TopicsData topicsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initToolbar();

        class_spinner = (Spinner) findViewById(R.id.classes_spinner);
        topics_spinner = (Spinner) findViewById(R.id.topics_spinner);
        subtopics_spinner= (Spinner) findViewById(R.id.subtopics_spinner);


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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateClasses();
                }
            });
        }
    }

    private void populateClasses() {
        List<String> classesL = new ArrayList<>();

//        for (int i = 0; i< topicsData.getClassesSize();i++){
//            classesL.add(topicsData.getClasesText(i));
//        }

        HintSpinner<String> hintSpinnerClasses= new HintSpinner<>(class_spinner,
                new HintAdapter<String>(this, R.string.hint_topics_spinner, classesL),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {

                    }
                });

       hintSpinnerClasses.init();
    }

    private  void populateTopics(){
        List<String> topicsL = new ArrayList<>();
//         for (int i = 0; i<topicsData.getTopicsSize();i++){
//
//         }
    }

}
