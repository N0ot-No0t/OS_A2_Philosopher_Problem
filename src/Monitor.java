/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor {
    /*
     * ------------
     * Data members
     * ------------
     */

    private enum status {eating, hungry, thinking}
    private enum talkStatus {not_talking, request_talk, talking}
    private boolean someoneIsTalking = false;

    private status state[];
    private talkStatus talkStates[];
    //private Semaphore self [];
    private int N;

    /**
     * Constructor
     */
    public Monitor(int piNumberOfPhilosophers) {
        // TODO: set appropriate number of chopsticks based on the # of philosophers

        state = new status[piNumberOfPhilosophers];
        for (int i = 0; i < piNumberOfPhilosophers; i++) {
            state[i] = status.thinking;
        }
        talkStates = new talkStatus[piNumberOfPhilosophers];
        for (int i = 0; i < piNumberOfPhilosophers; i++) {
            talkStates[i] = talkStatus.not_talking;
        }
        //self = new Semaphore[piNumberOfPhilosophers];
        N = piNumberOfPhilosophers;

    }

    /*
     * -------------------------------
     * User-defined monitor procedures
     * -------------------------------
     */

    private synchronized void testEat(final int piTID) {

        int specialCaseNb = 0;

        //if you are the first philosopher and you check on your left, the neighbor
        // must be the last philosopher of the array (round table)
        if (piTID == 0) {
            specialCaseNb = N;
        } else {
            specialCaseNb = piTID;
        }

		/*System.out.println("\n****");
		System.out.println("pid:  " + piTID);
		System.out.println("left neighbor status: (state[(specialCaseNb - 1) % N], left neighbor pid: " + (specialCaseNb - 1) % N + ", result: " + (state[(specialCaseNb - 1) % N]));
		System.out.println("status that is checked: status.eating, result: " + status.eating);
		System.out.println("condition#1: (state[(specialCaseNb - 1) % N] != status.eating, result: " + (state[(specialCaseNb - 1) % N] != status.eating));
		System.out.println("-------");
		System.out.println("myself status: state[piTID], result: " + state[piTID]);
		System.out.println("status that is checked: status.hungry, result: " + status.hungry);
		System.out.println("condition#2: state[piTID] == status.hungry, result: " + (state[piTID] == status.hungry));
		System.out.println("-------");
		System.out.println("right neighbor status: state[(piTID + 1) % N], right neighbor pid: " + (piTID + 1) % N + ", result: " + state[(piTID + 1) % N]);
		System.out.println("status that is checked: status.eating, result: " + status.eating);
		System.out.println("condition#3: state[(piTID + 1) % N] != status.eating), result: " + (state[(piTID + 1) % N] != status.eating));
		System.out.println("****\n");*/


        //This if statement checks two things, if the left and right neighbors of the philosopher that is being test are eating and if the
        //philosopher that is being tested is hungry. If one of these conditions fail, then the philosopher cannot eat. If all these conditions
        //are met, then the philosopher will eat.
        if ((state[(specialCaseNb - 1) % N] != status.eating) && (state[piTID] == status.hungry) && (state[(piTID + 1) % N] != status.eating)) {

            //System.out.println("Inside test method ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ Philosopher ["+piTID+"] is eating...");

            //The state of the philosopher that is being tested is set to eating since they can now eat.
            state[piTID] = status.eating;

        }

    }

    /**
     * Grants request (returns) to eat when both chopsticks/forks are available.
     * Else forces the philosopher to wait()
     */
    public synchronized void pickUp(final int piTID) {
        //System.out.println("piTID: "+piTID+"\n");

        //You want to pick up the chopsticks since you are hungry so set your own state as hungry.
        state[piTID] = status.hungry;

        //You want to see if your left and right neighbors are eating which will decide if you can eat or not.
        testEat(piTID);

        //To enter in this while, you must have failed the if statement in the testEat method meaning that either your left
        //neighbor is eating, or your right neighbor is eating. In that case, you wait until someone notifies you so that
        //you make check if someone tested you and changed your status from hungry to eating.
        while (state[piTID] != status.eating) {
            try {
                wait();
            } catch (InterruptedException ie) {

                DiningPhilosophers.reportException(ie);

            }
        }

    }

    /**
     * When a given philosopher's done eating, they put the chopstiks/forks down
     * and let others know they are available.
     */
    public synchronized void putDown(final int piTID) {
        state[piTID] = status.thinking;

        int specialCaseNb = 0;

        //if you are the first philosopher and you check on your left, the neighbor
        // must be the last philosopher of the array (round table)
        if (piTID == 0) {
            specialCaseNb = N;
        } else {
            specialCaseNb = piTID;
        }

        //In this case, since you are done eating, you now want to see if your left and right neighbors can eat.
        //If they can and they are hungry, then you will update their status to eating and you will then notify them
        //which will make them wake up, check their own statuses, and go eat if their status is eating.
        testEat((specialCaseNb - 1) % N);
        testEat((piTID + 1) % N);

        notifyAll();
    }

    /**
     * Only one philosopher at a time is allowed to philosophy
     * (while she is not eating).
     */
    public synchronized void requestTalk(final int piTID) {

        talkStates[piTID] = talkStatus.request_talk;

        if (!someoneIsTalking) {
            someoneIsTalking = true;
            talkStates[piTID] = talkStatus.talking;
        }
        else {
            try{
                while (someoneIsTalking) {
                    wait();
                }

                someoneIsTalking = true;
                talkStates[piTID] = talkStatus.talking;
            }catch (InterruptedException ie){
                DiningPhilosophers.reportException(ie);
            }
        }

    }

    /**
     * When one philosopher is done talking stuff, others
     * can feel free to start talking.
     */
    public synchronized void endTalk(final int piTID) {

        talkStates[piTID] = talkStatus.not_talking;
        someoneIsTalking = false;
        notifyAll();

    }
}

// EOF
