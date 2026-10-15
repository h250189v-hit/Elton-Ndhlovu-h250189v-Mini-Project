import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/** Dependency-free tests: run with  java LibraryTest  (exit code 1 on any failure). */
public class LibraryTest {
    static int passed = 0, failed = 0;

    static void check(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "PASS  " : "FAIL  ") + name);
    }

    static Library sample() {
        Library lib = new Library();
        lib.addItem(new Book("B1", "Clean Code", "Robert Martin"));
        lib.addItem(new Book("B2", "Effective Java", "Joshua Bloch"));
        lib.addItem(new Book("B3", "Java Concurrency", "Brian Goetz"));
        lib.addItem(new Dvd("D1", "The Matrix", 136));
        lib.registerMember(new Member("M1", "Tariro"));
        lib.registerMember(new Member("M2", "Farai"));
        return lib;
    }

    public static void main(String[] args) throws Exception {
        // polymorphism
        check("Book loan = 14 days", new Book("x", "t", "a").loanPeriodDays() == 14);
        check("Dvd loan = 7 days", new Dvd("x", "t", 90).loanPeriodDays() == 7);

        // validation
        boolean threw = false;
        try { new Book("", "t", "a"); } catch (IllegalArgumentException e) { threw = true; }
        check("blank id rejected", threw);
        threw = false;
        try { new Dvd("d", "t", 0); } catch (IllegalArgumentException e) { threw = true; }
        check("zero-minute dvd rejected", threw);
        threw = false;
        try { LibraryItemFactory.create("vhs", "v", "t", "x"); } catch (IllegalArgumentException e) { threw = true; }
        check("factory rejects unknown type", threw);

        // borrow / return
        Library lib = sample();
        lib.borrow("M1", "B1");
        check("item unavailable after borrow", !lib.searchByTitle("clean").get(0).isAvailable());
        threw = false;
        try { lib.borrow("M2", "B1"); } catch (ItemNotAvailableException e) { threw = true; }
        check("ItemNotAvailableException on double borrow", threw);

        // limit
        lib.borrow("M1", "B2");
        lib.borrow("M1", "B3");
        threw = false;
        try { lib.borrow("M1", "D1"); } catch (MemberLimitExceededException e) { threw = true; }
        check("MemberLimitExceededException on 4th loan", threw);
        check("failed borrow left the DVD available", lib.searchByTitle("matrix").get(0).isAvailable());

        lib.returnItem("B1");
        check("item available after return", lib.searchByTitle("clean").get(0).isAvailable());
        lib.borrow("M1", "D1");
        check("can borrow again after a return", true);

        threw = false;
        try { lib.returnItem("B1"); } catch (IllegalStateException e) { threw = true; }
        check("returning an item not on loan fails", threw);
        threw = false;
        try { lib.borrow("NOPE", "B1"); } catch (IllegalArgumentException e) { threw = true; }
        check("unknown member rejected", threw);

        // search (streams)
        List<LibraryItem> hits = sample().searchByTitle("JAVA");
        check("search is case-insensitive and sorted",
                hits.size() == 2 && hits.get(0).getTitle().equals("Effective Java"));

        // overdue
        Library od = sample();
        LocalDate today = LocalDate.now();
        od.borrow("M1", "B1", today.minusDays(20));   // due 6 days ago
        od.borrow("M2", "D1", today.minusDays(3));    // due in 4 days
        List<Loan> overdue = od.listOverdue(today);
        check("only the late loan is overdue", overdue.size() == 1 && overdue.get(0).getItem().getId().equals("B1"));
        check("days overdue computed", overdue.get(0).daysOverdue(today) == 6);

        // persistence round trip, including commas and quotes in titles
        Library p = sample();
        p.addItem(new Book("B9", "Hello, \"World\"", "Doe, Jane"));
        p.borrow("M1", "B2", LocalDate.of(2026, 1, 5));
        Path dir = Files.createTempDirectory("libtest");
        LibraryStorage.save(p, dir);
        Library q = LibraryStorage.load(dir);
        check("items survive reload", q.getItems().size() == 5);
        check("members survive reload", q.getMembers().size() == 2);
        check("loan survives reload", q.getActiveLoans().size() == 1);
        check("loaned item still unavailable", !q.searchByTitle("effective").get(0).isAvailable());
        check("loan date preserved", q.getActiveLoans().iterator().next().getBorrowDate().equals(LocalDate.of(2026, 1, 5)));
        check("comma/quote title preserved", q.searchByTitle("hello").get(0).getTitle().equals("Hello, \"World\""));
        check("member loan count restored", q.getMembers().stream().filter(m -> m.getId().equals("M1")).findFirst().get().getLoans().size() == 1);
        check("loading a missing folder gives empty library",
                LibraryStorage.load(dir.resolve("nothing-here")).getItems().isEmpty());

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
