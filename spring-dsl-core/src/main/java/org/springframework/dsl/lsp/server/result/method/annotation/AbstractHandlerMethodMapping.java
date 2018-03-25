/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.dsl.lsp.server.result.method.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.MethodIntrospector;
import org.springframework.dsl.lsp.server.HandlerMapping;
import org.springframework.dsl.lsp.server.HandlerMethod;
import org.springframework.dsl.lsp.server.ServerLspExchange;
import org.springframework.dsl.lsp.server.result.method.LspRequestMappingInfo;
import org.springframework.dsl.lsp.server.support.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

import reactor.core.publisher.Mono;

/**
 *
 * @author Janne Valkealahti
 *
 */
public abstract class AbstractHandlerMethodMapping extends ApplicationObjectSupport implements HandlerMapping, InitializingBean {

	private static final Log log = LogFactory.getLog(AbstractHandlerMethodMapping.class);

	private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

	private Map<LspRequestMappingInfo, HandlerMethod> registry = new HashMap<>();

	@Override
	public Mono<Object> getHandler(ServerLspExchange exchange) {
		return getHandlerInternal(exchange).map(handler -> {
			return handler;
		});
	}

	public Mono<HandlerMethod> getHandlerInternal(ServerLspExchange exchange) {
		HandlerMethod handlerMethod = null;

		try {
			handlerMethod = lookupHandlerMethod(exchange);
		}
		catch (Exception ex) {
			return Mono.error(ex);
		}

		if (handlerMethod != null) {
			handlerMethod = handlerMethod.createWithResolvedBean();
		}
		return Mono.justOrEmpty(handlerMethod);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initHandlerMethods();
	}

	@Nullable
	protected HandlerMethod lookupHandlerMethod(ServerLspExchange exchange)
			throws Exception {
		List<Match> matches = new ArrayList<>();
//		addMatchingMappings(this.mappingRegistry.getMappings().keySet(), matches, exchange);
		addMatchingMappings(registry.keySet(), matches, exchange);

		if (!matches.isEmpty()) {
			Comparator<Match> comparator = new MatchComparator(getMappingComparator(exchange));
			Collections.sort(matches, comparator);
//			if (logger.isTraceEnabled()) {
//				logger.trace("Found " + matches.size() + " matching mapping(s) for [" +
//						exchange.getRequest().getPath() + "] : " + matches);
//			}
			Match bestMatch = matches.get(0);
			if (matches.size() > 1) {
				Match secondBestMatch = matches.get(1);
				if (comparator.compare(bestMatch, secondBestMatch) == 0) {
					Method m1 = bestMatch.handlerMethod.getMethod();
					Method m2 = secondBestMatch.handlerMethod.getMethod();
					throw new IllegalStateException("Ambiguous handler methods mapped");
//					throw new IllegalStateException("Ambiguous handler methods mapped for HTTP path '" +
//							exchange.getRequest().getPath() + "': {" + m1 + ", " + m2 + "}");
				}
			}
			handleMatch(bestMatch.mapping, bestMatch.handlerMethod, exchange);
			return bestMatch.handlerMethod;
		}
		else {
			return null;
//			return handleNoMatch(this.mappingRegistry.getMappings().keySet(), exchange);
		}
	}

	protected Comparator<LspRequestMappingInfo> getMappingComparator(final ServerLspExchange exchange) {
		return (info1, info2) -> info1.compareTo(info2, exchange);
	}

	protected void handleMatch(LspRequestMappingInfo info, HandlerMethod handlerMethod,
			ServerLspExchange exchange) {

//		PathContainer lookupPath = exchange.getRequest().getPath().pathWithinApplication();
//
//		PathPattern bestPattern;
//		Map<String, String> uriVariables;
//		Map<String, MultiValueMap<String, String>> matrixVariables;
//
//		Set<PathPattern> patterns = info.getPatternsCondition().getPatterns();
//		if (patterns.isEmpty()) {
////			bestPattern = getPathPatternParser().parse(lookupPath.value());
////			uriVariables = Collections.emptyMap();
////			matrixVariables = Collections.emptyMap();
//		}
//		else {
//			bestPattern = patterns.iterator().next();
//			PathPattern.PathMatchInfo result = bestPattern.matchAndExtract(lookupPath);
//			Assert.notNull(result, () ->
//					"Expected bestPattern: " + bestPattern + " to match lookupPath " + lookupPath);
//			uriVariables = result.getUriVariables();
//			matrixVariables = result.getMatrixVariables();
//		}

//		exchange.getAttributes().put(BEST_MATCHING_HANDLER_ATTRIBUTE, handlerMethod);
//		exchange.getAttributes().put(BEST_MATCHING_PATTERN_ATTRIBUTE, bestPattern);
//		exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriVariables);
//		exchange.getAttributes().put(MATRIX_VARIABLES_ATTRIBUTE, matrixVariables);
//
//		if (!info.getProducesCondition().getProducibleMediaTypes().isEmpty()) {
//			Set<MediaType> mediaTypes = info.getProducesCondition().getProducibleMediaTypes();
//			exchange.getAttributes().put(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, mediaTypes);
//		}
	}

