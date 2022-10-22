package utils;

public class Graph {
    /**
     * DFS in an undirected graph.
     * @param adj         - Adjacency matrix
     * @param visited     - Integer vector
     * @param vertexCount - Number of vertexes of the graph
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
}
