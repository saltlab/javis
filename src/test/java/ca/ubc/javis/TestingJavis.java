package ca.ubc.javis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.Eventable.EventType;
import com.crawljax.core.state.StateFlowGraph;
import com.crawljax.core.state.StateVertex;
import com.crawljax.util.DomUtils;

public class TestingJavis {

	@Test
	public void testHrefInvisible() {
		Boolean result = true;
		String href = "#";
		assertEquals(result, Javis.hrefInvisible(href));
		href = "javascript.window";
		assertTrue(Javis.hrefInvisible(href));
		href = "http://www.google.ca";
		assertFalse(Javis.hrefInvisible(href));
	}

	@Test
	public void testAnchorVisibilityChecking() throws SAXException, IOException {
		Javis edp = new Javis(LoggerFactory.getLogger(TestingJavis.class));
		String html =
		        "<body><a id='achr' href='http://www.google.ca'>Google</a><div><span id='spn'>"
		                + "</span></div></body>";

		Document dom = DomUtils.asDocument(html);
		assertNotNull(dom);
		Element element = dom.getElementById("achr");

		Eventable clickable = new Eventable(element, EventType.click);
		assertTrue(edp.anchorVisibilityChecking(clickable));
	}

	@Test
	public void testGetElementAttributes() throws SAXException, IOException {
		String html = "<body><a id='achr' href='http://www.google.ca'>Google</a></body>";

		Document dom = DomUtils.asDocument(html);
		assertNotNull(dom);
		Element element = dom.getElementById("achr");
		Eventable clickable = new Eventable(element, EventType.click);
		assertEquals("http://www.google.ca", Javis.getElementAttributes(clickable.getElement()));
	}

	@Test
	public void testImgChecker() throws SAXException, IOException {
		Javis edp = new Javis(LoggerFactory.getLogger(TestingJavis.class));
		String html1 =
		        "<body><a id='achr' href=\"http://www.google.ca\">Google<img id='img' width='25' /></a></body>";
		String html2 = "<body><div id='div'>Click here<img id='img' width='25' /></div></body>";
		Document dom1 = DomUtils.asDocument(html1);
		Document dom2 = DomUtils.asDocument(html2);
		assertNotNull(dom1);
		assertNotNull(dom2);
		Element element1 = dom1.getElementById("img");
		Element element2 = dom2.getElementById("img");
		Eventable clickable1 = new Eventable(element1, EventType.click);
		Eventable clickable2 = new Eventable(element2, EventType.click);
		Node node1 = clickable1.getElement().getNode().getParentNode();
		Node node2 = clickable2.getElement().getNode().getParentNode();
		assertNotNull(node1);
		assertNotNull(node2);
		assertTrue(edp.imgChecker(node1, clickable1));
		assertFalse(edp.imgChecker(node2, clickable2));
	}

	@Test
	public void testStateCategorization_OneClickable_Visbile() throws SAXException, IOException {
		Javis.sfgInformation.getVisibleState().set(0);
		Javis.sfgInformation.getVisibleEdge().set(0);
		StateVertex index = new StateVertex("index", "<table><div>index</div></table>");
		StateVertex state2 =
		        new StateVertex("STATE_TWO", "<table><a id='achr'>state2</a></table>");
		StateVertex state3 = new StateVertex("STATE_THREE", "<table><div>state3</div></table>");

		StateFlowGraph graph = new StateFlowGraph(index);
		assertTrue(graph.addState(state2) == null);
		assertTrue(graph.addState(state3) == null);
		assertNotNull(graph);
		String html =
		        "<body><a id='achr' href='http://www.google.ca'>Google</a><div id='div'>Click Here!</div></body>";

		Document dom = DomUtils.asDocument(html);
		assertNotNull(dom);
		Element element = dom.getElementById("achr");
		Eventable clickable = new Eventable(element, EventType.click);

		assertTrue(graph.addEdge(index, state2, clickable));

		Set<Eventable> clickables = graph.getOutgoingClickables(index);
		System.out.println(clickables);
		Javis javis = new Javis(LoggerFactory.getLogger(TestingJavis.class));
		javis.stateCategorization(clickables);
		assertEquals(1, Javis.sfgInformation.getVisibleState().get());
	}

