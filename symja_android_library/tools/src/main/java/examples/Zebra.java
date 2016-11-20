package examples;

import jp.ac.kobe_u.cs.cream.*;

/*
 * Zebra puzzle
 * http://en.wikipedia.org/wiki/Zebra_Puzzle
 */
public class Zebra {
	static int houses = 5;
	
	static void rightOf(IntVariable v1, IntVariable v2) {
		v1.equals(v2.add(1));
	}
	
	static void nextTo(IntVariable v1, IntVariable v2) {
		v1.subtract(v2).abs().equals(1);
	}
	
	static String find(int value, IntVariable[] vs, Solution solution) {
		for (IntVariable v : vs) {
			if (solution.getIntValue(v) == value) {
				return v.getName();
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		Network net = new Network();
		IntVariable red = new IntVariable(net, 1, houses, "red");
		IntVariable green = new IntVariable(net, 1, houses, "green");
		IntVariable ivory = new IntVariable(net, 1, houses, "ivory");
		IntVariable yellow = new IntVariable(net, 1, houses, "yellow");
		IntVariable blue = new IntVariable(net, 1, houses, "blue");
		IntVariable[] color = {
				red, green, ivory, yellow, blue
		};
		IntVariable englishman = new IntVariable(net, 1, houses, "Englishman");
		IntVariable spaniard = new IntVariable(net, 1, houses, "Spaniard");
		IntVariable ukrainian = new IntVariable(net, 1, houses, "Ukrainian");
		IntVariable norwegian = new IntVariable(net, 1, houses, "Norwegian");
		IntVariable japanese = new IntVariable(net, 1, houses, "Japanese");
		IntVariable[] nationality = {
				englishman, spaniard, ukrainian, norwegian, japanese
		};
		IntVariable coffee = new IntVariable(net, 1, houses, "coffee");
		IntVariable tea = new IntVariable(net, 1, houses, "tea");
		IntVariable milk = new IntVariable(net, 1, houses, "milk");
		IntVariable orangeJuice = new IntVariable(net, 1, houses, "orange juice");
		IntVariable water = new IntVariable(net, 1, houses, "water");
		IntVariable[] drink = {
				coffee, tea, milk, orangeJuice, water
		};
		IntVariable oldGold = new IntVariable(net, 1, houses, "Old Gold");
		IntVariable kools = new IntVariable(net, 1, houses, "Kools");
		IntVariable chesterfields = new IntVariable(net, 1, houses, "Chesterfields");
		IntVariable luckyStrike = new IntVariable(net, 1, houses, "Lucky Strike");
		IntVariable parliaments = new IntVariable(net, 1, houses, "Parliaments");
		IntVariable[] smoke = {
				oldGold, kools, chesterfields, luckyStrike, parliaments
		};
		IntVariable dog = new IntVariable(net, 1, houses, "dog");
		IntVariable snails = new IntVariable(net, 1, houses, "snails");
		IntVariable fox = new IntVariable(net, 1, houses, "fox");
		IntVariable horse = new IntVariable(net, 1, houses, "horse");
		IntVariable zebra = new IntVariable(net, 1, houses, "zebra");
		IntVariable[] pet = {
				dog, snails, fox, horse, zebra 
		};
		new NotEquals(net, color);
		new NotEquals(net, nationality);
		new NotEquals(net, drink);
		new NotEquals(net, smoke);
		new NotEquals(net, pet);
		// The Englishman lives in the red house.
		englishman.equals(red);
		// The Spaniard owns the dog.
		spaniard.equals(dog);
		// Coffee is drunk in the green house.
		coffee.equals(green);
		// The Ukrainian drinks tea.
		ukrainian.equals(tea);
		// The green house is immediately to the right of the ivory house.
		rightOf(green, ivory);
		// The Old Gold smoker owns snails.
		oldGold.equals(snails);
		// Kools are smoked in the yellow house.
		kools.equals(yellow);
		// Milk is drunk in the middle house.
		milk.equals(3);
		// The Norwegian lives in the first house.
		norwegian.equals(1);
		// The man who smokes Chesterfields lives in the house next to the man with the fox.
		nextTo(chesterfields, fox);
		// Kools are smoked in the house next to the house where the horse is kept.
		nextTo(kools, horse);
		// The Lucky Strike smoker drinks orange juice.
		luckyStrike.equals(orangeJuice);
		// The Japanese smokes Parliaments.
		japanese.equals(parliaments);
		// The Norwegian lives next to the blue house.
		nextTo(norwegian, blue);

		Solver solver = new DefaultSolver(net);
		int count = 0;
		for (solver.start(); solver.waitNext(); solver.resume()) {
			Solution solution = solver.getSolution();
			count++;
			System.out.println("Solution " + count);
//			System.out.println(solution);
			for (int house = 1; house <= houses; house++) {
				System.out.println("\tHouse " + house
						+ ": " + find(house, color, solution)
						+ ", " + find(house, nationality, solution)
						+ ", " + find(house, drink, solution)
						+ ", " + find(house, smoke, solution)
						+ ", " + find(house, pet, solution)
						);
			}
			System.out.println();
		}
	}

}
