package com.campclaire.campscheduler;

/**
 * Represents a resident of the camp with basic attributes such as age and name.
 */
public class CampResident {
    protected int age;
    protected String name;

    /**
     * Default constructor initializing the resident with default values.
     */
    public CampResident() {
        this.age = 0;
        this.name = "NONE";
    }

    /**
     * Constructor initializing the resident with specified age and name.
     *
     * @param age the age of the resident
     * @param name the name of the resident
     */
    public CampResident(int age, String name) {
        this.setAge(age);
        this.setName(name);
    }

    /**
     * Gets the age of the resident.
     *
     * @return the age of the resident
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age of the resident.
     *
     * @param age the age to set for the resident
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Gets the name of the resident.
     *
     * @return the name of the resident
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the resident.
     *
     * @param name the name to set for the resident
     */
    public void setName(String name) {
        this.name = name;
    }
}
