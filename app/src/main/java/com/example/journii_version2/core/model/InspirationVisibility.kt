package com.example.journii_version2.core.model

/**
 * Whether this Inspiration should appear in public surfaces (Discover, Search)
 * for the given viewer. Drafts never appear regardless of privacy. FOLLOWERS_ONLY
 * is conservatively treated the same as PRIVATE until a real follow relationship
 * exists to check against — once Following is implemented, loosen this for
 * confirmed followers instead of collapsing it into "hidden."
 */
fun Inspiration.isVisibleInDiscovery(viewerId: String): Boolean {
    if (isDraft) return false
    if (creator.id == viewerId) return true
    return privacy == Privacy.PUBLIC
}