	@Test
	public void testStateCategorization_OneClickable_Invisbile() throws SAXException, IOException {
		StateVertex index = new StateVertex("index", "<table><div>index</div></table>");
		StateVertex state2 =
		        new StateVertex("STATE_TWO", "<table><a id='achr'>state2</a></table>");
		StateVertex state3 = new StateVertex("STATE_THREE", "<table><div>state3</div></table>");

		StateFlowGraph graph = new StateFlowGraph(index);

		assertNotNull(graph);
		String html =
		        "<body><a id='achr' href='http://www.google.ca'>Google</a><div id='div'>Click Here!</div></body>";

		Document dom = DomUtils.asDocument(html);
		assertNotNull(dom);
		Element element = dom.getElementById("div");
		Eventable clickable = new Eventable(element, EventType.click);

		assertTrue(graph.addState(state2) == null);
		assertTrue(graph.addState(state3) == null);
		assertTrue(graph.addEdge(state2, state3, clickable));

		Set<Eventable> clickables = graph.getOutgoingClickables(state2);

		Javis javis = new Javis(LoggerFactory.getLogger(TestingJavis.class));
		javis.stateCategorization(clickables);
		assertEquals(1, Javis.sfgInformation.getInvisibleState().get());
	}

	@Test
	public void testStateCategorization_TwoClickables_Visible() throws SAXException, IOException {
		Javis.sfgInformation.getVisibleState().set(0);
		Javis.sfgInformation.getVisibleEdge().set(0);
		StateVertex index = new StateVertex("index", "<table><div>index</div></table>");
		StateVertex state2 = new StateVertex("STATE_TWO", "<table><a>state2</a></table>");
		StateVertex state3 = new StateVertex("STATE_THREE", "<table><div>state3</div></table>");
		StateVertex state4 = new StateVertex("STATE_FOUR", "<table><div>state4</div></table>");

		StateFlowGraph graph = new StateFlowGraph(index);
		assertTrue(graph.addState(state2) == null);
		assertTrue(graph.addState(state3) == null);
		assertTrue(graph.addState(state4) == null);
		assertNotNull(graph);
		String html =
		        "<body><a id='achr7' href='http://www.google.ca'>Google</a><a href='http://www.yahoo.ca'>Yahoo<img id='img1'></a><div id='div'>Click Here!</div></body>";

		Document dom = DomUtils.asDocument(html);
		assertNotNull(dom);
		Element element1 = dom.getElementById("achr7");
		Eventable clickable1 = new Eventable(element1, EventType.click);
		assertTrue(graph.addEdge(state3, state4, clickable1));

		Element element2 = dom.getElementById("img1");
		Eventable clickable2 = new Eventable(element2, EventType.click);

		assertTrue(graph.addEdge(state3, state4, clickable2));

		Set<Eventable> clickables = graph.getOutgoingClickables(state3);
		System.out.println(clickables);
		Javis javis = new Javis(LoggerFactory.getLogger(TestingJavis.class));
		javis.stateCategorization(clickables);
		assertEquals(1, Javis.sfgInformation.getVisibleState().get());
		assertEquals(2, Javis.sfgInformation.getVisibleEdge().get());
	}

	@Test
	public void testStateCategorization_TwoClickables_Invisible() throws SAXException,
	        IOException {
		Javis.sfgInformation.getInvisibleState().set(0);
		Javis.sfgInformation.getInvisibleEdge().set(0);
		StateVertex index = new StateVertex("index", "<table><div>index</div></table>");
		StateVertex state2 =
		        new StateVertex("STATE_TWO", "<table><a id='achr'>state2</a></table>");
		StateVertex state3 = new StateVertex("STATE_THREE", "<table><div>state3</div></table>");
		StateVertex state4 = new StateVertex("STATE_FOUR", "<table><div>state4</div></table>");

		StateFlowGraph graph = new StateFlowGraph(index);
		assertTrue(graph.addState(state2) == null);
		assertTrue(graph.addState(state3) == null);
		assertTrue(graph.addState(state4) == null);
		assertNotNull(graph);
		String html =
		        "<body><span id='spn1'>Google</span><div id='div1'><img id='img1'/>Click Here!</div></body>";

		Document dom = DomUtils.asDocument(html);
		assertNotNull(dom);
		Element element1 = dom.getElementById("spn1");
		Eventable clickable1 = new Eventable(element1, EventType.click);
		assertTrue(graph.addEdge(state3, state4, clickable1));

		Element element2 = dom.getElementById("img1");
		Eventable clickable2 = new Eventable(element2, EventType.click);

		assertTrue(graph.addEdge(state3, state4, clickable2));

		Set<Eventable> clickables = graph.getOutgoingClickables(state3);
		Javis javis = new Javis(LoggerFactory.getLogger(TestingJavis.class));
		javis.stateCategorization(clickables);
		assertEquals(1, Javis.sfgInformation.getInvisibleState().get());
		assertEquals(2, Javis.sfgInformation.getInvisibleEdge().get());
	}

