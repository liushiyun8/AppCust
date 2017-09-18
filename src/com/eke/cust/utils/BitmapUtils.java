package com.eke.cust.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import com.eke.cust.AppContext;
import com.eke.cust.BuildConfig;
import com.eke.cust.Constants;
import com.eke.cust.tabmore.camera_activity.MediaScanner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
    private static final String TAG = "BitmapUtils";
    private Activity mActivity = null;
    private Context mContext = null;

    public BitmapUtils(Context context) {
        mContext = context;
        mActivity = (Activity) context;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        String imgPath = null;
        if (BuildConfig.DEBUG)
            Log.d(TAG, "uri = " + uri);
        if (BuildConfig.DEBUG)
            Log.d(TAG, "path = " + getPath(uri));
        imgPath = getPath(uri);
        bitmap = decodeFileAsBitmap(imgPath);
        return bitmap;
    }

    public static Bitmap decodeFileAsBitmap(String imgPath) {

        return decodeFileAsBitmap(imgPath, Constants.SCREEN_HEIGHT
                * Constants.SCREEN_WIDTH);
    }

    public static Bitmap decodeFileAsBitmap(String imgPath, int maxNumOfPixels) {
        Log.d(TAG, "imgPath = " + imgPath);
        Bitmap bitmap;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, opts);
        if (BuildConfig.DEBUG)
            Log.d(TAG, "width = " + opts.outWidth + ", height = "
                    + opts.outHeight);
        opts.inSampleSize = computeSampleSize(opts, -1, maxNumOfPixels);
        if (BuildConfig.DEBUG)
            Log.d(TAG, "inSampleSize = " + opts.inSampleSize);
        opts.inJustDecodeBounds = false;

        try {
            bitmap = BitmapFactory.decodeFile(imgPath, opts);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

        // try {
        // bitmap =
        // BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // return null;
        // }
        return bitmap;
    }

    /**
     * change uri to path string
     *
     * @param uri
     * @return
     */

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mActivity.managedQuery(uri, projection, null, null,
                null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    public static int computeInitialSampleSize(BitmapFactory.Options options,
                                               int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public Bitmap compressBmpToFile(Bitmap bmp, File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 1024 * 1.5) {
            baos.reset();
            options -= 2;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            byte[] data = baos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();

            new MediaScanner(mContext).updateGallery(file.toString());

            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap compressBmpToFile(Bitmap bmp, File file, int max_k,
                                    boolean isShow) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > max_k) {
            baos.reset();
            options -= 2;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            byte[] data = baos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();

            if (isShow) {
                new MediaScanner(mContext).updateGallery(file.toString());
            }

            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    public static Bitmap stringtoBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static byte[] getBitmapByte(String imgPath) {
        Bitmap bmp = decodeFileAsBitmap(imgPath,
                ScreenUtils.getScreenHeight(AppContext.getInstance())
                        * ScreenUtils.getScreenWidth(AppContext.getInstance()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        /*
         * while (baos.toByteArray().length / 1024 > 65) { baos.reset(); options
		 * -= 2; bmp.compress(Bitmap.CompressFormat.JPEG, options, baos); }
		 */

        // byte[] data = baos.toByteArray();
        return baos.toByteArray();
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /****
     * 通过路径获得图片，使用这个方法不用担心OOM
     *
     * @return
     */
    public static Bitmap sampleBitmap(String filePath) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    // 计算图片的缩放值
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap createBitmap(Bitmap src) {
        Bitmap bitmap = Bitmap.createBitmap(src);

        return bitmap;
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width,
                                      int height) {
        Bitmap bitmap = Bitmap.createBitmap(source, x, y, width, height);

        return bitmap;
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width,
                                      int height, Matrix m, boolean filter) {
        Bitmap bitmap = Bitmap.createBitmap(source, x, y, width, height, m,
                filter);
        return bitmap;
    }

    public static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);

        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap createBitmap(DisplayMetrics display, int width,
                                      int height, Bitmap.Config config) {
        Bitmap bitmap = Bitmap.createBitmap(display, width, height, config);

        return bitmap;
    }

    public static Bitmap createBitmap(int colors[], int offset, int stride,
                                      int width, int height, Bitmap.Config config) {
        Bitmap bitmap = Bitmap.createBitmap(colors, offset, stride, width,
                height, config);

        return bitmap;
    }

    public static Bitmap createBitmap(DisplayMetrics display, int colors[],
                                      int offset, int stride, int width, int height, Bitmap.Config config) {
        Bitmap bitmap = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bitmap = Bitmap.createBitmap(display, colors, offset, stride,
                    width, height, config);
        }

        return bitmap;
    }

    public static Bitmap createBitmap(int colors[], int width, int height,
                                      Bitmap.Config config) {
        Bitmap bitmap = Bitmap.createBitmap(colors, width, height, config);

        return bitmap;
    }

    public static Bitmap createBitmap(DisplayMetrics display, int colors[],
                                      int width, int height, Bitmap.Config config) {
        Bitmap bitmap = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bitmap = Bitmap.createBitmap(display, colors, width, height,
                    config);
        }

        return bitmap;
    }

    public static void saveIcon(Bitmap bitmap, Context context) {
        try {
            File path = new File(FileUtils.getTakePictureStorageDirectory());
            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(path, "app_icon.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut);//压缩图片
            fOut.flush();
            fOut.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static String IconPath() {
        return FileUtils.getTakePictureStorageDirectory() + "app_icon.png";

    }
}