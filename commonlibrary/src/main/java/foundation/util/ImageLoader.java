package foundation.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by wujian on 2015/9/9.
 */
public class ImageLoader {


    private static ImageLoader mInstanse;

    /**
     * 图片缓存核心类
     */
    private LruCache<String, Bitmap> mLruCache;

    /**
     * 线程池
     */
    private ExecutorService mThreadPool;

    //默认线程数
    private static final int DEAFFULT_THREAD_COUNT = 1;

    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;


    private Semaphore  mSemaphorePoolThreadHandler=new Semaphore(0);
    public enum Type {
        LIFI, LIFO
    }

    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;

    private Semaphore  mSemaphoreThreadPool;

    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;

    /**
     * UI线程的Handler
     */
    private Handler mUIHandler;

    private ImageLoader(int threadCount, Type type) {

        //初始化
        init(threadCount, type);
    }

    /**
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {
        //轮询线程
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //线程池取出一个任务执行
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                };
                mSemaphorePoolThreadHandler.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        //获取应用可用的最大内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();

        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;

        mSemaphoreThreadPool=new Semaphore(threadCount);
    }

    //单例
    public static ImageLoader getInstanse() {
        if (mInstanse == null) {
            synchronized (ImageLoader.class) {
                mInstanse = new ImageLoader(DEAFFULT_THREAD_COUNT, Type.LIFO);
            }
        }

        return mInstanse;
    }
    public static ImageLoader getInstanse(int threadCount,Type  mType) {
        if (mInstanse == null) {
            synchronized (ImageLoader.class) {
                mInstanse = new ImageLoader(threadCount, mType);
            }
        }

        return mInstanse;
    }

    /**
     * 加载
     *
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView) {
        imageView.setTag(path);
        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //获得图片为Imageview设置
                    ImageBeanHolder holder = (ImageBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;


                    ImageView imageview = holder.imageView;
                    String path = holder.path;
                    //比较两个路径是否一致
                    if (imageview.getTag().toString().equals(path)) {
                        imageview.setImageBitmap(bm);
                    }
                }
            };
        }
        Bitmap bitmap = getBitmapFromLruCache(path);

        if (bitmap != null) {

            refreashBitmap(bitmap, path, imageView);

        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    // 加载图片  图片压缩
                    //1 获取图片需要显示的大小
                    ImageSize imageSize = getImageViewSize(imageView);
                    //压缩图片
                    Bitmap bm = decodeSampledBitmapFromPath(path, imageSize.width, imageSize.height);
                    bm = BitmapUtils.getScaledCompressBitmap(path, imageSize.width,
                            imageSize.height, 1024 * 100);
                    int Degree = BitmapUtils.getOrientation(path);
                    if (Degree != 0) {
                        bm = BitmapUtils.rotateBitmap(bm, Degree);
                    }
                    //把图片加入到缓存
                    addBitmapToLruCache(path, bm);
                    refreashBitmap(bm, path, imageView);
                    mSemaphoreThreadPool.release();

                }
            });
        }


    }

    /**
     * 通过反射获取属性值
     */

    private static int getImageViewFieldValue(Object object,String fieldName){
        int value=0;
        try {
            Field  field=ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue=field.getInt(object);
            if(fieldValue>0&&fieldValue<Integer.MAX_VALUE){
                value= fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return   value;
    }

    private void refreashBitmap(Bitmap bm, String path, ImageView imageView) {
        Message message = Message.obtain();
        ImageBeanHolder holder = new ImageBeanHolder();
        holder.bitmap = bm;
        holder.path = path;
        holder.imageView = imageView;
        message.obj = holder;
        mUIHandler.sendMessage(message);
    }

    /**
     * 将图片加入缓存
     * @param path
     * @param bm
     */
    private void addBitmapToLruCache(String path, Bitmap bm) {
        if(getBitmapFromLruCache(path)==null){
            if(bm!=null){
                mLruCache.put(path,bm);
            }
        }
    }

    //根据图片需要显示的宽和高对图片进行压缩
    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, width, height);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = decodeFile(path,options);
        return bitmap;
    }
    //TODO:考虑大图
    public static Bitmap decodeFile(String pathName) {
        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
        return bitmap;
    }
    public static Bitmap decodeFile(String pathName, BitmapFactory.Options opts) {
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
        return bitmap;
    }
    // 计算图片的缩放值
    private static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
    /**
     * 根据imageView 获取适当的压缩的宽和高
     *
     * @param imageView
     * @return
     */
    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        final DisplayMetrics displayMetrics = imageView.getContext()
                .getResources().getDisplayMetrics();
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();

        int width = params.width == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getWidth(); // Get actual image width
        if (width <= 0)
            width = params.width; // Get layout width parameter
        if (width <= 0)
            width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
        // maxWidth
        // parameter
        if (width <= 0)
            width = displayMetrics.widthPixels;
        int height = params.height == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getHeight(); // Get actual image height
        if (height <= 0)
            height = params.height; // Get layout height parameter
        if (height <= 0)
            height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
        // maxHeight
        // parameter
        if (height <= 0)
            height = displayMetrics.heightPixels;
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    private Runnable getTask() {
        if (mType == Type.LIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFI) {

            return mTaskQueue.removeLast();

        }
        return null;

    }

    //添加任务
    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        try {
            if(mPoolThreadHandler==null)
            mSemaphorePoolThreadHandler.acquire();
        }catch (InterruptedException e){

        }
        mPoolThreadHandler.sendEmptyMessage(0x110);

    }

    //  从缓存中获取Bitmap
    private Bitmap getBitmapFromLruCache(String key) {

        return mLruCache.get(key);
    }


    private class ImageSize {
        int width;
        int height;

    }

    private class ImageBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }

}
