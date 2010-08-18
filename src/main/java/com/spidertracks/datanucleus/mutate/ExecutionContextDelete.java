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

Contributors : Todd Nine
 ***********************************************************************/
package com.spidertracks.datanucleus.mutate;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Stack;

import org.apache.cassandra.thrift.ConsistencyLevel;
import org.datanucleus.store.ExecutionContext;
import org.datanucleus.store.ObjectProvider;
import org.wyki.cassandra.pelops.KeyDeletor;

/**
 * Holds all mutations for the current execution context
 * 
 * @author Todd Nine
 * 
 */
public class ExecutionContextDelete extends ExecutionContextOp {



	//operations of mutations to perform
	//our reference to visited objects so we don't get stuck in a recursive delete
	private IdentityHashMap<ObjectProvider, Object> visited = new IdentityHashMap<ObjectProvider, Object>();
	private List<Deletion> mutations = new Stack<Deletion>();
	private KeyDeletor deletor;

	
	public ExecutionContextDelete(ExecutionContext ctx, KeyDeletor deletor) {
		super(ctx);
		this.deletor = deletor;
	}




	/**
	 * Add the deleting if the op hasn't already been visited
	 * @param op The object we're deleting
	 * @param key The key to delete
	 * @param columnFamily The CF to dele
	 * @return True if this is the first visit to the object.  False otherwise
	 */
	public boolean addDeletion(ObjectProvider op, String key, String columnFamily) {
		
		if(visited.containsKey(op)){
			return false;
		}
		
		visited.put(op, null);
		
		mutations.add(new Deletion(key, columnFamily));
		
		return true;
	}



	public void execute(ConsistencyLevel consistency) throws Exception {
		for (Deletion deletion : mutations) {
			deletor.deleteRow(deletion.rowKey, deletion.columnFamily, consistency);
		}
	}
	
	private class Deletion{
		
		public Deletion(String rowKey, String columnFamily) {
			super();
			this.rowKey = rowKey;
			this.columnFamily = columnFamily;
		}
		
		private String rowKey;
		private String columnFamily;
	}

}