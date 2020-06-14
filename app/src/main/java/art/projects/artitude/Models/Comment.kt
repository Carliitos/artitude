package art.projects.artitude.Models

data class Comment(
    var commentid:String?="",
    var postid:String?="",
    var commenttext: String? = "",
    var userId: String? = "",
    var timestamp: Long? = -1,
    var current:String? = ""
)