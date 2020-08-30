package org.wojtekz.akademik.conf;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.wojtekz.akademik.entity.Plikowalny;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;

/**
 * Test security dla XStreama.
 * 
 * @author Wojciech Zaręba
 *
 */
public class AkademikXStreamTest {
	private AkademikXStream xstream;

	@Before
	public void setUp() throws Exception {
		xstream = new AkademikXStream();
		xstream.setSupportedClasses(Plikowalny.class);
	}

	@Test
	public void testBuildXStream() {
		assertEquals(true, xstream.supports(Pokoj.class));
		assertEquals(true, xstream.supports(Student.class));
	}

}
