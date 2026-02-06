package com.example.ecommerce_system.util.handler;

import com.example.ecommerce_system.exception.cart.*;
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

    private static final Map<Class<? extends Exception>, ErrorType> EXCEPTION_ERROR_TYPE_MAP = Map.ofEntries(
            Map.entry(CustomerNotFoundException.class, ErrorType.NOT_FOUND),
            Map.entry(ProductNotFoundException.class, ErrorType.NOT_FOUND),
            Map.entry(OrderDoesNotExist.class, ErrorType.NOT_FOUND),
            Map.entry(InsufficientProductStock.class, ErrorType.BAD_REQUEST),
            Map.entry(InvalidOrderStatusException.class, ErrorType.BAD_REQUEST),
            Map.entry(IllegalArgumentException.class, ErrorType.BAD_REQUEST),
            Map.entry(OrderCreationException.class, ErrorType.INTERNAL_ERROR),
            Map.entry(OrderUpdateException.class, ErrorType.INTERNAL_ERROR),
            Map.entry(OrderRetrievalException.class, ErrorType.INTERNAL_ERROR),
            Map.entry(CartCreationException.class, ErrorType.INTERNAL_ERROR),
            Map.entry(CartRetrievalException.class, ErrorType.INTERNAL_ERROR),
            Map.entry(CartItemAddException.class, ErrorType.INTERNAL_ERROR),
            Map.entry(CartItemRemoveException.class, ErrorType.INTERNAL_ERROR),
            Map.entry(CartItemAuthorizationException.class, ErrorType.UNAUTHORIZED),
            Map.entry(CartItemNotFoundException.class, ErrorType.NOT_FOUND)
    );

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        ErrorType errorType = getErrorType(ex);

        if (errorType == null) return null;

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
