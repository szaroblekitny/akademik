package org.wojtekz.akademik.core;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.wojtekz.akademik.entity.Student;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;


public class Plikowanie {
	
	private static final String FILE_NAME = "studenci.xml";
    private static Marshaller marshaller;
    private static Unmarshaller unmarshaller;

    public void setMarshaller(Marshaller marshaller) {
        Plikowanie.marshaller = marshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        Plikowanie.unmarshaller = unmarshaller;
    }

    public void saveStudenta(Student student) throws IOException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(FILE_NAME);
            Plikowanie.marshaller.marshal(student, new StreamResult(os));
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }
    
    public static <T> void saveObjectList(BufferedWriter writer, List<T> list)
    		throws XmlMappingException, IOException {
    	StreamResult result = new StreamResult(writer);
    	for (T element : list) {
    		Plikowanie.marshaller.marshal(element, result);
    		writer.newLine();
    	}
    }

    public Student loadStudenta() throws IOException {
    	Student student = new Student();
        FileInputStream is = null;
        try {
            is = new FileInputStream(FILE_NAME);
            student = (Student) Plikowanie.unmarshaller.unmarshal(new StreamSource(is));
        } finally {
            if (is != null) {
                is.close();
            }
        }
        
        return student;
    }

}
