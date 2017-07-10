/**
 * 
 */
package com.ericsson.codtool;

import org.apache.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * @author Puneet Raj Srivastava
 * Ericsson India Pvt Ltd 
 * FEB 2015
 * 
 */

public class CODDAO {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CODDAO.class);

	public Connection getConnection(Connection connection)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("CODDAO-Making Connection to CODDb Databse :::getConnection(Connection) - start");
		}

		try {
			// connect method #1 - embedded driver
			String dbURL1 = "jdbc:derby:CODDb";
			connection = DriverManager.getConnection(dbURL1);
			logger.debug("Connected database"+dbURL1);
			
		} catch (SQLException ex) {
			logger.error("getConnection(Connection)", ex);

		logger.error("getConnection(Connection)", ex);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("CODDAO-getConnection(Connection) - end");
		}
		return connection;
	}
		public  void insertRecordIntoCODSTATS(String fdate,String filename) throws SQLException {
			if (logger.isDebugEnabled()) {
				logger
					.debug("insertRecordIntoCODSTATS(String, String) - start");
			}
			 
			PreparedStatement preparedStatement = null;
			Connection con =null;

			String insertTableSQL = "INSERT INTO CODSTATS "
					+ "(FDATE, FILENAME) VALUES"
					+ "(?,?)";
			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-insertRecordIntoCODSTATS(String, String)"
					+ insertTableSQL);
			}
			try {
				CODDAO coddao=new CODDAO();
				con=coddao.getConnection(con);
				preparedStatement = con.prepareStatement(insertTableSQL);
				preparedStatement.setString(1, fdate);
				preparedStatement.setString(2, filename);
				preparedStatement.executeUpdate();

				if (logger.isDebugEnabled()) {
					logger
						.debug("insertRecordIntoCODSTATS(String, String) - Record is inserted into CODSTATS table!");
				}

			} catch (SQLException e) {
				logger.error("insertRecordIntoCODSTATS(String, String)", e);

				if (logger.isDebugEnabled()) {
					logger.debug("CODDAO-insertRecordIntoCODSTATS(String, String)"
						+ e.getMessage());
				}

			} finally {

				if (preparedStatement != null) {
					preparedStatement.close();
				}

				if (con != null) {
					con.close();
					logger.debug("Connection Closed");
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-insertRecordIntoCODSTATS(String, String) - end");
			}
		}

		public  void insertRecordIntoCODPROCESSED(	String fdate,String filename,String technology,String calltested) throws SQLException {
			if (logger.isDebugEnabled()) {
				logger
					.debug("insertRecordIntoCODPROCESSED(String, String, String, String) - start");
			}
			 
			PreparedStatement preparedStatement = null;
			Connection dbConnection=null;

			String insertTableSQL = "INSERT INTO CODPROCESSED "
					+ "(FDATE, FILENAME,TECHNOLOGY,CALLTEST) VALUES"
					+ "(?,?,?,?)";

			try {
				
				CODDAO coddao=new CODDAO();
				dbConnection=coddao.getConnection(dbConnection);
				preparedStatement = dbConnection.prepareStatement(insertTableSQL);

				preparedStatement.setString(1, fdate);
				preparedStatement.setString(2, filename);
				preparedStatement.setString(3, technology);
				preparedStatement.setString(4, calltested);
				
				preparedStatement.executeUpdate();

				if (logger.isDebugEnabled()) {
					logger
						.debug("insertRecordIntoCODPROCESSED(String, String, String, String) - Record is inserted into CODPROCESSED table!");
				}

			} catch (SQLException e) {
				logger
					.error(
						"insertRecordIntoCODPROCESSED(String, String, String, String)",
						e);

				if (logger.isDebugEnabled()) {
					logger
						.debug("insertRecordIntoCODPROCESSED(String, String, String, String)"
							+ e.getMessage());
				}

			} finally {

				if (preparedStatement != null) {
					preparedStatement.close();
				}

				if (dbConnection != null) {
					dbConnection.close();
					logger.debug("Connection Closed");
				}

			}

			if (logger.isDebugEnabled()) {
				logger
					.debug("insertRecordIntoCODPROCESSED(String, String, String, String) - end");
			}
		}

		
		
		
		public  int getAllfiles() throws SQLException {
			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllfiles() - start");
			}
			 
			Statement statement = null;
			String allFileRecordsSQL = "SELECT count (*) FROM CODSTATS ";
			int allfiles=0;
			Connection dbConnection=null;
			

			try {
				CODDAO coddao=new CODDAO();
				dbConnection=coddao.getConnection(dbConnection);
				statement = dbConnection.createStatement();

				ResultSet resultSet=statement.executeQuery(allFileRecordsSQL);
				while(resultSet.next())
				{
					allfiles=resultSet.getInt(1);
				}


			} catch (SQLException e) {
				logger.error("getAllfiles()", e);

				if (logger.isDebugEnabled()) {
					logger.debug("CODDAO-getAllfiles()" + e.getMessage());
				}

			} finally {

				if (statement != null) {
					statement.close();
				}

				if (dbConnection != null) {
					dbConnection.close();
					logger.debug("Connection Closed");
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllfiles() - end");
			}
return allfiles;
		}
		
		public  int getAllProcessedfilesCallY() throws SQLException {
			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesCallY() - start");
			}
			 
			Statement statement = null;
			String allFileRecordsSQL = "SELECT count (*) FROM CODPROCESSED WHERE CALLTEST='Y' ";
			int allProcessedfilesY=0;
			Connection dbConnection=null;
			

			try {
				CODDAO coddao=new CODDAO();
				dbConnection=coddao.getConnection(dbConnection);
				statement = dbConnection.createStatement();

				ResultSet resultSet=statement.executeQuery(allFileRecordsSQL);
				while(resultSet.next())
				{
					allProcessedfilesY=resultSet.getInt(1);
				}


			} catch (SQLException e) {
				logger.error("getAllProcessedfilesCallY()", e);

				if (logger.isDebugEnabled()) {
					logger
						.debug("getAllProcessedfilesCallY()" + e.getMessage());
				}

			} finally {

				if (statement != null) {
					statement.close();
				}

				if (dbConnection != null) {
					dbConnection.close();
					logger.debug("Connection Closed");
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesCallY() - end");
			}
return allProcessedfilesY;
		}
		
		public  int getAllProcessedfilesCallN() throws SQLException {
			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesCallN() - start");
			}
			 
			Statement statement = null;
			String allFileRecordsSQL = "SELECT count (*) FROM CODPROCESSED WHERE CALLTEST='N' ";
			int allProcessedfilesN=0;
			Connection dbConnection=null;
			

			try {
				CODDAO coddao=new CODDAO();
				dbConnection=coddao.getConnection(dbConnection);
				statement = dbConnection.createStatement();

				ResultSet resultSet=statement.executeQuery(allFileRecordsSQL);
				while(resultSet.next())
				{
					allProcessedfilesN=resultSet.getInt(1);
				}


			} catch (SQLException e) {
				logger.error("getAllProcessedfilesCallN()", e);

				if (logger.isDebugEnabled()) {
					logger
						.debug("getAllProcessedfilesCallN()" + e.getMessage());
				}

			} finally {

				if (statement != null) {
					statement.close();
				}

				if (dbConnection != null) {
					dbConnection.close();
					logger.debug("Connection Closed");
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesCallN() - end");
			}
return allProcessedfilesN;
		}
		
		public  int getAllProcessedfilesLTE() throws SQLException {
			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesLTE() - start");
			}
			 
			Statement statement = null;
			String allFileRecordsSQL = "SELECT count (*) FROM CODPROCESSED WHERE TECHNOLOGY='LTE' ";
			int allProcessedfilesLTE=0;
			Connection dbConnection=null;
			

			try {
				CODDAO coddao=new CODDAO();
				dbConnection=coddao.getConnection(dbConnection);
				statement = dbConnection.createStatement();

				ResultSet resultSet=statement.executeQuery(allFileRecordsSQL);
				while(resultSet.next())
				{
					allProcessedfilesLTE=resultSet.getInt(1);
				}


			} catch (SQLException e) {
				logger.error("getAllProcessedfilesLTE()", e);

				if (logger.isDebugEnabled()) {
					logger.debug("CODDAO-getAllProcessedfilesLTE()" + e.getMessage());
				}

			} finally {

				if (statement != null) {
					statement.close();
				}

				if (dbConnection != null) {
					dbConnection.close();
					logger.debug("Connection Closed");
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesLTE() - end");
			}
return allProcessedfilesLTE;
		}

		public  int getAllProcessedfilesWCDMA() throws SQLException {
			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesWCDMA() - start");
			}
			 
			Statement statement = null;
			String allFileRecordsSQL = "SELECT count (*) FROM CODPROCESSED WHERE TECHNOLOGY='WCDMA' ";
			int allProcessedfilesWCDMA=0;
			Connection dbConnection=null;
			

			try {
				CODDAO coddao=new CODDAO();
				dbConnection=coddao.getConnection(dbConnection);
				statement = dbConnection.createStatement();

				ResultSet resultSet=statement.executeQuery(allFileRecordsSQL);
				while(resultSet.next())
				{
					allProcessedfilesWCDMA=resultSet.getInt(1);
				}


			} catch (SQLException e) {
				logger.error("getAllProcessedfilesWCDMA()", e);

				if (logger.isDebugEnabled()) {
					logger
						.debug("getAllProcessedfilesWCDMA()" + e.getMessage());
				}

			} finally {

				if (statement != null) {
					statement.close();
				}

				if (dbConnection != null) {
					dbConnection.close();
					logger.debug("Connection Closed");
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesWCDMA() - end");
			}
return allProcessedfilesWCDMA;
		}
		
		public  LinkedHashSet<String> getAllProcessedfilesDateY() throws SQLException {
			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesDateY() - start");
			}
			 
			Statement statement = null;
			String allFileRecordsSQL = "SELECT fdate from codprocessed where calltest='Y' ";
			LinkedHashSet<String> hs = new LinkedHashSet<String>();
			Connection dbConnection=null;
			

			try {
				CODDAO coddao=new CODDAO();
				dbConnection=coddao.getConnection(dbConnection);
				statement = dbConnection.createStatement();

				ResultSet resultSet=statement.executeQuery(allFileRecordsSQL);
				while(resultSet.next())
				{
					hs.add(resultSet.getString(1));
				}


			} catch (SQLException e) {
				logger.error("getAllProcessedfilesDateY()", e);

				if (logger.isDebugEnabled()) {
					logger
						.debug("getAllProcessedfilesDateY()" + e.getMessage());
				}

			} finally {

				if (statement != null) {
					statement.close();
				}

				if (dbConnection != null) {
					dbConnection.close();
					logger.debug("Connection Closed");
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesDateY() - end");
			}
return hs;
		}

		
		
		
		public  LinkedHashSet<String> getAllProcessedfilesNameY() throws SQLException {
			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesNameY() - start");
			}
			 
			Statement statement = null;
			String allFileRecordsSQL = "SELECT filename from codprocessed where calltest='Y' ";
			LinkedHashSet<String> hs = new LinkedHashSet<String>();
			Connection dbConnection=null;
			

			try {
				CODDAO coddao=new CODDAO();
				dbConnection=coddao.getConnection(dbConnection);
				statement = dbConnection.createStatement();

				ResultSet resultSet=statement.executeQuery(allFileRecordsSQL);
				while(resultSet.next())
				{
					if (logger.isDebugEnabled()) {
						logger.debug("CODDAO-getAllProcessedfilesNameY()"
							+ resultSet.getString(1)
							+ "RESULT");
					}
					hs.add(resultSet.getString(1));
					
				}
if (logger.isDebugEnabled()) {
	logger.debug("CODDAO-getAllProcessedfilesNameY()" + hs.toString() + "SIZE");
}

			} catch (SQLException e) {
				logger.error("getAllProcessedfilesNameY()", e);

				if (logger.isDebugEnabled()) {
					logger
						.debug("getAllProcessedfilesNameY()" + e.getMessage());
				}

			} finally {

				if (statement != null) {
					statement.close();
					logger.debug("Connection Closed");
				}

				if (dbConnection != null) {
					dbConnection.close();
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesNameY() - end");
			}
return hs;
		}
