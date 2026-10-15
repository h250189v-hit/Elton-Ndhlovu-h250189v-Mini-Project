import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Console menu. Contains no business rules; it only talks to Library. */
public class Main {
    private final Library library;
    private final Scanner in = new Scanner(System.in);

    private Main(Library library) { this.library = library; }

    public static void main(String[] args) {
        Path dir = Paths.get(args.length > 0 ? args[0] : "data");
        Library lib;
        try {
            lib = LibraryStorage.load(dir);
            System.out.println("Loaded " + lib.getItems().size() + " items, "
                    + lib.getMembers().size() + " members, "
                    + lib.getActiveLoans().size() + " active loans from '" + dir + "'");
        } catch (IOException e) {
            System.out.println("Could not read saved data (" + e.getMessage() + "). Starting empty.");
            lib = new Library();
        }

        new Main(lib).run();

        try {
            LibraryStorage.save(lib, dir);
            System.out.println("Data saved to '" + dir + "'. Goodbye!");
        } catch (IOException e) {
            System.out.println("ERROR: could not save data: " + e.getMessage());
        }
    }

    private void run() {
        boolean running = true;
        while (running) {
            printMenu();
            try {
                String choice = ask("Choose").trim();
                switch (choice) {
                    case "1" -> addItem();
                    case "2" -> registerMember();
                    case "3" -> borrow();
                    case "4" -> giveBack();
                    case "5" -> listOverdue();
                    case "6" -> search();
                    case "7" -> library.getItems().forEach(System.out::println);
                    case "8" -> library.getMembers().forEach(System.out::println);
                    case "0" -> running = false;
                    default  -> System.out.println("Please choose 0-8.");
                }
            } catch (ItemNotAvailableException | MemberLimitExceededException e) {
                System.out.println("Cannot borrow: " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NoSuchElementException e) {      // input ended (Ctrl+D): leave and save
                running = false;
            }
        }
    }

    private void printMenu() {
        System.out.println("""

                ===== LIBRARY MENU =====
                1. Add item          5. List overdue items
                2. Register member   6. Search by title
                3. Borrow            7. List all items
                4. Return            8. List members
                0. Save and exit""");
    }

    private String ask(String prompt) {
        System.out.print(prompt + ": ");
        return in.nextLine();
    }

    private void addItem() {
        String type  = ask("Type (book/dvd)");
        String id    = ask("Item id");
        String title = ask("Title");
        String extra = ask(type.trim().equalsIgnoreCase("dvd") ? "Duration in minutes" : "Author");
        library.addItem(LibraryItemFactory.create(type, id, title, extra));
        System.out.println("Item added.");
    }

    private void registerMember() {
        library.registerMember(new Member(ask("Member id"), ask("Name")));
        System.out.println("Member registered.");
    }

    private void borrow() throws ItemNotAvailableException, MemberLimitExceededException {
        Loan loan = library.borrow(ask("Member id"), ask("Item id"));
        System.out.println("Borrowed: " + loan);
    }

    private void giveBack() {
        Loan loan = library.returnItem(ask("Item id"));
        long late = loan.daysOverdue(LocalDate.now());
        System.out.println("Returned: " + loan.getItem().getTitle()
                + (late > 0 ? " (" + late + " days late)" : " (on time)"));
    }

    private void listOverdue() {
        List<Loan> overdue = library.listOverdue(LocalDate.now());
        if (overdue.isEmpty()) { System.out.println("No overdue items."); return; }
        overdue.forEach(l -> System.out.println(l + " - " + l.daysOverdue(LocalDate.now()) + " days overdue"));
    }

    private void search() {
        List<LibraryItem> found = library.searchByTitle(ask("Title contains"));
        if (found.isEmpty()) System.out.println("No matches.");
        else found.forEach(System.out::println);
    }
}
