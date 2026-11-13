package org.wojtekz.akademik.plik;

import java.util.List;

import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;

/**
 * Zawartość wczytanego pliku jako encje gotowe do zapisu w bazie (jeszcze bez ID).
 *
 * @param pokoje pokoje z sekcji {@code <pokoje>}, pusta lista, gdy sekcji nie było
 * @param studenci studenci z sekcji {@code <studenci>}, pusta lista, gdy sekcji nie było
 */
public record DaneZPliku(List<Pokoj> pokoje, List<Student> studenci) {
}
