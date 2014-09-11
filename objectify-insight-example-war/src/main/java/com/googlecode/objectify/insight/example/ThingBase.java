package com.googlecode.objectify.insight.example;

import com.googlecode.objectify.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Some arbitrary entity
 */
@Data
@NoArgsConstructor
public class ThingBase {
	private @Id Long id;

	private double data;

	public ThingBase(Long id) {
		this.id = id;
		this.data = Math.random();
	}
}
