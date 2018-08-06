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
package org.springframework.dsl.antlr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.dsl.antlr.support.DefaultAntlrCompletionEngine.CandidatesCollection;
import org.springframework.dsl.domain.Position;

/**
 * Defines a contract how completion infomation can be requested from
 * {@code ANTLR}.
 *
 * @author Janne Valkealahti
 *
 */
public interface AntlrCompletionEngine {

//	CandidatesCollection collectCandidates(int caretTokenIndex, ParserRuleContext context);
	CandidatesCollection collectCandidates(Position position, ParserRuleContext context);

}
