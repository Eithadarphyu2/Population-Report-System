package com.team14.database;

import com.team14.database.query.DatabaseQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the database utility classes. They need no database.
 */
class DatabaseUtilityClassesTest {

    /**
     * Checks that a utility class refuses to be created.
     */
    private static void assertCannotBeInstantiated(Class<?> type) throws Exception {

        Constructor<?> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException error = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance,
                type.getSimpleName() + " constructor should throw when invoked via reflection"
        );

        assertTrue(
                error.getCause() instanceof UnsupportedOperationException,
                type.getSimpleName() + " constructor should fail with UnsupportedOperationException"
        );
    }

    @Test
    @DisplayName("DatabaseConnection cannot be instantiated")
    void connectionClassCannotBeInstantiated() throws Exception {
        assertCannotBeInstantiated(DatabaseConnection.class);
    }

    @Test
    @DisplayName("DatabaseQuery cannot be instantiated")
    void queryClassCannotBeInstantiated() throws Exception {
        assertCannotBeInstantiated(DatabaseQuery.class);
    }
}