package org.springframework.dsl.lsp4j.converter;

import java.util.HashSet;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.util.ClassUtils;

public class GenericLsp4jObjectConverter implements GenericConverter {

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> convertiblePairs = new HashSet<ConvertiblePair>();
        convertiblePairs.add(new ConvertiblePair(InitializeResult.class, org.eclipse.lsp4j.InitializeResult.class));
        convertiblePairs.add(new ConvertiblePair(org.eclipse.lsp4j.InitializeResult.class, InitializeResult.class));
        return convertiblePairs;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (ClassUtils.isAssignable(InitializeResult.class, sourceType.getType())) {
			return ConverterUtils.toInitializeResult((InitializeResult)source);
		} else if (ClassUtils.isAssignable(org.eclipse.lsp4j.InitializeResult.class, sourceType.getType())) {
			return ConverterUtils.toInitializeResult((org.eclipse.lsp4j.InitializeResult)source);
		}
		throw new IllegalArgumentException();
	}
}
