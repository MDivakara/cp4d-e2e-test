/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.apriori;

import java.util.Iterator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;


import de.mrapp.apriori.Transaction;

public class DataIterator implements Iterator<Transaction<NamedItem>>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataIterator.class);
	
	private int currPos = 0;
	private int length = 0;
	JSONArray transactions = new JSONArray();
	
	public DataIterator(JSONArray transactions){
		currPos = 0;
		length = transactions.length();
		this.transactions = transactions;
	}
	
	@Override
	public boolean hasNext() {
		if (currPos == length)
			return false;
		else
			return true;
	}

	@Override
	public Transaction<NamedItem> next() {
		if (hasNext()) {
			TransactionImplementation transaction;
			try {
				transaction = new TransactionImplementation(transactions.getJSONArray(currPos));
				currPos++;
				return transaction;	
			} catch (JSONException e) {
				LOGGER.error("exception in getting current prosition of the transactions JSONArray.", e);
			}
        }

		return null;
	}		

}
