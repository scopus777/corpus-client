package com.corpus.client.json;

import java.io.IOException;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;

public class Vector3DDeserializer extends JsonDeserializer<Vector3D> {

	@Override
	public Vector3D deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		double x = (Double) ((DoubleNode) node.get("x")).numberValue();
		double y = (Double) ((DoubleNode) node.get("y")).numberValue();
		double z = (Double) ((DoubleNode) node.get("z")).numberValue();

		return new Vector3D(x, y, z);
	}
}
