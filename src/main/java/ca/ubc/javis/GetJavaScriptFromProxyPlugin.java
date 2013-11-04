package ca.ubc.javis;

import com.crawljax.core.CandidateElement;
import com.crawljax.core.CrawlerContext;
import com.crawljax.core.configuration.ProxyConfiguration;
import com.crawljax.core.plugin.PreStateCrawlingPlugin;
import com.crawljax.core.state.StateVertex;
import com.crawljax.plugins.proxy.WebScarabProxyPlugin;
import com.google.common.collect.ImmutableList;


public class GetJavaScriptFromProxyPlugin implements PreStateCrawlingPlugin {

	@SuppressWarnings("static-access")
	@Override
	public void preStateCrawling(CrawlerContext context,
			ImmutableList<CandidateElement> candidateElements, StateVertex state) {
		
		WebScarabProxyPlugin proxyPlugin = new WebScarabProxyPlugin();
				
		proxyPlugin.addPlugin(new GetJavaScriptFromProxyAddOn(state.getUrl()));
				
		context.getConfig().builderFor(state.getUrl()).addPlugin(proxyPlugin).setProxyConfig(
				ProxyConfiguration.manualProxyOn("127.0.0.1", 8084)).addPlugin(
						new GetJavaScriptFromProxyPlugin());
					
	}

}
