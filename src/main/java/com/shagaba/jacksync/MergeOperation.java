package com.shagaba.jacksync;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shagaba.jacksync.utils.JacksonUtils;

/**
 * This is an implementation Inspired by RFC 6902 (JSON Patch) and RFC 7386 
 * (JSON Merge Patch) - "merge" operation.
 * 
 * A JSON merge patch document describes changes to be made to a target JSON 
 * document using a syntax that closely mimics the document being modified.
 * 
 * o Recipients of a merge patch document determine the exact set of changes 
 * being requested by comparing the content of the provided patch against the 
 * current content of the target document.
 * 
 * o If the provided merge patch contains members that do not appear within 
 * the target, those members are added.
 * 
 * o If the target does contain the member, the value is replaced.
 * 
 * o Null values in the merge patch are given special meaning to indicate the 
 * removal of existing values in the target.
 * 
 * Examples:
 * 
 * 1. A JSON Merge Patch document:
 * 
 * { "op" : "merge", "path" : "/", "value" : { "title" : null, "author" : 
 * { "firstName" : "James" , "lastName" : "Bond" } } }.
 * 
 * @author Shagaba
 *
 */
public class MergeOperation extends PatchPathValueOperation {

    /**
	 * Constructs the merge operation
	 */
	public MergeOperation() {
		super();
	}

	/**
	 * Constructs the merge operation
	 * 
	 * @param path the path where the value will be added. ('/foo/bar/4')
	 * @param value the value to add.
	 */
	public MergeOperation(JsonPointer path, JsonNode value) {
		super(path, value);
	}

	/**
	 * Constructs the merge operation
	 * 
	 * @param path the path where the value will be added. ('/foo/bar/4')
	 * @param value the value to add.
	 */
	public MergeOperation(String path, JsonNode value) {
		super(path, value);
	}

	@Override
	public JsonNode apply(JsonNode objectJsonNode) {
		merge(objectJsonNode, value);
        return objectJsonNode;
      }
	
	/**
	 * 
	 * @param sourceJsonNode
	 * @param elementJsonNode
	 */
	private void merge(JsonNode sourceJsonNode, JsonNode elementJsonNode) {
		JsonNode pathJsonNode = JacksonUtils.locate(sourceJsonNode, path);
 		for (Iterator<Map.Entry<String, JsonNode>> iterator = elementJsonNode.fields() ; iterator.hasNext();) {
     		Map.Entry<String, JsonNode> elementMapEntry = iterator.next();
 			if (pathJsonNode.isArray()) {
 				pathJsonNode = elementJsonNode;
 			} else {
 				ObjectNode sourceObjectNode = (ObjectNode) pathJsonNode;
	     		JsonNode nextValueJsonNode = elementMapEntry.getValue();
	     		JsonNode nextSourceJsonNode = pathJsonNode.get(elementMapEntry.getKey());

	     		if (sourceObjectNode.has(elementMapEntry.getKey())) {
	     			if (!nextValueJsonNode.isMissingNode() && !nextSourceJsonNode.isObject()) {
	     				sourceObjectNode.replace(elementMapEntry.getKey(), elementMapEntry.getValue());
		     			continue;
	     			} 
	     		} else {
	     			sourceObjectNode.replace(elementMapEntry.getKey(), elementMapEntry.getValue());
	     			continue;
	     		}
	 			merge(nextSourceJsonNode, nextValueJsonNode);
			}
		}
	}

}
