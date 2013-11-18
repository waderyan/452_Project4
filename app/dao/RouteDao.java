package dao;

import java.sql.*;
import java.util.*;

import common.DbUtils;

import models.Flight;
import models.Route;
import play.Logger;
import play.db.DB;

public final class RouteDao {

	private final static String SELECT_SRC = "" +
			"select flight_num, source_city, dest_city, dep_time, arr_time, airfare, mileage" +
			" from flight" +
			" where source_city = ?";
	
	public static List<Flight> getFlights(String src) {
		Connection conn = DB.getConnection();
		
		PreparedStatement stmt = null;
		ResultSet rs =  null;
		List<Flight> srcs = new ArrayList<Flight>();
		try {
			stmt = conn.prepareStatement(SELECT_SRC);
			stmt.setString(1, src);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				srcs.add(FlightDao.deserialize(rs));
			}
			
		} catch(Exception e) {
			Logger.warn("Failed getCheapestRoute");
			return null;
		} finally {
			DbUtils.closeAll(conn, stmt, rs);
		}
		return srcs;
	}
	
	private static List<Route> routes;
	private static final int MAX_PRICE = 10000000;
	
	private static Collection<Route> minimize(List<Route> routes) {
		int min = MAX_PRICE;
		for (Route r : routes) {
			if (r.num < min) {
				min = r.num;
			}
		}
		List<Route> result = new ArrayList<Route>();
		for (Route r : routes) {
			if (r.num == min) {
				result.add(r);
			}
		}
		return result;
	}
	
	public static Collection<Route> getCheapestRoute(String src, String dest) {
		routes = new ArrayList<Route>();
		cheapest(src, dest, 0, MAX_PRICE, new ArrayList<Flight>());
		return minimize(routes);
	}
	
	private static void cheapest(String src, String dest, int price, int min, List<Flight> current) {
		List<Flight> flights = getFlights(src);
		
		for (Flight f : flights) {
			List<Flight> path = new ArrayList<Flight>(current);
			path.add(f);
			if (f.dest_city.equals(dest)) {
				if (price + f.airfare < min) {
					min = price + f.airfare;
					routes.add(new Route(price + f.airfare, path));
				}
			} else {
				cheapest(f.dest_city, dest, price + f.airfare, min, path);
			}
		}
	}
	
	public static Collection<Route> getCheapestRouteByN(String src, String dest, int target) {
		routes = new ArrayList<Route>();
		cheapest(src, dest, 0, MAX_PRICE, new ArrayList<Flight>(), -1, target);
		return minimize(routes);
	}
	
	private static void cheapest(String src, String dest, int price, int min, List<Flight> current, int n, int target) {
		if (n >= target) {
			return;
		}
		List<Flight> flights = getFlights(src);
		
		for (Flight f : flights) {
			if (f.dest_city.equals(dest)) {
				if (price + f.airfare < min) {
					min = price + f.airfare;
					List<Flight> path = new ArrayList<Flight>(current);
					path.add(f);
					routes.add(new Route(price + f.airfare, path));
				}
			} else {
				List<Flight> path = new ArrayList<Flight>(current);
				path.add(f);
				int sum = 0;
				for (Flight p : path) {
					sum += p.airfare;
				}
				cheapest(f.dest_city, dest, sum, min, path, n + 1, target);
			}
		}
	}
	
	public static Collection<Route> getCheapestRouteByLayover(String src, String dest, int layover) {
		routes = new ArrayList<Route>();
		cheapest(src, dest, 0, MAX_PRICE, new ArrayList<Flight>());
		
		List<Integer> toKill = new ArrayList<Integer>();
		for (int i = 0; i < routes.size(); i++) {
			List<Flight> flights = routes.get(i).flights;
			for (int f = 1; f < flights.size(); f++) {
				// layover prior arr_time - dep_time
				if ((flights.get(f).dep_time - flights.get(f-1).arr_time) > layover) {
					System.out.println("kill: " + i);
					toKill.add(i);
					break;
				}
			}
		}
		List<Route> result = new ArrayList<Route>();
		for (int i = 0; i < routes.size(); i++) {
			if (!toKill.contains(i)) {
				result.add(routes.get(i));
			}
		}
		return minimize(result);
	}
	
	public static Collection<Route> getCheapestRouteNotIn(String src, String dest, String notIn) {
		routes = new ArrayList<Route>();
		cheapest(src, dest, 0, MAX_PRICE, new ArrayList<Flight>(), notIn);
		return minimize(routes);
	}
	
	private static void cheapest(String src, String dest, int price, int min, List<Flight> current, String notIn) {
		List<Flight> flights = getFlights(src);
		
		for (Flight f : flights) {
			if (f.dest_city.equals(notIn)) {
				continue;
			}
			if (f.dest_city.equals(dest)) {
				if (price + f.airfare < min) {
					min = price + f.airfare;
					List<Flight> path = new ArrayList<Flight>(current);
					path.add(f);
					routes.add(new Route(price + f.airfare, path));
				}
			} else {
				List<Flight> path = new ArrayList<Flight>(current);
				path.add(f);
				int sum = 0;
				for (Flight p : path) {
					sum += p.airfare;
				}
				cheapest(f.dest_city, dest, sum, min, path, notIn);
			}
		}
	}

	public static Collection<Route> getMinDist(String src, String dst) {
		routes = new ArrayList<Route>();
		minDist(src, dst, 0, new ArrayList<Flight>());
		return minimize(routes);
	}
	
	private static void minDist(String src, String dst, int dist, List<Flight> current) {
		List<Flight> flights = getFlights(src);
		
		for (Flight f : flights) {
			List<Flight> path = new ArrayList<Flight>(current);
			path.add(f);
			if (f.dest_city.equals(dst)) {
				routes.add(new Route(dist + f.mileage, path));
			} else {
				minDist(f.dest_city, dst, dist + f.mileage, path);
			}
		}
	}
	
	public static Collection<Route> getMinStopOvers(String src, String dst) {
		routes = new ArrayList<Route>();
		minStopOvers(src, dst, new ArrayList<Flight>(), 0);
		return minimize(routes);
	}
	
	private static void minStopOvers(String src, String dst, List<Flight> current, int stopovers) {
		List<Flight> flights = getFlights(src);
		
		for (Flight f : flights) {
			List<Flight> path = new ArrayList<Flight>(current);
			path.add(f);
			if (f.dest_city.equals(dst)) {
				routes.add(new Route(stopovers, path));
			} else {
				minStopOvers(f.dest_city, dst, path, stopovers + 1);
			}
		}
	}
	
	public static Collection<Route> getShortestTime(String src, String dst) {
		routes = new ArrayList<Route>();
		shortestTime(src, dst, new ArrayList<Flight>(), 0);
		return minimize(routes);
	}
	
	private static void shortestTime(String src, String dst, List<Flight> current, int time) {
		List<Flight> flights = getFlights(src);
		
		for (Flight f : flights) {
			List<Flight> path = new ArrayList<Flight>(current);
			path.add(f);
			if (f.dest_city.equals(dst)) {
				routes.add(new Route(time + f.travelTime(), path));
			} else {
				shortestTime(f.dest_city, dst, path, time + f.travelTime());
			}
		}
	}
	
	public static Collection<Route> getShortestTimeWithLayovers(String src, String dst) {
		routes = new ArrayList<Route>();
		shortestTime(src, dst, new ArrayList<Flight>(), 0);
		for (Route r : routes) {
			assert r.flights.size() > 1;
			r.num = Math.abs(r.flights.get(0).dep_time - r.flights.get(r.flights.size() - 1).arr_time);
		}
		return minimize(routes);
	}

	
	public static Collection<Route> getStopsAndMileage(String src, String dst, int n, int m) {
		routes = new ArrayList<Route>();
		stopsAndMileage(src, dst, new ArrayList<Flight>(), 0, 0, n, m);
		return routes;
	}
	
	private static void stopsAndMileage(String src, String dest, List<Flight> current, int curN, int curM, final int maxN, final int maxM) {
		if (curN > maxN) {
			return;
		}
		
		List<Flight> flights = getFlights(src);
		
		for (Flight f : flights) {
			List<Flight> path = new ArrayList<Flight>(current);
			path.add(f);
			if (f.dest_city.equals(dest)) {
				if (curM + f.mileage <= maxM) {
					routes.add(new Route(curM + f.airfare, path));
				}
			} else {
				stopsAndMileage(f.dest_city, dest, path, curN + 1, curM + f.mileage, maxN, maxM);
			}
		}
	}

	public static Collection<Route> getAirfareCannotExceed(String src, String dst, int m) {
		routes = new ArrayList<Route>();
		airfare(src, dst, new ArrayList<Flight>(), 0, m);
		return routes;
	}
	
	private static void airfare(String src, String dest, List<Flight> current, int price, final int maxPrice) {
		List<Flight> flights = getFlights(src);
		
		for (Flight f : flights) {
			List<Flight> path = new ArrayList<Flight>(current);
			path.add(f);
			if (f.dest_city.equals(dest)) {
				if (price + f.airfare <= maxPrice) {
					routes.add(new Route(price + f.airfare, path));
				}
			} else {
				airfare(f.dest_city, dest, path, price + f.airfare, maxPrice);
			}
		}
	}

	public static Collection<Route> getNoTravelDuringTime(String src, String dst, int time) {
		routes = new ArrayList<Route>();
		shortestTime(src, dst, new ArrayList<Flight>(), 0);
		List<Route> results = new ArrayList<Route>();
		for (Route r : routes) {
			if (r.flights.get(0).dep_time < time || r.flights.get(r.flights.size() - 1).arr_time > time) {
				results.add(r);
			}
		}
		return results;
	}
	
}
