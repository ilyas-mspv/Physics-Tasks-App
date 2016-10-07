package imspv.lycee.physics.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import imspv.lycee.physics.R;
import imspv.lycee.physics.helper.JSONParser;

public class AllTasksActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    //TODO saving data to sharedPrefs
    private ProgressDialog dialog;
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> tasksList;
    private static String url_all_tasks = "http://physics.atlascience.ru/get_all_tasks.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TASKS = "tasks";
    private static final String TAG_TASK = "task";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CREATED = "created_at";
    private static final String TAG_UPDATED = "updated_at";
    private static final String COMPLEXITY = "complexity";
    public  ListView lv;
    JSONArray tasks = null;
    Context context;
    SimpleAdapter simpleAdapter;

    SharedPreferences sharedPref;
    SwipeRefreshLayout mSwipeRefreshLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_tasks);
        inits();
        initToolbar();

        sharedPref = getSharedPreferences("TaskData", Context.MODE_PRIVATE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {


               if(isOnline()){

                mSwipeRefreshLayout.setRefreshing(true);

                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);

                        if(tasksList.isEmpty()){
                            new LoadAllTasks().execute();
                        }
                    }
                }, 3000);
               }
            }
        });

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private void refreshTasks(){

        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                if(isOnline()){
                    if(tasksList.isEmpty()){
                        Toast.makeText(getApplicationContext(),"No task found or check your internet connection",Toast.LENGTH_SHORT).show();
                    }else{
                        new LoadAllTasks().execute();
                    }
                }
            }
        }, 3000);

    }

    @Override
    public void onRefresh() {
        refreshTasks();
    }

    private void inits() {


        lv =(ListView) findViewById(R.id.tasksList);
        tasksList = new ArrayList<HashMap<String, String>>();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = ((TextView) view.findViewById(R.id.id)).getText().toString();
                String created_at = ((TextView) view.findViewById(R.id.created_at)).getText().toString();
                String title = ((TextView) view.findViewById(R.id.title)).getText().toString();
                String updated_at = ((TextView) view.findViewById(R.id.updated_at)).getText().toString();
                String task = ((TextView) view.findViewById(R.id.task_row)).getText().toString();
                String complexity = ((TextView) view.findViewById(R.id.complexity)).getText().toString();
                Intent details = new Intent(getApplicationContext(), TaskDetailsActivity.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.all_tasks_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case  R.id.Settings:
               startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.filter:
                //TODO Alert
                startActivity(new Intent(this,FilterActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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

    class LoadAllTasks extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            if(isOnline()){
                tasksList.clear();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                JSONObject json = jParser.makeHttpRequest(url_all_tasks,"GET",params);
                try {

                    int success = json.getInt(TAG_SUCCESS);
                    if(success==1){
                        tasks = json.getJSONArray(TAG_TASKS);
                        for(int i = 0; i<tasks.length(); i++){
                            JSONObject c = tasks.getJSONObject(i);
                            String id = c.getString(TAG_ID);
                            String title = c.getString(TAG_TITLE);
                            String task = c.getString(TAG_TASK);

                            String complexity = c.getString(COMPLEXITY);
                            String created_at = c.getString(TAG_CREATED);
                            String updated_at = c.getString(TAG_UPDATED);

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put(TAG_ID, id);
                            map.put(TAG_TITLE, title);
                            map.put(TAG_TASK, task);
                            map.put(COMPLEXITY, complexity);
                            map.put(TAG_CREATED, created_at);
                            map.put(TAG_UPDATED, updated_at);
                            tasksList.add(map);



                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Check Internet Connection",Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    simpleAdapter = new SimpleAdapter(AllTasksActivity.this,tasksList,R.layout.row_tasks, new String[]{
                            TAG_ID,TAG_TITLE, COMPLEXITY, TAG_CREATED,TAG_UPDATED,TAG_TASK
                    }, new int[]{R.id.id, R.id.title,R.id.complexity,R.id.created_at,R.id.updated_at,R.id.task_row});

                    lv.setAdapter(simpleAdapter);
                    simpleAdapter.notifyDataSetChanged();


                }
            });

        }
    }

}
