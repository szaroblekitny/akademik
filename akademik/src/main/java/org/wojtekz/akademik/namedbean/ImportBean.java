package org.wojtekz.akademik.namedbean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.faces.application.FacesMessage;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Import plików z danymi o studentach lub pokojach.
 * 
 * @author Wojciech Zaręba
 *
 */
@Component
@Scope("session")
public class ImportBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static String KATALOG_IMPORTU = "h:/TEMP/";
	private static final String IMPORT_PLIKU = "Import pliku";
	
	private static Logger logg = LogManager.getLogger();
	
	private transient Messagesy komunikaty;
	
	private UploadedFile upFile;
	
	@Autowired
	public void setMessagesy(Messagesy komunikaty) {
		this.komunikaty = komunikaty;
	}
 
    public UploadedFile getFile() {
        return upFile;
    }
 
    public void setFile(UploadedFile upFile) {
        this.upFile = upFile;
    }

    /**
     * Wykonuje upload (import pliku z maszyny użytkownika) i zapisuje plik.
     * Tworzy komunikaty JSF o statusie statusie przetwarzania.
     * 
     */
    public void upload() {
    	logg.debug("-------> ImportBean upload");
        if (upFile != null) {

        	String filename = FilenameUtils.getName(upFile.getFileName());

        	try {
        		InputStream input = upFile.getInputStream();
        		OutputStream output = new FileOutputStream(new File(KATALOG_IMPORTU, filename));

                IOUtils.copy(input, output);

			} catch (Exception ee) {
				logg.error("Błąd zapisu " + upFile.getFileName(), ee);
				komunikaty.addMessage(FacesMessage.SEVERITY_ERROR, IMPORT_PLIKU, "Błąd " + ee.toString(), null);
				return;
			}

        	komunikaty.addMessage(IMPORT_PLIKU, "Plik " + upFile.getFileName() + " zaimportowany");
        } else {
        	komunikaty.addMessage(IMPORT_PLIKU, "Brak pliku");
        }
    }

}
