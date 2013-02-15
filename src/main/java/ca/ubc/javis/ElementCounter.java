package ca.ubc.javis;
import java.util.concurrent.atomic.AtomicInteger;

public class ElementCounter {

	private final static AtomicInteger anchors = new AtomicInteger();
	private final static AtomicInteger div = new AtomicInteger();
	private final static AtomicInteger span = new AtomicInteger();
	private final static AtomicInteger img = new AtomicInteger();
	private final static AtomicInteger button = new AtomicInteger();
	private final static AtomicInteger input = new AtomicInteger();

	public void incrementAnchors() {
		anchors.incrementAndGet();
	}
	
	public void incrementDivs() {
		div.incrementAndGet();
	}
	
	public void incrementSpans() {
		span.incrementAndGet();
	}
	
	public void incrementImages() {
		img.incrementAndGet();
	}
	
	public void incrementButton() {
		button.incrementAndGet();
	}
	
	public void incrementInputs() {
		input.incrementAndGet();
	}
	
	public static AtomicInteger getAnchors(){
		return anchors;
	}
	
	public static AtomicInteger getDivs(){
		return div;
	}
	
	public static AtomicInteger getSpans(){
		return span;
	}
	
	public static AtomicInteger getImages(){
		return img;
	}
	
	public static AtomicInteger getButtons(){
		return button;
	}
	
	public static AtomicInteger getInputs(){
		return input;
	}
}
