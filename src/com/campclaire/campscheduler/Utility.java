package com.campclaire.campscheduler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * The Utility class provides static utility methods for performing common tasks
 * such as deep copying objects and checking for duplicates in lists.
 */
public class Utility {

    /**
     * Creates a deep copy of the given Serializable object.
     *
     * @param <T> the type of the object to be copied
     * @param object the object to be deep copied
     * @return a deep copy of the object, or null if the copy operation fails
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCopy(T object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if the given list of integers contains any duplicate values.
     *
     * @param list the list of integers to check for duplicates
     * @return true if a duplicate value is found, false otherwise
     */
    public static boolean containsDuplicateInteger(ArrayList<Integer> list) {
        HashSet<Integer> set = new HashSet<>();

        for (Integer num : list) {
            if (set.contains(num)) {
                return true; // Duplicate found
            }
            set.add(num);
        }

        return false; // No duplicates found
    }

    /**
     * Checks if the given list of ClassClass objects contains any duplicate class
     * titles, ignoring duplicates for double-period classes.
     *
     * @param list the list of ClassClass objects to check for duplicates
     * @return true if a duplicate class title is found, false otherwise
     */
    public static boolean containsDuplicateClassTitle(ArrayList<ClassClass> list) {
        HashSet<ClassClass> set = new HashSet<>();

        for (ClassClass class_ : list) {
            if (!class_.isDoublePeriod() && set.contains(class_)) {
                return true; // Duplicate found
            }
            set.add(class_);
        }

        return false; // No duplicates found
    }
}
