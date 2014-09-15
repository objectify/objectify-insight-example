package com.googlecode.objectify.insight.example;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.insight.InsightAsyncDatastoreService;
import com.googlecode.objectify.insight.Recorder;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Override the factory to use the Insight service as the "raw" datastore
 */
@Singleton
@Slf4j
public class OfyFactory extends ObjectifyFactory {

	/** */
	private final Recorder recorder;

	/** */
	@Inject
	public OfyFactory(Recorder recorder) {
		// Important to replace the static singleton instance!
		ObjectifyService.setFactory(this);

		this.recorder = recorder;

		recorder.getCollector().setAgeThresholdMillis(2000);

		register(Thing1.class);
		register(Thing2.class);
		register(Thing3.class);
	}

	/** */
	@Override
	protected AsyncDatastoreService createRawAsyncDatastoreService(DatastoreServiceConfig cfg) {
		AsyncDatastoreService raw = super.createRawAsyncDatastoreService(cfg);

		//return raw;
		return new InsightAsyncDatastoreService(raw, recorder);
	}
}
