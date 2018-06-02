

import java.util.ArrayList;

/**
 * Dependency query , each query have this form : P(J=true|B=true,B=true,B=true,B=true...),A-E-M ..
 * @author Oleg
 *
 */
public class ProbabilityQuery extends Query{

	public Pair src;
	public ArrayList<String> eliminationOrder = new ArrayList<>();
	public ArrayList<Pair> given = new ArrayList<>();

	/**
	 * @param src - His probability must be calculated 
	 * @param given - given variables.
	 * @param eliminationOrder - elimination order.
	 */
	public ProbabilityQuery(Pair src, ArrayList<Pair> given, ArrayList<String> eliminationOrder) {
		this.src = src;
		this.given.addAll(given);
		this.eliminationOrder.addAll(eliminationOrder);
	}
	
	@Override
	public String toString() {
		return "ProbabilityQuery [src=" + src + ", eliminationOrder=" + eliminationOrder + ", given=" + given + "]";
	}

	public Pair getSrc() {
		return src;
	}

	public void setSrc(Pair src) {
		this.src = src;
	}

	public ArrayList<String> getEliminationOrder() {
		return eliminationOrder;
	}

	public void setEliminationOrder(ArrayList<String> eliminationOrder) {
		this.eliminationOrder = eliminationOrder;
	}

	public ArrayList<Pair> getGiven() {
		return given;
	}

	public void setGiven(ArrayList<Pair> given) {
		this.given = given;
	}
	
	public String getGivenValue(String name){
		
		for (int i = 0; i < given.size(); i++) {
			if(given.get(i).getName().equals(name)){
				return given.get(i).getValue();
			}
		}
		return null;
	}

}
