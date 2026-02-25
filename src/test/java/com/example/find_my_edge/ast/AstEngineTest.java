package com.example.find_my_edge.ast;

import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.enums.ComputationMode;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.executor.AggregateExecutor;
import com.example.find_my_edge.analytics.ast.executor.RowSequenceExecutor;
import com.example.find_my_edge.analytics.ast.mapper.AstNodeMapper;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.parser.AstBuilder;
import com.example.find_my_edge.analytics.ast.parser.PostfixConverter;
import com.example.find_my_edge.analytics.ast.parser.Tokenizer;
import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.domain.trade.entity.TradeEntity;
import com.example.find_my_edge.domain.trade.repository.TradeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.ArrayList;
import java.util.List;

import static com.example.find_my_edge.common.config.builder.AstConfigBuilder.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AstEngineTest {

    @Autowired
    private AstEvaluator evaluator;

    @Autowired
    private RowSequenceExecutor rowExecutor;

    @Autowired
    private AggregateExecutor aggExecutor;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private Tokenizer tokenizer;

    @Autowired
    private PostfixConverter postfixConverter;

    @Autowired
    private AstBuilder astBuilder;

    @Test
    void shouldEvaluateConstant() {
        AstConfig ast = function("ABS", binary(constant(10.0), "-", constant(20.0)));


        Object evaluate = evaluator.evaluate(AstNodeMapper.toNode(ast), null);

        System.out.println(evaluate);


        assertEquals(10.0, (double) evaluate);
    }

    @Test
    void shouldComputeRunningSum() {

        List<TradeEntity> allByUserId = tradeRepository.findAllByUserId("dev-user-123");

//        AstConfig ast = function(
//                "SUM", binary(
//                        binary(key("exit"), "-", key("entry")),
//                        "*",
//                        key("qty")
//                )
//        );

        AstNode ast = astBuilder.build(
                postfixConverter.toPostfix(tokenizer.tokenize("SUM(entry)"))).getAstNode();

        Object result = aggExecutor.execute(
                ast,
                (i, key) -> Double.parseDouble(allByUserId.get(i).getValues().get(key)),
                allByUserId::size,
                key -> new SchemaType("number", "number")
        );

        System.out.println(result);
        assertEquals(60.0, (double) result);
    }
}
