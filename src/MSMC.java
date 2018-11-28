import java.util.*;

class MultiSet {
	private int[] item;
	private int card;

	MultiSet(int[] item) {
//Variable item getting value from method toArray
		this.item = toArray(item);
	}

	int cardinality() {
		return card;
	}

	private int[] toArray(int[] item) {
//Creating a temp array to copy the input array item.
//Returning temp and holding cardinality to this.card
        int[] temp = new int[item.length];
		int cardinality = 0;
		for(int i = 0; i < item.length ; i++){
			temp[i] = item[i];
			cardinality += item[i];
		}
		this.card = cardinality;
		return temp;
	}

	MultiSet isect(MultiSet mset) {
//return null if multiset have different array size,
//else assign smallest int in a temp array and create,
//a new multiset with this array when for loop is finished
		if(this.item.length != mset.item.length){
			System.out.println("Oops! Different array size!");
			return null;
		}
		else{
			int[] temp = new int[mset.item.length];
			for(int i = 0; i < mset.item.length; i++){
				if(this.item[i] <= mset.item[i]){
					temp[i] = this.item[i];
				}
				else{
					temp[i] = mset.item[i];
				}
			}
			return new MultiSet(temp);
		}
	}

	MultiSet minus(MultiSet mset) {
//return null if multiset have different array size,
//else, assign max{0,A(x)-B(x)} in temp array and create,
//a new multiset with this array when for loop is finished
		if(this.item.length != mset.item.length){
			System.out.println("Oops! Different array size!");
			return null;
		}
		else{
			int[] temp = new int[mset.item.length];
			for(int i = 0; i < mset.item.length - 1; i++){
				int difference = this.item[i] - mset.item[i];
				if(difference <= 0){
					temp[i] = 0;
				}
				else{
					temp[i] = difference;
				}
			}
			return new MultiSet(temp);
		}
	}
//added this method so i can get the array for new Multiset,
//this.mset = mset constrain assignment "fix"
	int[] getItem() {
        return this.item;
    }
}

class Node {
	int cost;
	MultiSet mset;
	Node next;
	private double division;
//Node constructor
	Node(MultiSet mset, int cost) {
		this.mset = new MultiSet(mset.getItem());
        this.cost = cost;
        this.next = null;
        this.division = 0;
	}
	double getDivision(){
		return this.division;
	}
	void setDivision(double division) {
		this.division = division;
	}
}

class MSetList {
	int value;
	private int nbNodes;
	private Node first;
	private Node last;
	private int m;
	private int n;

	MSetList() {
	}

	int getM() {
		return m;
	}

	int getN() {
		return n;
	}

	MultiSet readFile(String filename) {
		java.io.BufferedReader br = null;
		int[] target = null;
		try {
			br = new java.io.BufferedReader(new java.io.FileReader(filename));
//Read dimensions
			String line = br.readLine();
			String[] data = line.split(" ");
//Changed m,n to this.m/n so i can keep them in an instance
			this.m = Integer.parseInt(data[0]);
			this.n = Integer.parseInt(data[1]);
			this.value = Integer.parseInt(data[2]);
//Read target
			line = br.readLine();
			if (line == null) {
				return null;
			}
			data = line.split(" ");
			target = new int[data.length];
			for (int i = 0; i < data.length; i++) {
				target[i] = Integer.parseInt(data[i]);
			}
//Read multisets
			while ((line = br.readLine()) != null) {
				data = line.split(" ");
//Read multiset cost
				int cost = Integer.parseInt(data[0]);
//Read multiset contents
				int[] item = new int[data.length - 1];
				for (int i = 1; i < data.length; i++) {
					item[i - 1] = Integer.parseInt(data[i]);
				}
				this.append(new MultiSet(item), cost);
			}
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (java.io.IOException ex) {
				ex.printStackTrace();
			}
		}
		return (new MultiSet(target));
	}

	private int length() {
		return this.nbNodes;
	}

	private boolean isEmpty() {
		return this.nbNodes == 0;
	}

	private void append(MultiSet mset, int cost) {
//We have 2 cases, empty or not empty list
		Node newnode = new Node(mset, cost);
		if (isEmpty()) {
//For empty list, this is Node 1, that is first AND last
			this.nbNodes = 1;
			this.first = newnode;
			this.last = newnode;
		} else {
//For other Nodes, new Node is the new last
			this.nbNodes += 1;
			this.last.next = newnode;
			this.last = newnode;
		}
	}

//We are getting all values from the object that is called on,
//and returning a MSetList type of object.
	public MSetList copy() {
		MSetList temp = new MSetList();
		temp.value = this.value;
		temp.nbNodes = this.nbNodes;
		temp.first = this.first;
		temp.last = this.last;
		return temp;
	}

