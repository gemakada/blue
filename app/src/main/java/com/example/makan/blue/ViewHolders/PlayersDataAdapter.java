package com.example.makan.blue.ViewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.example.makan.blue.interfaces.listviewListener;
import com.example.makan.blue.MainActivity;
import com.example.makan.blue.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PlayersDataAdapter  extends RecyclerView.Adapter<PlayersDataAdapter.PlayerViewHolder> {
    private List<Player> players;
    private static final String LOG_TAG = PlayersDataAdapter.class.getSimpleName();
    public listviewListener activityListener;

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        private TextView name, nationality, club, rating, age;
        private Button unlockbtn;
        SwitchCompat automatic;

        public PlayerViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            nationality = (TextView) view.findViewById(R.id.nationality);
            //club = (TextView) view.findViewById(R.id.club);
            rating = (TextView) view.findViewById(R.id.rating);
            unlockbtn = (Button) view.findViewById(R.id.unlock);
            automatic = (SwitchCompat) view.findViewById(R.id.switchAutomatic);

            //age = (TextView) view.findViewById(R.id.age);
        }
    }

    public PlayersDataAdapter(List<Player> players, Context con) {

        this.players = players;
        this.activityListener = ((listviewListener) con);
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.locker_layout, parent, false);

        return new PlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder holder, final int position) {
        Player player = players.get(position);
        holder.name.setText(player.getName());
        holder.nationality.setText(player.getNationality());
        //holder.club.setText(player.getClub());
        holder.rating.setText(player.getRating().toString());
        holder.unlockbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e(LOG_TAG, "Button Pressed: "+String.valueOf(position) );
                activityListener.onListViewClickButton(position);
               // mBluetoothLeService.disconnect();
                //  chatController.stop();
                // btnDisConnect.setEnabled(false);
            }
        });

        holder.automatic.setOnCheckedChangeListener(new  CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activityListener.onSwitchChange(true);
                    Log.e(LOG_TAG, "checked: " );
                } else {
                    activityListener.onSwitchChange(false);
                    Log.e(LOG_TAG, "not checked: " );
                }
            }
        });
        //holder.age.setText(player.getAge().toString());
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public void setRSSI(String rssi) {
        double _rssi = Double.parseDouble(rssi);
        Double truncatedDouble = BigDecimal.valueOf(_rssi)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
        int _irssi = (int)_rssi;
        for (int i=0; i<players.size(); i++) {
            players.get(i).setRating(truncatedDouble);
        }
    }
}
