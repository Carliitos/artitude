package art.projects.artitude.Models

data class Post(
    var description: String? = "",
    var imageUrl: String? = "",
    var postid: String? = "",
    var tags: String? = "",
    var timesliked: Int? = null,
    var user: String? = ""
)