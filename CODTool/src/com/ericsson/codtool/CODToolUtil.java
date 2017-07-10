package com.ericsson.codtool;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Puneet Raj Srivastava
 * Ericsson India Pvt Ltd 
 * FEB 2015
 * 
 */
public class CODToolUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CODToolUtil.class);

	static int repeat=0;
	/* Method to read values from property file */
	public String getPropValues(String propKey) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-Method getPropValues() - started with Key: "+propKey);
		}

		Properties prop = new Properties();
		String propFileName = "CODTool.properties";

		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream(propFileName);

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName
					+ "' not found in the classpath");
		}

		Date time = new Date(System.currentTimeMillis());

		// get the property value and print it out
		String propValue = prop.getProperty(propKey);
		if(propValue.isEmpty())
		{
			logger.error("Property key"+propKey+" Not found");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-Method getPropValues() - END");
		}
		return propValue;
	}

	/* Method to search log */
	
	/**
	 * @param inputDir
	 * @param filename
	 * @param searchinfostart
	 * @param searchinfoend
	 * @return
	 */
	public String searchLogLTE(String inputDir,String filename, String searchinfostart,String searchinfoend)

	{
		if (logger.isDebugEnabled()) {
			logger
				.debug("CODToolUtil-searchLogLTE(String, String, String, String) - start");
		}

		String result =null;
		StringBuffer finalResult = new StringBuffer();
		try {

			String pathofdirectory = inputDir;// Directory to search
														// input log files
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(pathofdirectory + filename), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			sb.append("Processing File");
			String line = br.readLine();

			while (line != null) {
				if(line.startsWith("*******"))
					line=line.concat(">");
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			StringBuffer searchbuffer = new StringBuffer(searchinfostart);
			String strArray[] = sb.toString().split(searchbuffer.toString());
			for (int l = 1; l < strArray.length; l++) {
				
				int end = strArray[l].indexOf(searchinfoend);
				String str2 = strArray[l].substring(0, end);
				int k = str2.lastIndexOf("\n");
				result = str2.substring(0, k);
				finalResult.append(result);
				
			}

			br.close();
		} catch (IOException e) {
			logger.error("CODToolUtil-searchLogLTE(String, String, String, String)", e);

			logger.error("CODToolUtil-searchLogLTE(String, String, String, String)", e);
		}

		String returnString = finalResult.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-searchLogLTE(String, String, String, String) - end");
		}
		return returnString;
	}

	/* Extra Values search in log */

	/**
	 * @param inputDir
	 * @param filename
	 * @param searchterm
	 * @return
	 */
	public String searchExtraLog(String inputDir,String filename, String searchterm)

	{
		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-searchExtraLog(String, String, String) - start");
		}

		String result = "No Value Find";//If  no value returned from method
		try {

			String pathofdirectory = inputDir;// Directory to search input log files
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pathofdirectory + filename), "UTF-8"));
			String line = br.readLine();
			line=line.trim();
			 
			while (line != null) {
				if(line.startsWith(searchterm))
				
				{   
					if (line.split(":").length>1) //make sure splitted string has some values
					{
					String resultarray[] = line.split(":");
					result=resultarray[1];
					}
				}
				line = br.readLine();
			}


			br.close();
		} catch (IOException e) {
			logger.error("CODToolUtil-searchExtraLog(String, String, String)", e);

			logger.error("CODToolUtil-searchExtraLog(String, String, String)", e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-searchExtraLog(String, String, String) - end");
		}
		return result;
	}

	public static String getDate()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-getDate() - start");
		}

		Date date = new Date();
	      SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	      String fdate=sdf.format(date);

		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-getDate() - end");
		}
		return fdate;
		
	}
	
	public static String getDateHourFormat()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-getDateHourFormat() - start");
		}

		Date date = new Date();
	      SimpleDateFormat sdf = new SimpleDateFormat("MM:dd:yyyy:HH");
	      String fdate=sdf.format(date);

		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-getDateHourFormat() - end");
		}
		return fdate;
		
	}
	
	public static String getDatefromFile(String filename)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-getDatefromFile(String) - start");
		}

	      StringBuffer sbf1=new StringBuffer(filename);
	      String reverse=sbf1.reverse().toString();
	      
	      String[] filesplitname = reverse.split("\\_"); 
	      StringBuffer againreverse=new StringBuffer(filesplitname[4]);
	      StringBuffer againreverse1=new StringBuffer(filesplitname[3]);
	      String d1=againreverse.reverse().toString();
	      String d2=againreverse1.reverse().toString();
		String returnString = d1 + d2;
		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-getDatefromFile(String) - end");
		}
	      return returnString;
	}
	//delete file method
	public boolean deleteFile(String filename){
		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-deleteFile(String) - start");
		}

		boolean b=false;
		{	
    	try{
 
    		File file = new File(filename);
 
    		if(file.delete()){
				if (logger.isDebugEnabled()) {
					logger.debug("CODToolUtil-deleteFile(String)"
						+ file.getName()
						+ " is deleted!");
				}
    			b=true;
    		}else{
				if (logger.isDebugEnabled()) {
					logger
						.debug("CODToolUtil-deleteFile(String) - Delete operation is failed.");
				}
    		}
 
    	}catch(Exception e){
			logger.error("CODToolUtil-deleteFile(String)", e);
 
			logger.error("CODToolUtil-deleteFile(String)", e);
 
    	}
    }

		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-deleteFile(String) - end");
		}
	return b;
	
    }
	//method end
	
	
	
	
	
	
	public static void main(String[] args) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("CODToolUtil-main(String[]) - start");
		}

//lh ru fui get vswr
		//>testing/COD_WCDMA_SCCLU0023_01092015_14_39_Manual_CallTestY.txt
		System.out.println(new CODToolUtil().searchLogLTE("C:\\Users\\epunsri\\workspace\\CODTool\\testing\\", "testingfile.txt", "UTRANCELL\\/EUTRANCELLS OF NEW CARRIER  :","CallTest Result"));
		//System.out.println(new CODToolUtil().getDatefromFile("COD_WCDMA_SCCLU0023_01092015_14_39_Manual_CallTestY.txt"));
		//System.out.println(new CODToolUtil().deleteFile("crawl.log"));
		
	}//6051028010
}
