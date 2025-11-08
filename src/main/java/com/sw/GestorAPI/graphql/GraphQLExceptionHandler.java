package com.sw.GestorAPI.graphql;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    @Nullable
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof AccessDeniedException) {
            return buildError(env, "No tienes permisos para esta operación", "FORBIDDEN", null);
        }
        if (ex instanceof AuthenticationException) {
            return buildError(env, "Autenticación requerida o inválida", "UNAUTHENTICATED", null);
        }
        if (ex instanceof MethodArgumentNotValidException manv) {
            List<String> errors = manv.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            Map<String, Object> extra = new HashMap<>();
            extra.put("errors", errors);
            return buildError(env, "Solicitud inválida", "BAD_REQUEST", extra);
        }
        if (ex instanceof ConstraintViolationException cve) {
            List<String> errors = cve.getConstraintViolations()
                    .stream()
                    .map(this::formatViolation)
                    .collect(Collectors.toList());
            Map<String, Object> extra = new HashMap<>();
            extra.put("errors", errors);
            return buildError(env, "Solicitud inválida", "BAD_REQUEST", extra);
        }
        return null; // fallback to default handling
    }

    private String formatViolation(ConstraintViolation<?> v) {
        String path = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "";
        return (path.isEmpty() ? "" : path + ": ") + v.getMessage();
    }

    private GraphQLError buildError(DataFetchingEnvironment env, String message, String code, Map<String, Object> extra) {
        GraphqlErrorBuilder builder = GraphqlErrorBuilder.newError(env)
                .message(message)
                .errorType(null) // we use extensions.code instead of ErrorType
                .extensions(Map.of("code", code));

        if (extra != null && !extra.isEmpty()) {
            Map<String, Object> merged = new HashMap<>(builder.build().getExtensions());
            merged.putAll(extra);
            // rebuild with merged extensions
            return GraphqlErrorBuilder.newError(env)
                    .message(message)
                    .extensions(merged)
                    .build();
        }
        return builder.build();
    }
}
