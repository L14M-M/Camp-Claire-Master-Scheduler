package com.campclaire.campscheduler;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScheduleCreator {
	private final int NUMBER_PERIODS = 3;
	public static final int MAX_SCHEDULE_ATTEMPTS = 100000;
	private ArrayList<Camper> campers;
	private HashMap<ClassClass, Integer> classCounts;
	private HashMap<ClassClass, Integer> classPeriods;
	private HashSet<ClassClass> eliminatedClasses;
	private ClassSlot[] classSlots;

	/**
	 * Default constructor that initializes an empty list of campers and class slots
	 * for three periods.
	 */
	public ScheduleCreator() {
		this.campers = new ArrayList<Camper>();
		this.classSlots = new ClassSlot[3];
	}

	/**
	 * Constructor that initializes the list of campers with the provided list.
	 *
	 * @param campers the list of campers to initialize
	 */
	public ScheduleCreator(ArrayList<Camper> campers) {
		this.campers = new ArrayList<Camper>(campers);
	}

	/**
	 * Adds a camper to the list of campers.
	 *
	 * @param add the camper to add
	 */
	public void addCamper(Camper add) {
		this.campers.add(add);
	}

	/**
	 * Removes a camper from the list of campers.
	 *
	 * @param remove the camper to remove
	 * @return true if the camper was successfully removed, false otherwise
	 */
	public boolean removeCounselor(Camper remove) {
		if (this.campers.contains(remove)) {
			this.campers.remove(remove);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Populates each camper's final choices buffer with their top class choices,
	 * considering whether classes have been eliminated or not.
	 */
	public void findCamperChoices() {
		for (Camper camper : this.campers) {
			boolean eliminatedClassesEmpty = this.eliminatedClasses == null || this.eliminatedClasses.isEmpty();
			if (!eliminatedClassesEmpty) {
				camper.clearFinalChoicesBuffer();
			}
			for (ClassClass class_ : camper.getTopClassChoices()) {
				ClassClass currentChoice = class_;
				if (eliminatedClassesEmpty) {
					while (!camper.canTakeClass(currentChoice) || camper.finalChoicesBufferContains(currentChoice)
							|| currentChoice == null) {
						currentChoice = camper.getChoiceOfRank(camper.getRankOfChoice(currentChoice) + 1);
					}
					camper.addFinalChoiceToBuffer(currentChoice);
					if (camper.getFinalChoicesBufferSize() > 2) {
						break;
					}
				} else {
					while (this.eliminatedClasses.contains(currentChoice) || !camper.canTakeClass(currentChoice)
							|| camper.finalChoicesBufferContains(currentChoice) || currentChoice == null) {
						currentChoice = camper.getChoiceOfRank(camper.getRankOfChoice(currentChoice) + 1);
					}
					camper.addFinalChoiceToBuffer(currentChoice);
					if (camper.getFinalChoicesBufferSize() > 2) {
						break;
					}
				}
			}
			camper.configureFinalChoices();
		}
	}

	/**
	 * Initializes the class count by creating a map that associates each class with
	 * the number of campers who ranked it in their top three choices. Classes with
	 * fewer than five top-3 votes are eliminated.
	 */
	public void initializeClassCount() {
		HashMap<ClassClass, Integer> numTopThreeRankings = new HashMap<ClassClass, Integer>();
		for (Camper camper : this.campers) {
			for (ClassClass class_ : camper.getFinalChoices()) {
				if (class_ != null) {
					if (!numTopThreeRankings.containsKey(class_)) {
						numTopThreeRankings.put(class_, 1);
					} else {
						numTopThreeRankings.put(class_, numTopThreeRankings.get(class_) + 1);
					}
				}
			}
		}
		this.eliminatedClasses = new HashSet<ClassClass>();
		for (Iterator<ClassClass> iterator = numTopThreeRankings.keySet().iterator(); iterator.hasNext();) {
			ClassClass class_ = iterator.next();
			if (!class_.isRequired()) {
				if (numTopThreeRankings.get(class_) < 5) {
					this.eliminatedClasses.add(class_);
					iterator.remove();
				}
			}
		}
		this.classCounts = new HashMap<ClassClass, Integer>(numTopThreeRankings);
	}

	/**
	 * Calculates the number of periods needed for each class based on the number of
	 * campers who chose it. It considers the capacity and type of each class.
	 */
	public void calculateNumberPeriods() {
		HashMap<ClassClass, Integer> out = new HashMap<ClassClass, Integer>();
		for (ClassClass class_ : classCounts.keySet()) {
			int classCount = classCounts.get(class_);
			if (class_.isDoublePeriod()) {
				out.put(class_, 2);
			} else if (classCount > class_.getSinglePeriodCutoff() * 2) {
				out.put(class_, 3);
			} else if (classCount > class_.getSinglePeriodCutoff()) {
				out.put(class_, 2);
			} else {
				out.put(class_, 1);
			}
		}
		this.classPeriods = new HashMap<ClassClass, Integer>(out);
	}

	/**
	 * Calculates the total number of periods required for all classes.
	 *
	 * @return the total number of periods
	 */
	private int getTotalPeriods() {
		int out = 0;
		for (Integer periods : this.classPeriods.values()) {
			out += periods;
		}
		return out;
	}

	/**
	 * Finds the index of the first open class slot in the provided class slot list.
	 *
	 * @param classSlotList the list of class slots to search
	 * @return the index of the first open class slot, or -1 if none are open
	 */
	public static int findIndexOpenClassSlot(ClassSlot[] classSlotList) {
		int out = -1;
		for (int i = 0; i < classSlotList.length; i++) {
			if (classSlotList[i] == null) {
				out = i;
				break;
			}
		}
		return out;
	}

	/**
	 * Sorts a HashMap by its values, prioritizing required classes first.
	 *
	 * @param toSort the HashMap to sort
	 * @return a LinkedHashMap sorted by the values of the original map
	 */
	private LinkedHashMap<ClassClass, Integer> sortHashMapByValues(HashMap<ClassClass, Integer> toSort) {
		List<Map.Entry<ClassClass, Integer>> list = new ArrayList<>(toSort.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<ClassClass, Integer>>() {
			public int compare(Map.Entry<ClassClass, Integer> o1, Map.Entry<ClassClass, Integer> o2) {
				boolean isRequired1 = o1.getKey().isRequired();
				boolean isRequired2 = o2.getKey().isRequired();

				if (isRequired1 && !isRequired2) {
					return -1;
				} else if (!isRequired1 && isRequired2) {
					return 1;
				} else {
					return o1.getValue().compareTo(o2.getValue());
				}
			}
		});

		LinkedHashMap<ClassClass, Integer> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<ClassClass, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	/**
	 * Initializes the class slots by determining the number of periods needed for
	 * each class based on the total periods calculated.
	 *
	 * @return an array of ClassSlot objects initialized for each period
	 */
	public ClassSlot[] initializeClassSlots() {
		this.classSlots = new ClassSlot[3];
		int totalPeriods = this.getTotalPeriods();

		int quotient = totalPeriods / 3;
		int remainder = totalPeriods % 3;

		int totalFirstPeriodSlots = quotient + remainder;
		int totalSecondPeriodSlots = quotient + remainder / 2;
		int totalThirdPeriodSlots = quotient + remainder / 2 + remainder % 2;
		this.classSlots[0] = new ClassSlot(1, totalFirstPeriodSlots);
		this.classSlots[1] = new ClassSlot(2, totalSecondPeriodSlots);
		this.classSlots[2] = new ClassSlot(3, totalThirdPeriodSlots);
		return this.classSlots;
	}

	/**
	 * Finds the indices of the two least filled class slots.
	 *
	 * @return an array of two integers representing the indices of the least filled
	 *         class slots
	 */
	private int[] findIndexTwoLeastFilledClassSlots() {
		int minSlotsOpen = Integer.MAX_VALUE;
		int mostFullIndex = -1;
		for (int i = 0; i < 3; i++) {
			int numberSlotsOpen = this.classSlots[i].getTotalSlots() - this.classSlots[i].getNumberClasses();
			if (numberSlotsOpen < minSlotsOpen) {
				minSlotsOpen = numberSlotsOpen;
				mostFullIndex = i;
			}
		}

		int[] out = new int[2];
		int currIndex = 0;
		for (int j = 0; j < 3; j++) {
			if (j != mostFullIndex) {
				out[currIndex++] = j;
			}
		}
		return out;
	}

	/**
	 * Finds the index of the least filled class slot.
	 *
	 * @return the index of the least filled class slot
	 */
	private int findIndexLeastFilledClassSlot() {
		int maxSlotsOpen = Integer.MIN_VALUE;
		int leastFullIndex = -1;
		for (int i = 0; i < 3; i++) {
			int numberSlotsOpen = this.classSlots[i].getTotalSlots() - this.classSlots[i].getNumberClasses();
			if (numberSlotsOpen > maxSlotsOpen) {
				maxSlotsOpen = numberSlotsOpen;
				leastFullIndex = i;
			}
		}
		return leastFullIndex;
	}

	/**
	 * Finds the index of the class slot with the fewest classes that have only one
	 * period.
	 *
	 * @return the index of the class slot with the fewest one-period classes
	 */
	private int findIndexLeastOnePeriodClassSlot() {
		int choice = -1;
		int onePeriods = Integer.MAX_VALUE;
		for (int i = 0; i < this.classSlots.length; i++) {
			int currPeriods = 0;
			HashSet<ClassPeriod> slots = this.classSlots[i].getSlots();
			for (ClassPeriod period : slots) {
				if (this.classPeriods.get(period.getClass_()) == 1) {
					currPeriods++;
				}
			}
			if (currPeriods < onePeriods) {
				onePeriods = currPeriods;
				choice = i;
			}
		}
		return choice;
	}

	/**
	 * Finds the indices of two consecutive periods that are the least filled.
	 *
	 * @return an array of two integers representing the indices of the least filled
	 *         consecutive periods
	 */
	private int[] findIndexLeastFullConsecutivePeriods() {
		int totalFirst = this.classSlots[0].getNumberClasses();
		int totalThird = this.classSlots[2].getNumberClasses();
		return totalFirst < totalThird ? new int[] { 0, 1 } : new int[] { 1, 2 };
	}

	/**
	 * Fills the class slots with classes, considering class restrictions and
	 * preferences.
	 *
	 * @return an array of filled ClassSlot objects
	 */
	public ClassSlot[] fillClassSlots() {
		for (ClassClass class_ : this.sortHashMapByValues(this.classPeriods).keySet()) {
			boolean[] restrictions = new boolean[3];
			if (class_.hasRestrictedPeriods()) {
				int[] restrictedPeriods = class_.getRestrictedPeriods();
				for (int i = 1; i < 4; i++) {
					final int currentIndex = i;
					boolean restrictedPeriod = !Arrays.stream(restrictedPeriods).anyMatch(x -> x == currentIndex);
					if (restrictedPeriod) {
						restrictions[i - 1] = true;
					}
				}
			}
			if (class_.hasConcurrentClassRestriction()) {
				for (String restricted : class_.getRestrictedConcurrentClasses()) {
					for (int i = 0; i < 3; i++) {
						if (this.classSlots[i].containsClass(restricted)) {
							restrictions[i] = true;
						}
					}
				}
			}
			if (class_.isDoublePeriod()) {
				if (class_.hasRestrictedPeriods()) {
					for (int i = 0; i < 3; i++) {
						if (!restrictions[i]) {
							this.classSlots[i].addClassPeriod(new ClassPeriod(class_, i + 1));
						}
					}
				} else {
					int[] periods = findIndexLeastFullConsecutivePeriods();
					this.classSlots[periods[0]].addClassPeriod(new ClassPeriod(class_, periods[0] + 1));
					this.classSlots[periods[1]].addClassPeriod(new ClassPeriod(class_, periods[1] + 1));
				}
			} else {
				int numPeriods = this.classPeriods.get(class_);
				if (class_.hasRestrictedPeriods() || class_.hasConcurrentClassRestriction()) {
					if (numPeriods == 3) {
						for (int i = 0; i < 3; i++) {
							if (!restrictions[i]) {
								this.classSlots[i].addClassPeriod(new ClassPeriod(class_, i + 1));
							}
						}
					} else if (numPeriods == 2) {
						if (class_.mustBeConsecutive()) {
							int[] leastFullPeriods = this.findIndexLeastFullConsecutivePeriods();
							for (int i : leastFullPeriods) {
								if (!restrictions[i]) {
									this.classSlots[i].addClassPeriod(new ClassPeriod(class_, i + 1));
								}
							}
						} else {
							int[] leastFullPeriods = this.findIndexTwoLeastFilledClassSlots();
							int numAdded = 0;
							for (int i : leastFullPeriods) {
								if (!restrictions[i]) {
									this.classSlots[i].addClassPeriod(new ClassPeriod(class_, i + 1));
									numAdded++;
								}
							}
							if (numAdded < 2) {
								for (int i = 0; i < 3; i++) {
									final int currentIndex = i;
									boolean inLeastFull = Arrays.stream(leastFullPeriods)
											.anyMatch(x -> x == currentIndex);
									if (!inLeastFull && !restrictions[i]) {
										this.classSlots[i].addClassPeriod(new ClassPeriod(class_, i + 1));
									}
								}
							}
						}
					} else {
						int leastFull = this.findIndexLeastOnePeriodClassSlot();
						int numAdded = 0;
						if (!restrictions[leastFull]) {
							this.classSlots[leastFull].addClassPeriod(new ClassPeriod(class_, leastFull + 1));
							numAdded++;
						}
						if (numAdded < 1) {
							leastFull = this.findIndexLeastFilledClassSlot();
							if (!restrictions[leastFull]) {
								this.classSlots[leastFull].addClassPeriod(new ClassPeriod(class_, leastFull + 1));
								numAdded++;
							}
							if (numAdded < 1) {
								int[] leastFullPeriods = this.findIndexTwoLeastFilledClassSlots();
								for (int i = 0; i < 2; i++) {
									if (leastFullPeriods[i] != leastFull && !restrictions[leastFullPeriods[i]]) {
										this.classSlots[leastFullPeriods[i]]
												.addClassPeriod(new ClassPeriod(class_, leastFullPeriods[i] + 1));
										numAdded++;
										break;
									}
								}
								if (numAdded < 1) {
									for (int i = 0; i < 3; i++) {
										final int currentIndex = i;
										boolean inLeastFull = Arrays.stream(leastFullPeriods)
												.anyMatch(x -> x == currentIndex);
										if (!inLeastFull && !restrictions[i]) {
											this.classSlots[i].addClassPeriod(new ClassPeriod(class_, i + 1));
										}
									}
								}
							}
						}
					}
				} else {
					if (numPeriods == 3) {
						for (int i = 0; i < 3; i++) {
							this.classSlots[i].addClassPeriod(new ClassPeriod(class_, i + 1));
						}
					} else if (numPeriods == 2) {
						int[] leastFullPeriods = class_.mustBeConsecutive()
								? this.findIndexLeastFullConsecutivePeriods()
								: this.findIndexTwoLeastFilledClassSlots();
						for (int i : leastFullPeriods) {
							this.classSlots[i].addClassPeriod(new ClassPeriod(class_, i + 1));
						}
					} else {
						int leastFull = this.findIndexLeastOnePeriodClassSlot();
						this.classSlots[leastFull].addClassPeriod(new ClassPeriod(class_, leastFull + 1));
					}
				}
			}
		}
		return classSlots;
	}

	/**
	 * Finds the period with the least number of students enrolled for a given class.
	 *
	 * @param class_ the class to check
	 * @return the ClassPeriod object representing the least full period, or null if
	 *         the class is eliminated
	 */
	public ClassPeriod findLeastFullPeriod(ClassClass class_) {
		if (this.eliminatedClasses.contains(class_)) {
			return null;
		}
		ClassPeriod leastFull = null;
		int leastSize = Integer.MAX_VALUE;
		for (ClassSlot classSlot : this.classSlots) {
			for (ClassPeriod period : classSlot.getSlots()) {
				int periodSize = period.getCapacity();
				if (period.getClass_().equals(class_)) {
					if (periodSize < leastSize) {
						leastFull = period;
						leastSize = periodSize;
					}
				}
			}
		}
		if (leastFull == null) {
			System.out.println("PROBLEM");
		}
		return leastFull;
	}

	/**
	 * Finds the second least full period for a given class.
	 *
	 * @param class_ the class to check
	 * @return the ClassPeriod object representing the second least full period, or
	 *         null if the class is eliminated
	 */
	public ClassPeriod findSecondLeastFullPeriod(ClassClass class_) {
		if (this.eliminatedClasses.contains(class_)) {
			return null;
		}
		ClassPeriod out = null;
		int leastFullPeriod = this.findLeastFullPeriod(class_).getPeriod();
		int numPeriods = this.classPeriods.get(class_);
		if (numPeriods > 2) {
			ClassPeriod[] others = new ClassPeriod[2];
			int otherIndex = 0;
			for (int i = 0; i < 3; i++) {
				if (i != leastFullPeriod - 1) {
					for (ClassPeriod period : this.classSlots[i].getSlots()) {
						if (period.getClass_().equals(class_)) {
							others[otherIndex++] = period;
						}
					}
				}
			}
			out = others[0].getCapacity() < others[1].getCapacity() ? others[0] : others[1];
		} else {
			for (int i = 0; i < 3; i++) {
				if (i != leastFullPeriod - 1 && out == null) {
					for (ClassPeriod period : this.classSlots[i].getSlots()) {
						if (period.getClass_().equals(class_)) {
							out = period;
							break;
						}
					}
				}
			}
		}
		return out;
	}

	/**
	 * Finds the period with the most number of students enrolled for a given class.
	 *
	 * @param class_ the class to check
	 * @return the ClassPeriod object representing the most full period, or null if
	 *         the class is eliminated
	 */
	public ClassPeriod findMostFullPeriod(ClassClass class_) {
		if (this.eliminatedClasses.contains(class_)) {
			return null;
		}
		ClassPeriod moastFull = null;
		int maxSize = Integer.MIN_VALUE;
		for (ClassSlot classSlot : this.classSlots) {
			for (ClassPeriod period : classSlot.getSlots()) {
				int periodSize = period.getCapacity();
				if (period.getClass_().equals(class_)) {
					if (periodSize > maxSize) {
						moastFull = period;
						maxSize = periodSize;
					}
				}
			}
		}
		if (moastFull == null) {
			System.out.println("PROBLEM");
		}
		return moastFull;
	}

	/**
	 * Adds campers to essential classes, ensuring that required classes and double
	 * period classes are filled first.
	 */
	public void addCampersToEssentialClasses() {
		for (Camper camper : this.campers) {
			for (ClassClass choice : camper.getFinalChoices()) {
				if (choice != null) {
					if (choice.isRequired()) {
						ClassPeriod period = this.findLeastFullPeriod(choice);
						period.addCamper(camper);
					} else if (choice.isDoublePeriod()) {
						for (ClassSlot slot : this.classSlots) {
							if (slot.containsClass(choice.getTitle())) {
								for (ClassPeriod period : slot.getSlots()) {
									if (period.getClass_().equals(choice)) {
										period.addCamper(camper);
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Adds campers to non-essential classes, filling remaining slots with the best
	 * available options for each camper.
	 */
	public void addCampersToOtherClasses() {
		for (int i = 0; i < 3; i++) {
			for (Camper camper : this.campers) {
				if (camper.getNumEnrolledClasses() < 2) {
					ClassClass enrollAttempt = this.findClassFirstLowestAvailableSlots(camper);
					if (enrollAttempt != null) {
						ClassPeriod leastFull = this.findLeastFullPeriod(enrollAttempt);
						if (leastFull != null) {
							while (!leastFull.addCamper(camper)) {
								boolean added = false;
								int numPeriods = this.classPeriods.get(enrollAttempt);
								if (numPeriods > 1) {
									ClassPeriod secondLeastFull = this.findSecondLeastFullPeriod(enrollAttempt);
									if (!secondLeastFull.addCamper(camper)) {
										if (numPeriods > 2) {
											ClassPeriod mostFull = this.findMostFullPeriod(enrollAttempt);
											if (mostFull.addCamper(camper)) {
												added = true;
											}
										}
									} else {
										added = true;
									}
								}
								if (added) {
									break;
								} else {
									do {
										ClassClass nextUp = camper.findNextRankedClass(enrollAttempt);
										while (nextUp == null || this.eliminatedClasses.contains(nextUp)
												|| camper.isEnrolled(nextUp)) {
											nextUp = camper.findNextRankedClass(nextUp);
										}
										enrollAttempt = nextUp;
										leastFull = this.findLeastFullPeriod(enrollAttempt);
									} while (leastFull == null);
								}
							}
						}
					}
				} else if (camper.getNumEnrolledClasses() < 3) {
					int notEnrolled = camper.getPeriodNotEnrolled();
					ClassPeriod bestNotEnrolled = camper
							.findHighestRankedUnenrolledClass(this.classSlots[notEnrolled - 1]);
					if (bestNotEnrolled != null) {
						bestNotEnrolled.addCamper(camper);
					} else {
						ClassPeriod bestNotEnrolledOverride = camper.findHighestRankedUnenrolledClasssOverride(
								this.classSlots[notEnrolled - 1]);
						bestNotEnrolledOverride.addCamperOverride(camper);
					}
				}
			}
		}
	}

	/**
	 * Finds the class with the first lowest number of available slots for a given
	 * camper.
	 *
	 * @param camper the camper to find the class for
	 * @return the class with the first lowest number of available slots
	 */
	private ClassClass findClassFirstLowestAvailableSlots(Camper camper) {
		int lowestAvailableSlots = Integer.MAX_VALUE;
		ClassClass lowestAvailable = null;
		for (ClassClass class_ : camper.getFinalChoices()) {
			if (class_ != null && !class_.isRequired() && !camper.isEnrolled(class_)
					&& !this.eliminatedClasses.contains(class_)) {
				int currentNumberPeriods = this.classPeriods.get(class_);
				if (currentNumberPeriods == 1) {
					return class_;
				}
				if (lowestAvailable == null) {
					lowestAvailable = class_;
					lowestAvailableSlots = currentNumberPeriods;
				} else {
					if (currentNumberPeriods < lowestAvailableSlots) {
						lowestAvailable = class_;
						lowestAvailableSlots = currentNumberPeriods;
					}
				}
			}
		}
		return lowestAvailable;
	}

	/**
	 * Displays the current class slots, printing them to the console.
	 */
	public void displaySlots() {
		StringBuffer out = new StringBuffer();
		for (ClassSlot slot : this.classSlots) {
			out.append(slot.toString()).append('\n');
		}
		System.out.println(out);
	}

	/**
	 * Displays the number of periods allocated to each class, printing them to the
	 * console.
	 */
	public void displayClassPeriodsNumbers() {
		StringBuffer out = new StringBuffer();
		for (ClassClass class_ : this.classPeriods.keySet()) {
			out.append(class_.getTitle()).append(": ").append(this.classPeriods.get(class_).toString()).append('\n');
		}
		System.out.println(out.toString());
	}

	/**
	 * Displays the count of how many campers have chosen each class, printing them
	 * to the console.
	 */
	public void displayClassCounts() {
		StringBuffer out = new StringBuffer();
		for (ClassClass class_ : this.classCounts.keySet()) {
			out.append(class_.getTitle()).append(": ").append(this.classCounts.get(class_).toString()).append('\n');
		}
		System.out.println(out.toString());
	}

	/**
	 * Displays each camper's top three class choices, printing them to the console.
	 */
	public void displayCamperTopThrees() {
		StringBuffer out = new StringBuffer();
		for (Camper camper : this.campers) {
			out.append(camper.getName()).append(" - ");
			for (int i = 0; i < NUMBER_PERIODS; i++) {
				if (i != 2) {
					try {
						out.append(camper.getFinalChoice(i).toString()).append(", ");
					} catch (NullPointerException e) {
						out.append("INVALID").append(", ");
					}
				} else {
					try {
						out.append(camper.getFinalChoice(i).toString()).append('\n');
					} catch (NullPointerException e) {
						out.append("INVALID").append('\n');
					}
				}
			}
		}
		System.out.println(out.toString());
	}

	/**
	 * Randomly shuffles the list of campers.
	 */
	public void shuffleCampers() {
		Collections.shuffle(campers);
	}

	/**
	 * Clears the set of eliminated classes.
	 */
	public void clearEliminatedClasses() {
		this.eliminatedClasses.clear();
	}

	/**
	 * Clears each camper's schedule and final choices.
	 */
	public void clearCamperScheduleAndFinalChoices() {
		for (Camper camper : this.campers) {
			camper.clearScheduleAndFinalChoices();
		}
	}
	
	/**
	 * Identifies classes that have been eliminated and adds them to the eliminated
	 * classes set.
	 */
	private void findEliminatedClasses() {
		for (ClassClass class_ : ScheduleDriver.getClassList()) {
			boolean presentInSlots = false;
			for (ClassSlot classSlot : this.classSlots) {
				for (ClassPeriod slot : classSlot.getSlots()) {
					if (slot.getClass_().equals(class_)) {
						presentInSlots = true;
						break;
					}
				}
				if (presentInSlots) {
					break;
				}
			}
			if (!presentInSlots && !this.eliminatedClasses.contains(class_)) {
				this.eliminatedClasses.add(class_);
			}
		}
	}
	
	/**
	 * Adjusts the counts of class periods based on the current schedule.
	 */
	private void adjustClassPeriodCounts() {
		for (ClassClass class_ : ScheduleDriver.getClassList()) {
			int classCount = 0;
			if (!this.eliminatedClasses.contains(class_)) {
				for (ClassSlot classSlot : this.classSlots) {
					for (ClassPeriod slot : classSlot.getSlots()) {
						if (slot.getClass_().equals(class_)) {
							classCount++;
						}
					}
				}
			}
			this.classPeriods.put(class_, classCount);
		}
	}

	/**
	 * Executes the scheduling process, filling class slots and enrolling campers.
	 *
	 * @return the final schedule after processing all steps
	 */
	public Schedule run() {
		this.findCamperChoices();
		this.initializeClassCount();
		this.findCamperChoices();
		this.calculateNumberPeriods();
		this.initializeClassSlots();
		this.fillClassSlots();
		this.findEliminatedClasses();
		this.adjustClassPeriodCounts();
		this.addCampersToEssentialClasses();
		this.addCampersToOtherClasses();
		return new Schedule(this.campers, this.classSlots, this.eliminatedClasses);
	}

}
