package com.googlecode.objectify.insight.example;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.insight.Flusher;
import com.googlecode.objectify.insight.puller.InsightDataset;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 */
@Slf4j
public class GuiceListener extends GuiceServletContextListener {

	/** */
	private static final String CLIENTSECRETS_LOCATION = "/client_secret.json";
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
		public Bigquery bigquery(Credential credential) {
			return new Bigquery.Builder(TRANSPORT, JSON_FACTORY, credential)
					.setApplicationName(SystemProperty.applicationId.get())
					.build();
		}

		@Provides
		public GoogleClientSecrets googleClientSecrets() throws IOException {
			return GoogleClientSecrets.load(
					new JacksonFactory(),
					new InputStreamReader(this.getClass().getResourceAsStream(CLIENTSECRETS_LOCATION)));
		}

		@Provides
		public Credential credential(GoogleClientSecrets googleClientSecrets) {
			return new GoogleCredential.Builder()
					.setTransport(TRANSPORT)
					.setJsonFactory(JSON_FACTORY)
					.setClientSecrets(googleClientSecrets)
					.build();
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
		}
	}

	/**
	 */
	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new OurModule(), new OurServletModule());
	}
}
