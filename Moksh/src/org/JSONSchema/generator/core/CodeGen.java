package org.JSONSchema.generator.core;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.JSONSchema.generator.core.ClassMetaData.API;
import org.JSONSchema.generator.core.ClassMetaData.Function;
import org.JSONSchema.generator.core.ClassMetaData.Property;

public class CodeGen {
	public static CodeGen CG = new CodeGen();
	public Map<String, ClassMetaData> classes;
	// public static final String LOCATION = "D:\\nvme\\data\\CodeGen";

	private CodeGen() {
		classes = new HashMap<String, ClassMetaData>();
	}

	public ClassMetaData createEntityClass(String name, String scope,String root) {
		if (scope != null && scope.trim().length() > 0)
			scope = scope.toLowerCase().trim();
		if(!scope.equals("local"))
			root=name;
		if (classes.get(root) != null)
			return classes.get(root);
		ClassMetaData clazz = new ClassMetaData("public", name, "class");
		clazz.addAnnotation("@Data");
		clazz.addImport("import lombok.Data;");
		clazz.addImport("import org.springframework.data.rest.core.annotation.Description;");
		clazz.setScope(scope);
		if ("persist".equals(scope)) {
			clazz.addAnnotation("@Entity");
			Property prop = clazz.addProperty("public", "long", "id", true);
			prop.annotations.add("@Id");
			prop.annotations.add("@JsonIgnore");
			prop.annotations.add("@GeneratedValue(strategy = GenerationType.AUTO)");
			clazz.addImport("import javax.persistence.Entity;");
			clazz.addImport("import javax.persistence.GeneratedValue;");
			clazz.addImport("import javax.persistence.GenerationType;");
			clazz.addImport("import javax.persistence.Id;");
			clazz.addImport("import javax.validation.constraints.NotEmpty;");
			clazz.addImport("import com.fasterxml.jackson.annotation.JsonIgnore;");
			clazz.setModelType("Entity");
			createRepositoryInterface(name);
			createResourceClass(name);
			
		}
		System.out.println(root);
		clazz.setRoot(root);
		System.out.println(clazz.getName());
		classes.put(root, clazz);
		return clazz;
	}

	public ClassMetaData createAPIClass(String name) {
		if (classes.get(name) != null)
			return classes.get(name);
		ClassMetaData clazz = new ClassMetaData("public", name, "class");
		clazz.setModelType("API");
		clazz.setScope("Global");
		ClassMetaData clazzResource =createResourceClass(name);
		clazz.setRoot(name);
		classes.put(name, clazz);
		return clazz;
	}

	public ClassMetaData createRepositoryInterface(String name) {
		String entityClass = name;
		name += "Repository";
		if (classes.get(name) != null)
			return classes.get("name");
		ClassMetaData clazz = new ClassMetaData("public", name, "interface");
		clazz.addExtends("PagingAndSortingRepository<" + entityClass + ", Long>");
		clazz.addAnnotation("@Repository");

		clazz.addImport("import org.springframework.data.repository.PagingAndSortingRepository;");
		clazz.addImport("import org.springframework.stereotype.Repository;");
		clazz.setScope("Global");
		clazz.setRoot(name);
		classes.put(name, clazz);
		return clazz;
	}

	public ClassMetaData createResourceClass(String name) {
		String entityClass = name;
		name += "Resource";
		if (classes.get(name) != null)
			return classes.get("name");
		ClassMetaData clazz = new ClassMetaData("public", name, "class");
		clazz.addAnnotation("@RestController");
		clazz.addAnnotation("@CrossOrigin");
		clazz.addImport("import org.springframework.web.bind.annotation.CrossOrigin;");
		clazz.addImport("import java.net.URI;");
		clazz.addImport("import java.util.List;");
		clazz.addImport("import java.util.Optional;");
		clazz.addImport("import org.springframework.beans.factory.annotation.Autowired;");
		clazz.addImport("import org.springframework.data.jpa.repository.JpaRepository;");
		clazz.addImport("import org.springframework.data.repository.query.Param;");
		clazz.addImport("import org.springframework.http.ResponseEntity;");
		clazz.addImport("import org.springframework.stereotype.Repository;");
		clazz.addImport("import org.springframework.web.bind.annotation.GetMapping;");
		clazz.addImport("import org.springframework.web.bind.annotation.PathVariable;");
		clazz.addImport("import org.springframework.web.bind.annotation.PostMapping;");
		clazz.addImport("import org.springframework.web.bind.annotation.RequestBody;");
		clazz.addImport("import org.springframework.web.bind.annotation.RequestParam;");
		clazz.addImport("import org.springframework.web.bind.annotation.RestController;");
		clazz.addImport("import org.springframework.web.servlet.support.ServletUriComponentsBuilder;");
		clazz.addImport("import org.springframework.data.repository.PagingAndSortingRepository;");
		clazz.addImport("import org.springframework.data.rest.core.annotation.RepositoryRestResource;");
		clazz.addImport("import java.util.Collection;");
		clazz.addImport("import java.util.ArrayList;");
		clazz.setScope("Global");
		clazz.setRoot(name);
		classes.put(name, clazz);
		return clazz;
	}

