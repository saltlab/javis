package ca.ubc.javis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.bcel.generic.GETSTATIC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import ca.ubc.javis.log.DynamicLoggerFactory;

import com.crawljax.core.CrawlSession;
import com.crawljax.core.ExitNotifier.ExitStatus;
import com.crawljax.core.plugin.PostCrawlingPlugin;
import com.crawljax.core.state.Element;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.StateVertex;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class Javis implements PostCrawlingPlugin {

	private static Logger myLogger = LoggerFactory.getLogger(Javis.class);
	private static Logger sfgLogger = LoggerFactory.getLogger(Javis.class);
	public static ArrayList<String> visiblearray = new ArrayList<String>();
	public static ArrayList<String> invisiblearray = new ArrayList<String>();
	public static ArrayList<String> stateCondition = new ArrayList<String>();
	public static StringBuffer buffer = new StringBuffer();
	public static StateFlowGraphInformation sfgInformation = new StateFlowGraphInformation();
	public static boolean indexFlag = true;
	private String pathIdentifier = JavisRunner.counter + JavisRunner.name;
	private static ArrayList<StateVertex> states;
	/*	private final Logger myLogger;
	
	public Javis(Logger siteLog) {
		myLogger = siteLog;
	}*/

	@Override
	public void postCrawling(CrawlSession session, ExitStatus exitReason) {
		
		int totalDomDifferenceSize;
		
		ImmutableSet<StateVertex> statesInStateFlowGraph = session.getStateFlowGraph().getAllStates();
		reproduceStateFlowGraphWithCorrectID(statesInStateFlowGraph);
		int id;
		for(StateVertex s : states){
			Set<Eventable> eve = session.getStateFlowGraph().getIncomingClickable(s);
			if (eve.size() == 0) {
				indexFlag = false;
				continue;
			}
			stateCategorization(eve);
			sfgLogger.info(getStateName(s) + " Incoming Edge(s): "
			        + session.getStateFlowGraph().getIncomingClickable(s) + "\n");

			if(!indexFlag){
				id = getId(s)-1;
				
			}else{
				id = getId(s);
			}
			 
			sfgLogger.info("Edge is: " + eve + "Condition: " + stateCondition.get(id));
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
		        new File(JavisRunner.path + pathIdentifier + "/TotalChangeResultLog.txt");
		if (!domDifference.exists()) {
			FileWriter totalDomDifferences;
			try {
				totalDomDifferences =
				        new FileWriter(JavisRunner.path + pathIdentifier + "/TotalChangeResultLog.txt");
				BufferedWriter out = new BufferedWriter(totalDomDifferences);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			totalDomDifferenceSize =
			        getDomDifferenceByteSize(JavisRunner.path + pathIdentifier + "/TotalChangeResultLog.txt");
			int contentSize = extractContents();
			printResults(totalDomDifferenceSize, contentSize, session);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

	private static String getStateName(StateVertex currentState) {
		String name = "";
		for(int i = 0 ; i < states.size() ; i++){
			if(currentState.equals(states.get(i)))
				if(i == 0){
					name = "index";
				}else{
					name = "state" + i;	
				}
		}
		return name;
	}

	private void reproduceStateFlowGraphWithCorrectID(
			ImmutableSet<StateVertex> statesBeforeCorrectingID) {
		ArrayList<StateVertex> statesInStateFlowGraph = new ArrayList<StateVertex>();
		int k = 0;
		for(StateVertex s : statesBeforeCorrectingID){
			statesInStateFlowGraph.add(k, s);
			if(k == statesBeforeCorrectingID.size())
				break;
			k++;
		}
		states = statesInStateFlowGraph;
		
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
					StateVertex src1 = getPreviousState(session, dest);
					if (src1.getName().equalsIgnoreCase("index")) {
						sourceState = "index";
						GetDomDifferences.calculateAndSave(src1.getDom(), dest.getDom(),
						        (Integer.toString(i + 1) + "from" + sourceState));
					} else {
						fromState = getCondition(src1.getName());
						fromState++;
						GetDomDifferences
						        .calculateAndSave(src1.getDom(), dest.getDom(),
						                (Integer.toString(i + 1) + "from" + (Integer
						                        .toString(fromState))));
					}

				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private StateVertex getPreviousState(CrawlSession session, StateVertex dest) {

		StateVertex result = null;
		Set<StateVertex> allStates = session.getStateFlowGraph().getAllStates();
		Iterator<StateVertex> stateID = allStates.iterator();
		StateVertex s = stateID.next();
		ImmutableSet<Eventable> eventables = session.getStateFlowGraph().getIncomingClickable(dest);
		for(Eventable event : eventables){
			result = event.getSourceStateVertex();	
			break;
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
				if (i == getCondition(getStateName(s))) {
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

	private int getId(StateVertex currentState) {
		int id = -1;
		for(int i = 0 ; i < states.size() ; i++){
			if(currentState.equals(states.get(i))){
				id = i;
				break;
			}
		}
		return id;
	}

	private int extractContents() {
		int size = 0;
		try {
			ContentExtraction newContent = new ContentExtraction();
			newContent.getContents();
			size =
			        getDomDifferenceByteSize(JavisRunner.path + pathIdentifier + "/TotalContent.txt");
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
			if(element.getTag().equals("A")){
				ImmutableMap<String, String> attributes = element.getAttributes();
				if (attributes != null) {
					if(attributes.get("href") != null){
						if(!hrefInvisible(attributes.get("href")))
								buffer.append(attributes.get("href"));
					}
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

		long timing = System.currentTimeMillis() - JavisRunner.startTime;
		checkElements();
		String result =
		        ("URL: " + JavisRunner.URL 
		        		+ "\n\t ---------"
		        		+ "\nTotal States: "
		                + session.getStateFlowGraph().getAllStates().size() 
		                +  "\nVisible States: "
		                + sfgInformation.getVisibleState()
		                + "\nInvisible States: "
		                + sfgInformation.getInvisibleState()
		                + "\n\t ---------"
		                + "\nTotal Edges: "
		                + session.getStateFlowGraph().getAllEdges().size() 
		                + "\nVisible Edges: "
		                + (sfgInformation.getVisibleEdge().get()) 
		                + "\nInvisible Edges: "
		                + sfgInformation.getInvisibleEdge()
		                + "\n\t ---------"
		                + "\nTotalDomDifferenceSize (Bytes): " + size
		                + "\nTotalDomDifferenceSize (KB): " + (size / 1024)
		                + "\nTotalContent (Bytes): "
		                + contentSize
		                + "\nTotalContent (KB) : "
		                + (contentSize / 1024)
		                + "\nDOM Size" 
		                + session.getStateFlowGraph().getMeanStateStringSize()
		                + "\n\t ---------"
		                + (contentSize / 1024) + "\nElapsed Time (milliseconds): " + timing
		                + "\n\t--------Clickables---------" + "\nA Visible: "
		                + (sfgInformation.getAVisCounter().get()) + "\nA Invisible: "
		                + sfgInformation.getAInvisCounter() + "\nDiv: "
		                + sfgInformation.getDivCounter() + "\nSpan: "
		                + sfgInformation.getSpanCounter() + "\nImg Visible: "
		                + sfgInformation.getImgVisCounter() + "\nImg Invisible: "
		                + sfgInformation.getImgInvisCounter() + "\nInput: "
		                + sfgInformation.getInputCounter() + "\nButton: " + sfgInformation
		                .getButtonCounter());
		try {
			Files.write(result, new File(JavisRunner.path + pathIdentifier + "/FinalResults.txt"), Charsets.UTF_8);
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
