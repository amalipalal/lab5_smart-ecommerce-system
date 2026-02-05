package com.example.ecommerce_system.util;

import com.example.ecommerce_system.exception.customer.CustomerNotFoundException;
import com.example.ecommerce_system.exception.order.InvalidOrderStatusException;
import com.example.ecommerce_system.exception.order.OrderCreationException;
import com.example.ecommerce_system.exception.order.OrderDoesNotExist;
import com.example.ecommerce_system.exception.order.OrderRetrievalException;
import com.example.ecommerce_system.exception.order.OrderUpdateException;
import com.example.ecommerce_system.exception.product.InsufficientProductStock;
import com.example.ecommerce_system.exception.product.ProductNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    private static final Map<Class<? extends Exception>, ErrorType> EXCEPTION_ERROR_TYPE_MAP = Map.of(
            CustomerNotFoundException.class, ErrorType.NOT_FOUND,
            ProductNotFoundException.class, ErrorType.NOT_FOUND,
            OrderDoesNotExist.class, ErrorType.NOT_FOUND,
            InsufficientProductStock.class, ErrorType.BAD_REQUEST,
            InvalidOrderStatusException.class, ErrorType.BAD_REQUEST,
            IllegalArgumentException.class, ErrorType.BAD_REQUEST,
            OrderCreationException.class, ErrorType.INTERNAL_ERROR,
            OrderUpdateException.class, ErrorType.INTERNAL_ERROR,
            OrderRetrievalException.class, ErrorType.INTERNAL_ERROR
    );

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        ErrorType errorType = getErrorType(ex);

        if (errorType == null) {
            return null;
        }

        return buildGraphQLError(ex, env, errorType);
    }

    private ErrorType getErrorType(Throwable ex) {
        return EXCEPTION_ERROR_TYPE_MAP.get(ex.getClass());
    }

    private GraphQLError buildGraphQLError(Throwable ex, DataFetchingEnvironment env, ErrorType errorType) {
        return GraphqlErrorBuilder.newError()
                .errorType(errorType)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }
}
