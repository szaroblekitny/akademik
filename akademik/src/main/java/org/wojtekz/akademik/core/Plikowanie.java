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
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.entity.Plikowalny;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.services.PokojService;
import org.wojtekz.akademik.services.StudentService;


/**
 * Klasa do obsługi operacji plikowych.
 * 
 * @author Wojtek Zaręba
 *
 */
@Component
public class Plikowanie {
	private static Logger logg = LogManager.getLogger();

	@Autowired
	private Marshaller marshaller;
	
	@Autowired
	private Unmarshaller unmarshaller;
	
	@Autowired
	private PokojService pokojService;
	
	@Autowired
	private StudentService studentService;
	

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
		logg.trace("----->>> saveObjectList method fired");
		
		if (writer == null) {
			logg.error("----->>> saveObjectList writer pusty");
			throw new IOException();
		}
		
		if (list == null || list.isEmpty()) {
			logg.error("----->>> saveObjectList pusta lista");
			throw new IOException();
		}
		
		StreamResult result = new StreamResult(writer);
		logg.debug("----->>> result: {}", result);
		marshaller.marshal(list, result);
		
		logg.trace("----->>> saveObjectList koniec");
	}

	
	/**
	 * Przekształca dane z pliku XML na listę obiektów.
	 * 
	 * @param reader BufferedReader - odczytywane dane
	 * @return listę obiektów odczytanych z pliku
	 * @throws XmlMappingException w przypadku niepowodzenia mapowania pliku XML na obiekty
	 *         podanego typu; również gdy plik nie jest plikiem XML
	 * @throws IOException błąd odczytu z pliku
	 */
	@SuppressWarnings("unchecked")
	public List<Plikowalny> loadObjectList(BufferedReader reader)
			throws XmlMappingException, IOException {
		List<Plikowalny> obj;
		obj =  (List<Plikowalny>) unmarshaller.unmarshal(new StreamSource(reader));
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
