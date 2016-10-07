package imspv.lycee.physics.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import imspv.lycee.physics.R;
import imspv.lycee.physics.helper.JSONParser;

public class CreateTask extends AppCompatActivity {

    ProgressDialog dialog;

    //TODO add other type of data, like a photo uploading, add creating topic/subtopic and complexity.
    JSONParser jParser = new JSONParser();
    EditText inputTitle;
    EditText inputTask;
    Spinner inputComplexity;

    private static String url_create_task ="http://physics.atlascience.ru/create_task.php";

    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        inits();

    }

    private void inits() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputTitle = (EditText) findViewById(R.id.task_title_edit_text);
        inputTask = (EditText) findViewById(R.id.task_edit_text);
         inputComplexity = (Spinner) findViewById(R.id.spinner);



        Button createBTN = (Button) findViewById(R.id.create_task_btn);



        createBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new CreateNewTask().execute();
            }
        });



    }

    class CreateNewTask extends AsyncTask<String,String,String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CreateTask.this);
            dialog.setMessage(getString(R.string.creating));
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
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
            dialog.dismiss();
        }
    }
}
