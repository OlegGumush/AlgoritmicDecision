

import java.util.ArrayList;
import java.util.Hashtable;

public class AlgorithemVariableElimination implements Algorithem{

	private ArrayList<Var> network = null;
	private ArrayList<Pair> given = null;
	private Pair src = null;
	private ArrayList<Factor> factors = new ArrayList <>();
	private ProbabilityQuery query = null;
	private BayesianNetwork bayes ;
	private ArrayList<String> parentsOfQueryOrEvidence = new ArrayList<>();
	private ArrayList<String> eliminationOrder = null;
	private Double probability ;
	private Integer numberOfMults;
	private Integer numberOfSums;

	@Override
	public String resolveQuery(Query q , BayesianNetwork n) {

		//query
		query = (ProbabilityQuery)q;
		//query variable
		src = query.getSrc();
		//list of given variables
		given = query.getGiven();
		//list of vars
		network = n.getNetwork();
		//basyan network
		bayes = n;
		//elimination order
		eliminationOrder = query.getEliminationOrder();

		numberOfMults = 0;
		numberOfSums = 0;
		
		buildFactors();
		joinFactorsByEliminationOrder();
		joinRemainingFactors();
		eleminateLastFactor(factors.get(0));		
		return getAnsFromLastFactor(factors.get(0));
		
	}

	
	
	public Double getProbability() {
		return probability;
	}



	public void setProbability(Double probability) {
		this.probability = probability;
	}



	public Integer getNumberOfMults() {
		return numberOfMults;
	}



	public void setNumberOfMults(Integer numberOfMults) {
		this.numberOfMults = numberOfMults;
	}



	public Integer getNumberOfSums() {
		return numberOfSums;
	}



	public void setNumberOfSums(Integer numberOfSums) {
		this.numberOfSums = numberOfSums;
	}



	/**
	 * Run on elimination order from input file.
	 * Get two factors if factors not null join them.
	 * If factors null remove from elimination order.
	 * If one of them null we eliminate this factor and continue.
	 */
	private void joinFactorsByEliminationOrder() {
		while(eliminationOrder.size() > 0){
			String currVar= eliminationOrder.get(0);
			//this function remove factor from list
			Factor f1= findFactorThatMentionHidden(currVar);
			Factor f2= findFactorThatMentionHidden(currVar);

			if(f1 == null && f2 == null){
				eliminationOrder.remove(currVar);
			}
			//join two factors
			if(f1 != null && f2 != null){
				Join(f1, f2 );
				sortFactors();
				continue;
			}
			if(f1!= null){
				//check if f1 has more than one colon if yes we need to eliminate current var
				if(f1.getName().size() > 1){
					eliminate(f1, currVar);
					eliminationOrder.remove(0);
				}else{
					factors.add(f1);
					eliminationOrder.remove(0);
				}
			}
		}		
	}

	/**
	 * Remove all colon's that are not query.
	 * @param fac - last factor
	 */
	private void eleminateLastFactor(Factor fac) {
		ArrayList<String> names = fac.getName();

		for (int i = 0; i < names.size(); i++) {
			if(names.get(i).equals(src.getName())){
				continue;
			}
			Factor toSend = factors.remove(0);
			eliminate(toSend , names.get(i));
		}
	}

	/**
	 * After elimination process we eliminate remain factors
	 */
	private void joinRemainingFactors() {
		while(factors.size()>1){
			Factor f1= factors.remove(0);
			Factor f2= factors.remove(0);
			Join(f1, f2);
		}		
	}

