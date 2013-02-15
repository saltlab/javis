package ca.ubc.javis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import org.w3c.dom.Node;

import com.crawljax.core.CrawlSession;
import com.crawljax.core.plugin.PostCrawlingPlugin;
import com.crawljax.core.state.Attribute;
import com.crawljax.core.state.Element;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.StateVertex;

public class Javis implements PostCrawlingPlugin {

	// TODO begin other class
	private static int invisibleState;
	private static int visibleState;
	private static int invisibleEdge;
	private static int visibleEdge;
	private static int divCounter;
	private static int spanCounter;
	private static int inputCounter;
	private static int buttonCounter;
	private static int avisibleCounter;
	private static int ainvisibleCounter;
	private static int imgvisibleCounter;
	private static int imginvisibleCounter;
	// end

	private static Logger myLogger = Logger.getLogger(Javis.class.getName());
	public static String[] visiblearray = new String[500];
	public static String[] invisiblearray = new String[500];
	public static String[] stateCondition = new String[500];
	public static StringBuffer buffer = new StringBuffer();

	public static void setVisibleState(int visibleStates) {
		visibleState = visibleStates;
	}

	public static int getVisibleState() {
		return visibleState;
	}

	public static void setDivCounter(int div) {
		divCounter = div;
	}

	public static int getDivCounter() {
		return divCounter;
	}

	public static void setSpanCounter(int span) {
		spanCounter = span;
	}

	public static int getSpanCounter() {
		return spanCounter;
	}

	public static void setInputCounter(int input) {
		inputCounter = input;
	}

	public static int getInputCounter() {
		return inputCounter;
	}

	public static void setButtonCounter(int button) {
		buttonCounter = button;
	}

	public static int getButtonCounter() {
		return buttonCounter;
	}

	public static void setAVisCounter(int avis) {
		avisibleCounter = avis;
	}

	public static int getAVisCounter() {
		return avisibleCounter;
	}

	public static void setAInvisCounter(int ainvis) {
		ainvisibleCounter = ainvis;
	}

	public static int getAIvnisCounter() {
		return ainvisibleCounter;
	}

	public static void setImgVisCounter(int imgvis) {
		imgvisibleCounter = imgvis;
	}

	public static int getImgVisCounter() {
		return imgvisibleCounter;
	}

	public static void setImgInisCounter(int imginvis) {
		imginvisibleCounter = imginvis;
	}

	public static int getImgInvisCounter() {
		return imginvisibleCounter;
	}

	public static void setInvisibleState(int invisibleStates) {
		invisibleState = invisibleStates;
	}

	public static int getInvisibleState() {
		return invisibleState;
	}

	public static void setVisibleEdge(int visibleEdges) {
		visibleEdge = visibleEdges;
	}

	public static int getVisibleEdge() {
		return visibleEdge;
	}

	public static void setInvisibleEdge(int invisibleEdges) {
		invisibleEdge = invisibleEdges;
	}

	public static int getInvisibleEdge() {
		return invisibleEdge;
	}

