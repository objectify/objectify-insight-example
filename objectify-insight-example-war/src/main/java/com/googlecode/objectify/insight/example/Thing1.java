package com.googlecode.objectify.insight.example;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.insight.Collect;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Some arbitrary entity
 */
@Collect
@Entity
@Data
@NoArgsConstructor
public class Thing1 extends ThingBase {
	public Thing1(Long id) {
		super(id);
	}
}
