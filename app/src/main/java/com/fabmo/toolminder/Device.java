package com.fabmo.toolminder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import org.json.*;

import com.fabmo.toolminder.HTTP_Request.GetStatus;

import android.content.Context;
import android.os.SystemClock;

public class Device{

	public String hostname;
	public ArrayList<Net_interface> network;
	public Status status;
	public int server_port;
	static DetectionService detection_service;
	
	public Device(String h,ArrayList<Net_interface> n, int p){
		this.hostname = h;
		this.network = n;
		this.server_port=p;
	}
	
	public static ArrayList<Device> ScanWifiNetwork(Context mContext){
		detection_service = new DetectionService(mContext);
		SystemClock.sleep(2000);
		if (! DetectionService.devices.isEmpty()){
			ArrayList<String> count_array = new ArrayList<String>(); // used for getting a unique device list.
			ArrayList<Device> new_device_array = new ArrayList<Device>(); // new Array, represent the devices that you can connect to.
			String dev_hostname = null;
			int server_port;
			ArrayList<Net_interface> dev_interfaces = new ArrayList<Net_interface>(); // reset the interfaces array for every new device
			try
			{
				for (JSONObject device : DetectionService.devices) {
						count_array.add(device.getJSONObject("device").getString("hostname"));
				}
				//remove duplicate
				HashSet<String> hs = new HashSet<String>();
				hs.addAll(count_array);
				count_array.clear();
				count_array.addAll(hs);
	
				new_device_array = new ArrayList<Device>();

				// new JSON object constructor
				for (String single_dev_hostname : count_array)
				{
					dev_interfaces = new ArrayList<Net_interface>(); // reset the interfaces array for every new device
					dev_hostname = single_dev_hostname; // get the hostname
	
					// get the interface array
					for(JSONObject device : DetectionService.devices) // array with all the ips and net interfaces separately
					{
						if( device.getJSONObject("device").getString("hostname") == dev_hostname ) //select the ones corresponding to the current device
						{
							for (int i=0;i<device.getJSONObject("device").getJSONArray("networks").length();i++)
							{
								JSONArray networks = device.getJSONObject("device").getJSONArray("networks");
								JSONObject network = networks.getJSONObject(i);
								String active_ip = null;
								try{
									active_ip=device.getString("active_ip");
								}catch(Exception ex){
									active_ip=device.getJSONObject("device").getString("active_ip");
								}
								if ( network.getString("ip_address").compareTo(active_ip) == 0 ) //select active interfaces.
									{
									dev_interfaces.add(new Net_interface(network.getString("interface"), network.getString("ip_address"))); // add theses to the network section
								}
							}
							server_port = device.getJSONObject("device").getInt("server_port");
							new_device_array.add(new Device(dev_hostname, dev_interfaces,server_port));
						}
						
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
				// add the device to the new_device_array


				/*********************************************************************/
			DetectionService.run_server = false;
			try {
				//detection_service.server.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new_device_array;
		}
		else
			DetectionService.run_server = false;
			try {
				//detection_service.server.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new ArrayList<Device>();
		
	}
	
	public Net_interface chooseInterface()
	{
		
		Net_interface itr = null;
		
		for(Net_interface net : this.network)
		{
			if (net.itr.compareTo("usb0") == 0) {
				itr = net;
				return net;
			}
		}
		for(Net_interface net : this.network)
		{
			if (net.itr.compareTo("eth0") == 0) {
				itr = net;
				return net;
			}
		}
		for(Net_interface net : this.network) {
		
			if (net.itr.compareTo("wlan0") == 0) {
				itr = net;
				return net;
			}
		}
		for(Net_interface net : this.network)
		{
			if (net.itr.compareTo("wlan1") == 0) {
				itr = net;
				return net;
			}
		}
		for(Net_interface net : this.network)
		{
			itr = net;
			return net;
		}
		return itr;
	}
	
	public void getStatus(Net_interface itr, int server_port)
	{
		String requestURL = "http://"+itr.ip_address+":"+server_port+"/status";
		GetStatus myBgTask = new GetStatus(this);
		try {
			myBgTask.execute(new URL(requestURL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}
	
	public void getStatus()
	{
		getStatus(this.chooseInterface(),this.server_port);
	}

	public void getStatusCallback(Status result) {
		status = result;
	}
}
