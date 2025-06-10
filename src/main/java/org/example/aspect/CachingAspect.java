package org.example.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class CachingAspect {

    @Value("${app.cache.time-to-live}")
    private int timeToLiveSeconds;

    private final Map<CacheKey, CacheValue> cache = new ConcurrentHashMap<>();

    @Around("@annotation(org.example.annotations.Cached)")
    public Object cacheResult(ProceedingJoinPoint joinPoint) throws Throwable {
        CacheKey key = generateKey(joinPoint);
        CacheValue cachedValue = cache.get(key);

        if (cachedValue != null && !isExpired(cachedValue)) {
            return cachedValue.value;
        }

        Object result = joinPoint.proceed();
        cache.put(key, new CacheValue(result, System.currentTimeMillis()));
        return result;
    }

    private CacheKey generateKey(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        return new CacheKey(methodName, args);
    }

    private boolean isExpired(CacheValue value) {
        return System.currentTimeMillis() > value.timestamp + (long) timeToLiveSeconds * 1000;
    }

    // Вспомогательные классы
    private static class CacheKey {
        private final String methodName;
        private final Object[] arguments;

        CacheKey(String methodName, Object[] arguments) {
            this.methodName = methodName;
            this.arguments = arguments;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return methodName.equals(cacheKey.methodName) && Arrays.equals(arguments, cacheKey.arguments);
        }

        @Override
        public int hashCode() {
            int result = methodName.hashCode();
            result = 31 * result + Arrays.hashCode(arguments);
            return result;
        }
    }

    private static class CacheValue {
        Object value;
        long timestamp;

        CacheValue(Object value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}