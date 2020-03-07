package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.services.PokojService;
import org.wojtekz.akademik.services.StudentService;

/**
 * Klasa do obsługi operacji plikowych.
 * 
 * @author Wojtek
 *
 */
public class Plikowanie {
	private static Logger logg = LogManager.getLogger();

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
	 * Zapisuje listę obiektów do pliku XML.
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
	 * Przekształca dane z pliku XML na listę obiektów.
	 * 
	 * @param reader buforowy
	 * @return listę obiektów odczytanych z pliku
	 * @throws XmlMappingException w przypadku niepowodzenia mapowania pliku XML na obiekty
	 *         podanego typu; również gdy plik nie jest plikiem XML
	 * @throws IOException błąd odczytu z pliku
	 */
	public List<?> loadObjectList(BufferedReader reader)
			throws XmlMappingException, IOException {
		List<?> obj;
		obj =  (List<?>) Plikowanie.unmarshaller.unmarshal(new StreamSource(reader));
		return obj;
	}
	
	
	/**
	 * Metoda pomocnicza służąca do zapisania pokojów z bazy danych do pliku.
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
	 * Metoda pomocnicza służąca do zapisania studentów z bazy danych do pliku.
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
