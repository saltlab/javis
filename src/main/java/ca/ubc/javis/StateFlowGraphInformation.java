package ca.ubc.javis;

public class StateFlowGraphInformation {

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

	public void setVisibleState(int visibleStates) {
		visibleState = visibleStates;
	}

	public int getVisibleState() {
		return visibleState;
	}

	public void setDivCounter(int div) {
		divCounter = div;
	}

	public int getDivCounter() {
		return divCounter;
	}

	public void setSpanCounter(int span) {
		spanCounter = span;
	}

	public int getSpanCounter() {
		return spanCounter;
	}

	public void setInputCounter(int input) {
		inputCounter = input;
	}

	public int getInputCounter() {
		return inputCounter;
	}

	public void setButtonCounter(int button) {
		buttonCounter = button;
	}

	public int getButtonCounter() {
		return buttonCounter;
	}

	public void setAVisCounter(int avis) {
		avisibleCounter = avis;
	}

	public int getAVisCounter() {
		return avisibleCounter;
	}

	public void setAInvisCounter(int ainvis) {
		ainvisibleCounter = ainvis;
	}

	public int getAInvisCounter() {
		return ainvisibleCounter;
	}

	public void setImgVisCounter(int imgvis) {
		imgvisibleCounter = imgvis;
	}

	public int getImgVisCounter() {
		return imgvisibleCounter;
	}

	public void setImgInisCounter(int imginvis) {
		imginvisibleCounter = imginvis;
	}

	public int getImgInvisCounter() {
		return imginvisibleCounter;
	}

	public void setInvisibleState(int invisibleStates) {
		invisibleState = invisibleStates;
	}

	public int getInvisibleState() {
		return invisibleState;
	}

	public void setVisibleEdge(int visibleEdges) {
		visibleEdge = visibleEdges;
	}

	public int getVisibleEdge() {
		return visibleEdge;
	}

	public void setInvisibleEdge(int invisibleEdges) {
		invisibleEdge = invisibleEdges;
	}

	public int getInvisibleEdge() {
		return invisibleEdge;
	}

}
