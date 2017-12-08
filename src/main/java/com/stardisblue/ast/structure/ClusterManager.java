package com.stardisblue.ast.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClusterManager<T> {

    private ArrayList<Cluster<T>> clusters;

    private HashMap<Integer, Integer> clusterIds;

    private int totalCount;

    private int[][] dynamicMatrix;

    public ClusterManager(List<Cluster<T>> clusters, int[][] adjacentMatrix, int sumValue) {
        this.clusters = new ArrayList<>(clusters.size());
        this.clusterIds = new HashMap<>(clusters.size());

        totalCount = sumValue;

        // copy of clusters
        for (Cluster<T> cluster : clusters) {
            this.clusters.add(cluster);
            this.clusterIds.put(cluster.getName(), clusterIds.size());
        }

        // copy of matrix
        this.dynamicMatrix = new int[adjacentMatrix.length][adjacentMatrix.length];
        for (int i = 0; i < adjacentMatrix.length; i++) {
            System.arraycopy(adjacentMatrix[i], 0, dynamicMatrix[i], 0, adjacentMatrix.length);
        }
    }

    /**
     * Sets up cluster similarity only if the clusters did not have similarity values
     *
     * @param firstId first clusterId
     * @param lastId  second clusterId
     */
    public void initSimilarity(int firstId, int lastId) {
        // if one of these cluster is not present in my matrix
        if (firstId == lastId) {
            return;
        }
        // the number of links between them
        int similarityValue = dynamicMatrix[firstId][lastId] + dynamicMatrix[lastId][firstId];

        clusters.get(firstId).initSimilarity(similarityValue);
        clusters.get(lastId).initSimilarity(similarityValue);
    }

    /**
     * Updates the clusterTree by fusing the corresponding cluster of firstId and lastId
     * <p>
     * updates the clusters and the matrix
     *
     * @param firstId a cluster to fuse
     * @param lastId  a cluster to fuse
     */
    public void updateMatrix(int firstId, int lastId) {
        // if one of these cluster is not present in my matrix
        if (firstId == lastId) {
            return;
        }

        // we get that for the dynamic matrix before the update
        //int firstId = clusterIds.get(toFuseFirst.getName());
        //int lastId = clusterIds.get(toFuseLast.getName());

        Cluster<T> toFuseFirst = clusters.get(firstId);
        Cluster<T> toFuseLast = clusters.get(lastId);

        // there is one less cluster
        int fusedCapacity = clusters.size() - 1;


        //biggest available name is always last
        int fusedName = clusters.get(fusedCapacity).getName() + 1;

        // merging values of both
        // removing from clusters and adding fused to clusters
        ArrayList<Cluster<T>> replacedClusters = new ArrayList<>(fusedCapacity);
        HashMap<Integer, Integer> replacedClusterIds = new HashMap<>(fusedCapacity);

        for (Cluster<T> cluster : clusters) {
            // we ignore these ones, we recopy the rest
            if (cluster.getName() == toFuseFirst.getName() || cluster.getName() == toFuseLast.getName()) {
                continue;
            }

            replacedClusters.add(cluster);
            replacedClusterIds.put(cluster.getName(), replacedClusterIds.size());
        }

        // the fused cluster
        int similarity = dynamicMatrix[firstId][lastId] + dynamicMatrix[lastId][firstId];
        Cluster<T> fused = new Cluster<>(fusedName, toFuseFirst, toFuseLast, similarity);

        // we add it as last
        int fusedId = replacedClusterIds.size();
        replacedClusters.add(fused);
        replacedClusterIds.put(fusedName, fusedId);

        clusters = replacedClusters;
        clusterIds = replacedClusterIds;

        int iterCorrection = 0; // because reindexing and also because they have not the same size
        int[][] updatedDynamicMatrix = new int[fusedCapacity][fusedCapacity]; // one size less matrix
        for (int i = 0; i < dynamicMatrix.length; i++) {
            if (i == lastId || i == firstId) { // copy while ignoring the removed clusters
                ++iterCorrection;
                continue;
            }
            // setting up the values for the merged cluster
            updatedDynamicMatrix[fusedId][i - iterCorrection] = dynamicMatrix[firstId][i] + dynamicMatrix[lastId][i];
            updatedDynamicMatrix[i - iterCorrection][fusedId] = dynamicMatrix[i][firstId] + dynamicMatrix[i][lastId];
        }

        // the sum of first with first and last with last and the value between the two of them
        // we dont care about the linktoself value
        //updatedDynamicMatrix[fusedId][fusedId] = dynamicMatrix[firstId][firstId] + dynamicMatrix[lastId][lastId] +
        //        dynamicMatrix[firstId][lastId] + dynamicMatrix[lastId][firstId];

        // now we need to remove the ancient link
        int rowCorrection = 0;
        for (int i = 0; i < dynamicMatrix.length; i++) {
            if (i == lastId || i == firstId) { // we ignore this row
                ++rowCorrection; // it does not exist in the new array
                continue;
            }
            int columnCorrection = 0;
            for (int j = 0; j < dynamicMatrix.length; j++) {
                if (j == lastId || j == firstId) {// we ignore this cell
                    ++columnCorrection; // it does not exist in the new array
                    continue;
                }

                updatedDynamicMatrix[i - rowCorrection][j - columnCorrection] = dynamicMatrix[i][j];
            }
        }

        // the two loop can be merged into one, but I will leave it like that for clarity purpose

        dynamicMatrix = updatedDynamicMatrix;
    }

    /**
     * Returns a pair of ClusterId with the highest relation
     *
     * @return an array of lenght 2 containing the pair of clusterIds
     */
    public int[] getClosestPairId() {
        int[] pair = new int[]{-1, -1};
        int max = -1;
        for (int i = 0; i < dynamicMatrix.length; i++) {
            for (int j = 0; j < dynamicMatrix.length; j++) {
                if (i == j) {
                    continue;
                }

                if (dynamicMatrix[i][j] > max) {
                    max = dynamicMatrix[i][j] + dynamicMatrix[j][i];
                    pair[0] = i;
                    pair[1] = j;
                }
            }
        }

        return pair;
    }

    public ArrayList<Cluster<T>> getClusters() {
        return clusters;
    }
}
