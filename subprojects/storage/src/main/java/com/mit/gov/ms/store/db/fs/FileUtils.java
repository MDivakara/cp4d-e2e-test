/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.db.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.store.StorageException;

/**
 * @author Shaik.Nawaz
 *
 */
public class FileUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
    private String rootNodeName = null;
    
    public FileUtils(String rootNodeName) {
        this.rootNodeName = new File(FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + "/" + rootNodeName;
    }
    
    public String getRootNode() {
        return this.rootNodeName;
    }
    
    public void checkPermission() throws StorageException {
        File file = new File(rootNodeName);
        LOGGER.info("Writing to file" + file.getAbsolutePath());
        File directory = new File(file.getParent());
        if ( !directory.exists() && ! directory.mkdirs() )
        {
            throw new StorageException("Unable to create directory " + file.getAbsolutePath());
        } 
    }
    
    public void writeToFileSystem(String dirName, String fileName, InputStream objectInputStream) throws StorageException {
        if (dirName == null) {
            return;
        }
        File file = new File(rootNodeName + "/" + dirName + "/" + fileName);
        LOGGER.info("Writing to file" + file.getAbsolutePath());
        File directory = new File(file.getParent());
        if ( !directory.exists() && ! directory.mkdirs() )
        {
            throw new StorageException("Unable to create directory " + file.getAbsolutePath());
        } 
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = objectInputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
        } catch (FileNotFoundException e) {
            throw new StorageException(e);
        } catch (IOException e) {
            throw new StorageException(e);
        }
        finally {
            try {
                if(os != null)
                    os.close();
			} catch (IOException e) {
				LOGGER.error("error while closing output stream, "+ e);
			}
        }
    }
    
    public static void writeToFileSystemDefaultFolder(String dirName, String fileName, InputStream objectInputStream) throws StorageException {
        if (dirName == null) {
            return;
        }
        File file = new File( dirName + "/" + fileName);
        LOGGER.info("Writing to file" + file.getAbsolutePath());
        File directory = new File(file.getParent());
        if ( !directory.exists() && ! directory.mkdirs() )
        {
            throw new StorageException("Unable to create directory " + file.getAbsolutePath());
        } 
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = objectInputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
        } catch (FileNotFoundException e) {
            throw new StorageException(e);
        } catch (IOException e) {
            throw new StorageException(e);
        }
        finally {
            try {
                if(os != null)
                    os.close();
			} catch (IOException e) {
				LOGGER.error("error while closing output stream, "+ e);
			}
        }
    }

    public InputStream readFromFileSystem(String dirName, String fileName) {
        if (dirName == null) {
            return null;
        }
        File file = new File(rootNodeName + "/" + dirName + "/" + fileName);
        LOGGER.info("Reading from file" + file.getAbsolutePath());
        if (file.canRead()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                LOGGER.error("error while reading file " + file.getAbsolutePath(), e);
            }
        } else {
            if (file.exists()) {
                LOGGER.error("unable to read file - exists " + file.getAbsolutePath());
            } else {
                LOGGER.error("unable to read file - not exists " + file.getAbsolutePath());
            }
        }
        return null;
    }

    public void deleteFromFileSystem(String dirName, String fileName) throws StorageException{
        if (dirName == null) {
            return;
        }
        File file = new File(rootNodeName + "/" + dirName + "/" + fileName);
        LOGGER.info("Deleting to file" + file.getAbsolutePath());
        if ( file.exists() && ! file.delete() ) {
            throw new StorageException("Unable to delete directory " + file.getAbsolutePath());
        }
    }
    
    public void deleteFromFileSystemDeafault(String dirName, String fileName) throws StorageException{
        if (dirName == null) {
            return;
        }
        File file = new File( dirName + "/" + fileName);
        LOGGER.info("Deleting to file" + file.getAbsolutePath());
        if ( file.exists() && ! file.delete() ) {
            throw new StorageException("Unable to delete directory " + file.getAbsolutePath());
        }
    }

    public void cleanUpFromFileSystem(String dirName) throws StorageException{
        if (dirName == null) {
            return;
        }
        File folder = new File(rootNodeName + "/" + dirName );
        if (!folder.exists()) {
            return;
        }
        LOGGER.info("Deleting folder" + folder.getAbsolutePath());
        deleteFolder(folder);
    }

    public void deleteFolder(File folder) throws StorageException {
        File[] files = folder.listFiles();
        if(files!=null) {
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    if ( ! f.delete() ) {
                        throw new StorageException("Unable to delete file " + f.getAbsolutePath());
                    }
                }
            }
        }
        if ( ! folder.delete() ) {
            throw new StorageException("Unable to delete the folder " + folder.getAbsolutePath());
        }
    }
    
    public ArrayList<String> getFileList(String dirName) {
        ArrayList<String> filelist = new ArrayList<String>();
        if (dirName == null) {
            return filelist;
        }
        File folder = new File(rootNodeName + "/" + dirName );
        if (!folder.exists()) {
            return filelist;
        }
        File[] files = folder.listFiles();
        if(files!=null) {
            for(File f: files) {
                if(!f.isDirectory()) {
                    filelist.add(f.getName());
                }
            }
        }
        return filelist;
    }
}
