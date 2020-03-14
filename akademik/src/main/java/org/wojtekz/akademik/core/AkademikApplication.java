package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.wojtekz.akademik.conf.AkademikConfiguration;

/**
 * To jest główna klasa naszej aplikacji pod tytułem Manager akademika. Zobacz opis aplikacji.
 * 
 * @author Wojtek Zaręba
 *
 */
public class AkademikApplication {
	private static Logger logg = LogManager.getLogger();
	private static Charset charset = StandardCharsets.UTF_8;
	
	
	
	/**
	 * Wywołanie naszej aplikacji. Podajemy plik z pokojami, plik ze studentami i plik wyjściowy,
	 * a resztę ma zrobić program, czyli zakwaterować studentów i wypisać wynik.
	 * 
	 * @param args argumenty: nazwa pliku z pokojami, nazwa pliku ze strudentami i nazwa pliku wyjściowego
	 */
	public static void glowna(String[] args) {
		logg.info("----->>> Akademik - dane z plików");
		
		if (args.length != 3) {
			System.out.println("Wywołanie z argumentami: nazwa_pliku_z_pokojami nazwa_pliku_ze_studentami nazwa_pliku_wynikowego");
			return;
		}
		
		GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(AkademikConfiguration.class);
		logg.debug("----->>> Mamy kontekst AkademikApplication");
		
		Akademik akademik = applicationContext.getBean(Akademik.class);
		
		try (
			BufferedReader pokojeReader = Files.newBufferedReader(FileSystems.getDefault().getPath(args[0]), charset);
			BufferedReader studenciReader = Files.newBufferedReader(FileSystems.getDefault().getPath(args[1]), charset);
			BufferedWriter outputWriter = Files.newBufferedWriter(FileSystems.getDefault().getPath(args[2]), charset))
		{
			akademik.akademik(pokojeReader, studenciReader, outputWriter);
		} catch (IOException ie) {
			logg.error("Problemy plikowe", ie);
			System.out.println("Pliki wejściowe są niewłaściwe");
		}
		
		applicationContext.close();
		
	}
	
}
