# Priority Satisfaction Scheduler

## Overview

This Java project is a scheduling algorithm originally designed for Camp Claire Inc.'s class scheduling but adapted for general use. The algorithm schedules participants into classes based on their preferences while adhering to various constraints and criteria. It aims to balance camper preferences with class availability and requirements, ensuring an efficient and fair schedule for all participants.

## Features

- **Class Preferences:** Participants rank their top class choices. The algorithm tries to accommodate these preferences as much as possible.
- **Single Period Cutoff (SPC):** Each class has an SPC which determines how many campers should be ideally added to any single period and how many top-three rankings are needed to include the class in the schedule.
- **Period Allocation:** The camp offers 3 periods per day, and each participant retains their schedule for the entire week.
- **Class Properties:**
  - **Restricted Periods:** Specific periods where a class can be scheduled.
  - **Double Period:** Indicates if a class spans two periods with the same roster.
  - **Is Required:** Some classes may be mandatory for participants based on their needs.
  - **Is 10 Plus:** Age restriction for class enrollment.
  - **Must be Consecutive:** For classes with multiple periods, they must be scheduled consecutively.
  - **Requires Swim Level:** Some classes require a specific swim ability.

## Scoring System

The algorithm ranks schedules based on:
- **Participant Preferences:** Classes are scored based on how closely the final schedule matches participants' top choices. A lower score indicates better alignment with participant preferences.
- **Class Distribution:** Classes with more evenly distributed periods are favored. The difference between the most and least filled periods of a class is added to the score, weighted by the class's SPC.

## Algorithm Workflow

1. **Initial Setup:**
   - Participants' top three class choices are collected, and invalid options are pruned based on class restrictions.
   
2. **Class Filtering:**
   - Classes are filtered based on the number of top-three votes. Only classes meeting the SPC/2 + 1 threshold are considered.
   
3. **Period Allocation:**
   - Classes are allocated periods based on the SPC and number of top-three votes:
     - **Three Periods:** If classCount > 2 * SPC
     - **Two Periods:** If classCount > SPC
     - **One Period:** Otherwise
   
4. **Class Slot Initialization:**
   - Class slots are created and distributed across three periods. Restrictions are applied to ensure compliance with class properties.
   
5. **Slot Filling:**
   - Classes are added to slots while respecting restrictions. Classes unable to be allocated due to restrictions are eliminated.
   
6. **Period Adjustment:**
   - Adjust the number of periods for each class based on actual slot filling.
   
7. **Essential Class Assignment:**
   - Participants are assigned to essential classes first (required or double-period classes).
   
8. **Non-Essential Class Assignment:**
   - Participants are then assigned to non-essential classes based on their rankings. If a preferred class isn't available, the algorithm seeks to place them in the next highest-ranked available class.

## Usage

1. **Input:**
   - Use the `ScheduleDriver` UI to input class properties, participant preferences, and other scheduling constraints.
   
2. **Execution:**
   - Run the scheduling algorithm to generate multiple possible schedules. Each schedule is evaluated and ranked based on the scoring system.
   
3. **Output:**
   - The best schedule, according to the scoring system, is selected and presented to participants.

## Example

For a camp offering Sports, Waterfront, Fishing, Archery, Yoga, Arts and Crafts, and Sailing:
- A camper's top choices are Waterfront (1), Archery (2), and Yoga (3).
- The algorithm tries to include these classes in the camper's schedule. If any of these classes are eliminated due to SPC, the algorithm attempts to place the camper in the next highest-ranked available class.

## Contributing

Contributions are welcome. Please fork the repository and submit a pull request with improvements or bug fixes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For any questions or issues, please reach out to liam1@terpmail.umd.edu.
