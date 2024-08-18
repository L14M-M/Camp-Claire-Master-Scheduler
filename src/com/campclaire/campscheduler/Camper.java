package com.campclaire.campscheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a camper at the camp, managing class choices, schedule, and 
 * enrollment information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Camper extends CampResident implements Comparable<Object> {
    /** The total number of classes available. */
    final int TOTAL_CLASSES = ScheduleDriver.getClassList().size();
    private ClassClass[] totalClassChoices;
    private ClassClass[] topClassChoices;
    private HashMap<ClassClass, Integer> finalChoicesBuffer;
    private ClassClass[] finalChoices;
    private HashSet<ClassPeriod> schedule;
    private boolean isCIT;
    private boolean isLIT;
    private int swimLevel;
    private int numEnrolledClasses;

    /**
     * Default constructor for camper, initializing default values.
     */
    public Camper() {
        super();

        this.totalClassChoices = new ClassClass[TOTAL_CLASSES];
        this.topClassChoices = new ClassClass[3];
        this.finalChoicesBuffer = new HashMap<ClassClass, Integer>(3);
        this.finalChoices = new ClassClass[3];
        this.schedule = new HashSet<ClassPeriod>(3);
        this.isCIT = false;
        this.isLIT = false;
        this.setSwimLevel(swimLevel);
        this.numEnrolledClasses = 0;
    }

    /**
     * Constructs a camper with specified age, name, and swim level.
     *
     * @param age       the age of the camper
     * @param name      the name of the camper
     * @param swimLevel the swim level of the camper
     */
    public Camper(int age, String name, int swimLevel) {
        super(age, name);
        this.totalClassChoices = new ClassClass[TOTAL_CLASSES];
        this.topClassChoices = new ClassClass[3];
        this.finalChoicesBuffer = new HashMap<ClassClass, Integer>(3);
        this.finalChoices = new ClassClass[3];
        this.schedule = new HashSet<ClassPeriod>(3);
        this.isCIT = false;
        this.isLIT = false;
        this.setSwimLevel(swimLevel);
        this.numEnrolledClasses = 0;
    }

    /**
     * Constructs a camper with specified age, name, swim level, and class choices.
     *
     * @param age          the age of the camper
     * @param name         the name of the camper
     * @param swimLevel    the swim level of the camper
     * @param classChoices an array of class choices for the camper
     */
    public Camper(int age, String name, int swimLevel, ClassClass[] classChoices) {
        super(age, name);
        this.totalClassChoices = classChoices;
        this.topClassChoices = new ClassClass[] { classChoices[0], classChoices[1], classChoices[2] };
        this.finalChoicesBuffer = new HashMap<ClassClass, Integer>(3);
        this.finalChoices = new ClassClass[3];
        this.schedule = new HashSet<ClassPeriod>(3);
        this.isCIT = false;
        this.isLIT = false;
        this.setSwimLevel(swimLevel);
        this.numEnrolledClasses = 0;
    }

    /**
     * Copy constructor to create a camper from another camper instance.
     *
     * @param copy the camper instance to copy from
     */
    public Camper(Camper copy) {
        super(copy.age, copy.name);
        this.totalClassChoices = copy.totalClassChoices;
        this.topClassChoices = copy.topClassChoices;
        this.finalChoicesBuffer = new HashMap<ClassClass, Integer>(3);
        for (ClassClass classTitle : copy.finalChoicesBuffer.keySet()) {
            this.finalChoicesBuffer.put(classTitle, copy.finalChoicesBuffer.get(classTitle));
        }
        this.finalChoices = copy.finalChoices;
        this.schedule = new HashSet<ClassPeriod>(3);
        for (ClassPeriod period : copy.schedule) {
            this.schedule.add(new ClassPeriod(period));
        }
        this.isCIT = copy.isCIT;
        this.isLIT = copy.isLIT;
        this.setSwimLevel(copy.swimLevel);
        this.numEnrolledClasses = copy.numEnrolledClasses;
    }

    /**
     * Compares this camper with another object for ordering based on age.
     *
     * @param o the object to compare with
     * @return 0 if equal, a negative integer if this camper is younger,
     *         and a positive integer if this camper is older.
     */
    @Override
    public int compareTo(Object o) {
        if (this == o) {
            return 0;
        }
        if (o instanceof Camper) {
            Camper other = (Camper) o;
            return this.getAge() - other.getAge();
        } else {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Checks if this camper is equal to another object based on name and age.
     *
     * @param o the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Camper) {
            Camper other = (Camper) o;
            return this.getName().equals(other.getName()) && this.getAge() == other.getAge();
        } else {
            return false;
        }
    }

    /**
     * Returns a hash code value for this camper based on name and age.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return this.getName().hashCode() * (this.getAge() ^ 3) * 439;
    }

    /**
     * Returns a string representation of this camper, including top class
     * choices, final class choices, and the actual class schedule.
     *
     * @return a string representation of this camper
     */
    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append(this.name).append('\n').append('\n').append("TOP CLASS CHOICES: ").append('\n').append('\n');
        for (ClassClass class_ : this.topClassChoices) {
            if (class_ != null) {
                out.append(class_.getTitle()).append('\n');
            } else {
                out.append("NONE");
            }
        }
        out.append('\n').append("FINAL CLASS CHOICES: ").append('\n').append('\n');
        for (ClassClass class_ : this.finalChoices) {
            if (class_ != null) {
                out.append(class_.getTitle()).append('\n');
            } else {
                out.append("NONE");
            }

        }
        out.append('\n').append("ACTUAL CLASS CHOICES: ").append('\n').append('\n');
        for (ClassPeriod classPeriod : this.schedule) {
            if (classPeriod != null) {
                out.append(classPeriod.getTitle()).append('\n');
            } else {
                out.append("NONE");
            }
        }
        return out.toString();
    }

    /**
     * Returns the total class choices available to the camper.
     *
     * @return an array of total class choices
     */
    public ClassClass[] getTotalClassChoices() {
        return this.totalClassChoices;
    }

    /**
     * Returns the top three class choices selected by the camper.
     *
     * @return an array of top class choices
     */
    public ClassClass[] getTopClassChoices() {
        return this.topClassChoices;
    }

    /**
     * Returns a string representation of the top class choices for the camper.
     *
     * @return a comma-separated string of top class choices
     */
    public String getTopClassChoicesString() {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < 3; i++) {
            buff.append(this.topClassChoices[i]);
            if (i != 2) {
                buff.append(", ");
            }
        }
        return buff.toString();
    }

    /**
     * Returns the final class choices after enrollment processing.
     *
     * @return an array of final class choices
     */
    public ClassClass[] getFinalChoices() {
        return this.finalChoices;
    }

    /**
     * Returns a string representation of the final class choices for the camper.
     *
     * @return a comma-separated string of final class choices
     */
    public String getFinalChoicesString() {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < 3; i++) {
            buff.append(this.finalChoices[i]);
            if (i != 2) {
                buff.append(", ");
            }
        }
        return buff.toString();
    }

    /**
     * Returns the top class choice at the specified index.
     *
     * @param index the index of the top class choice
     * @return the top class choice at the specified index
     */
    public ClassClass getTopClassChoice(int index) {
        return this.topClassChoices[index];
    }

    /**
     * Returns the final class choice at the specified index.
     *
     * @param index the index of the final class choice
     * @return the final class choice at the specified index
     */
    public ClassClass getFinalChoice(int index) {
        return this.finalChoices[index];
    }

    /**
     * Returns the index of a class choice within the top three choices.
     *
     * @param choice the class choice to find
     * @return the index of the class choice
     * @throws NoSuchElementException if the class choice is not in the top three
     */
    public int getIndexTopThree(ClassClass choice) {
        for (int i = 0; i < this.topClassChoices.length; i++) {
            if (this.topClassChoices[i] == choice) {
                return i;
            }
        }
        throw new NoSuchElementException("Specified Choice Not Present in Top 3");
    }

    /**
     * Returns the class choice at the specified rank.
     *
     * @param rank the rank of the class choice
     * @return the class choice at the specified rank
     * @throws IndexOutOfBoundsException if the rank is out of range
     */
    public ClassClass getChoiceOfRank(int rank) {
        if (rank > 12) {
            throw new IndexOutOfBoundsException("Specified Rank Out of Range");
        } else {
            return this.totalClassChoices[rank - 1];
        }
    }

    /**
     * Returns the rank of a specified class choice.
     *
     * @param choice the class choice to find the rank of
     * @return the rank of the class choice
     * @throws NoSuchElementException if the class choice is not present
     */
    public int getRankOfChoice(ClassClass choice) {
        int i = 0;
        for (ClassClass current : this.totalClassChoices) {
            if (current.equals(choice)) {
                return i + 1;
            }
            i++;
        }
        throw new NoSuchElementException("Specified Choice not Present in Class Selections");
    }

    /**
     * Returns the index of the worst-ranked class in the top three choices.
     *
     * @return the index of the worst-ranked class in the top three choices
     */
    @JsonIgnore
    public int getIndexWorstRankedTopThree() {
        int worstRank = Integer.MIN_VALUE;
        for (ClassClass choice : this.topClassChoices) {
            int choiceRank = this.getRankOfChoice(choice);
            if (choiceRank > worstRank) {
                worstRank = choiceRank;
            }
        }
        int out = 0;
        out = getIndexTopThree(this.getChoiceOfRank(worstRank));
        return out;
    }

    /**
     * Returns the number of classes the camper is enrolled in.
     *
     * @return the number of enrolled classes
     */
    public int getNumEnrolledClasses() {
        return numEnrolledClasses;
    }

    /**
     * Returns the swim level of the camper.
     *
     * @return the swim level of the camper
     */
    public int getSwimLevel() {
        return swimLevel;
    }

    /**
     * Sets the swim level of the camper.
     *
     * @param swimLevel the swim level to set
     */
    public void setSwimLevel(int swimLevel) {
        this.swimLevel = swimLevel;
    }

    /**
     * Returns the size of the final choices buffer.
     *
     * @return the size of the final choices buffer
     */
    @JsonIgnore
    public int getFinalChoicesBufferSize() {
        return this.finalChoicesBuffer.size();
    }

    /**
     * Returns the class schedule of the camper.
     *
     * @return a set of class periods 
     */
    public HashSet<ClassPeriod> getSchedule() {
        return new HashSet<ClassPeriod>(this.schedule);
    }

    /**
     * Checks if the camper is a Counselor-in-Training (CIT).
     *
     * @return true if the camper is a CIT, false otherwise
     */
    @JsonIgnore
    public boolean isCIT() {
        return isCIT;
    }

    /**
     * Checks if the camper is 10 years old or older.
     *
     * @return true if the camper is 10 or older, false otherwise
     */
    @JsonIgnore
    public boolean is10Plus() {
        return this.getAge() > 9;
    }

    /**
     * Checks if the camper requires swim lessons based on swim level.
     *
     * @return true if the camper requires swim lessons, false otherwise
     */
    public boolean requiresSwimLessons() {
        return this.swimLevel < 4;
    }

    /**
     * Checks if a class is in the top three choices of the camper.
     *
     * @param search the class to search for
     * @return true if the class is in the top three choices, false otherwise
     */
    public boolean containsClassInTopThree(ClassClass search) {
        for (ClassClass class_ : this.topClassChoices) {
            if (class_.equals(search)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is a duplicate class in the top three choices.
     *
     * @return true if a duplicate class exists, false otherwise
     */
    public boolean containsDuplicateInTopThree() {
        boolean containsTwoPeriodClass = false;
        for (ClassClass class_ : this.topClassChoices) {
            if (class_.isDoublePeriod()) {
                containsTwoPeriodClass = true;
                break;
            }
        }

        if (containsTwoPeriodClass) {
            return topClassChoices[0].equals(topClassChoices[1]) && topClassChoices[1].equals(topClassChoices[2]);
        } else {
            return topClassChoices[0].equals(topClassChoices[1]) || topClassChoices[0].equals(topClassChoices[2])
                    || topClassChoices[1].equals(topClassChoices[2]);
        }

    }

    /**
     * Checks if the camper can take a specified class based on age and swim level.
     *
     * @param class_ the class to check
     * @return true if the camper can take the class, false otherwise
     */
    public boolean canTakeClass(ClassClass class_) {
        if (class_.is10Plus() && !this.is10Plus()) {
            return false;
        } else if (class_.requiresSwimLevel() && this.swimLevel <= 3) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a class is in the final choices of the camper.
     *
     * @param search the class to search for
     * @return true if the class is in the final choices, false otherwise
     */
    public boolean containsClassInFinalChoices(ClassClass search) {
        for (ClassClass class_ : this.finalChoices) {
            if (class_.equals(search)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the camper is enrolled in a specified class.
     *
     * @param class_ the class to check enrollment for
     * @return true if the camper is enrolled in the class, false otherwise
     */
    public boolean isEnrolled(ClassClass class_) {
        boolean enrolledTwoPeriodClass = false;
        for (ClassPeriod period : this.schedule) {
            if (period.getClass_().isDoublePeriod()) {
                enrolledTwoPeriodClass = true;
                break;
            }
        }
        if (!enrolledTwoPeriodClass) {
            for (ClassPeriod period : this.schedule) {
                if (period.getTitle().equals(class_.getTitle())) {
                    return true;
                }
            }
            return false;
        } else {
            int periodCount = 0;
            for (ClassPeriod period : this.schedule) {
                if (period.getTitle().equals(class_.getTitle())) {
                    periodCount++;
                }
            }
            return periodCount > 1;
        }
    }

    /**
     * Checks if the camper can enroll in a specified class period.
     *
     * @param enroll the class period to check
     * @return true if the camper can enroll, false otherwise
     */
    public boolean canEnrollInClassPeriod(ClassPeriod enroll) {
        for (ClassPeriod period : this.schedule) {
            if (period.samePeriod(enroll)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the final choices buffer contains a specified class.
     *
     * @param class_ the class to check
     * @return true if the class is in the final choices buffer, false otherwise
     */
    public boolean finalChoicesBufferContains(ClassClass class_) {
        return this.finalChoicesBuffer.containsKey(class_);
    }

    /**
     * Checks if the camper is enrolled in the same class twice.
     *
     * @return true if enrolled in the same class twice, false otherwise
     */
    public boolean enrolledInSameClassTwice() {
        ArrayList<ClassClass> enrolledClassTitles = new ArrayList<ClassClass>();
        for (ClassPeriod period : this.schedule) {
            enrolledClassTitles.add(period.getClass_());
        }
        return Utility.containsDuplicateClassTitle(enrolledClassTitles);
    }

    /**
     * Configures the final class choices based on the highest and lowest ranked 
     * classes in the final choices buffer.
     */
    public void configureFinalChoices() {
        // first find highest (lowest int value) ranked class
        int highestRank = Integer.MAX_VALUE;
        ClassClass highestRankedChoice = null;
        for (ClassClass class_ : this.finalChoicesBuffer.keySet()) {
            if (highestRankedChoice == null) {
                highestRankedChoice = class_;
            }
            int classRank = this.finalChoicesBuffer.get(class_);
            if (classRank < highestRank) {
                highestRank = classRank;
                highestRankedChoice = class_;
            }
        }
        // find lowest (highest int value) ranked class
        int lowestRank = Integer.MIN_VALUE;
        ClassClass lowestRankedChoice = null;
        for (ClassClass class_ : this.finalChoicesBuffer.keySet()) {
            if (lowestRankedChoice == null) {
                lowestRankedChoice = class_;
            }
            int classRank = this.finalChoicesBuffer.get(class_);
            if (classRank > lowestRank) {
                lowestRank = classRank;
                lowestRankedChoice = class_;
            }
        }
        ClassClass middleChoice = null;
        for (ClassClass class_ : this.finalChoicesBuffer.keySet()) {
            if (!class_.equals(highestRankedChoice) && !class_.equals(lowestRankedChoice)) {
                middleChoice = class_;
                break;
            }
        }
        this.finalChoices[0] = highestRankedChoice;
        this.finalChoices[1] = middleChoice;
        if (!this.requiresSwimLessons()) {
            this.finalChoices[2] = lowestRankedChoice;
        } else {
            for (ClassClass class_ : ScheduleDriver.getClassList()) {
                if (class_.isRequired()) {
                    this.finalChoices[2] = class_;
                    break;
                }
            }
        }
        if (this.finalChoices[0].isDoublePeriod() || this.finalChoices[1].isDoublePeriod()) {
            // implies camper doesn't need swim lessons, remove third choice
            this.finalChoices[2] = null;
        }
    }

    /**
     * Adds a class choice to the choices of the camper with a specified rank.
     *
     * @param choice the class choice to add
     * @param rank   the rank of the class choice
     * @return true if the class choice is successfully added, false otherwise
     */
    public boolean addClassChoice(ClassClass choice, int rank) {
        if (rank > TOTAL_CLASSES) {
            return false;
        } else {
            if (rank >= 1 && rank <= 3) {
                this.topClassChoices[rank - 1] = choice;
            }
            this.totalClassChoices[rank - 1] = choice;
            return true;
        }
    }

    /**
     * Adds a class to the final choices buffer.
     *
     * @param choice the class choice to add to the buffer
     */
    public void addFinalChoiceToBuffer(ClassClass choice) {
        this.finalChoicesBuffer.put(choice, this.getRankOfChoice(choice));
    }

    /**
     * Enrolls the camper in a specified class period.
     *
     * @param classPeriod the class period to enroll in
     */
    public void enroll(ClassPeriod classPeriod) {
        this.schedule.add(classPeriod);
        this.numEnrolledClasses++;
    }

    /**
     * Un-enrolls the camper from a specified class period.
     *
     * @param classPeriod the class period to un-enroll from
     */
    public void unEnroll(ClassPeriod classPeriod) {
        this.schedule.remove(classPeriod);
        this.numEnrolledClasses--;
    }

    /**
     * Finds the next ranked class after a specified class.
     *
     * @param class_ the class to start searching from
     * @return the next ranked class choice
     */
    public ClassClass findNextRankedClass(ClassClass class_) {
        int rank = -1;
        try {
            rank = this.getRankOfChoice(class_);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        if (rank > 10) {
            System.out.println("HMM");
        }

        ClassClass choice = null;
        while (choice == null) {
            if (rank < this.totalClassChoices.length) {
                if (this.canTakeClass(this.totalClassChoices[rank])) {
                    choice = this.totalClassChoices[rank];
                } else {
                    rank++;
                }
            } else {
                break;
            }
        }

        if (choice == null) {
            System.out.println("PROBLEMM");
        }
        return choice;

    }

    /**
     * Clears the schedule and final class choices of the camper.
     */
    public void clearScheduleAndFinalChoices() {
        this.clearFinalChoicesBuffer();
        for (int i = 0; i < this.finalChoices.length; i++) {
            this.finalChoices[i] = null;
        }
        this.schedule.clear();
        this.numEnrolledClasses = 0;
    }

    /**
     * Returns the class the camper is enrolled in for a specified period.
     *
     * @param period the period to check
     * @return the class enrolled in during the specified period
     */
    public ClassClass classEnrolled(int period) {
        ClassClass out = null;
        for (ClassPeriod enrolledClass : this.schedule) {
            if (enrolledClass.getPeriod() == period) {
                out = enrolledClass.getClass_();
                break;
            }
        }
        return out;
    }

    /**
     * Returns the first period where the camper is not enrolled in any class.
     *
     * @return the first period not enrolled, or 0 if fully enrolled
     */
    @JsonIgnore
    public int getPeriodNotEnrolled() {
        for (int i = 1; i < 4; i++) {
            if (classEnrolled(i) == null) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Finds the highest-ranked class in the class slot that the camper is not 
     * enrolled in.
     *
     * @param classSlot the class slot to search
     * @return the highest-ranked unenrolled class period
     */
    public ClassPeriod findHighestRankedUnenrolledClass(ClassSlot classSlot) {
        int highestRanked = Integer.MAX_VALUE;
        ClassPeriod out = null;
        for (ClassPeriod slot : classSlot.getSlots()) {
            if (slot.open()) {
                ClassClass class_ = slot.getClass_();
                if (!class_.isRequired()) {
                    int classRank = this.getRankOfChoice(class_);
                    if (!this.isEnrolled(class_) && classRank < highestRanked) {
                        highestRanked = classRank;
                        out = slot;
                    }
                }
            }
        }
        return out;
    }

    /**
     * Finds the highest-ranked class in the class slot that the camper is not 
     * enrolled in, ignoring open/closed status.
     *
     * @param classSlot the class slot to search
     * @return the highest-ranked unenrolled class period
     */
    public ClassPeriod findHighestRankedUnenrolledClasssOverride(ClassSlot classSlot) {
        int highestRanked = Integer.MAX_VALUE;
        ClassPeriod out = null;
        for (ClassPeriod slot : classSlot.getSlots()) {
            ClassClass class_ = slot.getClass_();
            if (!class_.isRequired()) {
                int classRank = this.getRankOfChoice(class_);
                if (!this.isEnrolled(class_) && classRank < highestRanked) {
                    highestRanked = classRank;
                    out = slot;
                }
            }
        }
        return out;
    }

    /**
     * Clears the final choices buffer.
     */
    public void clearFinalChoicesBuffer() {
        this.finalChoicesBuffer.clear();
    }
}