	public ClassMetaData addListGetMapping(ClassMetaData clazz, String entity) {
		String name = clazz.getName().replace("Resource", "");
		Property prop = clazz.addProperty("private ", entity + "Repository", entity.toLowerCase() + "Repo", false);
		prop.annotations.add("@Autowired");
		Function func = clazz.addFunction("public", "List<" + entity + ">", "get" + entity + "s");

		String path = name.toLowerCase() + "s";
		if (name.equals(entity)) {
			func.annotations.add("@GetMapping(path = \"/" + path + "\")");
			func.codeLines.add("return (List<" + entity + ">)" + prop.name + ".findAll();");
		} else {
			path = path + "/{id}/" + entity.toLowerCase() + "s";
			func.annotations.add("@GetMapping(path = \"/" + path + "\")");
			func.addParam("@PathVariable", "long", "id");
			func.exceptions.add("Exception");
			func.codeLines.add(
					"Optional<" + name + "> " + name.toLowerCase() + "=" + name.toLowerCase() + "Repo.findById(id);");
			func.codeLines.add("if(" + name.toLowerCase() + ".isPresent()){ ");
			func.codeLines.add("List<" + entity + "> " + entity.toLowerCase() + "=" + name.toLowerCase() + ".get().get"
					+ entity + "s();");
			func.codeLines.add("if(" + entity.toLowerCase() + "==null || " + entity.toLowerCase() + ".size()==0)");
			func.codeLines.add("throw new Exception(\"" + entity + " not found\");");
			func.codeLines.add("return (List<" + entity + ">)" + entity.toLowerCase() + "Repo.findAll();\r\n}");
			// func.codeLines.add("return "+entity.toLowerCase()+";\r\n}");
			func.codeLines.add("throw new Exception(\"" + name + " not found\");");
		}
		return clazz;
	}

	public ClassMetaData addGetMapping(ClassMetaData clazz, String entity) {
		String name = clazz.getName().replace("Resource", "");
		Property prop = clazz.addProperty("private ", entity + "Repository", entity.toLowerCase() + "Repo", false);
		prop.annotations.add("@Autowired");
		Function func = clazz.addFunction("public", entity, "get" + entity);
		String path = name.toLowerCase() + "/{id}";
		func.addParam("@PathVariable", "long", "id");
		if (name.equals(entity)) {
			func.annotations.add("@GetMapping(path = \"/" + path + "\")");
			func.exceptions.add("Exception");
			func.codeLines.add("Optional<" + entity + "> " + entity.toLowerCase() + "=" + prop.name + ".findById(id);");
			func.codeLines
					.add("if(" + entity.toLowerCase() + ".isPresent())	return " + entity.toLowerCase() + ".get();");
			func.codeLines.add("throw new Exception(\"" + entity + " not found\");");
		} else {
			func.annotations.add("@GetMapping(path = \"/" + path + "/" + entity.toLowerCase() + "\")");
			func.exceptions.add("Exception");
			func.codeLines.add(
					"Optional<" + name + "> " + name.toLowerCase() + "=" + name.toLowerCase() + "Repo.findById(id);");
			func.codeLines.add("if(" + name.toLowerCase() + ".isPresent()){ ");
			func.codeLines.add(
					entity + " " + entity.toLowerCase() + "_tmp=" + name.toLowerCase() + ".get().get" + entity + "();");
			func.codeLines.add("if(" + entity.toLowerCase() + "_tmp==null)");
			func.codeLines.add("throw new Exception(\"" + entity + " not found\");");
			func.codeLines.add("return " + entity.toLowerCase() + "_tmp;\r\n}");
			func.codeLines.add("throw new Exception(\"" + name + " not found\");");
		}
		return clazz;
	}
	
	
	public ClassMetaData addDeleteMapping(ClassMetaData clazz) {
		String name = clazz.getName().replace("Resource", "");
		String objectName = name.toLowerCase();
		String path = objectName + "s/{id}";
		Property prop = clazz.addProperty("private ", name + "Repository", name.toLowerCase() + "Repo", false);
		prop.annotations.add("@Autowired");
		if (clazz.getFunction("delete" + name) != null)
			return null;

		Function func = clazz.addFunction("public", "void", "detele" + name);
		func.exceptions.add("Exception");

		clazz.addImport("import org.springframework.web.bind.annotation.DeleteMapping;");
		func.annotations.add("@DeleteMapping(path = \"/" + path + "\")");
		func.addParam("@PathVariable", "long", "id");
		func.exceptions.add("Exception");
		func.codeLines.add("Optional<" + name + "> " + name.toLowerCase() + "=" + name.toLowerCase() + "Repo.findById(id);");
		func.codeLines
				.add("if(" + name.toLowerCase() + ".isPresent()) " + name.toLowerCase() + ".get(); else");
		func.codeLines.add("throw new Exception(\"" + name + " not found\");");
		func.codeLines.add(name.toLowerCase() + "Repo.deleteById(id);");
		return clazz;
	}
	
