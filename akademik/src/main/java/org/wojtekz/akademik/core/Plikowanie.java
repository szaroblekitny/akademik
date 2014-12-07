package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;

/**
 * Klasa do obs�ugi operacji plikowych.
 * 
 * @author Wojtek
 *
 */
public class Plikowanie {
	private static Logger logg = Logger.getLogger(Plikowanie.class.getName());

	private static Marshaller marshaller;
	private static Unmarshaller unmarshaller;
	
	@Autowired
    XStreamMarshaller xStreamMarshaller;
	
	@Autowired
	PokojService pokojService;
	
	@Autowired
	StudentService studentService;
	
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
	 * Zapisuje list� obiekt�w do pliku XML.
	 * 
	 * @param writer BufferedWriter
	 * @param list lista obiekt�w podanego typu
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
	 * Przekszta�ca dane z pliku XML na list� obiekt�w.
	 * 
	 * @param reader buforowy
	 * @return list� obiekt�w odczytanych z pliku
	 * @throws XmlMappingException w przypadku niepowodzenia mapowania pliku XML na obiekty
	 *         podanego typu; r�wnie� gdy plik nie jest plikiem XML
	 * @throws IOException b��d odczytu z pliku
	 */
	public List<?> loadObjectList(BufferedReader reader)
			throws XmlMappingException, IOException {
		List<?> obj;
		obj =  (List<?>) Plikowanie.unmarshaller.unmarshal(new StreamSource(reader));
		return obj;
	}
	
	
	/**
	 * Metoda pomocnicza s�u��ca do zapisania pokoj�w z bazy danych do pliku.
	 * 
	 * @param writer BufferedWriter
	 * @throws IOException 
	 * @throws XmlMappingException 
	 */
	public void zapiszPokojeDoBufora(BufferedWriter writer) throws XmlMappingException, IOException {
		List<Pokoj> listaPokoi = pokojService.listAll();
		saveObjectList(writer, listaPokoi);
	}

	/**
	 * Metoda pomocnicza s�u��ca do zapisania student�w z bazy danych do pliku.
	 * 
	 * @param writer BufferedWriter
	 * @throws IOException 
	 * @throws XmlMappingException 
	 */
	public void zapiszStudentowDoBufora(BufferedWriter writer) throws XmlMappingException, IOException {
		List<Student> listaStudentow = studentService.listAll();
		saveObjectList(writer, listaStudentow);
	}

}
