/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.modelmanagement;

import java.sql.Timestamp;

public class Period {
	
	private Timestamp asOf;
	
	public Timestamp getAsOf() {
		return asOf;
	}

	public void setAsOf(Timestamp asOf) {
		this.asOf = asOf;
	}

	class StartEndRange{
		private Timestamp startDateTime;
		private Timestamp endDateTime;
		public Timestamp getStartDateTime() {
			return startDateTime;
		}
		public void setStartDateTime(Timestamp startDateTime) {
			this.startDateTime = startDateTime;
		}
		public Timestamp getEndDateTime() {
			return endDateTime;
		}
		public void setEndDateTime(Timestamp endDateTime) {
			this.endDateTime = endDateTime;
		}
	}

}