	public ClassMetaData addPutMapping(ClassMetaData clazz) {
		String name = clazz.getName().replace("Resource", "");
		String objectName = name.toLowerCase();
		String path = objectName + "s/{id}";

		Property prop = clazz.addProperty("private ", name + "Repository", name.toLowerCase() + "Repo", false);
		prop.annotations.add("@Autowired");
		if (clazz.getFunction("update" + name) != null)
			return null;

		Function func = clazz.addFunction("public", "ResponseEntity<Object>", "update" + name);
		func.exceptions.add("Exception");

		clazz.addImport("import org.springframework.web.bind.annotation.PutMapping;");
		System.out.println("'Path=\"" + path + "\"'");
		func.annotations.add("@PutMapping(path = \"/" + path + "\")");
		func.addParam("@PathVariable", "long", "id");
		func.addParam("@RequestBody", name, objectName);

		func.codeLines
				.add("Optional<" + name + "> " + name.toLowerCase() + "_persisted=" + name.toLowerCase() + "Repo.findById(id);");
		func.codeLines.add(name + " " + name.toLowerCase() + "_tmp=null;");
		func.codeLines.add("if(" + name.toLowerCase() + "_persisted.isPresent()) " + name.toLowerCase() + "_tmp="
				+ name.toLowerCase() + "_persisted.get();\r\nelse\r\n");
		func.codeLines.add("throw new Exception(\"" + name + " not found\");\r\n");
		List<Property> properties=classes.get(name).getProperties();
		for (Property property : properties) {
			if(property.primitive) {
				String propName=property.name;
				propName=propName.substring(0, 1).toUpperCase()+propName.substring(1);
				func.codeLines.add(name.toLowerCase() + "_tmp.set"+propName+"("+objectName + ".get"+propName+"()"+");");
			}
		}
		func.codeLines.add(objectName + "=" + objectName + "Repo.save(" +name.toLowerCase() + "_tmp);");
		func.codeLines
				.add("URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(\"/{id}\").buildAndExpand("
						+ objectName + ".getId()).toUri();");
		func.codeLines.add("return ResponseEntity.created(location).build();");

		return clazz;
	}

