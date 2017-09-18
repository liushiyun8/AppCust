package foundation.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @className: GlideImageLoader
 * @classDescription: 采用Glide图片加载 图片处理 图片缓存
 */
public class GlideImageLoader {

    //单例
    private static GlideImageLoader mImageLoader = null;

    public GlideImageLoader() {
    }

    /**
     * 获取单例
     *
     * @param
     * @return ImageLoader
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public static GlideImageLoader getInstace() {
        if (mImageLoader == null) {
            mImageLoader = new GlideImageLoader();
        }
        return mImageLoader;
    }

    /**
     * 调用加载图片方法 -- 基本图片加载功能
     *
     * @param context,imageView,imgUrl
     * @return void
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public void loadImg(Context context, ImageView imageView, String imgUrl) {
        load(context, imageView, imgUrl, 0, null, false, false, false, 0);
    }

    /**
     * 调用加载图片方法 -- 基本图片加载功能
     *
     * @param context,imageView,imgUrl, defaultImage
     * @return void
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public void loadImg(Context context, ImageView imageView, String imgUrl, int defaultImage) {
        load(context, imageView, imgUrl, defaultImage, null, false, false, false, 0);
    }

    /**
     * 调用加载图片方法 -- 高斯模糊
     *
     * @param context,imageView,imgUrl,defaultImage, transform_valve
     * @return void
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public void loadBlurTransImg(Context context, ImageView imageView, String imgUrl, int defaultImage, int transform_valve) {
        Glide.with(context)
                .load(imgUrl)
                .error(defaultImage)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .thumbnail(Contants.THUMB_SIZE)
                .crossFade(1000)
                .bitmapTransform(new BlurTransformation(context, transform_valve, 4))  // “23”：设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”。
                .into(imageView);
    }

    /**
     * 调用加载图片方法 -- 是否切成圆形
     *
     * @param context,imageView,imgUrl
     * @return void
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public void loadCircleImg(Context context, ImageView imageView, String imgUrl, int defaultImage) {
        load(context, imageView, imgUrl, defaultImage, null, true, false, false, 0);
    }

    /**
     * 调用加载图片方法 -- 是否切成圆角，需提供圆角半径
     *
     * @param context,imageView,imgUrl
     * @return void
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public void loadRoundedCornersImg(Context context, ImageView imageView, String imgUrl, int radius, int defaultImage) {
        load(context, imageView, imgUrl, defaultImage, null, false, true, false, radius);
    }

    /**
     * 调用加载图片方法 -- 是否加载为灰度图片
     *
     * @param context,imageView,imgUrl
     * @return void
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public void loadGrayscaleImg(Context context, ImageView imageView, String imgUrl, int defaultImage) {
        load(context, imageView, imgUrl, defaultImage, null, false, false, true, 0);
    }

    /**
     * 常量
     */
    static class Contants {
        public static final float THUMB_SIZE = 0.5f; //0-1之间  10%原图的大小
    }

    /**
     * 加载缩略图
     *
     * @param context
     * @param imageView 图片容器
     * @param imgUrl    图片地址
     */
    public void loadThumbnailImage(Context context, ImageView imageView, String imgUrl, int defaultImage) {
        Glide.with(context)
                .load(imgUrl)
                .error(defaultImage)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .thumbnail(Contants.THUMB_SIZE)
                .into(imageView);
    }

    /**
     * 加载圆角
     *
     * @param context
     * @param imageView 图片容器
     * @param bitmap    base64图片地址
     */
    public void loadCilcleBitmap(Context context, ImageView imageView, String bitmap, int defaultImage) {
        Glide.with(context).load(Base64.decode(bitmap, Base64.DEFAULT))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(defaultImage)
                .bitmapTransform(new CropCircleTransformation(context)).into(imageView)  ;

    }

    /**
     * 加载图片并设置为指定大小
     *
     * @param context
     * @param imageView
     * @param imgUrl
     * @param withSize
     * @param heightSize
     */
    public void loadOverrideImage(Context context, ImageView imageView,
                                  String imgUrl, int withSize, int heightSize, int defaultImage) {
        Glide.with(context)
                .load(imgUrl)
                .error(defaultImage)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .override(withSize, heightSize)
                .into(imageView);
    }

    /**
     * 同步加载图片
     *
     * @param context
     * @param imgUrl
     * @param target
     */
    public void loadBitmapSync(Context context, String imgUrl, SimpleTarget<Bitmap> target) {
        Glide.with(context)
                .load(imgUrl)
                .asBitmap()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .into(target);
    }

    /**
     * 加载gif的缩略图
     *
     * @param context
     * @param imageView
     * @param imgUrl
     */
    public void loadGifThumbnailImage(Context context, ImageView imageView, String imgUrl, int defaultImage) {
        Glide.with(context)
                .load(imgUrl)
                .asGif()
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .error(defaultImage)
                .thumbnail(Contants.THUMB_SIZE)
                .into(imageView);
    }

    /**
     * 加载gif的缩略图
     *
     * @param context
     * @param imageView
     * @param imgUrl
     */
    public void loadgiftImage(Context context, ImageView imageView, String imgUrl, int defaultImage) {
        Glide.with(context)
                .load(imgUrl)
                .asGif()
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .error(defaultImage)
                .thumbnail(Contants.THUMB_SIZE)
                .into(imageView);
    }


