package com.utevents;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ColorTextAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;
    
    private ArrayList<String> mStrings;
    
    private int mViewResourceId;
    
    public ColorTextAdapter(Context ctx, int viewResourceId, int textResourceId,
            ArrayList<String> strings) {
        super(ctx, viewResourceId, textResourceId, strings);
        
        mInflater = (LayoutInflater)ctx.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        
        mStrings = strings;
        
        mViewResourceId = viewResourceId;
    }

    @Override
    public int getCount() {
        return mStrings.size();
    }

    @Override
    public String getItem(int position) {
        return mStrings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);
        
        View iv = (View)convertView.findViewById(R.id.option_icon);
        iv.setBackgroundColor( 0xFF000000 | rand256() << 16 | rand256() << 8 | rand256() );
        
        TextView tv = (TextView)convertView.findViewById(R.id.option_text);
        tv.setText(getItem(position));
        
        return convertView;
    }
    
    public int rand256() {
    	return (int) (Math.random()*256);
    }
}
