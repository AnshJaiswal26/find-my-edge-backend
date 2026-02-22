package com.example.find_my_edge.ast.evaluator;

import com.example.find_my_edge.ast.context.EvaluationContext;
import com.example.find_my_edge.ast.util.TypeUtil;
import com.example.find_my_edge.common.dto.AstDTO;
import org.springframework.stereotype.Service;

@Service
public class UnaryHandler {

    public Object handle(AstDTO ast, EvaluationContext ctx, AstEvaluator evaluator) {
        Object value = evaluator.evaluate(ast.getArg(), ctx);
        if (value == null) return null;

        if ("-".equals(ast.getOp())) {
            return -TypeUtil.toDouble(value);
        }

        return null;
    }
}