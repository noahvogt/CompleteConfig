package me.lortseam.completeconfig.data;

import com.google.common.collect.Iterables;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.structure.ParentStructurePart;
import me.lortseam.completeconfig.data.structure.StructurePart;
import me.lortseam.completeconfig.data.structure.client.Translatable;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import me.lortseam.completeconfig.util.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;

abstract class BaseCollection implements ParentStructurePart, Translatable {

    private final EntrySet entries = new EntrySet(this);
    private final CollectionSet collections = new CollectionSet(this);

    public final java.util.Collection<Entry> getEntries() {
        return Collections.unmodifiableCollection(entries);
    }

    public final java.util.Collection<Collection> getCollections() {
        return Collections.unmodifiableCollection(collections);
    }

    final void resolveContainer(ConfigContainer container) {
        entries.resolve(container);
        for (Class<? extends ConfigContainer> clazz : container.getConfigClasses()) {
            resolve(Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                if (field.isAnnotationPresent(ConfigContainer.Transitive.class)) {
                    if (!ConfigContainer.class.isAssignableFrom(field.getType())) {
                        throw new IllegalAnnotationTargetException("Transitive field " + field + " must implement " + ConfigContainer.class.getSimpleName());
                    }
                    return !Modifier.isStatic(field.getModifiers()) || clazz == container.getClass();
                }
                return false;
            }).map(field -> {
                if (!field.canAccess(container)) {
                    field.setAccessible(true);
                }
                try {
                    return (ConfigContainer) field.get(container);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).toArray(ConfigContainer[]::new));
            Class<?>[] nestedClasses = clazz.getDeclaredClasses();
            ArrayUtils.reverse(nestedClasses);
            resolve(Arrays.stream(nestedClasses).filter(nestedClass -> {
                if (nestedClass.isAnnotationPresent(ConfigContainer.Transitive.class)) {
                    if (!ConfigContainer.class.isAssignableFrom(nestedClass)) {
                        throw new IllegalAnnotationTargetException("Transitive " + nestedClass + " must implement " + ConfigContainer.class.getSimpleName());
                    }
                    if (!Modifier.isStatic(nestedClass.getModifiers())) {
                        throw new IllegalAnnotationTargetException("Transitive " + nestedClass + " must be static");
                    }
                    return true;
                }
                return false;
            }).map(nestedClass -> {
                try {
                    return (ConfigContainer) ReflectionUtils.instantiateClass(nestedClass);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to instantiate nested " + nestedClass, e);
                }
            }).toArray(ConfigContainer[]::new));
        }
        resolve(container.getTransitives());
    }

    final void resolve(ConfigContainer... containers) {
        for (ConfigContainer container : containers) {
            if (container instanceof ConfigGroup) {
                collections.resolve((ConfigGroup) container);
            } else {
                resolveContainer(container);
            }
        }
    }

    @Override
    public final Iterable<StructurePart> getChildren() {
        return Iterables.concat(entries, collections);
    }

    final boolean isEmpty() {
        return Iterables.size(getChildren()) == 0;
    }

}
