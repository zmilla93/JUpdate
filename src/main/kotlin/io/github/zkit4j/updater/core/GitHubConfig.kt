package io.github.zkit4j.updater.core

class GitHubConfig(
    val author: String,
    val repo: String,
    val allowPreRelease: Boolean = false
)