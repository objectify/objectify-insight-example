package com.googlecode.objectify.insight.example;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.insight.InsightAsyncDatastoreService;
import com.googlecode.objectify.insight.InsightCollector;
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
	private final InsightCollector collector;

	/** */
	@Inject
	public OfyFactory(InsightCollector collector) {
		// Important to replace the static singleton instance!
		ObjectifyService.setFactory(this);

		this.collector = collector;

		collector.setAgeThresholdMillis(2000);

		register(Thing.class);
	}

	/** */
	@Override
	protected AsyncDatastoreService createRawAsyncDatastoreService(DatastoreServiceConfig cfg) {
		AsyncDatastoreService raw = super.createRawAsyncDatastoreService(cfg);

		log.debug("Creating insight async datastore");

		return new InsightAsyncDatastoreService(raw, collector);
	}
}
