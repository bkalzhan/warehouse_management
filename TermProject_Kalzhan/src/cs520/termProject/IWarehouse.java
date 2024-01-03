package cs520.termProject;

public interface IWarehouse {
	// method to load products from the file name provided 
	// into your warehouse data structure
	public void LoadProducts (String filename);
	// method to fullfill an oder from the warehouse 
	// (display information to console)
	public void FulfillOrders(String filename);
	// method to merge partially filled totes with 
	// intent to reduce number of used totes
	public boolean MergeTotes();
	// display warehouse information to the console
	public void DisplayDetails();
}
