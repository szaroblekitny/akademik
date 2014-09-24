package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.wojtekz.akademik.entity.Student;

public class Plikowanie {
	private static Logger logg = Logger.getLogger(Plikowanie.class.getName());

	private static final String FILE_NAME = "studenci.xml";
	private static Marshaller marshaller;
	private static Unmarshaller unmarshaller;
	
	@Autowired
    XStreamMarshaller xStreamMarshaller;
	
	public Plikowanie() {
		Plikowanie.marshaller = xStreamMarshaller;
		Plikowanie.unmarshaller = xStreamMarshaller;
	}

	public void setMarshaller(Marshaller marshaller) {
		logg.debug("----->>> setMarshaller method fired");
		Plikowanie.marshaller = marshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		logg.debug("----->>> setUnmarshaller method fired");
		Plikowanie.unmarshaller = unmarshaller;
	}

	public void saveStudentow(List<Student> studenty) throws IOException {
		logg.debug("----->>> !!! method fired");

		FileOutputStream os = null;

		try {
			os = new FileOutputStream(FILE_NAME);

			Plikowanie.marshaller.marshal(studenty, new StreamResult(os));
		} catch (Exception ee) {
			logg.error("----- ERROR >> ", ee);
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

	public static <T> void saveObjectList(BufferedWriter writer, List<T> list)
			throws XmlMappingException, IOException {
		logg.debug("----->>> saveObjectList method fired");
		StreamResult result = new StreamResult(writer);
		Plikowanie.marshaller.marshal(list, result);
	}

	@SuppressWarnings("unchecked")
	public List<Student> loadStudentow() throws IOException {
		List<Student> studenci = new ArrayList<Student>();
		FileInputStream is = null;
		try {
			is = new FileInputStream(FILE_NAME);
			studenci = (List<Student>) Plikowanie.unmarshaller
					.unmarshal(new StreamSource(is));
		} finally {
			if (is != null) {
				is.close();
			}
		}

		return studenci;
	}

	
	public static List<?> loadObjectList(BufferedReader reader)
			throws XmlMappingException, IOException {
		List<?> obj;
		obj =  (List<?>) Plikowanie.unmarshaller.unmarshal(new StreamSource(reader));
		return obj;
	}

}
