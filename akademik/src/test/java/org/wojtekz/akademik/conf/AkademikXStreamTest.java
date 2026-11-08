package org.wojtekz.akademik.conf;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

	@BeforeEach
	public void setUp() throws Exception {
		xstream = new AkademikXStream();
		xstream.setSupportedClasses(Plikowalny.class);
	}

	@Test
	public void testBuildXStream() {
		assertTrue(xstream.supports(Pokoj.class));
		assertTrue(xstream.supports(Student.class));
	}

}
