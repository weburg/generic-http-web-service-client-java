package com.weburg.ghowst;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HttpWebServiceInvoker {
    private static String getEntityName(String name, String verb) {
        return name.substring(verb.length(), name.length()).toLowerCase();
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
        List<NameValuePair> parameters;
        String json;
        Gson gson;
        Object argument;
        String id = "";

        switch (verb) {
            case "get":
                client = new DefaultHttpClient();

                HttpGet getRequest = new HttpGet(baseUrl + "/" + entity +
                        (arguments != null ? "?id=" + URLEncoder.encode(arguments[0].toString(), "UTF-8") : ""));
                HttpResponse getResponse = client.execute(getRequest);

                json = new String(IOUtils.toByteArray(getResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
            case "create":
                client = new DefaultHttpClient();

                argument = arguments[0];
                fields = arguments[0].getClass().getDeclaredFields();

                // TODO Detect when File is present ahead of time, and do multipart only then

                //parameters = new ArrayList<>();
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                for (Field field : fields) {
                    field.setAccessible(true);

                    if (field.get(argument) instanceof File) {
                        multipartEntityBuilder.addBinaryBody(field.getName(), (File) field.get(argument));
                    } else {
                        multipartEntityBuilder.addTextBody(field.getName(), field.get(argument).toString());
                    }
                }

                HttpPost postRequest = new HttpPost(baseUrl + "/" + entity);
                //multipartEntityBuilder.setParameters(parameters);
                //postRequest.setEntity(new UrlEncodedFormEntity(urlParameters));
                postRequest.setEntity(multipartEntityBuilder.build());
                HttpResponse postResponse = client.execute(postRequest);
                json = new String(IOUtils.toByteArray(postResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
            case "createOrReplace":
                // TODO support Files

                client = new DefaultHttpClient();

                argument = arguments[0];
                fields = arguments[0].getClass().getDeclaredFields();
                parameters = new ArrayList<>();
                for (Field field : fields) {
                    field.setAccessible(true);
                    parameters.add(new BasicNameValuePair(field.getName(), field.get(argument).toString()));

                    if (field.getName().equals("id")) {
                        id = field.get(argument).toString();
                    }
                }

                HttpPut putRequest = new HttpPut(baseUrl + "/" + entity + "?id=" + URLEncoder.encode(id, "UTF-8"));
                putRequest.setEntity(new UrlEncodedFormEntity(parameters));
                HttpResponse putResponse = client.execute(putRequest);
                json = new String(IOUtils.toByteArray(putResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
            case "update":
                // TODO support Files

                client = new DefaultHttpClient();

                // TODO does this update only fields passed, or does it overwrite everything not passed?
                argument = arguments[0];
                fields = argument.getClass().getDeclaredFields();
                parameters = new ArrayList<>();
                for (Field field : fields) {
                    field.setAccessible(true);
                    parameters.add(new BasicNameValuePair(field.getName(), field.get(argument).toString()));

                    if (field.getName().equals("id")) {
                        id = field.get(argument).toString();
                    }
                }

                HttpPatch patchRequest = new HttpPatch(baseUrl + "/" + entity + "?id=" + URLEncoder.encode(id, "UTF-8"));
                patchRequest.setEntity(new UrlEncodedFormEntity(parameters));
                HttpResponse patchResponse = client.execute(patchRequest);
                json = new String(IOUtils.toByteArray(patchResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
            case "delete":
                client = new DefaultHttpClient();

                HttpDelete deleteRequest = new HttpDelete(baseUrl + "/" + entity + "?id=" + URLEncoder.encode(arguments[0].toString(), "UTF-8"));
                HttpResponse deleteResponse = client.execute(deleteRequest);

                json = new String(IOUtils.toByteArray(deleteResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
            default:
                // POST to a custom verb resource

                // TODO support Files

                client = new DefaultHttpClient();

                argument = arguments[0];
                fields = arguments[0].getClass().getDeclaredFields();
                parameters = new ArrayList<>();
                for (Field field : fields) {
                    field.setAccessible(true);
                    parameters.add(new BasicNameValuePair(field.getName(), field.get(argument).toString()));
                    // TODO test and handle more than just int id input. Also, this loop passes lots of fields right now!
                }

                HttpPost postCustomRequest = new HttpPost(baseUrl + "/" + entity + "/" + verb
                        + (arguments.length > 0 ? "?id=" + URLEncoder.encode(arguments[0].toString(), "UTF-8") : ""));
                postCustomRequest.setEntity(new UrlEncodedFormEntity(parameters));
                HttpResponse postCustomResponse = client.execute(postCustomRequest);
                json = new String(IOUtils.toByteArray(postCustomResponse.getEntity().getContent()));
                gson = new Gson();

                return gson.fromJson(json, method.getGenericReturnType());
        }
    }
}
