package com.main;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.tika.Tika;

import com.uglify.UglifyRoadJs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLClassLoader;

/** jar 파일 읽어서 js파일들 난독화 작업 
 * 현재 난독화 작업까지 했다.
 * jar파일에 써주는 작업만 진행하면 된다.
 * @author YOU
 *
 */
public class JarTest {

	public static void main(String[]args) throws Exception {
		System.out.println("테스트");
//		Scanner sc = new Scanner(System.in);
//		String test = sc.nextLine();
//		System.out.println(test);
		String testTemp = "C:/Users/YOU/Desktop/test/";
		FileLoad(testTemp);
	}
	
	public static void FileLoad( String directoryPath) throws Exception {
		 File dir = new File(directoryPath);
		 File is[] = dir.listFiles();
		 //C:\Users\YOU\Desktop\test
		 int fileCnt = 0;
		 ArrayList<File> filePath = new ArrayList<>(); 
		 ArrayList<String> filePathStr = new ArrayList<>(); 
		 
		 for(int i = 0 ; i < is.length ; i++) {
			 System.out.println(is[i]);
			 String fileName = is[i].toString();
			 //int fileIndex = fileName.indexOf("jar");
			 int fileIndex = fileName.indexOf("war");
			 if( fileIndex > -1) { ++fileCnt;filePath.add(is[i]); filePathStr.add(is[i].toString());}
		 }
		 if(fileCnt == 0 ) {
			 throw new Exception("파일 디렉토리 재체크 부탁드립니다.( JAR가 존재하지 않습니다. )");
		 }
		 

		JarFile jarLoad = new JarFile(filePathStr.get(0));
		Enumeration<JarEntry> entries = jarLoad.entries();
		 // jar 파일을 읽는 작업이 필요 
		URLClassLoader classLoader;
		
		InputStream in = null;
		ZipArchiveInputStream zais = null;
		ZipArchiveEntry zipEntry = null;
		Tika tika = new Tika();
		
		File jarFile = new File(filePathStr.get(0));
		ArrayList<String> fileList = new ArrayList<>();
		if(jarFile.exists()) {
			in = new FileInputStream(new File(filePathStr.get(0)));
			
			zais = new ZipArchiveInputStream(in, "UTF-8", true);
			while((zipEntry = zais.getNextZipEntry()) != null) {
				// application/javascript
				// text/x-jsp
				String fileType = tika.detect(zipEntry.getName());
				String fileDetailPath = zipEntry.getName();
				if( fileType.equals("application/javascript") 
					&& fileDetailPath.indexOf("jquery") == -1
					&& fileDetailPath.indexOf("jqgrid") == -1
					) {
					
					fileList.add(fileDetailPath);
					int singleCh = 0;
					String scriptDetail = "";
					InputStreamReader jsCodeEncoding = new InputStreamReader( zais , "utf-8");
					while((singleCh = jsCodeEncoding.read()) != -1){
						scriptDetail += (char)singleCh;
		            }
					System.out.println("---------- "+fileDetailPath+" ---------------");
					if( fileDetailPath.equals("contents/js/nCommon.js")){
					String ugStr = new UglifyRoadJs().uglify(scriptDetail);
					System.out.println( "ugStr >>>>>> "+ugStr);
					System.out.println("-------------------------");
					Pattern pattern = Pattern.compile("^[a-zA-Z0-9,`~!@#$%^&*()-_=+;:/\'\"\\.\\,\\w\\s ]*");
					Matcher stringChk = pattern.matcher(scriptDetail );
					
					String[] pathLength = fileDetailPath.split("/");
					String pathLengthRun = pathLength[pathLength.length-1];   
					fileWriter(ugStr,pathLengthRun);
					}
					//break;
				}
//				else if( fileType.equals("text/x-jsp") ) {
//					fileList.add(fileDetailPath);
//					int singleCh = 0;
//					String jspDetail = "";
//					InputStreamReader jspCodeEncoding = new InputStreamReader( zais , "utf-8");
//					while((singleCh = jspCodeEncoding.read()) != -1){
//						jspDetail += (char)singleCh;
//		            }
//					Pattern pattern = Pattern.compile("^[a-zA-Z0-9,`~!@#$%^&*()-_=+;:/\'\"\\.\\,\\w\\s ]*");
//					Matcher stringChk = pattern.matcher(jspDetail);
//					
//					String ugStr = new UglifyRoadJs().uglify(jspDetail);
//					
//				}
				
			}
		}
		
	}
	
	public static void fileWriter(String args , String fileName) throws Exception {

		File file = new File("C:/Users/YOU/Desktop/ICM2팀 강범서/"+fileName);

		FileWriter fw = new FileWriter(file, true);

		//fw.write("FileWriter는 한글로된 " + "\r\n");
		fw.write(args);

		fw.flush();

		fw.close();

		System.out.println("파일에 저장되었습니다.");

	}
	
}
