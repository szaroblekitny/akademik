package org.wojtekz.akademik.namedbean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.UploadedFile;
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
	// private static String KATALOG_IMPORTU = "pokoje_test_appl.xml";
	private static final String IMPORT_PLIKU = "Import pliku";
	
	private static Logger logg = LogManager.getLogger();
	
	private transient Messagesy komunikaty;
	
	@Autowired
	public void setMessagesy(Messagesy komunikaty) {
		this.komunikaty = komunikaty;
	}
	
	private UploadedFile file;
 
    public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }
 
    public void upload() {
    	logg.debug("-------> ImportBean upload");
        if (file != null) {
        	try {
				file.write(file.getFileName());
			} catch (Exception ee) {
				komunikaty.addMessage(FacesMessage.SEVERITY_ERROR, IMPORT_PLIKU, "Błąd " + ee.getMessage(), null);
				return;
			}
        	
        	komunikaty.addMessage(IMPORT_PLIKU, "Plik " + file.getFileName() + " zaimportowany");
        } else {
        	komunikaty.addMessage(IMPORT_PLIKU, "Brak pliku");
        }
    }

}