	public ClassMetaData addPostMapping(ClassMetaData clazz, String entity) {
		String name = clazz.getName().replace("Resource", "");
		String entityObject = entity.toLowerCase();
		String objectName = name.toLowerCase();
		String path = objectName + "s";
		String params = "";
		String paramArray[] = classes.get(entity).getRequiredNonPrimitiveProps();
		String clazzReqprops[] = classes.get(name).getRequiredNonPrimitiveProps();
		for (String prop : clazzReqprops) {
			// System.out.println(prop+"=="+entity);
			if (prop.contains(entity)) {
				System.out.println("Path not created: '" + path + "/{id}/" + entityObject + "'");
				return null;
			}
		}
		for (String param : paramArray) {
			if (!param.equals(name))
				params += "\"" + param.toLowerCase() + "Id\",";
		}
		if (params.endsWith(",")) {
			params += ",";
			params = params.replace(",,", "");
		}
		if (params.trim().length() > 0) {
			params = ",params= {" + params + "}";
		}

		Property prop = clazz.addProperty("private ", entity + "Repository", entity.toLowerCase() + "Repo", false);
		prop.annotations.add("@Autowired");
		if (clazz.getFunction("create" + entity) != null)
			return null;

		Function func = clazz.addFunction("public", "ResponseEntity<Object>", "create" + entity);
		func.exceptions.add("Exception");

		if (name.equals(entity)) {
			System.out.println("'Path=\"" + path + "\"" + params + "'");
			func.annotations.add("@PostMapping(path = \"/" + path + "\"" + params + ")");
			func.addParam("@RequestBody", entity, entityObject);
			if (params.trim().length() == 0) {
				func.codeLines.add(entityObject + "=" + entityObject + "Repo.save(" + entityObject + ");");
				func.codeLines.add(
						"URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(\"/{id}\").buildAndExpand("
								+ entityObject + ".getId()).toUri();");
				func.codeLines.add("return ResponseEntity.created(location).build();");
			} else {
				if (paramArray != null) {
					for (String param : paramArray)
						if (!param.equals(name)) {
							prop = clazz.addProperty("private ", param + "Repository ", param.toLowerCase() + "Repo",
									false);
							prop.annotations.add("@Autowired");
							func.addParam("@RequestParam", "long", param.toLowerCase() + "Id");
							func.codeLines.add("Optional<" + param + "> " + param.toLowerCase() + "="
									+ param.toLowerCase() + "Repo.findById(" + param.toLowerCase() + "Id" + ");");
							func.codeLines.add(param + " " + param.toLowerCase() + "_tmp=null;");
							func.codeLines.add("if(" + param.toLowerCase() + ".isPresent()) " + param.toLowerCase()
									+ "_tmp=" + param.toLowerCase() + ".get();\r\nelse\r\n");
							func.codeLines.add("throw new Exception(\"" + param + " not found\");\r\n");
							func.codeLines.add(entityObject + ".set" + param + "(" + param.toLowerCase() + "_tmp);");
						}
					func.codeLines.add(entityObject + "=" + entityObject + "Repo.save(" + entityObject + ");");
					func.codeLines.add("URI uri = URI.create(\"" + entityObject + "/\"+" + entityObject + ".getId());");
					func.codeLines.add("return ResponseEntity.created(uri).build();");
				}
			}
			// System.out.println("Class name:"+clazz.getName());
			// System.out.println(func.toString());
		} else {
			path += "/{id}/" + entityObject;
			System.out.println("'Path=\"/" + path + "\"" + params + "'");
			func.annotations.add("@PostMapping(path = \"/" + path + "\"" + params + ")");
			func.addParam("@RequestBody", entity, entityObject);
			func.addParam("@PathVariable", "long", "id");
			func.codeLines.add(
					"Optional<" + name + "> " + name.toLowerCase() + "=" + name.toLowerCase() + "Repo.findById(id);");
			func.codeLines.add(name + " " + name.toLowerCase() + "_tmp=null;");
			func.codeLines.add("if(" + name.toLowerCase() + ".isPresent()) " + name.toLowerCase() + "_tmp="
					+ name.toLowerCase() + ".get();\r\nelse\r\n");
			func.codeLines.add("throw new Exception(\"" + name + " not found\");\r\n");
			func.codeLines.add(entityObject + ".set" + name + "(" + name.toLowerCase() + "_tmp);");
			if (paramArray != null)
				for (String param : paramArray)
					if (!param.equals(name)) {
						prop = clazz.addProperty("private ", param + "Repository ", param.toLowerCase() + "Repo",
								false);
						prop.annotations.add("@Autowired");
						func.addParam("@RequestParam", "long", param.toLowerCase() + "Id");
						func.codeLines.add("Optional<" + param + "> " + param.toLowerCase() + "=" + param.toLowerCase()
								+ "Repo.findById(" + param.toLowerCase() + "Id" + ");");
						func.codeLines.add(param + " " + param.toLowerCase() + "_tmp=null;");
						func.codeLines.add("if(" + param.toLowerCase() + ".isPresent()) " + param.toLowerCase()
								+ "_tmp=" + param.toLowerCase() + ".get();\r\nelse\r\n");
						func.codeLines.add("throw new Exception(\"" + param + " not found\");\r\n");
						func.codeLines.add(entityObject + ".set" + param + "(" + param.toLowerCase() + "_tmp);");
					}

			func.codeLines.add(entityObject + "=" + entityObject + "Repo.save(" + entityObject + ");");
			func.codeLines.add("URI uri = URI.create(\"" + entityObject + "/\"+" + entityObject + ".getId());");
			func.codeLines.add("return ResponseEntity.created(uri).build();");
			// System.out.println(func.toString());
		}
		return clazz;
	}

