package com.googlecode.objectify.insight.example;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.utils.SystemProperty.Environment.Value;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.insight.Flusher;
import com.googlecode.objectify.insight.puller.InsightDataset;
import com.googlecode.objectify.insight.servlet.GuicePullerServlet;
import com.googlecode.objectify.insight.servlet.GuiceTableMakerServlet;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 */
@Slf4j
public class GuiceListener extends GuiceServletContextListener {

	/** */
	private static final String P12_LOCATION = "WEB-INF/privatekey.p12";
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport TRANSPORT = new NetHttpTransport();

	/**
	 */
	public static class OurModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(ObjectifyFilter.class).in(Singleton.class);

			bind(InsightDataset.class).to(ExampleInsightDataset.class);

			bind(OfyFactory.class).asEagerSingleton();
		}

		@Provides
		public Bigquery bigquery(HttpRequestInitializer credential) {
			return new Bigquery.Builder(TRANSPORT, JSON_FACTORY, credential)
					.setApplicationName(SystemProperty.applicationId.get())
					.build();
		}

		@Provides
		public HttpRequestInitializer credential(ServletContext ctx) throws GeneralSecurityException, IOException {
			if (SystemProperty.environment.value() == Value.Production) {
				return new AppIdentityCredential(Collections.singleton(BigqueryScopes.BIGQUERY));
			} else {
				String path = ctx.getRealPath(P12_LOCATION);
				return new GoogleCredential.Builder()
						.setTransport(TRANSPORT)
						.setJsonFactory(JSON_FACTORY)
						.setServiceAccountId("711533592980-godahdtbk3o91r83lhbbcf4hgigr39e7@developer.gserviceaccount.com")
						.setServiceAccountScopes(Collections.singleton(BigqueryScopes.BIGQUERY))
						.setServiceAccountPrivateKeyFromP12File(new File(path))
						.build();
			}
		}

		@Provides
		@Named("insight")
		public Queue queue() {
			return QueueFactory.getQueue(Flusher.DEFAULT_QUEUE);
		}
	}

	/**
	 */
	public static class OurServletModule extends ServletModule {
		@Override
		protected void configureServlets() {
			filter("/*").through(ObjectifyFilter.class);

			serve("/go").with(GoServlet.class);
			serve("/fill").with(FillServlet.class);

			serve("/private/tableMaker").with(GuiceTableMakerServlet.class);
			serve("/private/puller").with(GuicePullerServlet.class);
		}
	}

	/**
	 */
	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new OurModule(), new OurServletModule());
	}
}
