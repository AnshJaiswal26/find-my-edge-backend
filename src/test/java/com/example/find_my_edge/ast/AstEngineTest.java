package com.example.find_my_edge.ast;

import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.executor.AggregateExecutor;
import com.example.find_my_edge.analytics.ast.executor.RowSequenceExecutor;
import com.example.find_my_edge.analytics.ast.mapper.AstNodeMapper;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.parser.AstBuilder;
import com.example.find_my_edge.analytics.ast.parser.PostfixConverter;
import com.example.find_my_edge.analytics.ast.parser.Tokenizer;
import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.domain.trade.entity.TradeEntity;
import com.example.find_my_edge.domain.trade.repository.TradeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;
import java.util.Map;

import static com.example.find_my_edge.common.config.builder.AstConfigBuilder.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Autowired
    private ComputeService computeService;

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

        AstNode ast = astBuilder.build(
                postfixConverter.toPostfix(tokenizer.tokenize("SUM(pnl)"))).getAstNode();

        Object result = aggExecutor.execute(
                ast,
                (i, key) -> allByUserId.get(i).getValues().get(key),
                allByUserId::size,
                key -> new SchemaType("number", "number")
        );

        System.out.println(result);
        assertEquals(9.0, (double) result);
    }

    Double toDouble(Object value) {
        return value instanceof Number
               ? ((Number) value).doubleValue() : Double.parseDouble((String) value);
    }

    @Test
    void shouldComputeAggregate() {
        long start = System.currentTimeMillis();

        List<TradeEntity> allByUserId = tradeRepository.findAllByUserId("dev-user-123");

        Double pnl = allByUserId
                .stream()
                .map(trade -> {
                    double entry = toDouble(trade.getValues().get("entry"));
                    double exit = toDouble(trade.getValues().get("exit"));
                    double qty = toDouble(trade.getValues().get("qty"));

                    return (exit - entry) * qty;
                })
                .reduce(0.0, Double::sum);

        Double entrySum = allByUserId
                .stream()
                .map(trade -> toDouble(trade.getValues().get("entry")))
                .reduce(0.0, Double::sum);

        Double exitSum = allByUserId
                .stream()
                .map(trade -> toDouble(trade.getValues().get("exit")))
                .reduce(0.0, Double::sum);

        Double qtySum = allByUserId
                .stream()
                .map(trade -> toDouble(trade.getValues().get("qty")))
                .reduce(0.0, Double::sum);

        Map<String, Double> entry =
                computeService.computeAggregateFromFormulas(
                        Map.of(
                                "pnl", "SUM(pnl)",
                                "entry", "SUM(entry)",
                                "exit", "SUM(exit)",
                                "qty", "SUM(qty)"
                        ));

        System.out.println("pnl: " + pnl + "," + "computed pnl: " + entry.get("pnl"));

        assertEquals(pnl, entry.get("pnl"));
        assertEquals(entrySum, entry.get("entry"));
        assertEquals(exitSum, entry.get("exit"));
        assertEquals(qtySum, entry.get("qty"));

        System.out.println(System.currentTimeMillis() - start);
    }
}
