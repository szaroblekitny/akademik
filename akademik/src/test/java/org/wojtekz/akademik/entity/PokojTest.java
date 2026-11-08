package org.wojtekz.akademik.entity;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wojtekz.akademik.util.DaneTestowe;

/**
 * Najprostszy test encji Pokoj.
 * 
 * @author Wojciech Zaręba
 *
 */
public class PokojTest {
	private DaneTestowe dane = new DaneTestowe();
	
	@Test
	public void testOgolny() {
		Assertions.assertEquals("101", dane.getPokoj1().getNumerPokoju());
		Assertions.assertEquals(2, dane.getPokoj1().getLiczbaMiejsc());
	}
	
	@Test
	public void testZakwaterowania() {
		Pokoj pokoik = dane.getPokoj2();
		Assertions.assertNotNull(pokoik);
		Student studenciak = dane.getStudent3();
		Assertions.assertNotNull(studenciak);
		pokoik.zakwateruj(studenciak);
		Assertions.assertEquals(1, pokoik.getZakwaterowani().size());
		Assertions.assertEquals("102", studenciak.getPokoj().getNumerPokoju());
	}
	
	@Test
	public void testWykwaterowania() {
		Pokoj pokoik = dane.getPokoj3();
		Student testowanyStudent = dane.getStudent4();
		Assertions.assertNotNull(pokoik);
		pokoik.zakwateruj(dane.getStudent3());
		pokoik.zakwateruj(testowanyStudent);
		Assertions.assertEquals("103", testowanyStudent.getPokoj().getNumerPokoju());
		Assertions.assertEquals(2, pokoik.getZakwaterowani().size());
		pokoik.wykwateruj(testowanyStudent);
		Assertions.assertEquals(1, pokoik.getZakwaterowani().size());
		Assertions.assertNull(testowanyStudent.getPokoj());
	}
	
	/**
	 * Kontrakt compareTo: wynik ujemny, gdy ten obiekt jest "mniejszy",
	 * zero dla równych, dodatni, gdy "większy". Liczy się tylko znak,
	 * nie konkretna wartość. Pokoje porządkujemy rosnąco po ID.
	 */
	@Test
	public void testPorownania() {
		Pokoj pokoj1 = dane.getPokoj1();
		Pokoj pokoj2 = dane.getPokoj2();
		Assertions.assertTrue(pokoj1.compareTo(pokoj2) < 0, "pokój o ID 1 powinien być przed pokojem o ID 2");
		Assertions.assertTrue(pokoj2.compareTo(pokoj1) > 0, "pokój o ID 2 powinien być po pokoju o ID 1");
		Assertions.assertEquals(0, pokoj1.compareTo(pokoj1), "pokój porównany sam ze sobą");
	}

	/**
	 * Różnica ID równa 2^32 = 4 294 967 296. Po rzutowaniu takiej różnicy
	 * z long na int zostaje 0, więc błędna implementacja uzna dwa różne pokoje za równe.
	 */
	@Test
	public void testPorownaniaDuzychId() {
		Pokoj maly = pokojOId(0L);
		Pokoj duzy = pokojOId(4_294_967_296L);
		Assertions.assertTrue(maly.compareTo(duzy) < 0, "pokój o ID 0 powinien być przed pokojem o ID 2^32");
	}

	@Test
	public void testSortowania() {
		List<Pokoj> pokoje = List.of(pokojOId(3L), pokojOId(1L), pokojOId(2L));
		List<Long> posortowaneId = pokoje.stream()
				.sorted()
				.map(Pokoj::getId)
				.toList();
		Assertions.assertEquals(List.of(1L, 2L, 3L), posortowaneId);
	}

	private static Pokoj pokojOId(long id) {
		Pokoj pokoj = new Pokoj();
		pokoj.setId(id);
		return pokoj;
	}

}
