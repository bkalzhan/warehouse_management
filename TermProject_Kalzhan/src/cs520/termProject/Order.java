package cs520.termProject;

import java.util.ArrayList;
import java.util.List;

public class Order {
	private int orderNum;
	private List<String> upcs;
	
	public Order(int orderNum) {
		this.orderNum = orderNum;
		this.upcs = new ArrayList<>();
	}
	
	public int getOrderNum() {
		return this.orderNum;
	}
	
	public List<String> getUpcs(){
		return this.upcs;
	}
	
	public void addUpc(String upc) {
		this.upcs.add(upc);
	}

}
