/**
 * This file is part of the Nilestore project.
 * 
 * Copyright (C) (2011) Nile University (NU)
 *
 * Nilestore is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eg.nileu.cis.nilestore.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import eg.nileu.cis.nilestore.main.progress.FileBodyProgress;
import eg.nileu.cis.nilestore.main.progress.OutputStreamProgress;
import eg.nileu.cis.nilestore.main.progress.ProgressListener;
import eg.nileu.cis.nilestore.main.progress.ProgressUtils;
import eg.nileu.cis.nilestore.utils.FileUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpDealer.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class HttpDealer {

	/**
	 * Put.
	 * 
	 * @param filepath
	 *            the filepath
	 * @param url
	 *            the url
	 * @throws ClientProtocolException
	 *             the client protocol exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void put(String filepath, String url)
			throws ClientProtocolException, IOException {
		url = url + (url.endsWith("/") ? "" : "/") + "upload?t=json";
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(url);
		MultipartEntity entity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);

		File file = new File(filepath);
		final double filesize = file.length();
		
		ContentBody fbody = new FileBodyProgress(file, new ProgressListener() {
			
			@Override
			public void transfered(long bytes, float rate) {
				int percent = (int) ((bytes / filesize) * 100);
				String bar = ProgressUtils.progressBar("Uploading file to the gateway : ",percent,rate);
				System.err.print("\r"+bar);
				if(percent == 100){
					System.err.println();
					System.err.println("wait the gateway is processing and saving your file");
				}
			}
		});
		
		entity.addPart("File", fbody);

		post.setEntity(entity);

		HttpResponse response = client.execute(post);

		if (response != null) {
			System.err.println(response.getStatusLine());
			HttpEntity ht = response.getEntity();
			String json = EntityUtils.toString(ht);
			JSONParser parser = new JSONParser();
			try {
				JSONObject obj = (JSONObject) parser.parse(json);
				System.out.println(obj.get("cap"));
			} catch (ParseException e) {
				System.err.println("Error during parsing the response: "
						+ e.getMessage());
				System.exit(-1);
			}
		} else {
			System.err.println("Error: response = null");
		}

		client.getConnectionManager().shutdown();
	}

	/**
	 * Gets the.
	 * 
	 * @param url
	 *            the url
	 * @param cap
	 *            the cap
	 * @param downloadDir
	 *            the download dir
	 * @throws ClientProtocolException
	 *             the client protocol exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void get(String url, String cap, String downloadDir)
			throws ClientProtocolException, IOException {
		url = url + (url.endsWith("/") ? "" : "/") + "download/" + cap;
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);

		HttpGet get = new HttpGet(url);

		HttpResponse response = client.execute(get);
		
		if (response != null) {
			System.err.println(response.getStatusLine());
			HttpEntity ht = response.getEntity();
			final double filesize = ht.getContentLength();
			
			FileOutputStream out = new FileOutputStream(FileUtils.JoinPath(
					downloadDir, cap));
			OutputStreamProgress cout = new OutputStreamProgress(out, new ProgressListener() {
				
				@Override
				public void transfered(long bytes,float rate) {
					int percent = (int) ((bytes / filesize) * 100);
					String bar = ProgressUtils.progressBar("Download Progress: ",percent,rate);
					System.out.print("\r"+bar);
				}
			});
			
			ht.writeTo(cout);
			out.close();
			System.out.println();
		} else {
			System.err.println("Error: response = null");
		}

		client.getConnectionManager().shutdown();
	}
	
}
