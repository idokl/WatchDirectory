package com.idoklein.WatchDirectory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

	public static StringBuffer sendRequest(String filename, double duration) {
		URL url;
		try {
			url = new URL("http://localhost:8080/transcribe");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("filename", filename);
			parameters.put("duration", String.valueOf(duration));

			con.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
			out.flush();
			out.close();

			int status = con.getResponseCode();
			Reader streamReader = null;
			 
			if (status > 299) {
			    streamReader = new InputStreamReader(con.getErrorStream());
			} else {
			    streamReader = new InputStreamReader(con.getInputStream());
			}
			
			BufferedReader in = new BufferedReader(streamReader);
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			con.disconnect();
			//System.out.println(content.toString());
			return content;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static class ParameterStringBuilder {
		public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
			StringBuilder result = new StringBuilder();

			for (Map.Entry<String, String> entry : params.entrySet()) {
				result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				result.append("&");
			}

			String resultString = result.toString();
			return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
		}
	}

}