	@Test
	public void testStateCategorization_TwoClickables_Invisible_FromTwoStates()
	        throws SAXException, IOException {
		Javis.sfgInformation.getInvisibleState().set(0);
		Javis.sfgInformation.getInvisibleEdge().set(0);
		StateVertex index = new StateVertex("index", "<table><div>index</div></table>");
		StateVertex state2 =
		        new StateVertex("STATE_TWO", "<table><a id='achr'>state2</a></table>");
		StateVertex state3 = new StateVertex("STATE_THREE", "<table><div>state3</div></table>");
		StateVertex state4 = new StateVertex("STATE_FOUR", "<table><div>state4</div></table>");

		StateFlowGraph graph = new StateFlowGraph(index);
		assertTrue(graph.addState(state2) == null);
		assertTrue(graph.addState(state3) == null);
		assertTrue(graph.addState(state4) == null);
		assertNotNull(graph);
		String html =
		        "<body><span id='spn1'>Google</span><div id='div1'><img id='img1'/>Click Here!</div></body>";

		Document dom = DomUtils.asDocument(html);
		assertNotNull(dom);
		Element element1 = dom.getElementById("spn1");
		Eventable clickable1 = new Eventable(element1, EventType.click);
		assertTrue(graph.addEdge(state3, state4, clickable1));

		Element element2 = dom.getElementById("img1");
		Eventable clickable2 = new Eventable(element2, EventType.click);

		assertTrue(graph.addEdge(state2, state4, clickable2));

		Set<Eventable> clickables = graph.getIncomingClickable(state4);
		Javis javis = new Javis(LoggerFactory.getLogger(TestingJavis.class));
		javis.stateCategorization(clickables);
		assertEquals(1, Javis.sfgInformation.getInvisibleState().get());
		assertEquals(2, Javis.sfgInformation.getInvisibleEdge().get());
	}

	/*
	 * @Test public void testStateCategorization_TwoClickables_Invisible_VS_Visible() throws
	 * SAXException, IOException { Javis.sfgInformation.getInvisibleEdge().set(0);
	 * Javis.sfgInformation.getVisibleEdge().set(0);
	 * Javis.sfgInformation.getInvisibleState().set(0);
	 * Javis.sfgInformation.getVisibleState().set(0); StateVertex index = new StateVertex("index",
	 * "<table><div>index</div></table>"); StateVertex state2 = new StateVertex("STATE_TWO",
	 * "<table><a id='achr'>state2</a></table>"); StateVertex state3 = new
	 * StateVertex("STATE_THREE", "<table><div>state3</div></table>"); StateVertex state4 = new
	 * StateVertex("STATE_FOUR", "<table><div>state4</div></table>"); StateFlowGraph graph = new
	 * StateFlowGraph(index); assertTrue(graph.addState(state2) == null);
	 * assertTrue(graph.addState(state3) == null); assertTrue(graph.addState(state4) == null);
	 * assertNotNull(graph); String html =
	 * "<body><div id='div1'>Google!</div><a id='achr' href='http://www.google.ca'>Google</a></body>"
	 * ; Document dom = DomUtils.asDocument(html); assertNotNull(dom); Element element1 =
	 * dom.getElementById("div1"); Eventable clickable1 = new Eventable(element1, EventType.click);
	 * assertTrue(graph.addEdge(state3, state4, clickable1)); Element element2 =
	 * dom.getElementById("achr"); Eventable clickable2 = new Eventable(element2, EventType.click);
	 * assertTrue(graph.addEdge(state3, state4, clickable2)); Set<Eventable> clickables =
	 * graph.getOutgoingClickables(state3); Javis javis = new Javis();
	 * javis.stateCategorization(clickables); assertEquals(1,
	 * Javis.sfgInformation.getVisibleState().get()); assertEquals(0,
	 * Javis.sfgInformation.getInvisibleState().get()); assertEquals(1,
	 * Javis.sfgInformation.getInvisibleEdge().get()); assertEquals(1,
	 * Javis.sfgInformation.getVisibleEdge().get()); }
	 */

}
