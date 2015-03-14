package com.corpus.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.ws.rs.core.MediaType;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.corpus.client.json.RotationDeserializer;
import com.corpus.client.json.Vector3DDeserializer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jogamp.opengl.util.FPSAnimator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * This application uses the <code>CORPUS</code> framework to access the data of
 * multiple sensors tracking human motion.
 * 
 * @see <a href="https://github.com/scopus777/corpus">CORPUS Framework</a>
 * 
 * @author Matthias Weise
 * 
 */
public class CorpusClient {

	static FPSAnimator animator;

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {

		Client c = Client.create();
		c.setFollowRedirects(true);

		WebResource r = c
				.resource("http://localhost:8080/corpus/customModel?type=hierarchical&field=children&field=jointType&field=absolutePosition&field=absoluteorientation");

		String response = r.accept(MediaType.APPLICATION_JSON).get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Vector3D.class, new Vector3DDeserializer());
		module.addDeserializer(Rotation.class, new RotationDeserializer());
		mapper.registerModule(module);
		Joint[] root = mapper.readValue(response, Joint[].class);

		ModelFrame canvas = new ModelFrame(1024, 1024);
		canvas.model = root;
		JFrame frame = new JFrame("Corpus Client");
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.setSize(1024, 1024);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		canvas.requestFocus();

		while (true) {
			canvas.model = mapper
					.readValue(
							c.resource(
									"http://localhost:8080/corpus/customModel?type=hierarchical&field=children&field=jointType&field=absolutePosition&field=absoluteorientation")
									.accept(MediaType.APPLICATION_JSON).get(String.class), Joint[].class);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
