package org.wojtekz.akademik.namedbean;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.springframework.stereotype.Component;

/**
 * Dodaje komunikat do kontekstu serwletu JSF.
 * Bardzo sprytne rozwiązanie, które ma umożliwić testowane beanów JSF.
 *
 * @author Wojciech Zaręba
 *
 */
@Component
public class Messagesy {
	
	/**
	 * Tu dodajemy komunikat i tytuł, a on ma być wyświetlony na stronce JSF.
	 * Można podać ważność komunikatu i id obiektu na stronce.
	 * 
	 * @param severity waga komunikatu FacesMessage: SEVERITY_INFO, SEVERITY_WARN, SEVERITY_ERROR, SEVERITY_FATAL
	 * @param title tytuł komunikatu
	 * @param message detale
	 * @param clientId identyfikator nadawcy na stronce (id taga)
	 */
	public void addMessage(Severity severity, String title, String message, String clientId) {
		FacesMessage msg = new FacesMessage(severity, title, message);
        FacesContext.getCurrentInstance().addMessage(clientId, msg);
	}
	
	/**
	 * Komunikat, tytuł i id klienta, SEVERITY_INFO.
	 * 
	 * @param title tytuł komunikatu
	 * @param message detale
	 * @param clientId identyfikator nadawcy na stronce (id taga)
	 */
	public void addMessage(String title, String message, String clientId) {
		addMessage(FacesMessage.SEVERITY_INFO, title, message, clientId);
	}
	
	/**
	 * Komunikat i tytuł bez id klienta, SEVERITY_INFO.
	 * 
	 * @param title tytuł komunikatu
	 * @param message detale
	 */
	public void addMessage(String title, String message) {
		addMessage(title, message, null);
	}
}