	/**
	 * Compute alpha and return answer to query
	 * @param fac
	 * @return
	 */
	private String getAnsFromLastFactor(Factor fac) {

		double alpha ;
		double sum =0;
		//sum all rows
		for (Double d : fac.getValues().values()) {
			sum += d;
		}
		//compute alpha
		alpha = 1/sum;

		//Multiply all values by alpha
		for(ArrayList<String> string : fac.getValues().keySet()) {
			fac.getValues().put(string, fac.getValues().get(string)*alpha);
		}
		
		
		
		//Create an appropriate entry to the query
		ArrayList<String> a = new ArrayList<>();
		a.add(src.getValue());
		
		this.numberOfSums+=fac.getValues().values().size()-1;
		
		//return answer to the query
		return fac.getValues().get(a).toString();
	}

	
	/**
	 * Join two factors
	 * @param f1 - factor1
	 * @param f2 - factor2
	 */
	private void Join(Factor f1, Factor f2) {

		//find common colon's
		ArrayList<String> commonColomns = findCommonColomnsName(f1,f2);
		//name to the new factor
		ArrayList<String> table3Name = CreateNewVarNameFromTwoFactors(f1, f2, commonColomns);

		//common indexes 
		ArrayList<Integer> factor1indexes = new ArrayList<>();
		ArrayList<Integer> factor2indexes = new ArrayList<>();
		for (int i = 0; i < commonColomns.size(); i++) {
			factor1indexes.add(getIndexByNameFronName(f1.getName() , commonColomns.get(i)));
			factor2indexes.add(getIndexByNameFronName(f2.getName() , commonColomns.get(i)));
		}


		//retrive two tables
		Hashtable<ArrayList<String>, Double> t1 = f1.getValues();
		Hashtable<ArrayList<String>, Double> t2 = f2.getValues();
		Hashtable<ArrayList<String>, Double> t3 = new Hashtable<>();

		// first table lines
		for (ArrayList<String> line1 : t1.keySet()) {

			// second table lines
			for (ArrayList<String> line2 : t2.keySet()) {

				boolean needToMultipleLines = true;
				//check if two lines have same values in common colon's
				for (int i = 0; i < factor1indexes.size(); i++) {
					if(!line1.get(factor1indexes.get(i)).equals(line2.get(factor2indexes.get(i)))){
						needToMultipleLines = false;
					}
				}

				//multiple lines
				if(needToMultipleLines){
					//build new key
					ArrayList<String> key = new ArrayList<>();
					for (int i = 0; i < table3Name.size(); i++) {

						if(f1.getName().contains(table3Name.get(i))){
							key.add(line1.get(getIndexByNameFronName(f1.getName() , table3Name.get(i))));
						}
						else if(f2.getName().contains(table3Name.get(i))){
							key.add(line2.get(getIndexByNameFronName(f2.getName() , table3Name.get(i))));
						}
					}
					this.numberOfMults++;
					t3.put(key, t1.get(line1) * t2.get(line2));
				}
			}
		}
		//create new factor
		factors.add(new Factor(table3Name ,t3));
	}

	/**
	 * Get two factors and return new name based on theit tables
	 * @param f1
	 * @param f2
	 * @param commonColomns - names of common colomns
	 * @return - list of string that represent new name
	 */
	private ArrayList<String> CreateNewVarNameFromTwoFactors(Factor f1, Factor f2, ArrayList<String> commonColomns) {
		
		//add all common
		ArrayList<String> table3Name = new ArrayList<>();
		table3Name.addAll(commonColomns);
		//add new values from factor1
		for (int i = 0; i < f1.getName().size(); i++) {
			if(!table3Name.contains(f1.getName().get(i))){
				table3Name.add(f1.getName().get(i));
			}
		}
		//add new values from factor2
		for (int i = 0; i < f2.getName().size(); i++) {
			if(!table3Name.contains(f2.getName().get(i))){
				table3Name.add(f2.getName().get(i));
			}
		}
		return table3Name;
	}

