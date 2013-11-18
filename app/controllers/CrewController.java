package controllers;

import java.sql.SQLException;
import java.util.Map;

import models.Assign;
import models.Crew;
import play.data.DynamicForm;
import play.mvc.*;

import dao.AssignDao;
import dao.CrewDao;

public class CrewController extends Controller {
	
	public static Result insert() {
		DynamicForm data = new DynamicForm().bindFromRequest();
		if (data.hasErrors()) {
			return badRequest("form has errors");
		}
		try {
			String name = CrewFormHelper.name(data.get("name"));
			int salary = CrewFormHelper.salary(data.get("salary"));
			String position = CrewFormHelper.position(data.get("position"));
			int seniority = CrewFormHelper.seniority(data.get("seniority"));
			int flyHours = CrewFormHelper.flyhours(data.get("fly-hours"));
			int mgrid = CrewFormHelper.mgrid(data.get("mgr"));
			int id = CrewDao.insert(new Crew(name, salary, position, seniority, flyHours, mgrid));
			
			for (Map.Entry<String, String> entry : data.data().entrySet()) {
				if (entry.getKey().startsWith("flight")) {
					AssignDao.insert(new Assign(id, CrewFormHelper.parseFlightNum(data.data().get(entry.getKey()))));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return badRequest("error inserting crew and assign data " + e.getMessage());
		}
		
		return redirect(routes.Application.getDb());
	}
	
	public static Result update() {
		DynamicForm data = new DynamicForm().bindFromRequest();
		if (data.hasErrors()) {
			return badRequest("form has errors");
		}
		try {
			int id = Integer.parseInt(data.get("original_id").split(" ")[0]);
			Crew original = CrewDao.get(id);
			
			String name = CrewFormHelper.validName(data.get("name")) ? data.get("name") : original.name;
			int salary = CrewFormHelper.validSalary(data.get("salary")) ? CrewFormHelper.salary(data.get("salary")) : original.salary;
			String position = CrewFormHelper.validPosition(data.get("position")) ? CrewFormHelper.position(data.get("position")) : original.position;
			int seniority = CrewFormHelper.validSeniority(data.get("seniority")) ? CrewFormHelper.seniority(data.get("seniority")) : original.seniority;
			int flyHours = CrewFormHelper.validFlyHours(data.get("fly-hours")) ? CrewFormHelper.flyhours(data.get("fly-hours")) : original.fly_hours;
			int mgrid = CrewFormHelper.validMgrid(data.get("mgr")) ? CrewFormHelper.mgrid(data.get("mgr")) : original.mgrid;
			
			if (id == mgrid) {
				throw new RuntimeException("id and mgrid are the same!");
			}
			
			CrewDao.update(id, new Crew(name, salary, position, seniority, flyHours, mgrid));	
		} catch (Exception e) {
			e.printStackTrace();
			return badRequest("error updating crew " + e.getMessage());
		}
		return redirect(routes.Application.getDb());
	}
	
	public static Result delete() {
		DynamicForm d = new DynamicForm().bindFromRequest();
		try {
			int id = Integer.parseInt(d.get("id").split(" ")[0]);
			AssignDao.delete(id);
			CrewDao.setMgrToNull(id);
			return CrewDao.delete(id) ? redirect(routes.Application.getDb()) : badRequest("error deleting");
		} catch (SQLException e) {
			return badRequest("error deleting crew " + e.getMessage());
		}
	}
}

class CrewFormHelper{
	
	static String name(String s) {
		if (!validName(s)) {
			throw new RuntimeException("invalid name");
		}
		return s;
	}
	
	static int parseFlightNum(String s) {
		return Integer.parseInt(s.split(":")[0]);
	}
	
	static int salary(String s) {
		try {
			int result = Integer.parseInt(s);
			if (result < 0) {
				throw new Exception("salary cannot be negative");
			}
			return result;
		} catch(Exception e) {
			throw new RuntimeException("invalid salary");
		}
	}

	static String position(String s) {
		if (validPosition(s)) {
			return s;
		} else {
			throw new RuntimeException("invalid position");
		}
	}

	static int seniority(String s) {
		try {
			int result = Integer.parseInt(s);
			if (result < 0) {
				throw new Exception("seniority cannot be negative");
			}
			return result;
		} catch(Exception e) {
			throw new RuntimeException("invalid seniority");
		}
	}
	
	static int flyhours(String s) {
		try {
			int result = Integer.parseInt(s);
			if (result < 0) {
				throw new Exception("fly hours cannot be negative");
			}
			return result;
		} catch(Exception e) {
			throw new RuntimeException("invalid flyhours");
		}
	}

	static int mgrid(String s) {
		if (s.equals("null")) {
			return Crew.NULL;
		}
		try {
			int result = Integer.parseInt(s.split(" ")[0]);
			if (result < 0) {
				throw new Exception("mgrid cannot be negative");
			}
			return result;
		} catch(Exception e) {
			throw new RuntimeException("invalid mgrid");
		}
	}

	static boolean validName(String name) {
		return name != null && name.trim() != "";
	}
	
	static boolean validSalary(String salary) {
		try {
			Integer.parseInt(salary);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	static boolean validPosition(String pos) {
		return pos != null && pos.trim() != "";
	}
	
	static boolean validSeniority(String sen) {
		try {
			Integer.parseInt(sen);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	static boolean validFlyHours(String fly) {
		try {
			Integer.parseInt(fly);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	static boolean validMgrid(String mgr) {
		if (mgr.equals("null")) {
			return true;
		}
		try {
			mgr = mgr.split(" ")[0];
			Integer.parseInt(mgr);
		} catch(Exception e) {
			return false;
		}
		return true;
	}

}

