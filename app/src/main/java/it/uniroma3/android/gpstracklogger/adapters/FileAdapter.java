package it.uniroma3.android.gpstracklogger.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.uniroma3.android.gpstracklogger.R;

/**
 * Created by Fabio on 07/06/2015.
 */
public class FileAdapter extends ArrayAdapter<String> {
    private Activity activity;
    private List<String> files;
    private static LayoutInflater inflater = null;

    public FileAdapter(Activity activity, int textViewResourceId,  List<String> files) {
        super(activity, textViewResourceId, files);
        try {
            this.activity = activity;
            this.files = files;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {

        }
    }

    public int getCount() {
        return files.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView fileName;
        public ImageView icon;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.fileview, null);
                holder = new ViewHolder();

                holder.fileName = (TextView) vi.findViewById(R.id.filename);
                holder.icon = (ImageView) vi.findViewById(R.id.imageicon);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
            String file = files.get(position);
            holder.fileName.setText(file);
            if (file.contains(".gpx"))
                holder.icon.setImageResource(R.drawable.ic_add_circle_outline_black_48dp);
            else
                holder.icon.setImageResource(R.drawable.ic_folder_open_black_48dp);

        } catch (Exception e) {

        }
        return vi;
    }

}