	/**
	 * Find index in the array
	 * @param name - array of names
	 * @param s - specific name
	 * @return - index of s in name
	 */
	private int getIndexByNameFronName(ArrayList<String> name, String s) {

		for (int i = 0; i < name.size(); i++) {
			if(name.get(i).equals(s)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Find common strings (names) in two factor
	 * @param f1
	 * @param f2
	 * @return - list with common strings
	 */
	private ArrayList<String> findCommonColomnsName(Factor f1,	Factor f2) {
		ArrayList<String> ans = new ArrayList<>();

		for (int i = 0; i < f2.getName().size(); i++) {
			if(f1.getName().contains(f2.getName().get(i))){
				ans.add(f2.getName().get(i));
			}
		}
		return ans;
	}

	/**
	 * Get factor and value (name of colon) and eliminate factor by this colon
	 * @param f
	 * @param currVar
	 */
	private void eliminate(Factor f, String currVar) {

		Hashtable<ArrayList<String>, Double> newValues = new Hashtable<>();		

		//find match colon
		int index =0;
		for (int i = 0; i < f.getName().size(); i++) {
			if(f.getName().get(i).equals(currVar)){
				index = i;
				break;
			}
		}

		for (int i = 0; i < f.getValues().keySet().size(); i++) {
			ArrayList<String> line1 = (ArrayList<String>)f.getValues().keySet().toArray()[i];

			ArrayList<String> newKey = new ArrayList<>();
			newKey.addAll(line1);
			newKey.remove(index);

			if(newValues.get(newKey) == null){
				newValues.put(newKey, f.getValues().get(line1));
			}else{
				this.numberOfSums++;
				newValues.put(newKey, newValues.get(newKey) + f.getValues().get(line1));
			}
		}
		f.getName().remove(currVar);
		f.setValues(newValues);
		factors.add(f);
		sortFactors();
	}

	/**
	 * bubble sort
	 */
	private void sortFactors() {

		Object[] arr = factors.toArray();
		int n = arr.length;  
		Factor temp = null;  
		for(int i=0; i < n; i++){  
			for(int j=1; j < (n-i); j++){  
				int a =((Factor)arr[j-1]).getValues().size();
				int b =((Factor)arr[j]).getValues().size();
				if(a > b){  
					//swap elements  
					temp = (Factor)arr[j-1];  
					arr[j-1] = (Factor)arr[j];  
					arr[j] = (Factor)temp;  
				}  
			}  
		}  
		factors.removeAll(factors);
		for (int i = 0; i < arr.length; i++) {
			factors.add((Factor)arr[i]);
		}
	}

	/**
	 * Find factor by name
	 * @param currVar - name
	 * @return
	 */
	private Factor findFactorThatMentionHidden(String currVar) {
		for (int i = 0; i < factors.size(); i++) {
			if(factors.get(i).getName().contains(currVar)){
				Factor ans = factors.get(i);
				factors.remove(ans);
				return ans;
			}
		}
		return null;
	}

	/**
	 * build factor from each var in the network
	 */
	private void buildFactors() {

		findAllParentsOfQueryAndGiven();

		for (int i = 0; i < network.size(); i++) {
			Var var = network.get(i);

			//create query factor
			if(src.getName().equals(network.get(i).getVarName())) {
				createFactor(var);
				continue;
			}

			//if given and not have parents : continue
			if(isGiven(var.getVarName()) && var.getParents().isEmpty()){
				continue;
			}

			//var is not parent of query or given : continue
			if(!parentsOfQueryOrEvidence.contains(var.getVarName())){
				continue;
			}

			createFactor(var);
		}
		sortFactors();
	}


	/**
	 * this function finds all the ancestor of the query/evidence variables
	 * (sends all the query/evidence variables to the recursive function)
	 */
	public void findAllParentsOfQueryAndGiven(){
		this.parentsOfQueryOrEvidence.add(src.getName());
		for (int i = 0; i < given.size(); i++) {
			this.parentsOfQueryOrEvidence.add(given.get(i).getName());
		}
		FindAllParentsOfQueryAndGivenRecursively(src.getName());
		for (int i = 0; i < given.size(); i++) {
			FindAllParentsOfQueryAndGivenRecursively(given.get(i).getName());
		}
	}

	/**
	 * this recursive function gets a variable and find all his ancestors
	 * enter them to ancestorsOfEvidanceOrQueries list
	 * @param currVar the variable we need to find all of his ancestors
	 */
	public void FindAllParentsOfQueryAndGivenRecursively(String currVar){
		if(bayes.getVarByName(currVar).getParents().size() == 0){
			return;
		}
		else{
			for (int j = 0; j < bayes.getVarByName(currVar).getParents().size() ; j++) {
				String parent= bayes.getVarByName(currVar).getParents().get(j);
				if(!parentsOfQueryOrEvidence.contains(parent)){
					parentsOfQueryOrEvidence.add(parent);
					FindAllParentsOfQueryAndGivenRecursively(parent);
				}
			}
		}
	}

	/**
	 * create factor from variable
	 * @param var - variable
	 */
	private void createFactor(Var var) {
		
		factors.add(new Factor( getFactorName(var) , getFactorValues(var) ));
	}

	/**
	 * create factor table
	 * @param var
	 * @return
	 */
	private Hashtable<ArrayList<String> , Double> getFactorValues(Var var) {

		ArrayList<Integer> parentsGivenIndexes = new ArrayList<>();
		ArrayList<String> parentsName = new ArrayList<>();
		ArrayList<String> parentsValue = new ArrayList<>();

		getParentsParameters(var, parentsGivenIndexes, parentsName, parentsValue);

		Hashtable<ArrayList<String>, Double> tempCpt = copyCpt(var);
		Hashtable<ArrayList<String>, Double> result = new Hashtable<>();

		//without parent
		if(parentsName.isEmpty()){
			return tempCpt;
		} 		

		for (ArrayList<String> line : var.getCpt().getCpt().keySet()) {

			if(isLineShouldStay(line , parentsGivenIndexes , parentsValue)){

				ArrayList<String> newKeyWithoutParents = createNewCptLineWithoutParents(line , parentsGivenIndexes , parentsValue);
				result.put(newKeyWithoutParents, var.getCpt().getCpt().get(line));			
			}

		}

		return result;
	}

	/**
	 * Create new cpt line
	 * @param line
	 * @param parentsGivenIndexes
	 * @param parentsValue
	 * @return
	 */
	private ArrayList<String> createNewCptLineWithoutParents(ArrayList<String> line,ArrayList<Integer> parentsGivenIndexes, ArrayList<String> parentsValue) {

		ArrayList<String> result = new ArrayList<>(line);

		for (int i = parentsGivenIndexes.size() -1 ; i >=0; i--) {
			result.remove((int)parentsGivenIndexes.get(i));
		}
		return result;
	}

	/**
	 * Check if this line should stay
	 * @param line
	 * @param parentsGivenIndexes
	 * @param parentsValue
	 * @return
	 */
	private boolean isLineShouldStay(ArrayList<String> line, ArrayList<Integer> parentsGivenIndexes,ArrayList<String> parentsValue) {

		for (int i = 0; i < parentsGivenIndexes.size(); i++) {

			if(!line.get(parentsGivenIndexes.get(i)).equals(parentsValue.get(i))){
				return false;
			}
		}
		return true;
	}

	/**
	 * copy cpt table from var
	 * @param var
	 * @return
	 */
	private Hashtable<ArrayList<String>, Double> copyCpt(Var var) {

		Hashtable<ArrayList<String>, Double> result = new Hashtable<>();

		for (ArrayList<String> list : var.getCpt().getCpt().keySet()) {

			ArrayList<String> key = new ArrayList<>();
			key.addAll(list);
			result.put(key, var.getCpt().getCpt().get(key));
		}

		return result;
	}

	/**
	 * 
	 * @param var
	 * @param parentsGivenIndexes
	 * @param parentsName
	 * @param parentsValue
	 */
	private void getParentsParameters(Var var, ArrayList<Integer> parentsGivenIndexes, ArrayList<String> parentsName,ArrayList<String> parentsValue) {
		ArrayList<String> a = new ArrayList<>();
		a.addAll(var.getParents());
		a.add(var.getVarName());

		for (int i = 0; i < a.size(); i++) {
			for (int j = 0; j < given.size(); j++) {
				if(given.get(j).getName().equals(a.get(i))){
					parentsGivenIndexes.add(i);
					parentsName.add(given.get(j).getName());
					parentsValue.add(given.get(j).getValue());
				}
			}
		}
	}

	/**
	 * Create factor name
	 * @param var - variable
	 * @return - list that represented name
	 */
	private ArrayList<String> getFactorName(Var var) {

		ArrayList<String> name = new ArrayList<>();

		for (int i = 0; i < var.getParents().size(); i++) {
			if(!isGiven(var.getParents().get(i))){
				name.add(var.getParents().get(i));
			}
		}
		if(!isGiven(var.getVarName())){
			name.add(var.getVarName());
		}
		return name;
	}

	/**
	 * function check if this name is given from query
	 * @param name - variable name
	 * @return - boolean 
	 */
	private boolean isGiven(String name){

		for (int i = 0; i < given.size(); i++) {
			if(given.get(i).getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	public ArrayList<Var> getNetwork() {
		return network;
	}

	public void setNetwork(ArrayList<Var> network) {
		this.network = network;
	}

	public ArrayList<Pair> getGiven() {
		return given;
	}

	public void setGiven(ArrayList<Pair> given) {
		this.given = given;
	}

	public Pair getSrc() {
		return src;
	}

	public void setSrc(Pair src) {
		this.src = src;
	}

	public ArrayList<Factor> getFactors() {
		return factors;
	}

	public void setFactors(ArrayList<Factor> factors) {
		this.factors = factors;
	}

	public ProbabilityQuery getQuery() {
		return query;
	}

	public void setQuery(ProbabilityQuery query) {
		this.query = query;
	}

	public BayesianNetwork getBayes() {
		return bayes;
	}

	public void setBayes(BayesianNetwork bayes) {
		this.bayes = bayes;
	}

	public ArrayList<String> getParentsOfQueryOrEvidence() {
		return parentsOfQueryOrEvidence;
	}

	public void setParentsOfQueryOrEvidence(ArrayList<String> parentsOfQueryOrEvidence) {
		this.parentsOfQueryOrEvidence = parentsOfQueryOrEvidence;
	}

	public ArrayList<String> getEliminationOrder() {
		return eliminationOrder;
	}

	public void setEliminationOrder(ArrayList<String> eliminationOrder) {
		this.eliminationOrder = eliminationOrder;
	}

	@Override
	public String toString() {
		return "AlgorithemVariableElimination [network=" + network + ", given=" + given + ", src=" + src + ", factors="
				+ factors + ", query=" + query + ", bayes=" + bayes + ", parentsOfQueryOrEvidence="
				+ parentsOfQueryOrEvidence + ", eliminationOrder=" + eliminationOrder + "]";
	}
	
	
	
}