package net.holmes.core.business.version;

/**
 * Manages Holmes version
 */
public interface VersionManager {

    /**
     * Get Current Holmes version.
     *
     * @return current version
     */
    String getCurrentVersion();

    /**
     * Get Holmes release info.
     *
     * @return release info
     */
    ReleaseInfo getReleaseInfo();

    /**
     * Updates release information.
     */
    void updateReleaseInfo();
}
