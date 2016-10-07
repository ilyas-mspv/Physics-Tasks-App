package imspv.lycee.physics.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import imspv.lycee.physics.R;
import imspv.lycee.physics.DTO.SettingDTO;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {

    private List<SettingDTO> settingList;

    public SettingsAdapter(List<SettingDTO> settingList) {
        this.settingList = settingList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings,parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SettingsAdapter.MyViewHolder holder, int position) {

        SettingDTO setting = settingList.get(position);
        holder.title.setText(setting.getTitle());
        holder.subtitle.setText(setting.getSubtitle());

    }

    @Override
    public int getItemCount() {
        return settingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,subtitle ;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
        }
    }
}
