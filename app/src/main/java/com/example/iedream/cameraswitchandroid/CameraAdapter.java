package com.example.iedream.cameraswitchandroid;
import android.widget.Adapter;
import android.widget.TextView;
import android.content.Context;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.nestlabs.sdk.Camera;

public class CameraAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<CameraModel> mDataSource;

    public CameraAdapter(Context context, ArrayList<CameraModel> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.camera_list, parent, false);
        TextView cameraNameView = (TextView) rowView.findViewById(R.id.camera_name);
        Camera camera = (Camera) getItem(position);
        cameraNameView.setText(camera.getName());
        return rowView;
    }
}