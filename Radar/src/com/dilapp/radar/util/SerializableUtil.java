package com.dilapp.radar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 操作Serializable 对象工具类
 * 
 * @ClassName: SerializableUtil
 * @date 2013-3-3 下午3:09:41
 */
public class SerializableUtil {
	/**
	 * 保存Serializable对象
	 * 
	 * @param filepath
	 * @param object
	 */
	public static void writeSerializableObject(String filepath, Object object) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(filepath));
			out.writeObject(object);
			out.flush();
			// ------------------------------------
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取Serializable对象
	 * 
	 * @param context
	 * @param filepath
	 * @return
	 */
	public static <T extends Object> T readSerializableObject(String filepath) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(filepath));
			T readObject = (T) in.readObject();
			return readObject;
		} catch (/*StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (*/Exception e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 删除保存的文件
	 * 
	 * @param context
	 * @param filename
	 */
	public static void deleteSerializableObject(String filename) {
		new File(filename).delete();
	}
}
