package org.wojtekz.akademik.namedbean;

import java.io.InputStream;
import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Bean JSF do obsługi eksportu (downloadu) plików. Chwilowo
 * tylko ćwiczebnie.
 * 
 * @author Wojciech Zaręba
 *
 */
@Component
@Scope("session")
public class EksportBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logg = LogManager.getLogger();
	
	private static String NAZWA_PLIKU = "/zyrafa3.jpg";
	
	private StreamedContent content;

	/**
	 * Konstruktor, który robi wszystko, czyli eksportuje.
	 */
    public EksportBean() {
    	logg.debug("-------> konstruktor EksportBean");

    	InputStream inpStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(NAZWA_PLIKU);

    	if (inpStream != null) {
	        content = DefaultStreamedContent.builder()
	        		.name(NAZWA_PLIKU.substring(1)).contentType("image/jpg").stream(() -> inpStream).build();
    	}

    }

    public StreamedContent getContent() {
        return content;
    }

}
