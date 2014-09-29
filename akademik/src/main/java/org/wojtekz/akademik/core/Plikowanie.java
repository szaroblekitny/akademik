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

/**
 * Klasa do obs³ugi operacji plikowych.
 * 
 * @author Wojtek
 *
 */
public class Plikowanie {
	private static Logger logg = Logger.getLogger(Plikowanie.class.getName());

	private static final String FILE_NAME = "studenci.xml";
	private static Marshaller marshaller;
	private static Unmarshaller unmarshaller;
	
	@Autowired
    XStreamMarshaller xStreamMarshaller;
	
	/**
	 * Konstruktor ustawia marszalera na XStreamMarshaller.
	 */
	public Plikowanie() {
		Plikowanie.marshaller = xStreamMarshaller;
		Plikowanie.unmarshaller = xStreamMarshaller;
	}

	/**
	 * Ustawia marshallera do generowania XML'a.
	 * 
	 * @param marshaller
	 */
	public void setMarshaller(Marshaller marshaller) {
		logg.debug("----->>> setMarshaller method fired");
		Plikowanie.marshaller = marshaller;
	}

	/**
	 * Ustawia unmarshallera do dekompozycji XML'a w obiekt.
	 * 
	 * @param unmarshaller
	 */
	public void setUnmarshaller(Unmarshaller unmarshaller) {
		logg.debug("----->>> setUnmarshaller method fired");
		Plikowanie.unmarshaller = unmarshaller;
	}

	/**
	 * TODO - usun¹æ po testach.
	 * 
	 * @param studenty
	 * @throws IOException
	 */
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

	/**
	 * Zapisuje listê obiektów do pliku XML.
	 * 
	 * @param writer BufferedWriter
	 * @param list lista obiektów podanego typu
	 * @throws XmlMappingException
	 * @throws IOException
	 */
	public <T> void saveObjectList(BufferedWriter writer, List<T> list)
			throws XmlMappingException, IOException {
		logg.debug("----->>> saveObjectList method fired");
		StreamResult result = new StreamResult(writer);
		Plikowanie.marshaller.marshal(list, result);
	}

	/**
	 * TODO - usun¹æ po testach.
	 * 
	 * @return lista studentów
	 * @throws IOException
	 */
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

	
	/**
	 * Przekszta³ca dane z pliku XML na listê obiektów.
	 * 
	 * @param reader buforowy
	 * @return listê obiektów odczytanych z pliku
	 * @throws XmlMappingException w przypadku niepowodzenia mapowania pliku XML na obiekty
	 *         podanego typu; równie¿ gdy plik nie jest plikiem XML
	 * @throws IOException b³¹d odczytu z pliku
	 */
	public List<?> loadObjectList(BufferedReader reader)
			throws XmlMappingException, IOException {
		List<?> obj;
		obj =  (List<?>) Plikowanie.unmarshaller.unmarshal(new StreamSource(reader));
		return obj;
	}

}
