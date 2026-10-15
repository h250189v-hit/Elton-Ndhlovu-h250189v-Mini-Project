import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Saves and loads the library as three CSV files (Lab 10). */
public final class LibraryStorage {
    private LibraryStorage() { }

    public static void save(Library lib, Path dir) throws IOException {
        Files.createDirectories(dir);

        try (BufferedWriter w = Files.newBufferedWriter(dir.resolve("items.csv"))) {
            w.write("type,id,title,extra\n");
            for (LibraryItem i : lib.getItems())
                w.write(row(i.typeName(), i.getId(), i.getTitle(), i.csvExtra()));
        }
        try (BufferedWriter w = Files.newBufferedWriter(dir.resolve("members.csv"))) {
            w.write("id,name\n");
            for (Member m : lib.getMembers())
                w.write(row(m.getId(), m.getName()));
        }
        try (BufferedWriter w = Files.newBufferedWriter(dir.resolve("loans.csv"))) {
            w.write("itemId,memberId,borrowDate\n");
            for (Loan l : lib.getActiveLoans())
                w.write(row(l.getItem().getId(), l.getMember().getId(), l.getBorrowDate().toString()));
        }
    }

    /** Missing files simply mean "first run": an empty library is returned. */
    public static Library load(Path dir) throws IOException {
        Library lib = new Library();

        for (List<String> f : readRows(dir.resolve("members.csv"))) {
            try { lib.registerMember(new Member(f.get(0), f.get(1))); }
            catch (RuntimeException e) { warn("members.csv", f, e); }
        }
        for (List<String> f : readRows(dir.resolve("items.csv"))) {
            try { lib.addItem(LibraryItemFactory.create(f.get(0), f.get(1), f.get(2), f.get(3))); }
            catch (RuntimeException e) { warn("items.csv", f, e); }
        }
        for (List<String> f : readRows(dir.resolve("loans.csv"))) {
            try { lib.borrow(f.get(1), f.get(0), LocalDate.parse(f.get(2))); }
            catch (Exception e) { warn("loans.csv", f, e); }
        }
        return lib;
    }

    // ---------- CSV helpers ----------

    private static List<List<String>> readRows(Path file) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        if (!Files.exists(file)) return rows;
        try (BufferedReader r = Files.newBufferedReader(file)) {
            r.readLine();                                    // skip the header
            String line;
            while ((line = r.readLine()) != null)
                if (!line.isBlank()) rows.add(parseLine(line));
        }
        return rows;
    }

    private static String row(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(fields[i]));
        }
        return sb.append('\n').toString();
    }

    static String escape(String s) {
        if (s.contains(",") || s.contains("\""))
            return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }

    static List<String> parseLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else inQuotes = false;
                } else cur.append(c);
            } else if (c == '"') inQuotes = true;
            else if (c == ',') { out.add(cur.toString()); cur.setLength(0); }
            else cur.append(c);
        }
        out.add(cur.toString());
        return out;
    }

    private static void warn(String file, List<String> row, Exception e) {
        System.err.println("Skipped bad row in " + file + " " + row + ": " + e.getMessage());
    }
}
