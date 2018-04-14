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
package org.springframework.dsl.reconcile;

/**
 * Besides the methods below, the only hard requirement for a 'problem type' is
 * that it is a unique object that is not 'equals' to any other object.
 * <p>
 * It is probably nice if you implement a good toString however.
 * <p>
 * A good way to implement a discrete set of problemType objects is as an enum
 * that implements this interace.
 *
 * @author Kris De Volder
 * @author Janne Valkealahti
 *
 */
public interface ProblemType {

	ProblemSeverity getDefaultSeverity();

	String getCode();
}
