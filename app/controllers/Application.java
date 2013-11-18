package controllers;

import java.util.ArrayList;

import models.Route;
import models.UserOption;

import play.api.Routes;
import play.mvc.*;
import static play.libs.Json.toJson;

import dao.AssignDao;
import dao.CrewDao;
import dao.DbInit;
import dao.FlightDao;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return Application.admin();
    }

    public static Result cheapest() {
    	return ok(index.render(cheapest.render(new ArrayList<Route>(), new ArrayList<UserOption>())));
    }
    
    public static Result admin() {
    	return ok(index.render(admin.render(FlightDao.getAll(), CrewDao.getAll())));
    }
    
    public static Result shortest() {
    	return ok(index.render(shortest.render(new ArrayList<Route>(), new ArrayList<UserOption>())));
    }
    
    public static Result special() {
    	return ok(index.render(special.render(new ArrayList<Route>(), new ArrayList<UserOption>())));
    }
    
    public static Result getDb() {
    	return ok(index.render(database.render(CrewDao.getAll(), FlightDao.getAll(), AssignDao.getAll())));
    }
    
    public static Result resetDb() {
    	DbInit.reset();
    	return Application.getDb();
    }

}
