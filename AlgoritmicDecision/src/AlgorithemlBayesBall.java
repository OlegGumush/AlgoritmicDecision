
import java.util.ArrayList;
import java.util.Hashtable;

public class AlgorithemlBayesBall implements Algorithem {

	private String src ;
	private String end;
	private BayesianNetwork network;
	private ArrayList<String> given;
	private String ans=Constants.YES ;
	private Hashtable<String, ArrayList<String>> visited = null;
	
	public String resolveQuery(Query q, BayesianNetwork network) {

		if(q == null || network == null){
			return "";
		}
		if(!(q instanceof DependencyQuery)){
			return ""  ;   		
		}
		DependencyQuery query = null;

		query = (DependencyQuery)q;    		

		this.network = network;
		//source Node name
		this.src = query.getSrc();
		//destination Node
		this.end = query.getDest();
		//evidence list "marked nodes"
		this.given = query.getGiven();
		//color list
		visited= new Hashtable<>();
		for (int i = 0; i < network.getNetwork().size(); i++) {
			Var current = network.getNetwork().get(i);
			visited.put(current.getVarName(), new ArrayList<>());
		}
				
		ChilderToNotPainted(network.getVarByName(src));
		return ans;
	}

	public void dfsAdvanced(String fromWhereWeCome , String colored ,Var node )
	{       
		if(end.equals(node.getVarName())){
			ans = Constants.NO;
			return ;
		}
		//visited[network.getIndexByName(node.getVarName())] = Constants.VISITED;

		//came from parent and node is evidence , can only run on parents
		if(fromWhereWeCome.equals(Constants.ABA) && colored.equals(Constants.YES)){

			ParentToPainted(node);
		}
		//came from parent and node is not evidence , can only run on children's
		else if(fromWhereWeCome.equals(Constants.ABA) && colored.equals(Constants.NO)){

			ParentToNotPainted(node);
		}
		//came from children and node is evidence , can't do nothing
		else if(fromWhereWeCome.equals(Constants.BEN) && colored.equals(Constants.YES)){

			//nothing
			ChildrenToPainted(node);
		}
		//came from children and node is not evidence , can run on parent and children's
		else if(fromWhereWeCome.equals(Constants.BEN) && colored.equals(Constants.NO)){
			ChilderToNotPainted(node);
		}

	}

	
	
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public BayesianNetwork getNetwork() {
		return network;
	}

	public void setNetwork(BayesianNetwork network) {
		this.network = network;
	}

	public ArrayList<String> getGiven() {
		return given;
	}

	public void setGiven(ArrayList<String> given) {
		this.given = given;
	}

	public String getAns() {
		return ans;
	}

	public void setAns(String ans) {
		this.ans = ans;
	}

	public Hashtable<String, ArrayList<String>> getVisited() {
		return visited;
	}

	public void setVisited(Hashtable<String, ArrayList<String>> visited) {
		this.visited = visited;
	}

	/**
	 * I'm the son of something unpainted so here we will go on both his fathers and his children
	 * @param node - Who should take a walk on his children and his fathers
	 */
	private void ChilderToNotPainted(Var node) {
		
		//for every children
		for(String c : node.getChildrens())
		{
			//check if already visit this node
			if(visited.get(node.getVarName()).contains(c)){
				continue;
			}
			
			//retrieve children
			Var children = network.getVarByName(c);
			//ad to visited list
			visited.get(node.getVarName()).add(c);
			
			//check if this specific children is evidence
			if(given.contains(children.getVarName())){
				dfsAdvanced(Constants.ABA , Constants.YES , children);				
			}else{
				dfsAdvanced(Constants.ABA , Constants.NO , children);				
			}
		}		
		//for every parent
		for(String p : node.getParents())
		{
			//check if already visit this node
			if(visited.get(node.getVarName()).contains(p)){
				continue;
			}
			
			//retrieve parent
			Var parent = network.getVarByName(p);
			visited.get(node.getVarName()).add(p);

			//check if this specific parent is evidence
			if(given.contains(parent.getVarName())){
				dfsAdvanced(Constants.BEN , Constants.YES , parent);				
			}else{
				dfsAdvanced(Constants.BEN , Constants.NO , parent);				
			}
		}
	}

	/**
	 * Here we do not do anything because the laws do not allow me to do it
	 * @param node 
	 */
	private void ChildrenToPainted(Var node) {
		return ;
	}

	/**
	 * I'm the father of something unpainted so I can only walk on his children
	 * @param node - We walk on his parents
	 */
	private void ParentToNotPainted(Var node) {
		
		for(String c : node.getChildrens())
		{
			//check if already visit this node
			if(visited.get(node.getVarName()).contains(c)){
				continue;
			}
			
			//retrieve children
			Var children = network.getVarByName(c);
			visited.get(node.getVarName()).add(c);

			//check if this specific children is evidence
			if(given.contains(children.getVarName())){
				dfsAdvanced(Constants.ABA , Constants.YES , children);				
			}else{
				dfsAdvanced(Constants.ABA , Constants.NO , children);				
			}
		}	
	}

	/**
	 * We come from Dad and want to work through something painted so we only run on his parents
	 * @param node - We walk on his childrens
	 */
	private void ParentToPainted(Var node) {
		//for every parent
		for(String p : node.getParents())
		{
			//check if already visit this node
			if(visited.get(node.getVarName()).contains(p)){
				continue;
			}
			
			//retrieve parent
			Var parent = network.getVarByName(p);
			visited.get(node.getVarName()).add(p);

			//check if this specific parent is evidence
			if(given.contains(parent.getVarName())){
				dfsAdvanced(Constants.BEN , Constants.YES , parent);				
			}else{
				dfsAdvanced(Constants.BEN , Constants.NO , parent);				
			}
		}		
	}

	@Override
	public String toString() {
		return "AlgorithemlBayesBall [src=" + src + ", end=" + end + ", network=" + network + ", given=" + given
				+ ", ans=" + ans + ", visited=" + visited + "]";
	}
	
	
}
