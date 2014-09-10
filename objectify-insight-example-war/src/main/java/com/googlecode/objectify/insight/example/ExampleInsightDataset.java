package com.googlecode.objectify.insight.example;

import com.googlecode.objectify.insight.puller.InsightDataset;

/**
 * How we communicate to Insight which project/dataset ids we use
 */
public class ExampleInsightDataset implements InsightDataset {

	/** Note: this is the id of the bigquery project */
	@Override
	public String projectId() {
		return "objectify-insight-test";
	}

	/** The dataset to which we will write our query results */
	@Override
	public String datasetId() {
		return "insight-example";
	}
}
