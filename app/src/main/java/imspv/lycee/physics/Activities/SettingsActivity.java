package imspv.lycee.physics.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import imspv.lycee.physics.Adapters.SettingsAdapter;
import imspv.lycee.physics.DTO.SettingDTO;
import imspv.lycee.physics.R;
import imspv.lycee.physics.helper.RecyclerItemClickListener;

public class SettingsActivity extends AppCompatActivity {

    private List<SettingDTO> settingList = new ArrayList<>();

    private RecyclerView recyclerView;
    private SettingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initToolbar();
        initRecyclerView();

        //TODO SettingActivity without recyclerview
    }

    private void initRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.settings_container);
        adapter = new SettingsAdapter(settingList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch(position){
                    case 0:
                        startActivity(new Intent(getApplicationContext(), CreateTask.class));
                        break;
                    case 1:
                        //TODO alert with themes
                        Toast.makeText(getApplicationContext(),"еще не готово", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }));

        SettingsData();
    }

    private void SettingsData(){
        SettingDTO setting = new SettingDTO(getString(R.string.title_activity_create_task),getString(R.string.create_desc));
        settingList.add(setting);

        setting = new SettingDTO(getString(R.string.change_theme), getString(R.string.change_desc));
        settingList.add(setting);

        adapter.notifyDataSetChanged();
    }


    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



}
