/**
 * Format pliku z danymi akademika (schemat {@code /xsd/akademik.xsd}).
 * <p>
 * Klasy z tego pakietu odwzorowują plik XML i nic więcej: nie są encjami bazy.
 * Plik nie ma identyfikatorów ani przydziału pokoi, a encje je mają, więc trzymanie
 * jednego i drugiego w tych samych klasach mieszałoby dwa różne modele.
 * <p>
 * {@link jakarta.xml.bind.annotation.XmlSchema} poniżej mówi JAXB, że wszystkie
 * elementy z tego pakietu należą do przestrzeni nazw {@code urn:wojtekz:akademik:dane:1}
 * ({@code QUALIFIED}), a atrybuty nie (domyślnie {@code UNQUALIFIED}), tak jak w schemacie.
 *
 * @author Wojciech Zaręba
 */
@XmlSchema(namespace = AkademikXml.PRZESTRZEN_NAZW, elementFormDefault = XmlNsForm.QUALIFIED)
package org.wojtekz.akademik.plik;

import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;
