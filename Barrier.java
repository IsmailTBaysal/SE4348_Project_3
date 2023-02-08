import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Barrier class to be implemented using monitors
public class Barrier {
	private final int size;
	private int count;
	private final Lock lock;
	private final Condition condition;

	// Constructor
	Barrier(int size) {
		this.size = size;
		this.count = 0;
		this.lock = new ReentrantLock();
		this.condition = lock.newCondition();
	}

	// Method to wait until all threads have arrived at the barrier
	public void arriveAndWait( ) {
		lock.lock();
		try {
			count++;
			if (count == size) {
				count = 0;
				condition.signalAll();
			}
			else {
				condition.await();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock(); }
	}
	// Returns the number of threads using the barrier
	public int size( ) {
		return size;
	}
}