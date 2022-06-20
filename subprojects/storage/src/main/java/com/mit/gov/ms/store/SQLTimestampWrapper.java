/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Shaik.Nawaz
 *
 */
public class SQLTimestampWrapper implements Serializable {

    private static final long serialVersionUID = 5540410410172388510L;
    private Timestamp timeStamp = null;
    private boolean isCurrentTS = false;
    
    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    
    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    
    public boolean isCurrentTS() {
        return isCurrentTS;
    }

    
    public void setCurrentTS(boolean isCurrentTS) {
        this.isCurrentTS = isCurrentTS;
    }

    public SQLTimestampWrapper() {
        
    }
    
    public SQLTimestampWrapper(String datetime) throws StorageException {
        if (datetime.equalsIgnoreCase(StorageConstants.CURRENT_TIMESTAMP)) {
            isCurrentTS = true;
        } else {
            timeStamp = StorageUtils.inferTimeStampFromString(datetime);
        }
    }

    public SQLTimestampWrapper(Timestamp datetime) {
        timeStamp = datetime;
        isCurrentTS = false;
    }

    public SQLTimestampWrapper(Date date) {
        timeStamp = new Timestamp(date.getTime());
        isCurrentTS = false;
    }

    public SQLTimestampWrapper(long timemilisec) {
        timeStamp = new Timestamp(timemilisec);
        isCurrentTS = false;
    }

    public boolean isCurrentTimeStamp() {
        return this.isCurrentTS;
    }

    public Timestamp getTimestamp() {
        return this.timeStamp;
    }

    public Object getValue() {
        if (this.isCurrentTS) {
            return StorageConstants.CURRENT_TIMESTAMP;
        } else {
            return this.timeStamp;
        }
    }

    public boolean isNull() {
        return ((!isCurrentTS) && timeStamp == null);
    }

    public static SQLTimestampWrapper valueOf(Timestamp datetime) {
        if (datetime != null) {
            return new SQLTimestampWrapper(datetime);
        }
        return null;
    }

    public static SQLTimestampWrapper valueOf(Date date) {
        if (date != null) {
            return new SQLTimestampWrapper(date);
        }
        return null;
    }

    public static SQLTimestampWrapper valueOf(Object ts) throws StorageException {
        if (ts != null) {
            if (ts instanceof String) {
                return new SQLTimestampWrapper((String) ts);
            } else if (ts instanceof Date) {
                return new SQLTimestampWrapper((Date) ts);
            } else if (ts instanceof Timestamp) {
                return new SQLTimestampWrapper((Timestamp) ts);
            } else if (ts instanceof SQLTimestampWrapper) {
                return (SQLTimestampWrapper) ts;
            }
        }
        return null;
    }

    public static SQLTimestampWrapper valueOf(String str) throws StorageException {
        if (str != null) {
            return new SQLTimestampWrapper(str);
        }
        return null;
    }

    public static SQLTimestampWrapper fromString(String str) throws StorageException {
        return valueOf(str);
    }

    @Override
    public String toString() {
        if (this.isCurrentTS) {
            return StorageConstants.CURRENT_TIMESTAMP;
        } else if (timeStamp != null) {
            return timeStamp.toString();
        }
        return null;
    }

    public boolean after(Date when) throws StorageException {
        if (when == null || isNull()) {
            throw new StorageException("Cant compare null timestamps");
        }
        if (this.isCurrentTS) {
            return !when.before(StorageUtils.getStartOfTomorrow());
        }
        return timeStamp.after(when);
    }

    public boolean after(Timestamp when) throws StorageException {
        if (when == null || isNull()) {
            throw new StorageException("Cant compare null timestamps");
        }
        if (this.isCurrentTS) {
            return !when.before(StorageUtils.getStartOfTomorrow());
        }
        return timeStamp.after(when);
    }

    public boolean after(SQLTimestampWrapper when) throws StorageException {
        if (when == null || when.isNull() || isNull()) {
            throw new StorageException("Cant compare null timestamps");
        }
        if (this.isCurrentTS) {
            if (when.isCurrentTS) {
                return false;
            }
            return after(when.getTimestamp());
        }
        return timeStamp.after(when.getTimestamp());
    }

    public boolean before(Date when) throws StorageException {
        if (when == null || isNull()) {
            throw new StorageException("Cant compare null timestamps");
        }
        if (this.isCurrentTS) {
            return !when.after(StorageUtils.getStartOfToday());
        }
        return timeStamp.before(when);
    }

    public boolean before(Timestamp when) throws StorageException {
        if (when == null || isNull()) {
            throw new StorageException("Cant compare null timestamps");
        }
        if (this.isCurrentTS) {
            return !when.after(StorageUtils.getStartOfToday());
        }
        return timeStamp.before(when);
    }

    public boolean before(SQLTimestampWrapper when) throws StorageException {
        if (when == null || when.isNull() || isNull()) {
            throw new StorageException("Cant compare null timestamps");
        }
        if (this.isCurrentTS) {
            if (when.isCurrentTS) {
                return false;
            }
            return before(when.getTimestamp());
        }
        return timeStamp.before(when.getTimestamp());
    }

    public int compareTo(Date when) throws StorageException {
        if (when == null || isNull()) {
            throw new StorageException("Cant compare null timestamps");
        }
        if (after(when)) {
            return 1;
        } else if (before(when)) {
            return -1;
        } else {
            return 0;
        }
        // return timeStamp.compareTo(when);
    }

    public int compareTo(Timestamp when) throws StorageException {
        if (when == null || isNull()) {
            throw new StorageException("Cant compare null timestamps");
        }
        if (after(when)) {
            return 1;
        } else if (before(when)) {
            return -1;
        } else {
            return 0;
        }
        // return timeStamp.compareTo(when);
    }

    public int compareTo(SQLTimestampWrapper when) throws StorageException {
        if (when == null || when.isNull() || isNull()) {
            throw new StorageException("Cant compare null timestamps");
        }
        if (after(when)) {
            return 1;
        } else if (before(when)) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Timestamp && this.timeStamp != null) {
            return this.timeStamp.equals((Timestamp) obj);
        } else if (obj instanceof Date && this.timeStamp != null) {
            return this.timeStamp.equals(new Timestamp(((Date) obj).getTime()));
        } else if (obj instanceof SQLTimestampWrapper && this.timeStamp != null
            && ((SQLTimestampWrapper) obj).getTimestamp() != null) {
            return this.timeStamp.equals(((SQLTimestampWrapper) obj).getTimestamp());
        }
        // TODO Auto-generated method stub
        return super.equals(obj);
    }

    public boolean isFuture() throws StorageException {
        if (isCurrentTS) {
            return false;
        }
        if (timeStamp == null) {
            throw new StorageException("Cant compare null timestamps");
        }
        return !timeStamp.before(StorageUtils.getStartOfTomorrow());
    }

    public boolean isPast() throws StorageException {
        if (isCurrentTS) {
            return false;
        }
        if (timeStamp == null) {
            throw new StorageException("Cant compare null timestamps");
        }
        return timeStamp.before(StorageUtils.getStartOfToday());
    }
}
