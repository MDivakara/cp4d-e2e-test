/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shaik.Nawaz
 *
 */
public class StorageUtils {

    private static final ZoneId zoneId = ZoneId.systemDefault();
    private static ZoneOffset zoneOffset = zoneId.getRules().getOffset(Instant.now());

    /**
     * Generate a random UUID string
     * 
     * @return a random UUID string
     */
    public static String getNewUUID() {
        return UUID.randomUUID().toString();
    }

    public static Date getStartOfToday() {
        return Date.from(LocalDate.now(zoneId).atStartOfDay().toInstant(zoneOffset));
    }

    public static Date getStartOfTomorrow() {
        return Date.from(LocalDate.now(zoneId).plusDays(1).atStartOfDay().toInstant(zoneOffset));
    }

    /**
     * Get Date from a UTC Date string in format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
     * 
     * @param dateString
     * @return Date corresponding to the UTC date String. returns null if dateString is null
     * @throws StorageException
     */
    public static Date getDateFromUTCString(String dateString) throws StorageException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (dateString != null) {
            try {
                return df.parse(dateString);
            } catch (ParseException e) {
                throw new StorageException("Error while getDateFromString", e);
            }
        }
        return null;
    }

    /**
     * Get Timestamp from a UTC Date string in format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
     * 
     * @param dateString
     * @return
     * @throws StorageException
     */
    public static Timestamp getTimeStampFromUTCString(String dateString) throws StorageException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (dateString != null) {
            try {
                return new Timestamp(df.parse(dateString).getTime());
            } catch (ParseException e) {
                throw new StorageException("Error while getDateFromString", e);
            }
        }
        return null;
    }

    public static Timestamp getTimeStampFromString(String dateString) throws StorageException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        if (dateString != null) {
            try {
                return new Timestamp(df.parse(dateString).getTime());
            } catch (ParseException e) {
                throw new StorageException("Error while getDateFromString", e);
            }
        }
        return null;
    }

    /**
     * Get UTC Date string in format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" from a Timestamp
     * 
     * @param timeStamp
     * @return
     * @throws StorageException
     */
    public static String getUTCStringFromTimeStamp(Timestamp timeStamp) throws StorageException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (timeStamp != null) {
            return df.format(new Date(timeStamp.getTime()));
        }
        return null;
    }

    /**
     * Get UTC Date string in format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" from a Date
     * 
     * @param date
     * @return
     * @throws StorageException
     */
    public static String getUTCStringFromDate(Date date) throws StorageException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (date != null) {
            return df.format(date);
        }
        return null;
    }

    private final static String YYYY = "([0-9]{4})";
    private final static String MM = "(0[1-9]|1[0-2])";
    private final static String DD = "(0[1-9]|[1-2][0-9]|3[0-1])";
    private final static String HH = "(0[0-9]|1[0-9]|2[0-3])";
    private final static String mm = "([0-5][0-9])";
    private final static String ss = "([0-5][0-9])";
    private final static String SSS = "(\\.[0-9]{1,10})*"; // optional .SSS yyyy-mm-dd hh:mm:ss.fff ffffff

    private final static String dtSeparater = "[ -/]";
    private final static String timeSeparater = ":";
    private final static String timeMarker = "[ T]";

    private final static String dtStr = YYYY + dtSeparater + MM + dtSeparater + DD;
    private final static String timeStr = HH + timeSeparater + mm + timeSeparater + ss + SSS;
    private final static String dtTimeStr = dtStr + "(" + timeMarker + timeStr + ")*([Z])*";
    private final static Pattern dtPatern = Pattern.compile(dtTimeStr);

    /**
     * Get/Infer Timestamp from a Date string
     * It uses InferredTypeEngine to compute the Timestamp using formats defined in DateTimeFormats.json
     * 
     * @param dateTimeStr
     * @return
     */

    public static Timestamp inferTimeStampFromString(String dateTimeStr) throws StorageException {
        if (dateTimeStr == null) {
            return null;
        }
        Matcher matcher = dtPatern.matcher(dateTimeStr);
        boolean matches = matcher.matches();
        if (!matches) {
            throw new StorageException("Date/Time Input String does not match expected format - " + dateTimeStr);
        }
        String dtNewStr = matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3); // yyyy-mm-dd
        if (matcher.group(4) != null) {
            dtNewStr += "T" + matcher.group(5) + ":" + matcher.group(6) + ":" + matcher.group(7);
            String milisec = (matcher.group(8) != null) ? matcher.group(8) : ".000";
            if (milisec.length() > 4) { // ".098456789"
                milisec = milisec.substring(0, 4);
            }
            while (milisec.length() < 4) {
                milisec += "0";
            }
            dtNewStr += milisec;
            if (matcher.group(9) != null) {
                // "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                dtNewStr += matcher.group(9);
                return getTimeStampFromUTCString(dtNewStr);
            } else {
                // "yyyy-MM-dd'T'HH:mm:ss.SSS"
                return getTimeStampFromString(dtNewStr);
            }
        } else {
            // "yyyy-MM-dd'T'HH:mm:ss.SSS"
            dtNewStr += "T00:00:00.000";
            if (matcher.group(9) != null) {
                dtNewStr += matcher.group(9);
                return getTimeStampFromUTCString(dtNewStr);
            } else {
                return getTimeStampFromString(dtNewStr);
            }
        }
    }

    public static String[] mergeLists(List<String> list1, List<String> list2, List<String> list3, List<String> list4,
        String... str) {
        List<String> mergedList = new ArrayList<String>();
        if (list1 != null) {
            mergedList.addAll(list1);
        }
        if (list2 != null) {
            mergedList.addAll(list2);
        }
        if (list3 != null) {
            mergedList.addAll(list3);
        }
        if (list4 != null) {
            mergedList.addAll(list4);
        }
        if (str != null && str.length > 0) {
            mergedList.addAll(Arrays.asList(str));
        }
        return mergedList.toArray(new String[mergedList.size()]);
    }

    public static Object[] mergeLists(List<Object> list1, List<Object> list2, List<Object> list3, List<Object> list4,
        Object... str) {
        List<Object> mergedList = new ArrayList<Object>();
        if (list1 != null) {
            mergedList.addAll(list1);
        }
        if (list2 != null) {
            mergedList.addAll(list2);
        }
        if (list3 != null) {
            mergedList.addAll(list3);
        }
        if (list4 != null) {
            mergedList.addAll(list4);
        }
        if (str != null && str.length > 0) {
            mergedList.addAll(Arrays.asList(str));
        }
        return mergedList.toArray(new Object[mergedList.size()]);
    }

}
