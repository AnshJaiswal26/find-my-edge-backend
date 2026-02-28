package com.example.find_my_edge.analytics.ast.reducer.schema;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.enums.NodeType;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
@FunctionMeta(
        argTypes = {"any"},
        semanticArgs = {
                @ArgType({"any"})
        },
        returnType = "any",
        semanticReturn = "any",
        signature = "PREV(expr)",
        description = "Value of expression from previous row",
        modes = {FunctionMode.WINDOW}
)
public class PrevFunction implements Reducer {

    @Override
    public FunctionType getType() {
        return FunctionType.PURE;
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.AST;
    }

    @Override
    public String getName() {
        return "PREV";
    }

    @Override
    public Object execute(AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        AstNode arg = fn.getArgs().getFirst();

        // Case 1: PREV(identifier)
        if (NodeType.IDENTIFIER == arg.getType()) {
            if (ctx.getTradeIndex() == 0) return null;

            return ctx.getTradeValue(ctx.getTradeIndex(), arg.getField());
        }

        // Case 2: PREV(expr)
        Object prev = ctx.getPrevValue();
        return prev != null ? prev : null;
    }
}