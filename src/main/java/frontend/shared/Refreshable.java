package main.java.frontend.shared;

/**
 * Refreshable interface — implemented by workspace card panels 
 * to automatically reload their data from files when the user
 * navigates to them via the sidebar.
 */
public interface Refreshable {
    void refreshData();
}
