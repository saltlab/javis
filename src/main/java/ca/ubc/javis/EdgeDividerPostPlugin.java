package ca.ubc.javis;

//import org.apache.commons.validator.routines;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.UrlValidator;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import ca.ubc.javis.unixdiff.TargetedDiff;

import com.crawljax.core.CrawlSession;
import com.crawljax.core.plugin.PostCrawlingPlugin;
import com.crawljax.core.state.Attribute;
import com.crawljax.core.state.Element;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.StateVertix;
import com.sun.corba.se.impl.orb.ParserAction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

public class EdgeDividerPostPlugin implements PostCrawlingPlugin {

	public static int invisibleState;
	public static int visibleState;
	public static int invisibleEdge;
	public static int visibleEdge;
	public static int divCounter;
	public static int spanCounter;
	public static int inputCounter;
	public static int buttonCounter;
	public static int avisibleCounter;
	public static int ainvisibleCounter;
	public static int imgvisibleCounter;
	public static int imginvisibleCounter;
	public static Logger myLogger = Logger.getLogger(EdgeDividerPostPlugin.class.getName());
	public static String[] visiblearray= new String[500];
	public static String[] invisiblearray=new String[500];
	public static String[] stateCondition = new String[500];
	public static StringBuffer buffer = new StringBuffer();
	
	
	public EdgeDividerPostPlugin()
	{
	}
	
	
	@Override
	public void postCrawling(CrawlSession session) {
	
		// configuring logger
		myLogger.setLevel(Level.ALL);
		int totalDomDifferenceSize;
		FileHandler myFileHandler = null;
		FileHandler Xmlfh = null;
		try {
			myFileHandler = new FileHandler("Log.txt");
			myFileHandler.setFormatter(new SimpleFormatter());
			
			Xmlfh = new FileHandler("log.xml");
			Xmlfh.setFormatter(new XMLFormatter());
			
			
		} catch (SecurityException e) {
			System.out.println("cannot open Log.txt for logging purpose");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("cannot open Log.txt for logging purpose");
			e.printStackTrace();
		}
		

		// end of logger configuration.	

		// Getting All of the Edges which have caused transition to a new state
 		
		Set <StateVertix> states = session.getStateFlowGraph().getAllStates();
		java.util.Iterator<StateVertix>  stateID = states.iterator();
		StateVertix previous = stateID.next();
		while (stateID.hasNext()){
			StateVertix s = stateID.next();
			if(s.getName().equals("index"))
				continue;
			Set <Eventable> eve = session.getStateFlowGraph().getIncomingClickable(s);
			String name = s.getName();
			stateCategorization(eve);
			String id = name.substring(5, name.length());
			int ID = Integer.parseInt(id);
			if(stateCondition[ID-1].equals("invisible")){
				try {
					getDomDifferences.getDifferences(previous.getDom(), s.getDom() , id);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			previous = s;
		}
		myLogger.addHandler(myFileHandler);
		myLogger.addHandler(Xmlfh);
		myLogger.log(Level.ALL, "Visibles: "+visibleState+" Invisibles: "+invisibleState+" Visible Edges: "+visibleEdge+" Invisible Edges: "+invisibleEdge+"-------------Edges---------"+"\nVisible Edges are:\n");
		int i;
		for(i = 0; i<visibleEdge;i++){
			myLogger.log(Level.ALL,visiblearray[i]);
		}
		myLogger.log(Level.ALL, "---------------\n Invisible Edges are:");
		for(i = 0; i<invisibleEdge;i++){
			myLogger.log(Level.ALL, invisiblearray[i]);
		}
		myFileHandler.flush();
		Xmlfh.flush();
		myFileHandler.close();
		Xmlfh.close();
		File domDifference = new File("/ubc/ece/home/am/grads/janab/Github/javis/TotalChangeResultLog.txt");
		if(!domDifference.exists()){
			FileWriter totalDomDifferences;
			try {
				totalDomDifferences = new FileWriter("TotalChangeResultLog.txt");
				BufferedWriter out = new BufferedWriter(totalDomDifferences);
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}			
		totalDomDifferenceSize = getDomDifferenceByteSize("/ubc/ece/home/am/grads/janab/Github/javis/TotalChangeResultLog.txt");
		printResults(totalDomDifferenceSize);
	}
	
public void stateCategorization(Set <Eventable> event){
	Node node;
	Boolean anchorVisible = false;
	Boolean imgVisible = false;
	Eventable edge;
	java.util.Iterator<Eventable> edg = event.iterator();
	if(event.size()==1){
		edge = edg.next();
		if(edge.getElement().getTag().equalsIgnoreCase("div")||edge.getElement().getTag().equalsIgnoreCase("span")||
			edge.getElement().getTag().equalsIgnoreCase("input")||edge.getElement().getTag().equalsIgnoreCase("button")){
			invisiblearray[invisibleEdge] = edge.toString();
			stateCondition[invisibleState+visibleState] = "invisible";
			invisibleState++;
			invisibleEdge++;
			getEdgeInfo(edge);
		}
		if(edge.getElement().getTag().equalsIgnoreCase("a")){
			anchorVisible = anchorVisibilityChecking(edge);
			if(anchorVisible){
				visiblearray[visibleEdge] = edge.toString();
				stateCondition[invisibleState+visibleState] = "visible";
				visibleState++;
				visibleEdge++;
			}
			else{
				invisiblearray[invisibleEdge] = edge.toString();
				stateCondition[invisibleState+visibleState] = "invisible";
				invisibleState++;
				invisibleEdge++;
			}
				
		}
		if(edge.getElement().getTag().toLowerCase().equalsIgnoreCase("img")){
			node = edge.getElement().getNode().getParentNode();
			imgVisible = imgChecker(node,edge);
			if(imgVisible){
				visiblearray[visibleEdge] = edge.toString();
				stateCondition[invisibleState+visibleState] = "visible";
				visibleState++;
				visibleEdge++;
				}
			else{
				invisiblearray[invisibleEdge] = edge.toString();
				stateCondition[invisibleState+visibleState] = "invisible";
				invisibleState++;
				invisibleEdge++;
			}
		}	
	}else if(event.size()>1){
		Set <Eventable> checkingEdges = event;
		while(edg.hasNext()){
			edge = edg.next();
			if(edge.getElement().getTag().equalsIgnoreCase("a"))
				if(anchorVisibilityChecking(edge)){
					stateCondition[invisibleState+visibleState] = "visible";
					visibleState++;	
				}
				else{
					stateCondition[invisibleState+visibleState] = "invisible";
					invisibleState++;
					}
		}
		java.util.Iterator<Eventable> edg1 = checkingEdges.iterator();
		while(edg1.hasNext()){
			edge = edg1.next();
			if(edge.getElement().getTag().equalsIgnoreCase("div")||edge.getElement().getTag().equalsIgnoreCase("span")||
					edge.getElement().getTag().equalsIgnoreCase("input")||edge.getElement().getTag().equalsIgnoreCase("button")){
					getEdgeInfo(edge);
					invisiblearray[invisibleEdge] = edge.toString();
					invisibleEdge++;
				}
				if(edge.getElement().getTag().equalsIgnoreCase("a")){
					anchorVisible = anchorVisibilityChecking(edge);
					if(anchorVisible){
						visiblearray[visibleEdge] = edge.toString();
						visibleEdge++;
					}
					else{
						invisiblearray[invisibleEdge] = edge.toString();
						invisibleEdge++;	
					}
				}
				if(edge.getElement().getTag().toLowerCase().equalsIgnoreCase("img")){
					node = edge.getElement().getNode().getParentNode();
					imgVisible = imgChecker(node,edge);
					if(imgVisible){
						visiblearray[visibleEdge] = edge.toString();
						visibleEdge++;
						}
					else{
						invisiblearray[invisibleEdge] = edge.toString();
						invisibleEdge++;
					}
				}	
			}	
		}
	} 	

public Boolean anchorVisibilityChecking(Eventable edge){
	StringBuffer hrefValue = new StringBuffer();
	Boolean hrefFlag = false;
	Boolean URLFlag = false;
	String newHref = "";
	Boolean visible = false;
	String anchorHrefValue="";
	if(edge.getElement().getTag().toLowerCase().equals("a")){
		anchorHrefValue = getElementAttributes(edge.getElement());
		if(anchorHrefValue!="")
		{
			hrefFlag = true;
			hrefValue.delete(0, hrefValue.length());
			hrefValue.insert(0, anchorHrefValue);
			newHref=hrefValue.toString();
		}
		if(hrefFlag)
		{
			if(hrefInvisible(newHref))
				URLFlag=false;
			else
				URLFlag=true;
			if (URLFlag){
				avisibleCounter++;
				visible = true;
			}
			else{
				visible = false;
				ainvisibleCounter++;
			}
		}
	}return visible;
}

public void getEdgeInfo(Eventable edge){
	if(edge.getElement().getTag().equals("DIV"))
		divCounter++;
	if(edge.getElement().getTag().equals("SPAN"))
		spanCounter++;
	if(edge.getElement().getTag().equals("INPUT"))
		inputCounter++;
	if(edge.getElement().getTag().equals("BUTTON"))
		buttonCounter++;
}

public boolean UrlValidity (String url){

	GenericValidator urlValidator = new GenericValidator();
	
	boolean result = urlValidator.isUrl(url);
	
	return result;
}
	
public static boolean hrefInvisible(String str){
	boolean result=false;
	if(str.startsWith("#")||str.startsWith("javascript")||str.equals(""))
		result=true;
	return result;
}

public static String getElementAttributes(Element element){
	StringBuffer buffer = new StringBuffer();

	if (element != null) {
		List<Attribute> attributes = element.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attr =  attributes.get(i);
				if(attr.getName().equalsIgnoreCase("href"))
					buffer.append(attr.getValue());
			}
		}
	}
	return buffer.toString();
}

public Boolean imgChecker(Node node, Eventable edge){
	Boolean visible = false;
	if(!node.equals(null))
		if(node.getNodeName().equalsIgnoreCase("a")){
			if(node.hasAttributes()){
				String imgHrefValue =  getElementAttributes(edge.getElement());
				if(hrefInvisible(imgHrefValue)){
					imginvisibleCounter++;					
				}
				else{
					visible = true;
					imgvisibleCounter++;
					}
			}
		}
		else{
			imginvisibleCounter++;
		}
	return visible;
}

public int getDomDifferenceByteSize(String path){
	
	int size = 0;
	StringBuffer buffer = new StringBuffer();
	
		  // Open the file that is the first 
		  // command line parameter
		 FileReader file;
		try {
			
			file = new FileReader(path);
			BufferedReader br = new BufferedReader(file);
			  String strLine = "";
			  //Read File Line By Line
			  try {
				while ( br.readLine() != null)   {
					  buffer.append(br.readLine());
				  }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  strLine = buffer.toString().trim();
			  size = (strLine.getBytes().length);
			  //Close the input stream
			  try {
				
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		return size;
}

public void printResults(int size){
	
	FileWriter finalResults;
	BufferedWriter out;
	try {
		finalResults = new FileWriter("FinalResults.txt");
		out = new BufferedWriter(finalResults);	
		out.write("Visible States: "+visibleState+"\nInvisible States: "+invisibleState+"\nVisible Edges: "+visibleEdge+"\nInvisible Edges: "+invisibleEdge
				+"\nTotalDomDifferenceSize (Bytes): "+size+"\nTotalDomDifferenceSize (KBytes): "+(size/1024)+"\n--------Clickables---------"+"\nA Visible: "+avisibleCounter+"\nA Invisible: "+ainvisibleCounter+
				"\nDiv: "+divCounter+"\nSpan: "+spanCounter+"\nImg Visible: "+imgvisibleCounter+"\nImg Invisible: "+imginvisibleCounter+
				"\nInput: "+inputCounter+"\nButton: "+buttonCounter);

		out.close();	
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}

}

