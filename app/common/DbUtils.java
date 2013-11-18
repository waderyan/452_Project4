package common;

import java.sql.*;

import play.Logger;

public class DbUtils {

	public static void closeAll(Connection conn, PreparedStatement stmt, ResultSet rs) {
		try {
			DbUtils.safeClose(conn);
			DbUtils.safeClose(stmt);
			DbUtils.safeClose(rs);
		} catch (Exception e) {
			Logger.warn("ERROR closing stmt and rs");
		}
	}
	
	public static void safeClose(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			}
			catch (SQLException e) {
				// ...
			}
		}
	}
	
	public static void safeClose(PreparedStatement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			}
			catch (SQLException e) {
				// ...
			}
		}
	}
	
	public static void safeClose(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			}
			catch (SQLException e) {
				// ...
			}
		}
	}

	public static void closeAll(Connection conn, PreparedStatement stmt) {
		try {
			DbUtils.safeClose(conn);
			DbUtils.safeClose(stmt);
		} catch (Exception e) {
			Logger.warn("ERROR closing stmt and rs");
		}
	}
}
