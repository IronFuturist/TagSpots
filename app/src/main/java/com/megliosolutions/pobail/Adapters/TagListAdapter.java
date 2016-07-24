package com.megliosolutions.pobail.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.megliosolutions.pobail.Objects.TagObject;
import com.megliosolutions.pobail.R;

import java.util.List;

/**
 * Created by Meglio on 6/14/16.
 */
public class TagListAdapter extends ArrayAdapter<TagObject> {
    public TagListAdapter(Context context, int resource, TagObject[] objects) {
        super(context, resource, objects);
    }
    public static String TAG = TagListAdapter.class.getSimpleName();
    public Context mContext;
    public List<TagObject> mTagsList;

    //Viewholder

    public class ViewHolder {
        TextView mTagTitle;
        TextView mCreated;

    }

    @Override
    public int getPosition(TagObject item) {
        return super.getPosition(item);
    }

    @Override
    public int getCount() {
        return mTagsList.size();
    }

    public TagListAdapter(Context context, List<TagObject> objects) {
        super(context, R.layout.activity_main, objects);
        this.mContext = context;
        this.mTagsList = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        TagObject mTag = mTagsList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.tag_list_item, null);
            holder.mTagTitle = (TextView) convertView.findViewById(R.id.tag_item_title);
            holder.mCreated = (TextView) convertView.findViewById(R.id.tag_item_created);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTagTitle.setText(mTag.getTag_title());
        holder.mCreated.setText(mTag.getCreated());

        return convertView;
    }
}
