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

    public Object invoke(Method method, Object[] arguments, String baseUrl) {
        String verb, entity;

        String methodName = method.getName();

        if (methodName.indexOf("get") == 0) {
            verb = "get";
        } else if (methodName.indexOf("createOrReplace") == 0) {
            verb = "createOrReplace";
        } else if (methodName.indexOf("create") == 0) {
            verb = "create";
        } else if (methodName.indexOf("update") == 0) {
            verb = "update";
        } else if (methodName.indexOf("delete") == 0) {
            verb = "delete";
        } else {
            String lcFirstCharOfMethodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);

            String[] parts = lcFirstCharOfMethodName.split("(?=[A-Z])");

            verb = parts[0].toLowerCase();
        }

        entity = getEntityName(methodName, verb);

        System.out.println("Verb: " + verb);
        System.out.println("Entity: " + entity);

        HttpClient client;
        Parameter[] parameterDefinitions;
        Field[] fields;
        List<NameValuePair> parameters = new ArrayList<>();
        String json;
        Gson gson;

        try {
            switch (verb) {
                case "get":
                    client = new DefaultHttpClient();

                    HttpGet getRequest = new HttpGet(baseUrl + "/" + entity + generateQs(arguments, method));
                    getRequest.addHeader("accept", "application/json");
                    HttpResponse getResponse = client.execute(getRequest);
                    if (getResponse.getStatusLine().getStatusCode() >= 400 || getResponse.getStatusLine().getStatusCode() < 200) {
                        throw new HttpWebServiceException(getResponse.getStatusLine().getStatusCode(), getResponse.getHeaders("x-error-message")[0].getValue());
                    } else if (getResponse.getStatusLine().getStatusCode() >= 300 && getResponse.getStatusLine().getStatusCode() < 400) {
                        throw new HttpWebServiceException(getResponse.getStatusLine().getStatusCode(), getResponse.getHeaders("location")[0].getValue());
                    }
                    json = new String(IOUtils.toByteArray(getResponse.getEntity().getContent()));
                    gson = new Gson();

                    return gson.fromJson(json, method.getGenericReturnType());
                case "create":
                    client = new DefaultHttpClient();

                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                    multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                    parameterDefinitions = method.getParameters();

                    for (int i = 0; i < arguments.length; i++) {
                        String objectName = parameterDefinitions[i].getName();
                        fields = arguments[i].getClass().getDeclaredFields();

                        // TODO Detect when File is present ahead of time, and do multipart only then

                        for (Field field : fields) {
                            field.setAccessible(true);

                            if (field.get(arguments[i]) instanceof File) {
                                multipartEntityBuilder.addBinaryBody(objectName + '.' + field.getName(), (File) field.get(arguments[i]));
                            } else {
                                multipartEntityBuilder.addTextBody(objectName + '.' + field.getName(), field.get(arguments[i]).toString());
                            }
                        }
                    }

                    HttpPost postRequest = new HttpPost(baseUrl + "/" + entity);
                    postRequest.addHeader("accept", "application/json");
                    postRequest.setEntity(multipartEntityBuilder.build());
                    HttpResponse postResponse = client.execute(postRequest);
                    if (postResponse.getStatusLine().getStatusCode() >= 400 || postResponse.getStatusLine().getStatusCode() < 200) {
                        throw new HttpWebServiceException(postResponse.getStatusLine().getStatusCode(), postResponse.getHeaders("x-error-message")[0].getValue());
                    } else if (postResponse.getStatusLine().getStatusCode() >= 300 && postResponse.getStatusLine().getStatusCode() < 400) {
                        throw new HttpWebServiceException(postResponse.getStatusLine().getStatusCode(), postResponse.getHeaders("location")[0].getValue());
                    }
                    json = new String(IOUtils.toByteArray(postResponse.getEntity().getContent()));
                    gson = new Gson();

                    return gson.fromJson(json, method.getGenericReturnType());
                case "createOrReplace":
                    // TODO support Files

                    client = new DefaultHttpClient();

                    parameterDefinitions = method.getParameters();

                    for (int i = 0; i < arguments.length; i++) {
                        String objectName = parameterDefinitions[i].getName();
                        fields = arguments[i].getClass().getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            parameters.add(new BasicNameValuePair(objectName + '.' + field.getName(), field.get(arguments[i]).toString()));
                        }
                    }

                    HttpPut putRequest = new HttpPut(baseUrl + "/" + entity);
                    putRequest.addHeader("accept", "application/json");
                    putRequest.setEntity(new UrlEncodedFormEntity(parameters));
                    HttpResponse putResponse = client.execute(putRequest);
                    if (putResponse.getStatusLine().getStatusCode() >= 400 || putResponse.getStatusLine().getStatusCode() < 200) {
                        throw new HttpWebServiceException(putResponse.getStatusLine().getStatusCode(), putResponse.getHeaders("x-error-message")[0].getValue());
                    } else if (putResponse.getStatusLine().getStatusCode() >= 300 && putResponse.getStatusLine().getStatusCode() < 400) {
                        throw new HttpWebServiceException(putResponse.getStatusLine().getStatusCode(), putResponse.getHeaders("location")[0].getValue());
                    }
                    json = new String(IOUtils.toByteArray(putResponse.getEntity().getContent()));
                    gson = new Gson();

                    return gson.fromJson(json, method.getGenericReturnType());
                case "update":
                    // TODO support Files

                    client = new DefaultHttpClient();

                    parameterDefinitions = method.getParameters();

                    for (int i = 0; i < arguments.length; i++) {
                        String objectName = parameterDefinitions[i].getName();
                        fields = arguments[i].getClass().getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            parameters.add(new BasicNameValuePair(objectName + '.' + field.getName(), field.get(arguments[i]).toString()));
                        }
                    }

                    HttpPatch patchRequest = new HttpPatch(baseUrl + "/" + entity);
                    patchRequest.addHeader("accept", "application/json");
                    patchRequest.setEntity(new UrlEncodedFormEntity(parameters));
                    HttpResponse patchResponse = client.execute(patchRequest);
                    if (patchResponse.getStatusLine().getStatusCode() >= 400 || patchResponse.getStatusLine().getStatusCode() < 200) {
                        throw new HttpWebServiceException(patchResponse.getStatusLine().getStatusCode(), patchResponse.getHeaders("x-error-message")[0].getValue());
                    } else if (patchResponse.getStatusLine().getStatusCode() >= 300 && patchResponse.getStatusLine().getStatusCode() < 400) {
                        throw new HttpWebServiceException(patchResponse.getStatusLine().getStatusCode(), patchResponse.getHeaders("location")[0].getValue());
                    }
                    json = new String(IOUtils.toByteArray(patchResponse.getEntity().getContent()));
                    gson = new Gson();

                    return gson.fromJson(json, method.getGenericReturnType());
                case "delete":
                    client = new DefaultHttpClient();

                    HttpDelete deleteRequest = new HttpDelete(baseUrl + "/" + entity + generateQs(arguments, method));
                    deleteRequest.addHeader("accept", "application/json");
                    HttpResponse deleteResponse = client.execute(deleteRequest);
                    if (deleteResponse.getStatusLine().getStatusCode() >= 400 || deleteResponse.getStatusLine().getStatusCode() < 200) {
                        throw new HttpWebServiceException(deleteResponse.getStatusLine().getStatusCode(), deleteResponse.getHeaders("x-error-message")[0].getValue());
                    } else if (deleteResponse.getStatusLine().getStatusCode() >= 300 && deleteResponse.getStatusLine().getStatusCode() < 400) {
                        throw new HttpWebServiceException(deleteResponse.getStatusLine().getStatusCode(), deleteResponse.getHeaders("location")[0].getValue());
                    }
                    json = new String(IOUtils.toByteArray(deleteResponse.getEntity().getContent()));
                    gson = new Gson();

                    return gson.fromJson(json, method.getGenericReturnType());
                default:
                    // POST to a custom verb resource

                    // TODO support Files

                    client = new DefaultHttpClient();

                    parameterDefinitions = method.getParameters();

                    for (int i = 0; i < arguments.length; i++) {
                        if (arguments[i].getClass().getName().startsWith("java.lang")) {
                            parameters.add(new BasicNameValuePair(parameterDefinitions[i].getName(), arguments[i].toString()));
                        } else {
                            String objectName = parameterDefinitions[i].getName();
                            fields = arguments[i].getClass().getDeclaredFields();
                            for (Field field : fields) {
                                field.setAccessible(true);
                                parameters.add(new BasicNameValuePair(objectName + '.' + field.getName(), field.get(arguments[i]).toString()));
                            }
                        }
                    }

                    HttpPost postCustomRequest = new HttpPost(baseUrl + "/" + entity + "/" + verb);
                    postCustomRequest.addHeader("accept", "application/json");
                    postCustomRequest.setEntity(new UrlEncodedFormEntity(parameters));
                    HttpResponse postCustomResponse = client.execute(postCustomRequest);
                    if (postCustomResponse.getStatusLine().getStatusCode() >= 400 || postCustomResponse.getStatusLine().getStatusCode() < 200) {
                        throw new HttpWebServiceException(postCustomResponse.getStatusLine().getStatusCode(), postCustomResponse.getHeaders("x-error-message")[0].getValue());
                    } else if (postCustomResponse.getStatusLine().getStatusCode() >= 300 && postCustomResponse.getStatusLine().getStatusCode() < 400) {
                        throw new HttpWebServiceException(postCustomResponse.getStatusLine().getStatusCode(), postCustomResponse.getHeaders("location")[0].getValue());
                    }
                    json = new String(IOUtils.toByteArray(postCustomResponse.getEntity().getContent()));
                    gson = new Gson();

                    return gson.fromJson(json, method.getGenericReturnType());
            }
        } catch (HttpWebServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpWebServiceException(0, "There was a problem processing the web service request: " + e.getMessage());
        }
    }
}
