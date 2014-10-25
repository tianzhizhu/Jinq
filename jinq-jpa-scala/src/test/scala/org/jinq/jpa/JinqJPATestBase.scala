package org.jinq.jpa;

import java.sql.DriverManager
import java.sql.SQLException
import java.util.ArrayList
import java.util.List
import java.util.Map
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence
import org.jinq.jpa.test.entities.Item
import org.jinq.jpa.test.entities.Lineorder
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass;
import _root_.scala.collection.mutable.ArrayBuffer

object JinqJPATestBase {
  var entityManagerFactory: EntityManagerFactory = null;
  var streams : JinqJPAScalaStreamProvider = null

   @BeforeClass
   def setUpBeforeClass() {
      entityManagerFactory = Persistence.createEntityManagerFactory("JPATest");
      streams = new JinqJPAScalaStreamProvider(JinqJPATestBase.entityManagerFactory);
      
      // Hibernate seems to generate incorrect metamodel data for some types of
      // associations, so we have to manually supply the correct information here.
      streams.registerAssociationAttribute(classOf[Lineorder].getMethod("getItem"), "item", false);
      streams.registerAssociationAttribute(classOf[Lineorder].getMethod("getSale"), "sale", false);
      
      var em = entityManagerFactory.createEntityManager();
      new CreateJpaDb(em).createDatabase();
      em.close();
   }

   @AfterClass
   def tearDownAfterClass() {
      entityManagerFactory.close();
      try {
         DriverManager.getConnection("jdbc:derby:memory:demoDB;drop=true");
      } catch {
        case e: SQLException => { }
      }
   }
   

}
class JinqJPATestBase {
   var em : EntityManager = null;
   var query : String = null;
   val queryList = new ArrayBuffer[String]();
   
   @Before
   def setUp() {
      em = JinqJPATestBase.entityManagerFactory.createEntityManager();
      em.getTransaction().begin();
      queryList.clear();
      JinqJPATestBase.streams.setHint("exceptionOnTranslationFail", true);
      JinqJPATestBase.streams.setHint("queryLogger", new JPAQueryLogger() {
         def logQuery(q: String,
               positionParameters: Map[Integer, Object] ,
               namedParameters: Map[String, Object] )
         {
            queryList.append(q);
            query = q;
         }});
   }

   @After
   def tearDown() {
      em.getTransaction().rollback();
      em.close();
   }
}