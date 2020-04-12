package org.wojtekz.akademik.conf;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import com.thoughtworks.xstream.security.ExplicitTypePermission;

/**
 * Rozszerzenie XStreamMarshaller, żeby włączyć 'security framework of XStream'.
 * 
 * @author Wojciech Zaręba
 *
 */
public class AkademikXStream extends XStreamMarshaller {
	private static Logger logg = LogManager.getLogger();
	
	public AkademikXStream() {
		super();
	}
	
	/**
	 * Towrzenie XStreama z włączeniem security dla naszych klas.
	 * 
	 * @return skonfigurowany XStream z włączonym security
	 */
	@Override
	protected XStream buildXStream() {
		logg.debug("-------> buildXStream");
		
		@SuppressWarnings("rawtypes")
		final Class[] cl = {Pokoj.class, Student.class};
		
		XStream xstream = new XStream();
		// clear out existing permissions and set own ones
		xstream.addPermission(NoTypePermission.NONE);
		// allow some basics
		xstream.addPermission(NullPermission.NULL);
		xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
		xstream.allowTypeHierarchy(List.class);
		
		// i tu nasze klasy Student i Pokoj
		xstream.addPermission(new ExplicitTypePermission(cl));
		
		return xstream;
	}

}
