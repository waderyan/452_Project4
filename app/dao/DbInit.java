package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import common.DbUtils;

import play.db.DB;

public class DbInit {
	
	private static final String FLIGHT_TABLE = "" +
			"create table flight (" +
			"	flight_num int," +
			" 	source_city varchar(40)," +
			" 	dest_city varchar(40)," +
			" 	dep_time int," +
			" 	arr_time int," +
			" 	airfare int," +
			" 	mileage int," +
			" 	primary key (flight_num)) " +
			"Engine = InnoDB;";
	
	private static final String CREW_TABLE = "" +
			"create table crew (" +
			"	id int AUTO_INCREMENT," +
			"	name varchar(40), " +
			"	salary int, " +
			"	position varchar(40), " +
			"	seniority int, " +
			"	fly_hours int, " +
			"	mgrid int, " +
			"	primary key (id) " +
			") Engine = InnoDB;";
	 
	private static final String ASSIGN_TABLE = "" +
			"create table assign (" +
			"	id int, " +
			"	flight_num int" +
			")";
	
	private static final String DROP_FLIGHT = "drop table if exists flight";
	
	private static final String DROP_ASSIGN = "drop table if exists assign";
	
	private static final String DROP_CREW = "drop table if exists crew";
	
	public static void reset() {
		Connection conn = DB.getConnection();
		
		PreparedStatement stmt = null;
		try {
			
			stmt = conn.prepareStatement(DROP_ASSIGN);
			stmt.executeUpdate();
			stmt = conn.prepareStatement(DROP_FLIGHT);
			stmt.executeUpdate();
			stmt = conn.prepareStatement(DROP_CREW);
			stmt.executeUpdate();
			
			stmt = conn.prepareStatement(FLIGHT_TABLE);
			stmt.executeUpdate();
			stmt = conn.prepareStatement(CREW_TABLE);
			stmt.executeUpdate();
			stmt = conn.prepareStatement(ASSIGN_TABLE);
			stmt.executeUpdate();
			
			stmt.addBatch("insert into flight (flight_num, source_city, dest_city, dep_time, arr_time, airfare, mileage) values (101, 'Montreal', 'NY', 0530, 0645, 180, 170), (102, 'Montreal', 'Washington', 0100, 0235, 100, 180), (103, 'NY', 'Chicago', 0800, 1000, 150, 300), (105, 'Washington', 'Kansas-City', 0600, 0845, 200, 600), (106, 'Washington', 'NY', 1200, 1330, 50, 80), (107, 'Chicago', 'SLC', 1100, 1430, 220, 750), (110, 'Kansas-City', 'Denver', 1400, 1525, 180, 300), (111, 'Kansas-City', 'SLC', 1300, 1530, 200, 500), (112, 'SLC', 'SanFran', 1800, 1930, 85, 210), (113, 'SLC', 'LA', 1730, 1900, 185, 230), (115, 'Denver', 'SLC', 1500, 1600, 75, 300), (116, 'SanFran', 'LA', 2200, 2230, 50, 75), (118, 'LA', 'Seattle', 2000, 2100, 150, 450);");
			stmt.addBatch("insert into crew (id, name, salary, position, seniority, fly_hours, mgrid) values (01, 	'John Smith',	   500000,   'Pilot',       15,        3000,  null), (02,   'Rob Anderson',  400000,   'Pilot',       12,        2700, 	   01), (03,    'Bill Talbot',   300000,   'Pilot',       12,        2500,     01), (11,    'Steve Lowe',	   250000,   'Co-pilot',	10,        2000,     02), (12,    'John Crowe',	   200000,   'Co-pilot',	 9,	   800,     03), (13,    'Mike York', 	   150000,   'Co-Pilot',	 8,	   650,     02), (21,    'Sam Carson',	   100000,   'Engineer',	12,       2400,    11), (22,    'Joe Young', 	   180000,   'Chief Engg', 9,	     0,     11), (10,    'Dave Empire',    80000,   'Engineer',	 2,	   300,     22), (30,    'Dee Brown', 	    70000,   'Engineer',	13,        1050,     22);");
		
			stmt.executeBatch();
			stmt.addBatch("insert into assign (id, flight_num) values (01,    101), (02,    102), (03,    106), (02,    105), (11,    103), (13,    107), (11,    110), (21,    111), (03,    112), (21,    112), (10,    113), (01,    116), (30,    116), (02,    118), (30,    118);");
			stmt.executeBatch();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeAll(conn, stmt);
		}
	}
}
