package art.projects.artitude.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.squareup.picasso.Picasso;
import java.util.List;
import art.projects.artitude.R;
import art.projects.artitude.Models.cards;


public class CardAdapter extends ArrayAdapter<cards>{
    Context context;
    public CardAdapter(Context context, int resourceId, List<cards> items){
        super(context, resourceId, items);
    }
    public View getView(int position, View convertView, ViewGroup parent){
        context = getContext();
        cards card_item = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.card_item, parent, false);
        }
        TextView name = convertView.findViewById(R.id.desc);
        ImageView image = convertView.findViewById(R.id.image);

        if(card_item.getName().equals("")){
            name.setVisibility(View.GONE);
        }
        name.setText(card_item.getName());
        Picasso.get().load(card_item.getProfileImageUrl()).into(image);


        return convertView;
    }
}
