package me.lortseam.completeconfig.data;

import com.google.common.collect.Iterables;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import me.lortseam.completeconfig.test.data.containers.*;
import me.lortseam.completeconfig.test.data.listeners.EmptyListener;
import me.lortseam.completeconfig.test.data.listeners.SetterListener;
import me.lortseam.completeconfig.text.TranslationKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ParentTest {

    private Parent parent;

    @BeforeEach
    public void beforeEach() {
        parent = new Parent() {
            @Override
            public TranslationKey getTranslation() {
                return mock(TranslationKey.class);
            }
        };
    }

    @Test
    public void resolve_includeFieldIfAnnotated() {
        parent.resolve(new ContainerWithEntry(), new ContainerWithContainerWithEntry(), new ContainerWithGroupWithEntry());
        assertEquals(2, parent.getEntries().size());
        assertEquals(1, parent.getClusters().size());
    }

    @Test
    public void resolve_excludeFieldIfNotAnnotated() {
        parent.resolve(new ContainerWithField());
        assertTrue(parent.isEmpty());
    }

    @Test
    public void resolve_includeFieldInEntries() {
        parent.resolve(new EntriesContainerWithEntry());
        assertEquals(1, parent.getEntries().size());
    }

    @Test
    public void resolve_excludeFieldInEntriesIfContainer() {
        parent.resolve(new EntriesContainerWithEmptyContainer());
        assertTrue(parent.isEmpty());
    }

    @Test
    public void resolve_excludeFieldInEntriesIfIgnoreAnnotated() {
        parent.resolve(new EntriesContainerWithIgnoredField());
        assertTrue(parent.isEmpty());
    }

    @Test
    public void resolve_excludeFieldInEntriesIfTransient() {
        parent.resolve(new EntriesContainerWithTransientField());
        assertTrue(parent.isEmpty());
    }

    @Test
    public void resolve_includeSuperclassFieldIfNonStatic() {
        parent.resolve(new SubclassOfContainerWithEntry(), new SubclassOfContainerWithContainerWithEntry());
        assertEquals(2, parent.getEntries().size());
    }

    @Test
    public void resolve_excludeSuperclassFieldIfStatic() {
        parent.resolve(new SubclassOfContainerWithStaticEntry(), new SubclassOfContainerWithStaticContainerWithEntry());
        assertTrue(parent.isEmpty());
    }

    @Test
    public void resolve_includeFromMethod() {
        parent.resolve(new ContainerIncludingContainerWithEntry(), new ContainerIncludingGroupWithEntry());
        assertEquals(1, parent.getEntries().size());
        assertEquals(1, parent.getClusters().size());
    }

    @Test
    public void resolve_includeNestedIfStatic() {
        parent.resolve(new ContainerNestingStaticContainerWithEntry());
        assertEquals(1, parent.getEntries().size());
    }

    @Test
    public void resolve_throwIfNestedNonContainer() {
        IllegalAnnotationTargetException exception = assertThrows(IllegalAnnotationTargetException.class, () -> parent.resolve(new ContainerNestingStaticClass()));
        assertEquals("Transitive " + ContainerNestingStaticClass.Class.class + " must implement " + ConfigContainer.class.getSimpleName(), exception.getMessage());
    }

    @Test
    public void resolve_throwIfNestedNonStatic() {
        IllegalAnnotationTargetException exception = assertThrows(IllegalAnnotationTargetException.class, () -> parent.resolve(new ContainerNestingContainerWithEntry()));
        assertEquals("Transitive " + ContainerNestingContainerWithEntry.ContainerWithEntry.class + " must be static", exception.getMessage());
    }

    @Test
    public void resolve_listenSetter() {
        SetterListener listener = new SetterListener();
        parent.resolve(listener);
        boolean value = !listener.getValue();
        Iterables.getOnlyElement(parent.getEntries()).setValue(value);
        assertEquals(value, listener.getValue());
    }

    @Test
    public void resolve_doNotUpdateListenerField() {
        EmptyListener listener = new EmptyListener();
        parent.resolve(listener);
        boolean oldValue = listener.getValue();
        Iterables.getOnlyElement(parent.getEntries()).setValue(!oldValue);
        assertEquals(oldValue, listener.getValue());
    }

}
