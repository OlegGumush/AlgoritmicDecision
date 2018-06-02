

import java.util.ArrayList;

/**
 * @author Oleg
 *
 */
public class Var {

	private String varName ;
	private ArrayList<String> values = new ArrayList<>();
	private ArrayList<String> parents = new ArrayList<>();
	private ArrayList<String> childrens = new ArrayList<>();
	private Cpt cpt;
	
	/**
	 * @param varName - variable name.
	 * @param values - A list of possible values that the variable can get
	 * @param parents - A list of possible parents.
	 * @param cpt - CPT "table".
	 */
	public Var(String varName, ArrayList<String> values, ArrayList<String> parents, ArrayList<ArrayList<String>> cpt) {
		this.varName = varName ;
		this.parents.addAll(parents);
		this.values.addAll(values);
		this.cpt = new Cpt(cpt , values,parents);
	}
	
	

	public String toString(){
		
		StringBuffer s= new StringBuffer();
		s.append("Var "+this.varName+"\n");
		s.append("Values: ");
		for (int i = 0; i < values.size(); i++) {
			s.append(values.get(i));
			s.append(",");
		}
		s.append('\n');
		s.append("Parents: ");
		for (int i = 0; i < parents.size(); i++) {
			s.append(parents.get(i)+",");
		}
		s.append('\n');
		s.append("CPT:\n");
		s.append(cpt.toString());
		s.append("\nChildrens :");
		for (int i = 0; i < childrens.size(); i++) {
			s.append(childrens.get(i)+",");
		}
		s.append("\n");
		return s.toString();
	}



	public String getVarName() {
		return varName;
	}

	public ArrayList<String> getValues() {
		return values;
	}

	public ArrayList<String> getParents() {
		return parents;
	}
	
	
	public ArrayList<String> getChildrens() {
		return childrens;
	}

	public void setChildrens(ArrayList<String> childrens) {
		this.childrens.addAll(childrens);
	}

	public Cpt getCpt() {
		return cpt;
	}
}

