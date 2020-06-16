package art.projects.artitude.Models

data class User(
    var uid: String? = "",
    var username: String? = "",
    var bio: String? = "",
    var avatarUrl: String? = "",
    var isAdmin: Boolean? = null
)