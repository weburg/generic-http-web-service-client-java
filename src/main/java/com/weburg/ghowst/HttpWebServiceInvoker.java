package com.weburg.ghowst;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HttpWebServiceInvoker {
    private static final Logger LOGGER = Logger.getLogger(HttpWebServiceInvoker.class.getName());

    private static String getResourceName(String name, String verb) {
        return name.substring(verb.length()).toLowerCase();
    }

    private static String generateQs(Object[] arguments, Parameter[] parameterDefinitions) {
        String qs = "";

        if (arguments != null && arguments.length > 0) {
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

    private static Object executeAndHandle(HttpRequestBase request, Method method) throws IOException {
        HttpClient client = new DefaultHttpClient();
        request.addHeader("accept", "application/json");
        HttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() >= 400 || response.getStatusLine().getStatusCode() < 200) {
            throw new HttpWebServiceException(response.getStatusLine().getStatusCode(), response.getHeaders("x-error-message")[0].getValue());
        } else if (response.getStatusLine().getStatusCode() >= 300 && response.getStatusLine().getStatusCode() < 400) {
            throw new HttpWebServiceException(response.getStatusLine().getStatusCode(), response.getHeaders("location")[0].getValue());
        }
        String json = new String(IOUtils.toByteArray(response.getEntity().getContent()));
        Gson gson = new Gson();

        return gson.fromJson(json, method.getGenericReturnType());
    }

    private static HttpEntity httpEntityFromArguments(Parameter[] parameterDefinitions, Object[] arguments) throws IllegalAccessException, UnsupportedEncodingException {
        List<NameValuePair> parameters = new ArrayList<>();

        boolean hasFile = false;

        for (Object argument : arguments) {
            Field[] fields = argument.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);

                if (field.get(argument) instanceof File) {
                    hasFile = true;
                    break;
                }
            }
        }

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].getClass().getName().startsWith("java.lang")) {
                if (!hasFile) {
                    parameters.add(new BasicNameValuePair(parameterDefinitions[i].getName(), arguments[i].toString()));
                } else {
                    if (arguments[i] instanceof File) {
                        multipartEntityBuilder.addBinaryBody(parameterDefinitions[i].getName(), (File) arguments[i]);
                    } else {
                        multipartEntityBuilder.addTextBody(parameterDefinitions[i].getName(), arguments[i].toString());
                    }
                }
            } else {
                String objectName = parameterDefinitions[i].getName();
                Field[] fields = arguments[i].getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);

                    if (!hasFile) {
                        parameters.add(new BasicNameValuePair(objectName + '.' + field.getName(), field.get(arguments[i]).toString()));
                    } else {
                        if (field.get(arguments[i]) instanceof File) {
                            multipartEntityBuilder.addBinaryBody(objectName + '.' + field.getName(), (File) field.get(arguments[i]));
                        } else {
                            multipartEntityBuilder.addTextBody(objectName + '.' + field.getName(), field.get(arguments[i]).toString());
                        }
                    }
                }
            }
        }

        if (!hasFile) {
            return new UrlEncodedFormEntity(parameters);
        } else {
            return multipartEntityBuilder.build();
        }
    }

    public Object invoke(Method method, Object[] arguments, String baseUrl) {
        String verb, resource;

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

        resource = getResourceName(methodName, verb);

        LOGGER.info("Verb: " + verb);
        LOGGER.info("Resource: " + resource);

        HttpEntity httpEntity;

        try {
            switch (verb) {
                case "get":
                    HttpGet getRequest = new HttpGet(baseUrl + "/" + resource + generateQs(arguments, method.getParameters()));

                    return executeAndHandle(getRequest, method);
                case "create":
                    HttpPost postRequest = new HttpPost(baseUrl + "/" + resource);

                    httpEntity = httpEntityFromArguments(method.getParameters(), arguments);
                    postRequest.setEntity(httpEntity);

                    return executeAndHandle(postRequest, method);
                case "createOrReplace":
                    HttpPut putRequest = new HttpPut(baseUrl + "/" + resource);

                    httpEntity = httpEntityFromArguments(method.getParameters(), arguments);
                    putRequest.setEntity(httpEntity);

                    return executeAndHandle(putRequest, method);
                case "update":
                    HttpPatch patchRequest = new HttpPatch(baseUrl + "/" + resource);

                    httpEntity = httpEntityFromArguments(method.getParameters(), arguments);
                    patchRequest.setEntity(httpEntity);

                    return executeAndHandle(patchRequest, method);
                case "delete":
                    HttpDelete deleteRequest = new HttpDelete(baseUrl + "/" + resource + generateQs(arguments, method.getParameters()));

                    return executeAndHandle(deleteRequest, method);
                default:
                    // POST to a custom verb resource
                    HttpPost postCustomRequest = new HttpPost(baseUrl + "/" + resource + "/" + verb);

                    httpEntity = httpEntityFromArguments(method.getParameters(), arguments);
                    postCustomRequest.setEntity(httpEntity);

                    return executeAndHandle(postCustomRequest, method);
            }
        } catch (HttpWebServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpWebServiceException(0, "There was a problem processing the web service request: " + e.getMessage());
        }
    }
}