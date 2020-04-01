import common.BaseThread;

import java.util.Random;

/**
 * Class Philosopher.
 * Outlines main subrutines of our virtual philosopher.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Philosopher extends BaseThread {
    /**
     * Max time an action can take (in milliseconds)
     */
    public static final long TIME_TO_WASTE = 1000;
    private Random r = new Random();

    /**
     * The act of eating.
     * - Print the fact that a given phil (their TID) has started eating.
     * - yield
     * - Then sleep() for a random interval.
     * - yield
     * - The print that they are done eating.
     */
    public void eat() {

        System.out.println("Philosopher [" + (getTID() - 1) + "] is eating...");

        try {
            //while(true){
            yield();
            sleep((long) (Math.random() * TIME_TO_WASTE));
            yield();
            //}
        } catch (InterruptedException e) {
            System.err.println("Philosopher.eat():");
            DiningPhilosophers.reportException(e);
            System.exit(1);
        }

        System.out.println("Philosopher [" + (getTID() - 1) + "] is done eating...");

    }

    /**
     * The act of thinking.
     * - Print the fact that a given phil (their TID) has started thinking.
     * - yield
     * - Then sleep() for a random interval.
     * - yield
     * - The print that they are done thinking.
     */
    public void think() {
        System.out.println("Philosopher [" + (getTID() - 1) + "] is thinking...");

        try {
            //while(true){
            yield();
            sleep((long) (Math.random() * TIME_TO_WASTE));
            yield();
            //}
        } catch (InterruptedException e) {
            System.err.println("Philosopher.eat():");
            DiningPhilosophers.reportException(e);
            System.exit(1);
        }

        System.out.println("Philosopher [" + (getTID() - 1) + "] is done thinking...");

    }

    /**
     * The act of talking.
     * - Print the fact that a given phil (their TID) has started talking.
     * - yield
     * - Say something brilliant at random
     * - yield
     * - The print that they are done talking.
     */
    public void talk() {
        System.out.println("Philosopher [" + (getTID() - 1) + "] is talking...");

//		try
//		{
        //while(true){
        yield();
        //sleep((long)(Math.random() * TIME_TO_WASTE));
        saySomething();
        yield();
        //}
        //}
//		catch(InterruptedException e)
//		{
//			System.err.println("Philosopher.eat():");
//			DiningPhilosophers.reportException(e);
//			System.exit(1);
//		}

        System.out.println("Philosopher [" + (getTID() - 1) + "] is done talking...");
    }

    /**
     * No, this is not the act of running, just the overridden Thread.run()
     */
    public void run() {
        for (int i = 0; i < DiningPhilosophers.DINING_STEPS; i++) {
            DiningPhilosophers.soMonitor.pickUp(getTID() - 1);

            eat();

            DiningPhilosophers.soMonitor.putDown(getTID() - 1);

            think();

            /*
             * TODO:
             * A decision is made at random whether this particular
             * philosopher is about to say something terribly useful.
             */



            if ((r.nextInt(2) + 1) == 1) {

                // Some monitor ops down here...

                DiningPhilosophers.soMonitor.requestTalk(getTID() - 1);

                talk();

                DiningPhilosophers.soMonitor.endTalk(getTID() - 1);
                // ...
            }

            yield();
        }
    } // run()

    /**
     * Prints out a phrase from the array of phrases at random.
     * Feel free to add your own phrases.
     */
    public void saySomething() {
        String[] astrPhrases =
                {
                        "Eh, it's not easy to be a philosopher: eat, think, talk, eat...",
                        "You know, true is false and false is true if you think of it",
                        "2 + 2 = 5 for extremely large values of 2...",
                        "If thee cannot speak, thee must be silent",
                        "My number is " + (getTID() - 1) + "",
                        "Roses are red, violets are blue, it don't always be like that but sometimes it do",
                        "Roses are red, violets are blue, vodka is cheaper than dinner for two"
                };

        System.out.println
                (
                        "Philosopher " + (getTID() - 1) + " says: " +
                                astrPhrases[(int) (Math.random() * astrPhrases.length)]
                );
    }
}

// EOF
