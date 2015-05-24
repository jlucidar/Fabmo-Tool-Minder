package com.fabmo.toolminder;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

	public ArrayList<Device> dev_list;
	private Timer statusTimer;
	Button rescan_button;
	ListView listDevicesView ;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext =this;
		
		dev_list = Device.ScanWifiNetwork(getApplicationContext());

		setContentView(R.layout.activity_main);
		rescan_button = (Button) findViewById(R.id.button_rescan);
		listDevicesView = (ListView)findViewById(R.id.listDevices);
		
		Device[] devices_to_display = new Device[dev_list.size()];
		for (int i=0;i<dev_list.size();i++)
		{
			devices_to_display[i]=dev_list.get(i);
		}

		StatusReportAdapter myStatusAdapter = new StatusReportAdapter(this,devices_to_display);
		listDevicesView.setAdapter(myStatusAdapter);
		statusTimer = new Timer();
		statusTimer.schedule(new TimerTask() {			
			@Override
			public void run() {
				updateStatus();
			}
			
		}, 0, 100);
		
		
		rescan_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	DetectionService.run_server = false;
            	try {Device.detection_service.socket.close();}catch(Exception ex) {}
        		dev_list = Device.ScanWifiNetwork(getApplicationContext());
            }
        });

		listDevicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView a, View v, int position, long id) {

				//on r�cup�re la HashMap contenant les infos de notre item (titre, description, img)
				Device d = (Device) listDevicesView.getItemAtPosition(position);

				//On cr�� un objet Bundle, c'est ce qui va nous permetre d'envoyer des donn�es � l'autre Activity
				Bundle objetbunble = new Bundle();

				//Cela fonctionne plus ou moins comme une HashMap, on entre une clef et sa valeur en face
				objetbunble.putString("ip", d.chooseInterface().ip_address);
				objetbunble.putInt("port", d.server_port);

				//On cr�� l'Intent qui va nous permettre d'afficher l'autre Activity
				//Attention il va surement falloir que vous modifier le premier param�tre (Tutoriel9_Android.this)
				//Mettez le nom de l'Activity dans la quelle vous �tes actuellement
				Intent intent = new Intent(MainActivity.this, WebBrowserActivity.class);

				//On affecte � l'Intent le Bundle que l'on a cr��
				intent.putExtras(objetbunble);

				//On d�marre l'autre Activity
				startActivityForResult(intent, 0);
			}
		});

	}

	@Override
	protected void onPause() {
		statusTimer.cancel();
		DetectionService.run_server = false;
		try {Device.detection_service.socket.close();}catch(Exception ex) {}
		super.onPause();
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void updateStatus()
	{
	    //This method is called directly by the timer
	    //and runs in the same thread as the timer.
		for(Device dev : dev_list){
			dev.getStatus();
		}
	    //We call the method that will work with the UI
	    //through the runOnUiThread method.
	    this.runOnUiThread(Update_status_on_UI);
	}


	private Runnable Update_status_on_UI = new Runnable() {
	    public void run() {
		Device[] devices_to_display = new Device[dev_list.size()];
		for (int i=0;i<dev_list.size();i++)
			{
				devices_to_display[i]=dev_list.get(i);
			}
			
			StatusReportAdapter myStatusAdapter = new StatusReportAdapter(mContext,devices_to_display);
			listDevicesView.setAdapter(myStatusAdapter);
	    //This method runs in the same thread as the UI.               

	    //Do something to the UI thread here

	    }
	};
	
	
}
