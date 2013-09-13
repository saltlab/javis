package ca.ubc.javis;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.crawljax.core.CandidateElement;

public class ElementCounter {

	private  static AtomicInteger anchors = new AtomicInteger();
	private  static AtomicInteger div = new AtomicInteger();
	private  static AtomicInteger span = new AtomicInteger();
	private  static AtomicInteger img = new AtomicInteger();
	private  static AtomicInteger button = new AtomicInteger();
	private  static AtomicInteger input = new AtomicInteger();

	public static void incrementAnchors() {
		anchors.incrementAndGet();
	}
	
	public static void incrementDivs() {
		div.incrementAndGet();
	}
	
	public static void incrementSpans() {
		span.incrementAndGet();
	}
	
	public static void incrementImages() {
		img.incrementAndGet();
	}
	
	public static void incrementButton() {
		button.incrementAndGet();
	}
	
	public static void incrementInputs() {
		input.incrementAndGet();
	}
	
	public static int getAnchors(){
		return anchors.get();
	}
	
	public static void setAnchors(int i){
		anchors.set(i);
	}
	public static int getDivs(){
		return div.get();
	}
	public static void setDivs(int i){
		div.set(i);
	}
	
	public static int getSpans(){
		return span.get();
	}
	public static void setSpans(int i){
		span.set(i);
	}
	
	public static int getImages(){
		return img.get();
	}
	public static void setImages(int i){
		img.set(i);
	}

	public static int getButtons(){
		return button.get();
	}
	
	public static void setButtons(int i){
		button.set(i);
	}
	public static int getInputs(){
		return input.get();
	}
	public static void setInputs(int i){
		input.set(i);
	}


	public void sortCandidateElements(List<CandidateElement> candidateElements) {
		for (CandidateElement candidateElement : candidateElements){
			if(candidateElement!=null)
			{
			if(candidateElement.getElement().getTagName()
			        .equalsIgnoreCase("A"))
				incrementAnchors();
			else if(candidateElement.getElement().getTagName().equalsIgnoreCase("DIV"))
				incrementDivs();
			else if(candidateElement.getElement().getTagName().equalsIgnoreCase("SPAN"))
				incrementSpans();
			else if(candidateElement.getElement().getTagName().equalsIgnoreCase("IMG"))
				incrementImages();
			else if(candidateElement.getElement().getTagName().equalsIgnoreCase("BUTTON"))
				incrementButton();
			else if(candidateElement.getElement().getTagName().equalsIgnoreCase("INPUT"))
				incrementInputs();
			}
		}
	}
}
