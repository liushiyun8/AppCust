package com.eke.cust;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class Common {
	public static final int time = 60;
	private static final String APPID = "appid=52368a3d";
	public static final String nurseryid = "1";
	// public static final String downloadpath =
	// "http://192.168.1.183:803/Version/apk.aspx?PRODUCT_ID="
	// + Common.nurseryid;
	public static int largeNum = 800;
	public static int smallNum = 480;
	public static int qualityNum = 80;
	public static String strImgPath = Environment.getExternalStorageDirectory()
			.toString() + File.separator + "sjbus" + File.separator;
	public static String filepath = Environment.getExternalStorageDirectory()
			.toString() + File.separator + "sjbus" + File.separator;
	public static List<String> pathlist;
	public static String content = "";
	public static String lineno = "";
	public static String line = "";
	public static String name = "";
	public static String username = "";
	public static String password = "";
	public static List<String[]> waitmessagelist;
	public static String image = "";
	public static MediaPlayer mediaPlayer;
	public static int position;
	public static String station = "";
	public static String str1 = "";
	public static Context context1;
	public static Activity activity;
	public static List<String[]> stations;
	public static String hc;
	public static String url;
	public static List<String[]> stationlists;
	public static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.color.white).showImageOnFail(R.color.white)
			.cacheInMemory(true).cacheOnDisk(true)
			.bitmapConfig(Bitmap.Config.ALPHA_8).build();

	public static String TransactSQLInjection(String sql) {
		if (sql == null || sql.equals(""))
			return "";
		return sql.replaceAll(".*([';]+|(--)+).*", " ");
	}

	/**
	 * ���๦��
	 */
	public static String cameraMethod(Activity activity,
			int RESULT_CAPTURE_IMAGE) {

		try {
			Intent imageCaptureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date()) + ".jpg";// ��Ƭ����
			strImgPath = Common.filepath;
			File out = new File(Common.filepath);
			if (!out.exists()) {
				out.mkdirs();
			}
			out = new File(strImgPath, fileName);
			strImgPath = strImgPath + fileName;// ����Ƭ�ľ���·��
			Uri uri = Uri.fromFile(out);
			imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			activity.startActivityForResult(imageCaptureIntent,
					RESULT_CAPTURE_IMAGE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strImgPath;

	}

	public static void saveMyBitmap(String bitName, Bitmap mBitmap)
			throws IOException {
		File f = new File(strImgPath + bitName);
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ������Ƭ��ͨ���ļ�·����
	public static String zoomPhoto(String strImgname) throws Exception {
		int largeNum = Common.largeNum;
		int smallNum = Common.smallNum;
		int qualityNum = Common.qualityNum;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(strImgname, options);
		options.inJustDecodeBounds = false;
		int iss = 1;
		try {
			iss = options.outHeight > options.outWidth ? options.outHeight
					/ largeNum : options.outWidth / largeNum;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		options.inSampleSize = iss < 1 ? 1 : iss;
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(strImgname, options);
		int bmpWidth = bitmap.getWidth();
		int bmpHeight = bitmap.getHeight();
		System.out.println(bmpWidth + "BItMap" + bmpHeight);
		float scaleWidth = 1;
		if (bmpWidth > 600 || bmpHeight > 600) {
			if (bmpWidth > bmpHeight) {
				scaleWidth = (float) 600 / bmpWidth;
			} else {
				scaleWidth = (float) 600 / bmpHeight;
			}
		}
		// if (bmpWidth > bmpHeight) {
		// scaleWidth = (float) largeNum / bmpWidth;
		// scaleHeight = (float) smallNum / bmpHeight;
		// } else {
		// scaleWidth = (float) smallNum / bmpWidth;
		// scaleHeight = (float) largeNum / bmpHeight;
		// }
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		// ����Ƭ��ȴ��ڸ߶�����ת��ʮ��
		// if (bmpWidth > bmpHeight) {
		// matrix.postRotate(90);
		// }
		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth,
				bmpHeight, matrix, false);
		// bitmap.recycle();
		bitmap = resizeBitmap;
		String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());// ��Ƭ����
		File out = new File(filepath);
		if (!out.exists()) {
			out.mkdirs();
		}
		out = new File(filepath, fileName);
		String file = filepath + fileName;// ����Ƭ�ľ���·��
		try {
			FileOutputStream fileoutputstream = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, qualityNum,
					fileoutputstream)) {
				fileoutputstream.flush();
				fileoutputstream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	// ������Ƭ��ͨ��bitmap��
	public static Bitmap TestbitmapZoomPhoto(Bitmap bitmap, int largeNum,
			int smallNum, int qualityNum) {
		qualityNum = 100;
		int bmpWidth = bitmap.getWidth();
		int bmpHeight = bitmap.getHeight();
		float scaleWidth = 0;
		float scaleHeight = 0;
		if (bmpWidth > bmpHeight) {
			scaleWidth = (float) largeNum / bmpWidth;
			scaleHeight = (float) smallNum / bmpHeight;
		} else {
			scaleWidth = (float) smallNum / bmpWidth;
			scaleHeight = (float) largeNum / bmpHeight;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth,
				bmpHeight, matrix, false);
		bitmap.recycle();
		bitmap = resizeBitmap;
		return bitmap;
	}

	// ���ļ���ȡ������
	public static byte[] readImage(String imagePath) {
		try {
			File file = new File(imagePath);
			FileInputStream stream = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			b = null;
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// ҳ��toast��ʾ
	public static void alert(Activity activity, String str) {
		Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
	}

	public static void alert(Context activity, String str) {
		Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
	}

	// ��ȡ��ǰ���ڣ�����ʱ���룩
	public static String getTime0() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return sDateFormat.format(
				new java.util.Date(System.currentTimeMillis()))
				.substring(0, 10);
	}

	public static String getTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return sDateFormat
				.format(new java.util.Date(System.currentTimeMillis()));
	}

	// ��ȡ��ǰ���ڣ�����ʱ���룩
	public static String getTime1() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return sDateFormat
				.format(new java.util.Date(System.currentTimeMillis()));
	}

	public static void deleteFile(String filepath) {
		try {
			File delFile = new File(filepath);
			if (delFile.exists()) {
				delFile.delete();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static String getVersionCode(String packageName, Activity ac) {
		try {
			// �ж��Ƿ�װ
			if (new File("/data/data/" + packageName).exists()) {
				// ��ȡϵͳ�а�װ������Ӧ�ð�������
				List<PackageInfo> packages = ac.getPackageManager()
						.getInstalledPackages(0);
				for (int i = 0; i < packages.size(); i++) {
					PackageInfo packageInfo = packages.get(i);
					// �ҳ�ָ����Ӧ��
					if (packageName.equals(packageInfo.packageName)) {
						return packageInfo.versionName;
					}
				}
			}
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String tojson(Object object) {
		return (new Gson()).toJson(object);
	}

	public static Object fromjson(String str, Object object) {

		return (new Gson()).fromJson(str, Object.class);
	}

	public static File getFile(byte[] bfile) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date()) + ".jpg";// ��Ƭ����
		try {
			File dir = new File(strImgPath);
			if (!dir.exists() && dir.isDirectory()) {// �ж��ļ�Ŀ¼�Ƿ����
				dir.mkdirs();
			}
			file = new File(strImgPath + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

	public static String toKilometer(String distance) {
		double d = Double.parseDouble(distance);
		String str = "";
		if (d < 1000) {
			if (distance.indexOf(".") != -1) {
				str = distance.substring(0, distance.indexOf(".")) + "��";
			} else {
				str = distance + "��";
			}
		} else if (d >= 1000) {
			d /= 1000;
			str = new Double(d).toString();
			str = str.substring(0, str.indexOf(".") + 2) + "����";
		}
		return str;
	}

	public static double distance(double lat1, double lon1, double lat2,
			double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		double miles = dist * 60 * 1.1515 * 1.609344 * 1000;
		return miles;
	}// ���Ƕ�ת��Ϊ����

	static double deg2rad(double degree) {
		return degree / 180 *
		//
				Math.PI;
	}

	// ������ת��Ϊ�Ƕ�
	static double rad2deg(double radian) {
		return
		//
		radian * 180 / Math.PI;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static boolean getFiles(String path, String name) throws Exception {
		boolean isexist = false;
		File pathfile = new File(path);
		if (!pathfile.exists()) {
			pathfile.mkdirs();
		}
		File[] allFiles = new File(path).listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			File file = allFiles[i];
			if (file.getAbsolutePath().contains(name)) {
				isexist = true;
				break;
			}
		}
		return isexist;
	}

	public static Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// �ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���
			baos.reset();// ����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// ����ѹ��50%����ѹ��������ݴ�ŵ�baos��
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// ��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// ���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
		float hh = 800f;// �������ø߶�Ϊ800f
		float ww = 480f;// �������ÿ��Ϊ480f
		// ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
		int be = 1;// be=1��ʾ������
		if (w > h && w > ww) {// �����ȴ�Ļ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// ����߶ȸߵĻ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// �������ű���
		// ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// ѹ���ñ�����С���ٽ�������ѹ��
	}

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
			baos.reset();// ����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��
			options -= 10;// ÿ�ζ�����10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
		return bitmap;
	}

	public static String bitmaptofile(Bitmap bitmap) {
		bitmap = Common.comp(bitmap);
		File file = new File(strImgPath + "aaa.png");
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strImgPath + "aaa.png";
	}

	// ����
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public static String hash(String origin) {
		String resultString = null;

		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {
			System.err.println("MD5Encode error: " + ex.getMessage());
		}
		return resultString;
	}

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}


	/** * ��ñ��ο�����ʹ�õ����� */
	public static String getData(Context context) {
		// PackageManager ��������
		PackageManager packageManager = context.getPackageManager();
		int PackageUid = 0;
		BigDecimal numRx = new BigDecimal("0");
		BigDecimal numTx = new BigDecimal("0");
		/** * ѭ��ץ������Ӧ�õİ��� * �͵�ǰӦ�õİ�������ƥ����� * �ѻ�ȡ����UID���浽һ����ʱ���� */
		for (ApplicationInfo info : packageManager.getInstalledApplications(0)) {
			int uid = info.uid;
			String packageName = info.packageName;
			if (packageName.equals("com.dingxun.bus")) {
				PackageUid = uid;
			}
		}
		// TrafficStats�����Ӧ�õ�UID��ȡ���������������
		long rx = TrafficStats.getUidRxBytes(PackageUid);// �ܽ�����
		long tx = TrafficStats.getUidTxBytes(PackageUid);// �ܷ�����
		if (rx > 0) {
			numRx = byteToM(rx);
		}
		if (tx > 0) {
			numTx = byteToM(tx);
		}
		// LogManager.show(TAG, "ת��Ϊ(MΪ��λ)����ܽ�������" + numRx.floatValue(), 1);
		// LogManager.show(TAG, "ת��Ϊ(MΪ��λ)����ܷ�������" + numTx.floatValue(), 1);
		return "��Ӧ�ñ��ο����ܽ�������" + numRx.floatValue() + "��Ӧ�ñ��ο����ܷ�������"
				+ numTx.floatValue();
	}

	/** * �ֽ�ת��ΪM */
	public static BigDecimal byteToM(long value) {
		BigDecimal result = new BigDecimal(Long.toString(value));
		result = result.divide(new BigDecimal("1024.0")).divide(
				new BigDecimal("1024.0"));
		return result;
	}

	private static PowerManager.WakeLock m_wakeLockObj = null;

	public static void AcquireWakeLock(long milltime, Context context) {
		if (m_wakeLockObj == null) {
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			m_wakeLockObj = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.ON_AFTER_RELEASE, "");
			m_wakeLockObj.acquire(milltime);
		}
		m_wakeLockObj = null;
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(activity.getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	public static void deleteAllFiles(File root) {
		File files[] = root.listFiles();
		if (files != null)
			for (File f : files) {
				if (f.isDirectory()) { // �ж��Ƿ�Ϊ�ļ���
					deleteAllFiles(f);
					try {
						f.delete();
					} catch (Exception e) {
					}
				} else {
					if (f.exists()) { // �ж��Ƿ����
						deleteAllFiles(f);
						try {
							f.delete();
						} catch (Exception e) {
						}
					}
				}
			}
	}

	public static void hideinput(Context context, EditText edittext) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		inputMethodManager
				.hideSoftInputFromWindow(edittext.getWindowToken(), 0); // ����
	}

	public static void showpic(final ImageView iv, final String imagepath,
			final Context context, final int width, final int height) {
		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(context));
		if (imagepath != null) {
			final String imagename = imagepath.substring(imagepath
					.lastIndexOf("/") + 1);
			try {
				if (Common.getFiles(Common.filepath, imagename)) {
					Bitmap slbitmap1 = BitmapFactory.decodeFile(Common.filepath
							+ imagename);
					iv.setImageBitmap(slbitmap1);
				} else {
					ImageLoader.getInstance().displayImage(imagepath, iv,
							Common.options, new SimpleImageLoadingListener() {
								@Override
								public void onLoadingStarted(String imageUri,
										View view) {
								}

								@Override
								public void onLoadingFailed(String imageUri,
										View view, FailReason failReason) {
								}

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									Bitmap slbitmap = loadedImage;
									if (width != 0) {
										slbitmap = ThumbnailUtils
												.extractThumbnail(loadedImage,
														Common.dip2px(context,
																width),
														Common.dip2px(context,
																height));
									}
									iv.setImageBitmap(slbitmap);
									try {
										Common.saveMyBitmap(imagename, slbitmap);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[_-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static int compare_date(String DATE1, String DATE2) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				System.out.println("dt1 ��dt2ǰ");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				System.out.println("dt1��dt2��");
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	public static String geturl(String str) {
		Pattern p = Pattern
				.compile(
						"((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)",
						Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		if (m.find()) {
			return m.group();
		} else {
			return str;
		}
	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = bitmap.getWidth() / 2;
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
			return output;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

}
