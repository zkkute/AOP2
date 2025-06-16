package aspect;

import org.example.aspect.CachingAspect;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

class CacheKeyTest {

    @Test
    void testEqualsAndHashCode() throws NoSuchMethodException {
        Method method1 = Object.class.getMethod("toString");
        Method method2 = Object.class.getMethod("hashCode");

        CachingAspect.CacheKey key1 = new CachingAspect.CacheKey("methodA", new Object[]{1, "a"});
        CachingAspect.CacheKey key2 = new CachingAspect.CacheKey("methodA", new Object[]{1, "a"});
        CachingAspect.CacheKey key3 = new CachingAspect.CacheKey("methodB", new Object[]{2, "b"});

        assertEquals(key1, key2);
        assertNotEquals(key1, key3);
        assertEquals(key1.hashCode(), key2.hashCode());
    }
}