package com.ericsson.codtool;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Puneet Raj Srivastava
 * Ericsson India Pvt Ltd 
 * FEB 2015
 * 
 */
public class CODTool {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CODTool.class);

	// Method to list all files
	public List<String> ListFile(String path) throws IOException  {
		List<String> fileNames = new ArrayList<String>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
			
		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				fileNames.add(listOfFiles[i].getName());

			}
		}
		return fileNames;
		

	}

	public static void main(String[] args) throws Exception {
		logger.debug("COD Tool Start Time :"+System.currentTimeMillis());
		logger.debug("COD Tool Started : "+new Date());
		logger.debug("COD Tool running in ["+System.getProperty("user.dir")+"]");
		CODTool codTool = new CODTool();
		CODToolUtil codToolUtil = new CODToolUtil();
		logger.debug("COD Tool Reading property file  CODTool.properties  to initialize constants");
		String outputDir = codToolUtil.getPropValues("outputDir");
		String inputDir = codToolUtil.getPropValues("inputDir");
		String fileOKString = codToolUtil.getPropValues("fileOKString");
		String fileNOTOKString = codToolUtil.getPropValues("fileNOTOKString");
		String dirSeprator = codToolUtil.getPropValues("dirSeprator");
		String savedFileExtension = codToolUtil.getPropValues("savedFileExtension");
		String templateCODFile = codToolUtil.getPropValues("templateCODFile");
		List<String> fileNames = codTool.ListFile(inputDir);// Directory to search input files
		if(inputDir.isEmpty())
		
		logger.debug("COD Tool Reading property file Done");
		String fname = null;
		String search_cmd_start = null;
		String search_cmd_end = null;
		String result = null;
		String regex = null;
		String regexend=null;
		CODDAO coddao = new CODDAO();
		Connection con = null;
		String fdate=null;
		String nowdate=CODToolUtil.getDateHourFormat();
		String [] nowdatearrays=nowdate.split("\\:");
		String currentdate=nowdatearrays[0]+nowdatearrays[1]+nowdatearrays[2]+nowdatearrays[3];
		int currenttooldate=Integer.parseInt(currentdate);//current tool run date
		logger.debug("COD Tool -Current Date in Hour Fetched By CODTool from System is :"+currenttooldate);
		
		
		for (int i = 0; i < fileNames.size(); i++) {
			fname = (String) fileNames.get(i);
			//NEW MODIFIED DATE FROM FILE LOGIC START
			File filemodifieddate = new File(inputDir+fname);
			if (logger.isDebugEnabled()) {
				logger.debug("CODTool- Modified Date of File is  :"+ filemodifieddate.lastModified());
			}
			
			Date lastModified = new Date(filemodifieddate.lastModified()); 
			SimpleDateFormat formatter = new SimpleDateFormat("MM:dd:yyyy:HH");  
			String formattedDateString = formatter.format(lastModified); 
			if (logger.isDebugEnabled()) {
				logger.debug("CODTool- Modified date of file in custom format : "+ formattedDateString);
			}
			String [] filedatearrays=formattedDateString.split("\\:");
			String modifiedDate=filedatearrays[0]+filedatearrays[1]+filedatearrays[2]+filedatearrays[3];
			int modifiedfiledate=Integer.parseInt(modifiedDate);
			logger.debug("Modified Date in Hour Fetched By CODTool from file is :"+modifiedfiledate);
			
			if (logger.isDebugEnabled()) {
				logger.debug("CODTool- MODIFIED DATE: "+ modifiedfiledate+ "   TOOL DATE:    "+ currenttooldate);
			}
			//NEW MODIFIED DATE FROM FILE LOGIC END
			
			
			
			if(fname.contains("LTE") && (fname.contains(fileOKString))||fname.contains("LTE") && (fname.contains(fileNOTOKString))||fname.contains("WCDMA") && (fname.contains(fileOKString))||fname.contains("WCDMA") && (fname.contains(fileNOTOKString)))
			{
				if(currenttooldate==modifiedfiledate)
				{
					con = coddao.getConnection(con);
					fdate = CODToolUtil.getDate();
					coddao.insertRecordIntoCODSTATS(fdate, fname);
					logger.debug("CODTool Records Inserted in CODPROCESSED "+fdate+""+fname);
				
					if (fname.contains("LTE") && (fname.contains(fileOKString))&& fname.contains("Manual")) {
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- CASE 1 LTE , Manual with CallTestY found file is "+ fname);
						}
						// call LTE Process Method
						FileInputStream fsIP = new FileInputStream(	new File(templateCODFile)); // Template COD file
						XSSFWorkbook wb = new XSSFWorkbook(fsIP);
						XSSFSheet worksheet = wb.getSheetAt(0);
						Cell cell = null;

						logger.debug("Extra Log Information writing from 1-16 col START");

						cell = worksheet.getRow(3).getCell(2);// 1-Node EnodeB
						regex = "NODEB / ENODEB ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(4).getCell(2);// 2-TECHNOLOGY
						regex = "TECHNOLOGY";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(5).getCell(2);// 3-RNC ID
						regex = "RNC ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(6).getCell(2);// 4-Market
						regex = "MARKET";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(7).getCell(2);// 5-OSS
						regex = "OSS";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(8).getCell(2);// 6-New Carrier
						regex = "NEW CARRIER";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(9).getCell(2);// 7-UTRANCELL/EUTRANCELLS OF NEW CARRIER
						regex = "UTRANCELL\\/EUTRANCELLS OF NEW CARRIER";
						regexend="CallTest Result";
						result = codToolUtil.searchLogLTE(inputDir, fname, regex,regexend);
						cell.setCellValue(result);

						cell = worksheet.getRow(10).getCell(2);// 8-Date
						regex = "Date  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(11).getCell(2);// 9-NIC Integrator
						regex = "NIC Integrator";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(12).getCell(2);// 10-PTN
						regex = "PTN";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(13).getCell(2);// 11-CallTest Result
						regex = "CallTest Result";
						
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(15).getCell(2);// 12-WAS 911 DONE
						regex = "WAS 911 DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(16).getCell(2);// 13-WAS NEA TESTING DONE
						regex = "WAS NEA TESTING DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(17).getCell(2);// 14-FE NAME
						regex = "FE NAME";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(18).getCell(2);// 15-FE PHONE No
						regex = "FE PHONE No";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(19).getCell(2);// 16-SOW
						regex = "SOW";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(26).getCell(2);// 17-EAMS Activity ID
						regex = "EAMS Activity ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(27).getCell(2);// 18-Implementation Manager
						regex = "Implementation Manager";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						// Extra Log Information from 1-16 col END

						cell = worksheet.getRow(29).getCell(2);// CELL STATUS
						search_cmd_start = "hget EUtranCellFDD \\(administrativeState\\|cellBarred\\|operationalState\\|partOfSectorPower\\|primaryPlmnReserved\\)";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						
						cell.setCellValue(result);

						cell = worksheet.getRow(35).getCell(2);// ALARMS
						search_cmd_start = "alt";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(38).getCell(2);// RSSI
						search_cmd_start = "pmr -r 4 \\| grep 'Int_RadioRecInterference";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(44).getCell(2);// VSWR
						search_cmd_start = "lh ru fui get vswr 1";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						String secondcmd = "lh ru fui get vswr 2";
						String totalresult = (result + "\n" + codToolUtil.searchLogLTE(inputDir, fname, secondcmd, search_cmd_end));
						cell.setCellValue(totalresult);

						cell = worksheet.getRow(51).getCell(2);// RET
						search_cmd_start = "st ret";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);

						String secondcmdret = "hget retsub userlabel\\|iuantBaseStationId";
						String secondResult = codToolUtil.searchLogLTE(inputDir, fname,secondcmdret, search_cmd_end);
						String totalresultRET = (result + "\n" + secondResult);
						cell.setCellValue(totalresultRET);

						cell = worksheet.getRow(54).getCell(2);// TMA
						search_cmd_start = "st tma";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						//
						cell = worksheet.getRow(57).getCell(2);// Relation not applied for LTE
						cell.setCellValue("Not Applied For LTE");

						cell = worksheet.getRow(61).getCell(2);// MISC
						search_cmd_start = "hget sectorcar reserved";
						String regex2, regex3, regex4, regex5, regex6;
						regex2 = "cabx";
						regex3 = "hget sector tx\\|rx";
						regex4 = "hget sector power";
						regex5 = "hget rfb aup\\|res";
						regex6 = "pst";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						String result2 = codToolUtil.searchLogLTE(inputDir, fname, regex2,search_cmd_end);
						String result3 = codToolUtil.searchLogLTE(inputDir, fname, regex3,search_cmd_end);
						String result4 = codToolUtil.searchLogLTE(inputDir, fname, regex4,search_cmd_end);
						String result5 = codToolUtil.searchLogLTE(inputDir, fname, regex5,search_cmd_end);
						String result6 = codToolUtil.searchLogLTE(inputDir, fname, regex6,search_cmd_end);

						cell.setCellValue(result + "\n" + result2 + "\n" + result3 + "\n"+ result4 + "\n" + result5 + "\n" + result6);

						fsIP.close(); // Close the InputStream
						String[] filesplitname = fname.split("\\."); // String array,file name splitting

						String splitfile = filesplitname[0];

						File saveDirectory = new File(outputDir);// Create OutPut Directory
						saveDirectory.mkdir();
						String savefilePath = saveDirectory.getAbsolutePath();
						FileOutputStream output_file = new FileOutputStream(
						new File(savefilePath + dirSeprator + splitfile	+ savedFileExtension)); // Open FileOutputStream to write updates

						wb.write(output_file); // write changes save it like logfilename.xlsm
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- COD Form Saved File"+ savefilePath+ dirSeprator+ splitfile+ savedFileExtension);
						}
						output_file.close(); // close the stream
						
						//call email sender
						//EmailSender emailSender=new EmailSender();
						String host=codToolUtil.getPropValues("host");
						String fromAddress=codToolUtil.getPropValues("mailFrom");
						String toAddress=codToolUtil.getPropValues("CODmailto");
						String ccAddresses=codToolUtil.getPropValues("CODmailcc");
						String bccAddresses=codToolUtil.getPropValues("CODmailbcc");
						String subject=splitfile+new Date().toString();
						String message =codToolUtil.getPropValues("message")+new Date().toString();
						String attachement=savefilePath + dirSeprator+ splitfile + savedFileExtension;
						String[] attachFiles = new String[1];
						attachFiles[0]=attachement;
						try {
							EmailSender.sendEmailWithAttachments(host, fromAddress, toAddress,ccAddresses,bccAddresses,subject, message, attachFiles);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool- Email sent.with attachement For Case 1"+ attachement+ "to"+ toAddress+ "cc"+ ccAddresses+ "bcc"+ bccAddresses);
							}
				          //delete file now 
							codToolUtil.deleteFile(attachement);
							if (logger.isDebugEnabled()) {
								logger.debug("COD Tool- Deleting temporary attachement file ::"+ attachement+ "Deleted");
							}
				        } catch (Exception ex) {
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool- Could not send email.");
							}
							logger.error("CODTool-Thrown Exception while attempting e mail send", ex);
				        }
						//end email sending and file delete.

						// Insert into DB
						con = coddao.getConnection(con);
						fdate = CODToolUtil.getDate();
						coddao.insertRecordIntoCODPROCESSED(fdate, fname, "LTE","Y");
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- Record Inserted into CODPROCESSED Table:::"+ fdate+ fname+ "LTE"+""+"Y");
							logger.debug("CODTool-exited from ["+System.getProperty("user.dir")+"]");
							logger.debug("COD Tool Processing finished");
						}

					}
					//For SL as format and search is different for SL
					
					else if (fname.contains("LTE") && (fname.contains(fileOKString))&& fname.contains("SL")) {
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- CASE 2 LTE ,SL with CallTestY found file is "+ fname);
						}
						// call LTE Process Method
						FileInputStream fsIP = new FileInputStream(	new File(templateCODFile)); // Template COD file
						XSSFWorkbook wb = new XSSFWorkbook(fsIP);
						XSSFSheet worksheet = wb.getSheetAt(0);
						Cell cell = null;

						// Extra Log Information from 1-16 col START

						cell = worksheet.getRow(3).getCell(2);// 1-Node EnodeB
						regex = "NODEB / ENODEB ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(4).getCell(2);// 2-TECHNOLOGY
						regex = "TECHNOLOGY";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(5).getCell(2);// 3-RNC ID
						regex = "RNC ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(6).getCell(2);// 4-Market
						regex = "MARKET";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(7).getCell(2);// 5-OSS
						regex = "OSS";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(8).getCell(2);// 6-New Carrier
						regex = "NEW CARRIER";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(9).getCell(2);// 7-UTRANCELL/EUTRANCELLS OF NEW CARRIER
						regex = "UTRANCELL\\/EUTRANCELLS OF NEW CARRIER";
						regexend="CallTest Result";
						result = codToolUtil.searchLogLTE(inputDir, fname, regex,regexend);
						cell.setCellValue(result);

						cell = worksheet.getRow(10).getCell(2);// 8-Date
						regex = "Date  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(11).getCell(2);// 9-NIC Integrator
						regex = "NIC Integrator";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(12).getCell(2);// 10-PTN
						regex = "PTN";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(13).getCell(2);// 11-CallTest Result
						regex = "CallTest Result";
						
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(15).getCell(2);// 12-WAS 911 DONE
						regex = "WAS 911 DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(16).getCell(2);// 13-WAS NEA TESTING DONE
						regex = "WAS NEA TESTING DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(17).getCell(2);// 14-FE NAME
						regex = "FE NAME";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(18).getCell(2);// 15-FE PHONE No
						regex = "FE PHONE No";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(19).getCell(2);// 16-SOW
						regex = "SOW";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(26).getCell(2);// 17-EAMS Activity ID
						regex = "EAMS Activity ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(27).getCell(2);// 18-Implementation Manager
						regex = "Implementation Manager";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						// Extra Log Information from 1-16 col END

						cell = worksheet.getRow(29).getCell(2);// CELL STATUS
						search_cmd_start ="-> SITE STATUS";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						if (logger.isDebugEnabled()) {
							logger
								.debug("CODTool- SITE STATUS IS" + result);
						}
						cell.setCellValue(result);

						cell = worksheet.getRow(35).getCell(2);// ALARMS
						search_cmd_start = "-> ALARMS";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(38).getCell(2);// RSSI
						search_cmd_start = "-> RSSI";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(44).getCell(2);// VSWR
						search_cmd_start = "-> VSWR 1";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						String secondcmd = "-> VSWR 2";
						String totalresult = (result + "\n" + codToolUtil.searchLogLTE(inputDir, fname, secondcmd, search_cmd_end));
						cell.setCellValue(totalresult);

						cell = worksheet.getRow(51).getCell(2);// RET
						search_cmd_start = "-> RET";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(54).getCell(2);// TMA
						search_cmd_start = "-> TMA";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						//
						cell = worksheet.getRow(57).getCell(2);// Relation not applied for LTE
						cell.setCellValue("Not Applied For LTE");

						cell = worksheet.getRow(61).getCell(2);// MISC
						search_cmd_start = "hget sectorcar reserved";
						String regex2, regex3, regex4, regex5, regex6;
						regex2 = "cabx";
						regex3 = "hget sector tx\\|rx";
						regex4 = "hget sector power";
						regex5 = "hget rfb aup\\|res";
						regex6 = "pst";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						String result2 = codToolUtil.searchLogLTE(inputDir, fname, regex2,search_cmd_end);
						String result3 = codToolUtil.searchLogLTE(inputDir, fname, regex3,search_cmd_end);
						String result4 = codToolUtil.searchLogLTE(inputDir, fname, regex4,search_cmd_end);
						String result5 = codToolUtil.searchLogLTE(inputDir, fname, regex5,search_cmd_end);
						String result6 = codToolUtil.searchLogLTE(inputDir, fname, regex6,search_cmd_end);

						cell.setCellValue(result + "\n" + result2 + "\n" + result3 + "\n"+ result4 + "\n" + result5 + "\n" + result6);

						fsIP.close(); // Close the InputStream
						String[] filesplitname = fname.split("\\."); // String array,file name splitting

						String splitfile = filesplitname[0];

						File saveDirectory = new File(outputDir);// Create OutPut Directory
						saveDirectory.mkdir();
						String savefilePath = saveDirectory.getAbsolutePath();
						FileOutputStream output_file = new FileOutputStream(
						new File(savefilePath + dirSeprator + splitfile	+ savedFileExtension)); // Open FileOutputStream to write updates

						wb.write(output_file); // write changes save it like logfilename.xlsm
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- COD Form Saved File"
								+ savefilePath
								+ dirSeprator
								+ splitfile
								+ savedFileExtension);
						}
						output_file.close(); // close the stream
						
						//call email sender
						//EmailSender emailSender=new EmailSender();
						String host=codToolUtil.getPropValues("host");
						String fromAddress=codToolUtil.getPropValues("mailFrom");
						String toAddress=codToolUtil.getPropValues("CODmailto");
						String ccAddresses=codToolUtil.getPropValues("CODmailcc");
						String bccAddresses=codToolUtil.getPropValues("CODmailbcc");
						String subject=splitfile+new Date().toString();
						String message =codToolUtil.getPropValues("message")+new Date().toString();
						String attachement=savefilePath + dirSeprator+ splitfile + savedFileExtension;
						String[] attachFiles = new String[1];
						attachFiles[0]=attachement;
						try {
							EmailSender.sendEmailWithAttachments(host, fromAddress, toAddress,ccAddresses,bccAddresses,subject, message, attachFiles);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool- Email sent.with attachement For Case 1"+ attachement+ "to"+ toAddress+ "cc"+ ccAddresses+ "bcc"+ bccAddresses);
							}
				          //delete file now 
							codToolUtil.deleteFile(attachement);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool)"
									+ attachement
									+ "Deleted");
							}
				        } catch (Exception ex) {
							if (logger.isDebugEnabled()) {
								logger
									.debug("CODTool- Could not send email.");
							}
							logger.error("CODTool)", ex);
				        }
						//end email sending and file delete.

						// Insert into DB
						con = coddao.getConnection(con);
						fdate = CODToolUtil.getDate();
						coddao.insertRecordIntoCODPROCESSED(fdate, fname, "LTE","Y");
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- Record Inserted into CODPROCESSED Table:::"+ fdate+ fname+ "LTE"+""+"Y");
							logger.debug("COD Tool Processing finished");
							logger.debug("CODTool-exited from ["+System.getProperty("user.dir")+"]");
						}

					} 
						
						
						
				//end SL		
					
					// Case-3
					else if (fname.contains("WCDMA") && (fname.contains(fileOKString))&& fname.contains("Manual") ) {
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- CASE 3 WCDMA ,Manual And CallTestY found"+ fname);
						}
						FileInputStream fsIP = new FileInputStream(
						new File(templateCODFile)); // Template file
						XSSFWorkbook wb = new XSSFWorkbook(fsIP);
						XSSFSheet worksheet = wb.getSheetAt(0);
						Cell cell = null;
						// Get Cell id from file name fname
						String fnameDetail[] = fname.split("\\_");
						String cellId = fnameDetail[2];
						if (logger.isDebugEnabled()) {
							logger
								.debug("CODTool- Fetched Cell Id of WCDMA"
									+ cellId);
						}
						// Extra Log Information from 1-16 col START
						cell = worksheet.getRow(3).getCell(2);// 1-Node EnodeB
						regex = "NODEB / ENODEB ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);
						
						cell = worksheet.getRow(4).getCell(2);// 2-TECHNOLOGY
						regex = "TECHNOLOGY";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(5).getCell(2);// 3-RNC ID
						regex = "RNC ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(6).getCell(2);// 4-Market
						regex = "MARKET";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(7).getCell(2);// 5-OSS
						regex = "OSS";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(8).getCell(2);// 6-New Carrier
						regex = "NEW CARRIER  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(9).getCell(2);// 7-UTRANCELL/EUTRANCELLS OF NEW CARRIER
						regex = "UTRANCELL\\/EUTRANCELLS OF NEW CARRIER";
						regexend="CallTest Result";
						result = codToolUtil.searchLogLTE(inputDir, fname, regex,regexend);
						cell.setCellValue(result);

						cell = worksheet.getRow(10).getCell(2);// 8-Date
						regex = "Date  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(11).getCell(2);// 9-NIC Integrator
						regex = "NIC Integrator";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(12).getCell(2);// 10-PTN
						regex = "PTN";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(13).getCell(2);// 11-CallTest Result
						regex = "CallTest Result";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(15).getCell(2);// 12-WAS 911 DONE
						regex = "WAS 911 DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(16).getCell(2);// 13-WAS NEA TESTING DONE
						regex = "WAS NEA TESTING DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(17).getCell(2);// 14-FE NAME
						regex = "FE NAME";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(18).getCell(2);// 15-FE PHONE No
						regex = "FE PHONE No";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(19).getCell(2);// 16-SOW
						regex = "SOW";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						
						cell = worksheet.getRow(26).getCell(2);// 17-EAMS Activity ID
						regex = "EAMS Activity ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(27).getCell(2);// 18-Implementation Manager
						regex = "Implementation Manager";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);
						// Extra Log Information from 1-16 col END

						cell = worksheet.getRow(29).getCell(2);// CELL STATUS
						search_cmd_start = "hget UtranCell=" + cellId+ " state\\|cellreserved";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(33).getCell(2);// ALARMS
						search_cmd_start = "alt";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(36).getCell(2);// RSSI
						search_cmd_start = "run \\/var\\/opt\\/ericsson\\/nms_umts_wran_bcg\\/files\\/import\\/NIC_WCDMA\\/RSSI_Branch\\/RSSI_Branch_Execute.mos";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(42).getCell(2);// VSWR
						search_cmd_start = "lh ru fui get vswr 1";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						String secondcmd = search_cmd_start = "lh ru fui get vswr 2";
						String totalresult = (result + "\n" + codToolUtil.searchLogLTE(
								inputDir, fname, secondcmd, search_cmd_end));
						cell.setCellValue(totalresult);

						cell = worksheet.getRow(49).getCell(2);// RET
						search_cmd_start = "st ret";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(52).getCell(2);// TMA
						search_cmd_start = "st tma";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						//
						cell = worksheet.getRow(55).getCell(2);// Relation
						search_cmd_start = "lpr UtranCell=" + cellId;
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(59).getCell(2);// MISC
						search_cmd_start = "get cell \\^localcellid";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						String regex1, regex2, regex3, regex4, regex5;
						regex1 = "cabx";
						regex2 = "get \\. maxTotalOutputPower\\$";
						regex3 = "get UtranCell\\=" + cellId + " cid";
						regex4 = "pst";
						regex5 = "lh ru fui get vswr";
						String result1, result2, result3, result4, result5;
						result1 = codToolUtil.searchLogLTE(inputDir, fname, regex1,
								search_cmd_end);
						result2 = codToolUtil.searchLogLTE(inputDir, fname, regex2,
								search_cmd_end);
						result3 = codToolUtil.searchLogLTE(inputDir, fname, regex3,
								search_cmd_end);
						result4 = codToolUtil.searchLogLTE(inputDir, fname, regex4,
								search_cmd_end);
						result5 = codToolUtil.searchLogLTE(inputDir, fname, regex5,
								search_cmd_end);

						cell.setCellValue(result + "\n" + result1 + "\n" + result2 + "\n"
								+ result3 + "\n" + result4 + "\n" + result5);

						fsIP.close(); // Close the InputStream
						String[] filesplitname = fname.split("\\."); // String array,file name splitting
																		

						String splitfile = filesplitname[0];
						File saveDirectory = new File(outputDir);// Create OutPut Directory
						saveDirectory.mkdir();
						String savefilePath = saveDirectory.getAbsolutePath();
						FileOutputStream output_file = new FileOutputStream(
						new File(savefilePath + dirSeprator + splitfile+ savedFileExtension)); // Open FileOutputStream to write updates
						wb.write(output_file); // write changes save it like logfilename.xlsm
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- File Written "
								+ savefilePath
								+ dirSeprator
								+ splitfile
								+ savedFileExtension);
						}
						output_file.close(); // close the stream
						//call email sender
						//EmailSender emailSender=new EmailSender();
						String host=codToolUtil.getPropValues("host");
						String fromAddress=codToolUtil.getPropValues("mailFrom");
						String toAddress=codToolUtil.getPropValues("CODmailto");
						String ccAddresses=codToolUtil.getPropValues("CODmailcc");
						String bccAddresses=codToolUtil.getPropValues("CODmailbcc");
						String subject=splitfile+new Date().toString();
						String message =codToolUtil.getPropValues("message")+new Date().toString();
						String attachement=savefilePath + dirSeprator+ splitfile + savedFileExtension;
						String[] attachFiles = new String[1];
						attachFiles[0]=attachement;
						try {
							EmailSender.sendEmailWithAttachments(host, fromAddress, toAddress,ccAddresses,bccAddresses,subject, message, attachFiles);
							if (logger.isDebugEnabled()) {
								logger
									.debug("CODTool- Email sent.with attachement for Case 2"
										+ attachement
										+ "to"
										+ toAddress
										+ "cc"
										+ ccAddresses
										+ "bcc"
										+ bccAddresses);
							}
				          //delete file now 
							codToolUtil.deleteFile(attachement);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool)"
									+ attachement
									+ "Deleted");
							}
				        } catch (Exception ex) {
							if (logger.isDebugEnabled()) {
								logger
									.debug("CODTool- Could not send email.");
							}
							logger.error("CODTool)", ex);
				        }
						//end email sending and file delete ends.
						
						// Insert to DB
						con = coddao.getConnection(con);
						fdate = CODToolUtil.getDate();
						coddao.insertRecordIntoCODPROCESSED(fdate, fname, "WCDMA","Y");
						logger.debug("CODTool- Record Inserted into CODPROCESSED Table:::"+ fdate+ fname+ "WCDMA"+""+"Y");
						logger.debug("COD Tool Processing finished");
						logger.debug("CODTool-exited from ["+System.getProperty("user.dir")+"]");
					}
					
					//SL for WCDMA
					else if (fname.contains("WCDMA") && (fname.contains(fileOKString))&& fname.contains("SL") ) {
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- CASE 4 WCDMA,SL And CallTestY found"+ fname);
						}
						FileInputStream fsIP = new FileInputStream(
						new File(templateCODFile)); // Template file
						XSSFWorkbook wb = new XSSFWorkbook(fsIP);
						XSSFSheet worksheet = wb.getSheetAt(0);
						Cell cell = null;
						// Get Cell id from file name fname
						String fnameDetail[] = fname.split("\\_");
						String cellId = fnameDetail[2];
						if (logger.isDebugEnabled()) {
							logger
								.debug("CODTool- Fetched Cell Id of WCDMA:::"
									+ cellId);
						}
						// Extra Log Information from 1-16 col START
						cell = worksheet.getRow(3).getCell(2);// 1-Node EnodeB
						regex = "NODEB / ENODEB ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);
						
						cell = worksheet.getRow(4).getCell(2);// 2-TECHNOLOGY
						regex = "TECHNOLOGY";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(5).getCell(2);// 3-RNC ID
						regex = "RNC ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(6).getCell(2);// 4-Market
						regex = "MARKET";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(7).getCell(2);// 5-OSS
						regex = "OSS";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(8).getCell(2);// 6-New Carrier
						regex = "NEW CARRIER  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(9).getCell(2);// 7-UTRANCELL/EUTRANCELLS OF NEW CARRIER
						regex = "UTRANCELL\\/EUTRANCELLS OF NEW CARRIER";
						regexend="CallTest Result";
						result = codToolUtil.searchLogLTE(inputDir, fname, regex,regexend);
						cell.setCellValue(result);

						cell = worksheet.getRow(10).getCell(2);// 8-Date
						regex = "Date  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(11).getCell(2);// 9-NIC Integrator
						regex = "NIC Integrator";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(12).getCell(2);// 10-PTN
						regex = "PTN";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(13).getCell(2);// 11-CallTest Result
						regex = "CallTest Result";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(15).getCell(2);// 12-WAS 911 DONE
						regex = "WAS 911 DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(16).getCell(2);// 13-WAS NEA TESTING DONE
						regex = "WAS NEA TESTING DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(17).getCell(2);// 14-FE NAME
						regex = "FE NAME";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(18).getCell(2);// 15-FE PHONE No
						regex = "FE PHONE No";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(19).getCell(2);// 16-SOW
						regex = "SOW";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						
						cell = worksheet.getRow(26).getCell(2);// 17-EAMS Activity ID
						regex = "EAMS Activity ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(27).getCell(2);// 18-Implementation Manager
						regex = "Implementation Manager";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);
						// Extra Log Information from 1-16 col END

						cell = worksheet.getRow(29).getCell(2);// CELL STATUS
						search_cmd_start = "hget UtranCell=" + cellId+ " state\\|cellreserved";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(33).getCell(2);// ALARMS
						search_cmd_start = "alt";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(36).getCell(2);// RSSI
						search_cmd_start = "run \\/var\\/opt\\/ericsson\\/nms_umts_wran_bcg\\/files\\/import\\/NIC_WCDMA\\/RSSI_Branch\\/RSSI_Branch_Execute.mos";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(42).getCell(2);// VSWR
						search_cmd_start = "lh ru fui get vswr 1";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						String secondcmd = search_cmd_start = "lh ru fui get vswr 2";
						String totalresult = (result + "\n" + codToolUtil.searchLogLTE(
								inputDir, fname, secondcmd, search_cmd_end));
						cell.setCellValue(totalresult);

						cell = worksheet.getRow(49).getCell(2);// RET
						search_cmd_start = "st ret";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(52).getCell(2);// TMA
						search_cmd_start = "st tma";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						//
						cell = worksheet.getRow(55).getCell(2);// Relation
						search_cmd_start = "lpr UtranCell=" + cellId;
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(59).getCell(2);// MISC
						search_cmd_start = "get cell \\^localcellid";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						String regex1, regex2, regex3, regex4, regex5;
						regex1 = "cabx";
						regex2 = "get \\. maxTotalOutputPower\\$";
						regex3 = "get UtranCell\\=" + cellId + " cid";
						regex4 = "pst";
						regex5 = "lh ru fui get vswr";
						String result1, result2, result3, result4, result5;
						result1 = codToolUtil.searchLogLTE(inputDir, fname, regex1,
								search_cmd_end);
						result2 = codToolUtil.searchLogLTE(inputDir, fname, regex2,
								search_cmd_end);
						result3 = codToolUtil.searchLogLTE(inputDir, fname, regex3,
								search_cmd_end);
						result4 = codToolUtil.searchLogLTE(inputDir, fname, regex4,
								search_cmd_end);
						result5 = codToolUtil.searchLogLTE(inputDir, fname, regex5,
								search_cmd_end);

						cell.setCellValue(result + "\n" + result1 + "\n" + result2 + "\n"
								+ result3 + "\n" + result4 + "\n" + result5);

						fsIP.close(); // Close the InputStream
						String[] filesplitname = fname.split("\\."); // String array,file name splitting
																		

						String splitfile = filesplitname[0];
						File saveDirectory = new File(outputDir);// Create OutPut Directory
						saveDirectory.mkdir();
						String savefilePath = saveDirectory.getAbsolutePath();
						FileOutputStream output_file = new FileOutputStream(
						new File(savefilePath + dirSeprator + splitfile+ savedFileExtension)); // Open FileOutputStream to write updates
						wb.write(output_file); // write changes save it like logfilename.xlsm
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- File Written "
								+ savefilePath
								+ dirSeprator
								+ splitfile
								+ savedFileExtension);
						}
						output_file.close(); // close the stream
						//call email sender
						//EmailSender emailSender=new EmailSender();
						String host=codToolUtil.getPropValues("host");
						String fromAddress=codToolUtil.getPropValues("mailFrom");
						String toAddress=codToolUtil.getPropValues("CODmailto");
						String ccAddresses=codToolUtil.getPropValues("CODmailcc");
						String bccAddresses=codToolUtil.getPropValues("CODmailbcc");
						String subject=splitfile+new Date().toString();
						String message =codToolUtil.getPropValues("message")+new Date().toString();
						String attachement=savefilePath + dirSeprator+ splitfile + savedFileExtension;
						String[] attachFiles = new String[1];
						attachFiles[0]=attachement;
						try {
							EmailSender.sendEmailWithAttachments(host, fromAddress, toAddress,ccAddresses,bccAddresses,subject, message, attachFiles);
							if (logger.isDebugEnabled()) {
								logger
									.debug("CODTool- Email sent.with attachement for Case 2"
										+ attachement
										+ "to"
										+ toAddress
										+ "cc"
										+ ccAddresses
										+ "bcc"
										+ bccAddresses);
							}
				          //delete file now 
							codToolUtil.deleteFile(attachement);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool)"
									+ attachement
									+ "Deleted");
							}
				        } catch (Exception ex) {
							if (logger.isDebugEnabled()) {
								logger
									.debug("CODTool- Could not send email.");
							}
							logger.error("CODTool)", ex);
				        }
						//end email sending and file delete ends.
						
						// Insert to DB
						con = coddao.getConnection(con);
						fdate = CODToolUtil.getDate();
						coddao.insertRecordIntoCODPROCESSED(fdate, fname, "WCDMA","Y");
						logger.debug("CODTool- Record Inserted into CODPROCESSED Table:::"+ fdate+ fname+ "WCDMA"+""+"Y");
						logger.debug("COD Tool Processing finished");
						logger.debug("CODTool-exited from ["+System.getProperty("user.dir")+"]");
					}
					//SL end for WCDMA
					// Case-5 LTE for Call Test No.
					else if (fname.contains("LTE") && (fname.contains(fileNOTOKString))&& fname.contains("Manual")){
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- CASE 5  LTE , Manual with Call Test N  found"+fname);
						}
						// call LTE Process Method
						FileInputStream fsIP = new FileInputStream(
						new File(templateCODFile)); // Template file
						XSSFWorkbook wb = new XSSFWorkbook(fsIP);
						XSSFSheet worksheet = wb.getSheetAt(0);
						Cell cell = null;

						// Extra Log Information from 1-16 col START

						cell = worksheet.getRow(3).getCell(2);// 1-Node EnodeB
						regex = "NODEB / ENODEB ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(4).getCell(2);// 2-TECHNOLOGY
						regex = "TECHNOLOGY";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(5).getCell(2);// 3-RNC ID
						regex = "RNC ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(6).getCell(2);// 4-Market
						regex = "MARKET";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(7).getCell(2);// 5-OSS
						regex = "OSS";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(8).getCell(2);// 6-New Carrier
						regex = "NEW CARRIER";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(9).getCell(2);// 7-UTRANCELL/EUTRANCELLS OF NEW CARRIER
						regex = "UTRANCELL\\/EUTRANCELLS OF NEW CARRIER";
						regexend="CallTest Result";
						result = codToolUtil.searchLogLTE(inputDir, fname, regex,regexend);
						cell.setCellValue(result);

						cell = worksheet.getRow(10).getCell(2);// 8-Date
						regex = "Date  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(11).getCell(2);// 9-NIC Integrator
						regex = "NIC Integrator";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(12).getCell(2);// 10-PTN
						regex = "PTN";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(13).getCell(2);// 11-CallTest Result
						regex = "CallTest Result";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(15).getCell(2);// 12-WAS 911 DONE
						regex = "WAS 911 DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(16).getCell(2);// 13-WAS NEA TESTING DONE
						regex = "WAS NEA TESTING DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(17).getCell(2);// 14-FE NAME
						regex = "FE NAME";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(18).getCell(2);// 15-FE PHONE No
						regex = "FE PHONE No";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(19).getCell(2);// 16-SOW
						regex = "SOW";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(26).getCell(2);// 17-EAMS Activity ID
						regex = "EAMS Activity ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(27).getCell(2);// 18-Implementation Manager
						regex = "Implementation Manager";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						// Extra Log Information from 1-16 col END

						cell = worksheet.getRow(29).getCell(2);// CELL STATUS
						search_cmd_start = "hget EUtranCellFDD \\(administrativeState\\|cellBarred\\|operationalState\\|partOfSectorPower\\|primaryPlmnReserved\\)";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- RESULT IS" + result);
						}
						cell.setCellValue(result);

						cell = worksheet.getRow(35).getCell(2);// ALARMS
						search_cmd_start = "alt";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(38).getCell(2);// RSSI
						search_cmd_start = "pmr -r 4 \\| grep 'Int_RadioRecInterference";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(44).getCell(2);// VSWR
						search_cmd_start = "lh ru fui get vswr 1";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						String secondcmd = "lh ru fui get vswr 2";
						String totalresult = (result + "\n" + codToolUtil.searchLogLTE(
								inputDir, fname, secondcmd, search_cmd_end));
						cell.setCellValue(totalresult);

						cell = worksheet.getRow(51).getCell(2);// RET
						search_cmd_start = "st ret";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);

						String secondcmdret = "hget retsub userlabel\\|iuantBaseStationId";
						String secondResult = codToolUtil.searchLogLTE(inputDir, fname,
								secondcmdret, search_cmd_end);
						String totalresultRET = (result + "\n" + secondResult);
						cell.setCellValue(totalresultRET);

						cell = worksheet.getRow(54).getCell(2);// TMA
						search_cmd_start = "st tma";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						//
						cell = worksheet.getRow(57).getCell(2);// Relation not applied for
																// LTE
						cell.setCellValue("Not Applied For LTE");

						cell = worksheet.getRow(61).getCell(2);// MISC
						search_cmd_start = "hget sectorcar reserved";
						String regex2, regex3, regex4, regex5, regex6;
						regex2 = "cabx";
						regex3 = "hget sector tx\\|rx";
						regex4 = "hget sector power";
						regex5 = "hget rfb aup\\|res";
						regex6 = "pst";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						String result2 = codToolUtil.searchLogLTE(inputDir, fname, regex2,
								search_cmd_end);
						String result3 = codToolUtil.searchLogLTE(inputDir, fname, regex3,
								search_cmd_end);
						String result4 = codToolUtil.searchLogLTE(inputDir, fname, regex4,
								search_cmd_end);
						String result5 = codToolUtil.searchLogLTE(inputDir, fname, regex5,
								search_cmd_end);
						String result6 = codToolUtil.searchLogLTE(inputDir, fname, regex6,
								search_cmd_end);

						cell.setCellValue(result + "\n" + result2 + "\n" + result3 + "\n"
								+ result4 + "\n" + result5 + "\n" + result6);

						fsIP.close(); // Close the InputStream
						String[] filesplitname = fname.split("\\."); // String array, each
																		// element is text
																		// between dots

						String splitfile = filesplitname[0];

						File saveDirectory = new File(outputDir);// Create OutPut Directory
						saveDirectory.mkdir();
						String savefilePath = saveDirectory.getAbsolutePath();
						FileOutputStream output_file = new FileOutputStream(
								new File(savefilePath + dirSeprator + splitfile
										+ savedFileExtension)); // Open FileOutputStream to
																// write updates

						wb.write(output_file); // write changes save it like
												// logfilename.xlsm
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- Puneet Saved File"
								+ savefilePath
								+ dirSeprator
								+ splitfile
								+ savedFileExtension);
						}

						output_file.close(); // close the stream
						//call email sender
						//EmailSender emailSender=new EmailSender();
						String host=codToolUtil.getPropValues("host");
						String fromAddress=codToolUtil.getPropValues("mailFrom");
						String toAddress=codToolUtil.getPropValues("CODmailtoN");
						String ccAddresses=codToolUtil.getPropValues("CODmailccN");
						String bccAddresses=codToolUtil.getPropValues("CODmailbccN");
						String subject=splitfile+new Date().toString();
						String message =codToolUtil.getPropValues("message")+new Date().toString();
						String attachement=savefilePath + dirSeprator+ splitfile + savedFileExtension;
						String[] attachFiles = new String[1];
						attachFiles[0]=attachement;
						try {
							EmailSender.sendEmailWithAttachments(host, fromAddress, toAddress,ccAddresses,bccAddresses,subject, message, attachFiles);
							if (logger.isDebugEnabled()) {
								logger
									.debug("CODTool- Email sent.with attachement for Case 3"
										+ attachement
										+ "to"
										+ toAddress
										+ "cc"
										+ ccAddresses
										+ "bcc"
										+ bccAddresses);
							}
				          //delete file now 
							codToolUtil.deleteFile(attachement);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool)"
									+ attachement
									+ "Deleted");
							}
				        } catch (Exception ex) {
							if (logger.isDebugEnabled()) {
								logger
									.debug("CODTool- Could not send email to."
										+ toAddress
										+ "with attachement"
										+ attachement
										+ "at"
										+ new Date().toString());
							}
							logger.error("CODTool)", ex);
				        }
						//end email sending and file delete ends.
						// Insert into DB
						con = coddao.getConnection(con);
						fdate = CODToolUtil.getDate();
						coddao.insertRecordIntoCODPROCESSED(fdate, fname, "LTE","N");
						logger.debug("CODTool- Record Inserted into CODPROCESSED Table:::"+ fdate+ fname+ "LTE"+""+"N");
						logger.debug("COD Tool Processing finished");
						logger.debug("CODTool-exited from ["+System.getProperty("user.dir")+"]");

					}
					
					//FOR LTE SL CALL TEST NO
					else if (fname.contains("LTE") && (fname.contains(fileNOTOKString))&& fname.contains("SL")) {

						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- CASE 6 LTE ,SL with CallTestY found file is "+ fname);
						}
						// call LTE Process Method
						FileInputStream fsIP = new FileInputStream(	new File(templateCODFile)); // Template COD file
						XSSFWorkbook wb = new XSSFWorkbook(fsIP);
						XSSFSheet worksheet = wb.getSheetAt(0);
						Cell cell = null;

						// Extra Log Information from 1-16 col START

						cell = worksheet.getRow(3).getCell(2);// 1-Node EnodeB
						regex = "NODEB / ENODEB ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(4).getCell(2);// 2-TECHNOLOGY
						regex = "TECHNOLOGY";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(5).getCell(2);// 3-RNC ID
						regex = "RNC ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(6).getCell(2);// 4-Market
						regex = "MARKET";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(7).getCell(2);// 5-OSS
						regex = "OSS";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(8).getCell(2);// 6-New Carrier
						regex = "NEW CARRIER";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(9).getCell(2);// 7-UTRANCELL/EUTRANCELLS OF NEW CARRIER
						regex = "UTRANCELL\\/EUTRANCELLS OF NEW CARRIER";
						regexend="CallTest Result";
						result = codToolUtil.searchLogLTE(inputDir, fname, regex,regexend);
						cell.setCellValue(result);

						cell = worksheet.getRow(10).getCell(2);// 8-Date
						regex = "Date  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(11).getCell(2);// 9-NIC Integrator
						regex = "NIC Integrator";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(12).getCell(2);// 10-PTN
						regex = "PTN";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(13).getCell(2);// 11-CallTest Result
						regex = "CallTest Result";
						
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(15).getCell(2);// 12-WAS 911 DONE
						regex = "WAS 911 DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(16).getCell(2);// 13-WAS NEA TESTING DONE
						regex = "WAS NEA TESTING DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(17).getCell(2);// 14-FE NAME
						regex = "FE NAME";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(18).getCell(2);// 15-FE PHONE No
						regex = "FE PHONE No";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(19).getCell(2);// 16-SOW
						regex = "SOW";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(26).getCell(2);// 17-EAMS Activity ID
						regex = "EAMS Activity ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(27).getCell(2);// 18-Implementation Manager
						regex = "Implementation Manager";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						// Extra Log Information from 1-16 col END

						cell = worksheet.getRow(29).getCell(2);// CELL STATUS
						search_cmd_start ="-> SITE STATUS";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						if (logger.isDebugEnabled()) {
							logger
								.debug("CODTool- SITE STATUS IS" + result);
						}
						cell.setCellValue(result);

						cell = worksheet.getRow(35).getCell(2);// ALARMS
						search_cmd_start = "-> ALARMS";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(38).getCell(2);// RSSI
						search_cmd_start = "-> RSSI";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(44).getCell(2);// VSWR
						search_cmd_start = "-> VSWR 1";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						String secondcmd = "-> VSWR 2";
						String totalresult = (result + "\n" + codToolUtil.searchLogLTE(inputDir, fname, secondcmd, search_cmd_end));
						cell.setCellValue(totalresult);

						cell = worksheet.getRow(51).getCell(2);// RET
						search_cmd_start = "-> RET";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(54).getCell(2);// TMA
						search_cmd_start = "-> TMA";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						//
						cell = worksheet.getRow(57).getCell(2);// Relation not applied for LTE
						cell.setCellValue("Not Applied For LTE");

						cell = worksheet.getRow(61).getCell(2);// MISC
						search_cmd_start = "hget sectorcar reserved";
						String regex2, regex3, regex4, regex5, regex6;
						regex2 = "cabx";
						regex3 = "hget sector tx\\|rx";
						regex4 = "hget sector power";
						regex5 = "hget rfb aup\\|res";
						regex6 = "pst";
						search_cmd_end = "smart_laptop_command_finished";
						result = codToolUtil.searchLogLTE(inputDir, fname,search_cmd_start, search_cmd_end);
						String result2 = codToolUtil.searchLogLTE(inputDir, fname, regex2,search_cmd_end);
						String result3 = codToolUtil.searchLogLTE(inputDir, fname, regex3,search_cmd_end);
						String result4 = codToolUtil.searchLogLTE(inputDir, fname, regex4,search_cmd_end);
						String result5 = codToolUtil.searchLogLTE(inputDir, fname, regex5,search_cmd_end);
						String result6 = codToolUtil.searchLogLTE(inputDir, fname, regex6,search_cmd_end);

						cell.setCellValue(result + "\n" + result2 + "\n" + result3 + "\n"+ result4 + "\n" + result5 + "\n" + result6);

						fsIP.close(); // Close the InputStream
						String[] filesplitname = fname.split("\\."); // String array,file name splitting

						String splitfile = filesplitname[0];

						File saveDirectory = new File(outputDir);// Create OutPut Directory
						saveDirectory.mkdir();
						String savefilePath = saveDirectory.getAbsolutePath();
						FileOutputStream output_file = new FileOutputStream(
						new File(savefilePath + dirSeprator + splitfile	+ savedFileExtension)); // Open FileOutputStream to write updates

						wb.write(output_file); // write changes save it like logfilename.xlsm
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- COD Form Saved File"
								+ savefilePath
								+ dirSeprator
								+ splitfile
								+ savedFileExtension);
						}
						output_file.close(); // close the stream
						
						//call email sender
						//EmailSender emailSender=new EmailSender();
						String host=codToolUtil.getPropValues("host");
						String fromAddress=codToolUtil.getPropValues("mailFrom");
						String toAddress=codToolUtil.getPropValues("CODmailto");
						String ccAddresses=codToolUtil.getPropValues("CODmailcc");
						String bccAddresses=codToolUtil.getPropValues("CODmailbcc");
						String subject=splitfile+new Date().toString();
						String message =codToolUtil.getPropValues("message")+new Date().toString();
						String attachement=savefilePath + dirSeprator+ splitfile + savedFileExtension;
						String[] attachFiles = new String[1];
						attachFiles[0]=attachement;
						try {
							EmailSender.sendEmailWithAttachments(host, fromAddress, toAddress,ccAddresses,bccAddresses,subject, message, attachFiles);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool- Email sent.with attachement For Case 1"+ attachement+ "to"+ toAddress+ "cc"+ ccAddresses+ "bcc"+ bccAddresses);
							}
				          //delete file now 
							codToolUtil.deleteFile(attachement);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool)"
									+ attachement
									+ "Deleted");
							}
				        } catch (Exception ex) {
							if (logger.isDebugEnabled()) {
								logger
									.debug("CODTool- Could not send email.");
							}
							logger.error("CODTool)", ex);
				        }
						//end email sending and file delete.

						// Insert into DB
						con = coddao.getConnection(con);
						fdate = CODToolUtil.getDate();
						coddao.insertRecordIntoCODPROCESSED(fdate, fname, "LTE","Y");
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- Record Inserted into CODPROCESSED Table:::"+ fdate+ fname+ "LTE"+""+"Y");
							logger.debug("COD Tool Processing finished");
							logger.debug("CODTool-exited from ["+System.getProperty("user.dir")+"]");
						}

					
					} 
						
						
						
				//end SL		

					
					//END
					// Case -7 WCDMA for CallTest No
					else if (fname.contains("WCDMA") && (fname.contains(fileNOTOKString))&& fname.contains("Manual") ) {
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- CASE 7 WCDMA ,Manual with CallTestN found"+ fname);
						}

						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- WCDMA found:"+ inputDir+ fname);
						}

						FileInputStream fsIP = new FileInputStream(
								new File(templateCODFile)); // Template file
						XSSFWorkbook wb = new XSSFWorkbook(fsIP);
						XSSFSheet worksheet = wb.getSheetAt(0);
						Cell cell = null;

						// Get Cell id from file name fname
						String fnameDetail[] = fname.split("\\_");
						String cellId = fnameDetail[2];

						// Extra Log Information from 1-16 col START

						cell = worksheet.getRow(3).getCell(2);// 1-Node EnodeB
						regex = "NODEB / ENODEB ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(4).getCell(2);// 2-TECHNOLOGY
						regex = "TECHNOLOGY";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(5).getCell(2);// 3-RNC ID
						regex = "RNC ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(6).getCell(2);// 4-Market
						regex = "MARKET";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(7).getCell(2);// 5-OSS
						regex = "OSS";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(8).getCell(2);// 6-New Carrier
						regex = "NEW CARRIER  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(9).getCell(2);// 7-UTRANCELL/EUTRANCELLS OF NEW CARRIER
						regex = "UTRANCELL\\/EUTRANCELLS OF NEW CARRIER";
						regexend="CallTest Result";
						result = codToolUtil.searchLogLTE(inputDir, fname, regex,regexend);
						cell.setCellValue(result);

						cell = worksheet.getRow(10).getCell(2);// 8-Date
						regex = "Date  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(11).getCell(2);// 9-NIC Integrator
						regex = "NIC Integrator";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(12).getCell(2);// 10-PTN
						regex = "PTN";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(13).getCell(2);// 11-CallTest Result
						regex = "CallTest Result";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(15).getCell(2);// 12-WAS 911 DONE
						regex = "WAS 911 DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(16).getCell(2);// 13-WAS NEA TESTING DONE
						regex = "WAS NEA TESTING DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(17).getCell(2);// 14-FE NAME
						regex = "FE NAME";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(18).getCell(2);// 15-FE PHONE No
						regex = "FE PHONE No";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(19).getCell(2);// 16-SOW
						regex = "SOW";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);
						
						cell = worksheet.getRow(26).getCell(2);// 17-EAMS Activity ID
						regex = "EAMS Activity ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(27).getCell(2);// 18-Implementation Manager
						regex = "Implementation Manager";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						// Extra Log Information from 1-16 col END

						cell = worksheet.getRow(29).getCell(2);// CELL STATUS
						search_cmd_start = "hget UtranCell=" + cellId+ " state\\|cellreserved";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(33).getCell(2);// ALARMS
						search_cmd_start = "alt";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(36).getCell(2);// RSSI
						search_cmd_start = "run \\/var\\/opt\\/ericsson\\/nms_umts_wran_bcg\\/files\\/import\\/NIC_WCDMA\\/RSSI_Branch\\/RSSI_Branch_Execute.mos";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(42).getCell(2);// VSWR
						search_cmd_start = "lh ru fui get vswr 1";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						String secondcmd = search_cmd_start = "lh ru fui get vswr 2";
						String totalresult = (result + "\n" + codToolUtil.searchLogLTE(
								inputDir, fname, secondcmd, search_cmd_end));
						cell.setCellValue(totalresult);

						cell = worksheet.getRow(49).getCell(2);// RET
						search_cmd_start = "st ret";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(52).getCell(2);// TMA
						search_cmd_start = "st tma";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						//
						cell = worksheet.getRow(55).getCell(2);// Relation
						search_cmd_start = "lpr UtranCell=" + cellId;
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(59).getCell(2);// MISC
						search_cmd_start = "get cell \\^localcellid";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						String regex1, regex2, regex3, regex4, regex5;
						regex1 = "cabx";
						regex2 = "get \\. maxTotalOutputPower\\$";
						regex3 = "get UtranCell\\=" + cellId + " cid";
						regex4 = "pst";
						regex5 = "lh ru fui get vswr";
						String result1, result2, result3, result4, result5;
						result1 = codToolUtil.searchLogLTE(inputDir, fname, regex1,
								search_cmd_end);
						result2 = codToolUtil.searchLogLTE(inputDir, fname, regex2,
								search_cmd_end);
						result3 = codToolUtil.searchLogLTE(inputDir, fname, regex3,
								search_cmd_end);
						result4 = codToolUtil.searchLogLTE(inputDir, fname, regex4,
								search_cmd_end);
						result5 = codToolUtil.searchLogLTE(inputDir, fname, regex5,
								search_cmd_end);

						cell.setCellValue(result + "\n" + result1 + "\n" + result2 + "\n"
								+ result3 + "\n" + result4 + "\n" + result5);

						fsIP.close(); // Close the InputStream
						String[] filesplitname = fname.split("\\."); // String array, each
																		// element is text
																		// between dots

						String splitfile = filesplitname[0];

						File saveDirectory = new File(outputDir);// Create OutPut Directory
						saveDirectory.mkdir();
						String savefilePath = saveDirectory.getAbsolutePath();
						FileOutputStream output_file = new FileOutputStream(
								new File(savefilePath + dirSeprator + splitfile
										+ savedFileExtension)); // Open FileOutputStream to
																// write updates

						wb.write(output_file); // write changes save it like
												// logfilename.xlsm
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- File Written "+ savefilePath+ dirSeprator+ splitfile+ savedFileExtension);
						}

						output_file.close(); // close the stream
						//call email sender
						//EmailSender emailSender=new EmailSender();
						String host=codToolUtil.getPropValues("host");
						String fromAddress=codToolUtil.getPropValues("mailFrom");
						String toAddress=codToolUtil.getPropValues("CODmailtoN");
						String ccAddresses=codToolUtil.getPropValues("CODmailccN");
						String bccAddresses=codToolUtil.getPropValues("CODmailbccN");
						String subject=splitfile+new Date().toString();
						String message =codToolUtil.getPropValues("message")+new Date().toString();
						String attachement=savefilePath + dirSeprator+ splitfile + savedFileExtension;
						String[] attachFiles = new String[1];
						attachFiles[0]=attachement;
						try {
							EmailSender.sendEmailWithAttachments(host, fromAddress, toAddress,ccAddresses,bccAddresses,subject, message, attachFiles);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool- Email sent.with attachement for Case 4"+ attachement+ "to"+ toAddress+ "cc"+ ccAddresses+ "bcc"+ bccAddresses);
							}
				          //delete file now 
							codToolUtil.deleteFile(attachement);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool)"
									+ attachement
									+ "Deleted");
							}
				        } catch (Exception ex) {
							if (logger.isDebugEnabled()) {
								logger
									.debug("CODTool- Could not send email.");
							}
							logger.error("CODTool)", ex);
				        }
						//end email sending and file delete ends.
						
						// Insert to DB
						con = coddao.getConnection(con);
						fdate = CODToolUtil.getDate();
						coddao.insertRecordIntoCODPROCESSED(fdate, fname, "WCDMA","N");
						logger.debug("CODTool- Record Inserted into CODPROCESSED Table:::"+ fdate+ fname+ "WCDMA"+""+"N");
						logger.debug("COD Tool Processing finished");
						logger.debug("CODTool-exited from ["+System.getProperty("user.dir")+"]");

					}
				
					//For Last Case 
					else if (fname.contains("WCDMA") && (fname.contains(fileNOTOKString))&& fname.contains("SL") ) {
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- CASE 8 WCDMA ,SL with CallTestN found"+ fname);
						}

						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- WCDMA found:"+ inputDir+ fname);
						}

						FileInputStream fsIP = new FileInputStream(
								new File(templateCODFile)); // Template file
						XSSFWorkbook wb = new XSSFWorkbook(fsIP);
						XSSFSheet worksheet = wb.getSheetAt(0);
						Cell cell = null;

						// Get Cell id from file name fname
						String fnameDetail[] = fname.split("\\_");
						String cellId = fnameDetail[2];

						// Extra Log Information from 1-16 col START

						cell = worksheet.getRow(3).getCell(2);// 1-Node EnodeB
						regex = "NODEB / ENODEB ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(4).getCell(2);// 2-TECHNOLOGY
						regex = "TECHNOLOGY";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(5).getCell(2);// 3-RNC ID
						regex = "RNC ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(6).getCell(2);// 4-Market
						regex = "MARKET";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(7).getCell(2);// 5-OSS
						regex = "OSS";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(8).getCell(2);// 6-New Carrier
						regex = "NEW CARRIER  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(9).getCell(2);// 7-UTRANCELL/EUTRANCELLS OF NEW CARRIER
						regex = "UTRANCELL\\/EUTRANCELLS OF NEW CARRIER";
						regexend="CallTest Result";
						result = codToolUtil.searchLogLTE(inputDir, fname, regex,regexend);
						cell.setCellValue(result);

						cell = worksheet.getRow(10).getCell(2);// 8-Date
						regex = "Date  :";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(11).getCell(2);// 9-NIC Integrator
						regex = "NIC Integrator";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(12).getCell(2);// 10-PTN
						regex = "PTN";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(13).getCell(2);// 11-CallTest Result
						regex = "CallTest Result";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(15).getCell(2);// 12-WAS 911 DONE
						regex = "WAS 911 DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(16).getCell(2);// 13-WAS NEA TESTING DONE
						regex = "WAS NEA TESTING DONE";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(17).getCell(2);// 14-FE NAME
						regex = "FE NAME";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(18).getCell(2);// 15-FE PHONE No
						regex = "FE PHONE No";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(19).getCell(2);// 16-SOW
						regex = "SOW";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);
						
						cell = worksheet.getRow(26).getCell(2);// 17-EAMS Activity ID
						regex = "EAMS Activity ID";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						cell = worksheet.getRow(27).getCell(2);// 18-Implementation Manager
						regex = "Implementation Manager";
						result = codToolUtil.searchExtraLog(inputDir, fname, regex);
						cell.setCellValue(result);

						// Extra Log Information from 1-16 col END

						cell = worksheet.getRow(29).getCell(2);// CELL STATUS
						search_cmd_start = "hget UtranCell=" + cellId+ " state\\|cellreserved";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(33).getCell(2);// ALARMS
						search_cmd_start = "alt";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(36).getCell(2);// RSSI
						search_cmd_start = "run \\/var\\/opt\\/ericsson\\/nms_umts_wran_bcg\\/files\\/import\\/NIC_WCDMA\\/RSSI_Branch\\/RSSI_Branch_Execute.mos";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(42).getCell(2);// VSWR
						search_cmd_start = "lh ru fui get vswr 1";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						String secondcmd = search_cmd_start = "lh ru fui get vswr 2";
						String totalresult = (result + "\n" + codToolUtil.searchLogLTE(
								inputDir, fname, secondcmd, search_cmd_end));
						cell.setCellValue(totalresult);

						cell = worksheet.getRow(49).getCell(2);// RET
						search_cmd_start = "st ret";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(52).getCell(2);// TMA
						search_cmd_start = "st tma";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						//
						cell = worksheet.getRow(55).getCell(2);// Relation
						search_cmd_start = "lpr UtranCell=" + cellId;
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						cell.setCellValue(result);

						cell = worksheet.getRow(59).getCell(2);// MISC
						search_cmd_start = "get cell \\^localcellid";
						search_cmd_end = ">";
						result = codToolUtil.searchLogLTE(inputDir, fname,
								search_cmd_start, search_cmd_end);
						String regex1, regex2, regex3, regex4, regex5;
						regex1 = "cabx";
						regex2 = "get \\. maxTotalOutputPower\\$";
						regex3 = "get UtranCell\\=" + cellId + " cid";
						regex4 = "pst";
						regex5 = "lh ru fui get vswr";
						String result1, result2, result3, result4, result5;
						result1 = codToolUtil.searchLogLTE(inputDir, fname, regex1,
								search_cmd_end);
						result2 = codToolUtil.searchLogLTE(inputDir, fname, regex2,
								search_cmd_end);
						result3 = codToolUtil.searchLogLTE(inputDir, fname, regex3,
								search_cmd_end);
						result4 = codToolUtil.searchLogLTE(inputDir, fname, regex4,
								search_cmd_end);
						result5 = codToolUtil.searchLogLTE(inputDir, fname, regex5,
								search_cmd_end);

						cell.setCellValue(result + "\n" + result1 + "\n" + result2 + "\n"
								+ result3 + "\n" + result4 + "\n" + result5);

						fsIP.close(); // Close the InputStream
						String[] filesplitname = fname.split("\\."); // String array, each
																		// element is text
																		// between dots

						String splitfile = filesplitname[0];

						File saveDirectory = new File(outputDir);// Create OutPut Directory
						saveDirectory.mkdir();
						String savefilePath = saveDirectory.getAbsolutePath();
						FileOutputStream output_file = new FileOutputStream(
								new File(savefilePath + dirSeprator + splitfile
										+ savedFileExtension)); // Open FileOutputStream to
																// write updates

						wb.write(output_file); // write changes save it like
												// logfilename.xlsm
						if (logger.isDebugEnabled()) {
							logger.debug("CODTool- File Written "+ savefilePath+ dirSeprator+ splitfile+ savedFileExtension);
						}

						output_file.close(); // close the stream
						//call email sender
						//EmailSender emailSender=new EmailSender();
						String host=codToolUtil.getPropValues("host");
						String fromAddress=codToolUtil.getPropValues("mailFrom");
						String toAddress=codToolUtil.getPropValues("CODmailtoN");
						String ccAddresses=codToolUtil.getPropValues("CODmailccN");
						String bccAddresses=codToolUtil.getPropValues("CODmailbccN");
						String subject=splitfile+new Date().toString();
						String message =codToolUtil.getPropValues("message")+new Date().toString();
						String attachement=savefilePath + dirSeprator+ splitfile + savedFileExtension;
						String[] attachFiles = new String[1];
						attachFiles[0]=attachement;
						try {
							EmailSender.sendEmailWithAttachments(host, fromAddress, toAddress,ccAddresses,bccAddresses,subject, message, attachFiles);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool- Email sent.with attachement for Case 4"+ attachement+ "to"+ toAddress+ "cc"+ ccAddresses	+ "bcc"+ bccAddresses);
							}
				          //delete file now 
							codToolUtil.deleteFile(attachement);
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool)"+ attachement+ "Deleted");
							}
				        } catch (Exception ex) {
							if (logger.isDebugEnabled()) {
								logger.debug("CODTool- Could not send email.");
							}
							logger.error("CODTool)", ex);
				        }
						//end email sending and file delete ends.
						
						// Insert to DB
						con = coddao.getConnection(con);
						fdate = CODToolUtil.getDate();
						coddao.insertRecordIntoCODPROCESSED(fdate, fname, "WCDMA","N");
						logger.debug("CODTool- Record Inserted into CODPROCESSED Table:::"+ fdate+ fname+ "WCDMA"+""+"N");
						logger.debug("COD Tool Processing finished");
						logger.debug("CODTool-exited from ["+System.getProperty("user.dir")+"]");

					}

					//End
				}
				
				else
				{
					logger.debug("File Date:: "+modifiedfiledate+" not matched with Tool Date: "+currenttooldate+" hence Skipping");
					logger.debug("CODTool-exited from ["+System.getProperty("user.dir")+"]");
				}
			
			}

			else
				if (logger.isDebugEnabled()) {
					logger.debug("CODTool - Not a valid file :"+ fname+ " , Skipping");
					logger.debug("CODTool-exited from ["+System.getProperty("user.dir")+"]");
				}
			
		}
			}
}
