package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Assign;
import models.Flight;
import models.UserOption;
import models.Route;
import dao.AssignDao;
import dao.FlightDao;
import dao.RouteDao;
import play.data.DynamicForm;
import play.mvc.*;

import views.html.*;

public class FlightController extends Controller {
	
	public static Result update() {
		DynamicForm data = new DynamicForm().bindFromRequest();
		if (data.hasErrors()) {
			return badRequest("form has errors");
		}
		try {
			int old_flight_num = FlightFormHelper.getFlightNum(data.get("old-flight-num").split(":")[0]);
			Flight f = FlightDao.get(old_flight_num);
			
			int flight_num = FlightFormHelper.validFlightNum(data.get("flight-num")) ? FlightFormHelper.getFlightNum(data.get("flight-num")) : f.flight_num;
			String src_city = FlightFormHelper.validCity(data.get("src-city")) ? FlightFormHelper.getCity(data.get("src-city")) : f.source_city;
			String dest_city = FlightFormHelper.validCity(data.get("dest-ctiy")) ? FlightFormHelper.getCity(data.get("dest-city")) : f.dest_city;
			int dep_time = FlightFormHelper.validTime(data.get("dep-time")) ? FlightFormHelper.getTime(data.get("dep-time")) : f.dep_time;
			int arr_time = FlightFormHelper.validTime(data.get("arr-time")) ? FlightFormHelper.getTime(data.get("arr-time")) : f.arr_time;
			int airfare = FlightFormHelper.validAirfare(data.get("airfare")) ? FlightFormHelper.getAirfare(data.get("airfare")) : f.airfare;
			int mileage = FlightFormHelper.validMileage(data.get("mileage")) ? FlightFormHelper.getMileage(data.get("mileage")) : f.mileage;
			
			if (src_city.equals(dest_city)) {
				return badRequest("src_city is the same as dest_city");
			}
			if (arr_time <= dep_time) {
				return badRequest("arr_time is before or equal to dep_time" + arr_time + " " + dep_time);
			}
			
			FlightDao.update(old_flight_num, new Flight(flight_num, src_city, dest_city, arr_time, dep_time, airfare, mileage));
			
		} catch(Exception e) {
			e.printStackTrace();
			return badRequest("error inserting flight and assign " + e.getMessage());
		}
		return redirect(routes.Application.getDb());
	}
	
