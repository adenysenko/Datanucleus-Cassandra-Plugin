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

package com.spidertracks.datanucleus.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.spidertracks.datanucleus.CassandraTest;
import com.spidertracks.datanucleus.collection.model.Card;
import com.spidertracks.datanucleus.collection.model.Pack;

public class JDOQLObjectTest extends CassandraTest {

	private PersistenceManager setupPm;

	private Pack pack1;
	private Card card1, card2;

	@Before
	public void setUp() throws Exception {
	
		setupPm = pmf.getPersistenceManager();

		Transaction tx = setupPm.currentTransaction();
		tx.begin();

		pack1=new Pack();
		setupPm.makePersistent(pack1);

		card1=new Card();
		card1.setPack(pack1);
		card1.setName("card1");
		setupPm.makePersistent(card1);
		
		card2=new Card();
		card2.setPack(pack1);
		card2.setName("card2");
		setupPm.makePersistent(card2);
		
		tx.commit();
	}

	@After
	public void tearDown() throws Exception {
		Transaction tx = setupPm.currentTransaction();
		tx.begin();
		
		setupPm.deletePersistent(card1);
		setupPm.deletePersistent(card2);
		setupPm.deletePersistent(pack1);
		
		tx.commit();
	}


	@SuppressWarnings("rawtypes")
	@Test
	public void testFindCardsByPack() {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			
			Query q = pm.newQuery(Card.class);
			q.setFilter("pack == :pack");
			Collection c = (Collection) q.execute(pack1);
			assertEquals(2, c.size());
			Iterator it = c.iterator();
			assertEquals(pack1, ((Pack) it.next()));
			
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	
	@Test
	public void testPackHasCards() {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			
			Pack pack = pm.getObjectById(Pack.class, pack1.getId());
			assertNotNull(pack);
			assertNotNull(pack.getCards());
			assertEquals(2, pack.getCards().size());
			
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}

	@Test
	public void testCardHasPack() {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			
			Card card = pm.getObjectById(Card.class, card1.getId());
			assertNotNull(card);
			assertNotNull(card.getPack());
			assertEquals(pack1, card.getPack());
			
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
}