/** Factory pattern (Lab 11): callers get a LibraryItem without naming concrete classes. */
public final class LibraryItemFactory {
    private LibraryItemFactory() { }

    public static LibraryItem create(String type, String id, String title, String extra) {
        if (type == null) throw new IllegalArgumentException("Item type must not be empty");
        switch (type.trim().toUpperCase()) {
            case "BOOK":
                return new Book(id, title, extra);
            case "DVD":
                try {
                    return new Dvd(id, title, Integer.parseInt(extra.trim()));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("DVD duration must be a whole number of minutes: " + extra);
                }
            default:
                throw new IllegalArgumentException("Unknown item type: " + type);
        }
    }
}
