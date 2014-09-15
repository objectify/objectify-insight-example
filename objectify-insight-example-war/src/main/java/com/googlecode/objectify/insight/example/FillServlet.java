package com.googlecode.objectify.insight.example;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Make some go tasks
 */
@Singleton
@Slf4j
public class FillServlet extends HttpServlet {

	private static final long serialVersionUID = 1;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<TaskOptions> tasks = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			tasks.add(TaskOptions.Builder.withUrl("/go").method(Method.GET));
		}

		Queue queue = QueueFactory.getQueue("go");

		// 1M per fill, if it doesn't time out
		for (int i = 0; i < 10000; i++) {
			queue.addAsync(tasks);

			if (i % 1000 == 0)
				log.debug("Filled " + (i * 100));
		}

		resp.getWriter().print("Done filling");
	}

}
