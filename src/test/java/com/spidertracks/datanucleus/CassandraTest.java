/**********************************************************************
Copyright (c) 2010 Todd Nine. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
    ...
 ***********************************************************************/
package com.spidertracks.datanucleus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.KsDef;
import org.apache.thrift.transport.TTransportException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.scale7.cassandra.pelops.Cluster;
import org.scale7.cassandra.pelops.ColumnFamilyManager;
import org.scale7.cassandra.pelops.KeyspaceManager;
import org.scale7.cassandra.pelops.support.EmbeddedCassandraServer;

/**
 * @author Todd Nine
 * 
 */
public abstract class CassandraTest {

	public static final String RPC_LISTEN_ADDRESS = "localhost";

	public static final int RPC_PORT = 19160;

	public static String BASE_DIRECTORY = "target/cassandra";

	public static final String KEYSPACE = "TestingKeyspace";

	protected static EmbeddedCassandraServer cassandraServer;
	protected static PersistenceManagerFactory pmf;

	/**
	 * Set embedded cassandra up and spawn it in a new thread.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setup() throws Exception {

		if (cassandraServer == null) {
			cassandraServer = new EmbeddedCassandraServer(RPC_LISTEN_ADDRESS,
					RPC_PORT, BASE_DIRECTORY);
			cassandraServer.start();

			// wait until cassandra server starts up. could wait less time, but
			// 2 seconds to be sure.
			Thread.sleep(2000);
		}

		pmf = JDOHelper.getPersistenceManagerFactory("Test");
	}


	@AfterClass
	public static void teardown() throws Exception {
		try{
		cassandraServer.stop();
		}catch(Exception e){
			/*SWALLOW*/
		}
	}
}
