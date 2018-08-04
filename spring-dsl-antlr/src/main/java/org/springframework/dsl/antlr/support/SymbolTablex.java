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
package org.springframework.dsl.antlr.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class SymbolTablex {

	enum MemberVisibility {
//	    Invalid = -1,
//	    Public = 0,
		Invalid, Public, Protected, Private, Library;
	};

	enum TypeKind {
		Integer, Float, String, Boolean, Date, Class, Array, Alias,
	};

	enum ReferenceKind {
		Irrelevant,
		Pointer, // Default for most languages for dynamically allocated memory ("Type*" in C++).
		Reference, // "Type&" in C++
		Instance, // "Type" as such and default for all value types.
	}

	interface Type {
	    String getName();

	    // The super type of this type or empty if this is a fundamental type.
	    // Also used as the target type for type aliases.
	    Type[] getBaseTypes();
	    TypeKind getTypeKind();
	    ReferenceKind getReferenceKind();
	};

	interface SymbolTableOptions {
		boolean allowDuplicateSymbols();
	}

	// A single class for all fundamental types. They are distinguished via the kind field.
	static class FundamentalType implements Type {

		private String name;
		private TypeKind typeKind;
		private ReferenceKind referenceKind;
		static FundamentalType integerType = new FundamentalType("int", TypeKind.Integer, ReferenceKind.Instance);
		static FundamentalType floatType = new FundamentalType("float", TypeKind.Float, ReferenceKind.Instance);
		static FundamentalType stringType = new FundamentalType("string", TypeKind.String, ReferenceKind.Instance);
		static FundamentalType boolType = new FundamentalType("bool", TypeKind.Boolean, ReferenceKind.Instance);
		static FundamentalType dateType = new FundamentalType("date", TypeKind.Date, ReferenceKind.Instance);

		public FundamentalType(String name, TypeKind typeKind, ReferenceKind referenceKind) {
			this.name = name;
			this.typeKind = typeKind;
			this.referenceKind = referenceKind;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Type[] getBaseTypes() {
			return new Type[0];
		}

		@Override
		public TypeKind getTypeKind() {
			return typeKind;
		}

		@Override
		public ReferenceKind getReferenceKind() {
			return referenceKind;
		}
	}


	// The root of the symbol table class hierarchy: a symbol can be any manageable entity (like a block), not only
	// things like variables or classes.
	// We are using a class hierarchy here, instead of an enum or similar, to allow for easy extension and certain
	// symbols can so provide additional APIs for simpler access to their sub elements, if needed.
	static class Symbol {
	    public String name; // The name of the scope or empty if anonymous.
	    public ParseTree context; // Reference to the parse tree which contains this symbol.

	    Symbol parent;

	    Symbol(String name) {
	        this.name = StringUtils.hasText(name) ? name : "";
	    }

	    /**
	     * The parent is usually a scoped symbol as only those can have children, but we allow
	     * any symbol here for special scenarios.
	     * This is rather an internal method and should rarely be used by external code.
	     */
	    public void setParent(Symbol parent) {
	        this.parent = parent;
	    }

	    public Symbol getParent() {
			return parent;
		}

	    public Symbol getFirstSibling() {
	        if (this.parent instanceof ScopedSymbol) {
	            return ((ScopedSymbol)this.parent).firstChild();
	        }
	        return this;
	    }

	    /**
	     * Returns the symbol before this symbol in it's scope.
	     */
	    public Symbol getPreviousSibling() {
	        if (!(this.parent instanceof ScopedSymbol)) {
	            return this;
	        }

	        return ((ScopedSymbol)this.parent).getPreviousSiblingOf(this);
	    }

	    /**
	     * Returns the symbol following this symbol in it's scope.
	     */
	    public Symbol getNextSibling() {
	        if (!(this.parent instanceof ScopedSymbol)) {
	            return this;
	        }

	        return ((ScopedSymbol)this.parent).nextSiblingOf(this);
	    }

	    public Symbol getLastSibling() {
	        if (this.parent instanceof ScopedSymbol) {
	            return ((ScopedSymbol)this.parent).lastChild();
	        }

	        return this;
	    }

	    /**
	     * Returns the next symbol in definition order, regardless of the scope.
	     */
	    public Symbol getNext() {
	        if (this.parent instanceof ScopedSymbol) {
	            return ((ScopedSymbol)this.parent).getNextOf(this);
	        }
	        return null;
	    }

	    public void removeFromParent() {
	        if (this.parent instanceof ScopedSymbol) {
	        	((ScopedSymbol)this.parent).removeSymbol(this);
	            this.parent = null;
	        }
	    }

	    /**
	     * Returns the first symbol with a given name, in the order of appearance in this scope
	     * or any of the parent scopes (conditionally).
	     */
	    public Symbol resolve(String name, boolean localOnly) {
	        if (this.parent instanceof ScopedSymbol) {
	            return this.parent.resolve(name, localOnly);
	        }
	        return null;
	    }

	    /**
	     * Get the outermost entity (below the symbol table) that holds us.
	     */
	    public Symbol getRoot() {
	        Symbol run = this.parent;
	        while (run != null) {
	            if (run.parent != null || run.parent instanceof SymbolTable) {
	            	return run;
	            }

	            run = run.parent;
	        }
	        return run;
	    }

	    /**
	     * Returns the symbol table we belong too or undefined if we are not yet assigned.
	     */
	    public SymbolTable getSymbolTable() {
//	    	if (ClassUtils.isAssignable(this.getClass(), SymbolTable.class)) {
//	    		return this;
//	    	}
	        if (this instanceof SymbolTable) {
	            return (SymbolTable) this;
	        }

	        Symbol run = this.parent;
	        while (run != null) {
	            if (run instanceof SymbolTable) {
	                return (SymbolTable) run;
	            }
	            run = run.parent;
	        }
	        return null;
	    }

	    /**
	     * Returns the next enclosing parent of the given type.
	     */
//	    public getParentOfType<T extends Symbol>(t: new (...args: any[]) => T): T | undefined {
	    public <T extends Symbol> T getParentOfType() {
	        Symbol run = this.parent;
	        while (run != null) {
//	            if (run instanceof T)
//	                return <T>run;
	            run = run.parent;
	        }
	        return null;
	    }

	    /**
	     * The list of symbols from this one up to root.
	     */
	    public Symbol[] getSymbolPath() {
//	        let result: Symbol[] = [];
	    	ArrayList<Symbol> result = new ArrayList<>();
	    	Symbol run = this;
	        while (run != null) {
//	            result.push(run);
//	            if (!run.parent)
//	                break;
	            run = run.parent;
	        }
	        return result.toArray(new Symbol[0]);
	    }

	    /**
	     * Creates a qualified identifier from this symbol and its parent.
	     * If `full` is true then all parents are traversed in addition to this instance.
	     */
	    public String qualifiedName() {

	    	return null;
	    }
//	    public qualifiedName(separator = ".", full = false, includeAnonymous = false): string {
//	        if (!includeAnonymous && this.name.length == 0)
//	            return "";
//
//	        let result: string = this.name.length == 0 ? "<anonymous>" : this.name;
//	        let run = this._parent;
//	        while (run) {
//	            if (includeAnonymous || run.name.length > 0) {
//	                result = (run.name.length == 0 ? "<anonymous>" : run.name) + separator + result;
//	                if (!full || !run._parent)
//	                    break;
//	            }
//	            run = run._parent;
//	        }
//	        return result;
//	    }
	}



	// A symbol with an attached type (variables, fields etc.).
	static class TypedSymbol extends Symbol {
	    public Type type;

	    public TypedSymbol(String name, Type type) {
	    	super(name);
	    	this.type = type;
		}
	}

	static class TypeAlias extends Symbol implements Type {
		private Type targetType;

		public TypeAlias(String name, Type target) {
			super(name);
			this.targetType = target;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Type[] getBaseTypes() {
			return new Type[] {targetType};
		}

		@Override
		public TypeKind getTypeKind() {
			return TypeKind.Alias;
		}

		@Override
		public ReferenceKind getReferenceKind() {
			return ReferenceKind.Irrelevant;
		}
	}

	// A symbol with a scope (so it can have child symbols).
	static class ScopedSymbol extends Symbol {

		private List<Symbol> children = new ArrayList<>();

		public ScopedSymbol(String name) {
			super(name);
		}

		public List<Symbol> children() {
			return children;
		}

	    public void clear() {
	        this.children.clear();
	    }

	    /**
	     * Adds the given symbol to this scope. If it belongs already to a different scope
	     * it is removed from that before adding it here.
	     */
//	    public addSymbol(symbol: Symbol) {
//	        symbol.removeFromParent();
//
//	        // Check for duplicates first.
//	        let symbolTable = this.symbolTable;
//	        if (!symbolTable || !symbolTable.options.allowDuplicateSymbols) {
//	            for (let child of this._children) {
//	                if (child == symbol || (symbol.name.length > 0 && child.name == symbol.name)) {
//	                    let name = symbol.name;
//	                    if (name.length == 0)
//	                        name = "<anonymous>";
//	                    throw new DuplicateSymbolError("Attempt to add duplicate symbol '" + name + "'");
//	                }
//	            }
//	        }
//
//	        this._children.push(symbol);
//	        symbol.setParent(this);
//	    }
//

	    public void removeSymbol(Symbol symbol) {
	    	children.remove(symbol);
	    }

//	    /**
//	     * Returns all (nested) children of a given type.
//	     */
//	    public getNestedSymbolsOfType<T extends Symbol>(t: new (...args: any[]) => T): T[] {
//	        let result: T[] = [];
//
//	        for (let child of this._children) {
//	            if (child instanceof t)
//	                result.push(child);
//	            if (child instanceof ScopedSymbol)
//	                result.push(...child.getNestedSymbolsOfType(t));
//	        }
//
//	        return result;
//	    }
//
//	    /**
//	     * Returns symbols from this and all nested scopes in the order they were defined.
//	     * @param name If given only returns symbols with that name.
//	     */
//	    public getAllNestedSymbols(name?: string): Symbol[] {
//	        let result: Symbol[] = [];
//
//	        for (let child of this._children) {
//	            if (!name || child.name == name) {
//	                result.push(child);
//	            }
//	            if (child instanceof ScopedSymbol)
//	                result.push(...child.getAllNestedSymbols(name));
//	        }
//
//	        return result;
//	    }
//
//	    /**
//	     * Returns direct children of a given type.
//	     */
//	    public getSymbolsOfType<T extends Symbol>(t: new (...args: any[]) => T): T[] {
//	        let result: T[] = [];
//	        for (let child of this._children) {
//	            if (child instanceof t)
//	                result.push(<T>child);
//	        }
//
//	        return result;
//	    }
//
//	    /**
//	     * Returns all symbols of the the given type, accessible from this scope (if localOnly is false),
//	     * within the owning symbol table.
//	     * TODO: add optional position dependency (only symbols defined before a given caret pos are viable).
//	     */
//	    public getAllSymbols<T extends Symbol>(t: new (...args: any[]) => T, localOnly = false): Set<Symbol> {
//	        let result: Set<Symbol> = new Set();
//
//	        // Special handling for namespaces, which act like grouping symbols in this scope,
//	        // so we show them as available in this scope.
//	        for (let child of this._children) {
//	            if (child instanceof t) {
//	                result.add(child);
//	            }
//	            if (child instanceof NamespaceSymbol) {
//	                child.getAllSymbols(t, true).forEach(result.add, result);
//	            }
//	        }
//
//	        if (!localOnly) {
//	            if (this._parent instanceof ScopedSymbol) {
//	                this._parent.getAllSymbols(t, true).forEach(result.add, result);
//	            }
//	        }
//
//	        return result;
//	    }
//
//	    /**
//	     * Returns the first symbol with a given name, in the order of appearance in this scope
//	     * or any of the parent scopes (conditionally).
//	     */
//	    public resolve(name: string, localOnly = false): Symbol | undefined {
//	        for (let child of this._children) {
//	            if (child.name == name)
//	                return child;
//	        }
//
//	        // Nothing found locally. Let the parent continue.
//	        if (!localOnly) {
//	            if (this._parent instanceof ScopedSymbol)
//	                return this._parent.resolve(name, false);
//	        }
//
//	        return undefined;
//	    }
//
//	    /**
//	     * Returns all accessible symbols that have a type assigned.
//	     */
//	    public getTypedSymbols(localOnly = true): TypedSymbol[] {
//	        let result: TypedSymbol[] = []
//
//	        for (let child of this._children) {
//	            if (child instanceof TypedSymbol) {
//	                result.push(child);
//	            }
//	        }
//
//	        if (!localOnly) {
//	            if (this._parent instanceof ScopedSymbol) {
//	                let localList = (this._parent as ScopedSymbol).getTypedSymbols(true);
//	                result.push(...localList);
//	            }
//	        }
//
//	        return result;
//	    }
//
//	    /**
//	     * The names of all accessible symbols with a type.
//	     */
//	    public getTypedSymbolNames(localOnly = true): string[] {
//	        let result: string[] = [];
//	        for (let child of this._children) {
//	            if (child instanceof TypedSymbol) {
//	                result.push(child.name);
//	            }
//	        }
//
//	        if (!localOnly) {
//	            if (this._parent instanceof ScopedSymbol) {
//	                let localList = (this._parent as ScopedSymbol).getTypedSymbolNames(true);
//	                result.push(...localList);
//	            }
//	        }
//
//	        return result;
//	    }
//
//	    /**
//	     * Returns all direct child symbols with a scope (e.g. classes in a module).
//	     */
//	    public get directScopes(): ScopedSymbol[] {
//	        return this.getSymbolsOfType(ScopedSymbol);
//	    }
//
//	    /**
//	     * Returns the symbol located at the given path through the symbol hierarchy.
//	     * @param path The path consisting of symbol names separator by `separator`.
//	     * @param separator The character to separate path segments.
//	     */
//	    public symbolFromPath(path: string, separator: string = "."): Symbol | undefined {
//	        let elements = path.split(separator);
//	        let index = 0;
//	        if (elements[0] == this.name || elements[0].length == 0)
//	            ++index;
//
//	        let result: Symbol = this;
//	        while (index < elements.length) {
//	            if (!(result instanceof ScopedSymbol)) // Some parts left but found a non-scoped symbol?
//	                return undefined;
//
//	            let child = result._children.find(child => child.name == elements[index]);
//	            if (!child)
//	                return undefined;
//	            result = child;
//	            ++index;
//	        }
//	        return result;
//	    }

	    /**
	     * Returns the index of the given child symbol in the child list or -1 if it couldn't be found.
	     */
	    public Integer indexOfChild(Symbol child) {
	    	return children.indexOf(child);
	    }

	    /**
	     * Returns the sibling symbol after the given child symbol, if one exists.
	     */
	    public Symbol nextSiblingOf(Symbol child) {
	    	Integer indexOfChild = indexOfChild(child);
	    	if (indexOfChild != null) {
	    		return children.get(indexOfChild + 1);
	    	}
	    	return null;

	    }

	    /**
	     * Returns the sibling symbol before the given child symbol, if one exists.
	     */
	    public Symbol getPreviousSiblingOf(Symbol child) {
	    	Integer indexOfChild = indexOfChild(child);
	    	if (indexOfChild != null) {
	    		return children.get(indexOfChild);
	    	}
	    	return null;
	    }

	    public Symbol firstChild() {
	    	if (!this.children.isEmpty()) {
	    		return this.children.get(0);
	    	}
	    	return null;
	    }

	    public Symbol lastChild() {
	    	if (!this.children.isEmpty()) {
	    		return this.children.get(children.size() - 1);
	    	}
	    	return null;
	    }

	    /**
	     * Returns the next symbol in definition order, regardless of the scope.
	     */
	    public Symbol getNextOf(Symbol child) {
	    	if (!(child.parent instanceof ScopedSymbol)) {
	    		return null;
	    	}
	    	if (child.parent != this) {
	    		return ((ScopedSymbol)child.parent).getNextOf(child);
	    	}
	    	if (child instanceof ScopedSymbol && !((ScopedSymbol) child).children.isEmpty()) {
	    		return ((ScopedSymbol) child).children.get(0);
	    	}
	    	Symbol sibling = this.nextSiblingOf(child);
	    	if (sibling != null) {
	    		return sibling;
	    	}
	    	return ((ScopedSymbol) this.parent).getNextOf(this);
	    }
	}

//	export class NamespaceSymbol extends ScopedSymbol {
//	}
//
//	export class BlockSymbol extends ScopedSymbol {
//	}
//
//	export class VariableSymbol extends TypedSymbol {
//	    constructor(name: string, value: any, type?: Type) {
//	        super(name, type);
//	        this.value = value;
//	    }
//
//	    value: any;
//	};
//
//	export class LiteralSymbol extends TypedSymbol {
//	    constructor(name: string, value: any, type?: Type) {
//	        super(name, type);
//	        this.value = value;
//	    }
//
//	    readonly value: any;
//	};
//
//	export class ParameterSymbol extends VariableSymbol { };
//
//	// A standalone function/procedure/rule.
//	export class RoutineSymbol extends ScopedSymbol {
//	    returnType: Type | undefined; // Can be null if result is void.
//	    constructor(name: string, returnType: Type) {
//	        super(name);
//	        this.returnType = returnType;
//	    }
//
//	    public getVariables(localOnly = true): VariableSymbol[] {
//	        return this.getSymbolsOfType(VariableSymbol);
//	    }
//
//	    public getParameters(localOnly = true): ParameterSymbol[] {
//	        return this.getSymbolsOfType(ParameterSymbol);
//	    }
//	};
//
//	export enum MethodFlags {
//	    None = 0,
//	    Virtual = 1,
//	    Const = 2,
//	    Overwritten = 4,
//	    SetterOrGetter = 8, // Distinguished by the return type.
//	    Explicit = 16,      // Special flag used e.g. in C++ for explicit c-tors.
//	};
//
//	// A routine which belongs to a class or other outer container structure.
//	export class MethodSymbol extends RoutineSymbol {
//	    public methodFlags = MethodFlags.None;
//	    public visibility = MemberVisibility.Invalid;
//
//	    constructor(name: string, returnType: Type) {
//	        super(name, returnType);
//	    }
//	};
//
//	export class FieldSymbol extends VariableSymbol {
//	    public visibility = MemberVisibility.Invalid;
//
//	    public setter: MethodSymbol | undefined;
//	    public getter: MethodSymbol | undefined;
//
//	    constructor(name: string, type: Type) {
//	        super(name, type);
//	    }
//	};
//
//	// Classes and structs.
//	export class ClassSymbol extends ScopedSymbol implements Type {
//
//	    public get baseTypes(): Type[] { return this.superClasses; };
//	    public get kind(): TypeKind { return TypeKind.Class; }
//	    public get reference(): ReferenceKind { return this.referenceKind; }
//
//	    public isStruct = false;
//
//	    /**
//	     * Usually only one member, unless the language supports multiple inheritance.
//	     */
//	    public readonly superClasses: ClassSymbol[] = [];
//
//	    constructor(name: string, referenceKind: ReferenceKind, ...superClass: ClassSymbol[]) {
//	        super(name);
//	        this.referenceKind = referenceKind;
//	        this.superClasses.push(...superClass); // Standard case: a single super class.
//	    }
//
//	    /**
//	     * Returns a list of all methods.
//	     */
//	    public getMethods(includeInherited = false): MethodSymbol[] {
//	        return this.getSymbolsOfType(MethodSymbol);
//	    }
//
//	    /**
//	     * Returns all fields.
//	     */
//	    public getFields(includeInherited = false): FieldSymbol[] {
//	        return this.getSymbolsOfType(FieldSymbol);
//	    }
//
//	    private referenceKind: ReferenceKind;
//	};
//
//	export class ArrayType extends Symbol implements Type {
//
//	    public get baseTypes(): Type[] { return []; };
//	    public get kind(): TypeKind { return TypeKind.Array; }
//	    public get reference(): ReferenceKind { return this.referenceKind; }
//
//	    public readonly elementType: Type;
//	    public readonly size: number; // > 0 if fixed length.
//	    constructor(name: string, referenceKind: ReferenceKind, elemType: Type, size = 0) {
//	        super(name);
//	        this.referenceKind = referenceKind;
//	        this.elementType = elemType;
//	        this.size = size;
//	    }
//
//	    private referenceKind: ReferenceKind;
//	};
//
	// The main class managing all the symbols for a top level entity like a file, library or similar.
	static class SymbolTable extends ScopedSymbol {

	    protected Set<SymbolTable> dependencies = new HashSet<>();
		private SymbolTableOptions options;

		public SymbolTable(String name, SymbolTableOptions options) {
			super(name);
			this.options = options;
		}

	    @Override
		public void clear() {
	        super.clear();
	        this.dependencies.clear();
	    }

	    public void addDependencies(SymbolTable[]... tables) {
	    	for (SymbolTable[] table : tables) {
	    		for (SymbolTable t : table) {
	    			dependencies.add(t);
	    		}
	    	}
	    }

	    public void removeDependency(SymbolTable symbolTable) {
	    	dependencies.remove(symbolTable);
	    }

//	    /**
//	     * Returns instance informations, mostly relevant for unit testing.
//	     */
//	    public get info() {
//	        return {
//	            dependencyCount: this.dependencies.size,
//	            symbolCount: this.children.length
//	        };
//	    }
//
//	    public addNewSymbolOfType<T extends Symbol>(t: new (...args: any[]) => T,
//	        parent: ScopedSymbol | undefined, ...args: any[]): T {
//
//	        let result = new t(...args);
//	        if (!parent || parent == this) {
//	            this.addSymbol(result);
//	        } else {
//	            parent.addSymbol(result);
//	        }
//	        return result;
//	    }
//
//	    /**
//	     * Adds a new namespace to the symbol table or the given parent. The path parameter specifies a single namespace name
//	     * or a chain of namespaces (which can be e.g. "outer.intermittant.inner.final").
//	     * If any of the parent namespaces is missing they are created implicitly. The final part must not exist however
//	     * or you'll get a duplicate symbol error.
//	     */
//	    public addNewNamespaceFromPath(parent: ScopedSymbol | undefined, path: string, delimiter = "."): NamespaceSymbol {
//	        let parts = path.split(delimiter);
//	        let i = 0;
//	        let currentParent = (parent == undefined) ? this : parent;
//	        while (i < parts.length - 1) {
//	            let namespace = currentParent.resolve(parts[i], true) as NamespaceSymbol;
//	            if (namespace == undefined) {
//	                namespace = this.addNewSymbolOfType(NamespaceSymbol, currentParent, parts[i]);
//	            }
//	            currentParent = namespace;
//	            ++i;
//	        }
//	        return this.addNewSymbolOfType(NamespaceSymbol, currentParent, parts[parts.length - 1]);
//	    }
//
//	    public getAllSymbols<T extends Symbol>(t?: new (...args: any[]) => T, localOnly: boolean = false): Set<Symbol> {
//	        let type = t ? t : Symbol;
//	        let result = super.getAllSymbols(type, localOnly);
//
//	        if (!localOnly) {
//	            for (let dependency of this.dependencies) {
//	                dependency.getAllSymbols(t, localOnly).forEach(result.add, result);
//	            }
//	        }
//
//	        return result;
//	    }
//
//	    public symbolWithContext(context: ParseTree): Symbol | undefined {
//
//	        function findRecursive(symbol: Symbol): Symbol | undefined {
//	            if (symbol.context == context) {
//	                return symbol;
//	            }
//
//	            if (symbol instanceof ScopedSymbol) {
//	                for (let child of symbol.children) {
//	                    let result = findRecursive(child);
//	                    if (result) {
//	                        return result;
//	                    }
//	                }
//	            }
//	        }
//
//	        let symbols = this.getAllSymbols(Symbol);
//	        for (let symbol of symbols) {
//	            let result = findRecursive(symbol);
//	            if (result) {
//	                return result;
//	            }
//	        }
//
//	        for (let dependency of this.dependencies) {
//	            symbols = dependency.getAllSymbols(Symbol);
//	            for (let symbol of symbols) {
//	                let result = findRecursive(symbol);
//	                if (result) {
//	                    return result;
//	                }
//	            }
//	        }
//	    }
//
//	    public resolve(name: string, localOnly = false): Symbol | undefined {
//	        let result = super.resolve(name, localOnly);
//
//	        if (!result && !localOnly) {
//	            for (let dependency of this.dependencies) {
//	                result = dependency.resolve(name, false);
//	                if (result)
//	                    break;
//	            }
//	        }
//
//	        return result;
//	    }
	}



}
