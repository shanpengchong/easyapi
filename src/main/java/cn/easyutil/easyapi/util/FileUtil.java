package cn.easyutil.easyapi.util;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class FileUtil {

	/**
	 * 创建新文件
	 *
	 * @param path
	 *            文件路径
	 * @return File
	 */
	public static File createNewFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			return file;
		} else {
			// 文件不存在时创建
			String parentPath = path.substring(0, path.lastIndexOf(File.separator));
			new File(parentPath).mkdirs();// 创建父目录
			try {
				File f = new File(path);
				f.createNewFile();// 创建文件
				return f;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * 创建文件夹
	 *
	 * @param path
	 *            文件路径
	 * @return File
	 */
	public static void createNewFolder(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 写入文件
	 * 
	 * @param file
	 *            文件对象
	 * @param content
	 *            内容
	 */
	public static void write(File file, String content) {
//		String aesKey = YiFeiAuthProcess.getAesKey();
//		content = StringUtil.AESEncode(content, aesKey);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (BufferedWriter bufferedWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), "utf-8"), 512 * 1024)) {
			bufferedWriter.write(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void del(File file) {
		del(file, true);
	}
	
	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void del(File file,boolean delChildren) {
		if (!file.exists()) {
			return;
		}
		if(file.isDirectory()){
			if(delChildren){
				//循环删除
				File[] files = file.listFiles();
				for (File f : files) {
					del(f, delChildren);
				}
			}
		}
		file.delete();
		
	}

	/**
	 * 读取源文件
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileContent(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
//			return StringUtil.AESDecode(sb.toString(), YiFeiAuthProcess.getAesKey());
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if(reader != null){
					reader.close();
				}
			} catch (IOException e) {
				return null;
			}
		}
	}

	/**
	 * 扫描指定包路径下所有包含指定注解的类
	 *
	 * @param packagePath
	 *            包名
	 * @param apiClass
	 *            指定的注解
	 * @return Set
	 */
	public static Set<Class> getClass4Annotation(String packagePath,Class<?>...apiClass) {
		Set<Class> classSet = new HashSet<>();
		// 是否循环迭代
		boolean recursive = true;
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			String path = StringUtil.isEmpty(packagePath)?"":packagePath;
			dirs = Thread.currentThread().getContextClassLoader().getResources(path);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					File dir = new File(filePath);
					List<File> fileList = new ArrayList<File>();
					fetchFileList(dir, fileList);
					for (File f : fileList) {
						String fileName = f.getAbsolutePath();
						if (fileName.endsWith(".class")) {
							String noSuffixFileName = fileName.substring(8 + fileName.lastIndexOf("classes"),
									fileName.indexOf(".class"));
							String regex = "\\\\";
							if (!"\\".equals(File.separator)) {
								regex = File.separator;
							}
							String filePackage = noSuffixFileName.replaceAll(regex, ".");
							try {
								Class clazz = Class.forName(filePackage);
								for (Class c : apiClass) {
									if (null != clazz.getAnnotation(c)) {
										classSet.add(clazz);
									}
									
								}
							} catch (ExceptionInInitializerError e) {
								System.out.println("扫描控制器错误:"+filePackage);
							}
						}
					}
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classSet;
	}

	/**
	 * 查找所有的文件
	 *
	 * @param dir
	 *            路径
	 * @param fileList
	 *            文件集合
	 */
	public static void fetchFileList(File dir, List<File> fileList) {
		if(!dir.exists()){
			return;
		}
		if (dir.isDirectory()) {
			for (File f : Objects.requireNonNull(dir.listFiles())) {
				fetchFileList(f, fileList);
			}
		} else {
			fileList.add(dir);
		}
	}
}
