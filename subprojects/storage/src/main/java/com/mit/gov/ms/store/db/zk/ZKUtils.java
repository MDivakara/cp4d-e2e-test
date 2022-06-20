/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.db.zk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.store.StorageException;

import org.slf4j.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.wink.json4j.JSON;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONArtifact;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.SessionExpiredException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZKUtil;
import org.apache.zookeeper.ZooDefs;

/**
 * @author Shaik.Nawaz
 *
 */
public class ZKUtils implements Watcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKUtils.class);
    private String zooKeeperHost = null;
    private ZooKeeper zookeeper = null;
    private String rootNodeName = null;

    public ZKUtils(String zooKeeperHost, String rootNodeName) throws StorageException {
        this.zooKeeperHost = zooKeeperHost;
        this.rootNodeName = rootNodeName.startsWith("/") ? rootNodeName : "/" + rootNodeName;
        checkIfNodeExists(this.rootNodeName);
    }

    public String getRootNodeName() {
        return this.zooKeeperHost;
    }

    /**
     * Checks if a particular ZooKeeper node exists and if not, create it
     * 
     * @param path
     * The path of the node to create
     */
    protected void checkIfNodeExists(String path) throws StorageException {
        try {
            ZooKeeper zk = getZooKeeper(true);
            if (zk.exists(path, false) == null) {
                zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            throw new StorageException(
                "Error while checking for the existence of the ZooKeeper node " + path + ": " + e.toString(), e);
        }
    }

    public ZooKeeper getZooKeeper(boolean testIfExpired) throws StorageException {
        if (this.zookeeper == null) {
            try {
                // String zooKeeperHost = InsightsConfiguration.getInstance().getZKUrl();
                CountDownLatch connectedSignal = new CountDownLatch(1);
                this.zookeeper = new ZooKeeper(zooKeeperHost, 2000, new Watcher() {

                    @Override
                    public void process(WatchedEvent event) {
                        if (event.getState() == KeeperState.SyncConnected) {
                            connectedSignal.countDown();
                        }
                    }
                });
                connectedSignal.await();
            } catch (IOException | InterruptedException e) {
                throw new StorageException("Cannot initialize ZooKeeper connection: " + e.getMessage(), e);
            }
        }
        if (testIfExpired) {
            try {
                this.zookeeper.exists(this.rootNodeName, false);
            } catch (SessionExpiredException e) {
                LOGGER.info("Zookeeper session expired: rebuilding a session...");
                try {
                    String zooKeeperHost = this.zooKeeperHost;
                    CountDownLatch connectedSignal = new CountDownLatch(1);
                    this.zookeeper = new ZooKeeper(zooKeeperHost, 2000, new Watcher() {

                        @Override
                        public void process(WatchedEvent event) {
                            if (event.getState() == KeeperState.SyncConnected) {
                                connectedSignal.countDown();
                            }
                        }
                    });
                    connectedSignal.await();
                } catch (IOException | InterruptedException e2) {
                    throw new StorageException("Cannot initialize ZooKeeper connection: " + e2.getMessage(), e2);
                }
                try {
                    this.zookeeper.exists(this.rootNodeName, false);
                } catch (Exception e3) {
                    throw new StorageException("Cannot connect to ZooKeeper: " + e3.getMessage(), e3);
                }
            } catch (Exception e) {
                throw new StorageException("Cannot connect to ZooKeeper: " + e.getMessage(), e);
            }
        }
        return this.zookeeper;
    }

    /**
     * Converts a JSON object to a byte array to be stored in ZooKeeper. If the
     * JSON string is <1MB, the JSON is stored in plain JSON. If the JSON string
     * is >1Mb, it is compressed using the gzip algorithm
     * 
     * @param json
     * @return
     */
    static byte[] jsonToBytes(JSONArtifact json) throws IOException {
        byte[] data;
        try {
            data = json.write(false).getBytes();
        } catch (JSONException e) {
            throw new IOException(e);
        }

        // if the data is too long, gzip it
        if (data.length > 1000000) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(out);
            gzipOut.write(data, 0, data.length);
            gzipOut.close();
            byte[] compressedData = out.toByteArray();
            out.close();
            return compressedData;
        } else {
            return data;
        }
    }

    /**
     * Converts an array of bytes as generated by the method jsonToBytes to the
     * unserialized JSONObject This method will check if the bytes are
     * compressed or not and will deserialize the JSON accordingly
     * 
     * @param data
     * The bytes to deserialize
     * @return The deserialized JSON object
     * @throws IOException
     */
    static JSONArtifact bytesToJSON(byte[] data) throws IOException {
        InputStream in = null;
        try {
            in = new ByteArrayInputStream(data);
            int header = ((int) data[0] & 0xff) | ((data[1] << 8) & 0xff00);
            // if the data are gzipped, unzip first
            if (GZIPInputStream.GZIP_MAGIC == header) {
                in = new GZIPInputStream(in);
            }
            JSONArtifact json = JSON.parse(in);
            return json;
        } catch (JSONException e) {
            throw new IOException(e);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * @param nodetype
     * @param subtype
     * @param result
     * @throws StorageException
     */
    public void save(String nodetype, String subtype, JSONArtifact result) throws StorageException {
        if (nodetype == null) {
            return;
        }
        String path = this.rootNodeName + "/" + nodetype;
        try {
            checkIfNodeExists(path);
            path += "/" + (subtype != null ? subtype : "all");
            checkIfNodeExists(path);
            if (result == null) {
                return;
            }
            if (result instanceof JSONObject && ((JSONObject) result).isEmpty()) {
                return;
            } else if (result instanceof JSONArray && ((JSONArray) result).isEmpty()) {
                return;
            }
            // LOGGER.debug("Saving to path - " + path + " \n" + result.toString());
            byte[] data = jsonToBytes(result);
            Stat stat = getZooKeeper(true).exists(path, true);
            getZooKeeper(true).setData(path, data, stat.getVersion());
        } catch (IOException | KeeperException | InterruptedException e) {
            LOGGER.error("Cannot save results to path " + path, e);
            throw new StorageException("Cannot save results to path " + path, e);
        }
    }

    /**
     * @param nodetype
     * @param subtype
     * @return JSONArtifact if both subtype is not null or else return JSONArray of subtypes
     * @throws StorageException
     */
    public JSONArtifact get(String nodetype, String subtype) throws StorageException {
        String path = this.rootNodeName + "/" + nodetype;
        try {
            if (nodetype != null) {
                if (subtype != null) {
                    path += "/" + subtype;
                    Stat stat = getZooKeeper(true).exists(path, true);
                    if (stat != null) {
                        byte[] data = getZooKeeper(true).getData(path, true, null);
                        if (data != null && data.length > 0) {
                            return bytesToJSON(data);
                        }
                    }
                    return new JSONObject();
                } else {
                    // get array of all subtype/subnode
                    // LOGGER.debug("Checking if path exist - " + path);
                    Stat stat = getZooKeeper(true).exists(path, true);
                    if (stat != null) {
                        // LOGGER.debug("Get subtypes for " + path);
                        List<String> subtypes = getZooKeeper(true).getChildren(path, this);
                        return new JSONArray(subtypes);
                    }
                    return new JSONArray();
                }
            }
        } catch (JSONException | KeeperException | InterruptedException | IOException e) {
            LOGGER.error("Cannot get results from path " + path, e);
            throw new StorageException("Cannot get results to path " + path, e);
        }
        if (subtype != null) {
            return new JSONObject();
        }
        return new JSONArray();
    }

    public void delete(String nodetype, String subtype) throws StorageException {
        if (nodetype == null) {
            return;
        }
        String path = this.rootNodeName + "/" + nodetype;
        if (subtype != null) {
            path += "/" + subtype;
        }
        try {
            Stat stat = getZooKeeper(true).exists(path, true);
            if (stat != null) {
                ZKUtil.deleteRecursive(getZooKeeper(true), path);
            }
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("Cannot delete path " + path, e);
            throw new StorageException("Cannot delete path " + path, e);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        LOGGER.info("Received ZooKeeper event " + event);
    }

    /**
     * @param nodetype
     * @param subtype
     * @param result
     * @throws StorageException
     */
    public void saveWithRetries(String nodetype, String subtype, JSONArtifact result) throws StorageException {

        int retries = 5;
        boolean retFlag = true;
        if (nodetype == null) {
            return;
        }
        String path = this.rootNodeName + "/" + nodetype;
        try {
            checkIfNodeExists(path);
            path += "/" + (subtype != null ? subtype : "all");
            checkIfNodeExists(path);
            if (result == null) {
                return;
            }
            if (result instanceof JSONObject && ((JSONObject) result).isEmpty()) {
                return;
            } else if (result instanceof JSONArray && ((JSONArray) result).isEmpty()) {
                return;
            }
            // LOGGER.debug("Saving to path - " + path + " \n" + result.toString());
            byte[] data = jsonToBytes(result);
            while (true) {

                try {
                    Stat stat = getZooKeeper(true).exists(path, true);
                    getZooKeeper(true).setData(path, data, stat.getVersion());
                    retFlag = true;
                } catch (KeeperException | InterruptedException | StorageException e) {
                    retFlag = false;
                    if (retries == 0) {
                        throw new StorageException("Cannot save results to path " + path, e);
                    }
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e1) {
                        LOGGER.error("Error while sleep", e);
                        break;
                    }
                    retries--;

                }
                if (retFlag) {
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Cannot save results to path " + path, e);
            throw new StorageException("Cannot save results to path " + path, e);
        }
    }
}
