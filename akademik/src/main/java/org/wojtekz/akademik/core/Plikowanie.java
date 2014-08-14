package org.wojtekz.akademik.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.wojtekz.akademik.entity.Student;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;


public class Plikowanie {
	
	private static final String FILE_NAME = "studenci.xml";
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public void saveStudenta(Student student) throws IOException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(FILE_NAME);
            this.marshaller.marshal(student, new StreamResult(os));
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public Student loadStudenta() throws IOException {
    	Student student = new Student();
        FileInputStream is = null;
        try {
            is = new FileInputStream(FILE_NAME);
            student = (Student) this.unmarshaller.unmarshal(new StreamSource(is));
        } finally {
            if (is != null) {
                is.close();
            }
        }
        
        return student;
    }

}
