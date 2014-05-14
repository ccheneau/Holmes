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
     * Get Holmes release info on GitHub.
     *
     * @return release info
     */
    ReleaseInfo getRemoteReleaseInfo();

    /**
     * Updates release information from GitHub.
     */
    void updateRemoteReleaseInfo();
}
