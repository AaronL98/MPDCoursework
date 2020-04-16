package alawso205.caledonian.ac.uk.mpdcoursework.Helper;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import alawso205.caledonian.ac.uk.mpdcoursework.Model.RSSItem;
import alawso205.caledonian.ac.uk.mpdcoursework.R;

/**
 * Aaron Lawson (S1624910)
 */

public class RSSItemAdapter extends ArrayAdapter<RSSItem> {

    Date filterDate = new Date();
    ArrayList<RSSItem> items = new ArrayList<RSSItem>();
    public RSSItemAdapter(Context context, ArrayList<RSSItem> items){
        super(context,0,items);

        this.items = items;

    }

    @NonNull
    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        RSSItem item = getItem(pos);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        TextView itemTitle = convertView.findViewById(R.id.itemTitle);
        TextView itemDescription = convertView.findViewById(R.id.itemDescription);
        TextView itemStartDate = convertView.findViewById(R.id.itemStartDate);
        TextView itemEndDate = convertView.findViewById(R.id.itemEndDate);

        itemTitle.setText(item.getTitle());
        itemDescription.setText(item.getDescription());

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        itemStartDate.setText("Start Date: " + format.format(item.getStartDate()));
        itemEndDate.setText("End Date: " + format.format(item.getEndDate()));


        return convertView;
    }

    public void refresh(ArrayList<RSSItem> items)
    {
        this.items = items;
        notifyDataSetChanged();
    }
}
