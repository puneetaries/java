/**
 * 
 */
package com.ericsson.codtool;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * @author Puneet Raj Srivastava
 * Ericsson India Pvt Ltd 
 * FEB 2015
 * 
 */
public class DashBoard {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(DashBoard.class);
	
	public String writeDashBoard() throws IOException, SQLException
	{
		if (logger.isDebugEnabled()) {
			logger.debug("writeDashBoard() - start");
		}

		CODToolUtil codToolUtil = new CODToolUtil();
		// Read property file to initialize constants
		String templateDashBoardFile = codToolUtil.getPropValues("templateDashBoardFile");
		String outputDir = codToolUtil.getPropValues("outputDir");
		String dirSeprator = codToolUtil.getPropValues("dirSeprator");
		String fdate = CODToolUtil.getDate();
		CODDAO coddao=new CODDAO();
		LinkedHashSet<String> hs= new LinkedHashSet<String>();
		LinkedHashSet<String> hs1= new LinkedHashSet<String>();
		FileInputStream fsIP= new FileInputStream(new File(templateDashBoardFile)); //Template file
		XSSFWorkbook wb = new XSSFWorkbook(fsIP);
	    
	    XSSFSheet worksheet = wb.getSheetAt(0);
	    


	    Cell cell = null; 
	    
		cell = worksheet.getRow(1).getCell(0);
	  	cell.setCellValue(CODToolUtil.getDate());//Date
	  	cell = worksheet.getRow(1).getCell(1);
	  	int allfiles=coddao.getAllfiles();
	  	cell.setCellValue(allfiles);//All Files
	  	
	  	cell = worksheet.getRow(1).getCell(2);
	  	int callfilesY=coddao.getAllProcessedfilesCallY();
	  	cell.setCellValue(callfilesY);//All Y Files
	  	
	  	cell = worksheet.getRow(1).getCell(3);
	  	int callfilesN=coddao.getAllProcessedfilesCallN();
	  	cell.setCellValue(callfilesN);//All N Files
	  	
	  	cell = worksheet.getRow(1).getCell(4);
	  	int allLTE=coddao.getAllProcessedfilesLTE();
	  	cell.setCellValue(allLTE);//All LTE Files
	  	
	  	cell = worksheet.getRow(1).getCell(5);
	  	int allWCDMA=coddao.getAllProcessedfilesWCDMA();
	  	cell.setCellValue(allWCDMA);//All WCDMA Files
	  	//Sheet 0 OverView Complete
	  	//Sheet 1 Successfull CT
	  	XSSFSheet worksheet1 = wb.getSheetAt(1);
	  	
	  	hs=coddao.getAllProcessedfilesNameY();
	  	hs1=coddao.getAllProcessedfilesDateY();
	  	Object[] fileNamesArray =  hs.toArray();
	  	Object[] fileDatesArray =  hs1.toArray();
	  	
	  	
	  	
	  	
	  	
	  	/*for(int  i=0;i<fileNamesArray.length;i++)
	  	{
	  		XSSFRow row = worksheet1.createRow(i+1);
            cell = row.createCell(0);
            
            cell.setCellValue(fileNamesArray[i].toString());
            
	  	}
	  	
	  	
	  	
	  	for(int i=0;i<fileDatesArray.length;i++)
	  	{
	  		XSSFRow row = worksheet1.createRow(i+1);
            cell = row.createCell(1);
            cell.setCellValue(fileDatesArray[i].toString());
            
	  	}*/
	  	/*Cell cell1,cell2;
	  	for(int  i=0;i<fileNamesArray.length;i++)
	  	{
	  	    XSSFRow row = worksheet1.createRow(i+1);
	  	    cell1 = row.createCell(0);
	  	    cell1.setCellValue(fileNamesArray[i].toString());

	  	    cell2 = row.createCell(1);
	  	    cell2.setCellValue(fileDatesArray[i].toString());
	  	}*/
	  	for(int  i=0;i<fileNamesArray.length;i++)
	  	{
	  	    XSSFRow row = worksheet1.createRow(i+1);
	  	    cell = row.createCell(0);
	  	    cell.setCellValue(fileNamesArray[i].toString());
	  	    cell = row.createCell(1);
	  	    cell.setCellValue(fileDatesArray[i].toString());
	  	}
	  	//sheet 2 ends .
	  	
	  	
	  	//sheet 3 starts
	  	XSSFSheet worksheet2 = wb.getSheetAt(2);
	  	hs=coddao.getAllProcessedfilesNameN();
	  	hs1=coddao.getAllProcessedfilesDateN();
	  	Object[] fileNamesArrayN =  hs.toArray();
	  	Object[] fileDatesArrayN =  hs1.toArray();
	  
	  	for(int  i=0;i<fileNamesArrayN.length;i++)
	  	{
	  	    XSSFRow row = worksheet2.createRow(i+1);
	  	    cell = row.createCell(0);
	  	    cell.setCellValue(fileNamesArrayN[i].toString());
	  	    cell = row.createCell(1);
	  	    cell.setCellValue(fileDatesArrayN[i].toString());
	  	}

	  	
	  	
	  	//sheet 3 ends
	  	
	  	
	  	
	  	
	  	fsIP.close();
	  	File saveDirectory = new File(outputDir);// Create OutPut Directory
		saveDirectory.mkdir();
		String savefilePath = saveDirectory.getAbsolutePath();
		String savedFile=savefilePath+dirSeprator+templateDashBoardFile;
		FileOutputStream output_file = new FileOutputStream(new File(savedFile)); // save in output
		wb.write(output_file); // write changes save it.
		output_file.close(); // close the stream

		if (logger.isDebugEnabled()) {
			logger.debug("writeDashBoard() - end");
		}
		return savedFile;
	  	
	  	
	  	
		
	}
	
public static void main(String[] args) throws IOException, SQLException {
	if (logger.isDebugEnabled()) {
		logger.debug("main(String[]) - start");
	}

	CODToolUtil codToolUtil=new CODToolUtil();
	
	
	String host=codToolUtil.getPropValues("host");
	String fromAddress=codToolUtil.getPropValues("mailFrom");
	String toAddress=codToolUtil.getPropValues("DashBoardmailto");
	String ccAddresses=codToolUtil.getPropValues("DashBoardmailcc");
	String bccAddresses=codToolUtil.getPropValues("DashBoardmailbcc");
	String subject="DashBoard:"+new Date().toString();
	String message ="DashBoard Report : "+new Date().toString();
	String[] attachFiles = new String[1];
	attachFiles[0]=(new DashBoard().writeDashBoard());;
	
	//EmailSender emailSender=new EmailSender();
	try {
		EmailSender.sendEmailWithAttachments(host, fromAddress, toAddress, ccAddresses, bccAddresses, subject, message, attachFiles);
		if (logger.isDebugEnabled()) {
			logger.debug("main(String[]) - DashBoard Mail Sent"
				+ host
				+ fromAddress
				+ toAddress
				+ ccAddresses
				+ bccAddresses
				+ subject
				+ message
				+ attachFiles
				+ "at"
				+ new Date());
		}
		codToolUtil.deleteFile(attachFiles[0]);
		if (logger.isDebugEnabled()) {
			logger.debug("main(String[])" + attachFiles[0] + "Deleted");
		}
	} catch (AddressException e) {
		logger.error("main(String[])", e);

		if (logger.isDebugEnabled()) {
			logger.debug("main(String[]) - Not Valid e mail Address"
				+ fromAddress
				+ toAddress
				+ ccAddresses
				+ bccAddresses);
		}
		logger.error("main(String[])", e);
	} catch (MessagingException e) {
		logger.error("main(String[])", e);

		if (logger.isDebugEnabled()) {
			logger.debug("main(String[]) - Messeging Exception Occured");
		}
		logger.error("main(String[])", e);
	}

	if (logger.isDebugEnabled()) {
		logger.debug("main(String[]) - end");
	}
	}
}
