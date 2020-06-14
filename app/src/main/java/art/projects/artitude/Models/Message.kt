package art.projects.artitude.Models

class Message(val id:String, var text:String, val fromId:String,val toId:String, val imgurl:String,
              timestamp: Long){
    constructor():this("","","","","",-1)
}