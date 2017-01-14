package imspv.lycee.physics.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import imspv.lycee.physics.R;
import imspv.lycee.physics.helper.JSONParser;

public class FoundTasks extends AppCompatActivity {

    String unique_id;
    int un_id;

    //Connection
    JSONParser jParser = new JSONParser();
    private static String url_find = "http://physics.atlascience.ru/find_tasks.php";
    private static String url_get_unique = "http://physics.atlascience.ru/get_unique_id.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TASKS = "tasks";
    private static final String TAG_TASK = "task";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CREATED = "created_at";
    private static final String TAG_UPDATED = "updated_at";
    private static final String COMPLEXITY = "complexity";
    private static final String TAG_UNIQUE = "unique_id";
    JSONArray subtopics = null;
    JSONArray tasks = null;
    TextView no_task;

    //Listview
    ArrayList<HashMap<String, String>> tasksList;
    public ListView lv;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_tasks);

        inits();
        getIntentFromFilter();
    }

    private void getIntentFromFilter(){
        Intent i = getIntent();
        un_id = i.getIntExtra(TAG_UNIQUE,0);
//        Log.i("UNIQUE_ID ",un_id); //6
        new GetFilteredData().execute();
    }

    private void inits() {
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TextView
        no_task = (TextView) findViewById(R.id.no_task);
        no_task.setVisibility(View.GONE);

        //ListView
        lv =(ListView) findViewById(R.id.found_tasks_list);
        tasksList = new ArrayList<HashMap<String, String>>();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Intent details = new Intent(getApplicationContext(), TaskDetailsActivity.class);

                String id = ((TextView) view.findViewById(R.id.id)).getText().toString();
                String created_at = ((TextView) view.findViewById(R.id.date)).getText().toString();
                String title = ((TextView) view.findViewById(R.id.title)).getText().toString();
                String updated_at = ((TextView) view.findViewById(R.id.updated_at)).getText().toString();
                String task = ((TextView) view.findViewById(R.id.task_row)).getText().toString();
                String complexity = ((TextView) view.findViewById(R.id.complexity)).getText().toString();

                details.putExtra(TAG_ID, id);
                details.putExtra(TAG_TITLE, title);
                details.putExtra(TAG_TASK, task);
                details.putExtra(COMPLEXITY,complexity);
                details.putExtra(TAG_CREATED, created_at);
                details.putExtra(TAG_UPDATED, updated_at);

                startActivityForResult(details,100);
            }
        });

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

    class GetFilteredData extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {

            //Getting all unique_ids
            List<NameValuePair> paramsss = new ArrayList<NameValuePair>();
            JSONObject js= jParser.makeHttpRequest(url_get_unique,"GET",paramsss);
            try{
                int success = js.getInt("success");
                if(success==1){
                    subtopics = js.getJSONArray("subtopics");
                    for(int i = 0; i < subtopics.length(); i++){
                        JSONObject c = subtopics.getJSONObject(i);
                        un_id = c.getInt("unique_id");

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            /*
            * Find tasks by selected parameters
            */
            List<NameValuePair> paramss = new ArrayList<NameValuePair>();
            paramss.add(new BasicNameValuePair("unique_id",String.valueOf(un_id)));
            JSONObject json = jParser.makeHttpRequest(url_find,"GET",paramss);
            Log.d("JSON IS WORKING",json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);
                if(success==1) {
                    tasks = json.getJSONArray(TAG_TASKS);
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);
                        String id = c.getString(TAG_ID);
                        String title = c.getString(TAG_TITLE);
                        String task = c.getString(TAG_TASK);

                        String complexity = c.getString(COMPLEXITY);
                        String created_at = c.getString(TAG_CREATED);
                        String updated_at = c.getString(TAG_UPDATED);

                        HashMap<String, String> map = new HashMap<String, String>();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = sdf.parse(created_at);
                            String your_format = new SimpleDateFormat("dd.MM.yy").format(date);
                            map.put(TAG_CREATED, your_format);
                        } catch (ParseException e) {
                            System.out.println(e.toString());
                        }
                        map.put(TAG_ID, id);
                        map.put(TAG_TITLE, title);
                        map.put(TAG_TASK, task);
                        map.put(COMPLEXITY, complexity);
                        map.put(TAG_UPDATED, updated_at);
                        tasksList.add(map);
                    }}else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            no_task.setVisibility(View.VISIBLE);
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("JSON ISN'T WORKING",e.toString());
            }


            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    simpleAdapter = new SimpleAdapter(FoundTasks.this,tasksList,R.layout.row_tasks, new String[]{
                            TAG_ID,TAG_TITLE, COMPLEXITY, TAG_CREATED,TAG_UPDATED,TAG_TASK
                    }, new int[]{R.id.id, R.id.title,R.id.complexity,R.id.date,R.id.updated_at,R.id.task_row});

                    lv.setAdapter(simpleAdapter);
                    simpleAdapter.notifyDataSetChanged();
                }
            });

        }
    }
}
