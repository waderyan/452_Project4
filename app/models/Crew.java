package models;

public class Crew {

	public static int NULL = -10001;
	
	public int id;
	public String name;
	public int salary;
	public String position;
	public int seniority;
	public int fly_hours;
	public int mgrid;

	public Crew (int id, String name, int salary, String position, 
			int seniority, int fly_hours, int mgrid) {
		
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.position = position;
		this.seniority = seniority;
		this.fly_hours = fly_hours;
		this.mgrid = mgrid;
    }
	
	public Crew (String name, int salary, String position, 
			int seniority, int fly_hours, int mgrid) {
		
		this.name = name;
		this.salary = salary;
		this.position = position;
		this.seniority = seniority;
		this.fly_hours = fly_hours;
		this.mgrid = mgrid;
    }
	
	public Crew (String name, int salary, String position, 
			int seniority, int fly_hours) {
		
		this.name = name;
		this.salary = salary;
		this.position = position;
		this.seniority = seniority;
		this.fly_hours = fly_hours;
    }

    public String toString() {
    	return String.format("id: %d, name: %s, salary: %d, position: %s, seniority: %d, fly_hours: %d, mgrid: %d"
    						, this.id, this.name, this.salary, this.position, this.seniority, this.fly_hours, this.mgrid);
    }

}