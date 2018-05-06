// taking partial grammar from a spring statemachine to test various concepts
parser grammar Test2Grammar;

options {
  tokenVocab=Test2Lexer;
}

definitions          :   ( statemachine | machineObjectList) ;
machineObjectList    :   ( state | transition )* ;
statemachine         :   STATEMACHINE id LBRACE machineObjectList RBRACE ;
state                :   STATE id LBRACE stateParameters RBRACE ;
transition           :   TRANSITION id? LBRACE transitionParameters RBRACE ;
stateParameters      :   ( stateParameter SEMI? )* ;
stateParameter       :   stateType id? ;
stateType            :   INITIAL | END ;
transitionParameters :   ( transitionParameter SEMI? )* ;
transitionParameter  :   transitionType id? ;
transitionType       :   SOURCE | TARGET ;
id                   :   ID ;