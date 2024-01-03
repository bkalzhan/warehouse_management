package cs520.termProject;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IWarehouse w = new Warehouse();
		w.LoadProducts("Products.txt");
		w.DisplayDetails();
		
		w.FulfillOrders("Orders.txt");
		w.DisplayDetails();
		
		w.MergeTotes();
		w.DisplayDetails();
	}

}
