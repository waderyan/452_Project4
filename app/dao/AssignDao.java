package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.db.DB;

import models.Assign;
import common.DbUtils;

public class AssignDao {

	private static final String SELECT_ALL = "select id, flight_num from assign";
	
	public static List<Assign> getAll() {
		Connection conn = DB.getConnection();

    	PreparedStatement stmt = null;
    	ResultSet rs = null;

    	List<Assign> assignments = new ArrayList<Assign>();
    	try {
    		stmt = conn.prepareStatement(SELECT_ALL);

    		rs = stmt.executeQuery();
    		while (rs.next()) {
    			assignments.add(deserialize(rs));
    		}

    		return assignments;

    	} catch (SQLException e) {
    		Logger.info("ERROR GETTING CREW MEMBERS!");
    		return null;
    	} finally {
    		DbUtils.closeAll(conn, stmt, rs);
    	}
	}
	
	private static Assign deserialize(ResultSet rs) throws SQLException {
		return new Assign(
				rs.getInt("id"), 
				rs.getInt("flight_num")
			);
	}

	private static final String INSERT = "insert into assign (id, flight_num) values (?, ?)";
	
	public static void insert(Assign assign) throws SQLException {
		Connection conn = DB.getConnection();
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT);
			stmt.setInt(1, assign.id);
			stmt.setInt(2, assign.flight_num);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeAll(conn, stmt);
		}
	}
	
	private static final String DELETE = "delete from assign where id = ?";
	
	public static void delete(int id) throws SQLException {
		Connection conn = DB.getConnection();
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE);
			stmt.setInt(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeAll(conn, stmt);
		}
	}
	
	private static final String DELETE_F = "delete from assign where flight_num = ?";
	
	public static void deleteFlight(int flight_num) throws SQLException {
		Connection conn = DB.getConnection();
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_F);
			stmt.setInt(1, flight_num);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeAll(conn, stmt);
		}
	}
}
