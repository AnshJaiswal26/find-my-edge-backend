package com.example.find_my_edge.analytics.ast.parser;

import com.example.find_my_edge.analytics.ast.enums.ValueType;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.model.AstResult;
import com.example.find_my_edge.analytics.ast.validator.AstSemanticValidator;
import com.example.find_my_edge.analytics.ast.validator.AstTypeValidator;
import com.example.find_my_edge.domain.schema.model.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AstPipeline {

    private final Tokenizer tokenizer;
    private final PostfixConverter postfixConverter;
    private final AstBuilder astBuilder;

    private final AstTypeValidator astTypeValidator;
    private final AstSemanticValidator astSemanticValidator;

    public AstResult buildAst(String expression, FunctionMode mode, Map<String, Schema> schemasById) {

        // Step 1: Tokenize
        List<Tokenizer.Token> tokens = tokenizer.tokenize(expression);

        // Step 2: Convert to postfix
        List<Tokenizer.Token> postfix = postfixConverter.toPostfix(tokens);

        // Step 3: Build AST
        AstResult build = astBuilder.build(postfix);

        astTypeValidator.validate(build.getAstNode(), mode, schemasById);

        ValueType semanticType = astSemanticValidator.validate(build.getAstNode(), schemasById);

        build.setSemanticType(semanticType);

        return build;
    }

    public AstResult buildAst(String expression) {

        // Step 1: Tokenize
        List<Tokenizer.Token> tokens = tokenizer.tokenize(expression);

        // Step 2: Convert to postfix
        List<Tokenizer.Token> postfix = postfixConverter.toPostfix(tokens);

        // Step 3: Build AST
        return astBuilder.build(postfix);
    }
}