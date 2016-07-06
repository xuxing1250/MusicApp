package com.example.administrator.musicapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16.
 */
public class MediaUtil {


    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    public static List<Mp3info> getMp3Infos(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        List<Mp3info> mp3Infos = new ArrayList<Mp3info>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            Mp3info mp3Info = new Mp3info();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));	//音乐id
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE))); // 音乐标题
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST)); // 艺术家
            String album = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM));	//专辑
            String displayName = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION)); // 时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // 是否为音乐
            if (isMusic != 0) { // 只把音乐添加到集合当中
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setAlbum(album);
                mp3Info.setDisplayName(displayName);
                mp3Info.setAlbumId(albumId);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                mp3Infos.add(mp3Info);
            }
        }
        return mp3Infos;
    }

    /**
     * 往List集合中添加Map对象数据，每一个Map对象存放一首音乐的所有属性
     * @param mp3Infos
     * @return
     */
    public static List<HashMap<String, String>> getMusicMaps(
            List<Mp3info> mp3Infos) {
        List<HashMap<String, String>> mp3list = new ArrayList<HashMap<String, String>>();
        for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext();) {
            Mp3info mp3Info = (Mp3info) iterator.next();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("title", mp3Info.getTitle());
            map.put("Artist", mp3Info.getArtist());
            map.put("album", mp3Info.getAlbum());
            map.put("displayName", mp3Info.getDisplayName());
            map.put("albumId", String.valueOf(mp3Info.getAlbumId()));
            map.put("duration", formatTime(mp3Info.getDuration()));
            map.put("size", String.valueOf(mp3Info.getSize()));
            map.put("url", mp3Info.getUrl());
            mp3list.add(map);
        }
        return mp3list;
    }




    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    //从文件当中获取专辑封面


    public static Bitmap getArtworkFromFile(Context context, long id, long albumId) {
        Bitmap bm=null;
        if (id<0&&albumId<0){
            throw new IllegalAccessError("Must specify an album or a song id");
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        FileDescriptor fd=null;                     //文件描述符
        try {
            if (albumId<0){
                Uri uri=Uri.parse("content://media/external/audio/media/"
                        + id + "/albumart");
                ParcelFileDescriptor pfd=context.getContentResolver().openFileDescriptor(uri,"r");
                if (pfd!=null){
                    fd=pfd.getFileDescriptor();
                }
            }else{
                Uri uri= ContentUris.withAppendedId(albumArtUri, albumId);
                ParcelFileDescriptor pfd=context.getContentResolver().openFileDescriptor(uri,"r");
                if (pfd!=null){
                    fd=pfd.getFileDescriptor();
                }
            }
            options.inSampleSize=1;
            options.inJustDecodeBounds=true;    //只进行大小判断
            BitmapFactory.decodeFileDescriptor(fd,null,options);        //得到图片大小

            options.inSampleSize=100;
            options.inJustDecodeBounds=false;                           //?????????
            options.inDither=false;
            options.inPreferredConfig= Bitmap.Config.ARGB_8888;         //?????????

//        根据option参数 减少bitmap参数
            bm=BitmapFactory.decodeFileDescriptor(fd,null,options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    public static Bitmap getArtwork(Context context,long id,long albumId,boolean allowdefalut, boolean small) {
        if (albumId < 0) {
            Bitmap bm = null;
            if (id < 0) {
                bm = getArtworkFromFile(context, id, -1);
                if (bm != null) {
                }
                return bm;
            }
            if (allowdefalut) {
                return getDefaultArtword(context, small);
            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumArtUri, albumId);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();

//                指定原始大小
                options.inSampleSize = 1;
//                进行大小判断
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                if (small) {
                    options.inSampleSize = computeSmapleSize(options, 10);
                } else {
                    options.inSampleSize = computeSmapleSize(options, 600);
                }


                // 我们得到了缩放比例，现在开始正式读入Bitmap数据
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, options);

            } catch (FileNotFoundException e) {
                Bitmap bm = getArtworkFromFile(context, id, albumId);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefalut) {
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static int computeSmapleSize(BitmapFactory.Options options, int target) {
        int w = options.outWidth;
        int h = options.outHeight;
        int candidateW = w / target;
        int candidateH = h / target;
        int candidate = Math.max(candidateW, candidateH);
        if(candidate == 0) {
            return 1;
        }
        if(candidate > 1) {
            if((w > target) && (w / candidate) < target) {
                candidate -= 1;
            }
        }
        if(candidate > 1) {
            if((h > target) && (h / candidate) < target) {
                candidate -= 1;
            }
        }
        return candidate;
    }

    //默认
    private static Bitmap getDefaultArtword(Context context, boolean small) {
//        BitmapFactory.Options opts = new BitmapFactory.Options();
//        opts.inPreferredConfig = Bitmap.Config.RGB_565;
//        if(small){  //返回小图片
//            return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.music3), null, opts);
//        }
//        return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.defaultalbum), null, opts);
        return null;
    }


}
