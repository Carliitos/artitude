package art.projects.artitude;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

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



        name.setText(card_item.getName());
        Picasso.get().load(card_item.getProfileImageUrl()).into(image);

        return convertView;

    }
}
