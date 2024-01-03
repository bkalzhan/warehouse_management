package cs520.termProject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import static javax.swing.JOptionPane.showMessageDialog;

public class Warehouse implements IWarehouse{
	
	//limits for numbers by requirements of task, I determined only here, so in the feature it is easy just change here
	private final int TOTES_NUMBER = 100;
	private final int PRODUCTS_NUMBER_IN_EACH_TOTE = 10;
	
	//From the products.txt file it is obvious that there are only 4 unique products, 
	//so this variable cares just unique products but it does not save any number of products int warehouse, 
	//I will update it in case if in warehouse new product that until that moment current product never been in selling
	//it gives me an opportunity to work just with UPC of product
	//key - UPC of product
	private Map<String, Product> products;
	
	//key - UPC, value - the numbers of totes that saves products of key-UPC
	//I can choose product for order from any tote, we do not need know the number of tote
	//so I chose set
	private Map<String, List<Integer>> productTotes;
	
	//totes which represents number of products on them
	private List<Integer> totes;
	
	private int fullTotesNum;
	private int partialTotesNum;
	
	public Warehouse() {
		this.products = new HashMap<>();
		this.productTotes = new HashMap<>();
		this.totes = new ArrayList<>();
		
		this.fullTotesNum = 0;
		this.partialTotesNum = 0;
	}

	@Override
	public void LoadProducts(String filename) {
		
		System.out.println("Loading products...");
		
		// create fileReader from product file
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(filename);
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
		
		//process each line
		BufferedReader reader = new BufferedReader(fileReader);
		String input;
		try {
			input = reader.readLine();
			while(input != null) {
				processInputProduct(input);
				input = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//close file
		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Loading complete.");
	}
	
	private void processInputProduct(String s) {
		StringTokenizer st = new StringTokenizer(s, ",");
		
		//create Product object
		String upc = st.nextToken();
		String description = null;
		Product product = null;
		
		
		//check do we have product with this upc, if not we will create
		if(!this.products.containsKey(upc)) {
			description = st.nextToken();
			product = new Product(upc, description);	
			this.products.put(upc, product);
		}
		
		//put current product to tote
		//there is no product with this upc in the warehouse
		if(!this.productTotes.containsKey(upc) || this.productTotes.get(upc).size() <= 0) {
			this.productTotes.put(upc, new ArrayList<>());
			addProductToNewTote(upc);
		} 
		//there is product with this upc in the warehouse
		else {
			//get the numbers of totes of the current product
			List<Integer> arr = this.productTotes.get(upc);
			//the number of products in the last tote of the current product
			int toteNumber = arr.get(arr.size() - 1);
			
			//case there is a space to put current product in the last tote
			if(this.totes.get(toteNumber) < this.PRODUCTS_NUMBER_IN_EACH_TOTE)
				this.totes.set(toteNumber, this.totes.get(toteNumber) + 1);
			//case there is no space, we will use new tote
			else addProductToNewTote(upc);
		}
	}
	
	private void addProductToNewTote(String upc) {
		//check for availability the space in the warehouse
		if(this.totes.size() == this.TOTES_NUMBER) {
			showMessageDialog(null, "There is no space in the Warehouse");
			return;
		}
		this.totes.add(1);
		this.productTotes.get(upc).add(this.totes.size() - 1);
		System.out.println(String.format("Used additional tote (%d) for UPC: %s", (this.totes.size() - 1), upc));
	}

	@Override
	public void FulfillOrders(String filename) {
		
		System.out.println("Fulfilling orders...\n");
		
		// create fileReader from product file
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(filename);
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
		
		//process each line
		BufferedReader reader = new BufferedReader(fileReader);
		String input;
		try {
			input = reader.readLine();
			while(input != null) {
				//process each order
				Order order = createInputOrder(input);
				processOrder(order);
				input = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//close file
		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Orders complete.\n");
		
	}
	
	private Order createInputOrder(String s) {
		StringTokenizer st = new StringTokenizer(s, ",");
		
		//create Order object
		int orderNum = Integer.parseInt(st.nextToken());
		Order order = new Order(orderNum);
		
		while(st.hasMoreTokens()) {
			order.addUpc(st.nextToken());
		}
		return order;
	}
	
	private void processOrder(Order order) {
		String res = String.format("Order %d", order.getOrderNum());
		System.out.println("Order fulfillment started: " + res);
		
		//loop through the upcs' in current order
		for(String upc : order.getUpcs()) {
			Random random = new Random();
			//remain the upc part, when we split them by commas, there can be empty spaces
			upc = upc.trim();
			int r = random.nextInt(this.productTotes.get(upc).size());
			int i = this.productTotes.get(upc).get(r);
			this.totes.set(i, this.totes.get(i) - 1);
			//if there is no more products in the current tote, remove the tote number from products' toteList
			if(this.totes.get(i) == 0) {
				this.productTotes.get(upc).remove(r);
			}
			System.out.println(String.format("Retrieving product from tote (%d) for UPC: %s", i, upc));
			res += String.format(", %s", upc);
		}
		System.out.println(String.format("Order fulfilled--> %s\n", res));

	}

	@Override
	public boolean MergeTotes() {
		System.out.println("Merging partially filled totes...");
		for(String key : this.productTotes.keySet()) {
			//get list of totes of current product
			List<Integer> list = this.productTotes.get(key);
			//calculate the total amount of this type of product
			int sum = 0;
			for(int i : list) {
				sum += this.totes.get(i);
			}
	
			int fullTotesNum = sum / this.PRODUCTS_NUMBER_IN_EACH_TOTE;
			int partialToteVal = sum - this.PRODUCTS_NUMBER_IN_EACH_TOTE * fullTotesNum;			
			
			for(int i = 0; i < fullTotesNum; i++) {
				this.totes.set(list.get(i), this.PRODUCTS_NUMBER_IN_EACH_TOTE);
			}
			//check do we have last tote with no full products
			if(partialToteVal != 0) {
				this.totes.set(list.get(fullTotesNum), partialToteVal);
			} else {
				fullTotesNum -= 1;
			}
			//make empty remained totes
			for(int i = list.size() - 1; i > fullTotesNum; i--) {
				this.totes.set(list.get(i), 0);
			}
			
			//remove empty totes from the list of current product
			while(list.size() > fullTotesNum + 1) {
				list.remove(list.size() - 1);
			}
			this.productTotes.put(key, list);
		}
		System.out.println("Merge complete.\n");
		return false;
	}

	@Override
	public void DisplayDetails() {
		System.out.println("Warehouse details:\n");
		//check totally full and partially full totes
		for(Integer i : this.totes) {
			if(i == this.PRODUCTS_NUMBER_IN_EACH_TOTE) this.fullTotesNum++;
			else if(i < this.PRODUCTS_NUMBER_IN_EACH_TOTE && i > 0) this.partialTotesNum++;
		}
		
		System.out.println(String.format("	%d full totes, %d partially filled totes, and %d empty totes\n", 
				this.fullTotesNum, this.partialTotesNum, this.TOTES_NUMBER - (this.fullTotesNum + this.partialTotesNum)));
		
		System.out.println("	UPC             # Totes");
		System.out.println("	--------------- -------");
		for(String key : this.productTotes.keySet()) {
			System.out.println("	" + key + "    " + this.productTotes.get(key).size());
		}
		System.out.println();
		
		//assign them value 0 to prepare for the next call of the current method
		this.fullTotesNum = 0;
		this.partialTotesNum = 0;		
	}

}
