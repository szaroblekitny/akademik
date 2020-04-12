package org.wojtekz.akademik.conf;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.oxm.xstream.XStreamMarshaller;

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
		
		final String[] cl = {"org.wojtekz.akademik.entity.Pokoj", "org.wojtekz.akademik.entity.Student"};
		
		XStream xstream = new XStream();
		// clear out existing permissions and set own ones
		xstream.addPermission(NoTypePermission.NONE);
		// allow some basics
		xstream.addPermission(NullPermission.NULL);
		xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
		xstream.allowTypeHierarchy(Collection.class);
		
		// i tu nasze klasy Student i Pokoj
		xstream.addPermission(new ExplicitTypePermission(cl));
		
		return xstream;
	}

}
