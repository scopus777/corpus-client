package com.corpus.client.json;

import java.io.IOException;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;

public class RotationDeserializer extends JsonDeserializer<Rotation> {

	@Override
	public Rotation deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		double q0 = (Double) ((DoubleNode) node.get("w")).numberValue();
		double q1 = (Double) ((DoubleNode) node.get("x")).numberValue();
		double q2 = (Double) ((DoubleNode) node.get("y")).numberValue();
		double q3 = (Double) ((DoubleNode) node.get("z")).numberValue();

		return new Rotation(q0, q1, q2, q3, false);
	}
}
