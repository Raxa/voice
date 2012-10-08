package jarvis.utils.textprocessing.distance;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Levenshtein {
	
	private List<CommensurableString> reference;
	private List<CommensurableString> hypothesis;
	private int[][] mat;
	
	public Levenshtein(LinkedList<CommensurableString> reference, 
			LinkedList<CommensurableString> hypothesis) {
		
		this.reference = reference;
		this.hypothesis = hypothesis;
		this.mat = new int[reference.size() + 1][hypothesis.size() + 1];
		init();
	}
	
	private void init() {
		for(int iter = 0; iter < reference.size() + 1; iter++){
			mat[iter][0] = iter;
		}
		
		for(int iter = 0; iter <  hypothesis.size() + 1;  iter++) {
			mat[0][iter] = iter;
		}		
	}
	
	public int computeDistance(){
		int distance = 0;
		Iterator<CommensurableString> refIter = reference.listIterator();
		for(int i = 1; i < reference.size() + 1; i++) {
			CommensurableString refObj = refIter.next();
			Iterator<CommensurableString> hypIter = hypothesis.listIterator();
			for(int j = 1; j < hypothesis.size() + 1; j++) {
				CommensurableString hypObj = hypIter.next();
				if(!refObj.isEqual(hypObj)) {
					mat[i][j] = Math.min(mat[i-1][j] + 1, mat[i][j-1] + 1);
				} else {
					mat[i][j] = mat[i-1][j-1];
				}				
			}
		}
		distance = mat[reference.size()][hypothesis.size()];
		return distance;
	}

}
