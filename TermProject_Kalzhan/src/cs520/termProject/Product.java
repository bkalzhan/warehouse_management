package cs520.termProject;

public class Product {
	private String upc;
	private String description;
	
	public Product(String upc, String description) {
		this.upc = upc;
		this.description = description;
	}
	
	public String getUpc() {
		return this.upc;
	}
	
	public void setUpc(String upc) {
		this.upc = upc;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
