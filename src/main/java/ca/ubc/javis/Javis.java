package ca.ubc.javis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.crawljax.core.CrawlSession;
import com.crawljax.core.plugin.PostCrawlingPlugin;
import com.crawljax.core.state.Attribute;
import com.crawljax.core.state.Element;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.StateVertex;
import com.google.common.io.Files;

public class Javis implements PostCrawlingPlugin {

	private static Logger sfgLogger = LoggerFactory.getLogger(Javis.class);
	public static ArrayList<String> visiblearray = new ArrayList<String>();
	public static ArrayList<String> invisiblearray = new ArrayList<String>();
	public static ArrayList<String> stateCondition = new ArrayList<String>();
	public static StringBuffer buffer = new StringBuffer();
	public static StateFlowGraphInformation sfgInformation = new StateFlowGraphInformation();
	public static boolean indexFlag = true;
	private final Logger myLogger;

	public Javis(Logger siteLog) {
		myLogger = siteLog;
	}

	@Override
	public void postCrawling(CrawlSession session) {
		int totalDomDifferenceSize;

		Set<StateVertex> states = session.getStateFlowGraph().getAllStates();
		java.util.Iterator<StateVertex> stateID = states.iterator();
		StateVertex s = stateID.next();
		// StateVertex previous = stateID.next();
		for (int i = 0; i < states.size(); i++) {
			/*
			 * if (s.getName().equals("index")) continue;
			 */
			Set<Eventable> eve = session.getStateFlowGraph().getIncomingClickable(s);
			if (eve.size() == 0) {
				indexFlag = false;
				s = stateID.next();
				continue;
			}

			stateCategorization(eve);
			sfgLogger.info(s.getName() + " Incoming Edge(s): "
			        + session.getStateFlowGraph().getIncomingClickable(s) + "\n");
			int j = getCondition(s.getName());
			sfgLogger.info("Edge is: " + eve + "Condition: " + stateCondition.get(j));
			if (stateID.hasNext())
				s = stateID.next();
			else
				break;
		}
		getDomDifferenceForInvisibleStates(session);
		myLogger.info("\nVisible States: " + sfgInformation.getVisibleState()
		        + " Invisible States: " + sfgInformation.getInvisibleState() + " Visible Edges: "
		        + sfgInformation.getVisibleEdge() + " Invisible Edges: "
		        + sfgInformation.getInvisibleEdge() + "\n-------------Edges---------"
		        + "\n Visible Edges are:\n");
		int i;
		for (i = 0; i < sfgInformation.getVisibleEdge().get(); i++) {
			myLogger.info(visiblearray.get(i));
		}
		myLogger.info("\n---------------\n Invisible Edges are:");
		for (i = 0; i < sfgInformation.getInvisibleEdge().get(); i++) {
			myLogger.info(invisiblearray.get(i));
		}
		myLogger.info("\n----------------------");
		File domDifference =
		        new File(CrawljaxRunner.path + CrawljaxRunner.counter + CrawljaxRunner.name
		                + "//TotalChangeResultLog.txt");
		if (!domDifference.exists()) {
			FileWriter totalDomDifferences;
			try {
				totalDomDifferences =
				        new FileWriter(CrawljaxRunner.path + CrawljaxRunner.counter
				                + CrawljaxRunner.name + "/TotalChangeResultLog.txt");
				BufferedWriter out = new BufferedWriter(totalDomDifferences);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			totalDomDifferenceSize =
			        getDomDifferenceByteSize(CrawljaxRunner.path + CrawljaxRunner.counter
			                + CrawljaxRunner.name + "/TotalChangeResultLog.txt");
			int contentSize = extractContents();
			printResults(totalDomDifferenceSize, contentSize, session);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static int getCondition(String name) {

		String id = "";
		int ID;
		if (name.equalsIgnoreCase("index") && indexFlag)
			return 0;
		else if (indexFlag && !name.equalsIgnoreCase("index")) {
			id = name.substring(5, name.length());
			ID = Integer.parseInt(id);
			return ID;
		} else {
			id = name.substring(5, name.length());
			ID = Integer.parseInt(id);
			return ID - 1;
		}
	}

	private void getDomDifferenceForInvisibleStates(CrawlSession session) {

		Set<StateVertex> states = session.getStateFlowGraph().getAllStates();
		StateVertex src;
		int fromState = 0;
		String sourceState;
		for (int i = 0; i < stateCondition.size(); i++) {
			if (stateCondition.get(i).equalsIgnoreCase("invisible")) {
				try {
					StateVertex dest = getCurrentState(states, i);
					List<StateVertex> srcStateVertexList = getPreviousState(session, dest);
					java.util.Iterator<StateVertex> srcStateVertex =
					        srcStateVertexList.iterator();
					if (srcStateVertexList.size() > 1)
						for (int j = 0; j < srcStateVertexList.size(); j++) {
							src = srcStateVertex.next();
							if (src.getName().equalsIgnoreCase("index")) {
								sourceState = "index";
								GetDomDifferences.calculateAndSave(src.getDom(), dest.getDom(),
								        (Integer.toString(i + 1) + "from" + sourceState));
							} else {
								fromState = getCondition(src.getName());
								fromState++;
								GetDomDifferences.calculateAndSave(src.getDom(), dest.getDom(),
								        (Integer.toString(i + 1) + "from" + (Integer
								                .toString(fromState))));
							}
							if (srcStateVertex.hasNext())
								continue;
							else
								break;
						}

					else {
						src = srcStateVertex.next();
						if (src.getName().equalsIgnoreCase("index")) {
							sourceState = "index";
							GetDomDifferences.calculateAndSave(src.getDom(), dest.getDom(),
							        (Integer.toString(i + 1) + "from" + sourceState));
						} else {
							fromState = getCondition(src.getName());
							fromState++;
							GetDomDifferences.calculateAndSave(src.getDom(), dest.getDom(),
							        (Integer.toString(i + 1) + "from" + (Integer
							                .toString(fromState))));
							// GetDomDifferences.calculateAndSave(src.getDom(), dest.getDom(),
							// Integer.toString(i));
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private List<StateVertex> getPreviousState(CrawlSession session, StateVertex dest) {

		List<StateVertex> result = new ArrayList<>();

		Set<StateVertex> allStates = session.getStateFlowGraph().getAllStates();
		java.util.Iterator<StateVertex> stateID = allStates.iterator();
		StateVertex s = stateID.next();

		Set<Eventable> eve = session.getStateFlowGraph().getIncomingClickable(dest);
		Iterator<Eventable> destEventable = eve.iterator();
		Eventable de = destEventable.next();
		boolean matched = false;
		while (stateID.hasNext()) {
			if (s.equals(dest)) {
				s = stateID.next();
				continue;
			}
			Set<Eventable> srcClickables = session.getStateFlowGraph().getOutgoingClickables(s);
			if (srcClickables.size() == 0) {
				s = stateID.next();
				continue;
			}
			Iterator<Eventable> srcEventable = srcClickables.iterator();
			Eventable se = srcEventable.next();
			for (int j = 0; j < eve.size(); j++) {
				for (int i = 0; i < srcClickables.size(); i++) {
					if (de.equals(se)) {
						result.add(s);
						matched = true;
						break;
					} else {
						if (srcEventable.hasNext())
							se = srcEventable.next();
						continue;
					}
				}
				if (matched)
					break;
				else {
					if (destEventable.hasNext())
						de = destEventable.next();
				}
			}
			if (stateID.hasNext())
				s = stateID.next();
		}
		return result;
	}

	private static StateVertex getCurrentState(Set<StateVertex> states, int i) {
		StateVertex result = null;
		java.util.Iterator<StateVertex> stateID = states.iterator();
		StateVertex s = stateID.next();
		for (int j = 0; j < states.size(); j++) {
			if (!indexFlag && j == 0) {
				s = stateID.next();
				continue;
			} else if (indexFlag && i == 0) {
				result = s;
				break;
			} else {
				if (i == getCondition(s.getName())) {
					result = s;
					break;
				} else {
					if (stateID.hasNext()) {
						s = stateID.next();
						continue;
					} else
						break;
				}
			}

		}
		return result;

	}

	private int extractContents() {
		int size = 0;
		try {
			ContentExtraction_Final newContent = new ContentExtraction_Final();
			newContent.getContents();
			size =
			        getDomDifferenceByteSize(CrawljaxRunner.path + CrawljaxRunner.counter
			                + CrawljaxRunner.name + "/TotalContent.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return size;
	}

	public void stateCategorization(Set<Eventable> event) {
		Node node;
		boolean anchorVisible = false;
		boolean visibleEdge = false;
		boolean imgVisible = false;
		Eventable edge;
		java.util.Iterator<Eventable> edg = event.iterator();

		if (event.size() == 1) {
			handleOneEdge(edg);
		} else if (event.size() > 1) {
			while (edg.hasNext()) {
				edge = edg.next();
				if (edge.getElement().getTag().equalsIgnoreCase("a")
				        || edge.getElement().getTag().equalsIgnoreCase("img")) {
					if (edge.getElement().getTag().equalsIgnoreCase("a")) {
						anchorVisible = anchorVisibilityChecking(edge);
						if (!anchorVisible)
							continue;
						else
							break;
					} else if (edge.getElement().getTag().equalsIgnoreCase("img")) {
						node = edge.getElement().getNode().getParentNode();
						imgVisible = imgChecker(node, edge);
						if (!imgVisible)
							continue;
						else
							break;
					}
				}
			}
			if (anchorVisible || imgVisible) {
				visibleEdge = true;
				stateCondition.add((sfgInformation.getInputCounter().get() + sfgInformation
				        .getVisibleState().get()), "visible");
				sfgInformation.getVisibleState().getAndIncrement();

			}

			if (!visibleEdge && !imgVisible) {
				stateCondition.add((sfgInformation.getInvisibleState().get() + sfgInformation
				        .getVisibleState().get()), "invisible");
				sfgInformation.getInvisibleState().getAndIncrement();
			}
			java.util.Iterator<Eventable> edg1 = event.iterator();

			checkAndCountEdges(edg1);
		}
	}

	private void checkAndCountEdges(java.util.Iterator<Eventable> edg1) {
		Node node;
		boolean anchorVisible;
		boolean imgVisible;
		Eventable edge;
		while (edg1.hasNext()) {
			edge = edg1.next();
			if (isRelevantTag(edge)) {
				getEdgeInfo(edge);
				invisiblearray.add((sfgInformation.getInvisibleEdge().get()), edge.toString());
				sfgInformation.getInvisibleEdge().getAndIncrement();
			}
			if (edge.getElement().getTag().equalsIgnoreCase("a")) {
				anchorVisible = anchorVisibilityChecking(edge);
				if (anchorVisible) {
					visiblearray.add(sfgInformation.getVisibleEdge().get(), edge.toString());
					sfgInformation.getVisibleEdge().getAndIncrement();
				} else {
					invisiblearray.add(sfgInformation.getInvisibleEdge().get(), edge.toString());
					sfgInformation.getInvisibleEdge().getAndIncrement();
				}
			}
			if (edge.getElement().getTag().toLowerCase().equalsIgnoreCase("img")) {
				node = edge.getElement().getNode().getParentNode();
				imgVisible = imgChecker(node, edge);
				if (imgVisible) {
					visiblearray.add(sfgInformation.getVisibleEdge().get(), edge.toString());
					sfgInformation.getVisibleEdge().getAndIncrement();
				} else {
					invisiblearray.add(sfgInformation.getInvisibleEdge().get(), edge.toString());
					sfgInformation.getInvisibleEdge().getAndIncrement();
				}
			}
		}
	}

	private void handleOneEdge(java.util.Iterator<Eventable> edg) {
		Node node;
		boolean anchorVisible;
		boolean imgVisible;
		Eventable edge;
		edge = edg.next();
		if (isRelevantTag(edge)) {
			invisiblearray.add(sfgInformation.getInvisibleEdge().get(), edge.toString());
			stateCondition.add((sfgInformation.getInvisibleState().get() + sfgInformation
			        .getVisibleState().get()), "invisible");
			sfgInformation.getInvisibleState().getAndIncrement();
			sfgInformation.getInvisibleEdge().getAndIncrement();
			getEdgeInfo(edge);
		} else if (edge.getElement().getTag().equalsIgnoreCase("a")) {
			anchorVisible = anchorVisibilityChecking(edge);
			if (anchorVisible) {
				visiblearray.add(sfgInformation.getVisibleEdge().get(), edge.toString());
				stateCondition.add((sfgInformation.getInvisibleState().get() + sfgInformation
				        .getVisibleState().get()), "visible");
				sfgInformation.getVisibleState().getAndIncrement();
				sfgInformation.getVisibleEdge().getAndIncrement();
			} else {
				invisiblearray.add(sfgInformation.getInvisibleEdge().get(), edge.toString());
				stateCondition.add((sfgInformation.getInvisibleState().get() + sfgInformation
				        .getVisibleState().get()), "invisible");
				sfgInformation.getInvisibleState().getAndIncrement();
				sfgInformation.getInvisibleEdge().getAndIncrement();
			}

		} else if (edge.getElement().getTag().toLowerCase().equalsIgnoreCase("img")) {
			node = edge.getElement().getNode().getParentNode();
			imgVisible = imgChecker(node, edge);
			if (imgVisible) {
				visiblearray.add(sfgInformation.getVisibleEdge().get(), edge.toString());
				stateCondition.add((sfgInformation.getInvisibleState().get() + sfgInformation
				        .getVisibleState().get()), "visible");
				sfgInformation.getVisibleState().getAndIncrement();
				sfgInformation.getVisibleEdge().getAndIncrement();
			} else {
				invisiblearray.add(sfgInformation.getInvisibleEdge().get(), edge.toString());
				stateCondition.add((sfgInformation.getInvisibleState().get() + sfgInformation
				        .getVisibleState().get()), "invisible");
				sfgInformation.getInvisibleState().getAndIncrement();
				sfgInformation.getInvisibleEdge().getAndIncrement();
			}
		}
	}

	private boolean isRelevantTag(Eventable edge) {
		return edge.getElement().getTag().equalsIgnoreCase("div")
		        || edge.getElement().getTag().equalsIgnoreCase("span")
		        || edge.getElement().getTag().equalsIgnoreCase("input")
		        || edge.getElement().getTag().equalsIgnoreCase("button");
	}

	public Boolean anchorVisibilityChecking(Eventable edge) {
		StringBuffer hrefValue = new StringBuffer();
		Boolean hrefFlag = false;
		Boolean URLFlag = false;
		String newHref = "";
		Boolean visible = false;
		String anchorHrefValue = "";
		if (edge.getElement().getTag().toLowerCase().equals("a")) {
			anchorHrefValue = getElementAttributes(edge.getElement());
			if (anchorHrefValue != "") {
				hrefFlag = true;
				hrefValue.delete(0, hrefValue.length());
				hrefValue.insert(0, anchorHrefValue);
				newHref = hrefValue.toString();
			}
			if (hrefFlag) {
				if (hrefInvisible(newHref))
					URLFlag = false;
				else
					URLFlag = true;
				if (URLFlag) {
					sfgInformation.getAVisCounter().getAndIncrement();
					visible = true;
				} else {
					visible = false;
					sfgInformation.getAInvisCounter().getAndIncrement();
				}
			}
		}
		return visible;
	}

	public void getEdgeInfo(Eventable edge) {
		if (edge.getElement().getTag().equals("DIV"))
			sfgInformation.getDivCounter().getAndIncrement();
		else if (edge.getElement().getTag().equals("SPAN"))
			sfgInformation.getSpanCounter().getAndIncrement();
		else if (edge.getElement().getTag().equals("INPUT"))
			sfgInformation.getInputCounter().getAndIncrement();
		else if (edge.getElement().getTag().equals("BUTTON"))
			sfgInformation.getButtonCounter().getAndIncrement();
	}

	public static boolean hrefInvisible(String str) {
		boolean result = false;
		if (str.startsWith("#") || str.startsWith("javascript") || str.equals(""))
			result = true;
		return result;
	}

	public static String getElementAttributes(Element element) {
		StringBuffer buffer = new StringBuffer();

		if (element != null) {
			List<Attribute> attributes = element.getAttributes();
			if (attributes != null) {
				for (int i = 0; i < attributes.size(); i++) {
					Attribute attr = attributes.get(i);
					if (attr.getName().equalsIgnoreCase("href"))
						buffer.append(attr.getValue());
				}
			}
		}
		return buffer.toString();
	}

	public Boolean imgChecker(Node node, Eventable edge) {
		Boolean visible = false;
		if (!node.equals(null))
			if (node.getNodeName().equalsIgnoreCase("a")) {
				if (node.hasAttributes()) {
					Element e = new Element(node);
					String imgHrefValue = getElementAttributes(e);
					if (hrefInvisible(imgHrefValue)) {
						sfgInformation.getImgInvisCounter().getAndIncrement();
					} else {
						visible = true;
						sfgInformation.getImgVisCounter().getAndIncrement();
					}
				}
			} else {
				sfgInformation.getImgInvisCounter().getAndIncrement();
			}
		return visible;
	}

	public int getDomDifferenceByteSize(String path) throws IOException {

		List<String> strLine = Files.readLines(new File(path), Charsets.UTF_8);

		StringBuilder lines = new StringBuilder();
		for (String line : strLine) {
			if (line.length() > 1) {
				lines.append(line.substring(1));
			}
		}
		String content = lines.toString().trim();
		int size = (content.getBytes().length);
		return size;
	}

	public void printResults(int size, int contentSize, CrawlSession session) {

		long timing = System.currentTimeMillis() - CrawljaxRunner.startTime;
		checkElements();
		String result =
		        ("URL: " + CrawljaxRunner.URL + "\nTotal States: "
		                + session.getStateFlowGraph().getAllStates().size() + "\nTotal Edges: "
		                + session.getStateFlowGraph().getAllEdges().size() + "\nVisible States: "
		                + sfgInformation.getVisibleState() + "\nInvisible States: "
		                + sfgInformation.getInvisibleState() + "\nVisible Edges: "
		                + (sfgInformation.getVisibleEdge().get()) + "\nInvisible Edges: "
		                + sfgInformation.getInvisibleEdge()
		                + "\nTotalDomDifferenceSize (Bytes): " + size
		                + "\nTotalDomDifferenceSize (KB): " + (size / 1024)
		                + "\nTotalContent (Bytes): " + contentSize + "\nTotalContent (KB) : "
		                + (contentSize / 1024) + "\nElapsed Time (milliseconds): " + timing
		                + "\n--------Clickables---------" + "\nA Visible: "
		                + (sfgInformation.getAVisCounter().get()) + "\nA Invisible: "
		                + sfgInformation.getAInvisCounter() + "\nDiv: "
		                + sfgInformation.getDivCounter() + "\nSpan: "
		                + sfgInformation.getSpanCounter() + "\nImg Visible: "
		                + sfgInformation.getImgVisCounter() + "\nImg Invisible: "
		                + sfgInformation.getImgInvisCounter() + "\nInput: "
		                + sfgInformation.getInputCounter() + "\nButton: " + sfgInformation
		                .getButtonCounter());
		try {
			Files.write(result, new File(CrawljaxRunner.path + CrawljaxRunner.counter
			        + CrawljaxRunner.name + "//FinalResults.txt"), Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void checkElements() {
		int avis = sfgInformation.getAVisCounter().get();
		int ainvis = sfgInformation.getAInvisCounter().get();
		int allinvisEdges =
		        (sfgInformation.getButtonCounter().get() + sfgInformation.getDivCounter().get()
		                + sfgInformation.getImgInvisCounter().get()
		                + sfgInformation.getInputCounter().get() + sfgInformation
		                .getSpanCounter().get());
		int remainingInvisEdges = (sfgInformation.getInvisibleEdge().get() - allinvisEdges);
		int remainingVisEdges =
		        sfgInformation.getVisibleEdge().get() - sfgInformation.getImgVisCounter().get();
		if (remainingVisEdges != avis)
			sfgInformation.getAVisCounter().set(remainingVisEdges);
		if (remainingInvisEdges != ainvis)
			sfgInformation.getAInvisCounter().set(remainingInvisEdges);
	}

}
