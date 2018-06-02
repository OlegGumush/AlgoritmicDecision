

import java.util.ArrayList;

/**
 * Dependency query , each query have this form : B-E|J=true,J=true,J=true,J=true
 * @author Oleg
 *
 */
public class DependencyQuery extends Query{

	String src;
	String dest;
	public ArrayList<String> given = new ArrayList<>();

	/**
	 * @param src - start node
	 * @param dest - end node
	 * @param given - given variables
	 */
	public DependencyQuery(String src , String dest , ArrayList<Pair> given){
		
		this.src = src;
		this.dest = dest;
		for (int i = 0; i < given.size(); i++) {
			this.given.add(given.get(i).getName());
		}		
	}

	/**
	 * print query like in the file.
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append(src.toString());
		s.append(Constants.HYPHEN);
		s.append(dest.toString());
		s.append(Constants.STANDING_LINE);

		for (int i = 0; i < given.size()-1; i++) {
			s.append(given.get(i).toString());
			s.append(Constants.EQUAL);
		}
		if(given.size() >0){
			s.append(given.get(given.size()-1));
			
		}
		
		return s.toString();
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public ArrayList<String> getGiven() {
		return given;
	}

	public void setGiven(ArrayList<String> given) {
		this.given = given;
	}
	
}
