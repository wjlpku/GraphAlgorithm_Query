package Query.memory;

import java.util.ArrayList;
import java.util.List;

public class structForSort {
	
	public int a,b,c,d,e;
	List<EvaluationPath> paths = new ArrayList<EvaluationPath>();
	public double ratio;
	public structForSort(){c=-1;}
	public structForSort(int _a, int _b) {
		// TODO Auto-generated constructor stub
		a = _a;
		b = _b;
	}
	public structForSort(int _a, int _b, int _c) {
		// TODO Auto-generated constructor stub
		a = _a;
		b = _b;
		c = _c;
	}
	public structForSort(int _a, int _b, int _c, int _d) {
		// TODO Auto-generated constructor stub
		a = _a;
		b = _b;
		c = _c;
		d = _d;
	}
	public String getContent(){
		return a+" "+b+" "+c+" "+d+" "+e+" "+ratio;
	}
}
