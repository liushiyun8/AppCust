package foundation.memory;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;



/**
 * Created by wfpb on 15/7/9.
 * Bitmap LruCache Utils
 */
public class LruCacheUtils {

    //清空缓存
    public static void clear() {
        LruCacheUtils.getInstance()._clear();
    }

    //添加缓存
    public static void put(String key, Bitmap bitmap) {
        LruCacheUtils.getInstance()._put(key, bitmap);
    }

    //获取缓存
    public static Bitmap get(String key) {
        return LruCacheUtils.getInstance()._get(key);
    }

    //移除缓存
    public static void remove(String key) {
        LruCacheUtils.getInstance()._remove(key);
    }

    private static LruCacheUtils _instance;

    protected static LruCacheUtils getInstance() {
        if (_instance == null) {
            synchronized (LruCacheUtils.class) {
                if (_instance == null) {
                    _instance = new LruCacheUtils();
                }
            }
        }
        return _instance;
    }

    private LruCache<String, Bitmap> mMemoryCache;

    private LruCacheUtils() {

        if (mMemoryCache == null) {

            int MAX_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024);

            mMemoryCache = new LruCache<String, Bitmap>(
                    MAX_MEMORY / 8) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key,
                                            Bitmap oldValue, Bitmap newValue) {

                }
            };
        }
    }

    //清空缓存
    protected synchronized void _clear() {
        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {

                mMemoryCache.evictAll();

            }
            mMemoryCache = null;
        }
    }

    //添加缓存
    protected synchronized void _put(String key, Bitmap bitmap) {
        if (mMemoryCache.get(key) == null) {
            if (key != null && bitmap != null)
                mMemoryCache.put(key, bitmap);
        }
    }

    //获取缓存
    protected synchronized Bitmap _get(String key) {
        Bitmap bm = mMemoryCache.get(key);
        if (key != null) {
            return bm;
        }
        return null;
    }

    //移除缓存
    protected synchronized void _remove(String key) {
        if (key != null) {
            if (mMemoryCache != null) {
                Bitmap bm = mMemoryCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }
}
