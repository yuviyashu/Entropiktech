package com.example.lap.library.DataBase;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Lap on 4/2/2016.
 */
public class DataBaseCommunicator {

    public static void setDataSet(String dataSetStr){



        //Actually should write to DB but as of now we are writing to text file
        OutputStreamWriter outStreamWriter = null;
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Entropiktech");

            if (!root.exists()) {
                root.mkdir();
            }
            File gpxfile = new File(root, "TouchTrack.txt");
            if(!gpxfile.exists()) {

                gpxfile.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(gpxfile, true);
            outStreamWriter = new OutputStreamWriter(outStream);
            outStreamWriter.append(dataSetStr+"\n\n");
            outStreamWriter.flush();
            outStreamWriter.close();
            Log.e("done","Written");
            //Toast.makeText(, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
           Log.e("DBC",e.toString());
        }


    }



}
