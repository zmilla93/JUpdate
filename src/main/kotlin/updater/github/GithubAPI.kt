package io.github.zmilla93.updater.github

import com.google.gson.Gson
import io.github.zmilla93.updater.core.GitHubConfig
import io.github.zmilla93.updater.core.UpdateUtil
import io.github.zmilla93.updater.listening.DownloadProgressListener
import org.slf4j.LoggerFactory
import java.io.*
import java.net.MalformedURLException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.swing.SwingUtilities


/**
 * Handles interacting with the GitHub Releases API.
 * Endpoints:
 *  api.github.com/repos/AUTHOR/REPO/releases
 *  api.github.com/repos/AUTHOR/REPO/releases/latest
 */
// FIXME : Alert on EDT
class GithubAPI(
    author: String,
    repo: String,
    val progressListeners: ArrayList<DownloadProgressListener>,
    var allowPreRelease: Boolean = false,
) {

    constructor(config: GitHubConfig, progressListeners: ArrayList<DownloadProgressListener>) : this(
        config.author,
        config.repo,
        progressListeners
    )

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
    private val logger = LoggerFactory.getLogger(javaClass.simpleName)
    private val gson = Gson()


//    val downloadProgressListeners = ArrayList<DownloadProgressListener>()


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
        hasCachedLatestRelease = true
        val data = fetchDataFromEndpoint(LATEST_RELEASE_ENDPOINT)
        if (data == null) return
        latestRelease = gson.fromJson(data, GitHubRelease::class.java);
    }

    private fun fetchAllReleases(forceCheck: Boolean = false) {
        if (hasCachedAllReleases && !forceCheck) return
        hasCachedAllReleases = true
        val data = fetchDataFromEndpoint(ALL_RELEASES_ENDPOINT) ?: return
        releases = gson.fromJson(data, Array<GitHubRelease>::class.java) ?: return
        if (allowPreRelease && releases!!.isNotEmpty()) latestRelease = releases!![0]
    }

    private fun fetchDataFromEndpoint(endpoint: String): String? {
        logger.info("Fetching release data from $endpoint...")
        try {
            // GitHub API only suppers TSLv1.2
            val sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, SecureRandom())
            val client: HttpClient = UpdateUtil.getHTTPClient()
            val request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .build()
            val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        } catch (e: MalformedURLException) {
            logger.error("Malformed releases URL: $ALL_RELEASES_ENDPOINT")
            e.printStackTrace()
        } catch (e: IOException) {
            logger.error("IOException fetching releases: $ALL_RELEASES_ENDPOINT")
            e.printStackTrace()
        }
        return null;
    }


    fun downloadFile(source: String, destination: Path): Boolean {
        try {
//            if (latestRelease == null) fetchLatestRelease();
//            if (latestRelease == null) return false;
            for (listener in progressListeners)
                SwingUtilities.invokeLater { listener.onDownloadStart(source) }
            logger.info("Downloading file...");
            logger.info("\tSource      : $source");
            logger.info("\tDestination : $destination");
            Files.createDirectories(destination.parent)
//            val httpConnection = URL(source).openConnection() as HttpURLConnection
//            val fileSize = httpConnection.getContentLength()
            val client = UpdateUtil.getHTTPClient()
            val request = HttpRequest.newBuilder()
                .uri(URI.create(source))
                .header("User-Agent", "Mozilla/5.0")
                .header("Accept", "*/*")
                .GET()
                .build();
            val response: HttpResponse<InputStream> = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
            logger.info("Reponse code: " + response.statusCode())
            logger.info("Location: " + response.headers().firstValue("Location"))
            val fileSize: Long = response.headers().firstValueAsLong("Content-Length").orElse(-1)
            logger.info("File size: $fileSize")
            val inputStream = BufferedInputStream(response.body())
            val outputStream = BufferedOutputStream(Files.newOutputStream(destination))
            val data = ByteArray(BYTE_BUFFER_SIZE)
            var totalBytesRead = 0
            var numBytesRead = 0
            var currentProgressPercent = 0
            while (true) {
                numBytesRead = inputStream.read(data, 0, BYTE_BUFFER_SIZE)
                if (numBytesRead < 0) break
                outputStream.write(data, 0, numBytesRead)
                totalBytesRead += numBytesRead
                val newProgressPercent = Math.round(totalBytesRead.toFloat() / fileSize * 100)
                if (newProgressPercent != currentProgressPercent) {
                    currentProgressPercent = newProgressPercent
                    for (listener in progressListeners) {
                        SwingUtilities.invokeLater { listener.onDownloadProgress(currentProgressPercent) }
                    }
                }
            }
            inputStream.close()
            outputStream.close()
            for (listener in progressListeners)
                SwingUtilities.invokeLater(listener::onDownloadComplete)
            logger.info("File downloaded successfully!")
            return true
        } catch (e: IOException) {
            logger.error("Error downloading file: $source")
            e.printStackTrace()
            for (listener in progressListeners)
                SwingUtilities.invokeLater(listener::onDownloadFailed)
            return false
        }
    }

    fun addDownloadProgressListener(listener: DownloadProgressListener) {
        progressListeners.add(listener)
    }

    fun removeDownloadProgressListener(listener: DownloadProgressListener) {
        progressListeners.remove(listener)
    }

    fun clearDownloadProgressListeners() {
        progressListeners.clear()
    }

}