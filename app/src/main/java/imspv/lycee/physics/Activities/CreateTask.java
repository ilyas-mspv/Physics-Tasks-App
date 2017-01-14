package imspv.lycee.physics.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import imspv.lycee.physics.DTO.FilterData;
import imspv.lycee.physics.DTO.Subtopic;
import imspv.lycee.physics.R;
import imspv.lycee.physics.helper.JSONParser;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class CreateTask extends AppCompatActivity {

    private static final String TAG_SUCCESS = "success";
    private static final int STORAGE_PERMISSION_CODE = 123;
    private static String url_create_task ="http://physics.atlascience.ru/create_task.php";
    private static String url_filter = "http://physics.atlascience.ru/filter.php";
    //Server Connection
    ProgressDialog pDialog;
    //TODO add a photo uploading
    //editText
    JSONParser jParser = new JSONParser();
    EditText inputTitle;
    EditText inputTask;
    int unique_id;
    String title, task, complexity,classes;
    Spinner complexity_spinner,topics_spinner, subtopics_spinner,class_spinner;
    FilterData topicsData;
    //spinners
    private ArrayList<FilterData> filterDatas;
    private ArrayList<Subtopic> subtopics;
    //PHOTOS
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        inits();
        requestStoragePermission();

    }


    private void inits() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Additional button "+"
        ImageView imageView = (ImageView) findViewById(R.id.create_other);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopUp(v);
            }
        });

        //Input Fields
        inputTitle = (EditText) findViewById(R.id.task_title_edit_text);
        inputTask = (EditText) findViewById(R.id.task_edit_text);

        //Spinners
        complexity_spinner = (Spinner) findViewById(R.id.complexity_spinner_create);
        class_spinner = (Spinner) findViewById(R.id.class_spinner_create);
        topics_spinner = (Spinner) findViewById(R.id.topics_spinner_create);
        subtopics_spinner = (Spinner) findViewById(R.id.subtopics_spinner_create);

        //Start background work
        new LoadSpinners().execute();

        Button createBTN = (Button) findViewById(R.id.create_task_btn);
        createBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateNewTask().execute();
            }
        });
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    // IMAGE FUNCTIONS
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //TODO Make translating String
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private void getThumbnailPicture() {


    }

    //popup
    private void ShowPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popup_attach_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.attachment:
                        getThumbnailPicture();
                        return true;
                    case R.id.choose:
                        showFileChooser();
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Choosing image from Gallery
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //spinners
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
                            getUniqueId(class_pos,topic_position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );
        hintSpinnerSubtopic.init();
    }
    private void getUniqueId(int class_pos, int top_pos) throws JSONException {
        for (int i = 0; i < topicsData.getSubtopicSize(class_pos,top_pos); i++){
          unique_id =  topicsData.getUniqueID(class_pos,top_pos,i);
        }
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

            title = inputTitle.getText().toString();
            task = inputTask.getText().toString();
            complexity = complexity_spinner.getSelectedItem().toString();
            classes = class_spinner.getSelectedItem().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("title",title));
            params.add(new BasicNameValuePair("task",task));
            params.add(new BasicNameValuePair("complexity",complexity));
            params.add(new BasicNameValuePair("class_id",classes));

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
    class LoadSpinners extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> paramss = new ArrayList<NameValuePair>();
            JSONObject json = jParser.makeHttpRequest(url_filter,"GET",paramss);
            topicsData = new FilterData(CreateTask.this, json.toString());

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateTask.this);
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
