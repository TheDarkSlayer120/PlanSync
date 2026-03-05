package views;

/**
 * Simple hook so AppController can ask views to refresh when they become visible.
 */
public interface RefreshableView {
    void refresh();
}