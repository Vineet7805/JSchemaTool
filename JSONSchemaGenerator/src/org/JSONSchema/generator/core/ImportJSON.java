package org.JSONSchema.generator.core;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ImportJSON {
	public static String jsonWorkspace="";
	public static String identifier="-~-~-~-010-~-~-~";
	public static Map<String, Object> schemaMap;
	
	public ImportJSON() {
		// TODO Auto-generated constructor stub
		schemaMap=new HashMap<String, Object>();
	}

 	public static void main(String[] args) throws Exception {
		//load("D:\\nvme\\data\\Sample.JSON");
		loadSchema("D:\\nvme\\data\\SampleSchema.JSON");
		System.out.println(jsonWorkspace);
	}
 	
 	
	
	public static String beautify(String jsonStr) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(jsonStr);
		String prettyJsonString = gson.toJson(je);
		return prettyJsonString;
	}
	
	public static String[] loadPayload(String fileName) throws Exception {
		Object obj = new JSONParser().parse(new FileReader(fileName)); 
        JSONObject jo = (JSONObject) obj;
        String org=jo.toJSONString();
        jsonWorkspace="";
        generateJSONSchema(jo,1);
        jsonWorkspace="{\n"+jsonWorkspace.replaceAll(","+identifier, "")+"\n}";
        jsonWorkspace=jsonWorkspace.replaceAll(identifier, "");
        //Object schemaObj = new JSONParser().parse(schema);
        //jo = (JSONObject) schemaObj;
        //System.out.println(schema);
        return new String[] {beautify(jsonWorkspace),beautify(org)};
	}
	
	public static String[] loadSchema(String fileName) throws Exception {
		Object obj = new JSONParser().parse(new FileReader(fileName)); 
        JSONObject jo = (JSONObject) obj;
        String org=jo.toJSONString();
        if(jo.get("type")!=null)
        	jo=(JSONObject)(new JSONParser().parse("{\"_root\":"+org+"}"));
        jsonWorkspace="";
        schemaMap=generateJsonPayload(jo,1,"object");
        jsonWorkspace="{\n"+jsonWorkspace.replaceAll(","+identifier, "")+"\n}";
        jsonWorkspace=jsonWorkspace.replaceAll(identifier, "");
        //Object schemaObj = new JSONParser().parse(schema);
        //jo = (JSONObject) schemaObj;
        //System.out.println(schema);
        //return null;
        return new String[] {beautify(org),beautify(jsonWorkspace)};
	}
	
	public static String[] generatePayload(String schemaStr) throws Exception {
		Object obj = new JSONParser().parse(schemaStr); 
        JSONObject jo = (JSONObject) obj;
        String org=jo.toJSONString();
        if(jo.get("type")!=null)
        	jo=(JSONObject)(new JSONParser().parse("{\"_root\":"+org+"}"));
        jsonWorkspace="";
        //schemaMap.clear();
        schemaMap=generateJsonPayload(jo,1,"object");
        jsonWorkspace="{\n"+jsonWorkspace.replaceAll(","+identifier, "")+"\n}";
        jsonWorkspace=jsonWorkspace.replaceAll(identifier, "");
        //Object schemaObj = new JSONParser().parse(schema);
        //jo = (JSONObject) schemaObj;
        //System.out.println(schema);
        //return null;
        return new String[] {beautify(org),beautify(jsonWorkspace)};
	}
	
	public static String[] generateSchema(String jStr) throws Exception {
		Object obj = new JSONParser().parse(jStr); 
		String org="";
		JSONObject jo=null;
		if(obj instanceof JSONArray) {
        	org=((JSONArray) obj).toJSONString();
        	String json="{\"_root\":"+org+"}";
        	obj = new JSONParser().parse(json);
        	jo= (JSONObject) obj;
        }else {
        	jo= (JSONObject) obj;
        	org=jo.toJSONString();
        }
        jsonWorkspace="";
        generateJSONSchema(jo,1);
        jsonWorkspace="{\n"+jsonWorkspace.replaceAll(","+identifier, "")+"\n}";
        jsonWorkspace=jsonWorkspace.replaceAll(identifier, "");
        //Object schemaObj = new JSONParser().parse(schema);
        //jo = (JSONObject) schemaObj;
        //System.out.println(jsonWorkspace);
        return new String[] {beautify(jsonWorkspace),beautify(org)};
	}
	
	private static void appendln(String data) {
		jsonWorkspace+=data+"\n";
		//System.out.println(data.replaceAll(","+identifier, "").replaceAll(identifier, ""));
	}
	
	private static void append(String data) {
		jsonWorkspace+=data;
		//System.out.print(data.replaceAll(","+identifier, "").replaceAll(identifier, ""));
	}
	
	public static String padding(int num) {
		String pad="";
		for(int pa=0;pa<=num;pa++)
    		pad+="  ";
		return pad;
	}
	
	private static Map<String, Object> generateJsonPayload(JSONObject jo, int root,String parentType) throws Exception {
		if(jo==null)
			return new HashMap<String, Object>();
		Object[] keys=jo.keySet().toArray();
		Map<String, Object> localMap=new HashMap<String, Object>();
        for (Object object : keys) {
        	Map<String, Object> dataMap=null;
        	String key=object.toString();
        	JSONObject keyObj=(JSONObject)jo.get(key);
        	String type=(String) keyObj.get("type");
        	String desc=(String) keyObj.get("description");
        	String title=(String) keyObj.get("title");
        	String example=(String) keyObj.get("example");
        	String enm=(keyObj.get("enum")+"").replace("null", "").trim();
        	if(enm.length()>=2) {
        		if(enm.endsWith("]"))
            		enm=enm.substring(0, enm.length()-1);
	        	if(enm.startsWith("["))
	        		enm=enm.replaceFirst("\\[", "");
        	}
        	
        	if(type==null)
        		throw new Exception("Schema type missing in one of the spec.");
        	if(type.equalsIgnoreCase("object")) {
        		appendln("\n"+padding(root)+"\""+key+"\" : {");
        		//schemaMap.put(key, value);
        		dataMap=generateJsonPayload((JSONObject)keyObj.get("properties"), root+1,"object");
        		dataMap.put("type","object");
        		dataMap.put("title",title);
        		dataMap.put("description",desc);
        		localMap.put(key, dataMap);
        		append(padding(root)+"},");
        	}
        	else if(type.equalsIgnoreCase("array")) {
        		appendln("\n"+padding(root)+"\""+key+"\" : [{");
        		dataMap=generateJsonPayload((JSONObject)((JSONObject)keyObj.get("items")).get("properties"), root+1,"array");
        		dataMap.put("type","array");
        		dataMap.put("title",title);
        		dataMap.put("description",desc);
        		localMap.put(key, dataMap);
        		append(padding(root)+"}],");
        	}else {
        		if(parentType.equals("array"))
        			append(padding(root)+"\""+key+"\":");
        		else
        			append(padding(root)+"\""+key+"\":");
        		dataMap=new HashMap<String, Object>();
        		dataMap.put("type",type);
        		dataMap.put("title",title);
        		dataMap.put("description",desc);
        		dataMap.put("enum",enm);
        		dataMap.put("example",example);
        		localMap.put(key, dataMap);
        		String val="\"Blabla\"";
        		if(example!=null && example.trim().length()>0){
        			val=example;
        		}else
        		switch (type) {
				case "integer":
					val="\"1\"";
					break;
				case "number":
					val="\"12.1234\"";
					break;
				case "float":
					val="\"1.5\"";
					break;
				case "double":
					val="\"1.02\"";
					break;
				default:
					break;
				}
        		if(parentType.equals("array"))
        			append(padding(root)+val+",");
        		else
        			append(padding(root)+val+",");
        	}
        }
        appendln(identifier);
        return localMap;
	}
	
	private static void generateJSONSchema(JSONObject jo, int root) {
		Object[] keys=jo.keySet().toArray();
        for (Object object : keys) {
        	
        	String key=object.toString();
        	if(jo.get(key) instanceof JSONArray) {
        		appendln("\n"+padding(root)+"\""+key+"\" : {");//Class
        		appendln(padding(root+2)+"\"type\":\"array\",");
        		appendln(padding(root+2)+"\"title\":\"Array of "+key+"\",");
        		appendln(padding(root+2)+"\"description\":\"Description of array of "+key+"\",");
        		appendln(padding(root+2)+"\"items\":{");
        		appendln(padding(root+3)+"\"type\":\"object\",");
        		appendln(padding(root+3)+"\"title\":\"Title for "+key+"\",");
        		appendln(padding(root+3)+"\"description\":\"Description for "+key+"\",");
        		appendln(padding(root+3)+"\"properties\":{");
        		if(((JSONArray)jo.get(key)).size()==0){
        			String val="";
        			JSONObject jsob=new JSONObject();
        			jsob.put("arrayElem", val);
        			generateJSONSchema(jsob,root+4);
        		}else
        		if(!(((JSONArray)jo.get(key)).get(0) instanceof JSONObject) ) {
        			String val=""+((JSONArray)jo.get(key)).get(0);
        			JSONObject jsob=new JSONObject();
        			jsob.put("arrayElem", val);
        			generateJSONSchema(jsob,root+4);
        		}
        		else
        			generateJSONSchema((JSONObject) ((JSONArray)jo.get(key)).get(0),root+4);
        		appendln(padding(root+3)+"}");
        		appendln(padding(root+2)+"}");
        		append(padding(root)+"},");
        		//}
			}
        	else if(jo.get(key) instanceof JSONObject) {
        		appendln("\n"+padding(root)+"\""+key+"\" : {");//Class
        		appendln(padding(root+2)+"\"type\":\"object\",");
        		appendln(padding(root+2)+"\"title\":\"Title for "+key+"\",");
        		appendln(padding(root+2)+"\"description\":\"Description for "+key+"\",");
        		appendln(padding(root+2)+"\"properties\":{");
        		generateJSONSchema((JSONObject) jo.get(key),root+3);
        		appendln(padding(root+2)+"}");
        		append(padding(root)+"},");
			}else {
				//property name
				appendln("\n"+padding(root)+"\""+key+"\" : {");//Class
        		appendln(padding(root+2)+"\"type\":\""+getType((jo.get(key)+"").replace("null", ""))+"\",");
        		appendln(padding(root+2)+"\"title\":\"Title for "+key+"\",");
        		appendln(padding(root+2)+"\"description\":\"Description for "+key+"\",");
        		appendln(padding(root+2)+"\"example\":\""+((jo.get(key)+"").replace("null", ""))+"\"");
				append(padding(root)+"},");
				//property type
				//appendln(padding(root)+jo.get(key).toString());
			}
		}
        appendln(identifier);
	}
	
	private static String getType(String text) {
		try {
			Integer.parseInt(text);
			return "integer"; 
		} catch (Exception e) {
			try {
				Double.parseDouble(text);
				return "number";
			} catch (Exception e2) {
				return "String";
			}
		}
	}
}
