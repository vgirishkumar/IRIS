package com.temenos.interaction.sdk.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


// TODO refactor this class, copied from somewhere
public class ResponderDBUtils {
	private final static Logger logger = Logger.getLogger(ResponderDBUtils.class.getName());

	public static String fillDatabase() {
		logger.fine("Loading HSQL JDBC driver");
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (Exception ex) {
			// TODO replace with slf4j instance of logger
			return "ERROR: failed to load HSQLDB JDBC driver.";
		}

		Connection conn = null;
		String line = "";
		try {
			logger.fine("Attempting to connect to database");
			conn = DriverManager.getConnection("jdbc:hsqldb:mem:responder", "sa", "");
			Statement statement = conn.createStatement();

			logger.fine("Loading SQL INSERTs file");
			InputStream xml = ResponderDBUtils.class.getResourceAsStream("/META-INF/responder_insert.sql");
			if (xml == null){
				return "ERROR: DML file not found [/META-INF/responder_insert.sql].";
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(xml, "UTF-8"));

			logger.fine("Reading SQL INSERTs file");
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					line = line.replace("`", "");
					line = line.replace(");", ")");
					line = line.replace("'0x", "'");

					if (line.length() > 5) {
						logger.fine("Inserting record: " + line);
						statement.executeUpdate(line);
						count++;
					}
				}
			}

			br.close();
			statement.close();
			logger.info(count + " rows have been inserted into the database.");

		} catch (Exception ex) {
			logger.severe("Failed to insert SQL statements.");
			ex.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					// do nothing
				}
			}
		}
		return "OK";
	}

	public static void writeStringToFile(String fileName, String contents) {
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(fileName),
					"utf-8");
			out.write(contents);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

	}

	public static String readFileToString(String fileName) {
		return readFileToString(fileName, Charset.defaultCharset().name());
	}

	public static String readFileToString(String fileName, String charsetName) {
		StringBuilder strBuilder = new StringBuilder();
		try {
			InputStream buf = ResponderDBUtils.class
					.getResourceAsStream(fileName);

			BufferedReader in = new BufferedReader(new InputStreamReader(buf,
					charsetName));

			String str;

			try {
				while ((str = in.readLine()) != null) {
					strBuilder.append(str);
				}
				in.close();

			} catch (IOException ex) {
				Logger.getLogger(ResponderDBUtils.class.getName()).log(
						Level.SEVERE, null, ex);
			}

		} catch (Exception ex) {
			Logger.getLogger(ResponderDBUtils.class.getName()).log(
					Level.SEVERE, null, ex);
		}

		return strBuilder.toString();
	}

}
