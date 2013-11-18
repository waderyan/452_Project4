package test;
import static org.fest.assertions.Assertions.assertThat;

import java.util.*;

import models.Flight;
import models.Route;

import org.junit.Test;

import dao.FlightDao;
import dao.RouteDao;


public class FlightControllerTest {

	private static final Map<Integer, Flight> flights;
	static {
		flights = new HashMap<Integer, Flight>();
		List<Flight> fs = FlightDao.getAll();
		for (Flight f : fs) {
			flights.put(f.flight_num, f);
		}
	}
	
    @Test
    public void cheapest() {
    	Collection<Route> expected = new ArrayList<Route>();
    	List<Flight> flights = new ArrayList<Flight>();
    	
    	flights.add(flights.get(111));
    	expected.add(new Route(200, flights));
    	assertThat(RouteDao.getCheapestRoute("Kansas-City", "SLC")).isEqualTo(expected);
    }


}