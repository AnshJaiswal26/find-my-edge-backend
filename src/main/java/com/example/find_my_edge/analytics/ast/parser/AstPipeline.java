package com.example.find_my_edge.analytics.ast.parser;

import com.example.find_my_edge.analytics.ast.model.AstResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AstPipeline {

    private final Tokenizer tokenizer;
    private final PostfixConverter postfixConverter;
    private final AstBuilder astBuilder;

    public AstResult buildAst(String expression, String mode) {

        // Step 1: Tokenize
        List<Tokenizer.Token> tokens = tokenizer.tokenize(expression);

        // Step 2: Convert to postfix
        List<Tokenizer.Token> postfix = postfixConverter.toPostfix(tokens);

        // Step 3: Build AST
        return astBuilder.build(postfix); // or however you expose AstNode
    }
}