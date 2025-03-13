package io.github.zmilla93.updater

import com.google.gson.Gson
import io.github.zmilla93.updater.github.GitHubRelease
import org.slf4j.LoggerFactory
import updater.IUpdateProgressListener
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.SwingUtilities

/**
 * Handles interacting with the GitHub Releases API.
 * Endpoints:
 *  api.github.com/repos/AUTHOR/REPO/releases
 *  api.github.com/repos/AUTHOR/REPO/releases/latest
 */
class GithubAPI(
    author: String,
    repo: String,
    var allowPreRelease: Boolean = false
) {
    // GitHub API Endpoints
    private val ALL_RELEASES_ENDPOINT: String = "https://api.github.com/repos/$author/$repo/releases"
    private val LATEST_RELEASE_ENDPOINT: String = "https://api.github.com/repos/$author/$repo/releases/latest"

    // Cache flags
    private var hasCachedAllReleases = false
    private var hasCachedLatestRelease = false

    // Response data
    private var releases: Array<GitHubRelease>? = null;
    private var latestRelease: GitHubRelease? = null

    // Internal
    private val logger = LoggerFactory.getLogger(javaClass)
    private val gson = Gson()
    private val progressListeners = ArrayList<IUpdateProgressListener>()


    // TODO : Add a function to fetch patch notes, and possibly save them to disk.
    companion object {
        const val BYTE_BUFFER_SIZE: Int = 1024 * 4
    }

    /**
     * Returns the latest release, fetching it if necessary,
     * or null if something went wrong.
     * */
    fun latestRelease(forceCheck: Boolean = false): GitHubRelease? {
        if (allowPreRelease) fetchAllReleases(forceCheck)
        else fetchLatestRelease(forceCheck)
        return latestRelease
    }

    /**
     * Returns a list of all releases, fetching it if necessary,
     * or null if something went wrong.
     * */
    fun releases(forceCheck: Boolean = false): Array<GitHubRelease>? {
        fetchAllReleases(forceCheck)
        return releases
    }

    private fun fetchLatestRelease(forceCheck: Boolean = false) {
        if (hasCachedLatestRelease && !forceCheck) return
        val data = fetchDataFromEndpoint(LATEST_RELEASE_ENDPOINT)
        if (data == null) return
        latestRelease = gson.fromJson(data, GitHubRelease::class.java);
        hasCachedLatestRelease = true
    }

    private fun fetchAllReleases(forceCheck: Boolean = false) {
        if (hasCachedAllReleases && !forceCheck) return
        val data = fetchDataFromEndpoint(ALL_RELEASES_ENDPOINT)
        if (data == null) return
        releases = gson.fromJson(data, Array<GitHubRelease>::class.java)
        if (releases == null) return
        if (allowPreRelease && releases!!.isNotEmpty()) latestRelease = releases!![0]
        hasCachedAllReleases = true
    }

    private fun fetchDataFromEndpoint(endpoint: String): String? {
        logger.info("Fetching data from $endpoint...")
        try {

            val httpConnection = (URL(endpoint).openConnection()) as HttpURLConnection
            val inputStream: BufferedReader
            try {
                inputStream = BufferedReader(InputStreamReader(httpConnection.inputStream, StandardCharsets.UTF_8))
            } catch (e: IOException) {
                logger.error("Failed to connect to GitHub. This is either a connection issue or the API rate limit has been exceeded.")
                return null
            }
            val builder = StringBuilder()
            while (inputStream.ready()) builder.append(inputStream.readLine())
            inputStream.close()
            return builder.toString()
        } catch (e: MalformedURLException) {
            logger.error("Malformed releases URL: $ALL_RELEASES_ENDPOINT")
            e.printStackTrace()
        } catch (e: IOException) {
            logger.error("IOException fetching releases: $ALL_RELEASES_ENDPOINT")
            e.printStackTrace()
        }
        return null;
    }


    fun downloadFile(source: String, destination: String): Boolean {
        try {
//            if (latestRelease == null) fetchLatestRelease();
//            if (latestRelease == null) return false;
            logger.info("Downloading file...");
            logger.info("\tSource      : $source");
            logger.info("\tDestination : $destination");
            val httpConnection = URL(source).openConnection() as HttpURLConnection
            val fileSize = httpConnection.getContentLength();
            val inputStream = BufferedInputStream(httpConnection.inputStream);
            val outputStream = BufferedOutputStream(Files.newOutputStream(Paths.get(destination)));
            val data = ByteArray(BYTE_BUFFER_SIZE)
            var totalBytesRead = 0;
            var numBytesRead = 0
            var currentProgressPercent = 0;
            while (true) {
                numBytesRead = inputStream.read(data, 0, BYTE_BUFFER_SIZE)
                if (numBytesRead < 0) break
                outputStream.write(data, 0, numBytesRead);
                totalBytesRead += numBytesRead;
                val newProgressPercent = Math.round(totalBytesRead.toFloat() / fileSize * 100);
                if (newProgressPercent != currentProgressPercent) {
                    currentProgressPercent = newProgressPercent;
                    for (listener in progressListeners) {
                        SwingUtilities.invokeLater { listener.onDownloadProgress(currentProgressPercent) }
                    }
                }
            }
            inputStream.close();
            outputStream.close();
            for (listener in progressListeners)
                SwingUtilities.invokeLater(listener::onDownloadComplete);
            return true;
        } catch (e: IOException) {
            logger.error("Error downloading file: $source");
            e.printStackTrace()
            for (listener in progressListeners)
                SwingUtilities.invokeLater(listener::onDownloadFailed);
            return false;
        }
    }

}