	public static Result insert() {
		DynamicForm data = new DynamicForm().bindFromRequest();
		if (data.hasErrors()) {
			return badRequest("errors in request");
		}
		try {
			int flight_num = FlightFormHelper.getFlightNum(data.get("flight-num"));
			String src_city = FlightFormHelper.getCity(data.get("src-city"));
			String dest_city = FlightFormHelper.getCity(data.get("dest-city"));
			int dep_time = FlightFormHelper.getTime(data.get("dep-time"));
			int arr_time = FlightFormHelper.getTime(data.get("arr-time"));
			int airfare = FlightFormHelper.getAirfare(data.get("airfare"));
			int mileage = FlightFormHelper.getMileage(data.get("mileage"));
			
			if (src_city.equals(dest_city)) {
				return badRequest("cities are the same!");
			}
			if (arr_time <= dep_time) {
				return badRequest("arrive time is before or equal to departure time!");
			}
			
			FlightDao.insert(new Flight(flight_num, src_city, dest_city, dep_time, arr_time, airfare, mileage));
			
			for (Map.Entry<String, String> entry : data.data().entrySet()) {
				if (entry.getKey().startsWith("crew")) {
					AssignDao.insert(new Assign(FlightFormHelper.parseCrewId(data.data().get(entry.getKey())), flight_num));
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			return badRequest("error inserting flight and assign " + e.getMessage());

		}
		return redirect(routes.Application.getDb());
	}
	
	public static Result delete() {
		DynamicForm d = new DynamicForm().bindFromRequest();
		try {
			int id = Integer.parseInt(d.get("flight-num").split(":")[0]);
			System.out.println("flight_num: " + id);
			AssignDao.deleteFlight(id);
			return FlightDao.delete(id) ? redirect(routes.Application.getDb()) : badRequest("assertion error");
		} catch (SQLException e) {
			return badRequest("error during delete " + e.getMessage());
		}
	}
	
	private static UserOption opt = new UserOption();
	
	public static Result cheapestRoute() {
		DynamicForm data = new DynamicForm().bindFromRequest();
		if (data.hasErrors()) {
			return badRequest();
		}
		List<Route> routes = new ArrayList<Route>();
		List<UserOption> opts = new ArrayList<UserOption>();
		
		try {
			opt.src_city = FlightFormHelper.getCity(data.get("src-city"));
			opt.dest_city = FlightFormHelper.getCity(data.get("dest-city"));
			
			if (opt.src_city.equals(opt.dest_city)) {
				return badRequest("src and destination city are the same");
			}
			
			opt.letter = data.get("opt-type").split(" ")[0];
			opt.other = data.get("opt-var");
			int target = -1;
			
			switch(opt.letter) {
			case "A":
				routes.addAll(RouteDao.getCheapestRoute(opt.src_city, opt.dest_city));
				break;
			case "B":
				target = FlightFormHelper.getN(opt.other);
				if (target == -1) {
					return badRequest("n is invalid");
				}
				routes.addAll(RouteDao.getCheapestRouteByN(opt.src_city, opt.dest_city, target));
				break;
			case "C":
				target = FlightFormHelper.getN(opt.other);
				if (target == -1) {
					return badRequest("n hours is invalid");
				}
				routes.addAll(RouteDao.getCheapestRouteByLayover(opt.src_city, opt.dest_city, target * 60));
				break;
			case "D":
				String notIn = FlightFormHelper.getCity(opt.other);
				if (notIn.equals(opt.dest_city) || notIn.equals(opt.src_city)) {
					return badRequest("dest or src can't be the city that is removed from the query");
				}
				routes.addAll(RouteDao.getCheapestRouteNotIn(opt.src_city, opt.dest_city, notIn));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return badRequest("error getting cheapest route " + e.getMessage());
		}
		
		opts.add(opt);
		return ok(index.render(cheapest.render(routes, opts)));
	}

	public static Result shortestRoute() {
		DynamicForm data = new DynamicForm().bindFromRequest();
		if (data.hasErrors()) {
			return badRequest();
		}
		List<Route> routes = new ArrayList<Route>();
		List<UserOption> opts = new ArrayList<UserOption>();
		try {
			opt.src_city = FlightFormHelper.getCity(data.get("src-city"));
			opt.dest_city = FlightFormHelper.getCity(data.get("dest-city"));
			
			if (opt.src_city.equals(opt.dest_city)) {
				return badRequest("src and destination city are the same");
			}
			
			opt.letter = data.get("opt-type").split(" ")[0];
			opt.other = "";
			switch(opt.letter) {
			case "A":
				routes.addAll(RouteDao.getMinDist(opt.src_city, opt.dest_city));
				break;
			case "B":
				routes.addAll(RouteDao.getMinStopOvers(opt.src_city, opt.dest_city));
				break;
			case "C":
				routes.addAll(RouteDao.getShortestTime(opt.src_city, opt.dest_city));
				break;
			case "D":
				routes.addAll(RouteDao.getShortestTimeWithLayovers(opt.src_city, opt.dest_city));
				break;
			}
		} catch(Exception e) {
			e.printStackTrace();
			return badRequest("error getting shortest route " + e.getMessage());
		}
		opts.add(opt);
		return ok(index.render(shortest.render(routes, opts)));
	}

	public static Result specialRoute() {
		DynamicForm data = new DynamicForm().bindFromRequest();
		if (data.hasErrors()) {
			return badRequest();
		}
		List<Route> routes = new ArrayList<Route>();
		List<UserOption> opts = new ArrayList<UserOption>();
		final int MAX_DOLLARS = 10000;
		try {
			opt.src_city = FlightFormHelper.getCity(data.get("src-city"));
			opt.dest_city = FlightFormHelper.getCity(data.get("dest-city"));
			
			if (opt.src_city.equals(opt.dest_city)) {
				return badRequest("src and destination city are the same");
			}
			
			opt.letter = data.get("opt-type").split(" ")[0];
			opt.other = data.get("opt-var1");
			opt.other2 = data.get("opt-var2");
			switch(opt.letter) {
			case "A":
				int n = FlightFormHelper.getN(opt.other);
				if (n == -1 || n > 3) {
					return badRequest("n is invalid");
				}
				int m = FlightFormHelper.getN(opt.other2);
				if (m <= -1) {
					return badRequest("m is invalid");
				}
				routes.addAll(RouteDao.getStopsAndMileage(opt.src_city, opt.dest_city, n, m));
				break;
			case "B":
				int w = FlightFormHelper.getN(opt.other);
				if (w == -1 || w > MAX_DOLLARS) {
					return badRequest("n is invalid");
				}
				routes.addAll(RouteDao.getAirfareCannotExceed(opt.src_city, opt.dest_city, w));
				break;
			case "C":
				int time = FlightFormHelper.parseTime(opt.other);
				if (time == -1) {
					return badRequest("invalid time");
				}
				routes.addAll(RouteDao.getNoTravelDuringTime(opt.src_city, opt.dest_city, time));
				break;
			}
		} catch(Exception e) {
			e.printStackTrace();
			return badRequest("error getting shortest route " + e.getMessage());
		}
		opts.add(opt);
		return ok(index.render(special.render(routes, opts)));
	}
	
}

class FlightFormHelper {
	
	static boolean validFlightNum(String num) {
		try {
			getFlightNum(num);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	static boolean validCity(String city) {
		try {
			getCity(city);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	static boolean validTime(String time) {
		try {
			getTime(time);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	static boolean validAirfare(String fare) {
		try {
			getAirfare(fare);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	static boolean validMileage(String mile) {
		try {
			getMileage(mile);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	static int getFlightNum(String num) {
		try {
			return Integer.parseInt(num);
		} catch(Exception e) {
			throw new RuntimeException("error getting flight number");
		}
	}
	
	static String getCity(String city) {
		if (city != null && city.trim() != "") {
			return city;
		}
		throw new RuntimeException("error getting city");
	}
	
	static int getTime(String time) {
		try {
			int result = Integer.parseInt(time);
			if (result <= 0) {
				throw new RuntimeException("invalid time");
			}
			return result;
		} catch(Exception e) {
			throw new RuntimeException("invalid time");
		}
	}
	
	static int getAirfare(String fare) {
		try {
			int result = Integer.parseInt(fare);
			if (result <= 0) {
				throw new RuntimeException("invalid fare");
			}
			
			return result;
		} catch(Exception e) {
			throw new RuntimeException("invalid fare");
		}
	}
	
	static int getMileage(String mile) {
		try {
			int result = Integer.parseInt(mile);
			if (result <= 0) {
				throw new RuntimeException("invalid mileage");
			}
			
			return result;
		} catch(Exception e) {
			throw new RuntimeException("invalid mileage");
		}
	}
	
	static int getN(String n) {
		try {
			int result = Integer.parseInt(n);
			if (result <= 0) {
				return -1;
			}
			
			return result;
		} catch(Exception e) {
			return -1;
		}
	}
	
	static int parseCrewId(String c) {
		return Integer.parseInt(c.split(" ")[0]);
	}
	
	static int parseTime(String time) {
		if (!time.contains(":")) {
			return -1;
		}
		String[] strs = time.split(":");
		try {
			int[] nums = new int[2];
			nums[0] = Integer.parseInt(strs[0]);
			if (nums[0] < 0 || nums[0] > 23) {
				return -1;
			}
			nums[1] = Integer.parseInt(strs[1]);
			if (nums[1] < 0 || nums[1] > 59) {
				return -1;
			}
			return nums[0] * 100 + nums[1];
		} catch(Exception e) {
			return -1;
		}
	}
}

