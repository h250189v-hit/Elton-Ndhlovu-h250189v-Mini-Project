# Elton-Ndhlovu-h250189v-Mini-Project
# Library Management System – Mini-Project

## 1. Project Overview

The Library Management System is a Java console application designed to make it easier to manage library operations. It allows users to add books and DVDs, register members, borrow and return items, search for library items, and check for overdue loans.

The project brings together the Object-Oriented Programming (OOP) concepts covered in Labs 1–10, including encapsulation, inheritance, polymorphism, abstraction, exception handling, collections, streams, and CSV file handling. It also uses the Factory Design Pattern introduced in Lab 11.

The main goal of the project is to demonstrate how these concepts can be combined to create a practical, organized, and reliable application.

## 2. Requirements

- Java Development Kit (JDK) 17 or later.
- A terminal or command prompt to compile and run the program.

## 3. How to Compile and Run

Follow these steps to run the application.

First, open a terminal and navigate to the `src` folder:

```bash
cd src
```

Compile the Java files:

```bash
javac *.java
```

Start the Library Management System:

```bash
java Main
```

By default, the application stores its data in the `./data` folder. You can also specify a different folder when starting the application:

```bash
java Main path/to/folder
```

To run the automated tests, use:

```bash
java LibraryTest
```

## 4. Implementation of OOP Concepts

The project uses several OOP concepts to keep the code organized, reusable, and easy to maintain.

| Concept | Implementation |
|---|---|
| Encapsulation | Class fields are private, and constructors validate the supplied data. |
| Inheritance | `Book` and `Dvd` inherit from the abstract `LibraryItem` class. |
| Abstraction | `LibraryItem` defines the common structure and behaviour of library items. |
| Polymorphism | Books and DVDs use their own loan periods through the `loanPeriodDays()` method. |
| Interfaces | `LibraryItem` implements the `Borrowable` interface. |
| Exception handling | Custom exceptions handle unavailable items and members who have reached their borrowing limit. |
| Collections | Maps store library items, members, and active loans using their IDs as keys. |
| Streams | Streams are used to search for items and identify overdue loans. |
| File handling | `LibraryStorage` saves and loads information using CSV files. |
| Factory Pattern | `LibraryItemFactory` is responsible for creating different types of library items. |

The `Member` class also prevents outside code from directly modifying its loan collection by returning an unmodifiable view through `getLoans()`.

## 5. Main Features

The application provides a menu that allows users to perform the following operations:

1. Add new books or DVDs to the library.
2. Register new library members.
3. Borrow available items.
4. Return borrowed items.
5. View overdue loans.
6. Search for items by title.
7. Display all library items.
8. Display registered members.

The system also enforces borrowing rules, including a maximum of three active loans per member.

## 6. Design and Implementation Decisions

### Polymorphism

Books and DVDs have different borrowing periods. Books can be borrowed for 14 days, while DVDs can be borrowed for 7 days. Instead of checking the type of each item whenever a loan is created, the system calls `loanPeriodDays()` on the item itself. This makes the code easier to extend if new item types are introduced.

### Reliable Borrowing Process

Before an item is marked as borrowed, the system checks whether it is available and whether the member has reached the borrowing limit. If a check fails, the operation is rejected without changing the item's availability.

### Exception Handling

Custom exceptions are used to handle common borrowing problems, such as attempting to borrow an unavailable item or exceeding the maximum number of loans. Other exceptions handle invalid input and incorrect operations, such as returning an item that is not currently on loan.

### Data Storage

The system saves information in three CSV files: `items.csv`, `members.csv`, and `loans.csv`. These files allow the application to retain its data after it is closed and restore it when it starts again.

Item availability is reconstructed from the saved loan records rather than stored separately. This helps prevent inconsistencies between the item and loan data.

### Separation of Responsibilities

The application is divided into classes with specific responsibilities:

- `Main` handles the menu and user interaction.
- `Library` manages the library's operations and business rules.
- `LibraryStorage` handles saving and loading data.
- `LibraryItemFactory` creates library item objects.
- `LibraryItem`, `Book`, `Dvd`, `Member`, and `Loan` represent the main entities in the system.

This structure makes the application easier to understand, test, and maintain.

### CSV Data Validation

The system handles commas and quotation marks in CSV fields by escaping them correctly. This means titles such as `Hello, "World"` can be saved and loaded without losing their original formatting.

If a CSV file contains an invalid row, the system displays a warning and skips that row instead of stopping the entire application.

## 7. Testing and Results

The project includes a simple automated test program called `LibraryTest`, which runs 24 checks without requiring an external testing framework such as JUnit.

The tests cover:

- Different loan periods for books and DVDs.
- Input validation.
- Custom exception handling.
- Borrowing failures without unwanted changes to the system.
- Rules for returning items.
- Case-insensitive searches with sorted results.
- Overdue loan calculations.
- Saving and reloading data.
- Handling special characters in CSV files.
- Working with a missing data folder.

**Test results: 24 passed, 0 failed.**

A manual test was also performed by running the application in two separate sessions to confirm that data was retained after restarting it. An older loan was also tested using a due date of 1 September 2026, and the system correctly reported it as 29 days overdue.

## 8. Limitations and Future Improvements

Although the system covers the main library operations, there are several features that could be added in the future.

- **Loan history:** Returned loans are removed from the active loan records, so the system does not maintain a complete borrowing history.
- **Fines:** The system does not calculate penalties for overdue items.
- **Reservations:** Members cannot reserve items that are currently unavailable.
- **Member management:** There is no option to delete registered members.
- **Automatic IDs:** Item and member IDs must be entered manually.
- **Graphical interface:** The current application uses a console menu, but it could be upgraded to a graphical user interface.
- **Database integration:** CSV storage could be replaced with a database for more advanced data management.

These improvements could make the system more suitable for use in a larger library.

## 9. Conclusion

This project demonstrates how Java and OOP concepts can be used to develop a functional Library Management System. It supports the main activities involved in managing library items, members, and loans while enforcing borrowing rules and preserving data between sessions.

The project also highlights the importance of exception handling, file persistence, automated testing, and separating responsibilities between classes. Although there is room for further development, the current implementation provides a solid foundation for a more advanced library management application.
