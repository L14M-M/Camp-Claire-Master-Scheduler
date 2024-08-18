package com.campclaire.campscheduler;

import java.util.ArrayList;

/**
 * Represents a period for a class session, managing the class details, 
 * roster, and enrollment capacity.
 */
public class ClassPeriod {
    private ClassClass class_;
    private int maxCapacity;
    private int capacity;
    private ArrayList<Camper> roster;
    private int period;

    /**
     * Constructs a ClassPeriod with the specified class and period.
     *
     * @param class_ the class for this period
     * @param period the period number
     */
    public ClassPeriod(ClassClass class_, int period) {
        this.class_ = class_;
        this.maxCapacity = class_.getSinglePeriodCutoff();
        this.capacity = 0;
        this.roster = new ArrayList<Camper>();
        this.period = period;
    }

    /**
     * Copy constructor to create a ClassPeriod from another ClassPeriod instance.
     *
     * @param copy the ClassPeriod instance to copy from
     */
    public ClassPeriod(ClassPeriod copy) {
        this.class_ = copy.class_;
        this.maxCapacity = copy.maxCapacity;
        this.capacity = copy.capacity;
        // SHALLOW COPY, TODO: change to DEEP COPY (eventually)
        this.roster = new ArrayList<Camper>();
        for (Camper camper : copy.roster) {
            this.roster.add(camper);
        }
        this.period = copy.period;
    }

    /**
     * Checks if this ClassPeriod is equal to another object based on class, period, 
     * and roster.
     *
     * @param o the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ClassPeriod) {
            ClassPeriod other = (ClassPeriod) o;
            return this.class_.equals(other.class_) && this.period == other.getPeriod()
                    && this.roster.equals(other.roster);
        } else {
            return false;
        }
    }

    /**
     * Returns a hash code value for this ClassPeriod based on class, period, 
     * and roster.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return (this.class_.getTitle().hashCode() + this.period ^ 2) ^ 2 + 439 * roster.hashCode();
    }

    /**
     * Checks if another ClassPeriod is in the same period as this one.
     *
     * @param other the ClassPeriod to compare with
     * @return true if the same period, false otherwise
     */
    public boolean samePeriod(ClassPeriod other) {
        return other.period == this.period;
    }

    /**
     * Adds a camper to the class roster.
     *
     * @param add the camper to add
     * @return true if the camper is successfully added, false if the camper is 
     * already enrolled, cannot enroll, or if the class is at capacity
     */
    public boolean addCamper(Camper add) {
        if (this.roster.contains(add) || !add.canEnrollInClassPeriod(this) || add.isEnrolled(this.class_)
                || this.capacity + 1 > this.maxCapacity) {
            return false;
        } else {
            this.roster.add(add);
            this.capacity++;
            add.enroll(this);
            return true;
        }
    }

    /**
     * Adds a camper to the class roster, ignoring maximum capacity.
     *
     * @param add the camper to add
     * @return true if the camper is successfully added, false if the camper is 
     * already enrolled or cannot enroll
     */
    public boolean addCamperOverride(Camper add) {
        // removed || this.capacity + 1 > this.maxCapacity
        if (this.roster.contains(add) || !add.canEnrollInClassPeriod(this) || add.isEnrolled(this.class_)) {
            return false;
        } else {
            this.roster.add(add);
            this.capacity++;
            add.enroll(this);
            return true;
        }
    }

    /**
     * Removes a camper from the class roster.
     *
     * @param remove the camper to remove
     * @return true if the camper is successfully removed, false if the camper 
     * is not registered for this class
     */
    public boolean removeCamper(Camper remove) {
        if (this.roster.contains(remove)) {
            this.roster.remove(remove);
            this.capacity--;
            remove.unEnroll(this);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the class associated with this period.
     *
     * @return the class associated with this period
     */
    public ClassClass getClass_() {
        return this.class_;
    }

    /**
     * Returns the title of the class associated with this period.
     *
     * @return the title of the class
     */
    public String getTitle() {
        return this.class_.getTitle();
    }

    /**
     * Returns the current enrollment capacity.
     *
     * @return the current enrollment capacity
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Returns a string representation of this class period, including class title 
     * and roster.
     *
     * @return a string representation of this class period
     */
    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append(this.class_.getTitle().toString()).append(" : ");
        for (Camper camper : this.roster) {
            out.append(camper.getName()).append(", ");
        }
        return out.toString();
    }

    /**
     * Checks if a camper is enrolled in this class period.
     *
     * @param camper the camper to check
     * @return true if the camper is enrolled, false otherwise
     */
    public boolean camperEnrolled(Camper camper) {
        return this.roster.contains(camper);
    }

    /**
     * Returns the period number for this class period.
     *
     * @return the period number
     */
    public int getPeriod() {
        return period;
    }

    /**
     * Checks if the class period is open for more enrollments.
     *
     * @return true if open for enrollment, false if at capacity
     */
    public boolean open() {
        return this.capacity < this.maxCapacity;
    }
}
