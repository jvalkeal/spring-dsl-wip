package org.springframework.dsl.lsp.domain;

public enum DiagnosticSeverity {

	Error(1),

	Warning(2),

	Information(3),

	Hint(4);

	private final int value;

	DiagnosticSeverity(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static DiagnosticSeverity forValue(int value) {
		DiagnosticSeverity[] allValues = DiagnosticSeverity.values();
		if (value < 1 || value > allValues.length)
			throw new IllegalArgumentException("Illegal enum value: " + value);
		return allValues[value - 1];
	}


}
