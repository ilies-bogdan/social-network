package com.socialnetwork.utils;

import java.util.LinkedList;
import java.util.Queue;

public class Graph {
    /**
     * DFS in an undirected graph.
     * @param adj         - Adjacency matrix
     * @param visited     - Boolean vector
     * @param vertexCount - Number of vertexes in the graph
     * @param start       - The starting vertex
     */
    public static void dfs(int[][] adj, boolean[] visited, int vertexCount, int start) {
        visited[start] = true;
        for (int i = 0; i < vertexCount; i++) {
            if (adj[start][i] > 0 && !visited[i]) {
                dfs(adj, visited, vertexCount, i);
            }
        }
    }

    /**
     * BFS in an undirected graph; finds the number of levels from the starting point.
     * @param adj - Adjacency matrix
     * @param visited - Boolean vector
     * @param vertexCount - Number of vertexes in the graph
     * @param start - The starting vertex
     * @return the number of levels from the starting point.
     */
    public static int bfs(int[][] adj, boolean[] visited, int vertexCount, int start) {
        Queue<Integer> queue = new LinkedList<>();
        visited[start] = true;
        queue.add(start);
        int length = 0;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            visited[current] = true;

            boolean found = false;
            for (int i = 0; i < vertexCount; i++) {
                if (adj[current][i] > 0 && !visited[i]) {
                    found = true;
                    queue.add(i);
                }
            }

            // Increment the level size.
            if (found) {
                length++;
            }
        }

        return length;
    }
}
