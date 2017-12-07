package com.stardisblue.ast.structure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Matrix {
    private final String[][] table;
    private final int[][] matrix;
    private HashMap<String, Integer> ids;
    private String[] names;
    private int compteur = 0;

    public Matrix(List<String> keys) {
        ids = new HashMap<>();
        names = new String[keys.size()];
        for (String key : keys) {
            names[ids.size()] = key;
            ids.put(key, ids.size());
        }

        matrix = new int[ids.size()][ids.size()];
        table = new String[ids.size() + 1][ids.size() + 1];

        for (String[] strings : table) {
            Arrays.fill(strings, "");
        }
    }

    public void increment(String key, String key2) {
        if (!ids.containsKey(key) || !ids.containsKey(key2)) {
            return;
        }

        int keyId = ids.get(key);
        int key2Id = ids.get(key2);

        ++matrix[keyId][key2Id];
        ++compteur;
    }

    public void generateTable() {
        // because first cell is empty ~tehe pero
        for (int i = 0; i < names.length; ++i) { // setting up table name
            table[0][i + 1] = names[i];
            table[i + 1][0] = names[i];
        }

        for (int i = 0; i < matrix.length; ++i) { // it's a square enix
            for (int j = 0; j < matrix.length; ++j) {
                if (i == j) continue;
                int sum = matrix[i][j] + matrix[j][i];
                table[i + 1][j + 1] = (sum != 0) ?
                        (String.valueOf((float) sum / compteur) + " (" + sum + "/" + compteur + ")") :
                        "-";
            }

            table[i + 1][i + 1] = (matrix[i][i] != 0) ?
                    String.valueOf((float) matrix[i][i] / compteur) + " (" + matrix[i][i] + "/" + compteur + ")" :
                    "-";
        }
    }

    public String[][] getTable() {
        return table;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public String[] getNames() {
        return names;
    }

    public int getTotal() {
        return compteur;
    }
}
