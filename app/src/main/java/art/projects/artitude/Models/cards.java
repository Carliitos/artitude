package art.projects.artitude.Models;

/**
 * Created by manel on 9/5/2017.
 */

public class cards {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String postid;
    private String tags;
    public cards (String userId, String name, String profileImageUrl,String postid,String tags){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.postid = postid;
        this.tags=tags;
    }
    public String getPostId(){return postid;}
    public String getUserId(){
        return userId;
    }
    public String getTags(){
        return tags;
    }
    public void setUserID(String userID){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
}