//PRS
		
		
//PRS T		
		
		
		
		public  LinkedHashSet<String> getAllProcessedfilesDateN() throws SQLException {
			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesDateN() - start");
			}
			 
			Statement statement = null;
			String allFileRecordsSQL = "SELECT fdate from codprocessed where calltest='N' ";
			LinkedHashSet<String> hs = new LinkedHashSet<String>();
			Connection dbConnection=null;
			

			try {
				CODDAO coddao=new CODDAO();
				dbConnection=coddao.getConnection(dbConnection);
				statement = dbConnection.createStatement();

				ResultSet resultSet=statement.executeQuery(allFileRecordsSQL);
				while(resultSet.next())
				{
					hs.add(resultSet.getString(1));
				}


			} catch (SQLException e) {
				logger.error("getAllProcessedfilesDateN()", e);

				if (logger.isDebugEnabled()) {
					logger
						.debug("getAllProcessedfilesDateN()" + e.getMessage());
				}

			} finally {

				if (statement != null) {
					statement.close();
				}

				if (dbConnection != null) {
					dbConnection.close();
					logger.debug("Connection Closed");
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesDateN() - end");
			}
return hs;
		}

		
		
		
		public  LinkedHashSet<String> getAllProcessedfilesNameN() throws SQLException {
			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesNameN() - start");
			}
			 
			Statement statement = null;
			String allFileRecordsSQL = "SELECT filename from codprocessed where calltest='N' ";
			LinkedHashSet<String> hs = new LinkedHashSet<String>();
			Connection dbConnection=null;
			

			try {
				CODDAO coddao=new CODDAO();
				dbConnection=coddao.getConnection(dbConnection);
				statement = dbConnection.createStatement();

				ResultSet resultSet=statement.executeQuery(allFileRecordsSQL);
				while(resultSet.next())
				{
					hs.add(resultSet.getString(1));
				}


			} catch (SQLException e) {
				logger.error("getAllProcessedfilesNameN()", e);

				if (logger.isDebugEnabled()) {
					logger
						.debug("getAllProcessedfilesNameN()" + e.getMessage());
				}

			} finally {

				if (statement != null) {
					statement.close();
				}

				if (dbConnection != null) {
					dbConnection.close();
					logger.debug("Connection Closed");
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-getAllProcessedfilesNameN() - end");
			}
return hs;
		}		
		
		
		


		public  int truncTable(String table) throws SQLException {
			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-truncTable(String) - start");
			}
			 
			Statement statement = null;
			String truncTableSQL = "truncate table "+table;
			int truncatedTable=0;
			Connection dbConnection=null;
		

			try {
				CODDAO coddao=new CODDAO();
				dbConnection=coddao.getConnection(dbConnection);
				statement = dbConnection.createStatement();

				statement.executeUpdate(truncTableSQL);
				dbConnection.commit();
				truncatedTable=1;
					
				


			} catch (SQLException e) {
				logger.error("truncTable(String)", e);

				if (logger.isDebugEnabled()) {
					logger.debug("CODDAO-truncTable(String)" + e.getMessage());
				}

			} finally {

				if (statement != null) {
					statement.close();
				}

				if (dbConnection != null) {
					dbConnection.close();
					logger.debug("Connection Closed");
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("CODDAO-truncTable(String) - end");
			}
	return truncatedTable;
		}
		
		
	public static void main(String[] args) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug("CODDAO-main(String[]) - start");
		}

		Connection con = null;
		con=new CODDAO().getConnection(con);
	if (logger.isDebugEnabled()) {
		logger.debug("CODDAO-main(String[])"
			+ new CODDAO().getAllProcessedfilesNameY().size()
			+ "size of processed files with Y");
	}	;	

		if (logger.isDebugEnabled()) {
			logger.debug("CODDAO-main(String[]) - end");
		}
	}



}
