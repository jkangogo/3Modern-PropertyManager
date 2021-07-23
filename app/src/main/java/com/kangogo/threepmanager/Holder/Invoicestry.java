package com.kangogo.threepmanager.Holder;
	import java.math.BigDecimal;
	import java.util.Date;
	public class Invoicestry {
	    public InvoiceDataData[] getInvoices() {
	        InvoiceDataData[] data = new InvoiceDataData[20];
	        for(int i = 0; i < 20; i ++) {
	            InvoiceDataData row = new InvoiceDataData();
	            row.id = (i+1);
	            row.invoiceNumber = row.id;
	            row.amountDue = BigDecimal.valueOf(20.00 * i);
	            row.invoiceAmount = BigDecimal.valueOf(120.00 * (i+1));
	            row.invoiceDate = new Date();
	            row.customerName =  "Thomas John Beckett";
	            row.customerAddress = "1112, Hash Avenue, NYC";
	            data[i] = row;
	        }
	        return data; 
	    }
	}