package com.musicsheetwriter.musicsheetwriter.formvalidator;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageValidator {

    public static final String DIR_NAME = "MusicSheetWriter";

    public String transformAndStore(Context context, Uri imageUri, String filename) {
        // Get path of the picture
        String path;
        Cursor cursor = context.getContentResolver().query(imageUri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            path = imageUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if (cursor.isNull(idx)) {
                return imageUri.getLastPathSegment();
            }
            path = cursor.getString(idx);
            cursor.close();
        }

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap != null) {
            ExifInterface ei;
            try {
                ei = new ExifInterface(path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = rotateImage(bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = rotateImage(bitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = rotateImage(bitmap, 270);
                        break;
                }

                if (path.endsWith(".jpeg") || path.endsWith(".jpg")) {
                    return storeFileFromBitmap(bitmap, Bitmap.CompressFormat.JPEG, filename);
                } else if (path.endsWith(".png")) {
                    return storeFileFromBitmap(bitmap, Bitmap.CompressFormat.PNG, filename);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

    public String storeFileFromBitmap(Bitmap bitmap, Bitmap.CompressFormat cf, String filename)
            throws IOException {
        final File dir = new File(Environment.getExternalStorageDirectory() + File.separator + DIR_NAME + File.separator);
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        final File tempFile = new File(dir, filename);
        FileOutputStream fos = new FileOutputStream(tempFile);
        bitmap.compress(cf, 100, fos);
        fos.close();
        return Uri.fromFile(tempFile).getPath();
    }
}
