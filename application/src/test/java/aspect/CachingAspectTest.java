package aspect;

import org.example.annotations.Cached;
import org.example.aspect.CachingAspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachingAspectTest {

    private CachingAspect cachingAspect;
    private ConcurrentHashMap<CachingAspect.CacheKey, CachingAspect.CacheValue> cache;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @BeforeEach
    void setUp() {
        cachingAspect = new CachingAspect();
        // Используем рефлексию для доступа к приватному полю 'cache'
        try {
            java.lang.reflect.Field field = CachingAspect.class.getDeclaredField("cache");
            field.setAccessible(true);
            cache = (ConcurrentHashMap<CachingAspect.CacheKey, CachingAspect.CacheValue>) field.get(cachingAspect);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }

    @Test
    void testCacheWorks() throws Throwable {
        DummyService service = new DummyService();
        Method method = DummyService.class.getMethod("getData", String.class);

        // Настройка мока
        when(joinPoint.getSignature()).thenReturn(new MockSignature(method.getName(), method.getReturnType()));
        when(joinPoint.getArgs()).thenReturn(new Object[]{"key1"});
        when(joinPoint.proceed()).thenReturn("Data for key1");

        // Первый вызов — должен выполниться
        String result = (String) cachingAspect.cacheResult(joinPoint);
        assertEquals("Data for key1", result);
        verify(joinPoint, times(1)).proceed();

        // Второй вызов — должен быть взят из кэша
        String cachedResult = (String) cachingAspect.cacheResult(joinPoint);
        assertEquals("Data for key1", cachedResult);
        verify(joinPoint, times(1)).proceed(); // метод не вызывается повторно
    }

    static class DummyService {
        public String getData(String key) {
            return "Data for " + key;
        }
    }

    static class MockSignature implements org.aspectj.lang.Signature {
        private final String name;
        private final Class<?> returnType;

        public MockSignature(String name, Class<?> returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public Class<?> getDeclaringType() {
            return Object.class;
        }

        // ✅ Новый метод, которого не хватало
        @Override
        public String getDeclaringTypeName() {
            return Object.class.getName();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getModifiers() {
            return 0;
        }

        @Override
        public String toLongString() {
            return toString();
        }

        @Override
        public String toShortString() {
            return toString();
        }

        @Override
        public String toString() {
            return "signatureToString()";
        }
    }
}