package com.example.administrator.musicapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/6/26.
 */
public class DefaultLrcBuilder {
    private static final String TAG ="DefaultBuilder" ;

    public List<LrcContent> getLrcRows(String content) {
        if (content == null || content.length() == 0) {
//            Log.e(TAG, "getLrcRows rawLrc null or empty");
            return null;
        }
        StringReader reader = new StringReader(content);
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        List<LrcContent> rows = new ArrayList<LrcContent>();
        try {
            do {
                line = br.readLine();
                if (line != null && line.length() > 0) {
                    List<LrcContent> lrcRows = LrcContent.createlist(line);
//                    Log.d(TAG,"run");
                    if (lrcRows != null && lrcRows.size() > 0) {
                        for (LrcContent lrcRow : lrcRows) {
                            rows.add(lrcRow);
                        }
                    }
                }
            } while (line != null);
            if( rows.size() > 0 ){
                // 根据歌词行的时间排序
                Collections.sort(rows);

            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader.close();
        }
        return rows;
        }
    }
