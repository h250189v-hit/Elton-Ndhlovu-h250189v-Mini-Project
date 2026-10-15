public class Book extends LibraryItem {
    private final String author;

    public Book(String id, String title, String author) {
        super(id, title);
        this.author = requireText(author, "Author");
    }

    public String getAuthor() { return author; }

    @Override public int loanPeriodDays() { return 14; }
    @Override public String typeName()    { return "BOOK"; }
    @Override protected String details()  { return "by " + author; }
    @Override public String csvExtra()    { return author; }
}
