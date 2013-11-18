package dao;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.db.DB;

import models.Flight;

import common.DbUtils;

public class FlightDao {

	private final static String SELECT_ALL = "select flight_num, source_city, dest_city, dep_time, arr_time, airfare, mileage from flight";
	
	public static List<Flight> getAll() {
		Connection conn = DB.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<Flight> flight = new ArrayList<Flight>();
		try {
			stmt = conn.prepareStatement(SELECT_ALL);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				flight.add(deserialize(rs));
			}
		} catch (Exception e) {
			Logger.warn("ERROR GETTING flights!");
    		return null;
		} finally {
			DbUtils.closeAll(conn, stmt, rs);
		}
		return flight;
	}
	
	public static Flight get(int flight_num) {
		List<Flight> flights = getAll();
		for (Flight f : flights) {
			if (f.flight_num == flight_num) {
				return f;
			}
		}
		return null;
	}
	
	private static final String INSERT = "insert into flight (flight_num, source_city, dest_city, dep_time, arr_time, airfare, mileage) values (?, ?, ?, ?, ?, ?, ?)";
	
	public static void insert(Flight f) throws SQLException {
		Connection conn = DB.getConnection();
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, f.flight_num);
			stmt.setString(2, f.source_city);
			stmt.setString(3, f.dest_city);
			stmt.setInt(4, f.dep_time);
			stmt.setInt(5, f.arr_time);
			stmt.setInt(6, f.airfare);
			stmt.setInt(7, f.mileage);
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeAll(conn, stmt, rs);
		}
	}
	
	private static final String DELETE = "delete from flight where flight_num = ?";
	
	public static boolean delete(int flight_num) throws SQLException {
		Connection conn = DB.getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE);
			stmt.setInt(1, flight_num);
			return stmt.executeUpdate() == 1;
		} finally {
			DbUtils.closeAll(conn, stmt);
		}
	}
	
	private static final String UPDATE = "update flight set flight_num = ?, source_city = ?, dest_city = ?, dep_time = ?, arr_time = ?, airfare = ?, mileage = ? where flight_num = ?";
	
	public static boolean update(int oldFlightNum, Flight f) throws SQLException {
		Connection conn = DB.getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE);
			stmt.setInt(1, f.flight_num);
			stmt.setString(2, f.source_city);
			stmt.setString(3, f.dest_city);
			stmt.setInt(4, f.dep_time);
			stmt.setInt(5, f.arr_time);
			stmt.setInt(6, f.airfare);
			stmt.setInt(7, f.mileage);
			stmt.setInt(8, oldFlightNum);
			return stmt.executeUpdate() == 1;
		} finally {
			DbUtils.closeAll(conn, stmt);
		}
	}
	
	public static Flight deserialize(ResultSet rs) throws SQLException {
		return new Flight(
				rs.getInt("flight_num"), 
				rs.getString("source_city"), 
				rs.getString("dest_city"), 
				rs.getInt("dep_time"), 
				rs.getInt("arr_time"), 
				rs.getInt("airfare"), 
				rs.getInt("mileage"));
	}
}
