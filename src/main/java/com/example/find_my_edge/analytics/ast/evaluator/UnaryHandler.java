package com.example.find_my_edge.analytics.ast.evaluator;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.util.TypeUtil;
import com.example.find_my_edge.common.config.AstConfig;
import org.springframework.stereotype.Service;

@Service
public class UnaryHandler {

    public Object handle(AstConfig ast, EvaluationContext ctx, AstEvaluator evaluator) {
        Object value = evaluator.evaluate(ast.getArg(), ctx);
        if (value == null) return null;

        if ("-".equals(ast.getOp())) {
            return -TypeUtil.toDouble(value);
        }

        return null;
    }
}