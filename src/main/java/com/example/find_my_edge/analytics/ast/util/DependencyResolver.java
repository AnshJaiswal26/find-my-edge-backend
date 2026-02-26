package com.example.find_my_edge.analytics.ast.util;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DependencyResolver {

    /**
     * Build dependency map: schemaId -> dependencies
     */
    public Map<String, List<String>> buildDependencyMap(Collection<? extends HasDependencies> items) {
        Map<String, List<String>> dependsOnMap = new HashMap<>();

        for (HasDependencies item : items) {
            dependsOnMap.put(
                    item.getId(),
                    item.getDependencies() != null ? item.getDependencies() : Collections.emptyList()
            );
        }

        return dependsOnMap;
    }

    /**
     * Resolve execution order using DFS-based topological sort
     */
    public List<String> resolveExecutionOrder(Map<String, List<String>> dependsOnMap) {
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();
        List<String> order = new ArrayList<>();

        for (String schemaId : dependsOnMap.keySet()) {
            if (!visited.contains(schemaId)) {
                dfs(schemaId, dependsOnMap, visited, visiting, order);
            }
        }

        return order;
    }

    private void dfs(
            String schemaId,
            Map<String, List<String>> dependsOnMap,
            Set<String> visited,
            Set<String> visiting,
            List<String> order
    ) {
        if (visiting.contains(schemaId)) {
            throw new IllegalStateException("Cycle detected at schema: " + schemaId);
        }

        if (visited.contains(schemaId)) return;

        visiting.add(schemaId);

        List<String> dependencies =
                dependsOnMap.getOrDefault(schemaId, Collections.emptyList());

        for (String dep : dependencies) {
            dfs(dep, dependsOnMap, visited, visiting, order);
        }

        visiting.remove(schemaId);
        visited.add(schemaId);

        order.add(schemaId); // post-order → correct topo order
    }
}