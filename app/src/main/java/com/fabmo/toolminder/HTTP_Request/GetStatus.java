package com.fabmo.toolminder.HTTP_Request;

import java.net.URL;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.fabmo.toolminder.Device;
import com.fabmo.toolminder.Status;
import android.os.AsyncTask;

public class GetStatus extends AsyncTask<URL, Integer, Status>{
	public Device my_device;
	public GetStatus(Device device) {
		my_device=device;
	}

	@Override
	protected com.fabmo.toolminder.Status doInBackground(URL... urls) {
		com.fabmo.toolminder.Status return_status = null;
	    int count = urls.length;

        // This will download stuff from each URL passed in
        for (int i = 0; i < count; i++) {	
			Scanner scanner = null;
			JSONObject jsonStatus = null;
			try {
				jsonStatus = new JSONObject("{'status':{'state':'disconnected'}}");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try{
			URL statusRequest = urls[i];
			scanner = new Scanner(statusRequest.openStream());
			String response = scanner.useDelimiter("\\Z").next();
			jsonStatus = new JSONObject(response);
			scanner.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			String state = "disconnected";
			double posx = 0;
			double posy = 0;
			double posz = 0;
		    String current_file = null;
		    int nb_lines = 0;
		    int line = 0;
			try {
                state = jsonStatus.getJSONObject("data").getJSONObject("status").getString("state");
			} catch (JSONException e) {}
			try {
				posx = jsonStatus.getJSONObject("data").getJSONObject("status").getDouble("posx");
			} catch (JSONException e) {}
			try {
				posy = jsonStatus.getJSONObject("data").getJSONObject("status").getDouble("posy");
			} catch (JSONException e) {}
			try {
				posz = jsonStatus.getJSONObject("data").getJSONObject("status").getDouble("posz");
			} catch (JSONException e) {}
			try {
				current_file = jsonStatus.getJSONObject("data").getJSONObject("status").getString("current_file");
			} catch (JSONException e) {}
			try {
				nb_lines = jsonStatus.getJSONObject("data").getJSONObject("status").getInt("nb_lines");
			} catch (JSONException e) {}
			try {
				line = jsonStatus.getJSONObject("data").getJSONObject("status").getInt("line");
			} catch (JSONException e) {}
			if(current_file != null && current_file.compareTo("null")!=0)
				return_status = new com.fabmo.toolminder.Status(state, posx, posy, posz, current_file, nb_lines, line);
			else
				return_status = new com.fabmo.toolminder.Status(state, posx, posy, posz);
        }
        return return_status;
	}
	
	@Override
	protected void onPostExecute(com.fabmo.toolminder.Status result) {
		my_device.getStatusCallback(result);
	}

}
