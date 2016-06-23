package com.megliosolutions.ipd.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.megliosolutions.ipd.Objects.NodeObject;
import com.megliosolutions.ipd.R;

import java.util.List;

/**
 * Created by Meglio on 6/14/16.
 */
public class StaticListAdapter extends ArrayAdapter<NodeObject> {
    public static String TAG = StaticListAdapter.class.getSimpleName();
    public Context mContext;
    public List<NodeObject> mNodes;

    //Viewholder

    public class ViewHolder {
        TextView mStaticAddress;
        TextView mDescription;

    }

    @Override
    public int getCount() {
        return mNodes.size();
    }

    public StaticListAdapter(Context context, List<NodeObject> objects) {
        super(context, R.layout.activity_main, objects);
        this.mContext = context;
        this.mNodes = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        NodeObject node = mNodes.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.node_item, null);
            holder.mStaticAddress = (TextView) convertView.findViewById(R.id.node_item_IP);
            holder.mDescription = (TextView) convertView.findViewById(R.id.node_item_desc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mStaticAddress.setText(node.getStaticAddress());
        holder.mDescription.setText(node.getDescription());

        return convertView;
    }
}
