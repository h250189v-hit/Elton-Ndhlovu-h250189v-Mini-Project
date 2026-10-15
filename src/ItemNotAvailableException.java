/** Checked exception: the item is already on loan. */
public class ItemNotAvailableException extends Exception {
    private static final long serialVersionUID = 1L;

    public ItemNotAvailableException(LibraryItem item) {
        super("'" + item.getTitle() + "' (" + item.getId() + ") is already on loan");
    }
}
