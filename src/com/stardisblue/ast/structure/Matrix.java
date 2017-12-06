package com.stardisblue.ast.structure;

import java.util.ArrayList;
import java.util.HashMap;

public class Matrix {
	private int[][] matrix;
	private HashMap<String, Integer> ids;

	public Matrix(List<String> keys){
		ids = new HashMap<>();

		for(String key : keys){
			ids.put(key, ids.size());
		}

		matrix = new int[ids.size()][ids.size()];
	}

	public void increment(String key, String key2){
		if(!ids.containsKey(key) || !ids.containsKey(key2)){
			return;
		}

		int keyId = ids.get(key);
		int key2Id = ids.get(key2);

		matrix[keyId][key2Id]++;

		// avoiding doublecount if is the same
		if(keyId != key2Id){
			matrix[key2Id][keyId]++;
		}
	}
}
