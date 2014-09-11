package com.googlecode.objectify.insight.example;

import com.googlecode.objectify.annotation.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Some arbitrary entity
 */
@Entity
@Data
@NoArgsConstructor
public class Thing3 extends ThingBase {
	public Thing3(Long id) {
		super(id);
	}
}
