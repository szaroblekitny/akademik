package org.wojtekz.akademik.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.services.StudentService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class StudentServiceImplTest {
	private static Logger logg = Logger.getLogger(StudentServiceImplTest.class.getName());
	
	Student student;
	
	@Autowired
	StudentService studService;


	@Before
	public void setUp() throws Exception {
		logg.debug("----->>> setUp");
		student = new Student();
		student.setId(1);
		student.setImie("Jan");
		student.setNazwisko("Kowalski");
		student.setPlec(Plec.Mezczyzna);
	}

	@After
	public void tearDown() throws Exception {
		logg.debug("----->>> tearDown");
		studService.deleteAll();
	}

	@Test
	public void testSave() {
		logg.debug("----->>> testSave");
		studService.save(student);
		Assert.assertEquals(1, studService.iluStudentow());
	}

	@Test
	public void testListAll() {
		logg.debug("----->>> testListAll");
		List<Student> studenci;
		studService.save(student);
		studenci = studService.listAll();
		for (Student st : studenci) {
			logg.debug("----->>> " + st.toString());
		}
		
		Assert.assertEquals("Kowalski", studenci.get(0).getNazwisko());
	}

	@Test
	public void testFindById() {
		Student stt;
		
		logg.debug("----->>> testFindById");
		studService.save(student);
		stt = studService.findById(1);
		Assert.assertEquals("Kowalski", stt.getNazwisko());
	}

	@Test
	public void testFindByName() {
		logg.debug("----->>> testFindByName");
		Student stt;
		
		studService.save(student);
		stt = studService.findByName("Kowalski");
		Assert.assertEquals("Jan", stt.getImie());
	}

	@Test
	public void testDeleteById() {
		logg.debug("----->>> testDeleteById");
		studService.save(student);
		Assert.assertEquals(1, studService.iluStudentow());
		studService.deleteById(1);
		Assert.assertEquals(0, studService.iluStudentow());

	}

}
