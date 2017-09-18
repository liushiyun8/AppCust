package foundation.helper;

import android.os.Environment;



import java.io.File;

import app.BaseAppContext;



public class FileManager {

	public static FileManager manager = new FileManager();

	private FileManager() {
		FileHelper.mkdir(imageCatchDir());
		FileHelper.mkdir(cameraDir());
		FileHelper.mkdir(tempImageDir());
	}

	public void delTempFile() {
		FileHelper.delete(new File(tempImageDir()));
	}

	// 根目录
	private String basePath() {
		String path = "";
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			path = Environment.getExternalStorageDirectory().toString();
		} else {

			path = 	BaseAppContext.getInstance().getFilesDir().getPath();
		}
		return path + File.separator + "玩艺汇"+ File.separator;
	}



	// 生成零时文件
	public String tempImageDir() {
		return basePath() + "tempimages" + File.separator;
	}

	// ImageLoader缓存图片存放路径
	public String imageCatchDir() {
		return basePath() + "images" + File.separator;
	}

	// 拍照文件临时存储路径
	public String cameraDir() {
		return basePath() + "images" + File.separator + "拍照" + File.separator;
	}


}
