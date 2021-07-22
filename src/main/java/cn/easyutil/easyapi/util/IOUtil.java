package cn.easyutil.easyapi.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {

	/**
	 * input流转byte数组
	 * @param in
	 * @return
	 */
	public static byte[] inputToByte(InputStream in){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] but = new byte[1024*1024];
		int length = 0;
		try {
			while((length=in.read(but)) != -1){
				out.write(but, 0, length);
			}
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * byte数组转input
	 * @param but
	 * @return
	 */
	public static InputStream byteToInput(byte[] but){
		return new ByteArrayInputStream(but);
	}
	
}
