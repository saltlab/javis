package ca.ubc.javis;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicInteger;

public class StateFlowGraphInformation {

	private static AtomicInteger invisibleState = new AtomicInteger();
	private static AtomicInteger visibleState = new AtomicInteger();
	private static AtomicInteger invisibleEdge = new AtomicInteger();
	private static AtomicInteger visibleEdge = new AtomicInteger();
	private static AtomicInteger divCounter = new AtomicInteger();
	private static AtomicInteger spanCounter = new AtomicInteger();
	private static AtomicInteger inputCounter = new AtomicInteger();
	private static AtomicInteger buttonCounter = new AtomicInteger();
	private static AtomicInteger avisibleCounter = new AtomicInteger();
	private static AtomicInteger ainvisibleCounter = new AtomicInteger();
	private static AtomicInteger imgvisibleCounter = new AtomicInteger();
	private static AtomicInteger imginvisibleCounter = new AtomicInteger();

	public AtomicInteger getVisibleState() {
		return visibleState;
	}
	public AtomicInteger getDivCounter() {
		return divCounter;
	}


	public AtomicInteger getSpanCounter() {
		return spanCounter ;
	}


	public AtomicInteger getInputCounter() {
		return inputCounter ;
	}

	public AtomicInteger getButtonCounter() {
		return buttonCounter ;
	}


	public AtomicInteger getAVisCounter() {
		return avisibleCounter ;
	}


	public AtomicInteger getAInvisCounter() {
		return ainvisibleCounter ;
	}

	public AtomicInteger getImgVisCounter() {
		return imgvisibleCounter ;
	}


	public AtomicInteger getImgInvisCounter() {
		return imginvisibleCounter ;
	}

	public AtomicInteger getInvisibleState() {
		return invisibleState ;
	}


	public AtomicInteger getVisibleEdge() {
		return visibleEdge ;
	}

	public AtomicInteger getInvisibleEdge() {
		return invisibleEdge ;
	}

}
