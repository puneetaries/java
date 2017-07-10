/**
 * 
 */

/**
 * @author Puneet Raj Srivastava
 *
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
 
public class RestClient {
 
	// http://localhost:8080/RESTfulExample/json/product/get
	public static void main(String[] args) {
 
	  try {
 
		  System.getProperties().put("http.proxyHost", "http://www-proxy.ericsson.se:8080/");
		//URL url = new URL("https://ldoapps.com/amsrest/eams/bulk_activities?customer=2");
		URL url = new URL("http://ip.jsontest.com/?callback=showMyIP");
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
 
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
 
		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
 
		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
 
		conn.disconnect();
 
	  } catch (MalformedURLException e) {
 
		e.printStackTrace();
 
	  } catch (IOException e) {
 
		e.printStackTrace();
 
	  }
 
	}
 
}