	private void addMatchingMappings(Collection<LspRequestMappingInfo> mappings, List<Match> matches, ServerLspExchange exchange) {
		for (LspRequestMappingInfo mapping : mappings) {
			LspRequestMappingInfo match = getMatchingMapping(mapping, exchange);
			if (match != null) {
//				matches.add(new Match(match, this.mappingRegistry.getMappings().get(mapping)));
				matches.add(new Match(match, registry.get(mapping)));
			}
		}
	}

	protected LspRequestMappingInfo getMatchingMapping(LspRequestMappingInfo info, ServerLspExchange exchange) {
		return info.getMatchingCondition(exchange);
	}

	protected void initHandlerMethods() {
		String[] beanNames = obtainApplicationContext().getBeanNamesForType(Object.class);

		for (String beanName : beanNames) {
			if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
				Class<?> beanType = null;
				try {
					beanType = obtainApplicationContext().getType(beanName);
				}
				catch (Throwable ex) {
					// An unresolvable bean type, probably from a lazy bean - let's ignore it.
					if (logger.isDebugEnabled()) {
						logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
					}
				}
				if (beanType != null && isHandler(beanType)) {
					detectHandlerMethods(beanName);
				}
			}
		}
	}

	protected abstract boolean isHandler(Class<?> beanType);

	protected void detectHandlerMethods(final Object handler) {
		Class<?> handlerType = (handler instanceof String ?
				obtainApplicationContext().getType((String) handler) : handler.getClass());

		if (handlerType != null) {
			final Class<?> userType = ClassUtils.getUserClass(handlerType);
//			Map<Method, ?> methods = MethodIntrospector.selectMethods(userType,
//					(MethodIntrospector.MetadataLookup<?>) method -> getMappingForMethod(method, userType));
			Map<Method, LspRequestMappingInfo> methods = MethodIntrospector.selectMethods(userType,
					(MethodIntrospector.MetadataLookup<LspRequestMappingInfo>) method -> getMappingForMethod(method, userType));
			if (log.isDebugEnabled()) {
				log.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
			}
			methods.forEach((key, mapping) -> {
				Method invocableMethod = AopUtils.selectInvocableMethod(key, userType);
				registerHandlerMethod(handler, invocableMethod, mapping);
			});
		}
	}

	protected void registerHandlerMethod(Object handler, Method method, LspRequestMappingInfo mapping) {
		HandlerMethod handlerMethod = createHandlerMethod(handler, method);
		registry.put(mapping, handlerMethod);
	}

	protected HandlerMethod createHandlerMethod(Object handler, Method method) {
		HandlerMethod handlerMethod;
		if (handler instanceof String) {
			String beanName = (String) handler;
			handlerMethod = new HandlerMethod(beanName,
					obtainApplicationContext().getAutowireCapableBeanFactory(), method);
		}
		else {
			handlerMethod = new HandlerMethod(handler, method);
		}
		return handlerMethod;
	}


	protected LspRequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
		LspRequestMappingInfo info = createRequestMappingInfo(method);
		if (info != null) {
			LspRequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
			if (typeInfo != null) {
				info = typeInfo.combine(info);
			}
		}
		return info;
	}

	protected abstract LspRequestMappingInfo createRequestMappingInfo(AnnotatedElement element);


	private static class MappingRegistration<T> {

		private final T mapping;

		private final HandlerMethod handlerMethod;

		public MappingRegistration(T mapping, HandlerMethod handlerMethod) {
			Assert.notNull(mapping, "Mapping must not be null");
			Assert.notNull(handlerMethod, "HandlerMethod must not be null");
			this.mapping = mapping;
			this.handlerMethod = handlerMethod;
		}

		public T getMapping() {
			return this.mapping;
		}

		public HandlerMethod getHandlerMethod() {
			return this.handlerMethod;
		}

	}


	/**
	 * A thin wrapper around a matched HandlerMethod and its mapping, for the purpose of
	 * comparing the best match with a comparator in the context of the current request.
	 */
	private class Match {

		private final LspRequestMappingInfo mapping;

		private final HandlerMethod handlerMethod;

		public Match(LspRequestMappingInfo mapping, HandlerMethod handlerMethod) {
			this.mapping = mapping;
			this.handlerMethod = handlerMethod;
		}

		@Override
		public String toString() {
			return this.mapping.toString();
		}
	}


	private class MatchComparator implements Comparator<Match> {

		private final Comparator<LspRequestMappingInfo> comparator;

		public MatchComparator(Comparator<LspRequestMappingInfo> comparator) {
			this.comparator = comparator;
		}

		@Override
		public int compare(Match match1, Match match2) {
			return this.comparator.compare(match1.mapping, match2.mapping);
		}
	}

}
