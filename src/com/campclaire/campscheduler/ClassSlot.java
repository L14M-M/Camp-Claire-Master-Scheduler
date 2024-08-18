package com.campclaire.campscheduler;

import java.util.HashSet;

/**
 * Represents a time slot in a schedule during which
 * classes can be held. It manages a set of ClassPeriod objects that
 * represent individual class periods within this time slot.
 */
public class ClassSlot {
    private int period;
    private int totalSlots;
    private HashSet<ClassPeriod> slots;
    private int numberClasses;

    /**
     * Constructs a new ClassSlot with the specified period and total number of slots.
     *
     * @param period     the period of the day (e.g., 1 for the first period)
     * @param totalSlots the total number of class periods that can fit in this time slot
     */
    public ClassSlot(int period, int totalSlots) {
        this.period = period;
        this.totalSlots = totalSlots;
        this.slots = new HashSet<ClassPeriod>(totalSlots);
        this.numberClasses = 0;
    }

    /**
     * Constructs a new ClassSlot by copying the attributes of another ClassSlot.
     *
     * @param other the ClassSlot to copy
     */
    public ClassSlot(ClassSlot other) {
        this.period = other.period;
        this.totalSlots = other.totalSlots;
        this.slots = new HashSet<ClassPeriod>(other.slots);
        this.numberClasses = other.numberClasses;
    }

    /**
     * Returns a copy of the set of ClassPeriod objects contained in this ClassSlot.
     *
     * @return a copy of the HashSet of ClassPeriod objects
     */
    public HashSet<ClassPeriod> getSlots() {
        return new HashSet<ClassPeriod>(this.slots);
    }

    /**
     * Returns the total number of slots available in this ClassSlot.
     *
     * @return the total number of slots
     */
    public int getTotalSlots() {
        return this.totalSlots;
    }

    /**
     * Returns the current number of classes scheduled in this ClassSlot.
     *
     * @return the number of classes
     */
    public int getNumberClasses() {
        return this.numberClasses;
    }

    /**
     * Adds a ClassPeriod to this ClassSlot if there is space available.
     *
     * @param classPeriod the ClassPeriod to add
     * @return true if the ClassPeriod was added successfully, 
     *         false if the period is full or the ClassPeriod is a duplicate
     */
    public boolean addClassPeriod(ClassPeriod classPeriod) {
        if (this.slots.size() + 1 > totalSlots) {
            // Adding an element will exceed the allowed number of slots
            return false;
        } else {
            this.numberClasses++;
            return this.slots.add(classPeriod);
        }
    }

    /**
     * Removes a ClassPeriod from this ClassSlot if it exists.
     *
     * @param classPeriod the ClassPeriod to remove
     * @return true if the ClassPeriod was removed successfully, 
     *         false if the ClassPeriod does not exist
     */
    public boolean removeClassPeriod(ClassPeriod classPeriod) {
        if (this.slots.remove(classPeriod)) {
            this.numberClasses--;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns a string representation of this ClassSlot, including all the class periods it contains.
     *
     * @return a string representation of the class slot and its periods
     */
    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("Period ").append(this.period).append('\n')
           .append("///////////////////////////////////").append('\n');
        for (ClassPeriod slot : this.slots) {
            out.append(slot.toString());
            out.append('\n');
        }
        return out.toString();
    }

    /**
     * Checks if this ClassSlot contains a class with the specified title.
     *
     * @param classTitle the title of the class to check for
     * @return true if a class with the specified title is found, 
     *         false otherwise
     */
    public boolean containsClass(String classTitle) {
        for (ClassPeriod period : this.slots) {
            if (period.getTitle().equals(classTitle)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the specified camper is enrolled in any of the class periods within this ClassSlot.
     *
     * @param camper the camper to check for
     * @return true if the camper is enrolled in any class period, 
     *         false otherwise
     */
    public boolean camperEnrolledInAnySLot(Camper camper) {
        for (ClassPeriod slot : this.slots) {
            if (slot.camperEnrolled(camper)) {
                return true;
            }
        }
        return false;
    }
}
