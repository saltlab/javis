package ca.ubc.javis;

import java.io.IOException;

import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.crawljax.util.DomUtils;

public class GetJavaScriptFromProxyAddOn extends ProxyPlugin{

	private String URL;
	
	public GetJavaScriptFromProxyAddOn(String url) {
	this.URL = url;	
	}

	@Override
	public String getPluginName() {
		return this.getClass().getName();
	}

	@Override
	public HTTPClient getProxyPlugin(HTTPClient in) {
		return (new Plugin(in));
	}

	private class Plugin implements HTTPClient {

		private HTTPClient client;

		/**
		 * Constructor.
		 * 
		 * @param in
		 *            HTTPClient
		 */
		public Plugin(HTTPClient in) {
			this.client = in;
		}

		@Override
		public Response fetchResponse(Request request) throws IOException {

			Response response = this.client.fetchResponse(request);

			if (response == null) {
				return null;
			}

			// parse the response body
			try {
				String contentType = response.getHeader("Content-Type");
				if (contentType == null) {
					return response;
				}

				if (contentType.contains("html")) {
					// parse the content
					String domStr = new String(response.getContent());
					Document dom = DomUtils.asDocument(domStr);
					getJavaScript(response, dom);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		private void getJavaScript(Response response, Document dom) {
			NodeList nodes = dom.getElementsByTagName("script");
			if (nodes.getLength() == 0) {
				nodes = dom.getElementsByTagName("SCRIPT");
			}

			// no target node found
			if (nodes.getLength() == 0) {
				System.out.println("HTML received but" + " does not contain javascript.");
			}
			// target node section found: inject the file
			else if (nodes.getLength() == 1) {
					System.out.println(nodes.item(0));
				}else{
					for (int i = 0; i < nodes.getLength(); i++){
						System.out.println(nodes.item(i).toString());
					}
			}
		}
	}
}