	private Node rmBestCover(MultiSet target) {
		if (length() == 0) {
			System.out.println("List is empty");
			return null;
		} else {
//if the list is not empty we compute the best cost solution from all Multisets.
//For this, we need 2 Nodes to follow the search, and 2 Nodes to help us with the Goal Node.
//We initialize one temp Node and set it as a goal to. And we start to search our list for better,
//solution. When we do we keep that, and the previous Node, to help us delete them from the list.
			Node temp_node, temp_node_previous, goal_node, goal_node_previous = null;
			double minimum_value;
			temp_node = this.first;
			temp_node.setDivision(temp_node.cost / (double) temp_node.mset.isect(target).cardinality());
			minimum_value = temp_node.getDivision();
			goal_node = temp_node;
			for (int i = 1; i < length(); i++) {
				temp_node_previous = temp_node;
				temp_node = temp_node.next;
				temp_node.setDivision(temp_node.cost / (double) temp_node.mset.isect(target).cardinality());
				if (temp_node.getDivision() < minimum_value) {
					goal_node = temp_node;
					goal_node_previous = temp_node_previous;
					minimum_value = temp_node.getDivision();
				}
			}
			if (goal_node_previous == null) {
				this.first = goal_node.next;
			} else {
				goal_node_previous.next = goal_node_previous.next.next;
			}
			goal_node.next = null;
			nbNodes -= 1;
			return goal_node;
		}
	}

	MSetList cover(MultiSet target) {
//Our greedy solution is simple. We take the best Nodes first, one by one,
//and we add them to a new list, we add the total value to the list, and,
//we subtract the x values of the node from the target Multiset. We check,
//if the method got solution or not, and we return solution/null.
		MSetList solution = new MSetList();
		Node best_node;
		int i;
		for (i = 0; i < nbNodes; i++) {
			if (target.cardinality() == 0) {
				break;
			} else {
				best_node = rmBestCover(target);
				solution.append(best_node.mset, best_node.cost);
				solution.value += best_node.cost;
				target = target.minus(best_node.mset);
			}
		}
		if (i == nbNodes && target.cardinality() > 0) {
			return null;
		} else {
			return solution;
		}
	}

//extra solution
//This is a greedy algorithm based on the best cost/target.cardinality of Multisets.
//It gives a "heavier" set, but has better runtime, cause it's one,
//list assign, one mergesort on that list, and getting first values.
	MSetList sortGreedy(MultiSet target) {
		LinkedList list = new LinkedList();
		Node temp = this.first;
		temp.setDivision(temp.cost / (double) temp.mset.isect(target).cardinality());
		list.add(temp);
		for (int i = 1; i < length(); i++) {
			temp = temp.next;
			temp.setDivision(temp.cost / (double) temp.mset.isect(target).cardinality());
			list.add(temp);
		}
		list.sort(Comparator.comparingDouble(Node::getDivision));
		MSetList solution = new MSetList();
		Node best_node;
		int i;
		for (i = 0; i < list.size(); i++) {
			if (target.cardinality() == 0) {
				break;
			} else {
				best_node = ((Node) list.get(i));
				solution.append(best_node.mset, best_node.cost);
				solution.value += best_node.cost;
				target = target.minus(best_node.mset);
			}
		}
		if (i == list.size() && target.cardinality() > 0) {
			return null;
		} else {
			return solution;
		}
	}
}

