package com.utevents;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ColorTextAdapter extends ArrayAdapter<Integer> {

    private LayoutInflater mInflater;
    
    private ArrayList<Integer> mIds;
    
    private HashMap<Integer, Category> mCategories;
    
    private int mViewResourceId;
    
    public ColorTextAdapter(Context ctx, int viewResourceId, int textResourceId,
            ArrayList<Integer> ids, HashMap<Integer, Category> categories) {
        super(ctx, viewResourceId, textResourceId, ids);
        
        mInflater = (LayoutInflater)ctx.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        
        mIds = ids;
        mCategories = categories;
        
        mViewResourceId = viewResourceId;
    }

    @Override
    public int getCount() {
        return mIds.size();
    }

    @Override
    public Integer getItem(int position) {
        return mIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);
        
        Category category = mCategories.get(getItem(position));
        
        View iv = (View)convertView.findViewById(R.id.option_icon);
        iv.setBackgroundColor( category.getColor() );
        
        TextView tv = (TextView)convertView.findViewById(R.id.option_text);
        tv.setText( category.getTitle() );
        
        return convertView;
    }
}
