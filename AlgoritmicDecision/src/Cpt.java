

import java.util.ArrayList;
import java.util.Hashtable;
/**
 * 
 * @author Oleg
 *
 */
public class Cpt {
	private Hashtable<ArrayList<String>, Double> cpt;

	/**
	 * 
	 * @param cpt - Arraylist of Arraylists represented matrix of string. (CPT table)
	 */
	public Cpt(ArrayList<ArrayList<String>> cpt , ArrayList<String> values ,ArrayList<String> parents ) {
		this.cpt = new Hashtable<>();
		
		String missedValue = getMissedValue(cpt , values);
		
		//Running on the rows of the table
		for (int i = 0; i < cpt.size(); i++) {
			
			//=variable - A variable I keep to its probability given certain variables
			ArrayList<String> vars = new ArrayList<>();
			//The variables are the givens of the =variable
			ArrayList<String> given = new ArrayList<>();
			//Probabilities are reserved in exactly the same order as the variables
			ArrayList<String> probabilities = new ArrayList<>();
			
			//Running on the columns of the specific row
			for (int j = 0; j < cpt.get(i).size(); j++) {
				String var = cpt.get(i).get(j);
				
				//=variable
				if(var.contains(Constants.EQUAL)){
					//Keeps it and its probability will be the key and the probability will be value of the second hash table.
					vars.add(var.substring(1));
					probabilities.add(cpt.get(i).get(j+1));
				}
				//No base variable or probability
				else if(!var.contains(Constants.SPOT)){
					given.add(var);
				}
			}
			
			
			//After line analysis I'm ready to put it in the hash
			createCptLine(vars , probabilities , given , missedValue);
		}		
	}

	private String getMissedValue(ArrayList<ArrayList<String>> cpt, ArrayList<String> values) {
		
		ArrayList<String> temp = new ArrayList<>();
		temp.addAll(values);
		for (int i = 0; i < cpt.size(); i++) {
			for (int j = 0; j < cpt.get(i).size(); j++) {
				if(cpt.get(i).get(j).startsWith(Constants.EQUAL)){
					String var = cpt.get(i).get(j).substring(1);
					if(temp.contains(var)){
						temp.remove(var);
					}
				}
			}
		}
		//this.get
		return temp.get(0);
	}

	/**
	 * @param vars - A variables list
	 * @param probabilities - A probabilities list
	 * @param given - A given variables list
	 * @param missedValue 
	 */
	private void createCptLine(ArrayList<String> vars, ArrayList<String> probabilities, ArrayList<String> given, String missedValue) {
				
		for (int i = 0; i < vars.size(); i++) {
			ArrayList<String> givenAndVar = new ArrayList<>();
			ArrayList<String> givenAndMissed = new ArrayList<>();
			givenAndVar.addAll(given);
			givenAndMissed.addAll(given);
			givenAndVar.add(vars.get(i));
			givenAndMissed.add(missedValue);			
			
			if(cpt.get(givenAndVar) == null){
				cpt.put(givenAndVar, new Double(probabilities.get(i)));
			}
			if(cpt.get(givenAndMissed) == null){
				cpt.put(givenAndMissed, 1 - new Double(probabilities.get(i)));
			}else{
				cpt.put(givenAndMissed, cpt.get(givenAndMissed) - new Double(probabilities.get(i)));
			}
		}
	}

	public Hashtable<ArrayList<String>, Double> getCpt() {
		return cpt;
	}

	public void setCpt(Hashtable<ArrayList<String>, Double> cpt) {
		this.cpt = cpt;
	}

	@Override
	public String toString() {
		return "Cpt [cpt=" + cpt + "]";
	}
	
	
}













