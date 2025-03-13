package io.github.zmilla93.updater.github

/**
 * An array of [GitHubRelease]s.
 * Response from api.github.com/repos/$author/$repo/releases
 */
class GitHubReleaseResponse() {

    var releases = emptyArray<GitHubRelease>()

}