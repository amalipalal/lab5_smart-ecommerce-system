package com.example.ecommerce_system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLScalarConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(jsonScalar());
    }

    private GraphQLScalarType jsonScalar() {
        return GraphQLScalarType.newScalar()
                .name("JSON")
                .description("A custom scalar that handles JSON objects")
                .coercing(new Coercing<String, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof String) {
                            return (String) dataFetcherResult;
                        }
                        try {
                            return objectMapper.writeValueAsString(dataFetcherResult);
                        } catch (Exception e) {
                            throw new CoercingSerializeException("Unable to serialize JSON", e);
                        }
                    }

                    @Override
                    public String parseValue(Object input) throws CoercingParseValueException {
                        if (input instanceof String) {
                            return (String) input;
                        }
                        try {
                            return objectMapper.writeValueAsString(input);
                        } catch (Exception e) {
                            throw new CoercingParseValueException("Unable to parse JSON", e);
                        }
                    }

                    @Override
                    public String parseLiteral(Object input) throws CoercingParseLiteralException {
                        if (input instanceof StringValue) {
                            return ((StringValue) input).getValue();
                        }
                        throw new CoercingParseLiteralException("Expected a string for JSON");
                    }
                })
                .build();
    }
}
