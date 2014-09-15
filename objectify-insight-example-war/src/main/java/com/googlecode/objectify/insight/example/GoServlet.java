package com.googlecode.objectify.insight.example;

import com.google.appengine.api.NamespaceManager;
import com.googlecode.objectify.Key;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Arbitrary hook so that we can execute some code
 */
@Singleton
@Slf4j
public class GoServlet extends HttpServlet {

	private static final long serialVersionUID = 1;

	/** Number of entities we process normally on a read or write */
	private static final int WORKSPACE = 1000;

	private Random rnd = new Random();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		NamespaceManager.set("namespace" + rnd.nextInt(4));

		try {
			switch (rnd.nextInt(4)) {
				case 0: read(); break;
				case 1: write(); break;
				case 2: query(); break;
				case 4: delete(); break;
			}
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			NamespaceManager.set(null);
		}
	}

	private void delete() {
		Key<?> key = pickKey();

		log.debug("Deleting " + key);

		ofy().delete().key(key).now();
	}

	private void query() {
		Class<? extends ThingBase> thingClass = pickThing();

		log.debug("Querying for " + thingClass);

		for (Object o: ofy().load().type(thingClass).filterKey(">", Key.create(thingClass, pickId())).limit(2))
			;
	}

	private void write() throws Exception {
		switch (rnd.nextInt(3)) {
			case 0:
			case 1: update(); break;
			case 2: insert(); break;
		}
	}

	private void insert() throws Exception {
		Class<? extends ThingBase> thingClass = pickThing();
		ThingBase thing = thingClass.newInstance();
		thing.setData(rnd.nextDouble());

		log.debug("Inserting " + thing);

		ofy().save().entity(thing).now();
	}

	private void update() throws Exception {
		Class<? extends ThingBase> thingClass = pickThing();
		ThingBase thing = thingClass.newInstance();
		thing.setId(pickId());
		thing.setData(rnd.nextDouble());

		log.debug("Updating " + thing);

		ofy().save().entity(thing).now();
	}

	private void read() {
		Key<?> key = pickKey();

		log.debug("Reading " + key.getRaw());

		ofy().load().key(key).now();
	}

	private Key<?> pickKey() {
		return Key.create(pickThing(), pickId());
	}

	private long pickId() {
		return rnd.nextInt(WORKSPACE) + 1;    // 0 is not allowed
	}

	/** Get a random Thing class */
	private Class<? extends ThingBase> pickThing() {
		switch (rnd.nextInt(6)) {
			case 0:
			case 1:
			case 2: return Thing1.class;

			case 3:
			case 4: return Thing2.class;

			default: return Thing3.class;
		}
	}
}
