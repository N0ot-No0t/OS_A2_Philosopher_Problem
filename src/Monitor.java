import java.util.concurrent.Semaphore;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */

	private enum status {eating, hungry, thinking}
	private status state [];
	//private Semaphore self [];
	private int N;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers

		state = new status[piNumberOfPhilosophers];
		for(int i = 0; i < piNumberOfPhilosophers; i++){
			state[i] = status.thinking;
		}
		//self = new Semaphore[piNumberOfPhilosophers];
		N = piNumberOfPhilosophers;

	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	private void test(final int piTID){

		if((state[(piTID - 1) % N] != status.eating) && (state[piTID] == status.hungry) && (state[(piTID + 1) % N] != status.eating)){
			state[piTID] = status.eating;
			this.notifyAll();
//			try {
//				self[piTID].acquire();
//			}catch (InterruptedException ie){
//
//			}

		}

	}

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		System.out.println("piTID: "+piTID+"\n");

		state[piTID] = status.hungry;
		test(piTID);
		if(state[piTID] != status.eating){
			try {
				//self[piTID].wait();
				this.wait();
			}catch (InterruptedException ie){

			}
		}

	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		state[piTID] = status.thinking;
		test((piTID - 1) % N);
		test((piTID + 1) % N);
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk()
	{
		// ...
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk()
	{
		// ...
	}
}

// EOF
