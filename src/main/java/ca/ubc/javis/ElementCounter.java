package ca.ubc.javis;
import java.util.concurrent.atomic.AtomicInteger;

public class ElementCounter {

	private final AtomicInteger anchors = new AtomicInteger();

	public void incrementAnchors() {
		anchors.incrementAndGet();
	}
}
