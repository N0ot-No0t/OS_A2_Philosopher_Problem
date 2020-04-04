/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor {


    private enum status {eating, hungry, thinking} //different statuses of the philosophers
    private enum talkStatus {not_talking, request_talk, talking}//different conversation behavior of the philosophers
    private boolean someoneIsTalking = false; //this boolean will allow only one philosopher to speak

    private status state[];
    private talkStatus talkStates[];
    //private Semaphore self [];
    private int N; //will be used to retrieve the number of philosophers

    /**
     * Constructor
     */
    public Monitor(int piNumberOfPhilosophers) {

        state = new status[piNumberOfPhilosophers];
        //initialise all philosopher to thiking
        for (int i = 0; i < piNumberOfPhilosophers; i++) {
            state[i] = status.thinking;
        }
        talkStates = new talkStatus[piNumberOfPhilosophers];
        //initialise all conversation behavior of the philosopher to not talking
        for (int i = 0; i < piNumberOfPhilosophers; i++) {
            talkStates[i] = talkStatus.not_talking;
        }
        N = piNumberOfPhilosophers;//getting the number of philosophers

    }



    /**
     *
     * Here, we are checking the states of the neighbors of a given
     * philosopher to see if they can eat
     *
     * @param piTID ID of the philosopher
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

        //we check whether or not someone else is talking at the moment
        if (!someoneIsTalking) {
            someoneIsTalking = true;
            talkStates[piTID] = talkStatus.talking;
        }
        else {
            try{
                //when someone else is already talking, the philosopher that has requested
                //the right of speech will wait until the other philosopher is done talking
                while (someoneIsTalking) {
                    wait();
                }

                //when this point is reached, this means whoever was talking is done and we can obtain
                //the right of speech
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
     *
     * This method will simply change the variables to not talking and will let all the philosophers know that
     * the philosopher that was talking is done.
     */
    public synchronized void endTalk(final int piTID) {

        talkStates[piTID] = talkStatus.not_talking;
        someoneIsTalking = false;
        notifyAll();

    }
}

// EOF
