package models;

public class Flight {

	public int flight_num;
	public String source_city;
	public String dest_city;
	public int dep_time;
	public int arr_time;
	public int airfare;
	public int mileage;
	
	public Flight(int flight_num, String source_city, String dest_city, 
			int dep_time, int arr_time, int airfare, int mileage) {
	
		this.flight_num = flight_num;
		this.source_city = source_city;
		this.dest_city = dest_city;
		this.dep_time = dep_time;
		this.arr_time = arr_time;
		this.airfare = airfare;
		this.mileage = mileage;
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + airfare;
		result = prime * result + arr_time;
		result = prime * result + dep_time;
		result = prime * result
				+ ((dest_city == null) ? 0 : dest_city.hashCode());
		result = prime * result + flight_num;
		result = prime * result + mileage;
		result = prime * result
				+ ((source_city == null) ? 0 : source_city.hashCode());
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
		Flight other = (Flight) obj;
		if (airfare != other.airfare)
			return false;
		if (arr_time != other.arr_time)
			return false;
		if (dep_time != other.dep_time)
			return false;
		if (dest_city == null) {
			if (other.dest_city != null)
				return false;
		} else if (!dest_city.equals(other.dest_city))
			return false;
		if (flight_num != other.flight_num)
			return false;
		if (mileage != other.mileage)
			return false;
		if (source_city == null) {
			if (other.source_city != null)
				return false;
		} else if (!source_city.equals(other.source_city))
			return false;
		return true;
	}

	public int travelTime() {
		return Math.abs(this.arr_time - this.dep_time);
	}
	
	public String toString() {
		return String.format("%d %s %s %d %d %d %d", 
				this.flight_num, 
				this.source_city, 
				this.dest_city, 
				this.dep_time, 
				this.arr_time, 
				this.airfare, 
				this.mileage
		);
	}
}