    /**
     * 加载图片方法
     *
     * @param context          上下文
     * @param imageView        图片控件
     * @param imgUrl           图片地址
     * @param defaultImage     默认图片
     * @param errorImage       错误时图片
     * @param isCropCircle     是否切成圆形
     * @param isRoundedCorners 是否切圆角 默认30
     * @param radius           圆角半径
     * @param isGrayscale      是否做灰度处理
     * @return void
     * <p>
     * tip: .diskCacheStrategy(DiskCacheStrategy strategy).设置缓存策略。
     * DiskCacheStrategy.SOURCE：缓存原始数据
     * DiskCacheStrategy.RESULT：缓存变换(如缩放、裁剪等)后的资源数据
     * DiskCacheStrategy.NONE：什么都不缓存
     * DiskCacheStrategy.ALL：缓存SOURC和RESULT
     * 默认采用DiskCacheStrategy.RESULT策略
     * 对于download only操作要使用DiskCacheStrategy.SOURCE。
     * <p>
     * .priority(Priority priority). 指定加载的优先级，优先级越高越优先加载，但不保证所有图片都按序加载。
     * 枚举Priority.IMMEDIATE，Priority.HIGH，Priority.NORMAL，Priority.LOW
     * 默认为Priority.NORMAL。
     * <p>
     * .skipMemoryCache(boolean skip). 设置是否跳过内存缓存，但不保证一定不被缓存
     * （比如请求已经在加载资源且没设置跳过内存缓存，这个资源就会被缓存在内存中）。
     * <p>
     * .override(int width, int height). 重新设置Target的宽高值（单位为pixel）。
     * <p>
     * .into(int width, int height). 后台线程加载时要加载资源的宽高值（单位为pixel）。
     * <p>
     * .preload(int width, int height). 预加载resource到缓存中（单位为pixel）。
     * <p>
     * .asBitmap(). 无论资源是不是gif动画，都作为Bitmap对待。如果是gif动画会停在第一帧。
     * <p>
     * .asGif().把资源作为GifDrawable对待。如果资源不是gif动画将会失败，会回调.error()
     * <p>
     * Glide.thumbnail(0.1f) 用原图的1/10作为缩略图
     * <p>
     * DrawableRequestBuilder<Integer> thumbnailRequest = Glide //用其它图片作为缩略图
     * .with(this)
     * .load(R.drawable.xxx);
     * //then
     * Glide.thumbnail(thumbnailRequest)
     * <p>
     * 更多API查看官方文档 https://github.com/bumptech/glide
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public void load(Context context, ImageView imageView, String imgUrl, int defaultImage,
                     Drawable errorImage, boolean isCropCircle, boolean isRoundedCorners, boolean isGrayscale, int radius) {

        //处理选项设置和开始一般resource类型资源的加载
        GenericRequestBuilder genericRequestBuilder = null;

        // 是否切成圆形
        if (isCropCircle) {
            genericRequestBuilder = Glide.with(context).load(imgUrl)
                    .bitmapTransform(new CropCircleTransformation(context));
        }
        //是否切成圆角
        if (isRoundedCorners) {
            genericRequestBuilder = Glide.with(context).load(imgUrl)
                    .bitmapTransform(new RoundedCornersTransformation(context, radius, 0, RoundedCornersTransformation.CornerType.ALL));
        }
        //是否做灰度处理
        if (isGrayscale) {
            genericRequestBuilder = Glide.with(context).load(imgUrl)
                    .bitmapTransform(new GrayscaleTransformation(context));
        }
        //不做glide-transformations处理
        if (!isCropCircle && !isRoundedCorners && !isGrayscale) {
            genericRequestBuilder = Glide.with(context).load(imgUrl);
        }

        //处理选项，加载图片
        if (genericRequestBuilder == null) {
            return;
        }
        genericRequestBuilder
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(defaultImage)
                .priority(Priority.NORMAL)
                .error(errorImage)
                .into(imageView);
    }

    /**
     * 恢复请求，一般在停止滚动的时候
     *
     * @param context
     */
    public void resumeRequests(Context context) {
        Glide.with(context).resumeRequests();
    }

    /**
     * 暂停请求 正在滚动的时候
     *
     * @param context
     */
    public void pauseRequests(Context context) {
        Glide.with(context).pauseRequests();
    }

    /**
     * 清除内存缓存
     *
     * @param context
     * @return
     * @author
     * @createTime
     * @lastModify
     */
    public void clearMemory(final Context context) {
        // 图片加载库采用Glide框架
        // 必须在UI线程中调用
        Glide.get(context).clearMemory();
        ;

    }

    /**
     * 清除磁盘缓存
     *
     * @param context
     * @return
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public void clearDiskCache(Context context) {
        // 必须在后台线程中调用，建议同时clearMemory()
        Glide.get(context).clearDiskCache();
    }

    /**
     * 清除view缓存
     *
     * @param view
     * @return
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public void clearViewCache(View view) {
        Glide.clear(view);
    }

    /**
     * 获取SD卡下图片路径
     *
     * @param fullPath SD下图片完整路径
     * @return
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public static String getSDSource(String fullPath) {
        return "file://" + fullPath;
    }

    /**
     * 获取ASSETS下图片路径
     *
     * @param fileName 图片名称
     * @return
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public static String getAssetsSource(String fileName) {
        return "file:///android_asset/" + fileName;
    }

    /**
     * 获取Raw下视频可以解析一张图片路径
     *
     * @param context 上下文
     * @param rawRid  视频id
     * @return
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public static String getRawSource(Context context, int rawRid) {
        return "android.resource://" + context.getPackageName() + "/raw/" + rawRid;
    }

    /**
     * 获取图片资源文件
     *
     * @param context 上下文
     * @param drawRid drawable目录下图片id
     * @return
     * @author wujian
     * @createTime 2017年3月27日
     * @lastModify 2017年3月27日
     */
    public static String getDrawableSource(Context context, int drawRid) {
        return "android.resource://" + context.getPackageName() + "/drawable/" + drawRid;
    }

}
