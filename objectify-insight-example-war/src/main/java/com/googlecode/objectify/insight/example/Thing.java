package com.googlecode.objectify.insight.example;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import lombok.Data;

/**
 * Some arbitrary entity
 */
@Entity
@Data
public class Thing {
	@Id Long id;

	String data;
}
