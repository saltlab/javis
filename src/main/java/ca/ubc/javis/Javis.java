package ca.ubc.javis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

	private static Logger myLogger = LoggerFactory.getLogger(Javis.class);
	public static ArrayList<String> visiblearray = new ArrayList<String>();
	public static ArrayList<String> invisiblearray = new ArrayList<String>();
	public static ArrayList<String> stateCondition = new ArrayList<String>();
	public static StringBuffer buffer = new StringBuffer();
	public static StateFlowGraphInformation sfgInformation = new StateFlowGraphInformation();

	@Override
	public void postCrawling(CrawlSession session) {
		int totalDomDifferenceSize;

		Set<StateVertex> states = session.getStateFlowGraph().getAllStates();
		java.util.Iterator<StateVertex> stateID = states.iterator();
		StateVertex previous = stateID.next();
		while (stateID.hasNext()) {
			StateVertex s = stateID.next();
			if (s.getName().equals("index"))
				continue;
			Set<Eventable> eve = session.getStateFlowGraph().getIncomingClickable(s);
			String name = s.getName();
			stateCategorization(eve);
			String id = name.substring(5, name.length());
			int ID = Integer.parseInt(id);
			if (stateCondition.get(ID - 1).equals("invisible")) {
				try {
					GetDomDifferences.calculateAndSave(previous.getDom(), s.getDom(), id);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			previous = s;
		}
		myLogger.info("\nVisible States: " + sfgInformation.getVisibleState()
		        + " Invisible States: " + sfgInformation.getInvisibleState() + " Visible Edges: "
		        + sfgInformation.getVisibleEdge() + " Invisible Edges: "
		        + sfgInformation.getInvisibleEdge() + "\n-------------Edges---------"
		        + "\nVisible Edges are:\n");
		int i;
		for (i = 0; i < sfgInformation.getVisibleEdge().get(); i++) {
			myLogger.info(visiblearray.get(i));
		}
		myLogger.info("\n---------------\n Invisible Edges are:");
		for (i = 0; i < sfgInformation.getInvisibleEdge().get(); i++) {
			myLogger.info(invisiblearray.get(i));
		}
		File domDifference =
		        new File(CrawljaxRunner.path + CrawljaxRunner.counter + CrawljaxRunner.name
		                + "//TotalChangeResultLog.txt");
		if (!domDifference.exists()) {
			FileWriter totalDomDifferences;
			try {
				totalDomDifferences =
				        new FileWriter(CrawljaxRunner.path + CrawljaxRunner.counter
				                + CrawljaxRunner.name + "//TotalChangeResultLog.txt");
				BufferedWriter out = new BufferedWriter(totalDomDifferences);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		totalDomDifferenceSize =
		        getDomDifferenceByteSize(CrawljaxRunner.path + CrawljaxRunner.counter
		                + CrawljaxRunner.name + "//TotalChangeResultLog.txt");
		int contentSize = extractContents(domDifference);
		printResults(totalDomDifferenceSize, contentSize, session);
	}

	private int extractContents(File domDifference) {
		int size = 0;
		try {
			String domString = Files.readLines(domDifference, Charsets.UTF_8).toString();
			ContentExtraction newContent = new ContentExtraction();
			newContent.getTagValues(domString);
			size =
			        getDomDifferenceByteSize(CrawljaxRunner.path + CrawljaxRunner.counter
			                + CrawljaxRunner.name + "//TotalContent.txt");
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
					if (edge.getElement().getTag().equalsIgnoreCase("a"))
						anchorVisible = anchorVisibilityChecking(edge);
					else if (edge.getElement().getTag().equalsIgnoreCase("img")) {
						node = edge.getElement().getNode().getParentNode();
						imgVisible = imgChecker(node, edge);
					}
					if (anchorVisible || imgVisible) {
						visibleEdge = true;
						stateCondition.add(
						        (sfgInformation.getInputCounter().get() + sfgInformation
						                .getVisibleState().get()), "visible");
						sfgInformation.getVisibleState().getAndIncrement();
						break;
					}
				}
			}
			if (!visibleEdge) {
				stateCondition.add((sfgInformation.getInvisibleState().get() + sfgInformation
				        .getVisibleState().get()), "invisible");
				sfgInformation.getInvisibleState().getAndIncrement();
			}
			java.util.Iterator<Eventable> edg1 = event.iterator();

			checkAndCountEdges(anchorVisible, edg1);
		}
	}

	private void checkAndCountEdges(boolean anchorVisible, java.util.Iterator<Eventable> edg1) {
		Node node;
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

	public int getDomDifferenceByteSize(String path) {

		int size = 0;
		StringBuffer buffer = new StringBuffer();
		FileReader file;
		try {

			file = new FileReader(path);
			BufferedReader br = new BufferedReader(file);
			String strLine = "";

			try {
				while (br.readLine() != null) {
					buffer.append(br.readLine());
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
			strLine = buffer.toString().trim();
			size = (strLine.getBytes().length);
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		}
		return size;
	}

	public void printResults(int size, int contentSize, CrawlSession session) {

		long timing = System.currentTimeMillis() - CrawljaxRunner.startTime;
		String result =
		        ("URL: " + CrawljaxRunner.URL + "\nTotal States: "
		                + session.getStateFlowGraph().getAllStates().size() + "\nTotal Edges: "
		                + session.getStateFlowGraph().getAllEdges().size() + "\nVisible States: "
		                + sfgInformation.getVisibleState() + "\nInvisible States: "
		                + sfgInformation.getInvisibleState() + "\nVisible Edges: "
		                + sfgInformation.getVisibleEdge() + "\nInvisible Edges: "
		                + sfgInformation.getInvisibleEdge()
		                + "\nTotalDomDifferenceSize (Bytes): " + size
		                + "\nTotalDomDifferenceSize (KB): " + (size / 1024)
		                + "\nTotalContent (Bytes): " + contentSize + "\nTotalContent (KB) : "
		                + (contentSize / 1024) + "\nElapsed Time (milliseconds): " + timing
		                + "\n--------Clickables---------" + "\nA Visible: "
		                + sfgInformation.getAVisCounter() + "\nA Invisible: "
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

}
