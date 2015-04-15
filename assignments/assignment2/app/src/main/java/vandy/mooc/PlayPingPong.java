package vandy.mooc;

import java.util.concurrent.CyclicBarrier;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * @class PlayPingPong
 *
 * @brief This class uses elements of the Android HaMeR framework to
 *        create two Threads that alternately print "Ping" and "Pong",
 *        respectively, on the display.
 */
public class PlayPingPong implements Runnable {
    /**
     * Keep track of whether a Thread is printing "ping" or "pong".
     */
    private enum PingPong {
        PING, PONG
    };

    /**
     * Number of iterations to run the ping-pong algorithm.
     */
    private final int mMaxIterations;

    /**
     * The strategy for outputting strings to the display.
     */
    private final OutputStrategy mOutputStrategy;

    /**
     * Define a pair of Handlers used to send/handle Messages via the
     * HandlerThreads.
     */
    private Handler[] mHandlers = new Handler[2];
    // @@ TODO - you fill in here.

    /**
     * Define a CyclicBarrier synchronizer that ensures the
     * HandlerThreads are fully initialized before the ping-pong
     * algorithm begins.
     */
    private CyclicBarrier mBarrier = new CyclicBarrier(2);
    // @@ TODO - you fill in here.

    /**
     * Implements the concurrent ping/pong algorithm using a pair of
     * Android Handlers (which are defined as an array field in the
     * enclosing PlayPingPong class so they can be shared by the ping
     * and pong objects).  The class (1) extends the HandlerThread
     * superclass to enable it to run in the background and (2)
     * implements the Handler.Callback interface so its
     * handleMessage() method can be dispatched without requiring
     * additional subclassing.
     */
    class PingPongThread extends HandlerThread implements Handler.Callback {
        /**
         * Keeps track of whether this Thread handles "pings" or
         * "pongs".
         */
        private PingPong mMyType;

        /**
         * Number of iterations completed thus far.
         */
        private int mIterationsCompleted;

        /**
         * Constructor initializes the superclass and type field
         * (which is either PING or PONG).
         */
        public PingPongThread(PingPong myType) {
        	super(myType.toString());
            mMyType = myType;
        }

        private Handler getHandler() {
            return mMyType == PingPong.PING ? mHandlers[0] : mHandlers[1];
        }

        private Handler getOtherHandler() {
            return mMyType == PingPong.PING ? mHandlers[1] : mHandlers[0];
        }

        /**
         * This hook method is dispatched after the HandlerThread has
         * been started.  It performs ping-pong initialization prior
         * to the HandlerThread running its event loop.
         */
        @Override    
        protected void onLooperPrepared() {
            // Create the Handler that will service this type of
            // Handler, i.e., either PING or PONG.
            // @@ TODO - you fill in here.
            int index = mMyType == PingPong.PING ? 0 : 1;
            mHandlers[index] = new Handler(getLooper(), this);

            try {
                mBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mMyType == PingPong.PING) {
                Message message = Message.obtain(getHandler(), 0, getOtherHandler());
                message.sendToTarget();
            }
        }

        /**
         * Hook method called back by HandlerThread to perform the
         * ping-pong protocol concurrently.
         */
        @Override
        public boolean handleMessage(Message reqMsg) {
            // Print the appropriate string if this thread isn't done
            // with all its iterations yet.
            // @@ TODO - you fill in here, replacing "true" with the
            // appropriate code.
            if (mIterationsCompleted < mMaxIterations && reqMsg.obj instanceof Handler) {
                Handler obj = (Handler) reqMsg.obj;
                mOutputStrategy.print(mMyType == PingPong.PING ? "ping" : "pong");
                obj.sendMessage(Message.obtain(obj, 0, reqMsg.getTarget()));
                mIterationsCompleted++;
            } else {
                quit();

                // Shutdown the HandlerThread to the main PingPong
                // thread can join with it.
                // @@ TODO - you fill in here.
            }

            // Create a Message that contains the Handler as the
            // reqMsg "target" and our Handler as the "obj" to use for
            // the reply.
            // @@ TODO - you fill in here.

            // Return control to the Handler in the other
            // HandlerThread, which is the "target" of the msg
            // parameter.
            // @@ TODO - you fill in here.

            return true;
        }
    }

    /**
     * Constructor initializes the data members.
     */
    public PlayPingPong(int maxIterations,
                        OutputStrategy outputStrategy) {
        // Number of iterations to perform pings and pongs.
        mMaxIterations = maxIterations;

        // Strategy that controls how output is displayed to the user.
        mOutputStrategy = outputStrategy;
    }

    /**
     * Start running the ping/pong code, which can be called from a
     * main() method in a Java class, an Android Activity, etc.
     */
    public void run() {
        // Let the user know we're starting. 
        mOutputStrategy.print("Ready...Set...Go!");

        PingPongThread ping = new PingPongThread(PingPong.PING);
        PingPongThread pong = new PingPongThread(PingPong.PONG);
        ping.start();
        pong.start();
        // Create the ping and pong threads.
        // @@ TODO - you fill in here.

        // Start ping and pong threads, which cause their Looper to
        // loop.
        // @@ TODO - you fill in here.


        try {
            ping.join();
            pong.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Let the user know we're done.
        mOutputStrategy.print("Done!");
    }
}
