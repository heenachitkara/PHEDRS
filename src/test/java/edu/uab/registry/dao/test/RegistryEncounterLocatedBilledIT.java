package edu.uab.registry.dao.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uab.registry.dao.RegistryEncountersDao;
import edu.uab.registry.domain.RegistryEncounter;
import edu.uab.registry.exception.DaoException;

public class RegistryEncounterLocatedBilledIT {


	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testgetEncountersLocatedBilled() {
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml")){
			RegistryEncountersDao regencdao = (RegistryEncountersDao) context.getBean("registryEncountersDao");
			assertNotNull(regencdao);
			List<RegistryEncounter> testlist = regencdao.get("448292", 2861);
			for(RegistryEncounter re : testlist) {
				System.out.println(re.getEncounterKey()+" "+re.getAdmitLocDescription()+ " " +re.getPlandesc());
			}
			assert(!testlist.isEmpty());
		} catch (Exception  | DaoException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testgetSpecificEncounterLocatedBilled() {
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml")){
			RegistryEncountersDao regencdao = (RegistryEncountersDao) context.getBean("registryEncountersDao");
			assertNotNull(regencdao);
			RegistryEncounter test = regencdao.get(new Integer(19187702),"448292", 2861);
			System.out.println(test.getEncounterKey()+" "+test.getAdmitLocDescription()+ " " +test.getPlandesc());
			assertNotNull(test);
		} catch (Exception  | DaoException e) {
			e.printStackTrace();
		}

	}

}
