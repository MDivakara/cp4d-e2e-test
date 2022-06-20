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


import org.apache.wink.json4j.JSONArray;

import de.mrapp.apriori.Transaction;

/**
 * An implementation of the interface [Transaction]. Each transaction
 * corresponds to a single line of a text file.
 *
 * @property line The line the transaction corresponds to
 */
class TransactionImplementation implements Transaction<NamedItem> {

	private JSONArray namedItems = new JSONArray();
	
	public TransactionImplementation(JSONArray namedItems) {
		this.namedItems = namedItems;
	}

	@Override
	public Iterator<NamedItem> iterator() {
		return new LineIterator(namedItems);
	}

	private class LineIterator implements Iterator<NamedItem> {

		JSONArray namedItems = new JSONArray();
		int curPos = 0;
		int length = 0;

		
		public LineIterator(JSONArray namedItems) {
			this.namedItems = namedItems;
			this.length = namedItems.length();
			this.curPos = 0;
		}

		@Override
		public boolean hasNext() {
			if (curPos == length)
				return false;
			else 
				return true;
		}

		@Override
		public NamedItem next() {
			String token = namedItems.optString(curPos);
			curPos++;
			return new NamedItem(token);

		}

	}
}