	public String generateClasses(String packageName, String folderPath) throws Exception {
		// String packageName=basePackage.getText();
		if (packageName == null || packageName.trim().length() == 0) {
			return ("Please specify base package name like com.example.model");
		}
		Object keys[] = classes.keySet().toArray();
		for (Object key : keys) {
			ClassMetaData clazz = classes.get(key);
			if ("Entity".equals(clazz.getModelType()) && !clazz.getScope().equalsIgnoreCase("local")) {
				//clazz.exportTypeScript(folderPath, packageName);
				ClassMetaData resource = classes.get(clazz.getName() + "Resource");
				addGetMapping(resource, clazz.getName());
				addListGetMapping(resource, clazz.getName());
				addPostMapping(resource, clazz.getName());
				addPutMapping(resource);
				addDeleteMapping(resource);
				List<Property> properties = clazz.getProperties();
				if (properties != null && properties.size() > 0)
					for (Property prop : properties) {
						if (prop.primitive == false && !prop.type.replace("List<", "").replace(">", "")
								.equals(resource.getName().replace("Resource", ""))) {
							if (prop.type.startsWith("List<"))
								addListGetMapping(resource, prop.type.replace("List<", "").replace(">", ""));
							else
								addGetMapping(resource, prop.type);
							addPostMapping(resource, prop.type.replace("List<", "").replace(">", ""));
						}
					}
			}
		}
		
		for (Object key : keys) {
			System.out.println("key:"+key);
			ClassMetaData clazz = classes.get(key);
			if("api".equalsIgnoreCase(clazz.getModelType())) {
				ClassMetaData resource=classes.get(clazz.getName()+"Resource");
				API api=clazz.getAPI();
				Set<String> imports=api.getImportList();
				if(imports!=null)
				for (String imprt : imports) {
					resource.addImport(imprt);
				}
				
				//resource.addProperty(access, type, name, primitive)
				ClassMetaData payload=classes.get(clazz.getName()+".Payload");
				String requestBody="@RequestBody(required = false)";
				if(payload.getProperty("requestpayload").required)
					requestBody="@RequestBody(required = true)";
				
				Function apiActionImpl=resource.addFunction("public", packageName+"."+clazz.getName()+".Payload.ResponsePayload.ResponsePayload", clazz.getName().toLowerCase());
				
				
				apiActionImpl.addParam(requestBody,packageName+"."+clazz.getName()+".Payload.RequestPayload.RequestPayload" , "requestPayload");
				String method=api.method.toLowerCase();
				method=method.substring(0, 1).toUpperCase()+method.substring(1);
				List<Property> queryStringProps= clazz.getProperties();
				ClassMetaData requestheaders=classes.get(clazz.getRoot()+".Headers"+"."+"RequestHeaders");
				
				List<Property> properties=requestheaders.getProperties();
				if(properties!=null && properties.size()>0) {
					resource.addImport("import org.springframework.web.bind.annotation.RequestHeader;");
					requestheaders.addAnnotation("@AllArgsConstructor");
					requestheaders.addImport("import lombok.AllArgsConstructor;");
					requestheaders.addAnnotation("@NoArgsConstructor");
					requestheaders.addImport("import lombok.NoArgsConstructor;");
					for (Property property : properties) {
						apiActionImpl.addParam("@RequestHeader(\""+property.name+"\")", property.type, property.name);
					}
				}
				//@PostMapping(path = "/entrys",params= {"projectId","timesheetId"})
				//@RequestParam long timesheetId
				//@PathVariable long id
				String params="";
				
				api.path=clazz.getProperty("path").value;
				for (Property property : queryStringProps) {
					if(property.primitive && !property.name.equalsIgnoreCase("path")) {
						if(!api.path.contains("{"+property.name+"}")) {
							apiActionImpl.addParam("@RequestParam", property.type, property.name);
							params+="\""+property.name+"\",";
						}else {
							apiActionImpl.addParam("@PathVariable", property.type, property.name);
						}
					}
				}
				if(params.trim().length()>0)
					params=(params+",").replace(",,", "");
				//@RequestHeader("category") String category
				//api.addQueryParam(name, type);
				apiActionImpl.annotations.add("@"+method+"Mapping(path=\""+api.path+"\", params={"+params+"})");
				apiActionImpl.codeLines.add(new String(Base64.getDecoder().decode(api.code)));
				resource.setCustomCode(new String(Base64.getDecoder().decode(api.custom)));
				clazz.exportAPI(folderPath, packageName,classes);
			}	
		}
		
		for (Object key : keys) {
			ClassMetaData clazz = classes.get(key);
			clazz.exportJava(folderPath, packageName);
		}
		// System.out.println(data);
		classes.clear();
		return null;
	}

}
