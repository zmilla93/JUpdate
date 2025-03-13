package io.github.zmilla93.updater.github

/**
 * A GitHub release.
 */
class GitHubRelease {

    var name = ""
    var tag_name = ""
    var draft = false
    var prerelease = false
    var created_at = ""
    var published_at = ""
    var tarball_url = ""
    var zipball_url = ""
    var body = ""
    var mentions_count = ""
    var assets = emptyArray<GitHubAsset>()

}