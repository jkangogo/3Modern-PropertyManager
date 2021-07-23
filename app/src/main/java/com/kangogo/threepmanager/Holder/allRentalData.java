package com.kangogo.threepmanager.Holder;
	import java.math.BigDecimal;
	import java.util.Date;

	public class allRentalData {
		// Property
			public int count;
			public String owner_id;
			public String p_name;
			public String p_desc;		
	        
			public int id;
	        public int invoiceNumber;
	        public Date invoiceDate;
	        public String customerName;
	        public String customerAddress;
	        public BigDecimal invoiceAmount; 
	        public BigDecimal amountDue;
			
	public allRentalData(String owner_id, String p_name,String p_desc) {
				
				this.owner_id = owner_id;
				this.p_name = p_name;
				this.p_desc = p_desc;
	}
	public allRentalData() {				
				//this.owner_id = owner_id;
				//this.p_name = p_name;
				//this.p_desc = p_desc;
	}
public String getOwnerid() {
        return owner_id;
    }
 public String getProperydesc() {
        return p_desc;
    }   
    public String getPropertyname() {
        return p_name;
    }
	public void setOwnerid(String owner_id ) {
       this.owner_id = owner_id;
    }
 public void setProperydesc(String p_desc) {
      
		this.p_desc = p_desc;
    }   
    public void setPropertyname(String p_name ) {
       
		this.p_name = p_name;
    }
	
}