package imspv.lycee.physics.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import imspv.lycee.physics.R;

public class TaskDetailsActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
//TODO get data from sharedPrefs
    String id,
        Tasktitle,
        Task,
        compexity,
        created_at,
        updated_at;

    TextView title,taskss,complexity;


    SharedPreferences sharedPref;
    public static String MY_PREF = "TaskData";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TASK = "task";
    private static final String TAG_CREATED = "created_at";
    private static final String TAG_UPDATED = "updated_at";
    private static final String COMPLEXITY = "complexity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        init();
        getIntentsFromAllTasks();
        iniToolbar();

        sharedPref = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        sharedPref.getString("title",TAG_TITLE);
        Task = sharedPref.getString("task",TAG_TASK);

    }

    private void getIntentsFromAllTasks() {
        Intent i = getIntent();
        id = i.getStringExtra(TAG_ID);
        id = getIntent().getExtras().getString(TAG_ID);
        Tasktitle = getIntent().getExtras().getString(TAG_TITLE);
//        Task = getIntent().getExtras().getString(TAG_TASK);
        compexity = i.getStringExtra(COMPLEXITY);
        created_at = i.getStringExtra(TAG_CREATED);
        updated_at = i.getStringExtra(TAG_UPDATED);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_details_task_menu,menu);
        return true;

    }

    private void init() {
        title = (TextView) findViewById(R.id.title);
        taskss = (TextView) findViewById(R.id.task);
        complexity = (TextView) findViewById(R.id.complexity);
        new GetTaskDetails().execute();
    }

    class GetTaskDetails extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TaskDetailsActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
           try {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       title.setText(Tasktitle);
                       taskss.setText(Task);
                   }
               });
           }catch (Exception e){
               Log.d("Errrrrroorrr",e.toString());
           }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

        }
    }

    private void iniToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.task_id_number) + " №" + id);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.details :
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskDetailsActivity.this);
                builder.setTitle(getString(R.string.info))
                        .setMessage(getString(R.string.task_complexity) + ": " + compexity + "\n" +
                                getString(R.string.created_at) + ": " + created_at + "\n"+
                                getString(R.string.updated_at) + ": " + updated_at )
                        .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }



    }
}
