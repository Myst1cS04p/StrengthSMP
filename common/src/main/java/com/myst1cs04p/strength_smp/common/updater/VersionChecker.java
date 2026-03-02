package com.myst1cs04p.strength_smp.common.updater;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Fetches the latest release version from GitHub asynchronously.
 * Uses Gson for JSON parsing (shaded into the jar) instead of json-simple.
 */
public class VersionChecker {

    private final Logger logger;
    private final String owner;
    private final String repo;

    private volatile String cachedLatestVersion;

    public VersionChecker(Logger logger, String owner, String repo) {
        this.logger = logger;
        this.owner = owner;
        this.repo = repo;
    }

    /**
     * @return CompletableFuture resolving to the latest version tag, or null on failure.
     */
    public CompletableFuture<String> fetchLatestVersion() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";
                HttpURLConnection connection = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                JsonObject release = JsonParser.parseString(response.toString()).getAsJsonObject();
                this.cachedLatestVersion = release.get("tag_name").getAsString();
                return cachedLatestVersion;

            } catch (Exception e) {
                logger.warning("[StrengthSMP] Failed to fetch version info from GitHub: " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * Returns true if {@code latest} is a higher semantic version than {@code current}.
     * Strips any leading 'v' or 'V' prefix before comparing.
     */
    public boolean isNewerVersion(String latest, String current) {
        if (latest == null || current == null) return false;

        String[] lp = latest.replaceAll("[^0-9.]", "").split("\\.");
        String[] cp = current.replaceAll("[^0-9.]", "").split("\\.");

        int length = Math.max(lp.length, cp.length);
        for (int i = 0; i < length; i++) {
            int l = (i < lp.length && !lp[i].isEmpty()) ? Integer.parseInt(lp[i]) : 0;
            int c = (i < cp.length && !cp[i].isEmpty()) ? Integer.parseInt(cp[i]) : 0;
            if (l > c) return true;
            if (l < c) return false;
        }
        return false;
    }

    public String getCachedLatestVersion() {
        return cachedLatestVersion;
    }
}
