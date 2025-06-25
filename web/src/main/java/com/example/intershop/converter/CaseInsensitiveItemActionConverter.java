package com.example.intershop.converter;

import com.example.intershop.model.ItemAction;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CaseInsensitiveItemActionConverter implements Converter<String, ItemAction> {

    @Override
    public ItemAction convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        for (ItemAction enumConstant : ItemAction.values()) {
            if (enumConstant.name().equalsIgnoreCase(source)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("No enum constant " + ItemAction.class.getCanonicalName() + " equals ignore case " + source);
    }
}