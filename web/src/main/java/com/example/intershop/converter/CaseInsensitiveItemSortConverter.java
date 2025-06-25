package com.example.intershop.converter;

import com.example.intershop.model.ItemSort;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CaseInsensitiveItemSortConverter implements Converter<String, ItemSort> {

    @Override
    public ItemSort convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        for (ItemSort enumConstant : ItemSort.values()) {
            if (enumConstant.name().equalsIgnoreCase(source)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("No enum constant " + ItemSort.class.getCanonicalName() + " equals ignore case " + source);
    }
}