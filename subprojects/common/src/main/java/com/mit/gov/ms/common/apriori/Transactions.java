/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.apriori;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.mrapp.apriori.Transaction;

public class Transactions implements Transaction{
	
	public List<Transaction> trnsList = new ArrayList<Transaction>();
	
	public void appendTransaction(Transaction trns) {
		this.trnsList.add(trns);
	}

	@Override
	public Iterator iterator() {
		return trnsList.iterator();
	}

}
