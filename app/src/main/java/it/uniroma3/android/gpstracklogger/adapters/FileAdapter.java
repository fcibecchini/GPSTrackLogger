/*******************************************************************************
 * This file is part of GPSTrackLogger.
 * Copyright (C) 2015  Fabio Cibecchini
 *
 * GPSTrackLogger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GPSTrackLogger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSTrackLogger.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package it.uniroma3.android.gpstracklogger.adapters;

import android.app.Activity;
import android.content.Context;
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
    private List<String> files;
    private static LayoutInflater inflater = null;

    public FileAdapter(Activity activity, int textViewResourceId,  List<String> files) {
        super(activity, textViewResourceId, files);
        try {
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

