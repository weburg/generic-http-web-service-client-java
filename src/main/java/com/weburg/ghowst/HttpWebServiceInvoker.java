package com.weburg.ghowst;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class HttpWebServiceInvoker {
    private static String getEntityName(String name, String verb) {
        return name.substring(verb.length(), name.length()).toLowerCase();
    }

    private static String generateQs(Object[] arguments, Method method) {
        String qs = "";

        if (arguments != null && arguments.length > 0) {
            Parameter[] parameterDefinitions = method.getParameters();

            URIBuilder uriBuilder = new URIBuilder();
            for (int i = 0; i < arguments.length; i++) {
                uriBuilder.addParameter(parameterDefinitions[i].getName(), arguments[i].toString());
            }

            try {
                qs += uriBuilder.build();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        return qs;
    }

    public Object invoke(Method method, Object[] arguments, String baseUrl) throws IOException, IllegalAccessException {
        String verb, entity;

        String methodName = method.getName();

        if (methodName.indexOf("get") == 0) {
            verb = "get";
            entity = getEntityName(methodName, verb);
        } else if (methodName.indexOf("createOrReplace") == 0) {
            verb = "createOrReplace";
            entity = getEntityName(methodName, verb);
        } else if (methodName.indexOf("create") == 0) {
            verb = "create";
            entity = getEntityName(methodName, verb);
        } else if (methodName.indexOf("update") == 0) {
            verb = "update";
            entity = getEntityName(methodName, verb);
        } else if (methodName.indexOf("delete") == 0) {
            verb = "delete";
            entity = getEntityName(methodName, verb);
        } else {
            String lcFirstCharOfMethodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);

            String[] parts = lcFirstCharOfMethodName.split("(?=[A-Z])");

            verb = parts[0].toLowerCase();
            entity = getEntityName(methodName, verb);
        }

        System.out.println("Verb: " + verb);
        System.out.println("Entity: " + entity);

        HttpClient client;
        Field[] fields;
        List<NameValuePair> parameters = new ArrayList<>();
        String json;
        Gson gson;

        switch (verb) {
            case "get":
                client = new DefaultHttpClient();

                HttpGet getRequest = new HttpGet(baseUrl + "/" + entity + generateQs(arguments, method));
                getRequest.addHeader("accept", "application/json");
                HttpResponse getResponse = client.execute(getRequest);

                json = new String(IOUtils.toByteArray(getResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
            case "create":
                client = new DefaultHttpClient();

                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                for (Object argument : arguments) {
                    fields = argument.getClass().getDeclaredFields();

                    // TODO Detect when File is present ahead of time, and do multipart only then

                    for (Field field : fields) {
                        field.setAccessible(true);

                        if (field.get(argument) instanceof File) {
                            multipartEntityBuilder.addBinaryBody(field.getName(), (File) field.get(argument));
                        } else {
                            multipartEntityBuilder.addTextBody(field.getName(), field.get(argument).toString());
                        }
                    }
                }

                HttpPost postRequest = new HttpPost(baseUrl + "/" + entity);
                postRequest.addHeader("accept", "application/json");
                postRequest.setEntity(multipartEntityBuilder.build());
                HttpResponse postResponse = client.execute(postRequest);
                json = new String(IOUtils.toByteArray(postResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
            case "createOrReplace":
                // TODO support Files

                client = new DefaultHttpClient();

                for (Object argument : arguments) {
                    fields = argument.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        parameters.add(new BasicNameValuePair(field.getName(), field.get(argument).toString()));
                    }
                }

                HttpPut putRequest = new HttpPut(baseUrl + "/" + entity);
                putRequest.addHeader("accept", "application/json");
                putRequest.setEntity(new UrlEncodedFormEntity(parameters));
                HttpResponse putResponse = client.execute(putRequest);
                json = new String(IOUtils.toByteArray(putResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
            case "update":
                // TODO support Files

                client = new DefaultHttpClient();

                for (Object argument : arguments) {
                    fields = argument.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        parameters.add(new BasicNameValuePair(field.getName(), field.get(argument).toString()));
                    }
                }

                HttpPatch patchRequest = new HttpPatch(baseUrl + "/" + entity);
                patchRequest.addHeader("accept", "application/json");
                patchRequest.setEntity(new UrlEncodedFormEntity(parameters));
                HttpResponse patchResponse = client.execute(patchRequest);
                json = new String(IOUtils.toByteArray(patchResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
            case "delete":
                client = new DefaultHttpClient();

                HttpDelete deleteRequest = new HttpDelete(baseUrl + "/" + entity + generateQs(arguments, method));
                deleteRequest.addHeader("accept", "application/json");
                HttpResponse deleteResponse = client.execute(deleteRequest);

                json = new String(IOUtils.toByteArray(deleteResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
            default:
                // POST to a custom verb resource

                // TODO support Files, better detection of objects since we might not always be getting int identifier

                client = new DefaultHttpClient();

                Parameter[] parameterDefinitions = method.getParameters();

                for (int i = 0; i < arguments.length; i++) {
                    parameters.add(new BasicNameValuePair(parameterDefinitions[i].getName(), arguments[i].toString()));
                }

                HttpPost postCustomRequest = new HttpPost(baseUrl + "/" + entity + "/" + verb);
                postCustomRequest.addHeader("accept", "application/json");
                postCustomRequest.setEntity(new UrlEncodedFormEntity(parameters));
                HttpResponse postCustomResponse = client.execute(postCustomRequest);
                json = new String(IOUtils.toByteArray(postCustomResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
        }
    }
}
