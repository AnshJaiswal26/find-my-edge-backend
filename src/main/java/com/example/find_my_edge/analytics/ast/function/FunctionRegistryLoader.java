package com.example.find_my_edge.analytics.ast.function;

import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@Getter
public class FunctionRegistryLoader {

    private final Map<String, FunctionMeta> defs;
    private final Map<FunctionMode, Set<String>> allowedByMode;

    public FunctionRegistryLoader(ObjectMapper mapper) throws Exception {
        this.defs = mapper.readValue(
                new ClassPathResource("functions/function-defs.json").getInputStream(),
                new TypeReference<>() {
                }
        );

        this.allowedByMode = mapper.readValue(
                new ClassPathResource("functions/functions-allowed-by-mode.json").getInputStream(),
                new TypeReference<>() {
                }
        );
    }

}