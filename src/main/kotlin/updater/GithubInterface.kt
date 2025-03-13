package io.github.zmilla93.updater

import com.google.gson.Gson
import io.github.zmilla93.updater.github.GitHubAsset
import io.github.zmilla93.updater.github.GitHubRelease
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Handles interacting with the GitHub API.
 *
 * Endpoints:
 *  api.github.com/repos/AUTHOR/REPO/releases
 *  api.github.com/repos/AUTHOR/REPO/releases/latest
 *  api.github.com/repos/AUTHOR/REPO/releases/tag/TAG
 * Artifact URL:
 *  github.com/AUTHOR/REPO/releases/download/TAG/ARTIFACT
 */
class GithubInterface(
    val author: String,
    val repo: String,
    var allowPreRelease: Boolean = false
) {

    var hasCachedData = false

    //    var releases = emptyArray<OLD_GitHubRelease>()
    val RELEASES_ENDPOINT: String
    var releases = emptyArray<GitHubRelease>();
    var latestRelease: GitHubRelease? = null;
    val logger = LoggerFactory.getLogger(javaClass)
    val gson = Gson()

    init {
        RELEASES_ENDPOINT = "https://api.github.com/repos/$author/$repo/releases"
    }

    fun latestRelease(): GitHubRelease? {
        fetchData()
        return null
    }

    fun fetchData(forceCheck: Boolean = false) {
        logger.info("Fetching data...")
        println("Endpoint: " + RELEASES_ENDPOINT)
        if (hasCachedData && !forceCheck) return
        val data = fetchDataFromEndpoint(RELEASES_ENDPOINT)
        releases = gson.fromJson(data, Array<GitHubRelease>::class.java);
        if (releases.isNotEmpty()) latestRelease = releases[0]
        println("Releases: " + releases.size)
        println(releases[0].name)
        println(releases[0].body)
        for (asset: GitHubAsset in releases[0].assets) {
            println("\t${asset.name}")
            println("\t${asset.url}")
            println("\t${asset.browser_download_url}")
        }
        hasCachedData = true
    }

    fun fetchDataFromEndpoint(endpoint: String, forceCheck: Boolean = false): String? {
        logger.info("Fetching data from $endpoint...")
        if (hasCachedData && !forceCheck) return null
        try {
            val httpConnection = (URL(RELEASES_ENDPOINT).openConnection()) as HttpURLConnection
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
            logger.error("Malformed releases URL: $RELEASES_ENDPOINT")
            e.printStackTrace()
        } catch (e: IOException) {
            logger.error("IOException fetching releases: $RELEASES_ENDPOINT")
            e.printStackTrace()
        }
        return null;
    }


}