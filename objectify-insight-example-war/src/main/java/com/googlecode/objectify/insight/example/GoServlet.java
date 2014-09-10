package com.googlecode.objectify.insight.example;

import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.Bigquery.Tables.List;
import com.googlecode.objectify.insight.puller.InsightDataset;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Arbitrary hook so that we can execute some code
 */
@Singleton
public class GoServlet extends HttpServlet {

	private static final long serialVersionUID = 1;

	private final Bigquery bigquery;
	private final InsightDataset insightDataset;

	@Inject
	public GoServlet(Bigquery bigquery, InsightDataset dataset) {
		this.bigquery = bigquery;
		this.insightDataset = dataset;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ofy().load().type(Thing.class).id(123L).now();

		final List list = bigquery.tables().list(insightDataset.projectId(), insightDataset.datasetId());

		resp.setContentType("text/plain");
		resp.getWriter().print("list is: " + list.toString());
	}
}
