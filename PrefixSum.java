import java.util.Arrays;

// Class to store input and output arrays
class Data {
	public int[] input;
	public int[] output;
	public int[][] scratch;

	Data(int size) {

		// Compute the number of phases
		int phases = (int) Math.ceil(Math.log(size)/Math.log(2));

		// Allocate the required scratch space to run the algorithm
		scratch = new int[phases+1][size];

		// Input and output arrays are the first and last arrays in the sequence
		input = scratch[0];
		output = scratch[scratch.length-1];

		// Initialize all entries of the scratch space
		for(int i = 0; i < input.length; ++i) {
			input[i] = 1;
		}

		for(int i = 1; i < scratch.length; ++i) {
			for(int j = 0; j < scratch[i].length; ++j) {
				scratch[i][j] = 0;
			}
		}


	}

	// Tests the validity of the output array
	public boolean sanityCheck( ) {

		for(int i = 0; i < output.length; ++i) {
			if (output[i] != i+1) return false;
		}

		return true;
	}

	public String toString( ) {
		return Arrays.deepToString(scratch);
	}

	public String inputToString( ) {
		return Arrays.toString(input);
	}

	public String outputToString( ) {
		return Arrays.toString(output);
	}
}



// Implements a thread
class Worker implements Runnable {

	public Worker(int identifier, Data data, Barrier barrier) {
		this.identifier = identifier;
		this.data = data;
		this.barrier = barrier;
	}

	public void run( ) {


		int sizeOfChunk = data.scratch[0].length/barrier.size( );
		int start = identifier * sizeOfChunk;
		int finish = identifier == barrier.size( ) - 1 ? data.scratch[0].length : start + sizeOfChunk;
		int fixed = 1;

		// Execute phases
		for(int i = 0; i < data.scratch.length - 1; ++i) {
			// Populate array[i+1] using array[i]
			// Compute the entry values for my portion
			for(int j = start; j < finish; ++j) {
				if (j < fixed) {
					data.scratch[i+1][j] = data.scratch[i][j];
				} else {
					data.scratch[i+1][j] = data.scratch[i][j] + data.scratch[i][j-fixed];
				}
			}
			fixed *= 2;

			// Wait until all threads have completed this phase
			barrier.arriveAndWait( );
		}
	}

	int identifier;
	Data data;
	Barrier barrier;
}


public class PrefixSum {

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Incorrect number of arguments");
			return;
		}

		int sizeOfArray = Integer.parseInt(args[0]);
		int numberOfThreads = Integer.parseInt(args[1]);

		if ((sizeOfArray <= 0) || (numberOfThreads <= 0)) {
			System.out.println("Invalid arguments");
			return;
		}

		// Allocate space for data and initialize it
		Data data = new Data(sizeOfArray);
		// Create a barrier
		Barrier barrier = new Barrier(numberOfThreads);

		// Create an array to store thread objects
		Thread[] workers = new Thread[numberOfThreads];

		// Create worker threads
		for(int i = 0; i < workers.length; ++i) {
			workers[i] = new Thread(new Worker(i, data, barrier));
		}
		// Start worker threads
		for(int i = 0; i < workers.length; ++i) {
			workers[i].start( );
		}

		// Wait for all worker threads to complete
		for(int i = 0; i < workers.length; ++i) {
			try {
				workers[i].join( );
			} catch (Exception e) {
				System.out.println("Exception caught while waiting for a thread to join!");
			}

		}


		// Check the output array
		System.out.println(data.sanityCheck( ));
	}

}
