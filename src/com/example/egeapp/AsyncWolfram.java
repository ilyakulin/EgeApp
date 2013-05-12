package com.example.egeapp;

import java.io.StringReader;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;

public class AsyncWolfram extends AsyncTask<Void, Integer, Void> {

	WolframResponse activity;
	String query;
	StringBuilder res;
	ProgressDialog pd;
	String colorOrange = "\"#f5811f\"";
	String colorRed = "\"#d71921\"";
	String[] podNames, podPlaintext;

	public AsyncWolfram(WolframResponse activity, String query) {
		this.activity = activity;
		this.query = query;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (!Assistentnetwork.IsOnline(activity)) {
			Assistentnetwork.showNoConnectionDialog(activity, true);
			this.cancel(true);
			return;
		}

		pd = new ProgressDialog(activity,
				android.R.style.Theme_Holo_Light_Dialog);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setIndeterminate(true);
		pd.setCancelable(false);
		pd.setMessage("Загрузка данных");
		pd.show();
	}
	@Override
	protected Void doInBackground(Void... params) {
		try {

			query = query.replace("_", "");
			query = query.replace("\u2007", "");
			query = URLEncoder.encode(query, "utf-8");

			String urlString = SH.WOLFRAM_API_HREF + query
					+ SH.WOLFRAM_API_FORMAT_OUTPUT + SH.WOLFRAM_API_PODTITLES;

			HttpClient client = AndroidHttpClient
					.newInstance("httpClient_wolfram");
			HttpGet get = new HttpGet(urlString);
			HttpResponse response = null;

			{
				int count = 0;
				do {
					try {
						response = client.execute(get);
						break;
					} catch (Exception e) {
						count++;
						if (count == 3)
							throw new Exception("Error wolfram query");
					}
				} while (true);
			}
			
			String outputString = EntityUtils.toString(response.getEntity());

			res = new StringBuilder();
			int numPods = 0;
			int curPod = 1;

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			while (true) {
				try {
					db.parse(new InputSource(new StringReader(outputString)));
					
					break;
				} catch (SAXException e) {
					e.printStackTrace();
					response = client.execute(get);
					outputString = EntityUtils.toString(response.getEntity());					
				}
			}
			((AndroidHttpClient) client).close();
			
			XmlPullParser p = XmlPullParserFactory.newInstance()
					.newPullParser();
			p.setInput(new StringReader(outputString));

			ParseCycle : while (true) {
				switch (p.getEventType()) {

					case XmlPullParser.END_DOCUMENT :
						break ParseCycle;

					case XmlPullParser.START_TAG : {

						String tagName = p.getName();

						if (tagName.equals("queryresult")) {
							String attrName;
							for (int i = 0; i < p.getAttributeCount(); i++) {
								attrName = p.getAttributeName(i);

								if (attrName.equals("error")) {
									if (p.getAttributeValue(i).equals("true")) {
										res = null;
										return null;
									}
								}

								if (attrName.equals("numpods")) {
									numPods = Integer.valueOf(p
											.getAttributeValue(i));
									if (numPods == 0) {
										res = null;
										return null;
									}
									podNames = new String[numPods+1];
									podNames[0]="Select an answer";
									podPlaintext = new String[numPods];

									break;
								}
							}
						}

						if (tagName.equals("pod")) {
							// get name of pod
							if (curPod == 1) {
								res.append("<h4><font color=" + colorRed + ">"
										+ p.getAttributeValue(0)
										+ "</color></h4>");
							} else {
								res.append("<h5><font color=" + colorOrange
										+ ">" + p.getAttributeValue(0)
										+ "</color></h5>");
							}
							podNames[curPod] = p.getAttributeValue(0);
							int subpods = 0;
							for (int i = 1; i < p.getAttributeCount(); i++) {
								if (p.getAttributeName(i).equals("numsubpods")) {
									subpods = Integer.valueOf(p
											.getAttributeValue(i));
									break;
								}

							}

							for (int i = 0; i < subpods; i++) {

								while (p.getName() == null
										|| !((p.getName().equals("plaintext") && p
												.getEventType() == XmlPullParser.START_TAG)))
									p.next();
								p.next();

								String temp = p.getText();
								temp = temp.replace(' ', '\u00B7');
								temp = temp.replace("\u00B7=\u00B7", " = ");
								res.append(temp);
								if (podPlaintext[curPod - 1] == null)
									podPlaintext[curPod - 1] = temp;
								else
									podPlaintext[curPod - 1] += "\n" + temp;

								if (i != subpods - 1)
									res.append("<p>");
							}

							curPod++;
						}

						p.next();
						break;
					}
					default : {
						p.next();
						break;
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (pd.isShowing())
			if (pd.getProgress() != 100) {
				pd.setProgress(100);
				pd.dismiss();
			}
		if (res == null || res.equals("")) {			
			activity.tw.setText(Html.fromHtml("Ошибка в запросе!"));
		} else {
			// set menu visible

			activity.tw.setText(Html.fromHtml(res.toString()));
			activity.adapter = new ArrayAdapter<String>(activity,
					android.R.layout.simple_spinner_item, podNames);
			activity.adapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			activity.bar.setListNavigationCallbacks(activity.adapter, activity);
			activity.podPlaintext = podPlaintext;
			activity.invalidateOptionsMenu();

		}

	}

}
