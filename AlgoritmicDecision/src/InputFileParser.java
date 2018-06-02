

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Parse input file . Lazy evaluation of objects , only on demand.
 * @author Oleg
 *
 */
public class InputFileParser {

	private BufferedReader reader;
	private String fileName ;
	
	public InputFileParser(String fileName){

		this.fileName = fileName;
	}

	/**
	 * Parse an input file and returns list of queries
	 * @return - list of queries.
	 * @throws IOException
	 */
	public ArrayList<Query> getQueryList() throws IOException{

		ArrayList<Query> querys = new ArrayList<>();
		reader = new BufferedReader(new FileReader(fileName));

		while(true){
			String line = reader.readLine();
			
			if (line == Constants.EOF){
				break;
			}
			//starting to make query list
			if(line.contains("Queries")){
				line = reader.readLine();

				while(line != null && !line.isEmpty()){

					//need to create probability query
					if(line.startsWith("P")){
						querys.add(createProbabilityQuery(line));
					}
					//need to create dependency query
					else{
						querys.add(createDependencyQuery(line));
					}
					line = reader.readLine();
				}
			}
		}

		return querys;
	}

	/**
	 * Create specific type of query , where we are required to calculate probability.
	 * @param line - String represented query
	 * @return - Query Object
	 */
	public Query createProbabilityQuery(String line) {

		//example : P(B=true|J=true,M=true),A-E
		//[P(B=true|J=true,M=true , A-E]
		String splitedLine[] = line.split("\\),");
		
		//after substring first part seem like this : B=true|J=true,M=true
		String firstPart = splitedLine[0].substring(2);	
		String splitedFirstPart[] = firstPart.split("\\|");
		String begin = splitedFirstPart[0]; //"B=true"
		Pair src = new Pair(begin.split(Constants.EQUAL)[0], begin.split(Constants.EQUAL)[1]); //[B,true]

		ArrayList<Pair> list = new ArrayList<>();

		//exists evidence variables "|J=true,G=false"
		if(splitedFirstPart.length > 1){
			//split variables from each other
			String givenVars[] = splitedFirstPart[1].split(Constants.COMMA);

			//create pair from each variable and his value
			for (int i = 0; i < givenVars.length; i++) {
				String pair[] = givenVars[i].split(Constants.EQUAL);
				Pair p1 = new Pair(pair[0] , pair[1]);
				list.add(p1);
			}
		}

		//Read second part of query , that represented elimination order.
		ArrayList<String> orderEliminationList = new ArrayList<>();
		if(splitedLine.length > 1){
			String secondPart = splitedLine[1];
			String splitedSecondPart[] = secondPart.split(Constants.HYPHEN);
			for (int i = 0; i < splitedSecondPart.length; i++) {
				orderEliminationList.add(splitedSecondPart[i]);
			}			
		}

		
		return new ProbabilityQuery(src , list , orderEliminationList );
	}

	/**
	 * Create specific type of query , where we are required to check if two variables are independent
	 * @param line - - String represented query
	 * @return - Query object
	 */
	private Query createDependencyQuery(String line) {

		String splitedLine[] = line.split("\\|");
		//first part of query "B-E|"
		String src = splitedLine[0].split(Constants.HYPHEN)[0];
		String dst= splitedLine[0].split(Constants.HYPHEN)[1];

		//exists evidence variables "|J=true,G=false"
		if(splitedLine.length > 1){
			//split variables from each other
			String givenVars[] = splitedLine[1].split(Constants.COMMA);
			ArrayList<Pair> list = new ArrayList<>();

			//create pair from each variable and his value
			for (int i = 0; i < givenVars.length; i++) {
				String pair[] = givenVars[i].split(Constants.EQUAL);
				Pair p = new Pair(pair[0] , pair[1]);
				list.add(p);
			}
			//create query with given variables
			return new DependencyQuery(src,dst,list);	
		}
		//create query without given variables
		return new DependencyQuery(src,dst, new ArrayList<>());	 
	}

	/**
	 * Reads from a file all the constructs and returns a list
	 * @return - List of variables
	 * @throws IOException - file reader exception
	 */
	public ArrayList<Var> getVariablesList() throws IOException{

		reader = new BufferedReader(new FileReader(fileName));
		ArrayList<Var> result = new ArrayList<>();

		while(true){
			String line = reader.readLine();
			if (line == Constants.EOF ){
				break;
			}
			//Start of variable in the file
			if(line.contains("Var ")){			
				String varName = line.split("\\s+")[1];
				ArrayList<String> values = parseValues(reader.readLine());
				ArrayList<String> parents= parseParents(reader.readLine());
				ArrayList<ArrayList<String>> cpt = parseCpt();
				result.add(new Var(varName, values ,parents, cpt));
			}
		}
		
		for (int i = 0; i < result.size(); i++) {
			ArrayList<String> childrens = new ArrayList<>();
			Var current = result.get(i);
			
			String varName = current.getVarName();
			
			for (int j = 0; j < result.size(); j++) {
				if(result.get(j).getParents().contains(varName)){
					childrens.add(result.get(j).getVarName());
				}
			}
			
			current.setChildrens(childrens);
		}
		
		return result;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private ArrayList<ArrayList<String>> parseCpt() throws IOException {
		ArrayList<ArrayList<String>> cptLinesResult = new ArrayList<>();

		//read "CPT:"
		String cptLine = reader.readLine();

		//end of paragraph
		while(!cptLine.isEmpty()){

			cptLine = reader.readLine();
			//last line in paragraph

			if(!cptLine.isEmpty()){
				String [] splitedLine = cptLine.split(Constants.COMMA);
				ArrayList<String> line = new ArrayList<>();
				for (int i = 0; i < splitedLine.length; i++) {
					line.add(splitedLine[i]);
				}		
				cptLinesResult.add(line);
			}
		}		
		return cptLinesResult;
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	private ArrayList<String> parseParents(String s) {

		if (s.contains(Constants.NONE)){
			return new ArrayList<>();
		}
		ArrayList<String> list = new ArrayList<>();
		String[] arr= s.split(":\\s+|,");
		for (int i = 1; i < arr.length; i++) {
			list.add(arr[i]);
		}
		return list;
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	private ArrayList<String> parseValues(String s) {
		ArrayList<String> list = new ArrayList<>();
		String[] arr= s.split(":\\s+|,\\s+");
		for (int i = 1; i < arr.length; i++) {
			list.add(arr[i]);
		}
		return list;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "InputFileParser [reader=" + reader + ", fileName=" + fileName + "]";
	}
}
