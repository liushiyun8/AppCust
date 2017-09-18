package foundation.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelper {

	static public boolean exist(String path) {
		File file = new File(path);
		return file.exists();
	}
	static public long filesize(String path) {
		File file = new File(path);
		return file.length();
	}

	// 递归删除文件及文件夹
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	static public boolean mkdir(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			return dir.mkdirs();
		}
		return true;
	}

	// 删除文件
	static public boolean delFile(String path) {
		File file = new File(path);
		return file.delete();
	}

	/**
	 * 移动文件
	 * 
	 * @param srcFileName
	 *            源文件完整路径
	 * @param destDirName
	 *            目的目录完整路径
	 * @return 文件移动成功返回true，否则返回false
	 */
	static public boolean moveFile(String srcFileName, String destDirName) {

		File srcFile = new File(srcFileName);
		
		if (!srcFile.exists() || !srcFile.isFile())
			return false;

		File destDir = new File(destDirName);
		if (!destDir.exists())
			destDir.mkdirs();

		return srcFile.renameTo(new File(destDirName + File.separator
				+ srcFile.getName()));
	}
	
	/**
	 * 重命名文件
	 * 
	 * @param srcFileName
	 *            源文件完整路径
	 * @param
	 *
	 * @return 文件移动成功返回true，否则返回false
	 */
	static public boolean renameFile(String srcFileName, String destFileName) {

		File srcFile = new File(srcFileName);
		
		if (!srcFile.exists() || !srcFile.isFile())
			return false;

		return srcFile.renameTo(new File(destFileName));
	}
	
	 
	static public boolean copyFile(String source, String dest) {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(new File(source));
			output = new FileOutputStream(new File(dest));
			byte[] buf = new byte[1024 * 1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null)
					input.close();
				if (output != null)	
					output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
