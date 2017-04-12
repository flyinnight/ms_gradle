package com.dilapp.radar.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.dilapp.radar.textbuilder.utils.L;

public class DownloadUtils {

	private final static String TAG = L.makeTag(DownloadUtils.class);
	private final static boolean LOG = true;

	private final static int BUFFSIZE = 1024;

	/**
	 * 下载文件，并以MD5编码url地址为文件名
	 * 
	 * @param url
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public static String downloadForUrl(URL url, String dir) throws IOException {

		String path = null;
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(5000);
		if (conn.getResponseCode() == 200) {
			String filename = MD5.getMD5(url.toExternalForm());
			path = dir + File.separator + filename;
			OutputStream os = null;
			InputStream is = null;
			os = new FileOutputStream(path);
			is = conn.getInputStream();
			if (LOG)
				L.d(TAG, "url saved to " + path + " (" + url.toString() + ")");

			try {
				byte[] buff = new byte[BUFFSIZE];
				int len = 0;
				while ((len = is.read(buff)) != -1) {
					os.write(buff, 0, len);
				}
			} catch (IOException e) {
				L.w(TAG, "read error " + url.toString(), e);
			} finally {
				try {
					os.close();
				} catch (Exception e) {
				}
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		return path;
	}
}
