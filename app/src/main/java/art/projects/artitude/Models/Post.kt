package art.projects.artitude.Models

data class Post(
    var description: String? = "",
    var imageUrl: String? = "",
    var postid: String? = "",
    var tags: String? = "",
    var timesliked: Int? = 0,
    var user: String? = ""
)