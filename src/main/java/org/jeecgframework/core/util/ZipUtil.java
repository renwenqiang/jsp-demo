package org.jeecgframework.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


public class ZipUtil {
	/**
	 * 解压到指定目录
	 * 
	 * @param zipPath
	 * @param descDir
	 * @author isea533
	 */
	@Deprecated
	public static void unZipFiles(String zipPath, String descDir)
			throws IOException {
		unZipFiles(new File(zipPath), descDir);
	}

	/**
	 * 解压文件到指定目录
	 * 
	 * @param zipFile
	 * @param descDir
	 * @author isea533
	 */
	@SuppressWarnings("rawtypes")
	public static void unZipFiles(File zipFile, String descDir)
			throws IOException {
		File pathFile = new File(descDir);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		ZipFile zip = new ZipFile(zipFile,Charset.forName("utf-8"));
		for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			String zipEntryName = entry.getName();
			InputStream in = zip.getInputStream(entry);
			String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
			outPath = new String(outPath.getBytes("utf-8"), "ISO8859-1");
			;
			// 判断路径是否存在,不存在则创建文件路径
			File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
			if (!file.exists()) {
				file.mkdirs();
			}
			// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
			if (new File(outPath).isDirectory()) {
				continue;
			}
			// 输出文件路径信息
			System.out.println(outPath);

			OutputStream out = new FileOutputStream(outPath);
			byte[] buf1 = new byte[1024];
			int len;
			while ((len = in.read(buf1)) > 0) {
				out.write(buf1, 0, len);
			}
			in.close();
			out.close();
		}
	}

	/**
	 * 递归压缩文件
	 * 
	 * @param source
	 *            源路径,可以是文件,也可以目录
	 * @param destinct
	 *            目标路径,压缩文件名
	 * @throws IOException
	 */
	public static void compress(String source, String destinct)
			throws IOException {
		List fileList = loadFilename(new File(source));
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
				new File(destinct)));

		byte[] buffere = new byte[8192];
		int length;
		BufferedInputStream bis;

		for (int i = 0; i < fileList.size(); i++) {
			File file = (File) fileList.get(i);
			zos.putNextEntry(new ZipEntry(getEntryName(source, file)));
			bis = new BufferedInputStream(new FileInputStream(file));

			while (true) {
				length = bis.read(buffere);
				if (length == -1)
					break;
				zos.write(buffere, 0, length);
			}
			bis.close();
			zos.closeEntry();
		}
		zos.close();
	}

	/**
	 * 递归获得该文件下所有文件名(不包括目录名)
	 * 
	 * @param file
	 * @return
	 */
	private static List loadFilename(File file) {
		List filenameList = new ArrayList();
		if (file.isFile()) {
			filenameList.add(file);
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				filenameList.addAll(loadFilename(f));
			}
		}
		return filenameList;
	}

	/**
	 * 获得zip entry 字符串
	 * 
	 * @param base
	 * @param file
	 * @return
	 */
	private static String getEntryName(String base, File file) {
		File baseFile = new File(base);
		String filename = file.getPath();
		// int index=filename.lastIndexOf(baseFile.getName());
		if (baseFile.getParentFile().getParentFile() == null)
			return filename.substring(baseFile.getParent().length());
		return filename.substring(baseFile.getParent().length() + 1);
	}
	
	 public static void main(String[] args) throws IOException {
	        //compress("D:/tomcat-5.5.20","d:/test/testZip.zip");
		 unZip("D:/saas-plugin-web-master-shiro-mybatis.zip","D:/123");
	 }
	 
	 private static final int buffer = 2048;   

   /**  
    * 解压Zip文件  
    * @param path 文件目录  
    */  
   public static void unZip(String path,String savepath)   
       {   
        int count = -1;   
        File file = null;   
        InputStream is = null;   
        FileOutputStream fos = null;   
        BufferedOutputStream bos = null;   
        new File(savepath).mkdir(); //创建保存目录   
        ZipFile zipFile = null;   
        try  
        {   
            zipFile = new ZipFile(path,Charset.forName("utf-8")); //解决中文乱码问题   
            Enumeration<?> entries = zipFile.entries();   
 
            while(entries.hasMoreElements())   
            {   
                byte buf[] = new byte[buffer];   
 
                ZipEntry entry = (ZipEntry)entries.nextElement();   
 
                String filename = entry.getName();   
                boolean ismkdir = false;   
                if(filename.lastIndexOf("/") != -1){ //检查此文件是否带有文件夹   
                   ismkdir = true;   
                }   
                filename = savepath + filename;   
 
                if(entry.isDirectory()){ //如果是文件夹先创建   
                   file = new File(filename);   
                   file.mkdirs();   
                    continue;   
                }   
                file = new File(filename);   
                if(!file.exists()){ //如果是目录先创建   
                   if(ismkdir){   
                   new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs(); //目录先创建   
                   }   
                }   
                file.createNewFile(); //创建文件   
 
                is = zipFile.getInputStream(entry);   
                fos = new FileOutputStream(file);   
                bos = new BufferedOutputStream(fos, buffer);   
 
                while((count = is.read(buf)) > -1)   
                {   
                    bos.write(buf, 0, count);   
                }   
                bos.flush();   
                bos.close();   
                fos.close();   
 
                is.close();   
            }   
 
            zipFile.close();   
 
        }catch(IOException ioe){   
            ioe.printStackTrace();   
        }finally{   
               try{   
               if(bos != null){   
                   bos.close();   
               }   
               if(fos != null) {   
                   fos.close();   
               }   
               if(is != null){   
                   is.close();   
               }   
               if(zipFile != null){   
                   zipFile.close();   
               }   
               }catch(Exception e) {   
                   e.printStackTrace();   
               }   
           }   
       }   


   /**
	 * 解压缩zip包
	 * 
	 * @param zipFilePath
	 *            zip文件路径
	 * @param targetPath
	 *            解压缩到的位置，如果为null或空字符串则默认解压缩到跟zip包同目录跟zip包同名的文件夹下
	 * @throws IOException
	 */
	public static void unzip(String zipFilePath, String targetPath) throws IOException {
		OutputStream os = null;
		InputStream is = null;
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFilePath,Charset.forName("utf-8"));
			String directoryPath = "";
			if (null == targetPath || "".equals(targetPath)) {
				directoryPath = zipFilePath.substring(0, zipFilePath.lastIndexOf("."));
			} else {
				directoryPath = targetPath;
			}
			Enumeration entryEnum = zipFile.entries();
			if (null != entryEnum) {
				ZipEntry zipEntry = null;
				while (entryEnum.hasMoreElements()) {
					zipEntry = (ZipEntry) entryEnum.nextElement();
					if (zipEntry.isDirectory()) {
						directoryPath = directoryPath + File.separator + zipEntry.getName();
						org.jeecgframework.core.util.LogUtil.info(directoryPath);
						continue;
					}
					if (zipEntry.getSize() > 0) {
						// 文件
						File targetFile = buildFile(directoryPath + File.separator + zipEntry.getName(), false);
						os = new BufferedOutputStream(new FileOutputStream(targetFile));
						is = zipFile.getInputStream(zipEntry);
						byte[] buffer = new byte[4096];
						int readLen = 0;
						while ((readLen = is.read(buffer, 0, 4096)) >= 0) {
							os.write(buffer, 0, readLen);
						}
						os.flush();
						os.close();
					} else {
						// 空目录
						buildFile(directoryPath + File.separator + zipEntry.getName(), true);
					}
				}
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (null != zipFile) {
				zipFile = null;
			}
			if (null != is) {
				is.close();
			}
			if (null != os) {
				os.close();
			}
		}
	}
	/**
	 * 生产文件 如果文件所在路径不存在则生成路径
	 * 
	 * @param fileName
	 *            文件名 带路径
	 * @param isDirectory
	 *            是否为路径
	 */
	public static File buildFile(String fileName, boolean isDirectory) {
		File target = new File(fileName);
		if (isDirectory) {
			target.mkdirs();
		} else {
			if (!target.getParentFile().exists()) {
				target.getParentFile().mkdirs();
				target = new File(target.getAbsolutePath());
			}
		}
		return target;
	}

}
