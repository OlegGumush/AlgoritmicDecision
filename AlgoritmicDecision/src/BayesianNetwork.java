

import java.util.ArrayList;

public class BayesianNetwork {

	public ArrayList<Var> network = new ArrayList<>();
	
	public BayesianNetwork(ArrayList<Var> nodes){
		
		network.addAll(nodes);
		
	}

	public ArrayList<Var> getNetwork() {
		return network;
	}

	public void setNetwork(ArrayList<Var> network) {
		this.network = network;
	}
	
	public Var getVarByName(String name) {
		for (int i = 0; i < network.size(); i++) {
			if (network.get(i).getVarName().equals(name)){
				return network.get(i);
			}
		}
		return null;
	}
	
	public Integer getIndexByName(String name) {
		for (int i = 0; i < network.size(); i++) {
			if (network.get(i).getVarName().equals(name)){
				return i;
			}
		}
		return null;
	}
	
	public Var getVarByIndex(int i){
		return network.get(i);
	}

	@Override
	public String toString() {
		return "BayesianNetwork [network=" + network + "]";
	}
	
	
}
