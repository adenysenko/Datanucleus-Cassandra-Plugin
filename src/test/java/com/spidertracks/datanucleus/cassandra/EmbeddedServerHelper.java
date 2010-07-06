/**
 * 
 */
package com.spidertracks.datanucleus.cassandra;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.cassandra.io.util.FileUtils;
import org.apache.cassandra.service.EmbeddedCassandraService;
import org.apache.thrift.transport.TTransportException;

/**
 * 
 * @author Ran Tavory (rantav@gmail.com)
 * @author Todd Nine
 * 
 */
public class EmbeddedServerHelper {

	private static final String TMP = "tmp";

	private EmbeddedCassandraService cassandra;

	/**
	 * Set embedded cassandra up and spawn it in a new thread.
	 * 
	 * @param deleteData
	 *            tue to delete the data before the server starts
	 * @throws TTransportException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void setup(boolean deleteData) throws TTransportException,
			IOException, InterruptedException {

		CassandraServiceDataCleaner cleaner = new CassandraServiceDataCleaner();

		// delete tmp dir first
		if (deleteData) {
			rmdir(TMP);
			cleaner.prepare(true);
		} else {
			cleaner.prepare(false);
		}
		cleaner.makeDirsIfNotExist();

		// make a tmp dir and copy storag-conf.xml and log4j.properties to it
		copy("/storage-conf.xml", TMP);
		copy("/log4j.properties", TMP);
		System.setProperty("storage-config", TMP);

		cassandra = new EmbeddedCassandraService();
		cassandra.init();
		Thread t = new Thread(cassandra);
		t.setDaemon(true);
		t.start();
	}

	public void teardown() {
		CassandraServiceDataCleaner cleaner = new CassandraServiceDataCleaner();

		try {
			cleaner.cleanupDataDirectories();
			rmdir(TMP);
		} catch (IOException e) {
			// IGNORE
		}
	}

	private static void rmdir(String dir) throws IOException {
		File dirFile = new File(dir);
		if (dirFile.exists()) {
			FileUtils.deleteDir(new File(dir));
		}
	}

	/**
	 * Copies a resource from within the jar to a directory.
	 * 
	 * @param resource
	 * @param directory
	 * @throws IOException
	 */
	private static void copy(String resource, String directory)
			throws IOException {
		mkdir(directory);
		InputStream is = EmbeddedServerHelper.class
				.getResourceAsStream(resource);
		String fileName = resource.substring(resource.lastIndexOf("/") + 1);
		File file = new File(directory + System.getProperty("file.separator")
				+ fileName);
		OutputStream out = new FileOutputStream(file);
		byte buf[] = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		is.close();
	}

	/**
	 * Creates a directory
	 * 
	 * @param dir
	 * @throws IOException
	 */
	private static void mkdir(String dir) throws IOException {
		FileUtils.createDirectory(dir);
	}
}
