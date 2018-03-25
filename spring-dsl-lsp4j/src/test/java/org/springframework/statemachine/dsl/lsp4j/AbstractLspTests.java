package org.springframework.statemachine.dsl.lsp4j;

import org.junit.After;
import org.junit.Before;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class AbstractLspTests {

	protected AnnotationConfigApplicationContext context;

	@Before
	public void setup() {
		context = buildContext();
	}

	@After
	public void clean() {
		if (context != null) {
			context.close();
		}
	}

	protected AnnotationConfigApplicationContext buildContext() {
		return null;
	}
}
