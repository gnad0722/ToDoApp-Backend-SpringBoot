package dev.gnad.SpringModulith.common.token.utility;

import org.jspecify.annotations.Nullable;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

public class TokenKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, @Nullable Object... params) {
        String rawToken = (String) params[0];
        return TokenCommon.hashToken(rawToken);
    }
}
