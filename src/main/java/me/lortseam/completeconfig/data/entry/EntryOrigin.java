package me.lortseam.completeconfig.data.entry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;

import java.lang.reflect.Field;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class EntryOrigin {

    @Getter
    private final Field field;
    @Getter
    private final ConfigEntryContainer parentObject;
    @Getter
    private final TranslationIdentifier parentTranslation;

}