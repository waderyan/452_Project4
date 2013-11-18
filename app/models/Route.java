package models;

import java.util.List;

public class Route implements Comparable<Route> {

	public int num;
	public List<Flight> flights;
	
	public Route(int total_fare, List<Flight> flights) {
		this.num = total_fare;
		this.flights = flights;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Num: ");
		sb.append(num);
		sb.append(" ");
		for (Flight f : flights) {
			sb.append(f.toString());
			sb.append(" ");
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flights == null) ? 0 : flights.hashCode());
		result = prime * result + num;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		if (flights == null) {
			if (other.flights != null)
				return false;
		} else if (!flights.equals(other.flights))
			return false;
		if (num != other.num)
			return false;
		return true;
	}

	public int compareTo(Route other) {
		if (this.num < other.num) {
			return -1;
		} else if (this.num > other.num) {
			return 1;
		}
		return 0;
	}
}
