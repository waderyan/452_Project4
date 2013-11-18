package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import models.Crew;
import play.Logger;
import play.db.DB;

import common.DbUtils;

public class CrewDao {

	private static final String SELECT_ALL = "select id, name, salary, position, seniority, fly_hours, mgrid from crew";
	
	public static List<Crew> getAll () {
    	Connection conn = DB.getConnection();

    	PreparedStatement stmt = null;
    	ResultSet rs = null;

    	List<Crew> crew = new ArrayList<Crew>();
    	try {
    		stmt = conn.prepareStatement(SELECT_ALL);

    		rs = stmt.executeQuery();
    		while (rs.next()) {
    			crew.add(deserialize(rs));
    		}

    		return crew;

    	} catch (SQLException e) {
    		Logger.info("ERROR GETTING CREW MEMBERS!");
    		return null;
    	} finally {
    		DbUtils.closeAll(conn, stmt, rs);
    	}
    }
	
	public static Crew get(int id) {
		List<Crew> crew = getAll();
		for (Crew c : crew) {
			if (c.id == id) {
				return c;
			}
		}
		return null;
	}
	
	private static final String INSERT = "insert into crew (name, salary, position, seniority, fly_hours, mgrid) values (?, ?, ?, ?, ?, ?)";
	
	public static int insert(Crew c) throws SQLException {
		Connection conn = DB.getConnection();
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int id = -1;
		try {
			stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, c.name);
			stmt.setInt(2, c.salary);
			stmt.setString(3, c.position);
			stmt.setInt(4, c.seniority);
			stmt.setInt(5, c.fly_hours);
			if (c.mgrid == Crew.NULL) {
				stmt.setNull(6, Types.NULL);
			} else {
				stmt.setInt(6, c.mgrid);
			}
			stmt.executeUpdate();
			
			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1);
			}
			
			return id;
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeAll(conn, stmt, rs);
		}
	}
	
	public static void setMgrToNull(int mgrid) throws SQLException {
		Connection conn = DB.getConnection();
		
		final String sql = "update crew set mgrid = NULL where id in (select a.id from (select * from crew) a where a.mgrid = ?);";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, mgrid);
			stmt.executeUpdate();
		} finally {
			DbUtils.closeAll(conn, stmt, rs);
		}
	}
	
	private static final String DELETE = "delete from crew where id = ?";
	
	public static boolean delete(int id) throws SQLException {
		Connection conn = DB.getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE);
			stmt.setInt(1, id);
			return stmt.executeUpdate() == 1;
		} finally {
			DbUtils.closeAll(conn, stmt);
		}
	}
	
	private static final String UPDATE = "update crew set name = ?, salary = ?, position = ?, seniority = ?, fly_hours = ?, mgrid = ? where id = ?";
	
	public static boolean update(int id, Crew c) throws SQLException {
		Connection conn = DB.getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE);
			stmt.setString(1, c.name);
			stmt.setInt(2, c.salary);
			stmt.setString(3, c.position);
			stmt.setInt(4, c.seniority);
			stmt.setInt(5, c.fly_hours);
			if (c.mgrid == Crew.NULL) {
				stmt.setNull(6, Types.NULL);
			} else {
				stmt.setInt(6, c.mgrid);
			}
			
			stmt.setInt(7, id);
			return stmt.executeUpdate() == 1;
		} finally {
			DbUtils.closeAll(conn, stmt);
		}
	}
	
	private static Crew deserialize(ResultSet rs) throws SQLException {
		return new Crew(
				rs.getInt("id"), 
				rs.getString("name"), 
				rs.getInt("salary"), 
				rs.getString("position"), 
				rs.getInt("seniority"), 
				rs.getInt("fly_hours"), 
				rs.getInt("mgrid"));
	}

}
