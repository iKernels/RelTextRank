package it.unitn.nlpir.uima;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class UIMAFilePersistence implements UIMAPersistence {

	private String casDir = "CASes";
	private final Logger logger = LoggerFactory.getLogger(UIMAFilePersistence.class);

	public UIMAFilePersistence(String path) {
		logger.info("Using the CAS dir at: {}", path);
		this.casDir = path;
		try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e) {
			logger.error("Failed to create path: " + path);
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(JCas cas, String id) {
		try (FileOutputStream out = new FileOutputStream(Paths.get(this.casDir, id).toFile())) {
			XmiCasSerializer ser = new XmiCasSerializer(cas.getTypeSystem());
			XMLSerializer xmlSer = new XMLSerializer(out, false);
			logger.info("Serializing cas for the document: {}", id);
			ser.serialize(cas.getCas(), xmlSer.getContentHandler());
		} catch (SAXException | IOException e) {
			logger.error("Failed to serialize cas for the document: {}", id);
			e.printStackTrace();
		}
	}

	@Override
	public void deserialize(JCas cas, String id) {
		try (FileInputStream in = new FileInputStream(Paths.get(casDir, id).toFile())) {
			logger.debug("Deserializing cas for the document: {}", id);
			XmiCasDeserializer.deserialize(in, cas.getCas());
		} catch (IOException | SAXException e) {
			logger.error("Failed to deserialize cas for the document: {}", id);
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAlreadySerialized(String casXMLPath) {
		return (new File(casDir, casXMLPath)).exists();
	}
}
