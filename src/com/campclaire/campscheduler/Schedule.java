package com.campclaire.campscheduler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * The Scheduleclass represents a camp schedule for a group of campers.
 * It contains a list of campers, an array of ClassSlot objects representing
 * different periods, a set of eliminated classes, and a calculated score that
 * evaluates the quality of the schedule.
 */
public class Schedule implements Comparable<Object> {
    private ArrayList<Camper> campers;
    private ClassSlot[] classSlots;
    private HashSet<ClassClass> eliminatedClasses;
    private int score;

    /**
     * Constructs a new Schedule with the specified campers, class slots, and eliminated classes.
     * The schedule score is calculated upon construction.
     *
     * @param campers            a list of campers involved in the schedule
     * @param classSlots         an array of ClassSlot objects representing the periods in the schedule
     * @param eliminatedClasses  a set of ClassClass objects that have been eliminated from the schedule
     */
    public Schedule(ArrayList<Camper> campers, ClassSlot[] classSlots, HashSet<ClassClass> eliminatedClasses) {
        this.campers = new ArrayList<Camper>(campers);
        this.classSlots = new ClassSlot[classSlots.length];
        for (int i = 0; i < classSlots.length; i++) {
            this.classSlots[i] = new ClassSlot(classSlots[i]);
        }
        this.eliminatedClasses = new HashSet<ClassClass>(eliminatedClasses);
        this.score = calculateScheduleScore();
    }

    /**
     * Compares this schedule to another object, which must also be a Schedule, based on their scores.
     *
     * @param o the object to compare with this schedule
     * @return a negative integer, zero, or a positive integer as this schedule is less than, equal to, or greater than the specified schedule
     */
    @Override
    public int compareTo(Object o) {
        if (this == o) {
            return 0;
        }
        if (o instanceof Schedule) {
            Schedule other = (Schedule) o;
            return this.getScore() - other.getScore();
        } else {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Returns a string representation of this schedule, including all class slots.
     *
     * @return a string representation of the schedule
     */
    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        for (ClassSlot slot : this.classSlots) {
            out.append(slot.toString()).append('\n');
        }
        return out.toString();
    }

    /**
     * Returns the score of this schedule.
     * The score is calculated based on the campers' preferences and the distribution of class enrollments.
     *
     * @return the score of the schedule
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns a copy of the list of campers associated with this schedule.
     *
     * @return a copy of the list of campers
     */
    public ArrayList<Camper> getCampers() {
        return new ArrayList<Camper>(this.campers);
    }

    /**
     * Calculates the score of the schedule based on the preferences of the campers and the class enrollments.
     * Classes that are required by campers contribute more to the score.
     * The variation in enrollment numbers for each class across periods also affects the score.
     *
     * @return the calculated schedule score
     */
    private int calculateScheduleScore() {
        int score = 0;
        for (Camper camper : this.campers) {
            for (ClassPeriod classPeriod : camper.getSchedule()) {
                ClassClass class_ = classPeriod.getClass_();
                if (class_.isRequired()) {
                    score += 3;
                } else {
                    int classRank = camper.getRankOfChoice(class_);
                    score += classRank;
                }
            }
        }
        for (ClassClass class_ : ScheduleDriver.getClassList()) {
            if (!this.eliminatedClasses.contains(class_)) {
                int lowestEnrolled = Integer.MAX_VALUE;
                int highestEnrolled = Integer.MIN_VALUE;
                for (ClassSlot slot : this.classSlots) {
                    for (ClassPeriod period : slot.getSlots()) {
                        if (period.getClass_().equals(class_)) {
                            int enrolled = period.getCapacity();
                            if (enrolled < lowestEnrolled) {
                                lowestEnrolled = enrolled;
                            }
                            if (enrolled > highestEnrolled) {
                                highestEnrolled = enrolled;
                            }
                            break;
                        }
                    }
                }
                score += 10 * (highestEnrolled - lowestEnrolled);
            }
        }
        return score;
    }

    /**
     * Finds and returns the camper with the worst class choice (i.e., the highest rank of choice)
     * along with the rank of that choice.
     *
     * @return a Map.Entry containing the camper with the worst choice and the rank of that choice
     */
    public Map.Entry<Camper, Integer> getWorstChoiceAndCamper() {
        int worstRank = -1;
        Camper worstCamper = null;
        for (Camper camper : this.campers) {
            for (ClassPeriod period : camper.getSchedule()) {
                ClassClass class_ = period.getClass_();
                if (!class_.isRequired()) {
                    int currentRank = camper.getRankOfChoice(class_);
                    if (currentRank > worstRank) {
                        worstRank = currentRank;
                        worstCamper = camper;
                    }
                }
            }
        }
        return Map.entry(worstCamper, worstRank);
    }
}
