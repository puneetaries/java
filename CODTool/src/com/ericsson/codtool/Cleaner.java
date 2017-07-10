package com.ericsson.codtool;

import org.apache.log4j.Logger;

import java.sql.SQLException;
/**
 * @author Puneet Raj Srivastava
 * Ericsson India Pvt Ltd 
 * FEB 2015
 * 
 */


public class Cleaner {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Cleaner.class);
	
	public static void main(String[] args)  {
		String table1,table2;
		table1="CODSTATS";
		table2="CODPROCESSED";
		try {
			if((new CODDAO().truncTable(table1))>0)
			
			{
				if (logger.isDebugEnabled()) {
					logger.debug("Cleaner::::main(String[])"+ table1+ ": Table Truncated & ");
				}
			}
			
			if((new CODDAO().truncTable(table2))>0)
				
			{
				if (logger.isDebugEnabled()) {
					logger.debug("Cleaner ::main(String[])"+ table2+ ": Table Truncated & Cleaner exiting");
				}
			}
			
			else			if (logger.isDebugEnabled()) {
				logger.debug("Cleaner :::::main(String[]) - Could not truncate "+ table1+ table2);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("main(String[])", e);
		}
	}

}
