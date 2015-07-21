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

package it.uniroma3.android.gpstracklogger.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import it.uniroma3.android.gpstracklogger.R;
import it.uniroma3.android.gpstracklogger.adapters.FileAdapter;
import it.uniroma3.android.gpstracklogger.application.AppSettings;
import it.uniroma3.android.gpstracklogger.application.Session;

public class LoadFileFragment extends Fragment {
    private String appDirectory;
    private String publicDir;
    private String FTYPE;
    private File mPath;
    private String chosenFile;
    private List<String> list;
    ListView listView;
    LinearLayout llayout;
    private FileAdapter adapter;

    public LoadFileFragment() {
        appDirectory = AppSettings.getDirectory();
        FTYPE = ".gpx";
        publicDir = Environment.getExternalStoragePublicDirectory("/").getAbsolutePath();
        mPath = new File(appDirectory);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llayout = (LinearLayout) inflater.inflate(R.layout.fragment_loadfile, container, false);
        listView = (ListView) llayout.findViewById(R.id.list);
        setAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                chosenFile = (String) listView.getItemAtPosition(position);

                if (chosenFile.contains(FTYPE)) {
                    Session.getController().loadTrack(mPath.getAbsolutePath()+"/"+chosenFile);
                    ViewPropertyAnimator viewPropertyAnimator = view.animate().setDuration(500).alpha(0);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        viewPropertyAnimator.setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                list.remove(chosenFile);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
                    } else {
                        viewPropertyAnimator.withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(chosenFile);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
                    }
                }

                else {
                    if (chosenFile.equals("Public Directory")) {
                        mPath = new File(publicDir);
                    } else if (chosenFile.equals("App Directory")) {
                        mPath = new File(appDirectory);
                    } else if (chosenFile.equals("..")) {
                        mPath = mPath.getParentFile();
                    } else {
                        mPath = new File(mPath.getPath() + "/" + chosenFile);
                    }
                    setAdapter();
                }
            }
        });

        return llayout;
    }

    private void setAdapter() {
        loadFileList();
        TextView path = (TextView) llayout.findViewById(R.id.dirpath);
        path.setText(mPath.getAbsolutePath());
        adapter = new FileAdapter(getActivity(),0, list);
        listView.setAdapter(adapter);
    }

    private void loadFileList() {
        try {
            mPath.mkdirs();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        list = new ArrayList<>();
        if (mPath.getAbsolutePath().equals(appDirectory)) {
            list.add("Public Directory");
        }
        else if (mPath.getAbsolutePath().equals(publicDir)) {
            list.add("App Directory");
        }
        else {
            list.add("..");
        }
        if(mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return filename.contains(FTYPE) || sel.isDirectory();
                }

            };
            String[] mFileList = mPath.list(filter);
            if (mFileList != null) {
                for (String file : mFileList) {
                    if (!file.contains(FTYPE) || Session.getController().getImportedTrack(file) == null) {
                        list.add(file);
                    }
                }
            }
            else {
                Toast.makeText(getActivity(), "No files to display...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}