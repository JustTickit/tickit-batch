package com.tickit.batch.logging;

import org.slf4j.MDC;

public class TraceContext {

	private static final String TRACE_ID = "traceId";

	public static void setTraceId(String traceId) {
		MDC.put(TRACE_ID, traceId);
	}

	public static String getTraceId() {
		return MDC.get(TRACE_ID);
	}

	public static void clear() {
		MDC.remove(TRACE_ID);
	}
}