	@Override
	public void postCrawling(CrawlSession session) {
		int totalDomDifferenceSize;
		myLogger.setLevel(Level.ALL);

		FileHandler myFileHandler = null;
		FileHandler Xmlfh = null;
		try {
			myFileHandler =
			        new FileHandler(CrawljaxRunner.path + CrawljaxRunner.counter + "//Log.txt");
			myFileHandler.setFormatter(new SimpleFormatter());

			Xmlfh = new FileHandler(CrawljaxRunner.path + CrawljaxRunner.counter + "//log.xml");
			Xmlfh.setFormatter(new XMLFormatter());

		} catch (SecurityException e) {
			System.out.println("cannot open Log.txt for logging purpose");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("cannot open Log.txt for logging purpose");
			e.printStackTrace();
		}

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
			if (stateCondition[ID - 1].equals("invisible")) {
				try {
					getDomDifferences.getDifferences(previous.getDom(), s.getDom(), id);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			previous = s;
		}
		myLogger.addHandler(myFileHandler);
		myLogger.addHandler(Xmlfh);
		myLogger.log(Level.ALL, "Visibles: " + visibleState + " Invisibles: " + invisibleState
		        + " Visible Edges: " + visibleEdge + " Invisible Edges: " + invisibleEdge
		        + "\n-------------Edges---------" + "\nVisible Edges are:\n");
		int i;
		for (i = 0; i < visibleEdge; i++) {
			myLogger.log(Level.ALL, visiblearray[i]);
		}
		myLogger.log(Level.ALL, "---------------\n Invisible Edges are:");
		for (i = 0; i < invisibleEdge; i++) {
			myLogger.log(Level.ALL, invisiblearray[i]);
		}
		myFileHandler.flush();
		Xmlfh.flush();
		myFileHandler.close();
		Xmlfh.close();
		File domDifference =
		        new File(CrawljaxRunner.path + CrawljaxRunner.counter
		                + "//TotalChangeResultLog.txt");
		if (!domDifference.exists()) {
			FileWriter totalDomDifferences;
			try {
				totalDomDifferences =
				        new FileWriter(CrawljaxRunner.path + CrawljaxRunner.counter
				                + "//TotalChangeResultLog.txt");
				BufferedWriter out = new BufferedWriter(totalDomDifferences);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		totalDomDifferenceSize =
		        getDomDifferenceByteSize(CrawljaxRunner.path + CrawljaxRunner.counter
		                + "//TotalChangeResultLog.txt");
		printResults(totalDomDifferenceSize, session);

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
						stateCondition[getInvisibleState() + getVisibleState()] = "visible";
						setVisibleState(getVisibleState() + 1);
						break;
					}
				}
			}
			if (!visibleEdge) {
				stateCondition[getInvisibleState() + getVisibleState()] = "invisible";
				setInvisibleState(getInvisibleState() + 1);
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
				invisiblearray[getInvisibleEdge()] = edge.toString();
				setInvisibleEdge(getInvisibleEdge() + 1);
			}
			if (edge.getElement().getTag().equalsIgnoreCase("a")) {
				if (anchorVisible) {
					visiblearray[getVisibleEdge()] = edge.toString();
					setVisibleEdge(getVisibleEdge() + 1);
				} else {
					invisiblearray[getInvisibleEdge()] = edge.toString();
					setInvisibleEdge(getInvisibleEdge() + 1);
				}
			}
			if (edge.getElement().getTag().toLowerCase().equalsIgnoreCase("img")) {
				node = edge.getElement().getNode().getParentNode();
				imgVisible = imgChecker(node, edge);
				if (imgVisible) {
					visiblearray[getVisibleEdge()] = edge.toString();
					setVisibleEdge(getVisibleEdge() + 1);
				} else {
					invisiblearray[getInvisibleEdge()] = edge.toString();
					setInvisibleEdge(getInvisibleEdge() + 1);
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
			invisiblearray[getInvisibleEdge()] = edge.toString();
			stateCondition[getInvisibleState() + getVisibleState()] = "invisible";
			setInvisibleState(getInvisibleState() + 1);
			setInvisibleEdge(getInvisibleEdge() + 1);
			getEdgeInfo(edge);
		} else if (edge.getElement().getTag().equalsIgnoreCase("a")) {
			anchorVisible = anchorVisibilityChecking(edge);
			if (anchorVisible) {
				visiblearray[getVisibleEdge()] = edge.toString();
				stateCondition[getInvisibleState() + getVisibleState()] = "visible";
				setVisibleState(getVisibleState() + 1);
				setVisibleEdge(getVisibleEdge() + 1);
			} else {
				invisiblearray[getInvisibleEdge()] = edge.toString();
				stateCondition[getInvisibleState() + getVisibleState()] = "invisible";
				setInvisibleState(getInvisibleState() + 1);
				setInvisibleEdge(getInvisibleEdge() + 1);
			}

		} else if (edge.getElement().getTag().toLowerCase().equalsIgnoreCase("img")) {
			node = edge.getElement().getNode().getParentNode();
			imgVisible = imgChecker(node, edge);
			if (imgVisible) {
				visiblearray[getVisibleEdge()] = edge.toString();
				stateCondition[getInvisibleState() + getVisibleState()] = "visible";
				setVisibleState(getVisibleState() + 1);
				setVisibleEdge(getVisibleEdge() + 1);
			} else {
				invisiblearray[getInvisibleEdge()] = edge.toString();
				stateCondition[getInvisibleState() + getVisibleState()] = "invisible";
				setInvisibleState(getInvisibleState() + 1);
				setInvisibleEdge(getInvisibleEdge() + 1);
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
					avisibleCounter++;
					visible = true;
				} else {
					visible = false;
					ainvisibleCounter++;
				}
			}
		}
		return visible;
	}

	public void getEdgeInfo(Eventable edge) {
		if (edge.getElement().getTag().equals("DIV"))
			divCounter++;
		else if (edge.getElement().getTag().equals("SPAN"))
			spanCounter++;
		else if (edge.getElement().getTag().equals("INPUT"))
			inputCounter++;
		else if (edge.getElement().getTag().equals("BUTTON"))
			buttonCounter++;
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
						imginvisibleCounter++;
					} else {
						visible = true;
						imgvisibleCounter++;
					}
				}
			} else {
				imginvisibleCounter++;
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

	public void printResults(int size, CrawlSession session) {
		FileWriter finalResults;
		long timing = System.currentTimeMillis() - CrawljaxRunner.startTime;
		BufferedWriter out;
		try {
			finalResults =
			        new FileWriter(CrawljaxRunner.path + CrawljaxRunner.counter
			                + "//FinalResults.txt");
			out = new BufferedWriter(finalResults);
			out.write("URL: " + CrawljaxRunner.URL + "\nTotal States: "
			        + session.getStateFlowGraph().getAllStates().size() + "\nTotal Edges: "
			        + session.getStateFlowGraph().getAllEdges().size() + "\nVisible States: "
			        + visibleState + "\nInvisible States: " + invisibleState
			        + "\nVisible Edges: " + visibleEdge + "\nInvisible Edges: " + invisibleEdge
			        + "\nTotalDomDifferenceSize (Bytes): " + size
			        + "\nTotalDomDifferenceSize (KBytes): " + (size / 1024)
			        + "\n--------Clickables---------" + "\nA Visible: " + avisibleCounter
			        + "\nA Invisible: " + ainvisibleCounter + "\nDiv: " + divCounter + "\nSpan: "
			        + spanCounter + "\nImg Visible: " + imgvisibleCounter + "\nImg Invisible: "
			        + imginvisibleCounter + "\nInput: " + inputCounter + "\nButton: "
			        + buttonCounter + "\nStart Time: " + timing);

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
