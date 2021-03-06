package com.shagaba.jacksync.operation;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shagaba.jacksync.exception.NoSuchPathException;
import com.shagaba.jacksync.utils.JacksonUtils;

/**
 * This is an implementation of RFC 6902 (JSON Patch) - "replace" operation.
 * 
 * The "replace" operation replaces the value at the target location with a new
 * value. The operation object MUST contain a "value" member whose content
 * specifies the replacement value.
 * 
 * The target location MUST exist for the operation to be successful.
 * 
 * This operation is functionally identical to a "remove" operation for a value,
 * followed immediately by an "add" operation at the same location with the
 * replacement value.
 * 
 * Example:
 * 
 * 1. A JSON Patch document:
 * 
 * { "op": "replace", "path": "/a/b/c", "value": 42 }.
 * 
 * @author Shagaba
 *
 */
public class ReplaceOperation extends PatchPathValueOperation {

	/**
	 * Constructs the replace operation
	 */
	public ReplaceOperation() {
		super();
	}

	/**
	 * Constructs the replace operation
	 * 
	 * @param path the path where the value will be replaced. ('/foo/bar/4')
	 * @param value the value to replace.
	 */
	public ReplaceOperation(JsonPointer path, JsonNode value) {
		super(path, value);
	}

	@Override
	public JsonNode apply(JsonNode sourceJsonNode) {
		JsonNode pathJsonNode = JacksonUtils.locateHeadContainer(sourceJsonNode, path);
		if (pathJsonNode.isArray()) {
			ArrayNode pathArrayNode = (ArrayNode) pathJsonNode;
			int index = JacksonUtils.parseLast(path);
			if (index < 0 || index > pathArrayNode.size()) {
				throw new NoSuchPathException(String.format("No such path index - %s", index));
			}
			pathArrayNode.set(index, value);

		} else {
			ObjectNode pathObjectNode = (ObjectNode) pathJsonNode;
			pathObjectNode.replace(JacksonUtils.lastFieldName(path), value);
		}
		return sourceJsonNode;
	}
}