class MSMC {
//Method for the file selection. We need to run our application,
//in the same folder with the text files. There is a way of dynamic,
//expand of files in folders but it's kind of tricky to learn it.
	private static String fileSelection(){
		int file_number;
		String filename = "";
		Scanner scan = new Scanner(System.in);
		do {
			System.out.println("File Selection");
			System.out.println("1)p200x5000.txt");
			System.out.println("2)p400x5000.txt");
			System.out.println("3)p500x1000.txt");
			System.out.println("4)p500x3000.txt");
			System.out.println("5)p500x5000.txt");
			System.out.println("6)p500x7000.txt");
			System.out.println("7)p500x9000.txt");
			System.out.println("8)p600x5000.txt");
			System.out.println("9)p800x5000.txt");
			System.out.println("10)p1000x5000.txt");
			switch (file_number = scan.nextInt()){
				case 1:
					filename = "p200x5000.txt";
					break;
				case 2:
					filename = "p400x5000.txt";
					break;
				case 3:
					filename = "p500x1000.txt";
					break;
				case 4:
					filename = "p500x3000.txt";
					break;
				case 5:
					filename = "p500x5000.txt";
					break;
				case 6:
					filename = "p500x7000.txt";
					break;
				case 7:
					filename = "p500x9000.txt";
					break;
				case 8:
					filename = "p600x5000.txt";
					break;
				case 9:
					filename = "p800x5000.txt";
					break;
				case 10:
					filename = "p1000x5000.txt";
					break;
				default:
					System.out.println("Invalid file option.");
					break;
			}
		} while(file_number < 1 || file_number > 10);
		return filename;
	}

//Method for selecting the number of turns the algorithm run.
	private static int turnsSelection(){
		int n;
		Scanner scan = new Scanner(System.in);
		do {
			System.out.println("How many turns should the algorithm run?");
			n = scan.nextInt();
			if(n < 1){
				System.out.println("Invalid input.");
			}
		} while(n < 1);
		return n;
	}

	private static double percentage(MSetList target, MSetList result){
		return ((result.value - target.value) / (double) target.value) * 100;
	}

	public static void main(String[] args) {
//We need a scanner to read values.
//We also have out variables initialization.
		Scanner scan = new Scanner(System.in);
		int n, menu;
		long start, elapsed_time, run_time_summary = 0;
		String filename;
		MSetList input_file = null, result = null;
		System.out.println("~Welcome to Greedy selection algorithm in Java~");
		do {
			System.out.println("Menu");
			System.out.println("0)Exit application");
			System.out.println("1)Regular solution");
			System.out.println("2)Extra solution");
			switch (menu = scan.nextInt()) {
				case 1:
					filename = fileSelection();
					if (filename.equals("")) {
						System.out.println("Oops. Something went wrong! No file input.");
					}
					n = turnsSelection();
					if (n == 0) {
						System.out.println("Oops. Something went wrong! No n input.");
					}
					for (int i = 0; i < n; i++) {
						start = System.nanoTime();
						input_file = new MSetList();
						MultiSet target = input_file.readFile(filename);
						result = input_file.cover(target);
						elapsed_time = System.nanoTime() - start;
						run_time_summary += elapsed_time;
					}
					System.out.println("File dimensions:" + input_file.getM() + "*" + input_file.getN() + " type of m*n");
					System.out.println("Best solution cost is:" + input_file.value);
					System.out.println("Greedy algorithm found cost of:" + result.value);
					System.out.println("Percentage of cost difference:" + percentage(input_file, result) + "% more expensive");
					System.out.println("Total time:" + run_time_summary / 1000000000.0 + " second.");
					System.out.println("Average time for " + n + " times:" + (run_time_summary / n) / 1000000000.0 + " seconds/run.");
					run_time_summary = 0;
					break;
				case 2:
					System.out.println("Greedy with LinkedList and sort, that uses cost/target.cardinality of multisets for sorting");
						filename = fileSelection();
						if (filename.equals("")) {
							System.out.println("Oops. Something went wrong! No file input.");
						}
						n = turnsSelection();
						if (n == 0) {
							System.out.println("Oops. Something went wrong! No n input.");
						}
						for (int i = 0; i < n; i++) {
							start = System.nanoTime();
							input_file = new MSetList();
							MultiSet target = input_file.readFile(filename);
							result = input_file.sortGreedy(target);
							elapsed_time = System.nanoTime() - start;
							run_time_summary += elapsed_time;
						}
						System.out.println("File dimensions:" + input_file.getM() + "*" + input_file.getN() + " type of m*n");
						System.out.println("Best solution cost is:" + input_file.value);
						System.out.println("Greedy algorithm with sort found cost of:" + result.value);
						System.out.println("Percentage of cost difference:" + percentage(input_file, result) + "% more expensive");
						System.out.println("Total time:" + run_time_summary / 1000000000.0 + " second.");
						System.out.println("Average time for " + n + " times:" + (run_time_summary / n) / 1000000000.0 + " seconds/run.");
						run_time_summary = 0;
					break;
				case 0:
					break;
				default:
					System.out.println("Invalid menu option.");
					break;
			}
		} while (menu != 0);
	}
}