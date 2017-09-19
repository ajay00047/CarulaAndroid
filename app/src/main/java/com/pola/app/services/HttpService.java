package com.pola.app.services;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.pola.app.Utils.API;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.ErrorCodes;
import com.pola.app.Utils.NetworkHelper;
import com.pola.app.beans.BaseRequestBean;
import com.pola.app.beans.GenericResponseBean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ajay on 05-Sep-17.
 */
public class HttpService {

    public static GenericResponseBean getResponse(String urlStr, BaseRequestBean requestBean, Context context) {

        JSONObject json = null;
        GenericResponseBean response = new GenericResponseBean();
        NetworkHelper netConn = null;
        InputStream is = null;
        OutputStream os=null;
        BufferedWriter writer=null;
        BufferedReader in=null;
        ObjectMapper mapper = new ObjectMapper();
        // Making the HTTP request

        try {
            netConn = new NetworkHelper(context);
            if(!netConn.isNetworkAvailable()){
                Log.e(Constants.LOG_TAG,"No Network");
                response.setErrorCode(ErrorCodes.CODE_777.toString());
                response.setErrorDescription(ErrorCodes.CODE_777.getErrorMessage());
                return response;
            }
            Log.e(Constants.LOG_TAG,"inside JSON Parser");
            URL url =new URL(API.HOSTNAME+urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/json; charset=utf-8");
            conn.setDoOutput(true);

            //convert requestBean to json
            String requestJson="";
            try{
                requestJson = mapper.writeValueAsString(requestBean);
                Log.i(Constants.LOG_TAG,"Request :" +requestJson);
            } catch (Exception e) {
                Log.e(Constants.LOG_TAG, "Error processing request ");
                e.printStackTrace();
                response.setErrorCode(ErrorCodes.CODE_778.toString());
                response.setErrorDescription(ErrorCodes.CODE_778.getErrorMessage());
            }
            os = conn.getOutputStream();
            writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(requestJson);
            writer.flush();

            conn.connect();
            is = conn.getInputStream();

            Log.i(Constants.LOG_TAG,"Request Method :" +conn.getRequestMethod());
            Log.e(Constants.LOG_TAG, is.toString());

            in = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }

            String outPut = sb.toString();
            Log.e(Constants.LOG_TAG, outPut);

            response = mapper.readValue(outPut,response.getClass());

            //json = new JSONObject(outPut);
            //Log.e(Constants.LOG_TAG, json.toString());

            //response = gson.fromJson(json.toString(), GenericResponseBean.class);

        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "Error processing request ");
            e.printStackTrace();
            response.setErrorCode(ErrorCodes.CODE_778.toString());
            response.setErrorDescription(ErrorCodes.CODE_778.getErrorMessage());
        }finally {
            try {
                if(writer != null)
                    writer.close();
                if(os != null)
                    os.close();
                if(is != null)
                    is.close();
            }catch (Exception e){
                Log.e(Constants.LOG_TAG, "Error processing request : Shit happened!");
            }
        }

        return response;

    }
}
