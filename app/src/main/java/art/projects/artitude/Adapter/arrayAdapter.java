package art.projects.artitude.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;

import com.squareup.picasso.Picasso;

import java.util.List;

import art.projects.artitude.R;
import art.projects.artitude.Models.cards;

/**
 * Created by manel on 9/5/2017.
 */

public class arrayAdapter extends ArrayAdapter<cards>{

    Context context;

    public arrayAdapter(Context context, int resourceId, List<cards> items){
        super(context, resourceId, items);
    }
    public View getView(int position, View convertView, ViewGroup parent){
        cards card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }


/*
        SharedPreferences settings;
        settings = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        //set the sharedpref
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("postId", card_item.getProfileImageUrl());
        editor.apply();
*/
        //main.setUserPrefs(card_item.getProfileImageUrl(),context);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

            //name.setVisibility(View.GONE);


        name.setText(card_item.getName());
        Picasso.get().load(card_item.getProfileImageUrl()).into(image);

        return convertView;

    }
}
