

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ex1 {

	public static void main(String[] args) throws IOException {
		
		InputFileParser p = new InputFileParser("input.txt");
		BayesianNetwork network = new BayesianNetwork(p.getVariablesList());
 		ArrayList<Query> q = p.getQueryList();
		
 		PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
 		
		for (int i = 0; i < q.size(); i++) {
			
			AlgorithemlBayesBall a1 = new AlgorithemlBayesBall();
			AlgorithemVariableElimination a2 = new AlgorithemVariableElimination();
			
			if(q.get(i) instanceof DependencyQuery){
				writer.println(a1.resolveQuery(q.get(i), network));		
				writer.flush();
			}else if(q.get(i) instanceof ProbabilityQuery){
				Double value = Double.parseDouble(a2.resolveQuery(q.get(i), network));
				DecimalFormat df = new DecimalFormat("#.#####");
				String dx=df.format(value);
				writer.print(dx);
				writer.print(",");
				writer.print(a2.getNumberOfSums());
				writer.print(",");
				writer.println(a2.getNumberOfMults());
				writer.flush();
			}
		}
		writer.close();
	}
}








