/**
 A class to measure time elapsed.
 Taken from UT CS314
*/

public class Stopwatch
{
    private long startTime;
    private long stopTime;
    private long totalTime;

    public static final double NANOS_PER_SEC = 1000000000.0;

	/**
	 start the stop watch.
	*/
	public void start(){
		startTime = System.nanoTime();
	}

	/**
	 stop the stop watch.
	*/
	public void stop()
	{	stopTime = System.nanoTime();
        totalTime += stopTime - startTime;	}

	/**
	elapsed time in seconds.
	@return the time recorded on the stopwatch in seconds
	*/
	public double time()
	{	return totalTime / NANOS_PER_SEC;	}

	public String toString(){
	    return "elapsed time: " + time() + " seconds.";
	}

	public void reset() {
		totalTime = 0;
	}

	/**
	elapsed time in nanoseconds.
	@return the time recorded on the stopwatch in nanoseconds
	*/
	public long timeInNanoseconds()
	{	return (stopTime - startTime);	}
}
