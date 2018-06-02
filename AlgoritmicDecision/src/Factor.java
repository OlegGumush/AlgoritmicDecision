

import java.util.ArrayList;
import java.util.Hashtable;

public class Factor implements Comparable<Factor>{

	ArrayList<String> name = new ArrayList<>();
	ArrayList<Pair> given = new ArrayList<>();
	Hashtable<ArrayList<String>, Double> values = new Hashtable<>();

	public Factor(){

	}

	public Factor(ArrayList<String> name, Hashtable<ArrayList<String>, Double> values) {
		this.name.addAll(name);
		for (ArrayList<String> a : values.keySet()) {
			ArrayList<String> a1 = new ArrayList<>();
			a1.addAll(a);
			this.values.put(a1, values.get(a));
		}
	}

	public ArrayList<String> getName() {
		return name;
	}

	public void setName(ArrayList<String> name) {
		this.name = name;
	}



	public ArrayList<Pair> getGiven() {
		return given;
	}

	public void setGiven(ArrayList<Pair> given) {
		this.given = given;
	}

	public Hashtable<ArrayList<String>, Double> getValues() {
		return values;
	}

	public void setValues(Hashtable<ArrayList<String>, Double> values) {
		this.values = values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((given == null) ? 0 : given.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}


	@Override
	public int compareTo(Factor o) {

		if(this.values.keySet().size() < o.values.keySet().size()){
			return 1;
		}
		return 0;
	}


	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("Factor: " +this.name +"\n");
		for (ArrayList<String> l : values.keySet()) {
			s.append(l + ":" + values.get(l) +"\n");
		}
		s.append("-----------------------------------------------------\n");
		return s.toString();
	}
	
	
	
}
