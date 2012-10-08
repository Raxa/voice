import jarvis.utils.textprocessing.distance.CommensurableString;
import jarvis.utils.textprocessing.distance.Levenshtein;

import java.util.LinkedList;
import java.util.StringTokenizer;


public class Demo {

	public static void main(String Args[]) {
		String ref = "Apurv is a god boy";
		String hyp = "is god boy";
		StringTokenizer refTok = new StringTokenizer(ref);
		LinkedList<CommensurableString> refList = new 
				LinkedList<CommensurableString>();
		StringTokenizer hypTok = new StringTokenizer(hyp);
		LinkedList<CommensurableString> hypList = new 
				LinkedList<CommensurableString>();
		while(refTok.hasMoreTokens()) {
			refList.add(new CommensurableString(refTok.nextToken()));
		}
		while(hypTok.hasMoreTokens()) {
			hypList.add(new CommensurableString(hypTok.nextToken()));
		}
		
		Levenshtein lev = new Levenshtein(refList, hypList);
		System.out.println(lev.computeDistance());
	}
}
