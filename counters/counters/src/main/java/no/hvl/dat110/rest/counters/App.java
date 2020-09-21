package no.hvl.dat110.rest.counters;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


import com.google.gson.Gson;
import entities.Todo;

import static spark.Spark.*;


public class App {

	private static final String PERSISTENCE_UNIT_NAME = "todos";
	private static EntityManagerFactory factory;

	static Todo todo = null;

	public static void main(String[] args) {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager manager = factory.createEntityManager();

		if (args.length > 0) {
			port(Integer.parseInt(args[0]));
		} else {
			port(8080);
		}

		after((req, res) -> {
  		  res.type("application/json");
  		});

        get("/todos/:todoid", (req, res) -> {
			manager.getTransaction().begin();
			todo = manager.find(Todo.class, new Long(req.params(":todoid")));
        	manager.getTransaction().commit();
        	return todo.toJson();
		});

        put("/todos", (req, res) -> {
        	Todo updated = new Gson().fromJson(req.body(), Todo.class);
        	manager.getTransaction().begin();
        	manager.merge(updated);
        	manager.getTransaction().commit();

        	return updated.toJson();
		});

        post("/todos", (req, res) -> {
			Todo make = new Gson().fromJson(req.body(), Todo.class);
			manager.getTransaction().begin();
			manager.persist(make);
        	manager.getTransaction().commit();

        	return make.toJson();
		});

        delete("/todos", (req, res) -> {
			//Create Todo-object so its possible to get the id(PK) that is to be deleted from database.
        	Todo delete = new Gson().fromJson(req.body(), Todo.class);
        	manager.getTransaction().begin();
			delete = manager.find(Todo.class, delete.getId());
        	manager.remove(delete);
        	manager.getTransaction().commit();

        	return delete.toJson();
		});
    }
}
