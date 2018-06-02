

/**
 * Pair class hold key and value
 * @author Oleg
 *
 */
public class Pair {

	private String name ;
	private String value;
	
	/**
	 * @param name 
	 * @param value
	 */
	public Pair(String name, String value) {
		this.name = name ;
		this.value = value;
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return name+"="+value;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
