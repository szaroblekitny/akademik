package org.wojtekz.akademik.plik;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Student;

/**
 * Klasa reprezentująca element student w pliku XML: {@code <student plec="KOBIETA"><imie>..</imie><nazwisko>..</nazwisko></student>}.
 * Bez ID i bez przydziału pokoju.
 * <p>
 * Płeć to zwykły {@code enum Plec}: JAXB zapisuje stałą pod jej nazwą
 * ({@code KOBIETA}, {@code MEZCZYZNA}), tak jak przewiduje schemat.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"imie", "nazwisko"})
public class StudentXml {

	@XmlAttribute(required = true)
	private Plec plec;

	@XmlElement(required = true)
	private String imie;

	@XmlElement(required = true)
	private String nazwisko;

	/** Dla JAXB. */
	public StudentXml() {
	}

	public StudentXml(String imie, String nazwisko, Plec plec) {
		this.imie = imie;
		this.nazwisko = nazwisko;
		this.plec = plec;
	}

	/**
	 * @param student encja z bazy
	 * @return student w formacie pliku
	 */
	public static StudentXml zEncji(Student student) {
		return new StudentXml(student.getImie(), student.getNazwisko(), student.getPlec());
	}

	/**
	 * @return nowa encja bez ID i bez pokoju
	 */
	public Student naEncje() {
		Student student = new Student();
		student.setImie(imie);
		student.setNazwisko(nazwisko);
		student.setPlec(plec);
		return student;
	}

	public String getImie() {
		return imie;
	}

	public String getNazwisko() {
		return nazwisko;
	}

	public Plec getPlec() {
		return plec;
	}
}
