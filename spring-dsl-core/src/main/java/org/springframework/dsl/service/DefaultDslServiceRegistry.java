/*
 * Copyright 2018 the original author or authors.
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
package org.springframework.dsl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dsl.model.LanguageId;

/**
 * Default implementation of a {@link DslServiceRegistry} which resolves
 * services from an {@link ApplicationContext}.
 *
 * @author Janne Valkealahti
 *
 */
public class DefaultDslServiceRegistry implements DslServiceRegistry, ApplicationContextAware {

	private List<Completioner> completioners;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		initServices(applicationContext);
	}

	@Override
	public List<Completioner> getCompletioners(LanguageId languageId) {
		return completioners
				.stream()
				.filter(competioner -> competioner.getSupportedLanguageIds().contains(languageId))
				.collect(Collectors.toList());
	}

	protected void initServices(ApplicationContext applicationContext) {
		Map<String, Completioner> completionerBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
				Completioner.class, true, false);
		this.completioners = new ArrayList<>(completionerBeans.values());
	}
}
