package art.projects.artitude.Models

class Message(val id:String, val text:String, val fromId:String,val toId:String,
              timestamp: Long){
    constructor():this("","","","",-1)
}