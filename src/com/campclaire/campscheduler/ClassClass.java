package com.campclaire.campscheduler;

import java.util.ArrayList;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a class in the camp, including its properties such as periods, 
 * restrictions, and requirements.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassClass {
    private String title;
    private int[] restrictedPeriods; // Class can ONLY occur during these periods
    private ArrayList<String> restrictedConcurrentClasses; // Classes cannot happen concurrently (same period)
    private boolean doublePeriod; // Class requires two periods
    private boolean isRequired; // True for classes some campers are required to take (swim lessons)
    private boolean is10Plus; // Class is 10+
    private boolean mustBeConsecutive; // If a class requires 2 periods, they must be consecutive
    private boolean requiresSwimLevel; // Class requires good swimming abilities
    private int singlePeriodCutoff; //SPC
    // if the number of campers that ranked this class top three < SPC (1 period required)
    // if SPC < number of campers that ranked this class top three <= 2*SPC (2 periods required)
    // if the number of campers that ranked this class top three > 2*SPC (3 periods required)

    /**
     * Default constructor for ClassClass, initializing default values.
     */
    public ClassClass() {
        this.restrictedConcurrentClasses = new ArrayList<>();
    }

    /**
     * Constructs a ClassClass with specified attributes.
     *
     * @param title                  the title of the class
     * @param restrictedPeriods      the periods during which the class cannot occur
     * @param doublePeriod           true if the class spans two periods
     * @param isRequired             true if the class is required
     * @param is10Plus               true if the class is for campers 10 years and older
     * @param mustBeConsecutive      true if the class periods must be consecutive
     * @param requiresSwimLevel      true if the class requires a swim level
     * @param singlePeriodCutoff     the cutoff for determining the class period length
     */
    public ClassClass(String title, int[] restrictedPeriods, boolean doublePeriod, boolean isRequired, boolean is10Plus,
            boolean mustBeConsecutive, boolean requiresSwimLevel, int singlePeriodCutoff) {
        this.title = title;
        this.restrictedPeriods = restrictedPeriods;
        this.doublePeriod = doublePeriod;
        this.isRequired = isRequired;
        this.is10Plus = is10Plus;
        this.mustBeConsecutive = mustBeConsecutive;
        this.requiresSwimLevel = requiresSwimLevel;
        this.singlePeriodCutoff = singlePeriodCutoff;
        this.restrictedConcurrentClasses = new ArrayList<String>();
    }

    /**
     * Returns the title of the class.
     *
     * @return the title of the class
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the periods during which the class cannot occur.
     *
     * @return an array of restricted periods
     */
    public int[] getRestrictedPeriods() {
        return restrictedPeriods;
    }

    /**
     * Returns a list of concurrent classes that are restricted.
     *
     * @return a list of restricted concurrent classes
     */
    public ArrayList<String> getRestrictedConcurrentClasses() {
        return new ArrayList<String>(restrictedConcurrentClasses);
    }

    /**
     * Adds a class to the list of restricted concurrent classes.
     *
     * @param toAdd the class to add
     */
    public void addRestrictedConcurrentClass(ClassClass toAdd) {
        this.restrictedConcurrentClasses.add(toAdd.getTitle());
    }

    /**
     * Removes a class from the list of restricted concurrent classes.
     *
     * @param toAdd the class to remove
     */
    public void removeRestrictedConcurrentClass(ClassClass toAdd) {
        this.restrictedConcurrentClasses.remove(toAdd.getTitle());
    }

    /**
     * Checks if the class is a double-period class.
     *
     * @return true if the class is a double-period class, false otherwise
     */
    public boolean isDoublePeriod() {
        return doublePeriod;
    }

    /**
     * Checks if the class is required.
     *
     * @return true if the class is required, false otherwise
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * Checks if the class is for campers 10 years and older.
     *
     * @return true if the class is for campers 10 years and older, false otherwise
     */
    public boolean is10Plus() {
        return is10Plus;
    }

    /**
     * Returns the cutoff for determining the class period length.
     *
     * @return the single period cutoff
     */
    public int getSinglePeriodCutoff() {
        return singlePeriodCutoff;
    }

    /**
     * Checks if the class periods must be consecutive.
     *
     * @return true if the class periods must be consecutive, false otherwise
     */
    public boolean mustBeConsecutive() {
        return mustBeConsecutive;
    }

    /**
     * Checks if the class has restricted periods.
     *
     * @return true if the class has restricted periods, false otherwise
     */
    public boolean hasRestrictedPeriods() {
        return this.restrictedPeriods.length > 0;
    }

    /**
     * Sets the title of the class.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the restricted periods during which the class cannot occur.
     *
     * @param restrictedPeriods the restricted periods to set
     */
    public void setRestrictedPeriods(int[] restrictedPeriods) {
        this.restrictedPeriods = restrictedPeriods;
    }

    /**
     * Sets the list of restricted concurrent classes.
     *
     * @param restrictedConcurrentClasses the list of restricted concurrent classes to set
     */
    public void setRestrictedConcurrentClasses(ArrayList<String> restrictedConcurrentClasses) {
        this.restrictedConcurrentClasses = new ArrayList<String>(restrictedConcurrentClasses);
    }

    /**
     * Clears the list of restricted concurrent classes.
     */
    public void clearRestrictedConcurrentClasses() {
        this.restrictedConcurrentClasses.clear();
    }

    /**
     * Sets whether the class is a double-period class.
     *
     * @param doublePeriod true if the class is a double-period class
     */
    public void setDoublePeriod(boolean doublePeriod) {
        this.doublePeriod = doublePeriod;
    }

    /**
     * Sets whether the class is required.
     *
     * @param isRequired true if the class is required
     */
    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    /**
     * Sets whether the class is for campers 10 years and older.
     *
     * @param is10Plus true if the class is for campers 10 years and older
     */
    public void setIs10Plus(boolean is10Plus) {
        this.is10Plus = is10Plus;
    }

    /**
     * Sets whether the class periods must be consecutive.
     *
     * @param mustBeConsecutive true if the class periods must be consecutive
     */
    public void setMustBeConsecutive(boolean mustBeConsecutive) {
        this.mustBeConsecutive = mustBeConsecutive;
    }

    /**
     * Sets whether the class requires a swim level.
     *
     * @param requiresSwimLevel true if the class requires a swim level
     */
    public void setRequiresSwimLevel(boolean requiresSwimLevel) {
        this.requiresSwimLevel = requiresSwimLevel;
    }

    /**
     * Sets the cutoff for determining the class period length.
     *
     * @param singlePeriodCutoff the single period cutoff to set
     */
    public void setSinglePeriodCutoff(int singlePeriodCutoff) {
        this.singlePeriodCutoff = singlePeriodCutoff;
    }

    /**
     * Returns a hash code value for this class based on title and single 
     * period cutoff.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(singlePeriodCutoff, title);
    }

    /**
     * Checks if this class is equal to another object based on title and single 
     * period cutoff.
     *
     * @param obj the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClassClass other = (ClassClass) obj;
        return singlePeriodCutoff == other.singlePeriodCutoff && title.equals(other.title);
    }

    /**
     * Returns a string representation of the class.
     *
     * @return the title of the class
     */
    @Override
    public String toString() {
        return new StringBuffer(this.title).toString();
    }

    /**
     * Checks if the class requires a swim level.
     *
     * @return true if the class requires a swim level, false otherwise
     */
    public boolean requiresSwimLevel() {
        return requiresSwimLevel;
    }

    /**
     * Checks if there are any concurrent class restrictions.
     *
     * @return true if there are concurrent class restrictions, false otherwise
     */
    public boolean hasConcurrentClassRestriction() {
        return !this.restrictedConcurrentClasses.isEmpty();
    }

    /**
     * Checks if the class can occur during a specified period.
     *
     * @param period the period to check
     * @return true if the class can occur during the specified period, false otherwise
     */
    public boolean canOccurDuring(int period) {
        for (int restricted : this.restrictedPeriods) {
            if (restricted == period) {
                return true;
            }
        }
        return false;
    }
}
